package com.andres.notes.master.di

import android.content.Context
import com.andres.notes.master.core.database.AppDatabase
import com.andres.notes.master.core.database.dao.ChecklistDao
import com.andres.notes.master.core.database.dao.ChecklistItemDao
import com.andres.notes.master.core.database.dao.TextNoteDao
import com.andres.notes.master.core.database.di.DatabaseBuilderProvider
import com.andres.notes.master.core.database.di.buildDatabase
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
        return buildDatabase(DatabaseBuilderProvider(context))
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