#ifndef FLUIDSYNTHDEMO_NOTEDISPATCHER_H
#define FLUIDSYNTHDEMO_NOTEDISPATCHER_H

#include <jni.h>
#include <fluidsynth.h>
#include <cinttypes>
#include <vector>
#include "Instrument.h"

using namespace std;

typedef struct {
    unsigned int seq_duration_in_ms;
    instrument instrument;
    vector<int>  midi_notes_numbers;
} dispatching_configs;

class NoteDispatcher {
public:
    NoteDispatcher(JavaVM *vm, jobject main_activity_reference, fluid_synth_t *pSynth);

    ~NoteDispatcher();

    void start_note_dispatching(JNIEnv *env, fluid_synth_t *synth, int sfId, const dispatching_configs& config);
    void stop_note_dispatching();

private:
    bool isAttached = false;
    short clientId{};
    short synthClientId{};
    unsigned int now{};
    unsigned int seqDurationMs{};
    JNIEnv *audio_thread_env;
    jobject main_activity_reference;
    JavaVM *vm;
    fluid_sequencer_t *sequencer = nullptr;
    vector<int>  midiNotes;
    _fluid_synth_t * synth;

    void send_noteon(JNIEnv *env , int chan, unsigned int date, int key);
    void send_noteoff(int chan, unsigned int date, int key);
    void schedule_next_sequence(JNIEnv *env);
    void schedule_next_callback();
    void dispatch_new_midi_note(int midi_note_number, JNIEnv *env);

};

#endif //FLUIDSYNTHDEMO_NOTEDISPATCHER_H
