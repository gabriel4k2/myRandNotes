package com.gabriel4k2.fluidsynthdemo.ui.providers

import androidx.compose.runtime.*
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import com.gabriel4k2.fluidsynthdemo.domain.model.NoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.model.AvailablePrecisions
import com.gabriel4k2.fluidsynthdemo.ui.model.TimeInSeconds
import com.gabriel4k2.fluidsynthdemo.ui.model.UINoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.model.ConfigChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


class NoteGeneratorSettingsController constructor(
    val coroutineScope: CoroutineScope,
    val settingsStorage: SettingsStorage
) {
    private val precisionFlow: MutableSharedFlow<AvailablePrecisions> =
        MutableSharedFlow(replay = 1)
    private val timeFlow: MutableSharedFlow<TimeInSeconds> = MutableSharedFlow(replay = 1)
    private val instrumentFlow: MutableSharedFlow<Instrument> = MutableSharedFlow(replay = 1)
    private val noteRangeFlow: MutableSharedFlow<List<Note>> = MutableSharedFlow(replay = 1)
    private val initialUINoteGenerationConfig =
        settingsStorage.getSettingsOrDefaultToInitial().toUINoteGenerationConfig()

    var dispatchedConfig = Channel<NoteGenerationConfig>(capacity = Channel.CONFLATED)


    fun dispatchChangeEvent(event: ConfigChangeEvent) {
        when (event) {
            is ConfigChangeEvent.PrecisionChangeEvent -> updatePrecision(event.precision)
            is ConfigChangeEvent.TimeChangeEvent -> updateTime(event.time)
            is ConfigChangeEvent.InstrumentChangeEvent -> updateInstrument(event.instrument)
            is ConfigChangeEvent.NoteRangeChangeEvent -> updateNoteRange(event.notes)
        }
    }

    private fun publishStoredConfig() {
        updateInstrument(initialUINoteGenerationConfig.instrument)
        updateTime(initialUINoteGenerationConfig.timeInSeconds.value)
        updatePrecision(initialUINoteGenerationConfig.precision)
        updateNoteRange(initialUINoteGenerationConfig.notes)
    }

    private fun updatePrecision(precision: AvailablePrecisions) {
        coroutineScope.launch { precisionFlow.emit(precision) }
    }

    private fun updateTime(time: String) {
        coroutineScope.launch { timeFlow.emit(TimeInSeconds(time)) }
    }

    private fun updateInstrument(instrument: Instrument) {
        coroutineScope.launch { instrumentFlow.emit(instrument) }
    }

    private fun updateNoteRange(notes: List<Note>) {
        coroutineScope.launch { noteRangeFlow.emit(notes) }
    }

    fun setupSettingsChangeListener() {
        coroutineScope.launch {
            delay(5000)
            publishStoredConfig()
            combine(
                precisionFlow,
                timeFlow,
                instrumentFlow,
                noteRangeFlow
            ) { availablePrecisions, timeInSeconds, instrument, noteRange ->
                NoteGenerationConfig.fromUINoteGenerationConfig(
                    UINoteGenerationConfig(
                        availablePrecisions,
                        timeInSeconds,
                        instrument,
                        noteRange
                    )
                )
            }.debounce(1000).collectIndexed { index, configs ->
                if (index != 0) {
                    settingsStorage.saveSettings(configs)
                }
                dispatchedConfig.send(configs)
            }
        }
    }
}

val LocalNoteGeneratorSettingsDispatcherProvider =
    compositionLocalOf<NoteGeneratorSettingsController> { error("No active user found!") }

@Composable
fun NoteGeneratorSettingsControllerProvider(
    settingsStorage: SettingsStorage,
    content: @Composable () -> Unit,
) {
    val cScope = rememberCoroutineScope()
    val dispatcher = remember(cScope) {
        NoteGeneratorSettingsController(
            settingsStorage = settingsStorage,
            coroutineScope = cScope
        )
    }
    LaunchedEffect(key1 = dispatcher) {
        dispatcher.setupSettingsChangeListener()
    }

    CompositionLocalProvider(
        LocalNoteGeneratorSettingsDispatcherProvider provides dispatcher
    ) {
        content()
    }
}