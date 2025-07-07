package com.andres.notes.master

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun KotlinMultiplatformExtension.androidLibrary(configure: Action<KotlinMultiplatformAndroidLibraryTarget>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("androidLibrary", configure)