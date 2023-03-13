package com.gabriel4k2.fluidsynthdemo.ui.time

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class TimeUIState(
    val currentTime: String = "1",
    val inErrorState: Boolean = false

)

@HiltViewModel
class TimeFormViewModel @Inject constructor(
) : ViewModel() {

    private var _uiState: MutableStateFlow<TimeUIState> = MutableStateFlow(TimeUIState())
    val uiSate: StateFlow<TimeUIState> = _uiState

    fun onTimeInputted(time: String) : String {

        val twoDigitsTime = try {
            val timeInt = time.toInt()
            if (timeInt == 0) {
                _uiState.update { it.copy(inErrorState = true) }
            } else {
                _uiState.update { it.copy(inErrorState = false) }

            }
            time
        } catch (e: Exception){
            _uiState.update { it.copy(inErrorState = true) }
            time
        }.take(2)

        return twoDigitsTime


    }

    fun onTimeSubmited(time: String) : String{
        val previouslyOnError = uiSate.value.inErrorState
        val currentTime = uiSate.value.currentTime
        val finalTime = if(previouslyOnError){ currentTime } else { time}
        _uiState.update { it.copy(currentTime = finalTime,inErrorState = false) }
        return finalTime
    }

}