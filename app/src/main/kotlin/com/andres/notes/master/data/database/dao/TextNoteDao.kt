package com.andres.notes.master.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.data.database.table.TEXT_NOTE_TABLE_NAME
import com.andres.notes.master.data.database.table.TextNoteEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface TextNoteDao {

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE is_trashed = 0")
    fun observeNotTrashed(): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE is_trashed = 1")
    fun observeTrashed(): Flow<List<TextNoteEntity>>

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<TextNoteEntity?>

    @Query("SELECT * FROM $TEXT_NOTE_TABLE_NAME WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TextNoteEntity?

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET pinned = :pinned WHERE id = :id")
    suspend fun updatePinnedStateById(id: Long, pinned: Boolean)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET title = :newTitle WHERE id = :id")
    suspend fun updateTitleById(id: Long, newTitle: String)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET content = :newContent WHERE id = :id")
    suspend fun updateContentById(id: Long, newContent: String)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET modification_date = :newDate WHERE id = :id")
    suspend fun updateModificationDateById(id: Long, newDate: Instant)

    @Insert
    suspend fun insertTextNote(textNote: TextNoteEntity): Long

    @Delete
    suspend fun delete(textNote: TextNoteEntity)

    @Delete
    suspend fun delete(textNotes: List<TextNoteEntity>)

    @Query("DELETE FROM $TEXT_NOTE_TABLE_NAME WHERE id = :noteId")
    suspend fun deleteById(noteId: Long)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET is_trashed = :isTrashed WHERE id = :id")
    suspend fun updateIsTrashedById(id: Long, isTrashed: Boolean)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET trashed_date = :date WHERE id = :id")
    suspend fun updateTrashedDateById(id: Long, date: Instant?)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET reminder_date = :date WHERE id = :id")
    suspend fun updateReminderDateById(id: Long, date: Instant?)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET reminder_posted = :isShown WHERE id = :id")
    suspend fun updateChecklistReminderShownState(id: Long, isShown: Boolean)

    @Query("UPDATE $TEXT_NOTE_TABLE_NAME SET display_color_resource = :color WHERE id = :id")
    suspend fun updateBackgroundColorById(id: Long, color: NoteColor?)
}