
rootProject.name = "annex-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("de.undercouch.download") version "4.1.2"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}