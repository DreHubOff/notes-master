package com.andres.notes.master.ui.screens.edit.checklist.model

import com.andres.notes.master.ui.focus.ElementFocusRequest

data class UncheckedListItemUi(
    val id: Long,
    val text: String,
    val focusRequest: ElementFocusRequest?,
)