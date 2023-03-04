package com.gabriel4k2.fluidsynthdemo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils
import com.gabriel4k2.fluidsynthdemo.ui.theme.FluidsynthdemoTheme
import com.gabriel4k2.fluidsynthdemo.data.Instrument
import com.gabriel4k2.fluidsynthdemo.providers.LocalMoshiInstance
import com.gabriel4k2.fluidsynthdemo.providers.LocalThemeProvider
import com.gabriel4k2.fluidsynthdemo.providers.MoshiProvider
import com.gabriel4k2.fluidsynthdemo.providers.ThemeProvider
import com.gabriel4k2.fluidsynthdemo.ui.NoteDisplayer
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import java.util.concurrent.Executors.newSingleThreadExecutor


class MainActivity : ComponentActivity() {

    var noteName: MutableState<String>? = null
    private val midiToNoteMap = NoteUtils.generateMidiNumberToNoteNameMap()
    private val intervalInMs = mutableStateOf(1000L)
    private val audioThread = newSingleThreadExecutor()
    private var instrumentList: List<Instrument> = emptyList()

    init {

        System.loadLibrary("fluidsynthdemo")
    }

    private external fun startFluidSynthEngine(sfAbsolutePath: String)
    private external fun startPlayingNotes(intervalInMs: Long, instrument: Instrument)
    private external fun pauseSynth()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sfFilePath = copyAssetToTmpFile("sfsource.sf2")
        audioThread.execute(Runnable {
            startFluidSynthEngine(sfFilePath)
        })

        val instrumentsJson =
            resources.openRawResource(R.raw.instruments).bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

        setContent {
            noteName = remember { mutableStateOf("-") }
            FluidsynthdemoTheme {
                MoshiProvider {
                    ThemeProvider {
                        val moshiInstance = LocalMoshiInstance.current
                        val dimensions = LocalThemeProvider.current.dimensions
                        val noteDisplayerContainerPadding = dimensions.noteDisplayerHorizontalContainerPadding



                        LaunchedEffect(key1 = true) {
                            val listType =
                                Types.newParameterizedType(List::class.java, Instrument::class.java)
                            val instrumentListAdapter: JsonAdapter<List<Instrument>> =
                                moshiInstance.adapter(listType)
                            instrumentList =
                                instrumentListAdapter.fromJson(instrumentsJson) ?: emptyList()
                            print(instrumentList)

                        }

                        ConstraintLayout(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            val (noteDisplayer) = createRefs()
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

                            }

                        }
                    }
                }

                // A surface container using the 'background' color from the theme

            }
        }
    }

    override fun onPause() {
        super.onPause()
        pauseSynth()
    }

    fun onMidiNoteChanged(midiNumber: Int) {
        var _noteName = midiToNoteMap[midiNumber]
        if (_noteName != null) {
            noteName?.value = _noteName
        }

    }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!", fontSize = 24.sp)
}

@Composable
fun InstrumentList(instruments: List<Instrument>) {
    LazyRow {
        items(items = instruments) {
            Text(
                text = "Instrument name ${it.name} type ${it.type} bank ${it.bankOffset}",
                fontSize = 24.sp
            )

        }


    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FluidsynthdemoTheme {
        Greeting("Android")
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