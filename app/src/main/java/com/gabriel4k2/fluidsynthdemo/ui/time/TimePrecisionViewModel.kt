package com.gabriel4k2.fluidsynthdemo.ui.time

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject



enum class AvailablePrecisions(val value: String){
    ZERO("0"),
    HALF_SECOND("500")
}

data class TimePrecisionUIState(
    val currentPrecision: AvailablePrecisions = AvailablePrecisions.ZERO

)

@HiltViewModel
class TimePrecisionViewModel @Inject constructor(
) : ViewModel() {

    private var _uiState: MutableStateFlow<TimePrecisionUIState> = MutableStateFlow(TimePrecisionUIState())
    val uiSate: StateFlow<TimePrecisionUIState> = _uiState




    fun onPrecisionSubmitted(precision: String) {
        _uiState.update { it.copy(currentPrecision = AvailablePrecisions.values().first { precisionEnum -> precisionEnum.value == precision }) }
    }

}