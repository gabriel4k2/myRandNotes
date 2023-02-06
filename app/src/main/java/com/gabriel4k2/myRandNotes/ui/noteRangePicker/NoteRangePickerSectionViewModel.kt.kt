package com.gabriel4k2.myRandNotes.ui.noteRangePicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.gabriel4k2.myRandNotes.domain.model.Note
import com.gabriel4k2.myRandNotes.ui.model.ConfigChangeEvent
import com.gabriel4k2.myRandNotes.ui.providers.LocalSoundEngineProvider
import com.gabriel4k2.myRandNotes.ui.providers.NoteGeneratorSettingsController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NoteRangePickerSectionUIState(
    val notes: List<Note> = emptyList()
)

@HiltViewModel
class NoteRangePickerSectionViewModel @Inject constructor() : ViewModel() {

    private var _uiState: MutableStateFlow<NoteRangePickerSectionUIState> =
        MutableStateFlow(NoteRangePickerSectionUIState())
    val uiSate: StateFlow<NoteRangePickerSectionUIState> = _uiState

    @Composable
    fun SetupViewModel() {
        val engine = LocalSoundEngineProvider.current
        val engineState = engine.engineState.collectAsState().value
        val notes = engineState.noteGenerationConfig.notes
        LaunchedEffect(key1 = notes) {
            _uiState.update {
                it.copy(
                    notes = notes
                )
            }
        }
    }

    fun onNewNoteRangeSelected(
        dispatcher: NoteGeneratorSettingsController,
        notes: List<Note>
    ) {
        dispatcher.dispatchChangeEvent(
            ConfigChangeEvent.NoteRangeChangeEvent(
                notes
            )
        )
    }
}
