

#ifndef FLUIDSYNTHDEMO_MYNOTESENGINE_H
#define FLUIDSYNTHDEMO_MYNOTESENGINE_H

#include <jni.h>
#include "NoteDispatcher.h"




class MyNotesEngine {
public:
     MyNotesEngine(JavaVM *vm, jobject mainActivityReference);
     ~MyNotesEngine();

    void loadsoundfont(const char *sfFilePath);
    void start_playing_notes(unsigned int seqInMs, jobject currentInstrument, JNIEnv * env);


    NoteDispatcher *dispatcher = nullptr;

    void create_dispatcher(jobject mainActivityReference);

private:
    JavaVM *vm;
    fluid_sequencer_t *sequencer;
    fluid_synth_t *synth;
    fluid_audio_driver_t * adriver;
    short synthSeqID;
    int sfId;

};

#endif
