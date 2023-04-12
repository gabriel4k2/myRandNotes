package com.gabriel4k2.fluidsynthdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.compose.AppTheme
import com.gabriel4k2.InstrumentViewModel
import com.gabriel4k2.fluidsynthdemo.domain.model.Instrument
import com.gabriel4k2.fluidsynthdemo.ui.NoteDisplaySection
import com.gabriel4k2.fluidsynthdemo.ui.SettingsSection
import com.gabriel4k2.fluidsynthdemo.ui.noteRangePicker.NoteRangePickerSection
import com.gabriel4k2.fluidsynthdemo.ui.providers.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors.newSingleThreadExecutor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val instrumentViewModel: InstrumentViewModel by viewModels()

    var noteName: MutableState<String>? = null

    //    val midiToNoteMap = NoteUtils.generateMidiNumberToNoteNameMap()
    private val audioThread = newSingleThreadExecutor()
    private val jniHandle = JNIHandle()


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
            AppTheme {
                ThemeProvider {
                    ShimmerProvider {
                        NoteGeneratorSettingsControllerProvider(settingsStorage = instrumentViewModel.settingsStorage) {
                            SoundEngineProvider(jniInterface = jniHandle) {
                                val dimensions = LocalThemeProvider.current.dimensions

                                ConstraintLayout(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colors.background),
                                ) {
                                    val (noteDisplayer, settingsSection, FAB) = createRefs()



                                    NoteDisplaySection(modifier = Modifier
                                        .fillMaxWidth()
                                        .constrainAs(noteDisplayer) {
                                            top.linkTo(
                                                anchor = parent.top,
                                                margin = dimensions.noteDisplayerRadius + dimensions.noteDisplayerTopContainerPadding
                                            )
                                        })

                                    SettingsSection(modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .constrainAs(settingsSection) {
                                            centerHorizontallyTo(parent)
                                            top.linkTo(
                                                noteDisplayer.bottom,
                                                margin = dimensions.noteDisplayerRadius
                                            )
                                        })


                                    NoteRangePickerSection(modifier = Modifier.constrainAs(FAB) {
                                        centerHorizontallyTo(parent)
                                        bottom.linkTo(
                                            parent.bottom,
                                            margin = 20.dp
                                        )
                                    })

                                }
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
//    fun onMidiNoteChanged(midiNumber: Int) {
//        var _noteName = midiToNoteMap[midiNumber]
//        if (_noteName != null) {
//            noteName?.value = _noteName.toString()
//        }
//
//    }

    inner class JNIHandle() : JNIInterface {
        override fun startPlayingNotesHandle(intervalInMs: Long, instrument: Instrument) {
            return audioThread.execute { startPlayingNotes(intervalInMs, instrument) }

        }

        override fun pauseSynthHandle() {
            return audioThread.execute { pauseSynth() }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("MainActivity", "Intent ${intent.toString()}")
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