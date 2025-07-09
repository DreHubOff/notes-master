package com.andres.notes.master

import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

fun PluginManager.alias(provider: Provider<PluginDependency>): Unit = apply(provider.get().pluginId)