package com.andres.notes.master

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.accessors.runtime.extensionOf
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.pluginManager(configure: PluginManager.() -> Unit): Unit = pluginManager.configure()

internal val Project.libs: LibrariesForLibs get() = extensionOf(this, "libs") as LibrariesForLibs

internal fun Project.kotlinMultiplatformExtension(configure: Action<KotlinMultiplatformExtension>): Unit =
    (this as ExtensionAware).extensions.configure("kotlin", configure)