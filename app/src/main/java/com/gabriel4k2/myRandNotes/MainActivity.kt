package com.gabriel4k2.myRandNotes

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.gabriel4k2.InstrumentViewModel
import com.gabriel4k2.myRandNotes.domain.model.Instrument
import com.gabriel4k2.myRandNotes.ui.NoteDisplaySection
import com.gabriel4k2.myRandNotes.ui.noteDisplayer.NoteDisplayViewModel
import com.gabriel4k2.myRandNotes.ui.noteRangePicker.NoteRangePickerSection
import com.gabriel4k2.myRandNotes.ui.providers.JNIInterface
import com.gabriel4k2.myRandNotes.ui.providers.LocalThemeProvider
import com.gabriel4k2.myRandNotes.ui.providers.NoteGeneratorSettingsControllerProvider
import com.gabriel4k2.myRandNotes.ui.providers.ShimmerProvider
import com.gabriel4k2.myRandNotes.ui.providers.SoundEngineProvider
import com.gabriel4k2.myRandNotes.ui.providers.ThemeProvider
import com.gabriel4k2.myRandNotes.ui.settings.SettingsSection
import com.gabriel4k2.myRandNotes.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors.newSingleThreadExecutor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val instrumentViewModel: InstrumentViewModel by viewModels()
    private val noteDisplayViewModel: NoteDisplayViewModel by viewModels()

    // The JENV is scoped for each thread, meaning that if you try to share data among
    // different threads a segfault will happen, that is why a single thread executor is used
    private val audioThread = newSingleThreadExecutor()
    private val jniHandle = JNIHandle()

    init {
        System.loadLibrary("myRandNotes")
    }

    private external fun startFluidSynthEngine(sfAbsolutePath: String)
    private external fun startPlayingNotes(
        intervalInMs: Long,
        instrument: Instrument,
        midiNoteRange: IntArray
    )

    private external fun pauseSynth()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDarkVariant)

        val sfFilePath = copyAssetToTmpFile("sfsource.sf2")

        audioThread.execute {
            startFluidSynthEngine(sfFilePath)
        }

        setContent {
            AppTheme {
                ThemeProvider {
                    ShimmerProvider {
                        NoteGeneratorSettingsControllerProvider(settingsStorage = instrumentViewModel.settingsStorage) {
                            SoundEngineProvider(jniHandle = jniHandle) {
                                val dimensions = LocalThemeProvider.current.dimensions

                                Scaffold(
                                    floatingActionButtonPosition = FabPosition.Center,
                                    floatingActionButton =
                                    { NoteRangePickerSection(Modifier) }
                                ) {
                                    val scrollState = rememberScrollState()
                                    ConstraintLayout(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colors.background)
                                            .verticalScroll(scrollState)
                                    ) {
                                        val (noteDisplay, settingsSection, bottomSpacer) = createRefs()

                                        NoteDisplaySection(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .constrainAs(noteDisplay) {
                                                    top.linkTo(
                                                        anchor = parent.top,
                                                        margin = dimensions.noteDisplayRadius + dimensions.noteDisplayTopContainerPadding
                                                    )
                                                },
                                            viewModel = noteDisplayViewModel
                                        )

                                        SettingsSection(
                                            modifier = Modifier
                                                .padding(horizontal = 20.dp)
                                                .constrainAs(settingsSection) {
                                                    centerHorizontallyTo(parent)
                                                    top.linkTo(
                                                        anchor = noteDisplay.bottom,
                                                        margin = dimensions.noteDisplayRadius - 40.dp
                                                    )
                                                }
                                        )

                                        Spacer(
                                            Modifier.height(100.dp).constrainAs(bottomSpacer) {
                                                centerHorizontallyTo(parent)
                                                top.linkTo(
                                                    settingsSection.bottom
                                                )
                                            }
                                        )
                                    }
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

    inner class JNIHandle : JNIInterface {
        override fun startPlayingNotesHandle(
            intervalInMs: Long,
            instrument: Instrument,
            playableNotesMidiNumbers: IntArray
        ) {
            return audioThread.execute {
                startPlayingNotes(
                    intervalInMs,
                    instrument,
                    playableNotesMidiNumbers
                )
            }
        }

        override fun pauseSynthHandle() {
            return audioThread.execute { pauseSynth() }
        }
    }

    // This is called from JNI, thus it is used even if the compiler says otherwise
    @Suppress("unused")
    fun onMidiNoteChanged(midiNoteNumber: Int) {
        noteDisplayViewModel.onNewNote(midiNoteNumber)
    }
}

private fun MainActivity.copyAssetToTmpFile(fileName: String): String {
    assets.open(fileName).use { `is` ->
        val tempFileName = "tmp_$fileName"
        openFileOutput(tempFileName, Context.MODE_PRIVATE).use { fos ->
            var bytesRead: Int
            val buffer = ByteArray(4096)
            while (`is`.read(buffer).also { bytesRead = it } != -1) {
                fos.write(buffer, 0, bytesRead)
            }
        }
        return filesDir.absolutePath + "/" + tempFileName
    }
}
