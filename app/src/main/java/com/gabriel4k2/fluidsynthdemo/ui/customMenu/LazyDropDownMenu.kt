package com.gabriel4k2.fluidsynthdemo.ui.customMenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


// Menu open/close animation.
internal const val InTransitionDuration = 120
internal const val OutTransitionDuration = 75

internal const val MaximumVisibleItems = 3

@Composable
fun <T> LazyDropDownMenu(
    items: List<T>,
    width: Dp,
    itemHeight: Dp,
    expanded: Boolean,
    onExpansionChange: (Boolean) -> Unit,
    onItemClick: (T) -> Unit,

    ) {

    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded
    val transition = updateTransition(expandedStates, "DropDownMenu")
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

    LazyColumn(modifier = Modifier
        .graphicsLayer {
            scaleY = scale
            scaleX = scale
            this.alpha = alpha
        }
        .width(width)
        .background(Color.Red)
        .height(if (expanded) itemHeight * MaximumVisibleItems else 0.dp)) {
        items(items) { item ->
            DropdownMenuItem(
                modifier = Modifier
                    .height(itemHeight)
                    .width(width),
                onClick = {
                    onItemClick(item)
                    onExpansionChange(false)

                }
            ) {
                Text(item.toString())
            }
        }
    }


}
