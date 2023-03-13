package com.gabriel4k2.fluidsynthdemo.ui.customMenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.launch


// Menu open/close animation.
internal const val InTransitionDuration = 120
internal const val OutTransitionDuration = 75

internal const val MaximumVisibleItems = 3

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun <T> LazyDropDownMenu(
    items: List<T>,
    width: Dp,
    itemHeight: Dp,
    expanded: Boolean,
    onItemClick: suspend (T) -> Unit,
    onExpandChange: suspend () -> Unit,
    itemsPerScroll: Int,

    ) {

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded
    val transition = updateTransition(expandedStates, "DropDownMenu")
    val lazyListState = rememberLazyListState()
    val flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState)
    val cScope = rememberCoroutineScope()


    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(
                    durationMillis = InTransitionDuration,
                    easing = LinearOutSlowInEasing
                )
            } else {
                // Expanded to dismissed.
                tween(
                    durationMillis = 1,
                    delayMillis = OutTransitionDuration - 1
                )
            }
        }, label = "dropdownmenu"
    ) {
        if (it) {
            // Menu is expanded.
            1f
        } else {
            // Menu is dismissed.
            0.8f
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = 30)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = OutTransitionDuration)
            }
        }, label = "dropdownmenu"
    ) {
        if (it) {
            // Menu is expanded.
            1f
        } else {
            // Menu is dismissed.
            0f
        }
    }

    Box(
    ) {
        if (expanded) {
            Popup(onDismissRequest = { cScope.launch { onExpandChange() } }) {
                LazyColumn(modifier = Modifier
                    .graphicsLayer {
                        scaleY = scale
                        scaleX = scale
                        this.alpha = alpha
                    }
                    .width(width)
                    .background(MaterialTheme.colors.primaryVariant)
                    .height(if (expanded) itemHeight * itemsPerScroll else 0.dp),
                    state = lazyListState,
                    flingBehavior = flingBehavior) {
                    items(items) { item ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .height(itemHeight)
                                .width(width),
                            onClick = {
                                cScope.launch { onItemClick(item) }

                            }
                        ) {
                            Text(text = item.toString())
                        }
                    }
                }
            }
        }

    }


}
