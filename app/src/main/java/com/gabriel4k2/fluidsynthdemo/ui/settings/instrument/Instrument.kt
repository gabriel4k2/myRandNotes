package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.InstrumentViewModel
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.fluidsynthdemo.utils.LoadingStateWrapper
import com.gabriel4k2.fluidsynthdemo.utils.shimmerOnLoading


@Composable
fun InstrumentsMenu(viewModel: InstrumentViewModel = hiltViewModel()) {
    viewModel.SetupViewModel()
    val uiState by viewModel.uiSate.collectAsState()
    val instrumentList = uiState.instruments
    val currentInstrument = uiState.currentInstrument
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current

    Box{
        LoadingStateWrapper(mocked = Instrument.UNKNOWN, isLoading =  currentInstrument == Instrument.UNKNOWN, value = currentInstrument)
            { instrument, isLoading ->
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
                    })
            }
        }




}
