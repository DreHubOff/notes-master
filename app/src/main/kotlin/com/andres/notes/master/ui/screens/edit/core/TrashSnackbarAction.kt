package com.andres.notes.master.ui.screens.edit.core

sealed class TrashSnackbarAction {

    data object Restore : TrashSnackbarAction()
    data object UndoNoteRestoration : TrashSnackbarAction()
}