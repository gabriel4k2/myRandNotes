package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.GridAnimationChoreographer
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.ITEMS_PER_ROW
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.NoteCard
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.NoteRangePickerActivityViewModel
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils.naturalMusicOrder
import kotlin.math.absoluteValue

@Composable
fun NoteRangePickerGrid(viewModel: NoteRangePickerActivityViewModel = hiltViewModel()) {
    viewModel.SetAnimationChoreographerEffect()

    val uiState by viewModel.uiSate.collectAsState()
    val notes = uiState.notes
    val orderedNotes = remember(notes) { notes.naturalMusicOrder() }
    val typeTeste = FontFamily(Font(R.font.notomusic))
    val gridState = rememberLazyGridState()

    var alertUser by remember {
        mutableStateOf(false)
    }

    // wait for gridAnimation Initialization
    val gridAnimationChoreographer = uiState.gridAnimationChoreographer ?: return

    LaunchedEffect(key1 = true){
        alertUser = viewModel.alertUser.receive()
    }

    GetGridItemSizeEffect(gridState, gridAnimationChoreographer)

    if(alertUser){
        AlertDialog(properties = DialogProperties(), onDismissRequest = { alertUser = false }, buttons = { Text("ok")})
    }
    var lineHeight by remember {
        mutableStateOf(0f)
    }

    Box {
        LazyVerticalGrid(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    lineHeight = change.position.y.absoluteValue
                    gridAnimationChoreographer.animateGridSelection(
                        firstVisibleItemIndex = gridState.firstVisibleItemIndex,
                        firstVisibleItemOffset = gridState.firstVisibleItemScrollOffset,
                        change = change,
                        reverseDrag = dragAmount.x < 0
                    )
                }

            }, columns = GridCells.Fixed(ITEMS_PER_ROW), state = gridState, content = {
            itemsIndexed(orderedNotes ) { index, note ->
                NoteCard(
                    Modifier.clickable {
                        gridAnimationChoreographer.animateGridSelection(
                            index
                        )
                    },
                    gridAnimationChoreographer.gridItemAnimationList[index],
                    note,
                    typeTeste
                )
            }
        })
//        Divider(color = Color.Red, thickness = 5.dp, modifier = Modifier.offset(x=0.dp, y = lineHeight.toInt().dp).fillMaxWidth().zIndex(4f).align(
//            Alignment.TopStart))

    }

}

@Composable
private fun GetGridItemSizeEffect(
    gridState: LazyGridState,
    gridAnimationChoreographer: GridAnimationChoreographer
) {
    var gridItemSizeAlreadyKnown by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = gridItemSizeAlreadyKnown) {
        if (!gridItemSizeAlreadyKnown) {
            snapshotFlow { gridState }.collect {
                if (it.layoutInfo.visibleItemsInfo.isNotEmpty() && it.layoutInfo.visibleItemsInfo.first().size != IntSize.Zero) {
                    // All items have the same size
                    gridAnimationChoreographer.setGridItemSize(it.layoutInfo.visibleItemsInfo.first().size)
                    gridItemSizeAlreadyKnown = true
                    gridAnimationChoreographer.setGridRowSize(
                        it.layoutInfo.viewportSize.width
                    )
                }

            }
        }

    }
}