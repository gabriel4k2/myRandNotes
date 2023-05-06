package com.gabriel4k2.myRandNotes.domain

import com.gabriel4k2.myRandNotes.domain.model.Instrument
import javax.inject.Inject

class InstrumentUseCase @Inject constructor(val repository: IInstrumentRepository) {

    fun getOrderedAndProcessedInstrumentList(): List<Instrument> {
        val instrumentList = repository.retrieveInstrumentList()
        val instrumentListGroupedByType = instrumentList.groupBy { it.type }

        val processedInstrumentList =
            instrumentListGroupedByType.entries.fold(mutableListOf<Instrument>()) { acc, group ->
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

        return processedInstrumentList.sortedBy { it.toString() }
    }
}
