/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 */

plugins {
    id("annex-docker-plugin")
}

version = "2021.10.14"

tasks {

    register<Copy>("configureJanusgraphV06Client") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/phreed/janusgraph-v06:latest")
        expand(
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.14",
            "schemaPath" to layout.projectDirectory.dir ("../schema"))
    }

    val composeTitle = "janusgraph-client"

    register<DockerComposeRunTask>("startJanusgraphV06Client") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-client:configureJanusgraphV06Client",
            ":docker-compose:createDockerVolumeJgScript",
            ":docker-compose:createDockerVolumeJgProductData",
            )
        group = "compose"
        title.set(composeTitle)
        alias.set("jg-client")
        yaml.set(layout.buildDirectory.file("docker-compose-jg-corpus.yaml"))
        logger.info("$this")
    }
}

