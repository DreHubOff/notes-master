package com.andres.notes.master.ui.shared.listitem

import com.andres.notes.master.core.model.NoteColor

data class TextNoteCardData(
    val transitionKey: Any,
    val title: String,
    val content: String,
    val isSelected: Boolean,
    val customBackground: NoteColor?,
)