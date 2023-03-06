package com.gabriel4k2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.InstrumentUseCase
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
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
    val instruments: List<UIInstrument> = emptyList(),
    val currentInstrument: UIInstrument = UIInstrument.EMPTY_INSTRUMENT

)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val instrumentUseCase: InstrumentUseCase,
) : ViewModel(){

    private var _uiState:MutableStateFlow<MainActivityUIState> = MutableStateFlow(MainActivityUIState())
    val uiSate: StateFlow<MainActivityUIState> = _uiState

    @Composable
    fun RetrieveInstrumentList()  {
        LaunchedEffect(key1 = true){
            val (firstInstrument, instrumentList) = instrumentUseCase.getOrderedAndProcessedInstrumentList()
            _uiState.update { it.copy(instruments = instrumentList, currentInstrument = firstInstrument) }

        }

    }

}