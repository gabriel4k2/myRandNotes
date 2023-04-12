package com.gabriel4k2.fluidsynthdemo.ui.noteDisplayer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class NoteDisplayerUIState(
    val currentNote: String = "-"

)

@HiltViewModel
class NoteDisplayerViewModel @Inject constructor(
) : ViewModel() {

    private var _uiState: MutableStateFlow<NoteDisplayerUIState> =
        MutableStateFlow(NoteDisplayerUIState())
    val uiSate: StateFlow<NoteDisplayerUIState> = _uiState



//    fun onNewNote(midiNumber: Int) {
//        var _noteName = midiToNoteMap[midiNumber]
//        if (_noteName != null) {
//            _uiState.update { it.copy(currentNote = _noteName) }
//        }
//
//    }
}
