//
// Created by gabriel on 21/02/2023.
//
#include <cstdlib>
#include <functional>
#include "NoteDispatcher.h"

void NoteDispatcher::startNoteDispatching(JNIEnv * env, unsigned int seqDurationMs, instrument *  currentInstrument, short synthSeqId ) {
    // remove all queued events that were  possibly using another time interval/
    // instrument.
//    fluid_sequencer_remove_events(sequencer, -1, clientId, -1);

    now = fluid_sequencer_get_tick(sequencer);
    this->seqDurationMs = seqDurationMs;
    this->synthSeqId = synthSeqId;
    schedule_next_sequence(env);


}

void NoteDispatcher::sendnoteon(int chan, unsigned int date, JNIEnv * env) {
    int range = 83 - 0 + 1;
    int num = rand() % range + 0;

    fluid_event_t *evt = new_fluid_event();
    fluid_event_set_source(evt, -1);
    fluid_event_set_dest(evt, synthSeqId);
    fluid_event_noteon(evt, chan, num, 60);
    fluid_sequencer_send_at(sequencer, evt, date, 1);
    delete_fluid_event(evt);

    dispatchNewMidiNote(num, env);
}


void NoteDispatcher::schedule_next_sequence(JNIEnv * env) {

    now = now + seqDurationMs;
    sendnoteon(0,   now , env);

    schedule_next_callback();
}


NoteDispatcher::NoteDispatcher(JavaVM *vm, fluid_sequencer_t *sequencer,  jobject mainActivityReference) {


    void (*cb)(unsigned int, fluid_event_t *, fluid_sequencer_t *, void *) = [](
            unsigned int time, fluid_event_t *event,
            fluid_sequencer_t *seq,
            void *data) {

        auto *classRef = static_cast<NoteDispatcher *>(data);
        if(!classRef->isAttached){
            (*classRef->vm).AttachCurrentThread(&classRef->audioThreadEnv, nullptr);
            classRef->isAttached = true;
        }


        classRef->schedule_next_sequence(classRef->audioThreadEnv);
    };



    clientId = fluid_sequencer_register_client(sequencer, "me",
                                               cb, this);
    this->vm = vm;
    this->sequencer = sequencer;
    this->mainActivityReference = mainActivityReference;

}

void NoteDispatcher::schedule_next_callback() {
    // I want to be called back before the end of the next sequence
   unsigned int callbackdate = now + seqDurationMs / 2;
    fluid_event_t *evt = new_fluid_event();
    fluid_event_set_source(evt, -1);
    fluid_event_set_dest(evt, clientId);
    fluid_event_timer(evt, NULL);
    fluid_sequencer_send_at(sequencer, evt, callbackdate, 1);
    delete_fluid_event(evt);
}

void NoteDispatcher::dispatchNewMidiNote(int midiNoteNumber, JNIEnv * env) {
    auto myclass = env->GetObjectClass(this->mainActivityReference);
    auto methodId = env->GetMethodID(myclass, "onMidiNoteChanged",
                                     "(I)V");
    env->CallVoidMethod(this->mainActivityReference, methodId, midiNoteNumber);
}

NoteDispatcher::~NoteDispatcher() {
    fluid_sequencer_unregister_client(sequencer, clientId );
    (*vm).DetachCurrentThread();
}
