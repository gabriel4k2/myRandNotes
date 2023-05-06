package com.gabriel4k2.myRandNotes.ui.settings.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.gabriel4k2.myRandNotes.ui.model.ConfigChangeEvent
import com.gabriel4k2.myRandNotes.ui.model.TimeInSeconds
import com.gabriel4k2.myRandNotes.ui.providers.LocalSoundEngineProvider
import com.gabriel4k2.myRandNotes.ui.providers.NoteGeneratorSettingsController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TimeUIState(
    val currentTime: TimeInSeconds = TimeInSeconds.UNKNOWN,
    val inErrorState: Boolean = false

)

@HiltViewModel
class TimeFormViewModel @Inject constructor() : ViewModel() {

    private var _uiState: MutableStateFlow<TimeUIState> = MutableStateFlow(TimeUIState())
    val uiSate: StateFlow<TimeUIState> = _uiState

    @Composable
    fun SetupViewModel() {
        val engine = LocalSoundEngineProvider.current
        val engineState = engine.engineState.collectAsState().value
        val currentTime = engineState.noteGenerationConfig.timeInSeconds
        LaunchedEffect(key1 = currentTime.value) {
            _uiState.update {
                it.copy(
                    currentTime = currentTime
                )
            }
        }
    }

    fun onTimeInputted(time: String): String {
        val firstTwoDigits = time.take(2)
        try {
            val timeInt = firstTwoDigits.toInt()
            if (timeInt == 0) {
                _uiState.update { it.copy(inErrorState = true) }
            } else {
                _uiState.update { it.copy(inErrorState = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(inErrorState = true) }
        }
        return firstTwoDigits
    }

    fun onTimeSubmitted(
        dispatcher: NoteGeneratorSettingsController,
        time: TimeInSeconds
    ): TimeInSeconds {
        val previouslyOnError = uiSate.value.inErrorState
        val currentTime = uiSate.value.currentTime
        val finalTime = if (previouslyOnError) {
            currentTime
        } else {
            time
        }
        _uiState.update { it.copy(currentTime = finalTime, inErrorState = false) }
        if (finalTime != TimeInSeconds.UNKNOWN) {
            dispatcher.dispatchChangeEvent(ConfigChangeEvent.TimeChangeEvent(finalTime.value))
        }
        return finalTime
    }
}
