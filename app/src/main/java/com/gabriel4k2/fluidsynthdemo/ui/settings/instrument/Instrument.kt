package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.ExposedDropdownMenu


@Composable
fun Instrument(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_guitar_acoustic),
            contentDescription = "Instrument",
            modifier = Modifier.size(32.dp),

            )

        ExposedDropdownMenu(
            items = instrumentList,
            selected = currentInstrument,
            onItemSelected = { viewModel.onNewInstrumentSelected( noteDispatcher, it) })


    }
}
