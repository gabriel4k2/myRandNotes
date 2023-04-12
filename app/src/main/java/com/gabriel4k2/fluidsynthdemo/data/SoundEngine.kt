package com.gabriel4k2.fluidsynthdemo.data

import android.util.Log
import com.gabriel4k2.fluidsynthdemo.ui.model.UINoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.providers.JNIInterface
import com.gabriel4k2.fluidsynthdemo.ui.providers.NoteGeneratorSettingsController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SoundEnginePlaybackState {
    PAUSED,
    PLAYING,
    LOADING,
}

data class SoundEngineState(
    val noteGenerationConfig: UINoteGenerationConfig,
    val playbackState: SoundEnginePlaybackState
)

class SoundEngine(
    val jniHandle: JNIInterface,
    val coroutineScope: CoroutineScope,
    val dispatcher: NoteGeneratorSettingsController
) {
    private val _playbackState: MutableStateFlow<SoundEnginePlaybackState> =
        MutableStateFlow(SoundEnginePlaybackState.LOADING)

    private val _engineState: MutableStateFlow<SoundEngineState> = MutableStateFlow(
        SoundEngineState(
            noteGenerationConfig = UINoteGenerationConfig.UNKNOWN,
            playbackState = SoundEnginePlaybackState.LOADING
        )
    )
    val engineState: StateFlow<SoundEngineState> = _engineState

    suspend fun setupEngine() {
        publishInitialState()
        val configFlow = dispatcher.dispatchedConfig.receiveAsFlow()
        combine(configFlow, _playbackState) { config, state ->
            Pair(config, state)
        }.collect { state ->
            val config = state.first
            val playback = state.second
            if (playback == SoundEnginePlaybackState.PLAYING) {
                jniHandle.startPlayingNotesHandle(
                    instrument = config.instrument,
                    intervalInMs = config.timeIntervalMs
                )
            } else if (playback == SoundEnginePlaybackState.PAUSED) {
                Log.e("Engine", "pausing $state")
                jniHandle.pauseSynthHandle()
            }
            _engineState.emit(
                SoundEngineState(
                    noteGenerationConfig = config.toUINoteGenerationConfig(),
                    playbackState = playback
                )
            )
        }
    }

    // Initial State, on which we are still expecting the configuration to be loaded from
    // the sharedPreferences
    private suspend fun publishInitialState() {
        _engineState.emit(
            SoundEngineState(
                noteGenerationConfig = UINoteGenerationConfig.UNKNOWN,
                playbackState = SoundEnginePlaybackState.LOADING
            )
        )

    }

    fun changePlaybackState(playbackState: SoundEnginePlaybackState) {
        coroutineScope.launch { _playbackState.emit(playbackState) }
    }
}