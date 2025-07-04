package com.andres.notes.master.ui.screens.edit.note

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.andres.notes.master.R
import com.andres.notes.master.ui.shared.TextOnlyAlertDialog
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun ConfirmPermanentlyDeleteNoteDialog(
    onDeleteClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    TextOnlyAlertDialog(
        title = stringResource(R.string.dialog_title_delete_forever),
        text = stringResource(R.string.confirm_permanently_delete_note),
        confirmButtonText = stringResource(id = R.string.action_delete),
        dismissButtonText = stringResource(R.string.cancel),
        confirmAction = onDeleteClick,
        dismissAction = onDismiss,
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ConfirmPermanentlyDeleteNoteDialog()
    }
}