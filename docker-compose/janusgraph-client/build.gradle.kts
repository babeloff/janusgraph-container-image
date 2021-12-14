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
        expand(
//            "dockerImage" to "docker.io/mesolab/janusgraph-v06",
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.14",
            "schemaPath" to layout.projectDirectory.dir ("../schema"))
    }

    val composeTitle = "janusgraph-client"

    register<org.janusgraph.plugin.docker.DockerComposeRunTask>("startJanusgraphV06Client") {
        dependsOn(
//            ":janusgraph-v06:jibDockerBuild",
            ":docker-compose:janusgraph-client:configureJanusgraphV06Client",
            ":docker-compose:createDockerVolumeJgScript",
            ":docker-compose:createDockerVolumeJgProductData",
            )
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        alias.set("jg-client")
        logger.info("$this")
    }
}

