package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gabriel4k2.fluidsynthdemo.R

@Composable
fun OnZeroNotesSelectionDialog(text: String, onDismissRequest : () -> Unit) {

    val textSize = remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    Dialog(
        onDismissRequest = { onDismissRequest() },
        content = {
            Surface( shape = MaterialTheme.shapes.medium) {
                Column( horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.background(MaterialTheme.colors.error).width(textSize.value + 24.dp.times(2)), horizontalArrangement = Arrangement.Center) {
                        Icon(

                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Instrument",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }

                    Text(modifier = Modifier
                        .padding(24.dp)
                        .onGloballyPositioned { textSize.value = with(density){it.size.width.toDp()}}  , text = text, style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    )
}