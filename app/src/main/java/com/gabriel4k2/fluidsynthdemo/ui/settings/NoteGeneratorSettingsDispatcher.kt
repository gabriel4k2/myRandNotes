package com.gabriel4k2.fluidsynthdemo.ui.settings

import com.gabriel4k2.fluidsynthdemo.ui.model.UIInstrument
import com.gabriel4k2.fluidsynthdemo.ui.time.AvailablePrecisions
import kotlinx.coroutines.flow.MutableSharedFlow

class NoteGeneratorSettingsDispatcher {
     private val precisionFlow: MutableSharedFlow<AvailablePrecisions> = MutableSharedFlow()
     private val timeFlow: MutableSharedFlow<String> = MutableSharedFlow()
     private val instrumentFlow: MutableSharedFlow<UIInstrument> = MutableSharedFlow()

     fun dispatchChangeEvent(event: SettingsChangeEvent){
          when (event) {
               is SettingsChangeEvent.PrecisionChangeEvent -> updatePrecision(event.precision)
               is SettingsChangeEvent.TimeChangeEvent -> updateTime(event.input)
               is SettingsChangeEvent.InstrumentChangeEvent -> updateInstrument(event.input)
          }

     }

     private fun updatePrecision(precision: AvailablePrecisions){
          precisionFlow.emit(precision)

     }

}