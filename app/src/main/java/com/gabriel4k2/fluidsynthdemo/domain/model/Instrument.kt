package com.gabriel4k2.fluidsynthdemo.domain.model

import com.gabriel4k2.fluidsynthdemo.domain.typeAwareInstrumentName
import com.squareup.moshi.Json

data class Instrument(
    val type:String,
    @Json(name = "patch_number")
    val patchNumber: Int,
    @Json(name = "instrument_name")
    val name: String,
    @Json(name = "bank_offset")
    val bankOffset: Int
) {

    companion object {
        val EMPTY_INSTRUMENT = Instrument(patchNumber = -1, name = "-", bankOffset = -1, type = "-")
        val INITIAL_INSTRUMENT: Instrument
            get()  = run {
                val instrument = Instrument(type = "guitar", patchNumber = 24, name = "Nylon Guitar", bankOffset = 0)
                val mockedProcessedName = instrument.typeAwareInstrumentName(isUniqueType = false)
                return instrument.copy(name = mockedProcessedName)
            }

    }
}