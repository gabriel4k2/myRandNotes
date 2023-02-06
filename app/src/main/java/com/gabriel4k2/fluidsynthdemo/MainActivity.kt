package com.gabriel4k2.fluidsynthdemo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gabriel4k2.fluidsynthdemo.ui.theme.FluidsynthdemoTheme

class MainActivity : ComponentActivity() {

    init {

        System.loadLibrary("fluidsynthdemo")
    }

     private external fun startFluidSynthEngine(sfAbsolutePath: String)
     private external fun playMidiNote(midiAbsolutePath: String, midiAbsolutePath2: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sfFilePath = copyAssetToTmpFile("sfsource.sf2")
        val midiFilePath =  copyAssetToTmpFile("C3.mid")
        val midiFilePath2 =  copyAssetToTmpFile("C32.mid")

        startFluidSynthEngine(sfFilePath)
        playMidiNote(midiFilePath, midiFilePath2)



        setContent {
            FluidsynthdemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Androidaaaaaaaaaaaaa")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
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