import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class SuppressWarningsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(kotlinExtension) {
                sourceSets.all {
                    languageSettings {
                        optIn("kotlin.time.ExperimentalTime")
                    }
                }
            }
        }
    }
}