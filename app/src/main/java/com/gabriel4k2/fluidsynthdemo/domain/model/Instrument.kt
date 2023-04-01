package com.gabriel4k2.fluidsynthdemo.domain.model

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.squareup.moshi.Json

data class Instrument(
    val type: String,
    @Json(name = "patch_number")
    val patchNumber: Int,
    @Json(name = "instrument_name")
    val name: String,
    @Json(name = "bank_offset")
    val bankOffset: Int,
    // This variable is set when we read the instrument list and thus we can analyse whether
    // the instrument has an unique type
    var isUniqueType: Boolean = false
) {
    /*   If there is only one instrument with the current type (say a Viola) than simply return its name
    else, return the name prefixed with the type, thus assuming we have more than one piano, the
    output is:
     Viola -> Viola
     Eletric Grande -> Piano - Electric Grand
    */
    fun uniquenessAwareInstrumentName(): String {
        return if (this.isUniqueType) {
            name
        } else {
            type.capitalize(Locale.current) + " - " + name
        }
    }


    override fun toString(): String {
        return this.uniquenessAwareInstrumentName()
    }

    companion object {
        val INITIAL_INSTRUMENT = Instrument(
            type = "guitar",
            patchNumber = 24,
            name = "Nylon Guitar",
            bankOffset = 0
        )

        val UNKNOWN = Instrument(
            type = "-",
            patchNumber = -1,
            name = "-",
            bankOffset = 0,
            isUniqueType = false
        )
    }
}