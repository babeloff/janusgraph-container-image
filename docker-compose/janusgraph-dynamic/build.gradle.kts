/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    id("annex-docker-plugin")
}

version = "2021.10.14"

tasks {

    register<Copy>("configureJanusgraphV06DynamicServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        expand(
            "dockerImage" to "janusgraph-v06",
//            "dockerImage" to "docker.io/mesolab/janusgraph-v06",
            "dockerImageVersion" to "2021.10.14")
    }

    val composeTitle = "janusgraph-dynamic-server"

    register<org.janusgraph.plugin.docker.DockerComposeDownTask>("downJanusgraphV06DynamicServer") {
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }

    register<org.janusgraph.plugin.docker.DockerComposeUpTask>("upJanusgraphV06DynamicServer") {
        dependsOn(
//            ":janusgraph-v06:jibDockerBuild",
            ":docker-compose:janusgraph-dynamic:configureJanusgraphV06DynamicServer",
            ":docker-compose:createDockerVolumeJgCorpusData",
            ":docker-compose:createDockerVolumeJgProductData",
            ":docker-compose:createDockerVolumeJgCqlData",
            ":docker-compose:createDockerVolumeJgEsData",
        )
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }
}

