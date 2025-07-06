@file:OptIn(ExperimentalMaterial3Api::class)

package com.andres.notes.master.ui.screens.edit.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andres.notes.master.R
import com.andres.notes.master.RescheduleRemindersCommandReceiver
import com.andres.notes.master.core.MainTypeEditorFacade
import com.andres.notes.master.core.interactor.BuildModificationDateTextInteractor
import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.core.model.MainTypeTextRepresentation
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.data.PermissionsRepository
import com.andres.notes.master.di.qualifier.ApplicationGlobalScope
import com.andres.notes.master.ui.intent.ShareFileIntentBuilder
import com.andres.notes.master.ui.intent.ShareTextIntentBuilder
import com.andres.notes.master.ui.navigation.NavigationEventsHost
import com.andres.notes.master.ui.screens.edit.ShareContentType
import com.andres.notes.master.ui.shared.SnackbarEvent
import com.andres.notes.master.ui.shared.defaultTransitionAnimationDuration
import com.andres.notes.master.ui.theme.DarkOcean
import com.andres.notes.master.ui.theme.LightOceanMist
import com.andres.notes.master.util.asStrikethroughText
import com.andres.notes.master.util.lighten
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Provider
import kotlin.time.Clock
import kotlin.time.Instant

abstract class EditScreenViewModel<State : EditScreenState<State>, Item : ApplicationMainDataType>(
    private val navigationEventsHost: NavigationEventsHost,
    private val editorFacade: MainTypeEditorFacade,
    @param:ApplicationGlobalScope private val applicationCoroutineScope: CoroutineScope,
    @param:ApplicationContext private val context: Context,
    private val buildModificationDateText: Lazy<BuildModificationDateTextInteractor>,
    private val shareTextIntentBuilder: Provider<ShareTextIntentBuilder>,
    private val shareFileIntentBuilder: Provider<ShareFileIntentBuilder>,
    private val permissionsRepository: Provider<PermissionsRepository>,
) : ViewModel() {

    private val reminderEditorDateFormat by lazy {
        LocalDateTime.Format {
            day(padding = Padding.NONE)
            char(' ')
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            chars(", ")
            year()
        }
    }
    private val reminderEditorTimeFormat by lazy {
        LocalDateTime.Format {
            hour(padding = Padding.NONE);
            char(':')
            minute()
            char(' ')
            amPmMarker("am", "pm")
        }
    }

    private val reminderDateTimeFormat by lazy {
        LocalDateTime.Format {
            day(padding = Padding.NONE)
            char(' ')
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            chars(", ")
            hour(padding = Padding.NONE)
            char(':')
            minute(padding = Padding.NONE)
            char(' ')
            amPmMarker(am = "am", pm = "pm")
        }
    }

    private var firstItemCache: Item? = null

    @Suppress("PropertyName")
    protected val _state: MutableStateFlow<State> by lazy { MutableStateFlow(getEmptyState()) }
    val state: StateFlow<State> by lazy {
        _state
            .onStart {
                val idFromArgs = getCurrentIdFromNavigationArgs()
                if (firstItemCache == null || (idFromArgs != -1L && idFromArgs != firstItemCache?.id)) {
                    firstItemCache = loadFirstItem(getCurrentIdFromNavigationArgs())
                }
                observeEditedItemChanges(firstItemCache?.id ?: -1)
            }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, getEmptyState())
    }

    private var pinChangesJob: Job? = null
    private var titleUpdatesJob: Job? = null

    @get:StringRes
    protected abstract val itemRestoredMessageRes: Int

    protected abstract suspend fun loadFirstItem(itemIdFromNavArgs: Long): Item

    protected abstract fun getCurrentIdFromNavigationArgs(): Long

    protected abstract fun fillWithScreenSpecificData(oldState: State, newState: State, updatedItem: Item): State

    protected abstract fun itemUpdatesFlow(itemId: Long): Flow<Item>

    protected abstract fun getEmptyState(): State

    protected abstract fun navigateBack(itemId: Long, isTrashed: Boolean)

    protected abstract suspend fun getTextRepresentation(state: State): MainTypeTextRepresentation?
    protected abstract suspend fun getPdfRepresentation(state: State): File?

    fun onPinCheckedChange(pinned: Boolean) {
        val currentState = _state.updateAndGet { it.copy(isPinned = pinned) }
        pinChangesJob?.cancel()
        pinChangesJob = applicationCoroutineScope.launch {
            editorFacade.storePinnedSate(itemId = currentState.itemId, pinned = pinned)
        }
    }

    fun onTitleChanged(title: String) {
        titleUpdatesJob?.cancel()
        titleUpdatesJob = applicationCoroutineScope.launch {
            delay(600)
            val currentState = _state.updateAndGet { it.copy(title = title) }
            editorFacade.storeNewTitle(itemId = currentState.itemId, title = title)
        }
    }

    fun moveToTrash() {
        val currentState = _state.value
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            editorFacade.moveToTrash(currentState.itemId)
        }
        viewModelScope.launch(Dispatchers.Default) {
            navigateBack(itemId = currentState.itemId, isTrashed = true)
        }
    }

    fun askConfirmationToPermanentlyDeleteItem() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = true) }
    }

    fun dismissPermanentlyDeleteConfirmation() {
        _state.update { it.copy(showPermanentlyDeleteConfirmation = false) }
    }

    fun permanentlyDeleteItemWhenConfirmed() {
        val currentState = _state.updateAndGet { it.copy(showPermanentlyDeleteConfirmation = false) }
        applicationCoroutineScope.launch {
            delay(defaultTransitionAnimationDuration.toLong())
            editorFacade.permanentlyDelete(itemId = _state.value.itemId)
        }
        viewModelScope.launch { navigateBack(itemId = currentState.itemId, isTrashed = false) }
    }

    fun restoreItemFromTrash() {
        _state.update { oldState ->
            oldState.copy(
                isTrashed = false,
                snackbarEvent = SnackbarEvent(
                    message = context.getString(itemRestoredMessageRes),
                    action = SnackbarEvent.Action(
                        label = context.getString(R.string.undo),
                        key = TrashSnackbarAction.UndoNoteRestoration,
                    ),
                )
            )
        }
        applicationCoroutineScope.launch {
            editorFacade.restoreItemFromTrash(itemId = _state.value.itemId)
        }
    }

    fun onAttemptEditTrashed() {
        _state.update { oldState ->
            oldState.copy(
                snackbarEvent = SnackbarEvent(
                    message = context.getString(R.string.cannot_edit_in_trash),
                    action = SnackbarEvent.Action(
                        label = context.getString(R.string.restore),
                        key = TrashSnackbarAction.Restore,
                    ),
                )
            )
        }
    }

    open fun handleSnackbarAction(action: SnackbarEvent.Action) {
        when (action.key as TrashSnackbarAction) {
            TrashSnackbarAction.Restore -> restoreItemFromTrash()
            TrashSnackbarAction.UndoNoteRestoration -> undoItemRestoration()
        }
    }

    fun onShareCurrentItemClick() {
        _state.update { it.copy(requestItemShareType = true) }
    }

    fun cancelItemShareTypeRequest() {
        _state.update { it.copy(requestItemShareType = false) }
    }

    fun shareItemAs(shareContentType: ShareContentType) {
        cancelItemShareTypeRequest()
        viewModelScope.launch(Dispatchers.Default) {
            when (shareContentType) {
                ShareContentType.AS_TEXT -> shareAsText()
                ShareContentType.AS_PDF -> shareAsPdf()
            }
        }
    }

    fun onAddReminderClick() {
        viewModelScope.launch(Dispatchers.Default) {
            if (checkReminderPermissions()) {
                _state.update { oldState ->
                    val editorData = buildReminderEditorDataForState(state = oldState)
                    oldState.copy(reminderEditorData = editorData, showReminderEditorOverview = true)
                }
            }
        }
    }

    fun checkReminderPermissions(): Boolean {
        val repository = permissionsRepository.get()
        if (!repository.canPostNotifications()) {
            _state.update {
                it.copy(
                    showPostNotificationsPermissionPrompt = true,
                    showSetAlarmsPermissionPrompt = false,
                )
            }
            return false
        }
        if (!repository.canScheduleAlarms()) {
            _state.update {
                it.copy(
                    showPostNotificationsPermissionPrompt = false,
                    showSetAlarmsPermissionPrompt = true,
                )
            }
            return false
        }

        val oldState = _state.getAndUpdate {
            it.copy(
                showPostNotificationsPermissionPrompt = false,
                showSetAlarmsPermissionPrompt = false,
            )
        }

        if (oldState.showPostNotificationsPermissionPrompt || oldState.showSetAlarmsPermissionPrompt) {
            context.sendBroadcast(RescheduleRemindersCommandReceiver.Companion.getIntent(context))
        }

        return true
    }

    fun hideReminderPermissionsPrompt() {
        _state.update {
            it.copy(
                showPostNotificationsPermissionPrompt = false,
                showSetAlarmsPermissionPrompt = false,
            )
        }
    }

    fun hideReminderOverview() {
        _state.update { it.copy(showReminderEditorOverview = false) }
    }

    fun saveReminder() {
        hideReminderOverview()
        applicationCoroutineScope.launch {
            val editorData = _state.value.reminderEditorData ?: return@launch
            val zonedDate = editorData.asZonedDateTime()
            editorFacade.setReminder(
                itemId = _state.value.itemId,
                date = zonedDate.toInstant(TimeZone.currentSystemDefault()),
            )
        }
    }

    fun deleteReminder() {
        _state.update { oldState ->
            oldState.copy(
                reminderEditorData = null,
                showReminderEditorOverview = false,
            )
        }
        applicationCoroutineScope.launch {
            editorFacade.deleteReminder(itemId = _state.value.itemId)
        }
    }

    fun editReminderDate() {
        _state.update { it.copy(showReminderDatePicker = true) }
    }

    fun editReminderTime() {
        _state.update { it.copy(showReminderTimePicker = true) }
    }

    fun hideReminderDatePicker() {
        _state.update { it.copy(showReminderDatePicker = false) }
    }

    fun saveReminderDatePickerResult(dateMillis: Long?) {
        if (dateMillis == null) return
        viewModelScope.launch(Dispatchers.Default) {
            _state.update { oldState ->
                val oldEditor = oldState.reminderEditorData
                val newEditor = oldEditor?.copy(dateMillis = dateMillis)
                val zonedDate = newEditor?.asZonedDateTime()
                    ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val editorData = buildReminderEditorDataForDate(date = zonedDate, isNewReminder = oldState.reminderData == null)
                oldState.copy(
                    reminderEditorData = editorData,
                    showReminderEditorOverview = true,
                    showReminderDatePicker = false,
                )
            }
        }
    }

    fun hideReminderTimePicker() {
        _state.update { it.copy(showReminderTimePicker = false) }
    }

    fun openAppSettings() {
        viewModelScope.launch {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            navigationEventsHost.navigate(intent)
        }
    }

    fun openAlarmsSettings() {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                navigationEventsHost.navigate(intent)
            }
        }
    }

    fun saveReminderTimePickerResult(timePickerState: TimePickerState) {
        viewModelScope.launch(Dispatchers.Default) {
            _state.update { oldState ->
                val reminderEditorData = oldState.reminderEditorData ?: return@update oldState
                val localDate = reminderEditorData
                    .copy(hourOfDay = timePickerState.hour, minuteOfHour = timePickerState.minute)
                    .asZonedDateTime()
                val editorData = buildReminderEditorDataForDate(
                    date = localDate,
                    isNewReminder = oldState.reminderData == null
                )
                oldState.copy(
                    reminderEditorData = editorData,
                    showReminderEditorOverview = true,
                    showReminderTimePicker = false,
                )
            }
        }
    }

    fun showBackgroundSelection() {
        _state.update { it.copy(showBackgroundSelector = true) }
    }

    fun hideBackgroundSelection() {
        _state.update { it.copy(showBackgroundSelector = false) }
    }

    fun saveBackgroundColor(color: NoteColor?) {
        hideBackgroundSelection()
        applicationCoroutineScope.launch {
            editorFacade.saveBackgroundColor(itemId = _state.value.itemId, color = color)
        }
    }

    private suspend fun shareAsText() {
        val textRepresentation = getTextRepresentation(_state.value) ?: return
        val shareIntent = shareTextIntentBuilder.get().build(
            subject = textRepresentation.title,
            content = textRepresentation.content,
        ) ?: return
        navigationEventsHost.navigate(intent = shareIntent)
    }

    private suspend fun shareAsPdf() {
        val pdfFile = getPdfRepresentation(_state.value) ?: return
        val shareIntent = shareFileIntentBuilder.get().build(file = pdfFile) ?: return
        navigationEventsHost.navigate(intent = shareIntent)
    }

    private fun undoItemRestoration() {
        _state.update { it.copy(isTrashed = true) }
        applicationCoroutineScope.launch {
            editorFacade.moveToTrash(itemId = _state.value.itemId)
        }
    }

    private fun observeEditedItemChanges(itemId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            itemUpdatesFlow(itemId).collectLatest { updatedItem: Item ->
                refreshScreenState(updatedItem)
            }
        }
    }

    private fun refreshScreenState(updatedItem: Item) {
        _state.update { oldState: State ->
            val newState = oldState.copy(
                itemId = updatedItem.id,
                title = updatedItem.title,
                isPinned = updatedItem.isPinned,
                reminderData = buildReminderData(updatedItem.reminderDate, screenBackground = updatedItem.backgroundColor),
                isTrashed = updatedItem.isTrashed,
                modificationStatusMessage = buildModificationDateText.get().invoke(updatedItem.modificationDate),
                background = updatedItem.backgroundColor,
                backgroundColorList = buildBackgroundColorList(),
            )
            fillWithScreenSpecificData(oldState, newState, updatedItem)
        }
    }

    private fun buildBackgroundColorList(): List<NoteColor?> {
        return buildList {
            add(null)
            addAll(NoteColor.entries)
        }
    }

    private fun buildReminderData(reminderDate: Instant?, screenBackground: NoteColor?): ReminderStateData? {
        if (reminderDate == null) return null
        val localDateTime = reminderDate.toLocalDateTime(TimeZone.currentSystemDefault())
        val dateText = reminderDateTimeFormat.format(localDateTime)
        val outdated = reminderDate < Clock.System.now()
        return ReminderStateData(
            sourceDate = reminderDate,
            dateString = if (outdated) dateText.asStrikethroughText() else AnnotatedString(dateText),
            outdated = outdated,
            reminderColorDay = screenBackground?.day?.let { Color(it).lighten(fraction = 0.4f) } ?: LightOceanMist,
            reminderColorNight = screenBackground?.night?.let { Color(it).lighten(fraction = 0.2f) } ?: DarkOcean,
        )
    }

    private fun buildReminderEditorDataForState(state: State): ReminderEditorData {
        val zonedDate = state.reminderEditorData?.asZonedDateTime()
            ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return buildReminderEditorDataForDate(date = zonedDate, isNewReminder = state.reminderData == null)
    }

    private fun buildReminderEditorDataForDate(
        date: LocalDateTime,
        isNewReminder: Boolean,
    ): ReminderEditorData {
        val zoneOffsetSeconds = ZonedDateTime.now(ZoneId.systemDefault()).offset.totalSeconds
        val dateMillis = (date.toInstant(TimeZone.currentSystemDefault()).epochSeconds + zoneOffsetSeconds) * 1000
        val editorData = ReminderEditorData(
            isNewReminder = isNewReminder,
            dateMillis = dateMillis,
            dateString = reminderEditorDateFormat.format(date),
            timeString = reminderEditorTimeFormat.format(date),
            hourOfDay = date.hour,
            minuteOfHour = date.minute,
        )
        return editorData
    }
}