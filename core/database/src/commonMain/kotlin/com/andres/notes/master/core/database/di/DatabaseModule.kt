package com.andres.notes.master.core.database.di

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.andres.notes.master.core.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class DatabaseBuilderProvider {
    fun get(): RoomDatabase.Builder<AppDatabase>
}

fun buildDatabase(builderProvider: DatabaseBuilderProvider): AppDatabase {
    return builderProvider
        .get()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}