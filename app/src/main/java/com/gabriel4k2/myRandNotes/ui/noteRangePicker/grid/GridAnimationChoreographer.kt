package com.gabriel4k2.myRandNotes.ui.noteRangePicker

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.gabriel4k2.myRandNotes.domain.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.absoluteValue

private const val TIME_TOLERANCE = 20 // 20 ms, a little more than a cycle in the standard 60hz freq.
private val CHECK_MARK_ICON_SIZE = 24.dp

/* Component that manages/controls the list of animation states. The important thing to notice here
   is that committing a change in the animation state is decoupled from performing the animation.
   This is because the animation runs asynchronously (in a coroutine). If we were to "commit" in
   same function where we animate, then the commit would be async as well. This would cause race
   condition problems: If two animations were quickly issued, the animation of the second animation
   could run concomitantly with the first, and the order would be unclear (non-deterministic).
 */
class GridItemAnimationStateController(private val coroutineScope: CoroutineScope, val notes: MutableList<Note>) {
    private var gridItemAnimationStatus: MutableList<GridItemState> =
        notes.mapIndexed { index, note ->
            val isNoteSelected = note.selected
            GridItemState(
                animationState = GridItemAnimator(
                    index = index,
                    initialItemAlpha = if (isNoteSelected) 1f else 0f,
                    initialItemSize = if (isNoteSelected) CHECK_MARK_ICON_SIZE else 0.dp
                ),
                isSelected = isNoteSelected
            )
        } as MutableList<GridItemState>

    private var selectedItemsCount = AtomicInteger(gridItemAnimationStatus.count { it.isSelected })

    fun getSelectedItemsCount(): Int {
        return selectedItemsCount.get()
    }

    fun getGridAnimationState(index: Int): GridItemState {
        return gridItemAnimationStatus.get(index = index)
    }

    fun getAllGridItemAnimationStates(): List<GridItemState> {
        return gridItemAnimationStatus
    }

    fun getResultingAnimateState(
        index: Int,
        cancelSelection: Boolean
    ): GridItemAnimator.GridItemAnimatorStatus {
        return determineResultingAnimation(index, cancelSelection)
    }

    private fun determineResultingAnimation(
        index: Int,
        cancelSelection: Boolean
    ): GridItemAnimator.GridItemAnimatorStatus {
        // Trigger the animation if cancelSelection and isCurrentItemSelected have the same boolean
        // value ie: Cancel action (cancelSelection == true) and item is selected
        // (isCurrentItemSelector == true). This is a NXOR.
        val isItemSelected = gridItemAnimationStatus[index].isSelected
        return if (!cancelSelection.xor(isItemSelected)) {
            if (cancelSelection) {
                GridItemAnimator.GridItemAnimatorStatus.WILL_DESELECTED
            } else {
                GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT
            }
        } else {
            GridItemAnimator.GridItemAnimatorStatus.NONE
        }
    }

    fun commitAnimationState(
        index: Int,
        animation: GridItemAnimator.GridItemAnimatorStatus
    ) {
        if (animation != GridItemAnimator.GridItemAnimatorStatus.NONE) {
            val willSelect =
                animation == GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT
            getGridAnimationState(index).isSelected = willSelect

            selectedItemsCount.getAndUpdate {
                it + if (willSelect) {
                    1
                } else {
                    -1
                }
            }
        }
    }

    fun commitAllAnimationState(
        animation: GridItemAnimator.GridItemAnimatorStatus
    ) {
        gridItemAnimationStatus.forEach {
            commitAnimationState(it.animationState.index, animation)
        }
    }

    suspend fun triggerAnimation(
        index: Int,
        animation: GridItemAnimator.GridItemAnimatorStatus
    ) {
        if (animation != GridItemAnimator.GridItemAnimatorStatus.NONE) {
            getGridAnimationState(index).animationState.triggerAnimation(animation)
        }
    }

    suspend fun triggerAllItemsSelectionAnimation() {
        gridItemAnimationStatus.forEach {
            coroutineScope.launch {
                triggerAnimation(
                    index = it.animationState.index,
                    animation = GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT
                )
            }
        }
    }
}

data class GridItemState(
    val animationState: GridItemAnimator,
    var isSelected: Boolean
)

class GridAnimationChoreographer(
    private val coroutineScope: CoroutineScope,
    notes: List<Note>,
    private val itemsPerRow: Int,
    val onZeroItemsSelected: () -> Unit
) {
    var mNotes: MutableList<Note>
    private var gridItemSize: IntSize = IntSize.Zero
    private var gridRowSize: Int = 0

    var gridItemAnimationStateController: GridItemAnimationStateController

    init {
        mNotes = notes.toMutableList()
        gridItemAnimationStateController = GridItemAnimationStateController(coroutineScope, mNotes)
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
        firstVisibleItemIndex: Int,
        firstVisibleItemOffset: Int,
        offset: Offset
    ): Int {
        var row = firstVisibleItemIndex / itemsPerRow
        val visibleSizeOfFirstCardRow = gridItemSize.height - firstVisibleItemOffset
        if (offset.y > visibleSizeOfFirstCardRow) {
            var unconsumedYaxisOffset = offset.y - visibleSizeOfFirstCardRow
            do {
                row += 1
                unconsumedYaxisOffset -= gridItemSize.height.toFloat()
            } while (unconsumedYaxisOffset > 0)
        }

        val column = (offset.x.absoluteValue / gridItemSize.width).toInt()
        return row * itemsPerRow + column
    }

    // If the drag is too fast then some cards will not be selected due
    // to differences between touchscreen frequency and refresh rate.
    // That is: two drag event samples will have offsets that are bigger than a
    // grid item size. To be more clear: say we received a drag event with Offset(0, GRID_SIZE*3)
    // and the previous was Offset(0, 0) and this occurred within TIME_TOLERANCE
    // then we need to account that the second and third items should be selected as well.
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

    fun animateAllGridItemsSelection() {
        gridItemAnimationStateController.commitAllAnimationState(animation = GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT)
        coroutineScope.launch {
            gridItemAnimationStateController.triggerAllItemsSelectionAnimation()
        }
    }

    fun animateGridSelection(
        firstVisibleItemIndex: Int,
        firstVisibleItemOffset: Int,
        change: PointerInputChange,
        reverseDrag: Boolean
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

                    val incomingAnimationStatus = gridItemAnimationStateController.getResultingAnimateState(
                        index = index,
                        cancelSelection = reverseDrag
                    )
                    val willDeselectItem =
                        incomingAnimationStatus == GridItemAnimator.GridItemAnimatorStatus.WILL_DESELECTED
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
                    val incomingAnimationStatus = gridItemAnimationStateController.getResultingAnimateState(
                        index = index,
                        cancelSelection = reverseDrag
                    )
                    val willSelectItem =
                        incomingAnimationStatus == GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT

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
                    gridItemAnimationStateController.getSelectedItemsCount() - deselectedItemsCount + selectedItemsCount

                if (updatedNumberOfSelectedGridItems == 0) {
                    onZeroItemsSelected()
                } else {
                    willBeSelectedIndexes.forEach {
                        gridItemAnimationStateController.commitAnimationState(
                            index = it,
                            animation = GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT
                        )
                    }

                    willBeDeselectedIndexes.forEach {
                        gridItemAnimationStateController.commitAnimationState(
                            index = it,
                            animation = GridItemAnimator.GridItemAnimatorStatus.WILL_DESELECTED
                        )
                    }

                    coroutineScope.launch {
                        val animationJobs =
                            willBeSelectedIndexes.fold(listOf<Deferred<Unit>>()) { acc, it ->
                                acc + this.async {
                                    gridItemAnimationStateController.triggerAnimation(
                                        index = it,
                                        animation = GridItemAnimator.GridItemAnimatorStatus.WILL_SELECT
                                    )
                                }
                            } + willBeDeselectedIndexes.fold(listOf()) { acc, it ->
                                acc + this.async {
                                    gridItemAnimationStateController.triggerAnimation(
                                        index = it,
                                        animation = GridItemAnimator.GridItemAnimatorStatus.WILL_DESELECTED
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
        val isSelected = gridItemAnimationStateController.getGridAnimationState(index).isSelected
        val resultingAnimation =
            gridItemAnimationStateController.getResultingAnimateState(index, isSelected)

        val updatedNumberOfSelectedGridItems =
            gridItemAnimationStateController.getSelectedItemsCount() +
                if (resultingAnimation == GridItemAnimator.GridItemAnimatorStatus.WILL_DESELECTED) {
                    -1
                } else {
                    1
                }

        if (updatedNumberOfSelectedGridItems == 0) {
            onZeroItemsSelected()
        } else {
            gridItemAnimationStateController.commitAnimationState(index, resultingAnimation)
            coroutineScope.launch {
                // If the item is selected then we are about to deselect it.
                gridItemAnimationStateController.triggerAnimation(index, resultingAnimation)
            }
        }
    }
}

class GridItemAnimator(
    val index: Int,
    initialItemSize: Dp,
    initialItemAlpha: Float
) {
    var itemSize =
        mutableStateOf(initialItemSize)
    var itemAlpha = mutableStateOf(initialItemAlpha)
    private var animatable = Animatable(initialValue = 0F)

    suspend fun triggerAnimation(animation: GridItemAnimatorStatus) {
        when (animation) {
            GridItemAnimatorStatus.WILL_SELECT -> {
                animatable.animateTo(1f) {
                    itemSize.value =
                        lerp(start = 0.dp, stop = CHECK_MARK_ICON_SIZE, fraction = this.value)
                    itemAlpha.value = lerp(start = 0f, stop = 1f, fraction = this.value)
                }
            }
            GridItemAnimatorStatus.WILL_DESELECTED -> {
                animatable.animateTo(0f) {
                    itemSize.value = lerp(0.dp, stop = CHECK_MARK_ICON_SIZE, this.value)
                    itemAlpha.value = lerp(start = 0f, stop = 1f, fraction = this.value)
                }
            }
            else -> {}
        }
    }

    private fun lerp(start: Float, stop: Float, fraction: Float): Float {
        return start + fraction * (stop - start)
    }

    enum class GridItemAnimatorStatus {
        WILL_SELECT,
        WILL_DESELECTED,
        NONE
    }
}
