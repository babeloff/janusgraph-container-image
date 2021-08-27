
rootProject.name = "janusgraph-docker2"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.gradle.docker") version "1.1.6"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":janusgraph2")
include(":docker-compose:janusgraph-client")
include(":docker-compose:janusgraph-dynamic")
