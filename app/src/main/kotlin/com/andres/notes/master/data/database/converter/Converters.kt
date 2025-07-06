package com.andres.notes.master.data.database.converter

import androidx.room.TypeConverter
import com.andres.notes.master.core.model.NoteColor
import kotlin.time.Instant

class Converters {

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.epochSeconds

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::fromEpochSeconds)

    @TypeConverter
    fun fromNoteColor(color: NoteColor): String = color.name

    @TypeConverter
    fun toNoteColor(name: String): NoteColor = NoteColor.valueOf(name)
}