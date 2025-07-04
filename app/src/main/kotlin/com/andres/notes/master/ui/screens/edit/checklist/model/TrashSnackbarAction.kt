package com.andres.notes.master.ui.screens.edit.checklist.model

sealed class TrashSnackbarAction {

    data object Restore : TrashSnackbarAction()
    data object UndoNoteRestoration : TrashSnackbarAction()
}