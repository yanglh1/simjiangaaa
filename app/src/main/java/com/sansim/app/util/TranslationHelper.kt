package com.sansim.app.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.sansim.app.i18n.tr

val LocalAppLanguage = compositionLocalOf { "简体中文" }
@Composable fun LT(key:String):String = tr(LocalAppLanguage.current,key)
@Composable fun L(key:String):String = tr(LocalAppLanguage.current,key)
