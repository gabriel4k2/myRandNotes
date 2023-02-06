#ifndef FLUIDSYNTHDEMO_UTILS_H
#define FLUIDSYNTHDEMO_UTILS_H

#include <vector>
#include "Instrument.h"
using namespace std;

instrument deserialize_instrument(JNIEnv *env , jobject instrument);
vector<int> deserialize_integer_list(JNIEnv *env , jintArray integer_list);

#endif //FLUIDSYNTHDEMO_UTILS_H
