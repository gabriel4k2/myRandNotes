package com.gabriel4k2.fluidsynthdemo.ui.providers

import android.util.Log
import androidx.compose.runtime.*
import com.gabriel4k2.fluidsynthdemo.MainActivity
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.domain.model.NoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.model.UINoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.settings.SettingsChangeEvent
import com.gabriel4k2.fluidsynthdemo.ui.time.AvailablePrecisions
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject


class NoteGeneratorSettingsDispatcher  constructor(
    val jniHandle : JNIInterface,
    settingsStorage: SettingsStorage
) {
    private val precisionFlow: MutableSharedFlow<AvailablePrecisions> = MutableSharedFlow(replay = 1)
    private val timeFlow: MutableSharedFlow<String> = MutableSharedFlow(replay = 1)
    private val instrumentFlow: MutableSharedFlow<Instrument> = MutableSharedFlow(replay = 1)
    private val initialUINoteGenerationConfig =
        settingsStorage.getSettingsOrDefaultToInitial().toUINoteGenerationConfig()


    fun dispatchChangeEvent(event: SettingsChangeEvent) {
        when (event) {
            is SettingsChangeEvent.PrecisionChangeEvent -> updatePrecision(event.precision)
            is SettingsChangeEvent.TimeChangeEvent -> updateTime(event.time)
            is SettingsChangeEvent.InstrumentChangeEvent -> updateInstrument(event.instrument)
        }

    }

    private fun publishInitialConfig() {

        updateInstrument(initialUINoteGenerationConfig.instrument)
        updateTime(initialUINoteGenerationConfig.timeInSeconds)
        updatePrecision(initialUINoteGenerationConfig.precision)

    }

    private fun updatePrecision(precision: AvailablePrecisions) {
        GlobalScope.launch { precisionFlow.emit(precision) }
    }

    private fun updateTime(time: String) {
        GlobalScope.launch { timeFlow.emit(time) }
    }

    private fun updateInstrument(instrument: Instrument) {
        GlobalScope.launch { instrumentFlow.emit(instrument) }
    }

    fun setupSettingsSChangeListener() {
        GlobalScope.launch {
            publishInitialConfig()

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

            }.drop(1).debounce(1000).collect { configs ->
                jniHandle.startPlayingNotesHandle(configs.timeIntervalMs, configs.instrument)
            }



        }
    }


}

val LocalNoteGeneratorSettingsDispatcherProvider =
    compositionLocalOf<NoteGeneratorSettingsDispatcher> { error("No active user found!") }


@Composable
fun NoteGeneratorSettingsDispatcherProvider(settingsStorage: SettingsStorage,  jniHandle: JNIInterface,   content: @Composable () -> Unit,) {
    val dispatcher = remember{
        NoteGeneratorSettingsDispatcher(
            jniHandle,
            settingsStorage
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