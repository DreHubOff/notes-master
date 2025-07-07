plugins {
    alias(libs.plugins.convention.kmpModule)
    alias(libs.plugins.convention.suppressWarnings)
    alias(libs.plugins.convention.room)
}

kotlin {
    androidLibrary {
        namespace = "com.andres.notes.master.core.database"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core:model"))
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}