package com.gabriel4k2.fluidsynthdemo.providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val LocalMoshiInstance = compositionLocalOf<Moshi> { error("No active user found!") }

@Composable
fun MoshiProvider(content: @Composable () -> Unit ) {
    val moshiInstance = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    CompositionLocalProvider(LocalMoshiInstance provides moshiInstance) {
        content()
    }
    
}