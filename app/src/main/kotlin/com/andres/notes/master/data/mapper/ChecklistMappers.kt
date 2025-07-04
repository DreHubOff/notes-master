package com.andres.notes.master.data.mapper

import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.ChecklistItem
import com.andres.notes.master.data.database.table.ChecklistEntity
import com.andres.notes.master.data.database.table.ChecklistItemEntity
import com.andres.notes.master.data.database.table.ChecklistWithItems

fun ChecklistWithItems.toDomain(): Checklist {
    return Checklist(
        id = checklist.id,
        title = checklist.title,
        items = items.sortedBy { it.listPosition }.map { it.toDomain() },
        creationDate = checklist.creationDate,
        modificationDate = checklist.modificationDate,
        isPinned = checklist.isPinned,
        backgroundColor = checklist.backgroundColor,
        isTrashed = checklist.isTrashed,
        trashedDate = checklist.trashedDate,
        reminderDate = checklist.reminderDate,
        reminderHasBeenPosted = checklist.reminderHasBeenPosted,
    )
}

fun ChecklistItemEntity.toDomain(): ChecklistItem {
    return ChecklistItem(
        id = id,
        title = title,
        isChecked = isChecked,
        listPosition = listPosition,
    )
}

fun Checklist.toEntity(): ChecklistEntity {
    return ChecklistEntity(
        id = id,
        title = title,
        creationDate = creationDate,
        modificationDate = modificationDate,
        isPinned = isPinned,
        backgroundColor = backgroundColor,
        isTrashed = isTrashed,
        trashedDate = trashedDate,
        reminderDate = reminderDate,
        reminderHasBeenPosted = reminderHasBeenPosted,
    )
}

fun ChecklistItem.toEntity(parentChecklistId: Long): ChecklistItemEntity {
    return ChecklistItemEntity(
        id = id,
        title = title,
        isChecked = isChecked,
        listPosition = listPosition,
        checklistId = parentChecklistId
    )
}