package com.gabriel4k2.myRandNotes.ui.settings.time

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.myRandNotes.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.myRandNotes.ui.customMenu.MenuArrangement
import com.gabriel4k2.myRandNotes.ui.model.AvailablePrecisions
import com.gabriel4k2.myRandNotes.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.myRandNotes.utils.LoadingStateWrapper
import com.gabriel4k2.myRandNotes.utils.shimmerOnLoading

@Composable
fun RowScope.TimePrecisionMenu(viewModel: TimePrecisionViewModel = hiltViewModel()) {
    val uiState = viewModel.uiSate.collectAsState()
    val selectedTimePrecision = uiState.value.currentPrecision
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current

    viewModel.SetupViewModel()

    Box(modifier = Modifier.weight(1f)) {
        LoadingStateWrapper(
            isLoading = selectedTimePrecision == AvailablePrecisions.UNKNOWN,
            mocked = AvailablePrecisions.ZERO,
            value = selectedTimePrecision
        ) { _, isLoading ->
            ExposedDropdownMenu(
                modifier = Modifier.shimmerOnLoading(),
                items = AvailablePrecisions.values().filter { it != AvailablePrecisions.UNKNOWN }.map { it.value },
                selected = selectedTimePrecision.value,
                enabled = !isLoading,
                itemsPerScroll = 2,
                arrangement = MenuArrangement.ON_TOP,
                suffix = "ms",
                onItemSelected = {
                    viewModel.onPrecisionSubmitted(
                        noteGeneratorSettingsDispatcher,
                        it
                    )
                }
            )
        }
    }
}
