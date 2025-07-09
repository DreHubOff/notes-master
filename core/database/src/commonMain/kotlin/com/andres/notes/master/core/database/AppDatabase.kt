package com.andres.notes.master.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andres.notes.master.core.database.converter.Converters
import com.andres.notes.master.core.database.dao.ChecklistDao
import com.andres.notes.master.core.database.dao.ChecklistItemDao
import com.andres.notes.master.core.database.dao.TextNoteDao
import com.andres.notes.master.core.database.di.AppDatabaseConstructor
import com.andres.notes.master.core.database.table.ChecklistEntity
import com.andres.notes.master.core.database.table.ChecklistItemEntity
import com.andres.notes.master.core.database.table.TextNoteEntity

// TODO:
//  1. Add KEEP rules: https://developer.android.com/kotlin/multiplatform/room#minification_and_obfuscation

@Database(
    entities = [
        TextNoteEntity::class,
        ChecklistEntity::class,
        ChecklistItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun textNoteDao(): TextNoteDao

    abstract fun checklistDao(): ChecklistDao

    abstract fun checklistItemDao(): ChecklistItemDao

    companion object {
        const val DB_NAME = "application_database"
    }
}