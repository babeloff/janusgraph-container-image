
rootProject.name = "janusgraph-docker2"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.gradle.docker").version("1.1.8")
        id("com.pswidersk.yaml-secrets-plugin").version("1.0.8")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":janusgraph-v06")
include(":docker-compose:janusgraph-client")
include(":docker-compose:janusgraph-cql-es")
include(":docker-compose:janusgraph-dynamic")
include(":docker-compose:janusgraph-inmemory")

