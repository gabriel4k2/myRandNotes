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
#include "MyNotesEngine.h"
#include "Instrument.h"


MyNotesEngine * engineHandle;
jobject currentClass;

//std::vector<instrument> MyNotesEngine::get_instruments_list() {
//    fluid_sfont_t *sfont;
//    fluid_preset_t *preset;
//    int offset;
//    std::vector<instrument> instruments;
//
//    sfont = fluid_synth_get_sfont_by_id(synth, sfId);
//    offset = fluid_synth_get_bank_offset(synth, sfId);
//
//    fluid_sfont_iteration_start(sfont);
//    while ((preset = fluid_sfont_iteration_next(sfont)) != NULL) {
//
//        auto bankOffset = fluid_preset_get_banknum(preset) + offset;
//        auto patchNumber = fluid_preset_get_num(preset);
//        auto instrumentName = fluid_preset_get_name(preset);
//        instruments.push_back(
//                instrument{patchNumber, instrumentName, bankOffset});
//
//    }
//    return instruments;
//
//
//}


extern "C" JNIEXPORT void  JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_startFluidSynthEngine(JNIEnv *env,
                                                                      jobject envClass,
                                                                      jstring sfAbsolutePath) {


    const char *sfFilePath = env->GetStringUTFChars(sfAbsolutePath, nullptr);

    currentClass = (env->NewGlobalRef(envClass));
    JavaVM *vm;
    env->GetJavaVM(&vm);
    engineHandle= new MyNotesEngine(vm, currentClass);

    engineHandle->loadsoundfont(sfFilePath);

//    engineHandle = reinterpret_cast<MyNotesEngine *>(env->NewGlobalRef(
//            reinterpret_cast<jobject>(engineHandle)));


}





extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_pauseSynth(JNIEnv *env, jobject thiz) {
    // TODO see if dispatcher is really destroyed
    delete ((MyNotesEngine *) engineHandle)->dispatcher;

}


MyNotesEngine::MyNotesEngine(JavaVM *vm, jobject mainActivityReference) {
    fluid_settings_t *settings;
    settings = new_fluid_settings();
    fluid_settings_setint(settings, "synth.reverb.active", 0);
    fluid_settings_setint(settings, "synth.chorus.active", 0);
    synth = new_fluid_synth(settings);
    adriver = new_fluid_audio_driver(settings, synth);
    sequencer = new_fluid_sequencer2(0);
    this->vm = vm;


    synthSeqID = fluid_sequencer_register_fluidsynth(sequencer, synth);
}

void MyNotesEngine::create_dispatcher(
        jobject mainActivityReference) {
    dispatcher = new NoteDispatcher(vm, sequencer, mainActivityReference);
}

void MyNotesEngine::loadsoundfont(const char *sfFilePath) {
     this->sfId = fluid_synth_sfload(synth, sfFilePath, 1);

}

void MyNotesEngine::start_playing_notes(unsigned int seqInMs, jobject currentInstrument, JNIEnv * env) {

    auto instrument = deserialize_instrument(env, reinterpret_cast<jobject>(currentInstrument));

    (this->dispatcher)->startNoteDispatching(env, seqInMs, instrument,  synthSeqID, synth, sfId);
}

MyNotesEngine::~MyNotesEngine() {
    delete_fluid_sequencer(sequencer);
    delete_fluid_audio_driver(adriver);
    delete_fluid_synth(synth);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_startPlayingNotes(JNIEnv *env, jobject thiz,
                                                                  jlong interval_in_ms,
                                                                  jobject instrument) {

    auto engine = ((MyNotesEngine *) engineHandle);
    if (engine->dispatcher == nullptr) {
//        jobject currentClass = (env->NewGlobalRef(thiz));
        engine->create_dispatcher(currentClass);
    }
    engine->start_playing_notes(interval_in_ms,
                                instrument, env);
}