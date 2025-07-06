package com.andres.notes.master.ui.screens.edit.core

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Stable
data class ReminderEditorData(
    val isNewReminder: Boolean,

    val dateMillis: Long?,
    val dateString: String,

    val minuteOfHour: Int,
    val hourOfDay: Int,
    val timeString: String,
) {

    fun asZonedDateTime(): LocalDateTime {
        val instant = dateMillis
            ?.let(kotlin.time.Instant::fromEpochMilliseconds)
            ?: Clock.System.now()

        val utcDate = instant.toLocalDateTime(TimeZone.UTC)
        return LocalDateTime(
            year = utcDate.year,
            month = utcDate.month,
            day = utcDate.day,
            hour = hourOfDay,
            minute = minuteOfHour,
            second = 0,
            nanosecond = 0,
        )
    }
}