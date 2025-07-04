package com.andres.notes.master.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andres.notes.master.data.database.converter.Converters
import com.andres.notes.master.data.database.dao.ChecklistDao
import com.andres.notes.master.data.database.dao.ChecklistItemDao
import com.andres.notes.master.data.database.dao.TextNoteDao
import com.andres.notes.master.data.database.table.ChecklistEntity
import com.andres.notes.master.data.database.table.ChecklistItemEntity
import com.andres.notes.master.data.database.table.TextNoteEntity

@Database(
    entities = [
        TextNoteEntity::class,
        ChecklistEntity::class,
        ChecklistItemEntity::class,
    ], version = 6, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun textNoteDao(): TextNoteDao

    abstract fun checklistDao(): ChecklistDao

    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        const val DB_NAME = "application_database"
    }
}