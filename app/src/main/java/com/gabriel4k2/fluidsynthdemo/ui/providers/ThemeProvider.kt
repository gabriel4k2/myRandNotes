package com.gabriel4k2.fluidsynthdemo.ui.providers

import android.content.res.Configuration
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val LocalThemeProvider = compositionLocalOf<Theme> { error("No active user found!") }

class Colors{
    // Color 1e90ff
    val primary= Color(0.11f,0.56f,1.0f,1f )
    // #1f69d9
    val primaryVariant= Color(0.12f,0.41f,0.85f,1f )
    // 6bb6ff
    val primaryLight= Color(0.72f,0.86f,1.0f,1f )
    val onPrimary= Color(0f,0.0f,0.1f,1f )
    val secondary= Color(0.07f, 0.14f,0.8f,1f )

    //teste 275fa3
    val teste= Color(0.15f, 0.37f,0.64f,1f )

}
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )

)

class Dimensions(configuration : Configuration){
    val noteDisplayerHorizontalContainerPadding  = 70.dp
    val noteDisplayerTopContainerPadding  = noteDisplayerHorizontalContainerPadding/2
    val noteDisplayerRadius = ((configuration.screenWidthDp.dp - noteDisplayerHorizontalContainerPadding.times(2))/2)
}


class Theme( configuration: Configuration) {
    val colors = Colors()
    val dimensions = Dimensions(configuration)
}

@Composable
fun ThemeProvider(content: @Composable () -> Unit ) {
    val configuration = LocalConfiguration.current
    CompositionLocalProvider(LocalThemeProvider provides Theme(configuration)) {
        content()
    }

}