#ifndef FLUIDSYNTHDEMO_INSTRUMENT_H
#define FLUIDSYNTHDEMO_INSTRUMENT_H

#include <jni.h>

typedef struct {
    int patch_number;
    const char *instrument_name;
    int bank_offset;
} instrument;

#endif //FLUIDSYNTHDEMO_INSTRUMENT_H
