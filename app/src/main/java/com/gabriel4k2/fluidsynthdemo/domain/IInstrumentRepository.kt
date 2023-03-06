package com.gabriel4k2.fluidsynthdemo.domain

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument

interface IInstrumentRepository{
    fun retriveInstrumentList() : List<Instrument>
}