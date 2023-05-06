package com.gabriel4k2.myRandNotes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.InstrumentViewModel
import com.gabriel4k2.myRandNotes.domain.model.Instrument
import com.gabriel4k2.myRandNotes.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.myRandNotes.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.myRandNotes.utils.LoadingStateWrapper
import com.gabriel4k2.myRandNotes.utils.shimmerOnLoading

@Composable
fun InstrumentsMenu(viewModel: InstrumentViewModel = hiltViewModel()) {
    viewModel.SetupViewModel()
    val uiState by viewModel.uiSate.collectAsState()
    val instrumentList = uiState.instruments
    val currentInstrument = uiState.currentInstrument
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current

    Box {
        LoadingStateWrapper(
            mocked = Instrument.UNKNOWN,
            isLoading = currentInstrument == Instrument.UNKNOWN,
            value = currentInstrument
        ) { instrument, isLoading ->
            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth().shimmerOnLoading(),
                items = instrumentList,
                selected = instrument,
                enabled = !isLoading,
                onItemSelected = {
                    viewModel.onNewInstrumentSelected(
                        noteGeneratorSettingsDispatcher,
                        it
                    )
                }
            )
        }
    }
}
