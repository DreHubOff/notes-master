import com.andres.notes.master.kotlinMultiplatformExtension
import com.andres.notes.master.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension as kotlinBaseExtension

class SuppressWarningsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(kotlinBaseExtension) {
                sourceSets.all {
                    languageSettings {
                        optIn("kotlin.time.ExperimentalTime")
                        optIn("kotlinx.cinterop.ExperimentalForeignApi")
                    }
                }
            }
            pluginManager.withPlugin(libs.plugins.kotlin.multiplatform.get().pluginId) {
                kotlinMultiplatformExtension {
                    compilerOptions {
                        freeCompilerArgs.add("-Xexpect-actual-classes")
                    }
                }
            }
        }
    }
}