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

    void startNoteDispatching(unsigned int seqDurationMs, instrument *  currentInstrument);



private:
    JNIEnv *audioThreadEnv;
    fluid_sequencer_t *sequencer;
    jobject mainActivityReference;
    instrument *currentInstrument;
    JavaVM *vm;
    short sequencerId;
    unsigned int now;
    unsigned int seqDurationMs;


    void sendnoteon(int chan, unsigned int date);

    void schedule_next_sequence();

    void schedule_next_callback();

    void dispatchNewMidiNote(int midiNoteNumber);

};
#endif //FLUIDSYNTHDEMO_NOTEDISPATCHER_H
