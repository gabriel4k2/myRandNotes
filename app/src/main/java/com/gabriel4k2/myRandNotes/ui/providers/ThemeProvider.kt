package com.gabriel4k2.myRandNotes.ui.providers

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

val LocalThemeProvider = compositionLocalOf<Theme> { error("No theme provider found!") }

class Dimensions(configuration: Configuration) {
    private val noteDisplayHorizontalContainerPadding = 70.dp
    val noteDisplayTopContainerPadding = noteDisplayHorizontalContainerPadding / 2
    val noteDisplayRadius = ((configuration.screenWidthDp.dp - noteDisplayHorizontalContainerPadding.times(2)) / 2)
}

class Theme(configuration: Configuration) {
    val dimensions = Dimensions(configuration)
}

// Provides themes that are not assignable to the MaterialTheme object
@Composable
fun ThemeProvider(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    CompositionLocalProvider(LocalThemeProvider provides Theme(configuration)) {
        content()
    }
}
