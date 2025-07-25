import com.andres.notes.master.AndroidBuildDefaults

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.convention.suppressWarnings)
    id("kotlin-parcelize")
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.andres.notes.master"
    compileSdk = AndroidBuildDefaults.COMPILE_SDK

    defaultConfig {
        applicationId = "com.andres.notes.master"
        minSdk = AndroidBuildDefaults.MIN_SDK
        versionCode = 1_0_0
        versionName = "1.0.0"
        val fileProviderAuthority = "${applicationId}.fileprovider"
        buildConfigField("String", "FILE_PROVIDER_AUTHORITY", "\"${fileProviderAuthority}\"")
        manifestPlaceholders["fileProviderAuthority"] = fileProviderAuthority
        buildConfigField("int", "TRASH_ITEM_MAX_LIFETIME_SECONDS", "60 * 60 * 24 * 7") // 7 days

        testInstrumentationRunner = "com.andres.notes.master.HiltTestRunner"
    }

    buildTypes {
        debug {
            buildConfigField("int", "TRASH_ITEM_MAX_LIFETIME_SECONDS", "60 * 1") // 1 minutes
        }
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(project(":core:model"))
    implementation(project(":core:database"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.runner)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)
    androidTestImplementation(libs.hilt.testing)
    kspAndroidTest(libs.hilt.compiler)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(libs.list.reorderable)

    implementation(libs.simplypdf)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
}