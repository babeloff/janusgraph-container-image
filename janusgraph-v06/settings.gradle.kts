@file:Suppress("REDUNDANT_LABEL_WARNING","INCUBATING_WARNING")

rootProject.name = "janusgraph-v06"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.gradle.docker") version("1.2.2")
        id("com.pswidersk.yaml-secrets-plugin") version("1.1.0")
        id("de.undercouch.download") version "4.1.2"
        id("com.google.cloud.tools.jib") version "3.1.4"
    }
    includeBuild("../annex-docker-plugin")
//    includeBuild("../annex-plugin")
}

