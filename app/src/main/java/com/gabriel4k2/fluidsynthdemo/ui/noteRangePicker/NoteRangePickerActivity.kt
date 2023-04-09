package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils.naturalMusicOrder

const val ITEMS_PER_ROW = 6

class NoteRangePickerActivity : ComponentActivity() {


    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteList = intent.getParcelableArrayListExtra<Note>("notes") as? List<Note>
        setContent {
            val orderedNotes = remember(noteList) { noteList?.naturalMusicOrder() }
            val typeTeste = FontFamily(Font(R.font.notomusic))
            val gridState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()
            val gridAnimationChoreographer = remember {
                GridAnimationChoreographer(coroutineScope, noteList ?: emptyList(), ITEMS_PER_ROW)
            }
            var gridItemSizeAlreadyKnown by remember { mutableStateOf(false) }

            GetGridItemSizeEffect(gridItemSizeAlreadyKnown, gridState, gridAnimationChoreographer)

            LazyVerticalGrid(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        Log.e(
                            "change and drag amount",
                            change.toString() + " " + dragAmount.toString()
                        )
                        gridAnimationChoreographer.animateGridSelection(
                            firstVisibleItemIndex = gridState.firstVisibleItemIndex,
                            change = change,
                            reverseDrag = dragAmount.x < 0
                        )
                    }

                }, columns = GridCells.Fixed(ITEMS_PER_ROW), state = gridState, content = {
                itemsIndexed(orderedNotes ?: emptyList()) { index, note ->
                    NoteCard(Modifier.clickable {
                        gridAnimationChoreographer.animateGridSelection(
                            index
                        )
                    }, gridAnimationChoreographer.gridItemAnimationList[index], note, typeTeste)
                }

            })
        }

    }

    @Composable
    private fun GetGridItemSizeEffect(
        gridItemSizeAlreadyKnown: Boolean,
        gridState: LazyGridState,
        gridAnimationChoreographer: GridAnimationChoreographer
    ) {
        var gridItemSizeAlreadyKnown1 = gridItemSizeAlreadyKnown
        LaunchedEffect(key1 = gridItemSizeAlreadyKnown1) {
            if (!gridItemSizeAlreadyKnown1) {
                snapshotFlow { gridState }.collect {
                    if (it.layoutInfo.visibleItemsInfo.isNotEmpty() && it.layoutInfo.visibleItemsInfo.first().size != IntSize.Zero) {
                        // All items have the same size
                        gridAnimationChoreographer.setGridItemSize(it.layoutInfo.visibleItemsInfo.first().size)
                        gridItemSizeAlreadyKnown1 = true
                        gridAnimationChoreographer.setGridRowSize(
                            it.layoutInfo.viewportSize.width
                        )
                    }

                }
            }

        }
    }


}