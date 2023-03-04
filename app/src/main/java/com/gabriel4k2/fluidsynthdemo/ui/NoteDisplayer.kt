package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.gabriel4k2.fluidsynthdemo.providers.LocalThemeProvider

@Composable
fun NoteDisplayer( currentNote:String = "C1") {
    val theme = LocalThemeProvider.current
    val colors = theme.colors
    val radiusInDp = theme.dimensions.noteDisplayerRadius
    val innerRadiusInPx= with(LocalDensity.current){ radiusInDp.toPx()}
    val outerRadiusInPx= with(LocalDensity.current){ (radiusInDp+2.dp).toPx()}
        Canvas(Modifier){
            drawCircle(color = colors.teste, radius = outerRadiusInPx)
            drawCircle(color = colors.primaryLight, radius = innerRadiusInPx)
            drawIntoCanvas {

                it.nativeCanvas.drawText("oi", 0F,
                    0F, android.graphics.Paint().also {  p->
                        p.textAlign = android.graphics.Paint.Align.CENTER
                        p.textSize = 50f
                    }

                ) }
        }


}

