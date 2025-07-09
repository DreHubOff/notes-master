import com.andres.notes.master.AndroidBuildDefaults
import com.andres.notes.master.alias
import com.andres.notes.master.androidLibrary
import com.andres.notes.master.kotlinMultiplatformExtension
import com.andres.notes.master.libs
import com.andres.notes.master.pluginManager
import org.gradle.api.Plugin
import org.gradle.api.Project

class KMPModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager {
                alias(libs.plugins.android.kotlin.multiplatform.library)
                alias(libs.plugins.kotlin.multiplatform)
            }

            kotlinMultiplatformExtension {
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
                    implementation(libs.kotlin.stdlib)
                }
            }
        }
    }
}