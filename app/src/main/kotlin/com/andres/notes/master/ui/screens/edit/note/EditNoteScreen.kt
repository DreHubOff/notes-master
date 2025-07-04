@file:OptIn(ExperimentalMaterial3Api::class)

package com.andres.notes.master.ui.screens.edit.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andres.notes.master.LocalThemeMode
import com.andres.notes.master.R
import com.andres.notes.master.ThemeMode
import com.andres.notes.master.demo_data.MainScreenDemoData
import com.andres.notes.master.ui.screens.edit.EditActionBar
import com.andres.notes.master.ui.screens.edit.ModificationDateOverlay
import com.andres.notes.master.ui.screens.edit.ShareTypeSelectionDialog
import com.andres.notes.master.ui.screens.edit.core.EditScreenViewModel
import com.andres.notes.master.ui.screens.edit.core.ReminderStateData
import com.andres.notes.master.ui.screens.edit.note.model.EditNoteScreenState
import com.andres.notes.master.ui.screens.edit.reminder.RemainderDatePickerDialog
import com.andres.notes.master.ui.screens.edit.reminder.RemainderEditorOverviewDialog
import com.andres.notes.master.ui.screens.edit.reminder.RemainderTimePickerDialog
import com.andres.notes.master.ui.screens.permissions.ExactAlarmsPermissionDialog
import com.andres.notes.master.ui.screens.permissions.PostNotificationsPermissionDialog
import com.andres.notes.master.ui.shared.ColorSelectorDialog
import com.andres.notes.master.ui.shared.HandleSnackbarState
import com.andres.notes.master.ui.shared.SnackbarEvent
import com.andres.notes.master.ui.shared.mainItemCardTransition
import com.andres.notes.master.ui.shared.rememberTextNoteToEditorTitleTransitionKey
import com.andres.notes.master.ui.shared.rememberTextNoteToEditorTransitionKey
import com.andres.notes.master.ui.theme.ApplicationTheme
import java.time.OffsetDateTime

@Composable
fun EditNoteScreen() {
    val viewModel: EditNoteViewModel = hiltViewModel()

    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    val backAction = {
        focusManager.clearFocus(force = true)
        keyboardManager?.hide()
        viewModel.onBackClicked()
    }

    BackHandler {
        backAction()
    }

    val state by viewModel.state.collectAsStateWithLifecycle(EditNoteScreenState.Companion.EMPTY)

    val background = key(state.background, LocalThemeMode.current) {
        val isDarkTheme = LocalThemeMode.current == ThemeMode.DARK
        (if (isDarkTheme) state.background?.night else state.background?.day)?.let(::Color)
    }
    val overriddenColorTheme = key(state.background) {
        MaterialTheme.colorScheme.copy(background = background ?: MaterialTheme.colorScheme.background)
    }
    MaterialTheme(
        colorScheme = overriddenColorTheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
    ) {
        ScreenContent(
            state = state,
            onTitleChanged = viewModel::onTitleChanged,
            onContentChanged = viewModel::onContentChanged,
            onBackClick = { backAction() },
            onPinCheckedChange = viewModel::onPinCheckedChange,
            onTitleNextClick = viewModel::onTitleNextClick,
            onMoveToTrashClick = viewModel::moveToTrash,
            onPermanentlyDeleteClick = viewModel::askConfirmationToPermanentlyDeleteItem,
            onRestoreClick = viewModel::restoreItemFromTrash,
            onAttemptEditTrashed = viewModel::onAttemptEditTrashed,
            onShareClick = viewModel::onShareCurrentItemClick,
            onSnackbarAction = viewModel::handleSnackbarAction,
            onAddReminderClick = viewModel::onAddReminderClick,
            onSelectBackgroundClick = viewModel::showBackgroundSelection,
        )

        HandleAlerts(state, viewModel)
    }
}

@Composable
fun ScreenContent(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPinCheckedChange: (Boolean) -> Unit = {},
    onTitleNextClick: () -> Unit = {},
    onMoveToTrashClick: () -> Unit = {},
    onPermanentlyDeleteClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {},
    onAttemptEditTrashed: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSnackbarAction: (SnackbarEvent.Action) -> Unit = {},
    onAddReminderClick: () -> Unit = {},
    onSelectBackgroundClick: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    HandleSnackbarState(
        snackbarHostState = snackbarHostState,
        snackbarEvent = state.snackbarEvent,
        onActionExecuted = onSnackbarAction,
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .mainItemCardTransition(rememberTextNoteToEditorTransitionKey(state.itemId)),
        contentWindowInsets = WindowInsets.systemBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            EditActionBar(
                systemBarInset = innerPadding.calculateTopPadding(),
                pinned = state.isPinned,
                trashed = state.isTrashed,
                onBackClick = onBackClick,
                onPinCheckedChange = onPinCheckedChange,
                onMoveToTrashClick = onMoveToTrashClick,
                onPermanentlyDeleteClick = onPermanentlyDeleteClick,
                onRestoreClick = onRestoreClick,
                onShareClick = onShareClick,
                onAddReminderClick = onAddReminderClick,
                onSelectBackgroundClick = onSelectBackgroundClick,
            )
            Box(modifier = Modifier.fillMaxSize()) {
                if (state !== EditNoteScreenState.Companion.EMPTY) {
                    Editor(
                        state,
                        onTitleChanged,
                        onContentChanged,
                        onTitleNextClick,
                        onAddReminderClick,
                        innerPadding
                    )
                }
                if (state.isTrashed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.2f))
                            .clickable(onClick = onAttemptEditTrashed)
                    )
                }
            }
        }
    }
}

@Composable
private fun Editor(
    state: EditNoteScreenState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onTitleNextClick: () -> Unit,
    onEditReminderClick: () -> Unit,
    innerPadding: PaddingValues,
) {
    Box {
        NoteBody(
            modifier = Modifier,
            title = state.title,
            titleTransitionKey = rememberTextNoteToEditorTitleTransitionKey(state.itemId),
            reminderData = state.reminderData,
            content = state.content,
            contentFocusRequest = state.contentFocusRequest,
            onTitleChanged = onTitleChanged,
            onContentChanged = onContentChanged,
            onTitleNextClick = onTitleNextClick,
            onEditReminderClick = onEditReminderClick,
        )
        ModificationDateOverlay(
            navigationBarPadding = innerPadding.calculateBottomPadding(),
            message = state.modificationStatusMessage,
        )
    }
}

@Composable
private fun HandleAlerts(
    state: EditNoteScreenState,
    viewModel: EditNoteViewModel,
) {
    if (state.showPermanentlyDeleteConfirmation) {
        ConfirmPermanentlyDeleteNoteDialog(
            onDeleteClick = { viewModel.permanentlyDeleteItemWhenConfirmed() },
            onDismiss = { viewModel.dismissPermanentlyDeleteConfirmation() }
        )
    }

    if (state.requestItemShareType) {
        ShareTypeSelectionDialog(
            title = stringResource(R.string.share_note_title),
            onDismiss = viewModel::cancelItemShareTypeRequest,
            onTypeSelected = viewModel::shareItemAs,
        )
    }

    if (state.showReminderEditorOverview && state.reminderEditorData != null) {
        RemainderEditorOverviewDialog(
            data = state.reminderEditorData,
            onDismiss = viewModel::hideReminderOverview,
            onSave = viewModel::saveReminder,
            onDelete = viewModel::deleteReminder,
            onEditDate = viewModel::editReminderDate,
            onEditTime = viewModel::editReminderTime,
        )
    }

    if (state.showReminderDatePicker && state.reminderEditorData != null) {
        RemainderDatePickerDialog(
            data = state.reminderEditorData,
            onDismiss = viewModel::hideReminderDatePicker,
            onDateSelected = viewModel::saveReminderDatePickerResult,
        )
    }

    if (state.showReminderTimePicker && state.reminderEditorData != null) {
        RemainderTimePickerDialog(
            data = state.reminderEditorData,
            onDismiss = viewModel::hideReminderTimePicker,
            onTimeSelected = viewModel::saveReminderTimePickerResult,
        )
    }

    if (state.showPostNotificationsPermissionPrompt) {
        PostNotificationsPermissionDialog(
            onDismiss = viewModel::hideReminderPermissionsPrompt,
            onGranted = { viewModel.checkReminderPermissions() },
            onOpenAppSettings = {
                viewModel.hideReminderPermissionsPrompt()
                viewModel.openAppSettings()
            }
        )
    }

    if (state.showSetAlarmsPermissionPrompt) {
        ExactAlarmsPermissionDialog(
            onDismiss = viewModel::hideReminderPermissionsPrompt,
            onOpenAlarmsSettings = {
                viewModel.hideReminderPermissionsPrompt()
                viewModel.openAlarmsSettings()
            }
        )
    }

    if (state.showBackgroundSelector) {
        ColorSelectorDialog(
            onDismiss = viewModel::hideBackgroundSelection,
            onColorSelected = viewModel::saveBackgroundColor,
            title = stringResource(R.string.note_color),
            colors = state.backgroundColorList,
            selectedColor = state.background,
        )
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(EditNoteScreenStateProvider::class) state: EditNoteScreenState) {
    ApplicationTheme {
        ScreenContent(state = state)
    }
}

private class EditNoteScreenStateProvider :
    PreviewParameterProvider<EditNoteScreenState> {
    override val values: Sequence<EditNoteScreenState>
        get() = sequenceOf(
            EditNoteScreenState.Companion.EMPTY.copy(
                itemId = 0,
                title = MainScreenDemoData.TextNotes.welcomeBanner.title,
                content = MainScreenDemoData.TextNotes.welcomeBanner.content,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.Companion.EMPTY.copy(
                itemId = 1,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.Companion.EMPTY.copy(
                itemId = 2,
                isPinned = true,
                modificationStatusMessage = "Edited 09:48 am",
            ),
            EditNoteScreenState.Companion.EMPTY.copy(
                itemId = 3,
                isPinned = true,
                reminderData = ReminderStateData(
                    sourceDate = OffsetDateTime.now(),
                    dateString = AnnotatedString(text = "21 May, 10:12 AM"),
                    outdated = false,
                    reminderColorDay = Color(0x14017FFA),
                )
            ),
        )
}