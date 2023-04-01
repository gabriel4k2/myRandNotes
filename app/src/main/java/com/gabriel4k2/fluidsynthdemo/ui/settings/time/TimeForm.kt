package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.fluidsynthdemo.ui.model.TimeInSeconds
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalSoundEngineProvider
import com.gabriel4k2.fluidsynthdemo.ui.settings.time.TimeFormViewModel
import com.gabriel4k2.fluidsynthdemo.utils.LoadingStateWrapper
import com.gabriel4k2.fluidsynthdemo.utils.shimmerOnLoading
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TimeForm(viewModel: TimeFormViewModel = hiltViewModel()) {

    val uiState by viewModel.uiSate.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current
    var pendingTime = uiState.currentTime

    viewModel.SetupViewModel()

    var currentFocus = remember {
        mutableStateOf(FocusInteraction.Focus())

    }

    val cScope = rememberCoroutineScope()

    val focusSaverInteractionSource = FocusOnPressInteractionSource(currentFocus)


//    var pendingTime by remember { mutableStateOf(currentTime) }

    val inErrorState = uiState.inErrorState

    Box(modifier = Modifier.weight(1f)) {
        LoadingStateWrapper(
            mocked = "0",
            isLoading = pendingTime == TimeInSeconds.UNKNOWN,
            value = pendingTime.value,
            content = { data, isLoading ->
                OutlinedTextField(
                    value = data,
                    onValueChange = {
                        if (isLoading) {
                        } else {
                            pendingTime = TimeInSeconds(value = viewModel.onTimeInputted(it))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerOnLoading(),
                    singleLine = true,
                    isError = inErrorState,
                    enabled = !isLoading,
                    trailingIcon = { Text("s") },
                    interactionSource = focusSaverInteractionSource,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = {
                        pendingTime =
                            viewModel.onTimeSubmitted(
                                noteGeneratorSettingsDispatcher,
                                pendingTime
                            )
                        keyboardController?.hide()
                        cScope.launch {
                            focusSaverInteractionSource.interactions.emit(
                                FocusInteraction.Unfocus(
                                    currentFocus.value
                                )
                            )
                        }

                    })

                )
            }
        )
    }


}

class FocusOnPressInteractionSource(private var currentFocus: MutableState<FocusInteraction.Focus>) :
    MutableInteractionSource {
    override val interactions = MutableSharedFlow<Interaction>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override suspend fun emit(interaction: Interaction) {
        if (interaction is PressInteraction.Press) {
            currentFocus.value = FocusInteraction.Focus()
            interactions.emit(currentFocus.value)
        } else {
            interactions.emit(interaction)

        }


    }

    override fun tryEmit(interaction: Interaction): Boolean {
        return interactions.tryEmit(interaction)
    }

}