package com.gabriel4k2.fluidsynthdemo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.gabriel4k2.fluidsynthdemo.ui.NoteUtils
import com.gabriel4k2.fluidsynthdemo.ui.theme.FluidsynthdemoTheme
import kotlinx.coroutines.*
import java.util.concurrent.Executors.newSingleThreadExecutor

class MainActivity : ComponentActivity() {

    lateinit var sfFilePath: String
    var noteName: MutableState<String>? = null
    val midiToNoteMap = NoteUtils.generateMidiNumberToNoteNameMap()
    val audioThread = newSingleThreadExecutor()

    init {

        System.loadLibrary("fluidsynthdemo")
    }

     private external fun startFluidSynthEngine(sfAbsolutePath: String)
     private external fun registerNoteChangeCallback()
     private external fun pauseSynth()

    override fun onResume() {
        super.onResume()

        audioThread.execute(Runnable { startFluidSynthEngine(sfFilePath) })
        audioThread.execute(Runnable { registerNoteChangeCallback()  })
//        audioJob = lifecycleScope.launch(Dispatchers.Default){
//            registerNoteChangeCallback()
//            startFluidSynthEngine(sfFilePath)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ::onMidiNoteChanged.name
        sfFilePath = copyAssetToTmpFile("sfsource.sf2")

        setContent {
            noteName =  remember {   mutableStateOf("-") }
            FluidsynthdemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(noteName!!.value)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pauseSynth()
    }

    fun onMidiNoteChanged(midiNumber : Int) {
        var _noteName = midiToNoteMap[midiNumber]
        if (_noteName != null) {
            noteName?.value= _noteName
        }

    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!", fontSize = 24.sp)
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