import androidx.room.gradle.RoomExtension
import com.andres.notes.master.alias
import com.andres.notes.master.kotlinMultiplatformExtension
import com.andres.notes.master.libs
import com.andres.notes.master.pluginManager
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager {
                alias(libs.plugins.androidx.room)
                alias(libs.plugins.ksp)
            }

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/roomSchemas")
            }

            kotlinMultiplatformExtension {
                sourceSets.commonMain.dependencies {
                    implementation(libs.androidx.room.runtime)
                    implementation(libs.androidx.sqlite)
                }
                dependencies {
                    // Dynamically checks supported KMP targets
                    // and applies KSP room compiled for each supported target
                    this@kotlinMultiplatformExtension.targets.all {
                        val kspConfiguration = "ksp${this.name.replaceFirstChar { it.titlecase() }}"
                        if (configurations.names.contains(kspConfiguration)) {
                            kspConfiguration(dependencyNotation = libs.androidx.room.compiler)
                        }
                    }
                }
            }
        }
    }
}