package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import com.gabriel4k2.InstrumentViewModel
import com.gabriel4k2.fluidsynthdemo.MainActivity
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.grid.NoteRangePickerGrid
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils.naturalMusicOrder

const val ITEMS_PER_ROW = 6

class NoteRangePickerActivity : ComponentActivity() {
    private val viewModel: NoteRangePickerActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_range_picker)
        val notesBundleKey = getString(R.string.notes_bundle_key)
        val noteList = intent.getParcelableArrayListExtra<Note>(notesBundleKey) as? List<Note>
        if (noteList != null) {
            viewModel.setNotesList(noteList)
        }
        setContent {
            NoteRangePickerGrid()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        getUpdateNoteList()
    }

    private fun getUpdateNoteList() {
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