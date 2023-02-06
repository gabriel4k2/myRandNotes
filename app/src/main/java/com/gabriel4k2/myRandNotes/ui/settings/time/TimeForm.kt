package com.gabriel4k2.myRandNotes.ui.settings.time

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.myRandNotes.ui.model.TimeInSeconds
import com.gabriel4k2.myRandNotes.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.myRandNotes.utils.LoadingStateWrapper
import com.gabriel4k2.myRandNotes.utils.shimmerOnLoading
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TimeForm(viewModel: TimeFormViewModel = hiltViewModel()) {
    viewModel.SetupViewModel()
    val uiState by viewModel.uiSate.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentTime = uiState.currentTime
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current

    val currentFocus = remember {
        mutableStateOf(FocusInteraction.Focus())
    }

    val cScope = rememberCoroutineScope()

    val focusSaverInteractionSource = FocusOnPressInteractionSource(currentFocus)

    var pendingTime by remember(currentTime) { mutableStateOf(currentTime) }

    val inErrorState = uiState.inErrorState

    Box(modifier = Modifier.weight(1f)) {
        LoadingStateWrapper(
            mocked = "0",
            isLoading = pendingTime == TimeInSeconds.UNKNOWN,
            value = pendingTime.value,
            content = { _, isLoading ->
                OutlinedTextField(
                    value = pendingTime.value,
                    onValueChange = {
                        if (!isLoading) {
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
        onBufferOverflow = BufferOverflow.DROP_OLDEST
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
