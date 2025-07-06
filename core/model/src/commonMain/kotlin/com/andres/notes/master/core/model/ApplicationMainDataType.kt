package com.andres.notes.master.core.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed interface ApplicationMainDataType {
    val id: Long
    val title: String
    val isPinned: Boolean
    val creationDate: Instant
    val modificationDate: Instant
    val reminderDate: Instant?
    val backgroundColor: NoteColor?
    val trashedDate: Instant?
    val reminderHasBeenPosted: Boolean

    fun isEmpty(): Boolean

    val isTrashed: Boolean get() = trashedDate != null
}