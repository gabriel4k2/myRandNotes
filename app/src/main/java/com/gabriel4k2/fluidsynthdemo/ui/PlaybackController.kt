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
    val playbackStateState = engine.playbackState.collectAsState()
    val playbackState = playbackStateState.value
    val icon = if (playbackState == SoundEnginePlaybackState.PLAYING) {
        R.drawable.ic_stop
    } else {
        R.drawable.ic_play
    }

    val commandToIssue =  if (playbackState == SoundEnginePlaybackState.PLAYING) {
        SoundEnginePlaybackState.PAUSED
    } else {
        SoundEnginePlaybackState.PLAYING
    }

    IconButton(onClick = { engine.changePlaybackState(commandToIssue)}) {
            Icon(painter = painterResource(id = icon), contentDescription = "")

    }

}