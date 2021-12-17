
// https://docs.gradle.org/current/userguide/java_platform_plugin.html
plugins {
    id("java-platform")
}
group = "edu.vanderbilt.metalab"

// Constraints for Gradle plugins
dependencies.constraints {
//    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")

    api("org.apache.logging.log4j:log4j-core:2.15.0")
}
