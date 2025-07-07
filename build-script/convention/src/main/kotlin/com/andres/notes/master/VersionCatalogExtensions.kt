package com.andres.notes.master

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.plugin.use.PluginDependency

internal fun VersionCatalog.plugin(name: String): PluginDependency = findPlugin(name).get().get()

internal fun VersionCatalog.library(name: String): MinimalExternalModuleDependency = findLibrary(name).get().get()