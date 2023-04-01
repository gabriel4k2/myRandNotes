package com.gabriel4k2.fluidsynthdemo.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gabriel4k2.ActivityViewModel
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.customMenu.ExposedDropdownMenu
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider
import com.gabriel4k2.fluidsynthdemo.ui.settings.time.TimePrecisionForm

@Composable
fun SettingsSection(
    viewModel: ActivityViewModel,
    instrumentList: List<Instrument>,
    currentInstrument: Instrument
) {


    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        InstrumentSelectionSection(viewModel, instrumentList, currentInstrument)
        TempoSelectionSection()
    }


}

@Composable
fun InstrumentSelectionSection(
    viewModel: ActivityViewModel,
    instrumentList: List<Instrument>,
    currentInstrument: Instrument
) {
    val noteDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current
    Column {
        Column(Modifier.height(48.dp), verticalArrangement = Arrangement.Center) {
            Text(
                text = "Instrument",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        }


        Divider(thickness = 1.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

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


}

@Composable
fun TempoSelectionSection() {

    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Note time interval",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )

            IconButton(modifier = Modifier.offset(x = 18.dp), onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_info_24),
                    contentDescription = "Instrument",
                    modifier = Modifier.size(14.dp),

                    )
            }
        }
        Divider(thickness = 1.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_timer),
                contentDescription = "Instrument",
                modifier = Modifier.size(32.dp),

                )

            TimeForm()
            TimePrecisionForm()

        }

    }


}