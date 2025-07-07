package com.andres.notes.master.data.mapper

import com.andres.notes.master.core.database.table.TextNoteEntity
import com.andres.notes.master.core.model.TextNote

fun TextNoteEntity.toDomain(): TextNote {
    return TextNote(
        id = id,
        title = title,
        content = content,
        creationDate = creationDate,
        modificationDate = modificationDate,
        backgroundColor = displayColorResource,
        isPinned = isPinned,
        isTrashed = isTrashed,
        trashedDate = trashedDate,
        reminderDate = reminderDate,
        reminderHasBeenPosted = reminderHasBeenPosted,
    )
}

fun TextNote.toEntity(): TextNoteEntity {
    return TextNoteEntity(
        id = id,
        title = title,
        content = content,
        creationDate = creationDate,
        modificationDate = modificationDate,
        displayColorResource = backgroundColor,
        isPinned = isPinned,
        isTrashed = isTrashed,
        trashedDate = trashedDate,
        reminderDate = reminderDate,
        reminderHasBeenPosted = reminderHasBeenPosted,
    )
}