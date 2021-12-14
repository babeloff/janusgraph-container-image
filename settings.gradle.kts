
rootProject.name = "janusgraph-docker2"


pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("annex-platform")
//    includeBuild("annex-plugin")
    includeBuild("annex-docker-plugin")
}

includeBuild("docker-compose")
includeBuild("janusgraph-v06")
