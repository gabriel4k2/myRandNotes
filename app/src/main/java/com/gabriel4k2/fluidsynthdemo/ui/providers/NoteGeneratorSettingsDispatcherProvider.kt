package com.gabriel4k2.fluidsynthdemo.ui.providers

import androidx.compose.runtime.*
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.domain.model.NoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.model.AvailablePrecisions
import com.gabriel4k2.fluidsynthdemo.ui.model.TimeInSeconds
import com.gabriel4k2.fluidsynthdemo.ui.model.UINoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.settings.SettingsChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


class NoteGeneratorSettingsController  constructor(
    val coroutineScope: CoroutineScope,
    val settingsStorage: SettingsStorage
) {
    private val precisionFlow: MutableSharedFlow<AvailablePrecisions> = MutableSharedFlow(replay = 1)
    private val timeFlow: MutableSharedFlow<TimeInSeconds> = MutableSharedFlow(replay = 1)
    private val instrumentFlow: MutableSharedFlow<Instrument> = MutableSharedFlow(replay = 1)
    private val initialUINoteGenerationConfig =
        settingsStorage.getSettingsOrDefaultToInitial().toUINoteGenerationConfig()

    var dispatchedConfig = Channel<NoteGenerationConfig>(capacity =  Channel.CONFLATED)


    fun dispatchChangeEvent(event: SettingsChangeEvent) {
        when (event) {
            is SettingsChangeEvent.PrecisionChangeEvent -> updatePrecision(event.precision)
            is SettingsChangeEvent.TimeChangeEvent -> updateTime(event.time)
            is SettingsChangeEvent.InstrumentChangeEvent -> updateInstrument(event.instrument)
        }
    }

    private fun publishStoredConfig() {
        updateInstrument(initialUINoteGenerationConfig.instrument)
        updateTime(initialUINoteGenerationConfig.timeInSeconds.value)
        updatePrecision(initialUINoteGenerationConfig.precision)
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

    fun setupSettingsSChangeListener() {
        coroutineScope.launch {
            delay(5000)
            publishStoredConfig()
            combine(
                precisionFlow,
                timeFlow,
                instrumentFlow
            ) { availablePrecisions, timeInSeconds, instrument ->
                NoteGenerationConfig.fromUINoteGenerationConfig(
                    UINoteGenerationConfig(
                        availablePrecisions,
                        timeInSeconds,
                        instrument
                    )
                )
            }.debounce(1000).collectIndexed { index , configs ->
                if(index!= 0){
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
fun NoteGeneratorSettingsDispatcherProvider(settingsStorage: SettingsStorage,    content: @Composable () -> Unit,) {
    val cScope = rememberCoroutineScope()
    val dispatcher = remember(cScope){
        NoteGeneratorSettingsController(
            settingsStorage = settingsStorage,
            coroutineScope = cScope
        )
    }
    LaunchedEffect(key1 = dispatcher) {
        dispatcher.setupSettingsSChangeListener()
    }

    CompositionLocalProvider(
        LocalNoteGeneratorSettingsDispatcherProvider provides dispatcher
    ) {
        content()
    }
}