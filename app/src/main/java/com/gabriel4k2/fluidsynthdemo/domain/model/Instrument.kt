package com.gabriel4k2.fluidsynthdemo.domain.model

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

    }
}