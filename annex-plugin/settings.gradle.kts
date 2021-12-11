
rootProject.name = "annex-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.5.21"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}