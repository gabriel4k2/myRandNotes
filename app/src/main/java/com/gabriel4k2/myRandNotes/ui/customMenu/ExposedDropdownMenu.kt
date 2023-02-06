package com.gabriel4k2.myRandNotes.ui.customMenu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce

enum class ClickSource {
    FROM_TEXT_FIELD,
    FROM_MENU
}

enum class MenuArrangement {
    DOWN,
    ON_TOP
}

@OptIn(FlowPreview::class)
@Composable
fun <T> ExposedDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: T,
    onItemSelected: (T) -> Unit,
    arrangement: MenuArrangement = MenuArrangement.DOWN,
    itemsPerScroll: Int = 3,
    enabled: Boolean = true,
    suffix: String = ""
) {
    val expandedFlow = remember { MutableSharedFlow<ClickSource>() }
    var expanded by remember { mutableStateOf(false) }
    var currentFocus = remember {
        FocusInteraction.Focus()
    }

    val arrowRotation by animateFloatAsState(if (expanded) 180F else 0F)

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )

            override suspend fun emit(interaction: Interaction) {
                if (interaction is PressInteraction.Release) {
                    expandedFlow.emit(ClickSource.FROM_TEXT_FIELD)
                } else {
                    // Focus is determined by whether the menu is expanded or not.
                    if (interaction !is FocusInteraction) {
                        interactions.emit(interaction)
                    }
                }
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    LaunchedEffect(key1 = expandedFlow) {
        /* We need to add a debounce to handle the following situation:
           1-) The menu is expanded
           2-) The user clicks in the text field in order to close the menu
           Because the popup is opened, the onDismiss will be called (because the text field does not
           belong to the popup) setting the expanded state to false which makes the click get consumed
           in the text field, which inverses the expanded field (which is now false because we just
           clicked outside the menu) thus setting the expansion to true again.
        */
        expandedFlow.debounce(200).collect { source ->
            expanded = when (source) {
                ClickSource.FROM_MENU -> {
                    false
                }
                ClickSource.FROM_TEXT_FIELD -> {
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
        arrangement = arrangement,
        textField = {
            OutlinedTextField(
                modifier = modifier,
                value = selected.toString(),
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                interactionSource = interactionSource,
                trailingIcon = {
                    Row {
                        if (suffix.isNotEmpty()) {
                            Text(suffix)
                        }
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.ArrowDropDown),
                            contentDescription = "Dropdown Arrow",
                            modifier = Modifier.rotate(arrowRotation)
                        )
                    }
                }
            )
        }
    ) { boxWidth, itemHeight ->
        LazyDropDownMenu(
            items = items,
            width = boxWidth,
            itemHeight = itemHeight,
            itemsPerScroll = itemsPerScroll,
            expanded = expanded,
            onExpandChange = { expandedFlow.emit(ClickSource.FROM_MENU) },
            onItemClick = {
                expandedFlow.emit(ClickSource.FROM_MENU)
                onItemSelected(it)
            }
        )
    }
}

@Composable
private fun ExposedDropdownMenuStack(
    arrangement: MenuArrangement = MenuArrangement.DOWN,
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
            dropdownPlaceable.placeRelative(
                0,
                if (arrangement == MenuArrangement.DOWN) {
                    textFieldPlaceable.height
                } else {
                    0
                }
            )
        }
    }
}

private enum class ExposedDropdownMenuSlot { TextField, Dropdown }
