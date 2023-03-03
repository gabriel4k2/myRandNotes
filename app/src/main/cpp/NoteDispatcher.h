//
// Created by gabriel on 21/02/2023.
//

#ifndef FLUIDSYNTHDEMO_NOTEDISPATCHER_H
#define FLUIDSYNTHDEMO_NOTEDISPATCHER_H

#include <jni.h>
#include <fluidsynth.h>
#include <cinttypes>
#include <vector>
#include "Instrument.h"

using namespace std;
typedef struct {
    unsigned int seqDurationMs;
    instrument instrument;
    vector<int>  midiNotes;
} dispatching_configs;

class NoteDispatcher {
public:
    NoteDispatcher(JavaVM *vm, fluid_sequencer_t *sequencer, jobject mainActivityReference);

    ~NoteDispatcher();

    void startNoteDispatching(JNIEnv *env, short synthSeqId, fluid_synth_t *synth, int sfId, dispatching_configs config);


private:
    JNIEnv *audioThreadEnv;
    jobject mainActivityReference;
    JavaVM *vm;

    instrument *currentInstrument;
    fluid_sequencer_t *sequencer;
    vector<int>  midiNotes;

    bool isAttached = false;
    short clientId;
    short synthSeqId;
    unsigned int now;
    unsigned int seqDurationMs;


    void sendnoteon(int chan, unsigned int date, int key, JNIEnv *env);

    void sendnoteoff(int chan, unsigned int date, int key);

    void schedule_next_sequence(JNIEnv *env);

    void schedule_next_callback();

    void dispatchNewMidiNote(int midiNoteNumber, JNIEnv *env);

};

#endif //FLUIDSYNTHDEMO_NOTEDISPATCHER_H
