package com.andres.notes.master.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class Checklist(
    override val id: Long,
    override val title: String,
    val items: List<ChecklistItem>,
    @Contextual override val creationDate: Instant,
    @Contextual override val modificationDate: Instant,
    override val isPinned: Boolean,
    override val backgroundColor: NoteColor?,
    override val isTrashed: Boolean,
    @Contextual override val trashedDate: Instant?,
    @Contextual override val reminderDate: Instant?,
    override val reminderHasBeenPosted: Boolean,
) : ApplicationMainDataType {

    override fun isEmpty(): Boolean = title.trim().isEmpty() && (items.isEmpty() || items.all { it.title.trim().isEmpty() })

    companion object {
        fun generateEmpty() = Checklist(
            id = 0L,
            title = "",
            items = listOf(ChecklistItem.generateEmpty()),
            creationDate = Clock.System.now(),
            modificationDate = Clock.System.now(),
            isPinned = false,
            backgroundColor = null,
            isTrashed = false,
            trashedDate = null,
            reminderDate = null,
            reminderHasBeenPosted = false,
        )
    }
}