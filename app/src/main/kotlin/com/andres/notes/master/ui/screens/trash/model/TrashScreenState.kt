package com.andres.notes.master.ui.screens.trash.model

data class TrashScreenState(
    val listItems: List<TrashListItem>,
    val requestEmptyTrashConfirmation: Boolean,
) {

    companion object {
        val EMPTY = TrashScreenState(listItems = emptyList(), requestEmptyTrashConfirmation = false)
    }
}