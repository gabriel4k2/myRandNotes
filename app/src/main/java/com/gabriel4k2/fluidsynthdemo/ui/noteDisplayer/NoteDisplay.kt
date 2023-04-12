package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalThemeProvider


@Composable
fun NoteDisplaySection(modifier: Modifier) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NoteDisplay()
        PlaybackController()
    }

}


@Composable
fun NoteDisplay(currentNote: String = "C1") {
    val theme = LocalThemeProvider.current
    val colors = MaterialTheme.colors

    val radiusInDp = theme.dimensions.noteDisplayerRadius
    val innerRadiusInPx = with(LocalDensity.current) { radiusInDp.toPx() }
    val outerRadiusInPx = with(LocalDensity.current) { (radiusInDp + 1.dp).toPx() }
    Canvas(Modifier) {
        drawCircle(color = colors.primaryVariant, radius = outerRadiusInPx)
        drawCircle(color = colors.primaryVariant, radius = innerRadiusInPx)
        drawIntoCanvas {

            it.nativeCanvas.drawText(currentNote, 0F,
                0F, android.graphics.Paint().also { p ->
                    p.textAlign = android.graphics.Paint.Align.CENTER
                    p.textSize = 50f
                }

            )
        }
    }


}

