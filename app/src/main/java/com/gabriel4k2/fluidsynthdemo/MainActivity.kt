package com.gabriel4k2.fluidsynthdemo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.compose.AppTheme
import com.gabriel4k2.ActivityViewModel
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.NoteDisplayer
import com.gabriel4k2.fluidsynthdemo.ui.PlaybackController
import com.gabriel4k2.fluidsynthdemo.ui.SettingsSection
import com.gabriel4k2.fluidsynthdemo.ui.providers.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.*
import java.util.concurrent.Executors.newSingleThreadExecutor
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    var noteName: MutableState<String>? = null
    private val audioThread = newSingleThreadExecutor()
    val jniHandle = JNIHandle()


    init {

        System.loadLibrary("fluidsynthdemo")

    }

    private external fun startFluidSynthEngine(sfAbsolutePath: String)
    private external fun startPlayingNotes(intervalInMs: Long, instrument: Instrument)
    private external fun pauseSynth()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sfFilePath = copyAssetToTmpFile("sfsource.sf2")
        audioThread.execute {
            startFluidSynthEngine(sfFilePath)
        }



        setContent {
            noteName = remember { mutableStateOf("-") }
            AppTheme {
                ThemeProvider {
                    NoteGeneratorSettingsDispatcherProvider(settingsStorage = viewModel.settingsStorage) {
                        SoundEngineProvider(jniInterface = jniHandle) {


                            val dimensions = LocalThemeProvider.current.dimensions

                            viewModel.RetrieveInstrumentList()
                            val uiState by viewModel.uiSate.collectAsState()
                            val instrumentList = uiState.instruments
                            val currentInstrument = uiState.currentInstrument


                            ConstraintLayout(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.background),
                            ) {
                                val (noteDisplayer, settingsSection, FAB) = createRefs()
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .constrainAs(noteDisplayer) {
                                            top.linkTo(
                                                parent.top,
                                                margin = dimensions.noteDisplayerRadius + dimensions.noteDisplayerTopContainerPadding
                                            )
                                        }, horizontalAlignment = CenterHorizontally
                                ) {
                                    NoteDisplayer()
                                    PlaybackController()


                                }

                                Column(
                                    Modifier
                                        .padding(horizontal = 20.dp)
                                        .constrainAs(settingsSection) {
                                            centerHorizontallyTo(parent)

                                            top.linkTo(
                                                noteDisplayer.bottom,
                                                margin = dimensions.noteDisplayerRadius
                                            )
                                        }) {
                                    SettingsSection(
                                        viewModel = viewModel,
                                        instrumentList = instrumentList,
                                        currentInstrument = currentInstrument
                                    )
                                }



                                ExtendedFloatingActionButton(
                                    modifier = Modifier.constrainAs(FAB) {
                                        centerHorizontallyTo(parent)
                                        bottom.linkTo(
                                            parent.bottom,
                                            margin = 20.dp
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_music_note),
                                            contentDescription = ""
                                        )
                                    },
                                    text = { Text("CONFIGURE NOTE RANGE") },
                                    onClick = {},
                                    backgroundColor = MaterialTheme.colors.primary,
                                    shape = RoundedCornerShape(16.dp)
                                )

                            }
                        }
                    }

                }
            }

        }

    }

    override fun onPause() {
        super.onPause()
        pauseSynth()
    }

    // Called from JNI
    fun onMidiNoteChanged(midiNumber: Int) {
//        var _noteName = midiToNoteMap[midiNumber]
        var _noteName = "oi"
        if (_noteName != null) {
            noteName?.value = _noteName
        }

    }

    inner class JNIHandle() : JNIInterface {
        override fun startPlayingNotesHandle(intervalInMs: Long, instrument: Instrument) {
            return audioThread.execute { startPlayingNotes(intervalInMs, instrument) }

        }

        override fun pauseSynthHandle() {
            return audioThread.execute { pauseSynth() }
        }

    }

}


private fun MainActivity.copyAssetToTmpFile(fileName: String): String {
    assets.open(fileName).use { `is` ->
        val tempFileName = "tmp_$fileName"
        openFileOutput(tempFileName, Context.MODE_PRIVATE).use { fos ->
            var bytes_read: Int
            val buffer = ByteArray(4096)
            while (`is`.read(buffer).also { bytes_read = it } != -1) {
                fos.write(buffer, 0, bytes_read)
            }
        }
        return filesDir.absolutePath + "/" + tempFileName
    }
}