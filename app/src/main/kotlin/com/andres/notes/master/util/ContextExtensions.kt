package com.andres.notes.master.util

import android.content.Context

fun Context.getAppVersionName(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (error: Exception) {
        "Unknown"
    }
}