package com.andres.notes.master.ui.screens.main.model

sealed class MainSnackbarActionKey {

    data class UndoTrashedChecklist(
        val checklistId: Long,
    ) : MainSnackbarActionKey()

    data class UndoTrashedNote(
        val noteId: Long,
    ) : MainSnackbarActionKey()

    data class UndoTrashedItemList(
        val items: List<MainScreenItem>,
    ) : MainSnackbarActionKey()
}