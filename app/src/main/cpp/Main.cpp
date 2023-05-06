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

NoteEngine *engineHandle;
jobject currentClass;
using namespace std;

extern "C" JNIEXPORT void  JNICALL
Java_com_gabriel4k2_myRandNotes_MainActivity_startFluidSynthEngine(JNIEnv *env, jobject envClass,
                                   jstring sfAbsolutePath) {

    JavaVM *vm;
    const char *sfFilePath = env->GetStringUTFChars(sfAbsolutePath, nullptr);
    currentClass = (env->NewGlobalRef(envClass));
    env->GetJavaVM(&vm);
    engineHandle = new NoteEngine(vm, currentClass);
    engineHandle->load_soundfont_file(sfFilePath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_myRandNotes_MainActivity_pauseSynth(JNIEnv *env, jobject thiz) {
    ((NoteEngine *) engineHandle)->pauseEngine();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_gabriel4k2_myRandNotes_MainActivity_startPlayingNotes(JNIEnv *env, jobject thiz,
                               jlong interval_in_ms,
                               jobject instrument,
                               jintArray midi_note_list) {

    auto engine = ((NoteEngine *) engineHandle);

    engine->pauseEngine();

    engine->start_playing_notes(interval_in_ms,
                                instrument,
                                midi_note_list,
                                env);
}
