
rootProject.name = "janusgraph-docker2"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.palantir.docker") version "0.28.0"
        id("com.palantir.docker-run") version "0.28.0"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("docker-image")
