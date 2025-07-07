import androidx.room.gradle.RoomExtension
import com.andres.notes.master.kotlin
import com.andres.notes.master.library
import com.andres.notes.master.libs
import com.andres.notes.master.plugin
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.plugin("androidx.room").pluginId)
            apply(plugin = libs.plugin("ksp").pluginId)

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/roomSchemas")
            }

            kotlin {
                sourceSets.commonMain.dependencies {
                    implementation(libs.library("androidx.room.runtime"))
                    implementation(libs.library("androidx.sqlite"))
                }
                dependencies {
                    // Dynamically checks supported KMP targets
                    // and applies KSP room compiled for each supported target
                    this@kotlin.targets.all {
                        val kspConfiguration = "ksp${this.name.replaceFirstChar { it.titlecase() }}"
                        if (configurations.names.contains(kspConfiguration)) {
                            kspConfiguration(dependencyNotation = libs.library("androidx.room.compiler"))
                        }
                    }
                }
            }
        }
    }
}