package com.gabriel4k2.fluidsynthdemo.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import com.gabriel4k2.fluidsynthdemo.R
import com.gabriel4k2.fluidsynthdemo.data.SoundEnginePlaybackState
import com.gabriel4k2.fluidsynthdemo.ui.providers.LocalSoundEngineProvider


@Composable
fun PlaybackController() {
    val engine = LocalSoundEngineProvider.current
    val engineState = engine.engineState.collectAsState()
    val playbackState = engineState.value?.playbackState
    val icon = if (playbackState == SoundEnginePlaybackState.PLAYING) {
        R.drawable.ic_stop
    } else {
        R.drawable.ic_play
    }

    val commandToIssue =  if (playbackState == SoundEnginePlaybackState.PLAYING) {
        SoundEnginePlaybackState.PAUSED
    } else if( playbackState == SoundEnginePlaybackState.PAUSED){
        SoundEnginePlaybackState.PLAYING
    } else {
        null
    }

    IconButton(enabled = playbackState != null, onClick = {
        if (commandToIssue != null) {
            engine.changePlaybackState(commandToIssue)
        }
    }) {
            Icon(painter = painterResource(id = icon), contentDescription = "")

    }

}