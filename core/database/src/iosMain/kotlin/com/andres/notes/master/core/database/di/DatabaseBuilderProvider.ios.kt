package com.andres.notes.master.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.andres.notes.master.core.database.AppDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseBuilderProvider {

    actual fun get(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = documentDirectory() + "/${AppDatabase.DB_NAME}.db"
        return Room.databaseBuilder<AppDatabase>(name = dbFilePath, factory = AppDatabaseConstructor::initialize)
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}