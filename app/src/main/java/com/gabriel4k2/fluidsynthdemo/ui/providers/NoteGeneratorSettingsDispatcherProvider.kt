package com.gabriel4k2.fluidsynthdemo.ui.providers

import android.util.Log
import androidx.compose.runtime.*
import com.gabriel4k2.fluidsynthdemo.data.SettingsStorage
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.domain.model.NoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
import com.gabriel4k2.fluidsynthdemo.ui.model.UINoteGenerationConfig
import com.gabriel4k2.fluidsynthdemo.ui.settings.SettingsChangeEvent
import com.gabriel4k2.fluidsynthdemo.ui.time.AvailablePrecisions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject


class NoteGeneratorSettingsDispatcher @Inject constructor(
    private val scope: CoroutineScope,
    private val jNIFunctionsProvider: JNIFunctionsHandle,
    settingsStorage: SettingsStorage
) {
    private val precisionFlow: MutableSharedFlow<AvailablePrecisions> = MutableSharedFlow()
    private val timeFlow: MutableSharedFlow<String> = MutableSharedFlow()
    private val instrumentFlow: MutableSharedFlow<Instrument> = MutableSharedFlow()
    val initialUINoteGenerationConfig =
        settingsStorage.getSettingsOrDefaultToInitial().toUINoteGenerationConfig()


    fun dispatchChangeEvent(event: SettingsChangeEvent) {
        when (event) {
            is SettingsChangeEvent.PrecisionChangeEvent -> updatePrecision(event.precision)
            is SettingsChangeEvent.TimeChangeEvent -> updateTime(event.time)
            is SettingsChangeEvent.InstrumentChangeEvent -> updateInstrument(event.instrument)
        }

    }

    private suspend fun publishInitialConfig() {
        updateInstrument(initialUINoteGenerationConfig.instrument)
        updateTime(initialUINoteGenerationConfig.timeInSeconds)
        updatePrecision(initialUINoteGenerationConfig.precision)

    }

    private fun updatePrecision(precision: AvailablePrecisions) {
        scope.launch { precisionFlow.emit(precision) }
    }

    private fun updateTime(time: String) {
        scope.launch { timeFlow.emit(time) }
    }

    private fun updateInstrument(uiInstrument: UIInstrument) {
        scope.launch { instrumentFlow.emit(uiInstrument.instrument) }
    }

    fun setupSettingsSChangeListener() {
        scope.launch {


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
                jNIFunctionsProvider.startPlayingNotes(configs.timeIntervalMs, configs.instrument)
            }

        }
    }


}

val LocalNoteGeneratorSettingsDispatcherProvider =
    compositionLocalOf<NoteGeneratorSettingsDispatcher> { error("No active user found!") }


@Composable
fun NoteGeneratorSettingsDispatcherProvider(content: @Composable () -> Unit) {
    val cScope = rememberCoroutineScope()
    val jniFunctionsHandle = LocalJNITFunctionsProvider.current
    val dispatcher = remember(cScope, jniFunctionsHandle) {
        NoteGeneratorSettingsDispatcher(
            cScope,
            jniFunctionsHandle
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