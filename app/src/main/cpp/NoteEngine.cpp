#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <android/log.h>
#include <fluidsynth.h>
#include <unistd.h>
#include <vector>
#include <string>
#include "NoteEngine.h"
#include "Utils.h"

NoteEngine::NoteEngine(JavaVM *vm, jobject main_activity_reference) {
    fluid_settings_t *settings;
    settings = new_fluid_settings();
    fluid_settings_setint(settings, "synth.reverb.active", 0);
    fluid_settings_setint(settings, "synth.chorus.active", 0);
    synth = new_fluid_synth(settings);
    adriver = new_fluid_audio_driver(settings, synth);
    this->vm = vm;
    this->main_activity_reference = main_activity_reference;
}

void NoteEngine::create_dispatcher() {
    dispatcher = new NoteDispatcher(vm, main_activity_reference, synth);
}

void NoteEngine::load_soundfont_file(const char *sfFilePath) {
    this->sfId = fluid_synth_sfload(synth, sfFilePath, 1);
}

void
NoteEngine::start_playing_notes(unsigned int seq_in_ms, jobject current_instrument,
                                jintArray midi_numbers, JNIEnv *env) {

    this->create_dispatcher();

    auto instrument = deserialize_instrument(env, current_instrument);
    auto _midi_numbers = deserialize_integer_list(env, midi_numbers);

    (this->dispatcher)->start_note_dispatching(env, synth, sfId,
                                               dispatching_configs{seq_in_ms, instrument,
                                                                   _midi_numbers});
}

NoteEngine::~NoteEngine() {
    delete_fluid_audio_driver(adriver);
    delete_fluid_synth(synth);
}

void NoteEngine::pauseEngine() {
    if (this->dispatcher != nullptr) {
        delete this->dispatcher;
        this->dispatcher = nullptr;
    }
}
