package com.gabriel4k2.fluidsynthdemo.ui.providers

import android.net.wifi.p2p.WifiP2pManager.ChannelListener
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class NoteGeneratorSettingsDispatcher  constructor(
    val coroutineScope: CoroutineScope,
    val settingsStorage: SettingsStorage
) {
    private val precisionFlow: MutableSharedFlow<AvailablePrecisions> = MutableSharedFlow(replay = 1)
    private val timeFlow: MutableSharedFlow<String> = MutableSharedFlow(replay = 1)
    private val instrumentFlow: MutableSharedFlow<Instrument> = MutableSharedFlow(replay = 1)
    private val initialUINoteGenerationConfig =
        settingsStorage.getSettingsOrDefaultToInitial().toUINoteGenerationConfig()

    var currentConfig = Channel<NoteGenerationConfig>(capacity =  Channel.CONFLATED)


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
        coroutineScope.launch { precisionFlow.emit(precision) }
    }

    private fun updateTime(time: String) {
        coroutineScope.launch { timeFlow.emit(time) }
    }

    private fun updateInstrument(instrument: Instrument) {
        coroutineScope.launch { instrumentFlow.emit(instrument) }
    }

    fun setupSettingsSChangeListener() {
        coroutineScope.launch {
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

            }.drop(1).debounce(1000).collectIndexed { index , configs ->
                // The first emission is from the initial load so we don't need to save it
                if(index!= 0){
                    settingsStorage.saveSettings(configs)

                }
                currentConfig.send(configs)
//                jniHandle.startPlayingNotesHandle(configs.timeIntervalMs, configs.instrument)
            }



        }
    }



}

val LocalNoteGeneratorSettingsDispatcherProvider =
    compositionLocalOf<NoteGeneratorSettingsDispatcher> { error("No active user found!") }


@Composable
fun NoteGeneratorSettingsDispatcherProvider(settingsStorage: SettingsStorage,    content: @Composable () -> Unit,) {
    val cScope = rememberCoroutineScope()
    val dispatcher = remember(cScope){
        NoteGeneratorSettingsDispatcher(
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