package com.gabriel4k2.myRandNotes.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.ui.InstrumentsMenu
import com.gabriel4k2.myRandNotes.ui.settings.time.TimeForm
import com.gabriel4k2.myRandNotes.ui.settings.time.TimePrecisionMenu

@Composable
fun SettingsSection(
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            InstrumentSelectionSection()
            TempoSelectionSection()
        }
    }
}

@Composable
fun InstrumentSelectionSection() {
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
                modifier = Modifier.size(32.dp)
            )

            InstrumentsMenu()
        }
    }
}

@Composable
fun TempoSelectionSection() {
    Column {
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
                modifier = Modifier.size(32.dp)

            )
            TimeForm()
            TimePrecisionMenu()
        }
    }
}
