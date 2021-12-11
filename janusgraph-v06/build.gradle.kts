/**
 * https://github.com/JetBrains/jetbrains-gradle-plugins/blob/master/docs/DOCKER.md
 *
 */

plugins {
    id("de.undercouch.download") version "4.1.2"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("com.pswidersk.yaml-secrets-plugin")
//    id("property-to-yaml-converter")
    id("application")
}

version = "2021.10.14"

jib {
    extraDirectories.setPaths(file("build/yq"))
    from {
        image = "openjdk:8-jre-slim-buster"
    }
    to {
        image = "janusgraph-container"
    }
}

val jgVersion = "0.6.0"
val yqVersion = "4.13.2"

tasks {
    val containerName = "janusgraph-v06"

    register<de.undercouch.gradle.tasks.download.Download>("getYq") {
        group = "download"
        src("https://github.com/mikefarah/yq/releases/download/v${yqVersion}/yq_linux_amd64")
        dest(layout.buildDirectory.dir("yq"))
        onlyIfModified(true)
    }

    register<Copy>("prepareYq") {
        dependsOn("getYq")
        group = "prepare"
        from( zipTree(layout.buildDirectory.file("janusgraphRaw/janusgraph-${jgVersion}.zip")))
        into(layout.buildDirectory.dir("opt/janusgraph"))
    }

    register<de.undercouch.gradle.tasks.download.Download>("getJanusGraph") {
        group = "download"
        src(listOf(
            "https://github.com/JanusGraph/janusgraph/releases/download/v${jgVersion}/janusgraph-${jgVersion}.zip",
            "https://github.com/JanusGraph/janusgraph/releases/download/v${jgVersion}/janusgraph-${jgVersion}.zip.asc",
            "https://github.com/JanusGraph/janusgraph/releases/download/v${jgVersion}/KEYS"
        ))
        dest(layout.buildDirectory.dir("janusgraphRaw"))
        onlyIfModified(true)
    }

    // If there are properties files create an equivalent yaml file
    register<Copy>("prepareJanusgraph") {
        dependsOn("getJanusGraph")
        group = "prepare"
        from(zipTree(layout.buildDirectory.file("janusgraphRaw/janusgraph-${jgVersion}.zip")))
        into(layout.buildDirectory.dir("opt/janusgraph"))
    }

//    register<org.janusgraph.plugin.docker.DockerStopTask>("stopJanusgraphV06") {
//        group = "application"
//        title.set(containerName)
//        logger.info("$this")
//    }
//
//    register<org.janusgraph.plugin.docker.DockerRunTask>("runJanusgraphV06") {
//        dependsOn("dockerJanusgraphV06Build")
//        group = "application"
//        image.set(docker.images["janusgraphV06"].imageNameWithTag)
//        title.set(containerName)
//        portSpecs.set(listOf("8080:8080"))
//        logger.info("$this")
//    }
}
