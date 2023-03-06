package com.gabriel4k2.fluidsynthdemo.ui.customMenu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
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

@Composable
fun <T> ExposedDropdownMenu(
    items: List<T>,
    selected: T,
    onItemSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val currentFocus = remember { mutableStateOf(FocusInteraction.Focus()) }

//    ExposedDropdownMenuBox(expanded = , onExpandedChange = ) {
//        ExposedDropdownMenu(onDismissRequest = )
//    }


//    LaunchedEffect(interactionSource) {
//        interactionSource.interactions
//            .collect {
//                Log.e("interaction", it.toString())
//                expanded = !expanded
//            }
//    }

    LaunchedEffect(interactionSource, expanded) {
        interactionSource.emit(
            if (expanded) {
                currentFocus.value = FocusInteraction.Focus()
                currentFocus.value


            } else {
                FocusInteraction.Unfocus(currentFocus.value)
            }
        )
    }

    ExposedDropdownMenuStack(
        textField = {
            OutlinedTextField(
                modifier = Modifier.clickable { expanded = !expanded },
                value = selected.toString(),
                onValueChange = {},
                interactionSource = interactionSource,
                readOnly = true,
                label = { Text("Instrument") },
                trailingIcon = {
                    val rotation by animateFloatAsState(if (expanded) 180F else 0F)
//                    IconButton(
//                        modifier = Modifier.focusRequester(focusRequester),
//                       onClick =  { expanded = !expanded }
//                    ) {
                    Icon(
                        rememberVectorPainter(Icons.Default.ArrowDropDown),
                        contentDescription = "Dropdown Arrow",
                        Modifier.rotate(rotation),
                    )
//                    }
                }
            )
        }
    ) { boxWidth, itemHeight ->
        LazyDropDownMenu(
            items = items,
            width = boxWidth,
            itemHeight = itemHeight,
            expanded = expanded,
            onExpandChange = { expanded = !expanded },
            onItemClick = {
                expanded = !expanded
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
