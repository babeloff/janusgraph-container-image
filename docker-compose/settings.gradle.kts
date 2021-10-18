
rootProject.name = "docker-compose"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.gradle.docker").version("1.1.6")
        id("com.pswidersk.yaml-secrets-plugin").version("1.0.8")
    }
    includeBuild("../annex-docker-plugin")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    includeBuild("../annex-platform")
    includeBuild(".") // self include for backward compatibility
}

//include("doc")
include(":janusgraph-util")

include(":janusgraph-client")
include(":janusgraph-cql-es")
include(":janusgraph-dynamic")
include(":janusgraph-inmemory")
