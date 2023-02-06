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


fluid_settings_t *settings;
fluid_synth_t *synth = NULL;
fluid_audio_driver_t *adriver = NULL;
int handlerTeste(void* data, fluid_midi_event_t *event);
extern "C" JNIEXPORT void  JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_startFluidSynthEngine(JNIEnv *env,
                                                                      jobject envClass,
                                                                      jstring sfAbsolutePath) {


    const char *nativeString = env->GetStringUTFChars(sfAbsolutePath, nullptr);


    /* Create the settings object. This example uses the default
     * values for the settings. */
    settings = new_fluid_settings();

    if (settings == NULL) {
        fprintf(stderr, "Failed to create the settings\n");
    }

    /* Create the synthesizer */
    synth = new_fluid_synth(settings);

    if (synth == NULL) {
        fprintf(stderr, "Failed to create the synthesizer\n");
    }

    auto tese = fluid_synth_sfload(synth, nativeString, 1);
    /* Load the soundfont */
    if (tese == FLUID_FAILED) {
        fprintf(stderr, "Failed to load the SoundFont\n");
    }



    /* Play a note */
//    fluid_synth_noteon(synth, 0, 60, 100);
    sleep(1);


}




extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_fluidsynthdemo_MainActivity_playMidiNote(JNIEnv *env, jobject thiz,
                                                             jstring midi_absolute_path, jstring midi_absolute_path2) {

    fluid_player_t* player;

    player = new_fluid_player(synth);

    const char *nativeString = env->GetStringUTFChars(midi_absolute_path, nullptr);

    /* start the synthesizer thread */
    /* play the midi files, if any */
    auto status = fluid_player_status();
    fluid_player_add(player, nativeString);


    nativeString = env->GetStringUTFChars(midi_absolute_path2, nullptr);


    /* start the synthesizer thread */
    adriver = new_fluid_audio_driver(settings, synth);
    /* play the midi files, if any */
    auto ticks = fluid_player_get_total_ticks(player);



    fluid_player_add(player, nativeString);
    fluid_player_play(player);


    fluid_player_join(player);

}

int handlerTeste(void* data, fluid_midi_event_t *event){
    auto eventType = fluid_midi_event_get_type(event);
    return 1;
}