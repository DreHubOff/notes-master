package com.andres.notes.master.ui.screens.edit.core

import androidx.compose.runtime.Stable
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.ui.shared.SnackbarEvent

@Stable
interface EditScreenState<Self : EditScreenState<Self>> {
    val itemId: Long
    val title: String
    val isPinned: Boolean
    val reminderData: ReminderStateData?
    val reminderEditorData: ReminderEditorData?
    val isTrashed: Boolean
    val requestItemShareType: Boolean
    val modificationStatusMessage: String

    val snackbarEvent: SnackbarEvent?

    val showPermanentlyDeleteConfirmation: Boolean
    val showReminderEditorOverview: Boolean
    val showReminderDatePicker: Boolean
    val showReminderTimePicker: Boolean

    val showPostNotificationsPermissionPrompt: Boolean
    val showSetAlarmsPermissionPrompt: Boolean

    val background: NoteColor?
    val backgroundColorList: List<NoteColor?>
    val showBackgroundSelector: Boolean

    fun copy(
        itemId: Long = this.itemId,
        title: String = this.title,
        isPinned: Boolean = this.isPinned,
        reminderData: ReminderStateData? = this.reminderData,
        reminderEditorData: ReminderEditorData? = this.reminderEditorData,
        isTrashed: Boolean = this.isTrashed,
        requestItemShareType: Boolean = this.requestItemShareType,
        modificationStatusMessage: String = this.modificationStatusMessage,
        showPermanentlyDeleteConfirmation: Boolean = this.showPermanentlyDeleteConfirmation,
        snackbarEvent: SnackbarEvent? = this.snackbarEvent,
        showReminderEditorOverview: Boolean = this.showReminderEditorOverview,
        showReminderDatePicker: Boolean = this.showReminderDatePicker,
        showReminderTimePicker: Boolean = this.showReminderTimePicker,
        showPostNotificationsPermissionPrompt: Boolean = this.showPostNotificationsPermissionPrompt,
        showSetAlarmsPermissionPrompt: Boolean = this.showSetAlarmsPermissionPrompt,
        background: NoteColor? = this.background,
        showBackgroundSelector: Boolean = this.showBackgroundSelector,
        backgroundColorList: List<NoteColor?> = this.backgroundColorList,
    ): Self
}