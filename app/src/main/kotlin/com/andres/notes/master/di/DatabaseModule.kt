package com.andres.notes.master.di

import android.content.Context
import androidx.room.Room
import com.andres.notes.master.core.database.AppDatabase
import com.andres.notes.master.core.database.dao.ChecklistDao
import com.andres.notes.master.core.database.dao.ChecklistItemDao
import com.andres.notes.master.core.database.dao.TextNoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.Companion.DB_NAME,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideTextNoteDao(database: AppDatabase): TextNoteDao {
        return database.textNoteDao()
    }

    @Provides
    fun provideChecklistDao(database: AppDatabase): ChecklistDao {
        return database.checklistDao()
    }

    @Provides
    fun provideChecklistItemDao(database: AppDatabase): ChecklistItemDao {
        return database.checklistItemDao()
    }
}