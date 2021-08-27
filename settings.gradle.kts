
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

include("docker-image").
include(":docker-compose:janusgraph-client")
