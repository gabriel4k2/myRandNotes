package com.gabriel4k2.myRandNotes.ui.providers

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

val LocalShimmerState = compositionLocalOf<ShimmerState?> { null }
data class ShimmerState(val alpha: State<Float>)

@Composable
fun ShimmerProvider(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition()
    val shimmerAlpha = transition.animateFloat(
        initialValue = maxOf(0f, 1f),
        targetValue = maxOf(0f, 0.5f),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )
    val shimmerState = remember {
        ShimmerState(alpha = shimmerAlpha)
    }

    CompositionLocalProvider(LocalShimmerState provides shimmerState) {
        content()
    }
}
