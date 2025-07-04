package com.andres.notes.master.core.model

import android.os.Parcelable
import java.time.OffsetDateTime

sealed interface ApplicationMainDataType : SortableListItem, Parcelable {
    val id: Long
    val title: String
    override val isPinned: Boolean
    override val creationDate: OffsetDateTime
    val modificationDate: OffsetDateTime
    val reminderDate: OffsetDateTime?
    val backgroundColor: NoteColor?
    val isTrashed: Boolean
    val trashedDate: OffsetDateTime?
    val reminderHasBeenPosted: Boolean

    fun isEmpty(): Boolean
}