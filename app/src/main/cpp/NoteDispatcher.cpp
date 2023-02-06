
#include <cstdlib>
#include <functional>
#include <android/log.h>
#include "NoteDispatcher.h"
#include <string>
#include <syslog.h>
#include<unistd.h>

void NoteDispatcher::start_note_dispatching(JNIEnv *env, fluid_synth_t *synth, int sfId,
                                            const dispatching_configs &config) {

    auto instrument = config.instrument;

    if(this->sequencer == nullptr){
        this->sequencer = new_fluid_sequencer2(0);
    }

    this->synthClientId = fluid_sequencer_register_fluidsynth(sequencer, synth);

    fluid_synth_program_select(synth, 0, sfId, instrument.bank_offset,
                               instrument.patch_number);

    void (*cb)(unsigned int, fluid_event_t *, fluid_sequencer_t *, void *) = [](
            unsigned int time, fluid_event_t *event,
            fluid_sequencer_t *seq,
            void *data) {

        auto *classRef = static_cast<NoteDispatcher *>(data);
        (*classRef->vm).AttachCurrentThread(&classRef->audio_thread_env, nullptr);
        classRef->schedule_next_sequence(classRef->audio_thread_env);
    };


    clientId = fluid_sequencer_register_client(sequencer, "me",
                                               cb, this);

    now = fluid_sequencer_get_tick(sequencer);
    this->seqDurationMs = config.seq_duration_in_ms;
    this->midiNotes = config.midi_notes_numbers;
    schedule_next_sequence(env);
}

void NoteDispatcher::send_noteon(JNIEnv *env, int chan, unsigned int date, int key) {
    fluid_event_t *evt = new_fluid_event();
    fluid_event_set_source(evt, -1);
    fluid_event_set_dest(evt, synthClientId);
    fluid_event_noteon(evt, chan, key, 100);
    fluid_sequencer_send_at(sequencer, evt, date, 1);
    delete_fluid_event(evt);

    dispatch_new_midi_note(key, env);
}

void NoteDispatcher::send_noteoff(int chan, unsigned int date, int key) {
    fluid_event_t *ev = new_fluid_event();
    fluid_event_set_source(ev, -1);
    fluid_event_set_dest(ev, synthClientId);
    fluid_event_noteoff(ev, chan, key);
    fluid_sequencer_send_at(sequencer, ev, date, 1);
    delete_fluid_event(ev);
}


void NoteDispatcher::schedule_next_sequence(JNIEnv *env) {
    now = now + seqDurationMs;
    int range = this->midiNotes.size() - 1;
    int randomNoteNumberIndex = rand() % range;
    auto randomNoteNumber = this->midiNotes[randomNoteNumberIndex];
    send_noteon(env, 0, now, randomNoteNumber);
    send_noteoff(0, now + seqDurationMs, randomNoteNumber);
    schedule_next_callback();
}


NoteDispatcher::NoteDispatcher(JavaVM *vm, jobject main_activity_reference, fluid_synth_t *pSynth) {
    this->vm = vm;
    this->main_activity_reference = main_activity_reference;
    this->synth = pSynth;
}

void NoteDispatcher::schedule_next_callback() {
    unsigned int callback_date = now + seqDurationMs;
    fluid_event_t *evt = new_fluid_event();
    fluid_event_set_source(evt, -1);
    fluid_event_set_dest(evt, clientId);
    fluid_event_timer(evt, NULL);
    fluid_sequencer_send_at(sequencer, evt, callback_date, 1);
    delete_fluid_event(evt);
}

void NoteDispatcher::dispatch_new_midi_note(int midi_note_number, JNIEnv *env) {
    auto main_activity_ref = env->GetObjectClass(this->main_activity_reference);
    auto method_id = env->GetMethodID(main_activity_ref, "onMidiNoteChanged",
                                      "(I)V");
    env->CallVoidMethod(this->main_activity_reference, method_id, midi_note_number);
}

NoteDispatcher::~NoteDispatcher() {
    fluid_sequencer_remove_events(sequencer, -1, -1, -1);
    fluid_sequencer_unregister_client(sequencer, clientId);
    fluid_synth_all_sounds_off(synth, 0);
    fluid_sequencer_unregister_client(sequencer, this->synthClientId);
}

