package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.LazyDropDownMenu
import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsSection(instrumentList: List<UIInstrument>, currentInstrument: UIInstrument) {
    val interactionSource = remember { MutableInteractionSource() }



    ExposedDropdownMenu(items = instrumentList, selected = currentInstrument, onItemSelected = {})


}


@Composable
fun <T> ExposedDropdownMenu(
    items: List<T>,
    selected: T ,
    onItemSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }


    LaunchedEffect(interactionSource) {
        interactionSource.interactions
            .filter { it is PressInteraction.Press }
            .collect {
                expanded = !expanded
            }
    }
    ExposedDropdownMenuStack(
        textField = {
            OutlinedTextField(
                value = selected.toString(),
                onValueChange = {},
                interactionSource = interactionSource,
                readOnly = true,
                label = { Text("Instrument") },
                trailingIcon = {
                    val rotation by animateFloatAsState(if (expanded) 180F else 0F)
                    IconButton(
                        { expanded = !expanded }
                    ) {
                        Icon(
                            rememberVectorPainter(Icons.Default.ArrowDropDown),
                            contentDescription = "Dropdown Arrow",
                            Modifier.rotate(rotation),
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
            expanded = expanded,
            onExpansionChange = { expanded = it },
            onItemClick = {})
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
