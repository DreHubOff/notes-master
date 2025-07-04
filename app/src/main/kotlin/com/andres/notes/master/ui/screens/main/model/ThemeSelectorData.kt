package com.andres.notes.master.ui.screens.main.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.andres.notes.master.core.model.ThemeType

@Stable
data class ThemeSelectorData(
    val options: List<ThemeOption>,
)

data class ThemeOption(
    @StringRes val nameRes: Int,
    val type: ThemeType,
    val isSelected: Boolean,
)
