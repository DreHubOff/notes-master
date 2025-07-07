import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.andres.notes.master.buildscript"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("kmpModule") {
            id = libs.plugins.convention.kmpModule.get().pluginId
            implementationClass = "KMPModuleConventionPlugin"
        }
        register("roomSetupPlugin") {
            id = libs.plugins.convention.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
        register("suppressWarningsPlugin") {
            id = libs.plugins.convention.suppressWarnings.get().pluginId
            implementationClass = "SuppressWarningsPlugin"
        }
    }
}