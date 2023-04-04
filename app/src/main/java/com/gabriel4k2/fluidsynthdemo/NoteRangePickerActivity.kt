package com.gabriel4k2.fluidsynthdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.NoteCard
import com.gabriel4k2.fluidsynthdemo.utils.MidiToNoteMap
import com.gabriel4k2.fluidsynthdemo.utils.naturalMusicOrder

class NoteRangePickerActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val teste = intent.getSerializableExtra("notes") as? MidiToNoteMap
        setContent {
            val orderedNotes = remember(teste){ teste?.naturalMusicOrder()}
            val typeTeste = FontFamily(Font(R.font.notomusic))

            LazyVerticalGrid(modifier = Modifier.fillMaxSize().background(Color(0.84f,0.89f,0.98f,1f )), columns = GridCells.Fixed(6), content = {
                items(orderedNotes ?: emptyList()){ note->

                    NoteCard(note, typeTeste)

                }

            })
        }

    }


}