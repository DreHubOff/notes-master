package com.andres.notes.master.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.andres.notes.master.ApplicationCoroutineScope
import com.andres.notes.master.R
import com.andres.notes.master.di.qualifier.ApplicationGlobalScope
import com.andres.notes.master.di.qualifier.BulletPointSymbol
import com.andres.notes.master.get
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RootApplicationModule {

    @Provides
    @Singleton
    @ApplicationGlobalScope
    fun provideApplicationScope(): CoroutineScope = ApplicationCoroutineScope.get()

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService<AlarmManager>()!!

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService<NotificationManager>()!!

    @Provides
    @BulletPointSymbol
    fun provideBulletPointSymbol(@ApplicationContext context: Context): String =
        context.getString(R.string.bullet_point_symbol)
}