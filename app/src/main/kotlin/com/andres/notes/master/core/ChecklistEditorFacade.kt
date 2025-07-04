package com.andres.notes.master.core

import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.data.ChecklistRepository
import com.andres.notes.master.data.ReminderSchedulerRepository
import java.time.OffsetDateTime
import javax.inject.Inject

class ChecklistEditorFacade @Inject constructor(
    private val checklistRepository: ChecklistRepository,
    private val reminderSchedulerRepository: ReminderSchedulerRepository,
) : MainTypeEditorFacade {

    override suspend fun storePinnedSate(pinned: Boolean, itemId: Long) {
        checklistRepository.storePinnedSate(pinned, itemId)
    }

    override suspend fun storeNewTitle(title: String, itemId: Long) {
        checklistRepository.storeNewTitle(title, itemId)
    }

    override suspend fun moveToTrash(itemId: Long) {
        cancelAlarm(itemId)
        checklistRepository.moveToTrash(itemId)
    }

    override suspend fun permanentlyDelete(itemId: Long) {
        cancelAlarm(itemId)
        checklistRepository.permanentlyDelete(itemId)
    }

    override suspend fun restoreItemFromTrash(itemId: Long) {
        checklistRepository.restoreItemFromTrash(itemId)
    }

    override suspend fun deleteReminder(itemId: Long) {
        cancelAlarm(itemId)
        checklistRepository.deleteReminder(itemId)
    }

    override suspend fun setReminder(itemId: Long, date: OffsetDateTime) {
        checklistRepository.storeReminderDate(itemId, date)
        val item = checklistRepository.getChecklistById(itemId) ?: return
        checklistRepository.updateChecklistReminderShownState(itemId, isShown = false)
        reminderSchedulerRepository.cancelReminder(item)
        reminderSchedulerRepository.scheduleReminder(item)
    }

    override suspend fun saveBackgroundColor(itemId: Long, color: NoteColor?) {
        checklistRepository.saveBackgroundColor(itemId, color)
    }

    private suspend fun cancelAlarm(itemId: Long) {
        val item = checklistRepository.getChecklistById(itemId) ?: return
        checklistRepository.updateChecklistReminderShownState(itemId, isShown = false)
        checklistRepository.deleteReminder(item.id)
        reminderSchedulerRepository.cancelReminder(item, removeNotification = true)
    }
}