//
// Created by gabriel on 26/02/2023.
//
#include "Instrument.h"

instrument  deserialize_instrument(JNIEnv *env , jobject _instrument){
    auto kotlinInsrumentClass = env->FindClass("com/gabriel4k2/fluidsynthdemo/data/Instrument");
    jfieldID  patchNumberFieldID = env->GetFieldID(kotlinInsrumentClass, "patchNumber", "I");
    auto patchNumber = env->GetIntField(_instrument, patchNumberFieldID);

//    jfieldID  typeFieldId = env->GetFieldID(kotlinInsrumentClass, "type", "Ljava/lang/String;");
//    auto typeJstring =  static_cast<jstring>( env->GetObjectField(instrument, typeFieldId));
//    auto typeString = env->GetStringUTFChars(typeJstring, 0);

    jfieldID  nameFieldID = env->GetFieldID(kotlinInsrumentClass, "name", "Ljava/lang/String;");
    auto nameJstring =  static_cast<jstring>( env->GetObjectField(_instrument, nameFieldID));
    auto nameString = env->GetStringUTFChars(nameJstring, 0);

    jfieldID  bankOffsetFieldID = env->GetFieldID(kotlinInsrumentClass, "bankOffset", "I");
    auto bankOffset = env->GetIntField(_instrument, bankOffsetFieldID);
    auto teste =  instrument{patchNumber, nameString, bankOffset};
    return teste;
//    jfieldID  patchNumberFieldID = env->GetFieldID(kotlinInsrumentClass, "patchNumber", "I");
//    auto patchNumber = env->GetIntField(instrument, patchNumberFieldID);
//
//    jfieldID  patchNumberFieldID = env->GetFieldID(kotlinInsrumentClass, "patchNumber", "I");
//    auto patchNumber = env->GetIntField(instrument, patchNumberFieldID);

}