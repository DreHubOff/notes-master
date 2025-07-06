package com.andres.notes.master.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.serializers.FormattedInstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Module
@InstallIn(SingletonComponent::class)
@OptIn(ExperimentalTime::class)
object SerializationModule {

    @Provides
    fun provideInstantSerializer(): FormattedInstantSerializer = object : FormattedInstantSerializer(
        name = "serializer.ISO_DATE_TIME_OFFSET", format = DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET
    ) {}

    @Provides
    @Singleton
    fun provideJson(serializer: FormattedInstantSerializer): Json {
        return Json {
            serializersModule = SerializersModule {
                contextual(Instant::class, serializer)
            }
        }
    }
}