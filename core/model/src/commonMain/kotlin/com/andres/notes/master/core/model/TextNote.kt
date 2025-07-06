package com.andres.notes.master.core.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class TextNote(
    override val id: Long,
    override val title: String,
    val content: String,
    @Contextual override val creationDate: Instant,
    @Contextual override val modificationDate: Instant,
    override val backgroundColor: NoteColor?,
    override val isPinned: Boolean,
    override val isTrashed: Boolean,
    @Contextual override val trashedDate: Instant?,
    @Contextual override val reminderDate: Instant?,
    override val reminderHasBeenPosted: Boolean,
) : ApplicationMainDataType {

    override fun isEmpty(): Boolean = title.trim().isEmpty() && content.trim().isEmpty()

    companion object {
        fun generateEmpty() = TextNote(
            id = 0,
            title = "",
            content = "",
            creationDate = Clock.System.now(),
            modificationDate = Clock.System.now(),
            backgroundColor = null,
            isPinned = false,
            isTrashed = false,
            trashedDate = null,
            reminderDate = null,
            reminderHasBeenPosted = false,
        )
    }
}