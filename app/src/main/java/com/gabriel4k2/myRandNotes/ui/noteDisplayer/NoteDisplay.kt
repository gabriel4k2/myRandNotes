package com.gabriel4k2.myRandNotes.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.ui.noteDisplayer.NoteDisplayViewModel
import com.gabriel4k2.myRandNotes.ui.noteRangePicker.coloredShadow
import com.gabriel4k2.myRandNotes.ui.providers.LocalThemeProvider
import com.gabriel4k2.myRandNotes.utils.NoteUtils.getFormattedNote

@Composable
fun NoteDisplaySection(modifier: Modifier, viewModel: NoteDisplayViewModel) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NoteDisplay(viewModel)
        PlaybackController(Modifier.padding(top = 20.dp))
    }
}

@Composable
fun NoteDisplay(viewModel: NoteDisplayViewModel) {
    val theme = LocalThemeProvider.current
    val colors = MaterialTheme.colors
    val uiState by viewModel.uiSate.collectAsState()
    val currentNote = uiState.currentNote
    val currentNoteDisplay = currentNote?.getFormattedNote() ?: "-"
    val context = LocalContext.current
    val density = LocalDensity.current
    val notoMusicFont = context.resources.getFont(R.font.notomusic)

    val radiusInDp = theme.dimensions.noteDisplayRadius
    val innerRadiusInPx = with(density) { radiusInDp.toPx() }
    val outerRadiusInPx = with(density) { (radiusInDp + 1.dp).toPx() }
    val fontSizeInSp = MaterialTheme.typography.h4
    val fontSizeInPx = with(density) { fontSizeInSp.fontSize.toPx() }
    Canvas(Modifier.coloredShadow(color = colors.primaryVariant, shadowRadius = 250.dp)) {
        drawCircle(color = Color.Black, radius = outerRadiusInPx)
        drawCircle(color = colors.primaryVariant, radius = innerRadiusInPx)
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                currentNoteDisplay,
                0F,
                0F,
                android.graphics.Paint().also { p ->
                    p.textAlign = android.graphics.Paint.Align.CENTER
                    p.textSize = fontSizeInPx
                    p.typeface = notoMusicFont
                }

            )
        }
    }
}
