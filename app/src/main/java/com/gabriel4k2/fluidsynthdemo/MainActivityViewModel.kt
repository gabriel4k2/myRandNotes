package com.gabriel4k2.fluidsynthdemo

import androidx.lifecycle.ViewModel
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.InstrumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val instrumentUseCase: InstrumentUseCase,
    val settingsStorage: SettingsStorage

) : ViewModel() {

//    private var _uiState: MutableStateFlow<InstrumentUIState> =
//        MutableStateFlow(InstrumentUIState())
//    val uiSate: StateFlow<InstrumentUIState> = _uiState



}