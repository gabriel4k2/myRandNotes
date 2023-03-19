package com.gabriel4k2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.InstrumentUseCase
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.providers.NoteGeneratorSettingsDispatcher
import com.gabriel4k2.fluidsynthdemo.ui.settings.SettingsChangeEvent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MainActivityUIState(
    val instruments: List<Instrument> = emptyList(),
    val currentInstrument: Instrument = Instrument.INITIAL_INSTRUMENT

)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val instrumentUseCase: InstrumentUseCase,
    val settingsStorage: SettingsStorage

    ) : ViewModel() {

    private var _uiState: MutableStateFlow<MainActivityUIState> =
        MutableStateFlow(MainActivityUIState())
    val uiSate: StateFlow<MainActivityUIState> = _uiState

    @Composable
    fun RetrieveInstrumentList() {
        LaunchedEffect(key1 = true) {
            val (firstInstrument, instrumentList) = instrumentUseCase.getOrderedAndProcessedInstrumentList()
            _uiState.update {
                it.copy(
                    instruments = instrumentList,
                    currentInstrument = firstInstrument
                )
            }

        }

    }

    fun onNewInstrumentSelected(
        dispatcher: NoteGeneratorSettingsDispatcher,
        instrument: Instrument
    ) {
        val uiState = uiSate.value
        val matchedInstrument =
            uiState.instruments.find { _instrument -> _instrument.toString() == instrument.toString() }

        _uiState.update {

            if (matchedInstrument != null) {
                it.copy(currentInstrument = matchedInstrument)
            } else {
                it
            }
        }

        dispatcher.dispatchChangeEvent(SettingsChangeEvent.InstrumentChangeEvent(matchedInstrument ?: uiState.currentInstrument))

    }

}