package com.gabriel4k2.fluidsynthdemo.ui.providers

import androidx.compose.runtime.*
import com.gabriel4k2.fluidsynthdemo.data.SoundEngine


val LocalSoundEngineProvider = compositionLocalOf<SoundEngine> { error("No active user found!") }


@Composable
fun SoundEngineProvider(jniInterface: JNIInterface ,content: @Composable () -> Unit ) {
    val dispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current
    val cScope = rememberCoroutineScope()
    val soundEngine = remember(cScope){ SoundEngine(dispatcher = dispatcher, jniHandle = jniInterface, coroutineScope = cScope )}

    LaunchedEffect(key1 = soundEngine){
        soundEngine.setupEngine()
    }

    CompositionLocalProvider(LocalSoundEngineProvider provides soundEngine) {
        content()
    }

}