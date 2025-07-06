package com.andres.notes.master.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.data.database.table.CHECKLIST_TABLE_NAME
import com.andres.notes.master.data.database.table.ChecklistEntity
import com.andres.notes.master.data.database.table.ChecklistWithItems
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface ChecklistDao {

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME WHERE is_trashed = 0")
    fun observeNotTrashed(): Flow<List<ChecklistWithItems>>

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME WHERE is_trashed = 1")
    fun observeTrashed(): Flow<List<ChecklistWithItems>>

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME WHERE id = :id")
    fun observeChecklistWithItemsById(id: Long): Flow<List<ChecklistWithItems>>

    @Transaction
    @Query("SELECT * FROM $CHECKLIST_TABLE_NAME WHERE id = :id LIMIT 1")
    suspend fun getChecklistWithItemsById(id: Long): ChecklistWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: ChecklistEntity): Long

    @Transaction
    suspend fun insertChecklistWithItems(
        checklistWithItems: ChecklistWithItems,
        checklistItemDao: ChecklistItemDao,
    ): Long {
        val checklistId = insertChecklist(checklistWithItems.checklist)
        val items = checklistWithItems.items.map {
            it.copy(checklistId = checklistId)
        }
        checklistItemDao.insertChecklistItems(items)
        return checklistId
    }

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET title = :title WHERE id = :id")
    suspend fun updateChecklistTitleById(id: Long, title: String)

    @Transaction
    suspend fun updateChecklistTitleById(id: Long, title: String, modificationDate: Instant) {
        updateChecklistTitleById(id, title)
        updateChecklistModifiedDateById(id, modificationDate)
    }

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET modification_date = :date WHERE id = :id")
    suspend fun updateChecklistModifiedDateById(id: Long, date: Instant)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET pinned = :isPinned WHERE id = :id")
    suspend fun updatePinnedStateById(id: Long, isPinned: Boolean)

    @Delete
    suspend fun deleteChecklist(entity: ChecklistEntity)

    @Delete
    suspend fun deleteChecklists(entities: List<ChecklistEntity>)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET is_trashed = :isTrashed WHERE id = :id")
    suspend fun updateIsTrashedById(id: Long, isTrashed: Boolean)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET trashed_date = :date WHERE id = :id")
    suspend fun updateTrashedDateById(id: Long, date: Instant?)

    @Query("DELETE FROM $CHECKLIST_TABLE_NAME WHERE id = :checklistId")
    suspend fun deleteChecklistById(checklistId: Long)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET reminder_date = :date WHERE id = :id")
    suspend fun updateReminderDateById(id: Long, date: Instant?)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET reminder_posted = :isShown WHERE id = :id")
    suspend fun updateChecklistReminderShownState(id: Long, isShown: Boolean)

    @Query("UPDATE $CHECKLIST_TABLE_NAME SET background_color = :color WHERE id = :id")
    suspend fun updateBackgroundColorById(id: Long, color: NoteColor?)
}