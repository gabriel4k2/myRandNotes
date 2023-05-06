package com.gabriel4k2.myRandNotes.ui.providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.gabriel4k2.myRandNotes.data.SoundEngine
import com.gabriel4k2.myRandNotes.data.SoundEnginePlaybackState

val LocalSoundEngineProvider = compositionLocalOf<SoundEngine> { error("No engine available!") }

@Composable
fun SoundEngineProvider(jniHandle: JNIInterface, content: @Composable () -> Unit) {
    val dispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current
    val cScope = rememberCoroutineScope()
    val soundEngine = remember(cScope) { SoundEngine(dispatcher = dispatcher, jniHandle = jniHandle, coroutineScope = cScope) }

    LaunchedEffect(key1 = soundEngine) {
        soundEngine.setupEngine()
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_PAUSE){
                soundEngine.changePlaybackState(SoundEnginePlaybackState.PAUSED)
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    CompositionLocalProvider(LocalSoundEngineProvider provides soundEngine) {
        content()
    }
}
