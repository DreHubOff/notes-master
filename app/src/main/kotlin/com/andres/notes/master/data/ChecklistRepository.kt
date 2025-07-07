package com.andres.notes.master.data

import androidx.room.withTransaction
import com.andres.notes.master.core.database.AppDatabase
import com.andres.notes.master.core.database.dao.ChecklistDao
import com.andres.notes.master.core.database.dao.ChecklistItemDao
import com.andres.notes.master.core.database.table.ChecklistWithItems
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.ChecklistItem
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.data.mapper.toDomain
import com.andres.notes.master.data.mapper.toEntity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

class ChecklistRepository @Inject constructor(
    private val database: AppDatabase,
    private val checklistDao: ChecklistDao,
    private val checklistItemDao: ChecklistItemDao,
) {

    suspend fun getChecklistById(id: Long): Checklist? =
        checklistDao.getChecklistWithItemsById(id)?.toDomain()

    fun observeChecklistById(id: Long): Flow<Checklist> {
        return checklistDao
            .observeChecklistWithItemsById(id)
            .filter { it.isNotEmpty() }
            .map { it.first().toDomain() }
    }

    suspend fun insertChecklist(checklist: Checklist): Checklist {
        val result = withContext(NonCancellable) {
            val checklistEntity = checklist.toEntity()
            val items = checklist.items.map { it.toEntity(parentChecklistId = checklistEntity.id) }
            val newChecklistId = checklistDao.insertChecklistWithItems(
                checklistWithItems = ChecklistWithItems(checklist = checklistEntity, items = items),
                checklistItemDao = checklistItemDao,
            )
            getChecklistById(newChecklistId)
        }
        return checkNotNull(result) { "New checklist was not inserted" }
    }

    fun observeNotTrashedChecklists(): Flow<List<Checklist>> =
        checklistDao.observeNotTrashed().map { list -> list.map { checklist -> checklist.toDomain() } }

    fun observeTrashedChecklists(): Flow<List<Checklist>> =
        checklistDao.observeTrashed().map { list -> list.map { checklist -> checklist.toDomain() } }

    suspend fun storeNewTitle(title: String, itemId: Long) {
        updateChecklistTitle(checklistId = itemId, title = title)
    }

    suspend fun updateChecklistTitle(checklistId: Long, title: String) {
        withContext(NonCancellable) {
            checklistDao.updateChecklistTitleById(
                id = checklistId,
                title = title,
                modificationDate = Clock.System.now()
            )
        }
    }

    suspend fun storePinnedSate(pinned: Boolean, itemId: Long) {
        withContext(NonCancellable) {
            // Does not effect modification date
            checklistDao.updatePinnedStateById(itemId, pinned)
        }
    }

    suspend fun saveChecklistItemAsLast(checklistId: Long, item: ChecklistItem): ChecklistItem {
        val (insertedItemId, newPosition) = withContext(NonCancellable) {
            database.withTransaction {
                val newPosition = (checklistItemDao.getLastListPosition(checklistId) ?: -1) + 1
                val itemToSave = item.copy(listPosition = newPosition).toEntity(parentChecklistId = checklistId)
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = Clock.System.now())
                checklistItemDao.insertChecklistItem(itemToSave) to newPosition
            }
        }
        return item.copy(id = insertedItemId, listPosition = newPosition)
    }

    suspend fun updateChecklistItemCheckedState(isChecked: Boolean, itemId: Long, checklistId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistItemDao.updateCheckedStateByIds(checked = isChecked, checklistId = checklistId, itemId = itemId)
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = Clock.System.now())
            }
        }
    }

    suspend fun updateChecklistItemTitle(itemId: Long, checklistId: Long, title: String) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = Clock.System.now())
                checklistItemDao.updateTitleByIds(itemId = itemId, checklistId = checklistId, title = title)
            }
        }
    }

    suspend fun deleteChecklistItem(itemId: Long, checklistId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistDao.updateChecklistModifiedDateById(id = checklistId, date = Clock.System.now())
                checklistItemDao.deleteItem(itemId = itemId, checklistId = checklistId)
                reorderItemPositions(checklistId = checklistId)
            }
        }
    }

    suspend fun permanentlyDelete(itemId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistItemDao.deleteItemsForChecklist(checklistId = itemId)
                checklistDao.deleteChecklistById(checklistId = itemId)
            }
        }
    }

    suspend fun delete(checklist: Checklist) = delete(listOf(checklist))

    suspend fun delete(checklists: List<Checklist>) {
        withContext(NonCancellable) {
            database.withTransaction {
                val items = checklists.flatMap { checklist ->
                    checklist
                        .items
                        .map { item -> item.toEntity(checklist.id) }
                }
                checklistItemDao.deleteItems(items)

                val checklistEntities = checklists.map(Checklist::toEntity)
                checklistDao.deleteChecklists(checklistEntities)
            }
        }
    }

    suspend fun insertChecklistItemAfterFollowing(
        checklistId: Long,
        itemBefore: Long,
        itemToInsert: ChecklistItem,
    ) {
        withContext(NonCancellable) {
            database.withTransaction {
                val oldItems = checklistItemDao.getItemsForChecklist(checklistId = checklistId)
                val itemBeforeIndex = oldItems.indexOfFirst { it.id == itemBefore }
                val itemToSave = itemToInsert.copy(listPosition = itemBeforeIndex + 1)

                if (itemBeforeIndex == oldItems.lastIndex) {
                    saveChecklistItemAsLast(checklistId = checklistId, item = itemToSave)
                    return@withTransaction
                }

                val itemsAfterInsertion = oldItems
                    .subList(itemToSave.listPosition, oldItems.size)
                    .map { checklistItem ->
                        checklistItem.copy(listPosition = checklistItem.listPosition + 1)
                    }

                val allToInsert = itemsAfterInsertion + itemToSave.toEntity(checklistId)
                checklistItemDao.insertChecklistItems(allToInsert)
            }
        }
    }

    suspend fun saveItemsNewOrder(checklistId: Long, orderedItemIds: List<Long>) {
        withContext(NonCancellable) {
            database.withTransaction {
                val dbUncheckedItems = checklistItemDao.getUncheckedItemsForChecklist(checklistId = checklistId)
                if (orderedItemIds.size != dbUncheckedItems.size) {
                    return@withTransaction
                }
                val orderedPositions = dbUncheckedItems.map { it.listPosition }.sorted()
                val updatedCollection = List(orderedItemIds.size) { index ->
                    val itemId = orderedItemIds[index]
                    val dbItem = dbUncheckedItems.find { it.id == itemId }
                    dbItem?.copy(listPosition = orderedPositions[index]) ?: error("Item with id=$itemId not found")
                }
                checklistItemDao.insertChecklistItems(updatedCollection)
            }
        }
    }

    suspend fun moveToTrash(itemId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistDao.updateIsTrashedById(id = itemId, isTrashed = true)
                checklistDao.updateTrashedDateById(id = itemId, date = Clock.System.now())
                checklistDao.updatePinnedStateById(id = itemId, isPinned = false)
            }
        }
    }

    suspend fun restoreItemFromTrash(itemId: Long) {
        restoreChecklist(checklistId = itemId)
    }

    suspend fun deleteReminder(itemId: Long) {
        checklistDao.updateReminderDateById(id = itemId, date = null)
    }

    suspend fun storeReminderDate(itemId: Long, date: Instant) {
        withContext(NonCancellable) {
            checklistDao.updateReminderDateById(id = itemId, date = date)
        }
    }

    suspend fun updateChecklistReminderShownState(checklistId: Long, isShown: Boolean) {
        withContext(NonCancellable) {
            checklistDao.updateChecklistReminderShownState(id = checklistId, isShown = isShown)
        }
    }

    suspend fun saveBackgroundColor(checklistId: Long, color: NoteColor?) {
        withContext(NonCancellable) {
            checklistDao.updateBackgroundColorById(id = checklistId, color = color)
        }
    }

    private suspend fun restoreChecklist(checklistId: Long) {
        withContext(NonCancellable) {
            database.withTransaction {
                checklistDao.updateIsTrashedById(id = checklistId, isTrashed = false)
                checklistDao.updateTrashedDateById(id = checklistId, date = null)
            }
        }
    }

    private suspend fun reorderItemPositions(checklistId: Long) {
        val reordered = checklistItemDao.getItemsForChecklist(checklistId = checklistId).mapIndexed { index, item ->
            item.copy(listPosition = index)
        }
        checklistItemDao.insertChecklistItems(reordered)
    }
}