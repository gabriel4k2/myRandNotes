package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.width
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
import com.gabriel4k2.fluidsynthdemo.ui.time.TimeFormViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeForm(viewModel: TimeFormViewModel = hiltViewModel() ) {

    val uiState by viewModel.uiSate.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var currentFocus = remember { mutableStateOf( FocusInteraction.Focus())

    }

    val cScope = rememberCoroutineScope()

    val focusSaverInteractionSource = FocusOnPressInteractionSource(currentFocus)


    var pendingTime by  remember{ mutableStateOf(uiState.currentTime) }

    val inErrorState = uiState.inErrorState

    OutlinedTextField(
        value = pendingTime,
        onValueChange = { pendingTime = viewModel.onTimeInputted(it) },
        modifier = Modifier
            .width(100.dp),
        singleLine= true,
        isError = inErrorState,
        trailingIcon = { Text("s") },
        interactionSource = focusSaverInteractionSource,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onDone = {
            pendingTime = viewModel.onTimeSubmited(pendingTime)
            keyboardController?.hide()
            cScope.launch { focusSaverInteractionSource.interactions.emit(FocusInteraction.Unfocus(currentFocus.value)) }

        } )

    )
}

class FocusOnPressInteractionSource(private var currentFocus:  MutableState<FocusInteraction.Focus>) : MutableInteractionSource {
        override val interactions = MutableSharedFlow<Interaction>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

        override suspend fun emit(interaction: Interaction) {
            if (interaction is PressInteraction.Press) {
                currentFocus.value = FocusInteraction.Focus()
                interactions.emit(currentFocus.value )
            } else{
                interactions.emit(interaction)

            }


        }

        override fun tryEmit(interaction: Interaction): Boolean {
            return interactions.tryEmit(interaction)
        }

}