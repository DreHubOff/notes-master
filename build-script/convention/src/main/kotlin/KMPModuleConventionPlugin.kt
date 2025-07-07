import com.andres.notes.master.AndroidBuildDefaults
import com.andres.notes.master.androidLibrary
import com.andres.notes.master.kotlin
import com.andres.notes.master.library
import com.andres.notes.master.libs
import com.andres.notes.master.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class KMPModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.plugin("android-kotlin-multiplatform-library").pluginId)
            apply(plugin = libs.plugin("kotlin-multiplatform").pluginId)

            kotlin {
                jvm()
                androidLibrary {
                    compileSdk = AndroidBuildDefaults.COMPILE_SDK
                    minSdk = AndroidBuildDefaults.MIN_SDK
                }

                val xcfName = "${target.displayName.replace("'", "")}Kit"
                iosX64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                iosArm64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                iosSimulatorArm64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                sourceSets.commonMain.dependencies {
                    implementation(libs.library("kotlin.stdlib"))
                }
            }
        }
    }
}