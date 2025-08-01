plugins {
    alias(libs.plugins.convention.kmpModule)
    alias(libs.plugins.convention.suppressWarnings)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.andres.notes.master.core.model"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}