import com.andres.notes.master.AndroidBuildDefaults
import com.andres.notes.master.androidLibrary
import com.andres.notes.master.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KMPModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.kotlin.multiplatform.library")
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")

            kotlin {
                androidLibrary {
                    compileSdk = AndroidBuildDefaults.compileSdk
                    minSdk = AndroidBuildDefaults.minSdk
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
            }
        }
    }
}