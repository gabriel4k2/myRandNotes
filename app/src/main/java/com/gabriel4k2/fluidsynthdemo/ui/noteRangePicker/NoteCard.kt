package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.model.Note


@Composable
fun NoteCard(note: Note, fontFamily: FontFamily) {

    Card(
        modifier = Modifier
            .padding(all = 2.dp)
    , elevation = 4.dp,

    ) {
        Box(Modifier.aspectRatio(0.75f, false)){
            Text(modifier= Modifier.align(Alignment.Center), text=note.toString(), fontFamily = fontFamily)

        }

    }


}