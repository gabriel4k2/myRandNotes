//
// Created by gabriel on 21/02/2023.
//

#ifndef FLUIDSYNTHDEMO_NOTEDISPATCHER_H
#define FLUIDSYNTHDEMO_NOTEDISPATCHER_H

#include <jni.h>
#include <fluidsynth.h>
#include <cinttypes>
#include "Instrument.h"




class NoteDispatcher {
public:
     NoteDispatcher(JavaVM *vm, fluid_sequencer_t *sequencer, jobject mainActivityReference);
     ~NoteDispatcher();

    void startNoteDispatching(JNIEnv * env, unsigned int seqDurationMs, instrument *  currentInstrument, short synthSeqId);



private:
    JNIEnv *audioThreadEnv;
    fluid_sequencer_t *sequencer;
    jobject mainActivityReference;
    instrument *currentInstrument;
    bool isAttached = false;
    JavaVM *vm;
    short clientId;
    short synthSeqId;
    unsigned int now;
    unsigned int seqDurationMs;


    void sendnoteon(int chan, unsigned int date, JNIEnv * env);

    void schedule_next_sequence(JNIEnv * env);

    void schedule_next_callback();

    void dispatchNewMidiNote(int midiNoteNumber, JNIEnv * env);

};
#endif //FLUIDSYNTHDEMO_NOTEDISPATCHER_H
