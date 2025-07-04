package com.andres.notes.master

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

enum class ThemeMode {
    DARK, LIGHT,
}

val LocalThemeMode: ProvidableCompositionLocal<ThemeMode> = compositionLocalOf { ThemeMode.LIGHT }