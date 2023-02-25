package com.gabriel4k2.fluidsynthdemo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.gabriel4k2.fluidsynthdemo.utils.NoteUtils
import com.gabriel4k2.fluidsynthdemo.ui.theme.FluidsynthdemoTheme
import com.gabriel4k2.fluidsynthdemo.data.Instrument
import com.gabriel4k2.fluidsynthdemo.providers.LocalMoshiInstance
import com.gabriel4k2.fluidsynthdemo.providers.MoshiProvider
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import kotlinx.coroutines.*
import java.lang.reflect.Type
import java.util.concurrent.Executors.newSingleThreadExecutor



class MainActivity : ComponentActivity() {

    var noteName: MutableState<String>? = null
    private val midiToNoteMap = NoteUtils.generateMidiNumberToNoteNameMap()
    private val intervalInMs = 1000
    private val audioThread = newSingleThreadExecutor()
    private var instrumentList: List<Instrument> = emptyList()

    init {

        System.loadLibrary("fluidsynthdemo")
    }

    private external fun startFluidSynthEngine(sfAbsolutePath: String)
    private external fun startPlayingNotes(intervalInMs: Long, instrument: Instrument)
    private external fun pauseSynth()


    override fun onResume() {
        super.onResume()


        audioThread.execute(Runnable {
            startPlayingNotes(
                intervalInMs = 1000,
                instrument = Instrument.mock()
            )
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sfFilePath = copyAssetToTmpFile("sfsource.sf2")
        audioThread.execute(Runnable {
            startFluidSynthEngine(sfFilePath)
        })

        val instrumentsJson = resources.openRawResource(R.raw.instruments).bufferedReader(Charsets.UTF_8).use { it.readText() }

        setContent {
            noteName = remember { mutableStateOf("-") }
            FluidsynthdemoTheme {
                MoshiProvider{
                    val moshiInstance = LocalMoshiInstance.current

                    LaunchedEffect(key1 = true){
                        val listType = Types.newParameterizedType(List::class.java, Instrument::class.java)
                        val instrumentListAdapter: JsonAdapter<List<Instrument>> = moshiInstance.adapter(listType)
                        instrumentList = instrumentListAdapter.fromJson(instrumentsJson) ?: emptyList()

                    }
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Greeting(noteName!!.value)
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
fun InstrumentList(instruments : List<Instrument>) {
    LazyRow{
        items(items= instruments){
            Text(text = "Instrument name ${it.name} type ${it.type} bank ${it.bankOffset}", fontSize = 24.sp)

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