package com.gabriel4k2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.InstrumentUseCase
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalSoundEngineProvider
import com.gabriel4k2.fluidsynthdemo.ui.providers.NoteGeneratorSettingsController
import com.gabriel4k2.fluidsynthdemo.ui.model.ConfigChangeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class InstrumentUIState(
    val instruments: List<Instrument> = emptyList(),
    val currentInstrument: Instrument = Instrument.UNKNOWN
)

@HiltViewModel
class InstrumentViewModel @Inject constructor(
    private val instrumentUseCase: InstrumentUseCase,
    val settingsStorage: SettingsStorage

) : ViewModel() {

    private var _uiState: MutableStateFlow<InstrumentUIState> =
        MutableStateFlow(InstrumentUIState())
    val uiSate: StateFlow<InstrumentUIState> = _uiState


    @Composable
    fun SetupViewModel() {
        RetrieveInstrumentList()
        val engine = LocalSoundEngineProvider.current
        val engineState = engine.engineState.collectAsState().value
        val currentInstrument = engineState.noteGenerationConfig.instrument
        LaunchedEffect(key1 = currentInstrument) {

            _uiState.update {
                it.copy(
                    currentInstrument = currentInstrument
                )
            }
        }
    }


    @Composable
    private fun RetrieveInstrumentList() {
        LaunchedEffect(key1 = true) {
            val instrumentList = instrumentUseCase.getOrderedAndProcessedInstrumentList()
            _uiState.update {
                it.copy(
                    instruments = instrumentList,
                )
            }

        }

    }

    fun onNewInstrumentSelected(
        dispatcher: NoteGeneratorSettingsController,
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

        dispatcher.dispatchChangeEvent(
            ConfigChangeEvent.InstrumentChangeEvent(
                matchedInstrument ?: uiState.currentInstrument
            )
        )

    }

}