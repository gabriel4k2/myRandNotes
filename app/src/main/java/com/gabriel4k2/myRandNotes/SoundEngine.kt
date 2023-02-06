package com.gabriel4k2.myRandNotes.data

import com.gabriel4k2.myRandNotes.ui.model.UINoteGenerationConfig
import com.gabriel4k2.myRandNotes.ui.providers.JNIInterface
import com.gabriel4k2.myRandNotes.ui.providers.NoteGeneratorSettingsController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SoundEnginePlaybackState {
    PAUSED,
    PLAYING,
    LOADING
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
            // If the code executes here, then the config has been loading and the player
            // is ready to be played
            val playbackStateAfterLoading = if (state == SoundEnginePlaybackState.LOADING) {
                SoundEnginePlaybackState.PAUSED
            } else {
                state
            }
            Pair(config, playbackStateAfterLoading)
        }.collect { state ->
            val config = state.first
            val playback = state.second
            if (playback == SoundEnginePlaybackState.PLAYING) {
                val playableNotes = config.notes.filter { it.selected }.map { it.midiNumber }.toIntArray()
                jniHandle.startPlayingNotesHandle(
                    instrument = config.instrument,
                    intervalInMs = config.timeIntervalMs,
                    playableNotesMidiNumbers = playableNotes
                )
            } else if (playback == SoundEnginePlaybackState.PAUSED) {
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
