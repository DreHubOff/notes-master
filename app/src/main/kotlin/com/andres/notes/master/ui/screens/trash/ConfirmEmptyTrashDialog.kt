package com.andres.notes.master.ui.screens.trash

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.andres.notes.master.R
import com.andres.notes.master.ui.shared.TextOnlyAlertDialog
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun ConfirmEmptyTrashDialog(
    onEmptyTrashConfirmed: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    TextOnlyAlertDialog(
        title = stringResource(R.string.dialog_title_empty_trash),
        text = stringResource(R.string.confirm_empty_trash_message),
        confirmButtonText = stringResource(id = R.string.confirm_empty_trash),
        dismissButtonText = stringResource(R.string.cancel),
        confirmAction = onEmptyTrashConfirmed,
        dismissAction = onDismiss,
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        ConfirmEmptyTrashDialog()
    }
}