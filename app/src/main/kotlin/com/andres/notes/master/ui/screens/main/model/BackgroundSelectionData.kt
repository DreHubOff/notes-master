package com.andres.notes.master.ui.screens.main.model

import com.andres.notes.master.core.model.NoteColor

data class BackgroundSelectionData(
    val colors: List<NoteColor?>,
    val selectedColor: NoteColor?,
)