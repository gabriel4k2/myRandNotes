package com.gabriel4k2.fluidsynthdemo.domain

import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import javax.inject.Inject

class InstrumentUseCase @Inject constructor(val repository: IInstrumentRepository) {

    /* Returns a pair containing the first instrument to be shown (in case the user has not
    * selected one) and the list itself. */
    fun getOrderedAndProcessedInstrumentList(): List<Instrument> {
        val instrumentList = repository.retrieveInstrumentList()
        val groupedByType = instrumentList.groupBy { it.type }

        val processedInstrumentList = groupedByType.entries.fold(mutableListOf<Instrument>()) { acc, group ->
            val instrumentsWithSameType = group.value
            if (instrumentsWithSameType.size == 1) {
                val instrument = instrumentsWithSameType.first()
                instrument.isUniqueType = true
                acc += instrument
                acc
            } else {
                instrumentsWithSameType.fold(acc) { innerAcc, instrument ->
                    instrument.isUniqueType = false
                    innerAcc += instrument
                    innerAcc
                }
            }

        }



        val  sortedInstrumentList = processedInstrumentList.sortedBy { it.toString() }
//        val firstInstrument = sortedInstrumentList.firstOrNull{ it.name.contains("Nylon")} ?: sortedInstrumentList.first()

        return   sortedInstrumentList

    }
}

