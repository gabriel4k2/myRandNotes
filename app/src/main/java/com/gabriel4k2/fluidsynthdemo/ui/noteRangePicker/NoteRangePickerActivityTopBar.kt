package com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.gabriel4k2.fluidsynthdemo.R
import kotlinx.coroutines.launch

private val TOOLBAR_CHECK_MARK_ICON_SIZE = 16.dp

@Composable
fun NoteRangePickerActivityTopBar(
    viewModel: NoteRangePickerActivityViewModel,
    gridAnimationChoreographer: GridAnimationChoreographer?
) {
    val uiState by viewModel.uiSate.collectAsState()
    val notes = uiState.notes
    val areAllNotesAlreadySelected = notes.all { it.selected }
    val cScope = rememberCoroutineScope()

    var checkBoxIconSize = remember {
        mutableStateOf( 0.dp)

    }
    var animatable = remember { Animatable(initialValue = 0F) }


    val checkboxColors = rememberUpdatedState(object : CheckboxColors {
        @Composable
        override fun borderColor(enabled: Boolean, state: ToggleableState): State<Color> {
            return animateColorAsState(colors.onPrimary)
        }

        @Composable
        override fun boxColor(enabled: Boolean, state: ToggleableState): State<Color> {
            return animateColorAsState(colors.primary)
        }

        @Composable
        override fun checkmarkColor(state: ToggleableState): State<Color> {
            return animateColorAsState(colors.onPrimary)
        }

    })

    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "All", color = colors.onPrimary)

        Box {
            Image(
                painter = painterResource(id = R.drawable.ic_checked_no_border),
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(checkBoxIconSize.value)
                    .zIndex(2f),
            )
            Checkbox(
                colors = checkboxColors.value,
                enabled = gridAnimationChoreographer != null,
                checked = areAllNotesAlreadySelected,
                onCheckedChange = {
                    if (it) {
                        gridAnimationChoreographer?.animateAllGrids()

                    }
                    val startSize = if(it){0.dp} else { TOOLBAR_CHECK_MARK_ICON_SIZE}
                    val endSize = if(it){TOOLBAR_CHECK_MARK_ICON_SIZE} else { 0.dp}
                    cScope.launch {
                        animatable.animateTo(if (it) 1f else 0f){
                            checkBoxIconSize.value = lerp(start = startSize , stop = endSize, fraction = this.value)

                        }
                    }
                })
        }

    }
}

