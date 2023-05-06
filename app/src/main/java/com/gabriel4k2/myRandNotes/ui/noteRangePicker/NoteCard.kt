package com.gabriel4k2.myRandNotes.ui.noteRangePicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.domain.model.Note

@Composable
fun NoteCard(
    modifier: Modifier,
    animationState: GridItemAnimator,
    note: Note,
    fontFamily: FontFamily
) {
    val colors = MaterialTheme.colors
    Card(
        modifier = modifier
            .padding(all = 3.dp)
            .coloredShadow(colors.primary, alpha = 0.4f),
        elevation = 3.dp,
        border = BorderStroke(
            width = 1.dp,
            color = colors.primaryVariant.copy(alpha = 0.4f)
        )
    ) {
        Box(Modifier.aspectRatio(0.75f, false)) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = note.toString(),
                fontFamily = fontFamily,
                fontSize = 16.sp
            )
            Image(
                painter = painterResource(id = R.drawable.ic_checked),
                colorFilter = ColorFilter.tint(colors.primary),
                contentDescription = null,
                modifier = Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .size(animationState.itemSize.value),
                alpha = animationState.itemAlpha.value
            )
        }
    }
}

fun Modifier.coloredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = composed {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()
    this.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = transparent
            frameworkPaint.setShadowLayer(
                shadowRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )
            it.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )
        }
    }
}
