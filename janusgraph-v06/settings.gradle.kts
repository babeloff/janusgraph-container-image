@file:Suppress("REDUNDANT_LABEL_WARNING","INCUBATING_WARNING")

rootProject.name = "janusgraph-v06"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.gradle.docker") version("1.2.2")
        id("com.pswidersk.yaml-secrets-plugin") version("1.1.0")
    }
    includeBuild("../annex-docker-plugin")
}

