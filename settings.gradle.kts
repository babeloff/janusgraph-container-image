
rootProject.name = "janusgraph-docker2"


pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("annex-platform")
    includeBuild("annex-docker-plugin")
}

includeBuild("docker-compose")
