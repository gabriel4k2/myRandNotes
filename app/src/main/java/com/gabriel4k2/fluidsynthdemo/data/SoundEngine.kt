package com.gabriel4k2.fluidsynthdemo.data

import android.util.Log
import com.gabriel4k2.fluidsynthdemo.domain.model.NoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.providers.JNIInterface
import com.gabriel4k2.fluidsynthdemo.ui.providers.NoteGeneratorSettingsDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SoundEnginePlaybackState {
    PAUSED,
    PLAYING
}

data class SoundEngineState(
    val noteGenerationConfig: NoteGenerationConfig,
    val playbackState: SoundEnginePlaybackState
)

class SoundEngine(
    val jniHandle: JNIInterface,
    val coroutineScope: CoroutineScope,
    val dispatcher: NoteGeneratorSettingsDispatcher
) {
    private var _playbackState: MutableStateFlow<SoundEnginePlaybackState> =
        MutableStateFlow(SoundEnginePlaybackState.PAUSED)
    val playbackState: StateFlow<SoundEnginePlaybackState> = _playbackState

    suspend fun setupEngine() {
        val configFlow = dispatcher.currentConfig.receiveAsFlow()
        combine(configFlow, _playbackState) { config, state ->
            SoundEngineState(config, state)
        }.collect { state ->
            if (state.playbackState == SoundEnginePlaybackState.PLAYING) {
                jniHandle.startPlayingNotesHandle(
                    instrument = state.noteGenerationConfig.instrument,
                    intervalInMs = state.noteGenerationConfig.timeIntervalMs
                )
                Log.e("Engine", "Dispatching the config $state")
            } else {
                Log.e("Engine", "pausing $state")

                jniHandle.pauseSynthHandle()
            }


        }
    }

    fun changePlaybackState(playbackState: SoundEnginePlaybackState) {
        coroutineScope.launch { _playbackState.emit(playbackState) }
    }
}