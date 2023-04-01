package com.gabriel4k2.fluidsynthdemo.ui.settings.time

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.MenuArrangement
import com.gabriel4k2.fluidsynthdemo.ui.model.AvailablePrecisions
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.fluidsynthdemo.utils.LoadingStateWrapper
import com.gabriel4k2.fluidsynthdemo.utils.shimmerOnLoading


@Composable
fun RowScope.TimePrecisionForm(viewModel: TimePrecisionViewModel = hiltViewModel()) {
    val uiState = viewModel.uiSate.collectAsState()
    val selectedTimePrecision = uiState.value.currentPrecision
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current

    viewModel.SetupViewModel()

    Box(modifier = Modifier.weight(1f)) {
        LoadingStateWrapper(
            isLoading = selectedTimePrecision == AvailablePrecisions.UNKNOWN,
            mocked = AvailablePrecisions.ZERO,
            value = selectedTimePrecision
        ) { precision, isLoading ->
                ExposedDropdownMenu(
                    modifier = Modifier.shimmerOnLoading(),
                    items = AvailablePrecisions.values().filter { it != AvailablePrecisions.UNKNOWN }.map { it.value },
                    selected = selectedTimePrecision.value,
                    enabled = !isLoading,
                    itemsPerScroll = 2,
                    arrangement = MenuArrangement.OnTop,
                    onItemSelected = {
                        viewModel.onPrecisionSubmitted(
                            noteGeneratorSettingsDispatcher,
                            precision.value
                        )
                    })


        }


    }

}
