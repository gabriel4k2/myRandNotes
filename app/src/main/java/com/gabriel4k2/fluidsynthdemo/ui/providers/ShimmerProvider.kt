package com.gabriel4k2.fluidsynthdemo.ui.providers

import androidx.compose.animation.core.*
import androidx.compose.runtime.*

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
                easing = FastOutSlowInEasing,
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