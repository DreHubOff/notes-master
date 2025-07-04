package com.andres.notes.master.data.mapper

import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.data.database.table.TextNoteEntity

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

fun com.andres.notes.master.core.model.TextNote.toEntity(): com.andres.notes.master.data.database.table.TextNoteEntity {
    return _root_ide_package_.com.andres.notes.master.data.database.table.TextNoteEntity(
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