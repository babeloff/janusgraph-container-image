
rootProject.name = "docker-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("../the-platform")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

