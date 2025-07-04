package com.andres.notes.master.ui.screens.trash.mapper

import android.content.Context
import com.andres.notes.master.BuildConfig
import com.andres.notes.master.R
import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.ui.screens.trash.model.TrashListItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

class ApplicationMainDataTypeToTrashListItemMapper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(source: ApplicationMainDataType): TrashListItem {
        return when (source) {
            is Checklist -> {
                TrashListItem.Checklist(
                    id = source.id,
                    title = source.title,
                    items = source.items.map { it.title },
                    tickedItems = source.items.count { it.isChecked },
                    daysLeftMessage = buildDaysLeftMessage(trashedDate = source.trashedDate),
                    customBackground = source.backgroundColor,
                )
            }

            is TextNote -> {
                TrashListItem.TextNote(
                    id = source.id,
                    title = source.title,
                    content = source.content,
                    daysLeftMessage = buildDaysLeftMessage(trashedDate = source.trashedDate),
                    customBackground = source.backgroundColor,
                )
            }
        }
    }

    private fun buildDaysLeftMessage(trashedDate: OffsetDateTime?): String {
        if (trashedDate == null) {
            // Indicates some error in trashing logic
            return context.getString(R.string.trashed_item_day_left_pattern, -1)
        }

        val currentTime = OffsetDateTime.now()
        val trashedDuration = Duration.between(trashedDate, currentTime).toKotlinDuration()
        val maxLifetime = BuildConfig.TRASH_ITEM_MAX_LIFETIME_SECONDS.seconds

        val durationLeft = maxLifetime - trashedDuration
        val daysLeft = durationLeft.inWholeHours.div(24.0).roundToInt().coerceAtLeast(0)

        return context.getString(R.string.trashed_item_day_left_pattern, daysLeft)
    }
}