package com.andres.notes.master.ui.shared.listitem

import com.andres.notes.master.core.model.NoteColor

data class ChecklistCardData(
    val transitionKey: Any,
    val title: String,
    val items: List<String>,
    val tickedItemsCount: Int,
    val isSelected: Boolean,
    val customBackground: NoteColor?,
)