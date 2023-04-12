package com.gabriel4k2.fluidsynthdemo.ui.settings.time

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.gabriel4k2.fluidsynthdemo.ui.model.AvailablePrecisions
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalSoundEngineProvider
import com.gabriel4k2.fluidsynthdemo.ui.providers.NoteGeneratorSettingsController
import com.gabriel4k2.fluidsynthdemo.ui.model.ConfigChangeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject




data class TimePrecisionUIState(
    val currentPrecision: AvailablePrecisions = AvailablePrecisions.ZERO

)

@HiltViewModel
class TimePrecisionViewModel @Inject constructor(
) : ViewModel() {

    private var _uiState: MutableStateFlow<TimePrecisionUIState> = MutableStateFlow(
        TimePrecisionUIState()
    )
    val uiSate: StateFlow<TimePrecisionUIState> = _uiState

    @Composable
    fun SetupViewModel() {
        val engine = LocalSoundEngineProvider.current
        val engineState = engine.engineState.collectAsState().value
        val currentPrecision = engineState.noteGenerationConfig.precision
        LaunchedEffect(key1 = currentPrecision.value) {
            Log.e("currentTime", currentPrecision.value.toString())

            _uiState.update {
                it.copy(
                    currentPrecision = currentPrecision
                )
            }
        }
    }


    fun onPrecisionSubmitted(dispatcher: NoteGeneratorSettingsController, precision: String) {
        val availablePrecisions = AvailablePrecisions.values().first { precisionEnum -> precisionEnum.value == precision }
        _uiState.update { it.copy(currentPrecision = availablePrecisions) }
        dispatcher.dispatchChangeEvent(ConfigChangeEvent.PrecisionChangeEvent(availablePrecisions))
    }

}