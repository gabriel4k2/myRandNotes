package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalThemeProvider
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private val midiToNoteMap = NoteUtils.generateMidiNumberToNoteNameMap()


    fun onNewNote(midiNumber: Int) {
        var _noteName = midiToNoteMap[midiNumber]
        if (_noteName != null) {
            _uiState.update { it.copy(currentNote = _noteName) }
        }

    }
}


@Composable
fun NoteDisplayer( currentNote:String = "C1") {
    val theme = LocalThemeProvider.current
    val colors = MaterialTheme.colors

    val radiusInDp = theme.dimensions.noteDisplayerRadius
    val innerRadiusInPx= with(LocalDensity.current){ radiusInDp.toPx()}
    val outerRadiusInPx= with(LocalDensity.current){ (radiusInDp+1.dp).toPx()}
        Canvas(Modifier){
            drawCircle(color = colors.primaryVariant, radius = outerRadiusInPx)
            drawCircle(color = colors.primaryVariant, radius = innerRadiusInPx)
            drawIntoCanvas {

                it.nativeCanvas.drawText(currentNote, 0F,
                    0F, android.graphics.Paint().also {  p->
                        p.textAlign = android.graphics.Paint.Align.CENTER
                        p.textSize = 50f
                    }

                ) }
        }


}

