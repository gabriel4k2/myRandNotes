//
// Created by gabriel on 25/02/2023.
//

#ifndef FLUIDSYNTHDEMO_INSTRUMENT_H
#define FLUIDSYNTHDEMO_INSTRUMENT_H

#include <jni.h>

typedef struct {
    int patchNumber;
    const char *instrumentName;
    int bankOffset;
} instrument;

//void deserialize_instrument(JNIEnv *env , jobject instrument){
//    auto kotlinInsrumentClass = env->FindClass("com/gabriel4k2/fluidsynthdemo/data/Instrument");
//    jfieldID  patchNumberID = env->GetFieldID(kotlinInsrumentClass, "patchNumber", "I");
//    auto patchNumber = env->GetIntField(instrument, patchNumberID);
//
//}

instrument deserialize_instrument(JNIEnv *env , jobject instrument);
#endif //FLUIDSYNTHDEMO_INSTRUMENT_H
