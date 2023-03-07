package com.gabriel4k2.fluidsynthdemo.ui.customMenu

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

enum class ClickSources {
    FROM_TEXT_FIELD,
    FROM_MENU
}


@Composable
fun <T> ExposedDropdownMenu(
    items: List<T>,
    selected: T,
    onItemSelected: (T) -> Unit,
) {
    var expandedFlow = remember { MutableSharedFlow<ClickSources>() }
    var expanded by remember { mutableStateOf(false) }
    var currentFocus = remember {
        FocusInteraction.Focus()
    }

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                Log.e("Outlinetext", interaction.toString())
                if (interaction is PressInteraction.Release) {

                    expandedFlow.emit(ClickSources.FROM_TEXT_FIELD)

                } else {
                    // Nop. Focus is determined whether the menu is expanded or not.
                    if (interaction is FocusInteraction) {

                    } else {
                        interactions.emit(interaction)
//
                    }
                }


            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }


    LaunchedEffect(key1 = expandedFlow) {
        // We need to add a debounce to avoid the following behaviour
        // 1-) The menu is expanded
        // 2-) The user clicks in the text field in order to close the menu
        // Because the popup is opened the onDismiss will be called (because the text does not
        // belong to the popup) setting the expanded to false, now the click gets consumed
        // in the text field, which inverses the expanded field (which is now false because we just
        // clicked outside the menu) thus setting the expansion to true again.
        expandedFlow.debounce(200).collect { source ->
            expanded = when (source) {
                ClickSources.FROM_MENU -> {
                    false
                }
                ClickSources.FROM_TEXT_FIELD -> {
                    !expanded
                }


            }

            if (expanded) {
                currentFocus = FocusInteraction.Focus()
                interactionSource.interactions.emit(currentFocus)

            } else {
                interactionSource.interactions.emit(FocusInteraction.Unfocus(currentFocus))
            }
        }

    }






    ExposedDropdownMenuStack(
        textField = {
            OutlinedTextField(
                value = selected.toString(),
                onValueChange = {},
                readOnly = true,
                interactionSource = interactionSource,
                label = { Text("Instrument") },
                trailingIcon = {
                    val rotation by animateFloatAsState(if (expanded) 180F else 0F)
                    Icon(
                        rememberVectorPainter(Icons.Default.ArrowDropDown),
                        contentDescription = "Dropdown Arrow",
                        Modifier.rotate(rotation),
                    )
                }
            )
        }
    ) { boxWidth, itemHeight ->
        LazyDropDownMenu(
            items = items,
            width = boxWidth,
            itemHeight = itemHeight,
            expanded = expanded,
            onExpandChange = { expandedFlow.emit(ClickSources.FROM_TEXT_FIELD) },
            onItemClick = {
                expandedFlow.emit(ClickSources.FROM_TEXT_FIELD)
                onItemSelected(it)
            })
    }
}


@Composable
private fun ExposedDropdownMenuStack(
    textField: @Composable () -> Unit,
    dropdownMenu: @Composable (boxWidth: Dp, itemHeight: Dp) -> Unit
) {
    SubcomposeLayout { constraints ->
        val textFieldPlaceable =
            subcompose(ExposedDropdownMenuSlot.TextField, textField).first().measure(constraints)
        val dropdownPlaceable = subcompose(ExposedDropdownMenuSlot.Dropdown) {
            dropdownMenu(textFieldPlaceable.width.toDp(), textFieldPlaceable.height.toDp())
        }.first().measure(constraints)
        layout(textFieldPlaceable.width, textFieldPlaceable.height) {
            textFieldPlaceable.placeRelative(0, 0)
            dropdownPlaceable.placeRelative(0, textFieldPlaceable.height)
        }
    }
}

private enum class ExposedDropdownMenuSlot { TextField, Dropdown }
