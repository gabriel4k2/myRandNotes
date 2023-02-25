package com.gabriel4k2.fluidsynthdemo.data

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
        fun mock(): Instrument {
            return Instrument(patchNumber = 0, name = "Stereo Grand", bankOffset = 0, type = "piano")
        }
    }
}