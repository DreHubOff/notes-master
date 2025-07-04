package com.andres.notes.master.data.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andres.notes.master.core.model.NoteColor
import java.time.OffsetDateTime

const val TEXT_NOTE_TABLE_NAME = "text_note"

@Entity(tableName = TEXT_NOTE_TABLE_NAME)
data class TextNoteEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "creation_date", typeAffinity = ColumnInfo.INTEGER)
    val creationDate: OffsetDateTime,

    @ColumnInfo(name = "modification_date", typeAffinity = ColumnInfo.INTEGER)
    val modificationDate: OffsetDateTime,

    @ColumnInfo("reminder_date", typeAffinity = ColumnInfo.INTEGER)
    val reminderDate: OffsetDateTime?,

    @ColumnInfo(name = "display_color_resource")
    val displayColorResource: NoteColor?,

    @ColumnInfo(name = "pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_trashed")
    val isTrashed: Boolean = false,

    @ColumnInfo(name = "trashed_date")
    val trashedDate: OffsetDateTime?,

    @ColumnInfo("reminder_posted")
    val reminderHasBeenPosted: Boolean,
)