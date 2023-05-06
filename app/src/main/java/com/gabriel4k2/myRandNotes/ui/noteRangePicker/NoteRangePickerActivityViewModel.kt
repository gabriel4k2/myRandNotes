package com.gabriel4k2.myRandNotes.ui.noteRangePicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel4k2.myRandNotes.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteRangePickerActivityUIState(
    val notes: List<Note> = emptyList(),
    var gridAnimationChoreographer: GridAnimationChoreographer? = null
)

@HiltViewModel
class NoteRangePickerActivityViewModel @Inject constructor() : ViewModel() {
    private var _uiState: MutableStateFlow<NoteRangePickerActivityUIState> =
        MutableStateFlow(NoteRangePickerActivityUIState())
    val uiSate: StateFlow<NoteRangePickerActivityUIState> = _uiState

    val alertUser = Channel<Boolean>(capacity = Channel.CONFLATED)

    fun setNotesList(notes: List<Note>) {
        viewModelScope.launch { _uiState.update { it.copy(notes = notes) } }
    }

    @Composable
    fun SetAnimationChoreographerEffect() {
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = true) {
            _uiState.update {
                it.copy(
                    gridAnimationChoreographer = GridAnimationChoreographer(
                        coroutineScope = coroutineScope,
                        notes = uiSate.value.notes,
                        itemsPerRow = ITEMS_PER_ROW,
                        onZeroItemsSelected = { viewModelScope.launch { alertUser.send(true) } }
                    )
                )
            }
        }
    }

    // retrieves the note list with update selected info based on the choreographer
    fun retrieveNewNotesList(): List<Note> {
        val uiState = uiSate.value
        val notes = uiState.notes
        val gridAnimationChoreographer = uiState.gridAnimationChoreographer
        return notes.zip(
            gridAnimationChoreographer?.gridItemAnimationStateController?.getAllGridItemAnimationStates() ?: emptyList()
        ) { note, gridItem ->
            Note(
                chord = note.chord,
                octave = note.octave,
                midiNumber = note.midiNumber,
                selected = gridItem.isSelected
            )
        }
    }
}
