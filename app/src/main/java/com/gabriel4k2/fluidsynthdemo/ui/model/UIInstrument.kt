package com.gabriel4k2.fluidsynthdemo.ui.model

import android.provider.Contacts.Intents.UI
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument

data class UIInstrument(
    val name: String,
    val instrument: Instrument
){

    companion object {
        val EMPTY_INSTRUMENT = UIInstrument("-", Instrument.EMPTY_INSTRUMENT)
    }
    override fun toString(): String {
        return name
    }
}