package com.gabriel4k2.myRandNotes.domain

import com.gabriel4k2.myRandNotes.domain.model.Instrument

interface IInstrumentRepository {
    fun retrieveInstrumentList(): List<Instrument>
}
