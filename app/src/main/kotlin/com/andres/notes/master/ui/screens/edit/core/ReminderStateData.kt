package com.andres.notes.master.ui.screens.edit.core

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.andres.notes.master.ui.theme.DarkOcean
import com.andres.notes.master.ui.theme.LightOceanMist
import kotlin.time.Instant

@Stable
data class ReminderStateData(
    val sourceDate: Instant,
    val dateString: AnnotatedString,
    val outdated: Boolean,
    val reminderColorDay: Color = LightOceanMist,
    val reminderColorNight: Color = DarkOcean,
)