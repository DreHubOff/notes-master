package com.andres.notes.master.core.interactor

import android.util.Log
import com.andres.notes.master.BuildConfig
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class PermanentlyDeleteOldTrashRecordsInteractor @Inject constructor(
    private val observeApplicationMainTypeTrashed: ObserveApplicationMainTypeTrashedInteractor,
    private val permanentlyDeleteApplicationMainDataType: PermanentlyDeleteApplicationMainDataTypeInteractor,
) {

    suspend operator fun invoke() {
        val trashedItems = observeApplicationMainTypeTrashed().firstOrNull().orEmpty()
        Log.d("PermanentlyDeleteInteractor", "Observed ${trashedItems.size} trashed items.")

        val now = Clock.System.now()
        val expirationThreshold = BuildConfig.TRASH_ITEM_MAX_LIFETIME_SECONDS.seconds

        val itemsToRemove = trashedItems.filter { item ->
            val trashedDate = item.trashedDate
            if (trashedDate == null) {
                Log.w("PermanentlyDeleteInteractor", "Skipping item with null trashedDate, id=${item.id}")
                return@filter false
            }
            val trashedDuration = now - trashedDate
            val expired = trashedDuration > expirationThreshold
            if (expired) {
                Log.d("PermanentlyDeleteInteractor", "Item id=${item.id} expired (trashed $trashedDuration ago)")
            }
            expired
        }

        if (itemsToRemove.isNotEmpty()) {
            Log.d("PermanentlyDeleteInteractor", "Deleting ${itemsToRemove.size} expired trashed items...")
            permanentlyDeleteApplicationMainDataType(*itemsToRemove.toTypedArray())
            Log.d("PermanentlyDeleteInteractor", "Deletion completed.")
        } else {
            Log.d("PermanentlyDeleteInteractor", "No expired trashed items to delete.")
        }
    }
}