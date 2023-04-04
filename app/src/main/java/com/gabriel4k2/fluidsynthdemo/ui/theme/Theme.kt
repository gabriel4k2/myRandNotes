package com.example.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.gabriel4k2.fluidsynthdemo.ui.theme.Typography


private val LightColors = lightColors(
    primary = Color(0.121f,0.28f,0.72f,1f ),
    primaryVariant = Color(0.84f,0.89f,0.98f,1f ),
    secondary = Color(0.72549f,0.55f,0.12f,1f ),

)




@Composable
fun AppTheme(
  content: @Composable() () -> Unit
) {
  val colors = LightColors

  MaterialTheme(
    colors = colors,
    content = content,
    typography = Typography

  )
}