package com.andres.notes.master

import android.app.Application
import android.util.Log
import com.andres.notes.master.data.FileManagerRepository
import com.andres.notes.master.di.qualifier.ApplicationGlobalScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

private val TAG = RootApplication::class.simpleName

@HiltAndroidApp
class RootApplication : Application() {

    @Inject
    @ApplicationGlobalScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var fileManagerRepository: Provider<FileManagerRepository>

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch(Dispatchers.IO) {
            val currentDate = System.currentTimeMillis().milliseconds
            fileManagerRepository.get().getSharableDir().listFiles()?.forEach { file ->
                val modificationDate = file.lastModified().milliseconds
                if ((currentDate - modificationDate) > 1.days) {
                    if (file.delete()) {
                        Log.d(TAG, "File deleted: ${file.name}")
                    } else {
                        Log.e(TAG, "Failed to delete file: ${file.name}")
                    }
                }
            }
        }
    }
}