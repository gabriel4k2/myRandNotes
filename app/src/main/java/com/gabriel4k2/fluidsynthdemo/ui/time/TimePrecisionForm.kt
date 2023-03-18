package com.gabriel4k2.fluidsynthdemo.ui.time

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.MenuArrangement
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider


@Composable
fun TimePrecisionForm(viewModel: TimePrecisionViewModel = hiltViewModel()) {
    val uiState = viewModel.uiSate.collectAsState()
    val selectedTimePrecision = uiState.value.currentPrecision
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current

    Column(modifier = Modifier.width(125.dp)) {
        ExposedDropdownMenu(
            items = AvailablePrecisions.values().map { it.value },
            selected = selectedTimePrecision.value,
            itemsPerScroll = 2,
            arrangement = MenuArrangement.OnTop,
            onItemSelected = {viewModel.onPrecisionSubmitted(noteGeneratorSettingsDispatcher, it)})

    }

}
