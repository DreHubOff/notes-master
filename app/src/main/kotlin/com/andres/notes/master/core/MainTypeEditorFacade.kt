package com.andres.notes.master.core

import com.andres.notes.master.core.model.NoteColor
import kotlin.time.Instant

interface MainTypeEditorFacade {

    suspend fun storePinnedSate(pinned: Boolean, itemId: Long)

    suspend fun storeNewTitle(title: String, itemId: Long)

    suspend fun moveToTrash(itemId: Long)

    suspend fun permanentlyDelete(itemId: Long)

    suspend fun restoreItemFromTrash(itemId: Long)

    suspend fun deleteReminder(itemId: Long)

    suspend fun setReminder(itemId: Long, date: Instant)

    suspend fun saveBackgroundColor(itemId: Long, color: NoteColor?)
}