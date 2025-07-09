package com.andres.notes.master.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andres.notes.master.core.database.AppDatabase

actual class DatabaseBuilderProvider(
    private val context: Context,
) {
    actual fun get(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        return Room.databaseBuilder(
            context = appContext,
            klass = AppDatabase::class.java,
            name = AppDatabase.DB_NAME,
        )
    }
}