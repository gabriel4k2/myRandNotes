package com.gabriel4k2.fluidsynthdemo.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalShimmerState
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalThemeProvider

/***
A helper for loading state, if the resource is null then it is
considered loading. This will cause the content
 */
@Composable
fun <T> LoadingStateWrapper(
    isLoading: Boolean,
    mocked: T,
    value: T,
    content: @Composable() (T, Boolean) -> Unit
) {
    CompositionLocalProvider(LocalInLoadingState provides isLoading) {
        content(
            if (isLoading) {
                mocked
            } else {
                value
            }, isLoading
        )
    }

}

val LocalInLoadingState = compositionLocalOf { false }


fun Modifier.shimmerOnLoading(
): Modifier = composed {
    val shouldShimmer = LocalInLoadingState.current
    val shimmerState = LocalShimmerState.current
    val theme = MaterialTheme.colors

    if (shouldShimmer) {
        Modifier.drawWithContent {

            drawRect(Color.White)
            drawRect(
                color = theme.primaryVariant,
                alpha = shimmerState?.alpha?.value ?: 0f
            )
        }
    } else {
        this
    }
}