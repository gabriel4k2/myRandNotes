package com.gabriel4k2.fluidsynthdemo.domain

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
import javax.inject.Inject

class InstrumentUseCase @Inject constructor(val repository: IInstrumentRepository) {

    /* Returns a pair containing the first instrument to be shown (in case the user has not
    * selected one) and the list itself. */
    fun getOrderedAndProcessedInstrumentList(): Pair<UIInstrument, List<UIInstrument>> {
        val instrumentList = repository.retriveInstrumentList()
        val groupedByType = instrumentList.groupBy { it.type }

        val processedInstrumentList = groupedByType.entries.fold(mutableListOf<UIInstrument>()) { acc, group ->
            val instrumentsWithSameType = group.value
            if (instrumentsWithSameType.size == 1) {
                val instrument = instrumentsWithSameType.first()
                acc += UIInstrument(
                    name = instrument.typeAwareInstrumentName(isUniqueType = true),
                    instrument = instrument
                )
                acc
            } else {
                instrumentsWithSameType.fold(acc) { innerAcc, instrument ->
                    innerAcc += UIInstrument(
                        name = instrument.typeAwareInstrumentName(isUniqueType = false),
                        instrument = instrument
                    )
                    innerAcc
                }
            }

        }

        val  sortedInstrumentList = processedInstrumentList.sortedBy { it.name }
        val firstInstrument = sortedInstrumentList.firstOrNull{ it.name.contains("Nylon")} ?: sortedInstrumentList.first()

        return  Pair(firstInstrument, sortedInstrumentList)

    }
}

/*
    If there is only one instrument with the current type (say a Viola) than simply return its name
    else, return the name prefixed with the type, thus assuming we have more than one piano, the
    output is:
     Viola -> Viola
     Eletric Grande -> Piano - Electric Grand
 */
fun Instrument.typeAwareInstrumentName(isUniqueType: Boolean): String {
    return if (isUniqueType) {
        name
    } else {
        type.capitalize(Locale.current) + " - " + name
    }
}