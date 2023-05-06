package com.gabriel4k2.myRandNotes.ui.noteRangePicker

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.domain.model.Note
import com.gabriel4k2.myRandNotes.ui.providers.LocalNoteGeneratorSettingsDispatcherProvider

@Composable
fun NoteRangePickerSection(modifier: Modifier, viewModel: NoteRangePickerSectionViewModel = hiltViewModel()) {
    viewModel.SetupViewModel()
    val uiState by viewModel.uiSate.collectAsState()
    val notes = uiState.notes
    val noteGeneratorSettingsDispatcher = LocalNoteGeneratorSettingsDispatcherProvider.current
    val loading = notes.isEmpty()

    val notesBundleKey = stringResource(id = R.string.notes_bundle_key)

    val getContent = rememberLauncherForActivityResult(
        contract = NoteRangeActivityResultContract(notesBundleKey),
        onResult = { _notes ->
            if (_notes.isEmpty()) {
                // Empty list is used to signal that the user has not selected a single note
                // we ignore it (do not send to the dispatcher).
            } else {
                viewModel.onNewNoteRangeSelected(noteGeneratorSettingsDispatcher, _notes)
            }
        }
    )
    ExtendedFloatingActionButton(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_music_note),
                contentDescription = ""
            )
        },
        text = { Text("CONFIGURE NOTE RANGE") },
        onClick = {
            if (!loading) {
                getContent.launch(notes)
            }
        },
        backgroundColor = if (!loading) MaterialTheme.colors.primary else Color.LightGray,
        shape = RoundedCornerShape(16.dp)
    )
}

class NoteRangeActivityResultContract(private val notesBundleKey: String) : ActivityResultContract<List<Note>, List<Note>>() {
    override fun createIntent(context: Context, input: List<Note>): Intent {
        val intent = Intent(
            context,
            NoteRangePickerActivity::class.java
        )
        return intent.putParcelableArrayListExtra(
            notesBundleKey,
            ArrayList(input)
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Note> {
        return if (resultCode == RESULT_OK) intent?.getParcelableArrayListExtra<Note>(notesBundleKey) as? List<Note> ?: emptyList() else emptyList()
    }
}
