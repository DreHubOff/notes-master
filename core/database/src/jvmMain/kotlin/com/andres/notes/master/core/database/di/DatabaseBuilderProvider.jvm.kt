package com.andres.notes.master.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.andres.notes.master.core.database.AppDatabase
import java.io.File

actual class DatabaseBuilderProvider {
    actual fun get(): RoomDatabase.Builder<AppDatabase> {
        val dbDir = File(System.getProperty("user.home"), ".notes_master")
        dbDir.mkdirs()
        val dbFile = File(dbDir, "${AppDatabase.DB_NAME}.db")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
            factory = AppDatabaseConstructor::initialize,
        )
    }
}