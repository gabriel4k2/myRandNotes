/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <android/log.h>
#include <fluidsynth.h>
#include <unistd.h>
#include <vector>
#include <string>



typedef struct {
    int patchNumber;
    const char *instrumentName;
    int bankOffset;
} instrument;

typedef struct {
    _jclass *cls;
    _jmethodID *callbackId;
} cb;


fluid_synth_t *synth;
fluid_audio_driver_t *adriver;
fluid_sequencer_t *sequencer;
JNIEnv *currentEnv;

jobject currentClass;
JavaVM * vm= new JavaVM();

short synthSeqID, mySeqID;
unsigned int now;
unsigned int seqduration;
int sfId;
_jmethodID * onMidiNoteChangedCallbackId = nullptr;


void seq_callback(unsigned int time, fluid_event_t *event, fluid_sequencer_t *seq, void *data);

void schedule_next_sequence_from_cb();

void create_synth() {
    fluid_settings_t *settings;
    settings = new_fluid_settings();
    fluid_settings_setint(settings, "synth.reverb.active", 0);
    fluid_settings_setint(settings, "synth.chorus.active", 0);
    synth = new_fluid_synth(settings);
    adriver = new_fluid_audio_driver(settings, synth);
    sequencer = new_fluid_sequencer2(0);

    // register synth as first destination
    synthSeqID = fluid_sequencer_register_fluidsynth(sequencer, synth);

    // register myself as second destination
    mySeqID = fluid_sequencer_register_client(sequencer, "me", seq_callback, NULL);

    // the sequence duration, in ms
    seqduration = 1000;
}

void delete_synth() {
    delete_fluid_sequencer(sequencer);
    delete_fluid_audio_driver(adriver);
    delete_fluid_synth(synth);
}

std::vector<instrument>
get_instruments_list() {
    fluid_sfont_t *sfont;
    fluid_preset_t *preset;
    int offset;
    std::vector<instrument> instruments;

    sfont = fluid_synth_get_sfont_by_id(synth, sfId);
    offset = fluid_synth_get_bank_offset(synth, sfId);

    fluid_sfont_iteration_start(sfont);
    while ((preset = fluid_sfont_iteration_next(sfont)) != NULL) {

        auto bankOffset = fluid_preset_get_banknum(preset) + offset;
        auto patchNumber = fluid_preset_get_num(preset);
        auto instrumentName = fluid_preset_get_name(preset);
        instruments.push_back(
                instrument{patchNumber, instrumentName, bankOffset});

    }
    return instruments;


}


void loadsoundfont(const char *sfFile) {
    sfId = fluid_synth_sfload(synth, sfFile, 1);
    get_instruments_list();

}

void sendnoteon(int chan, short key, unsigned int date, JNIEnv *pEnv) {
    int range = 83 - 0 + 1;
    int num = rand() % range + 0;
    fluid_event_t *evt = new_fluid_event();
    fluid_event_set_source(evt, -1);
    fluid_event_set_dest(evt, synthSeqID);
    fluid_event_noteon(evt, chan, num, 127);
    fluid_sequencer_send_at(sequencer, evt, date, 1);
    delete_fluid_event(evt);
    if(onMidiNoteChangedCallbackId != nullptr){
        auto myclass = pEnv->GetObjectClass(currentClass);
        auto methodId =  pEnv->GetMethodID(myclass,"onMidiNoteChanged",
                                          "(I)V");
        pEnv->CallVoidMethod(currentClass, onMidiNoteChangedCallbackId, num);

    }
}

void schedule_next_callback(JNIEnv *pEnv) {
    // I want to be called back before the end of the next sequence
    unsigned int callbackdate = now + seqduration / 2;
    fluid_event_t *evt = new_fluid_event();
    fluid_event_set_source(evt, -1);
    fluid_event_set_dest(evt, mySeqID);
    fluid_event_timer(evt, NULL);
    fluid_sequencer_send_at(sequencer, evt, callbackdate, 1);
    delete_fluid_event(evt);
}

void schedule_next_sequence(JNIEnv * env) {
    // Called more or less before each sequence start
    // the next sequence start date
    now = now + seqduration;
    sendnoteon(1, 60, now + seqduration, env);

    // so that we are called back early enough to schedule the next sequence
    schedule_next_callback(env);
}

/* sequencer callback */
void seq_callback(unsigned int time, fluid_event_t *event, fluid_sequencer_t *seq, void *data) {
    schedule_next_sequence_from_cb();
}

void schedule_next_sequence_from_cb() {
    JNIEnv *audioThreadEnv;
    (*vm).AttachCurrentThread(&audioThreadEnv, nullptr);



    schedule_next_sequence(audioThreadEnv);
}

void on_resume(JNIEnv *pEnv) {
    // initialize our absolute date
    now = fluid_sequencer_get_tick(sequencer);
    schedule_next_sequence(pEnv);

    sleep(2);
}


extern "C" JNIEXPORT void  JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_startFluidSynthEngine(JNIEnv *env,
                                                                      jobject envClass,
                                                                      jstring sfAbsolutePath) {


    const char *sfFilePath = env->GetStringUTFChars(sfAbsolutePath, nullptr);

   currentClass = (env->NewGlobalRef(envClass)) ;
   env->GetJavaVM(&vm);
    create_synth();
    loadsoundfont(sfFilePath);
    on_resume(env);


}



extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_pauseSynth(JNIEnv *env, jobject thiz) {
    delete_synth();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_registerNoteChangeCallback(JNIEnv *env,
                                                                           jobject thiz) {


    jclass cls = env->GetObjectClass(thiz);
    onMidiNoteChangedCallbackId =
            env->GetMethodID(cls,"onMidiNoteChanged",
                             "(I)V");

}