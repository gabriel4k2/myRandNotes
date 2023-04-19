package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.gabriel4k2.fluidsynthdemo.domain.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val TIME_TOLERANCE =
    20 // 20 ms, a little more than a cycle in the standard 60hz freq.


class GridAnimationChoreographer(
    private val coroutineScope: CoroutineScope,
    notes: List<Note>,
    private val itemsPerRow: Int,
    val onZeroItemsSelected: () -> Unit
) {
    var mNotes: MutableList<Note>
    private var gridItemSize: IntSize = IntSize.Zero
    private var gridRowSize: Int = 0
    private var numberOfSelectedGridItems: Int = 0
    var gridItemAnimationList: MutableList<GridItemAnimationState> =
        emptyList<GridItemAnimationState>().toMutableList()


    init {
        mNotes = notes.toMutableList()

        mNotes.forEachIndexed { index, note ->
            if (note.selected) {
                numberOfSelectedGridItems += 1
            }
            gridItemAnimationList.add(
                GridItemAnimationState(
                    index = index,
                    isCurrentItemSelected = note.selected
                )
            )

        }
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

    private fun getGridItemIndexFromOffset(
        firstVisibleItemIndex: Int, firstVisibleItemOffset: Int,
        offset: Offset
    ): Int {
        var row = firstVisibleItemIndex / itemsPerRow
        val visibleSizeOfFirstCardRow = gridItemSize.height - firstVisibleItemOffset
        if (offset.y > visibleSizeOfFirstCardRow) {
            var unconsumedYaxisOffset = offset.y - (gridItemSize.height - firstVisibleItemOffset)
            do {
                row += 1
                unconsumedYaxisOffset -= gridItemSize.height.toFloat()
            } while (unconsumedYaxisOffset > 0)

        }


        val column = (offset.x.absoluteValue / gridItemSize.width).toInt()
        return row * itemsPerRow + column
    }

    // If the drag is too fast then some cards will not be selected due
    // to differences between touchscreen frequency and refresh rate
    // that is: two drag events sample will have offsets that are bigger and a
    // grid size. Say we received a drag event with Offset(0, GRID_SIZE*3) and the
    // previous was Offset(0, 0) and this occurred within TIME_TOLERANCE
    // then we need to account that the second and third items should be selected as well
    private fun getOverlookedIndexes(
        previousPosition: Offset,
        currentDragPosition: Offset,
        firstVisibleItemOffset: Int,
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
                        offset = Offset(x = offset, y = currentDragPosition.y),
                        firstVisibleItemOffset = firstVisibleItemOffset
                    )
                )
                offset += gridItemSize.width
            }

        }
        return indexes.takeIf { it.isNotEmpty() }

    }

    fun animateAllGrids(){
        gridItemAnimationList.forEach {
            coroutineScope.launch {it.triggerAnimation(
                false
            )  }
            numberOfSelectedGridItems = mNotes.size

        }
    }

    fun animateGridSelection(
        firstVisibleItemIndex: Int,
        firstVisibleItemOffset: Int,
        change: PointerInputChange,
        reverseDrag: Boolean,
    ) {
        val currentDragPosition = change.position
        val recordedUptime = change.uptimeMillis
        val previousUptime = change.previousUptimeMillis
        val previousDragPosition = change.previousPosition

        if (currentDragPosition.x < gridRowSize) {

            val lastItemToBeAnimatedIndex =
                getGridItemIndexFromOffset(
                    firstVisibleItemIndex,
                    firstVisibleItemOffset,
                    currentDragPosition
                )

            val overlookedItemsIndexes = if (recordedUptime - previousUptime > TIME_TOLERANCE) {
                null
            } else {
                getOverlookedIndexes(
                    previousDragPosition,
                    currentDragPosition,
                    firstVisibleItemOffset,
                    firstVisibleItemIndex
                )

            }



            synchronized(this) {
                // distinct() avoid edges cases on which the lastItemToBeAnimatedIndex is contained in
                // the overlooked list
                val sequentialIndexesToBeAnimated =
                    listOf(lastItemToBeAnimatedIndex).plus(overlookedItemsIndexes ?: emptyList())
                        .sortedBy { it }.distinct()

                val willBeDeselectedIndexes = sequentialIndexesToBeAnimated.mapNotNull {
                    val index = it
                    val incomingAnimationStatus =
                        gridItemAnimationList[it].determineResultingAnimation(
                            reverseDrag
                        )
                    val willDeselectItem =
                        incomingAnimationStatus == GridItemAnimationState.GridItemAnimationStateStatus.WILL_DESELECTED
                    if (willDeselectItem) {
                        index
                    } else {
                        null
                    }

                }


                val willBeSelectedIndexes = sequentialIndexesToBeAnimated.minus(
                    willBeDeselectedIndexes.toSet()
                ).mapNotNull {
                    val index = it
                    val incomingAnimationStatus =
                        gridItemAnimationList[it].determineResultingAnimation(
                            reverseDrag
                        )
                    val willSelectItem =
                        incomingAnimationStatus == GridItemAnimationState.GridItemAnimationStateStatus.WILL_SELECT

                    if (willSelectItem) {
                        index
                    } else {
                        null
                    }

                }

                val selectedItemsCount =
                    willBeSelectedIndexes.size

                val deselectedItemsCount =
                    willBeDeselectedIndexes.size

                if (selectedItemsCount == 0 && deselectedItemsCount == 0) {
                    return@synchronized
                }

                val updatedNumberOfSelectedGridItems =
                    numberOfSelectedGridItems - deselectedItemsCount + selectedItemsCount

                Log.e(
                    "GridAnimation",
                    "Incoming update is  $updatedNumberOfSelectedGridItems current items: [$numberOfSelectedGridItems]" +
                            " deselected items: [${willBeDeselectedIndexes}]  selected items: [ ${willBeSelectedIndexes}] outlooked $overlookedItemsIndexes"
                )


                if (updatedNumberOfSelectedGridItems == 0) {
                    onZeroItemsSelected()
                } else {
                    numberOfSelectedGridItems = updatedNumberOfSelectedGridItems

                    coroutineScope.launch {

                        val animationJobs =
                            sequentialIndexesToBeAnimated.fold(listOf<Deferred<Boolean>>()) { acc, it ->
                                acc + this.async {
                                    gridItemAnimationList[it].triggerAnimation(
                                        reverseDrag
                                    )
                                }
                            }
                        awaitAll(*animationJobs.toTypedArray())

                    }

                }
            }
        }
    }

    // Callback to be used on card press, in order to perform selection
    fun animateGridSelection(index: Int) {
        val isSelected = gridItemAnimationList[index].isCurrentItemSelected
        val willBeDeselected =
            gridItemAnimationList[index].determineResultingAnimation(isSelected) == GridItemAnimationState.GridItemAnimationStateStatus.WILL_DESELECTED

        val updatedNumberOfSelectedGridItems =
            numberOfSelectedGridItems + if (willBeDeselected) {
                -1
            } else {
                1
            }


        if (updatedNumberOfSelectedGridItems == 0) {
            onZeroItemsSelected()
        } else {
            coroutineScope.launch {
                // If the item is selected then we are about to deselect it.
                gridItemAnimationList[index].triggerAnimation(
                    isSelected
                )
                numberOfSelectedGridItems = updatedNumberOfSelectedGridItems
            }
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


    fun determineResultingAnimation(cancelSelection: Boolean): GridItemAnimationStateStatus {
        // Trigger the animation if cancelSelection and isCurrentItemSelected have the same boolean
        /// value ie: Cancel action (cancelSelection == true) and item is selected
        // (isCurrentItemSelector == true). This is a NXOR.
        return if (!cancelSelection.xor(isCurrentItemSelected)) {
            if (cancelSelection) {
                GridItemAnimationStateStatus.WILL_DESELECTED
            } else {
                GridItemAnimationStateStatus.WILL_SELECT
            }
        } else {
            GridItemAnimationStateStatus.NONE
        }
    }


    suspend fun triggerAnimation(cancelSelection: Boolean): Boolean {
        when (determineResultingAnimation(cancelSelection)) {
            GridItemAnimationStateStatus.WILL_SELECT -> {
                Log.e("GridAnimation", "trigger selection")

                isCurrentItemSelected = true
                animatable.animateTo(1f) {
                    itemSize.value =
                        lerp(start = 0.dp, stop = CHECK_MARK_ICON_SIZE, fraction = this.value)
                    itemAlpha.value = lerp(start = 0f, stop = 1f, fraction = this.value)
                }

            }
            GridItemAnimationStateStatus.WILL_DESELECTED -> {
                Log.e("GridAnimation", "trigger deselection")

                isCurrentItemSelected = false
                animatable.animateTo(0f) {
                    itemSize.value = lerp(0.dp, stop = CHECK_MARK_ICON_SIZE, this.value)
                    itemAlpha.value = lerp(start = 0f, stop = 1f,fraction =  this.value)
                }
            }
            else -> {}
        }

        return isCurrentItemSelected

    }

    private fun lerp(start: Float, stop: Float, fraction: Float): Float {
        return start + fraction * (stop - start)
    }

    enum class GridItemAnimationStateStatus {
        WILL_SELECT,
        WILL_DESELECTED,
        NONE
    }

}