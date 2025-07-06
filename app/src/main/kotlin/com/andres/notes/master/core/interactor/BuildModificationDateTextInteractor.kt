package com.andres.notes.master.core.interactor

import android.content.Context
import com.andres.notes.master.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

class BuildModificationDateTextInteractor @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val timeFormat by lazy {
        LocalDateTime.Format {
            hour(padding = Padding.NONE)
            char(':')
            minute(padding = Padding.ZERO)
            char(' ')
            amPmMarker(am = "am", pm = "pm")
        }
    }
    private val dateShortFormat by lazy {
        LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            day(padding = Padding.ZERO)
        }
    }
    private val dateLongFormat by lazy {
        LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            day(padding = Padding.ZERO)
            chars(", ")
            year()
        }
    }

    operator fun invoke(modificationDate: Instant): String {
        val currentTime = Clock.System.now()
        val duration = currentTime - modificationDate

        val systemZone: TimeZone = TimeZone.currentSystemDefault()
        val modificationLocalDate = modificationDate.toLocalDateTime(systemZone)
        val currentLocalDate = currentTime.toLocalDateTime(systemZone)

        return when {
            modificationLocalDate.year != currentLocalDate.year -> {
                val time = modificationLocalDate.format(dateLongFormat)
                context.getString(R.string.edited_pattern).format(time).lowercase()
            }

            modificationLocalDate.dayOfYear != currentLocalDate.dayOfYear -> {
                val time = modificationLocalDate.format(dateShortFormat)
                context.getString(R.string.edited_pattern).format(time)
            }

            duration.inWholeMinutes >= 1L -> {
                val time = modificationLocalDate.format(timeFormat)
                context.getString(R.string.edited_pattern).format(time).lowercase()
            }

            else -> context.getString(R.string.edited_just_now)
        }
    }
}