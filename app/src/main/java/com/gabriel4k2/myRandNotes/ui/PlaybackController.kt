package com.gabriel4k2.myRandNotes.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.gabriel4k2.myRandNotes.R
import com.gabriel4k2.myRandNotes.data.SoundEnginePlaybackState
import com.gabriel4k2.myRandNotes.ui.providers.LocalSoundEngineProvider

@Composable
fun PlaybackController(modifier: Modifier =  Modifier) {
    val engine = LocalSoundEngineProvider.current
    val engineState = engine.engineState.collectAsState()
    val playbackState = engineState.value.playbackState
    val icon = if (playbackState == SoundEnginePlaybackState.PLAYING) {
        R.drawable.ic_stop
    } else {
        R.drawable.ic_play
    }

    val commandToIssue = when (playbackState) {
        SoundEnginePlaybackState.PLAYING -> SoundEnginePlaybackState.PAUSED
        SoundEnginePlaybackState.PAUSED -> SoundEnginePlaybackState.PLAYING
        else -> null
    }

    IconButton(modifier = modifier, enabled = playbackState !=  SoundEnginePlaybackState.LOADING, onClick = {
        if (commandToIssue != null) {
            engine.changePlaybackState(commandToIssue)
        }
    }) {
        Icon(painter = painterResource(id = icon), contentDescription = "")
    }
}
