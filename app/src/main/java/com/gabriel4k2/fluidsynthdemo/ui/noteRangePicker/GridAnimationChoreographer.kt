package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val TIME_TOLERANCE =
    20 // 20 ms, a little more than a cycle in the standard 60hz freq.

class GridAnimationChoreographer(
    val coroutineScope: CoroutineScope,
    notes: List<Note>,
    private val itemsPerRow: Int
) {
    var mNotes: MutableList<Note>
    private var gridItemSize: IntSize = IntSize.Zero
    private var gridRowSize: Int = 0
    var gridItemAnimationList: MutableList<GridItemAnimationState> =
        emptyList<GridItemAnimationState>().toMutableList()
    var lastAnimatedItemIndex = -1

    init {
        mNotes = notes.toMutableList()

        mNotes.forEachIndexed { index, note ->
            gridItemAnimationList.add(
                GridItemAnimationState(
                    index = index,
                    isCurrentItemSelected = false
                )
            )
        }
    }

    private fun getGridItemIndexFromOffset(firstVisibleItemIndex: Int, offset: Offset): Int {
        val row = (offset.y.absoluteValue / gridItemSize.height).toInt()
        val column = (offset.x.absoluteValue / gridItemSize.width).toInt()
        return firstVisibleItemIndex + row * itemsPerRow + column
    }

    // If the drag is too fast then some cards will not be selected due
    // to differences between touchscreen frequency and refresh rate
    // that is: two drag events sample will have offsets that are bigger and a
    // grid size. Say we received a drag event with Offset(0, GRID_SIZE*3) and the
    // previous was Offset(0, 0) and this occurred within TIME_TOLERANCE
    // then we need to account that the second and third items should be selected as well
    @OptIn(ExperimentalComposeUiApi::class)
    private fun getOverlookedIndexes(
        previousPosition: Offset,
        currentDragPosition: Offset,
        firstVisibleItemIndex: Int
    ): List<Int>? {
        val indexes = emptyList<Int>().toMutableList()
        if (currentDragPosition.x.absoluteValue - previousPosition.x.absoluteValue > gridItemSize.width) {
            // We only care for x-axis offset
            var offset = previousPosition.x.absoluteValue + gridItemSize.width - 5
            // Interpolate points
            while (offset < currentDragPosition.x.absoluteValue) {
                indexes.add(
                    getGridItemIndexFromOffset(
                        firstVisibleItemIndex = firstVisibleItemIndex,
                        // We are looking for same row, overlooked items, so we pass the currentDrag
                        // y-axis
                        offset = Offset(x = offset, y = currentDragPosition.y)
                    )
                )
                offset += gridItemSize.width
            }

        }
        return indexes.takeIf { it.isNotEmpty() }

    }


    fun setGridItemSize(size: IntSize) {
        if (size != IntSize.Zero) {
            gridItemSize = size
        }
    }

    fun setGridRowSize(size: Int) {
        if (size != 0) {
            gridRowSize = size
        }
    }

    fun animateGridSelection(
        firstVisibleItemIndex: Int,
        change: PointerInputChange,
        reverseDrag: Boolean,

        ) {
        val currentDragPosition = change.position
        val recordedUptime = change.uptimeMillis
        val previousUptime = change.previousUptimeMillis
        val previousDragPosition = change.previousPosition

        if (currentDragPosition.x < gridRowSize) {
            val lastItemToBeAnimatedIndex =
                getGridItemIndexFromOffset(firstVisibleItemIndex, currentDragPosition)
            val overlookedItemsIndexes = if (recordedUptime - previousUptime > TIME_TOLERANCE) {
                null
            } else {
                getOverlookedIndexes(
                    previousDragPosition,
                    currentDragPosition,
                    firstVisibleItemIndex
                )

            }


            coroutineScope.launch {

                val sequencialIndexesToBeAnimated =
                    listOf(lastItemToBeAnimatedIndex).plus(overlookedItemsIndexes ?: emptyList())
                        .sortedBy { it }
                launch {
                    sequencialIndexesToBeAnimated.forEach {
                        gridItemAnimationList[it].triggerAnimation(
                            reverseDrag
                        )
                    }
                }

            }
        }

    }

    fun animateGridSelection(index: Int) {
        coroutineScope.launch {
            val isSelected = gridItemAnimationList[index].isCurrentItemSelected
            gridItemAnimationList[index].triggerAnimation(
                isSelected
            )
        }
    }

}

class GridItemAnimationState(
    val index: Int,
    var isCurrentItemSelected: Boolean
) {
    var itemSize =
        mutableStateOf(if (isCurrentItemSelected) CHECK_MARK_ICON_SIZE else 0.dp)
    var itemAlpha = mutableStateOf(if (isCurrentItemSelected) 1f else 0f)
    private var animatable = Animatable(initialValue = 0F)

    suspend fun triggerAnimation(cancelSelection: Boolean) {

        if (!cancelSelection && !animatable.isRunning) {
            isCurrentItemSelected = true
            animatable.animateTo(1f) {
                itemSize.value = lerp(0.dp, stop = CHECK_MARK_ICON_SIZE, this.value)
                itemAlpha.value = lerp(0f, stop = 1f, this.value)
            }

        } else if (cancelSelection) {
            isCurrentItemSelected = false
            animatable.animateTo(0f) {
                itemSize.value = lerp(0.dp, stop = CHECK_MARK_ICON_SIZE, this.value)
                itemAlpha.value = lerp(0f, stop = 1f, this.value)
            }
        }


    }

    private fun lerp(start: Float, stop: Float, value: Float): Float {
        return start + value * (stop - start)
    }


}