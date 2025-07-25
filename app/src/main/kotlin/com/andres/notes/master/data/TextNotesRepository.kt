package com.andres.notes.master.data

import com.andres.notes.master.core.database.dao.TextNoteDao
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.data.mapper.toDomain
import com.andres.notes.master.data.mapper.toEntity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

class TextNotesRepository @Inject constructor(
    private val textNoteDao: TextNoteDao,
) {

    fun observeNotTrashedNotes(): Flow<List<TextNote>> =
        textNoteDao.observeNotTrashed().map { list -> list.map { textNote -> textNote.toDomain() } }

    fun observeTrashedNotes(): Flow<List<TextNote>> =
        textNoteDao.observeTrashed().map { list -> list.map { textNote -> textNote.toDomain() } }

    fun observeNoteById(id: Long): Flow<TextNote> =
        textNoteDao.observeById(id).mapNotNull { it?.toDomain() }

    suspend fun getNoteById(id: Long): TextNote? =
        textNoteDao.getById(id)?.toDomain()

    suspend fun saveTextNote(textNote: TextNote): TextNote {
        val id = withContext(NonCancellable) {
            textNoteDao.insertTextNote(textNote.toEntity())
        }
        return textNote.copy(id = id)
    }

    suspend fun permanentlyDelete(itemId: Long) {
        withContext(NonCancellable) {
            textNoteDao.deleteById(itemId)
        }
    }

    suspend fun permanentlyDelete(textNote: TextNote) = permanentlyDelete(listOf(textNote))

    suspend fun permanentlyDelete(textNotes: List<TextNote>) {
        withContext(NonCancellable) {
            textNoteDao.delete(textNotes.map(TextNote::toEntity))
        }
    }

    suspend fun storePinnedSate(pinned: Boolean, itemId: Long) {
        withContext(NonCancellable) {
            textNoteDao.updatePinnedStateById(id = itemId, pinned = pinned)
        }
    }

    suspend fun storeNewTitle(title: String, itemId: Long) {
        withContext(NonCancellable) {
            textNoteDao.updateTitleById(id = itemId, newTitle = title)
            textNoteDao.updateModificationDateById(id = itemId, newDate = Clock.System.now())
        }
    }

    suspend fun storeNewContent(content: String, itemId: Long) {
        withContext(NonCancellable) {
            textNoteDao.updateContentById(id = itemId, newContent = content)
            textNoteDao.updateModificationDateById(id = itemId, newDate = Clock.System.now())
        }
    }

    suspend fun moveToTrash(itemId: Long) {
        withContext(NonCancellable) {
            textNoteDao.updateIsTrashedById(id = itemId, isTrashed = true)
            textNoteDao.updateTrashedDateById(id = itemId, date = Clock.System.now())
            textNoteDao.updatePinnedStateById(id = itemId, pinned = false)
        }
    }

    suspend fun restoreItemFromTrash(itemId: Long) {
        withContext(NonCancellable) {
            textNoteDao.updateIsTrashedById(id = itemId, isTrashed = false)
            textNoteDao.updateTrashedDateById(id = itemId, date = null)
        }
    }

    suspend fun deleteReminder(itemId: Long) {
        textNoteDao.updateReminderDateById(id = itemId, date = null)
    }

    suspend fun storeReminderDate(itemId: Long, date: Instant) {
        withContext(NonCancellable) {
            textNoteDao.updateReminderDateById(id = itemId, date = date)
        }
    }

    suspend fun updateChecklistReminderShownState(noteId: Long, isShown: Boolean) {
        withContext(NonCancellable) {
            textNoteDao.updateChecklistReminderShownState(id = noteId, isShown = isShown)
        }
    }

    suspend fun saveBackgroundColor(noteId: Long, color: NoteColor?) {
        withContext(NonCancellable) {
            textNoteDao.updateBackgroundColorById(id = noteId, color = color)
        }
    }
}