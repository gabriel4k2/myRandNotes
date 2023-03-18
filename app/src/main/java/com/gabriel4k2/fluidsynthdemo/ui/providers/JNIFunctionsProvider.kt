package com.gabriel4k2.fluidsynthdemo.ui.providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument

val LocalJNITFunctionsProvider = compositionLocalOf<JNIFunctionsHandle> { error("No active user found!") }


class JNIFunctionsHandle(val startPlayingNotes : (Long, Instrument) -> Unit, val pauseEngine: () -> Unit)


@Composable
fun JNIFunctionsProvider( startPlayingNotes : (Long, Instrument) -> Unit,  pauseEngine: () -> Unit ,content: @Composable () -> Unit ) {
    CompositionLocalProvider(LocalJNITFunctionsProvider provides JNIFunctionsHandle(startPlayingNotes, pauseEngine)) {
        content()
    }

}