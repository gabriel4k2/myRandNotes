package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.LazyDropDownMenu
import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsSection(instrumentList: List<UIInstrument>, currentInstrument: UIInstrument) {
    val interactionSource = remember { MutableInteractionSource() }


    Column(verticalArrangement =  Arrangement.spacedBy(20.dp)) {
        InstrumentSelectionSection(instrumentList, currentInstrument)
    }


}

@Composable
fun InstrumentSelectionSection(instrumentList: List<UIInstrument>, currentInstrument: UIInstrument) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)){
        Icon(
            painter=painterResource(id = R.drawable.ic_guitar_acoustic),
            contentDescription = "Instrument",
            modifier = Modifier.size(32.dp),

        )

        ExposedDropdownMenu(items = instrumentList, selected = currentInstrument, onItemSelected = {})


    }

}