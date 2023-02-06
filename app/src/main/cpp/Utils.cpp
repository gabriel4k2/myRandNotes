#include <jni.h>
#include "Utils.h"
#include "Instrument.h"

instrument deserialize_instrument(JNIEnv *env, jobject _instrument) {
    auto kotlin_instrument_class = env->FindClass(
            "com/gabriel4k2/myRandNotes/domain/model/Instrument");
    jfieldID patch_number_field_id = env->GetFieldID(kotlin_instrument_class, "patchNumber", "I");
    auto patch_number = env->GetIntField(_instrument, patch_number_field_id);


    jfieldID name_field_id = env->GetFieldID(kotlin_instrument_class, "name", "Ljava/lang/String;");
    auto name_jstring = static_cast<jstring>(env->GetObjectField(_instrument, name_field_id));
    auto name_string = env->GetStringUTFChars(name_jstring, nullptr);

    jfieldID bank_offset_field_id = env->GetFieldID(kotlin_instrument_class, "bankOffset", "I");
    auto bank_offset = env->GetIntField(_instrument, bank_offset_field_id);
    auto _instrument_object = instrument{patch_number, name_string, bank_offset};

    env->ReleaseStringUTFChars(name_jstring, name_string);
    return _instrument_object;
}

vector<int> deserialize_integer_list(JNIEnv *env, jintArray integer_list) {
    auto notes_count = env->GetArrayLength(integer_list);
    jint *base_pointer = env->GetIntArrayElements(integer_list, 0);
    vector<int> midi_numbers;

    for (int i = 0; i < notes_count; i++) {
        midi_numbers.push_back(reinterpret_cast<int> (base_pointer[i]));

    }
    return midi_numbers;
}
