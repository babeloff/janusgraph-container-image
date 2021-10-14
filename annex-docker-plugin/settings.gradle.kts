
rootProject.name = "annex-docker-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("../annex-platform")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

