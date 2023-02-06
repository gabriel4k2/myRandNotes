package com.gabriel4k2.myRandNotes.ui.noteRangePicker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.gabriel4k2.myRandNotes.MainActivity
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.domain.model.Note
import com.gabriel4k2.myRandNotes.ui.noteRangePicker.grid.NoteRangePickerGrid
import com.gabriel4k2.myRandNotes.ui.theme.AppTheme

const val ITEMS_PER_ROW = 6

class NoteRangePickerActivity : ComponentActivity() {
    private val viewModel: NoteRangePickerActivityViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDarkVariant)
        val notesBundleKey = getString(R.string.notes_bundle_key)
        val noteList = intent.getParcelableArrayListExtra<Note>(notesBundleKey) as? List<Note>
        if (noteList != null) {
            viewModel.setNotesList(noteList)
        }
        setContent {
            viewModel.SetAnimationChoreographerEffect()
            val uiState by viewModel.uiSate.collectAsState()
            val gridAnimationChoreographer = uiState.gridAnimationChoreographer

            AppTheme {
                val toolBarColor = MaterialTheme.colors.primary

                Scaffold(topBar = {
                    TopAppBar(backgroundColor = toolBarColor) {
                        NoteRangePickerActivityTopBar(viewModel, gridAnimationChoreographer) { getUpdateNoteListAndFinish() }
                    }
                }) {
                    NoteRangePickerGrid(viewModel)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        getUpdateNoteListAndFinish()
    }

    private fun getUpdateNoteListAndFinish() {
        val notesBundleKey = getString(R.string.notes_bundle_key)

        val newNoteList = viewModel.retrieveNewNotesList()
        val data = Intent(this, MainActivity::class.java).apply {
            putParcelableArrayListExtra(
                notesBundleKey,
                ArrayList(newNoteList)
            )
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val resultCode = newNoteList.any { it.selected }
        setResult(if (resultCode) RESULT_OK else RESULT_CANCELED, data)
        finish()
    }
}
