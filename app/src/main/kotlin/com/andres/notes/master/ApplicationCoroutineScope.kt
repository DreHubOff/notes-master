package com.andres.notes.master

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val TAG = ApplicationCoroutineScope::class.simpleName

object ApplicationCoroutineScope

private val exceptionHandler
    get() = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Uncaught exception", throwable)
    }

fun ApplicationCoroutineScope.get() = CoroutineScope(SupervisorJob() + Dispatchers.Default + exceptionHandler)