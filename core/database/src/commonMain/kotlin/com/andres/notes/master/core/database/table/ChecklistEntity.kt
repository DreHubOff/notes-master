package com.andres.notes.master.core.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andres.notes.master.core.model.NoteColor
import kotlin.time.Instant

const val CHECKLIST_TABLE_NAME = "checklists"

@Entity(tableName = CHECKLIST_TABLE_NAME)
data class ChecklistEntity(

    @ColumnInfo(name = PRIMARY_KEY_COLUMN_NAME)
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo("title")
    val title: String,

    @ColumnInfo("creation_date", typeAffinity = ColumnInfo.INTEGER)
    val creationDate: Instant,

    @ColumnInfo("modification_date", typeAffinity = ColumnInfo.INTEGER)
    val modificationDate: Instant,

    @ColumnInfo("reminder_date", typeAffinity = ColumnInfo.INTEGER)
    val reminderDate: Instant?,

    @ColumnInfo("pinned")
    val isPinned: Boolean,

    @ColumnInfo("background_color")
    val backgroundColor: NoteColor?,

    @ColumnInfo("is_trashed")
    val isTrashed: Boolean,

    @ColumnInfo("trashed_date")
    val trashedDate: Instant?,

    @ColumnInfo("reminder_posted")
    val reminderHasBeenPosted: Boolean,
) {

    companion object {
        const val PRIMARY_KEY_COLUMN_NAME = "id"
    }
}