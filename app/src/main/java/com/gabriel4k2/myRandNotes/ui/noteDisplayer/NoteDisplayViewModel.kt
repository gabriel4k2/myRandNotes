package com.gabriel4k2.myRandNotes.ui.noteDisplayer

import androidx.lifecycle.ViewModel
import com.gabriel4k2.myRandNotes.domain.model.Note
import com.gabriel4k2.myRandNotes.utils.NoteUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NoteDisplayUIState(
    val currentNote: Note? = null

)

@HiltViewModel
class NoteDisplayViewModel @Inject constructor() : ViewModel() {

    private var _uiState: MutableStateFlow<NoteDisplayUIState> =
        MutableStateFlow(NoteDisplayUIState())
    val uiSate: StateFlow<NoteDisplayUIState> = _uiState
    private val allPossibleNotes = NoteUtils.generateInitialNoteList()

    fun onNewNote(midiNumber: Int) {
        var matchedNote = allPossibleNotes.find { it.midiNumber == midiNumber }

        if (matchedNote != null) {
            _uiState.update {
                it.copy(
                    currentNote = matchedNote
                )
            }
        }
    }
}
