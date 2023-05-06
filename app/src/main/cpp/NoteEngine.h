#ifndef FLUIDSYNTHDEMO_NOTE_ENGINE_H
#define FLUIDSYNTHDEMO_NOTE_ENGINE_H

#include <jni.h>
#include "NoteDispatcher.h"

class NoteEngine {
public:
    NoteDispatcher *dispatcher = nullptr;

    NoteEngine(JavaVM *vm, jobject main_activity_reference);
     ~NoteEngine();

    void load_soundfont_file(const char *sfFilePath);
    void pauseEngine();
    void start_playing_notes(unsigned int seq_in_ms, jobject current_instrument,
                             jintArray midi_numbers, JNIEnv *env);
    void create_dispatcher();

private:
    JavaVM *vm;
    fluid_synth_t *synth;
    fluid_audio_driver_t * adriver;
    jobject main_activity_reference;
    int sfId;

};

#endif
