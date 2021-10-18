/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    id("annex-docker-plugin")
}

version = "2021.10.14"

tasks {

    register<Copy>("configureJanusgraphV06CqlEsServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        expand(
            "dockerImage" to "janusgraph-v06",
//            "dockerImage" to "docker.io/mesolab/janusgraph-v06",
            "dockerImageVersion" to "2021.10.14")
    }

    val composeTitle = "janusgraph-cql-es-server"

    register<org.janusgraph.plugin.docker.DockerComposeDownTask>("downJanusgraphV06CqlEsServer") {
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }

    register<org.janusgraph.plugin.docker.DockerComposeUpTask>("upJanusgraphV06CqlEsServer") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":janusgraph-cql-es:configureJanusgraphV06CqlEsServer",
            ":janusgraph-util:createDockerNetworkJgBridgeNetwork",
            ":janusgraph-util:createDockerVolumeJgCorpusData",
            ":janusgraph-util:createDockerVolumeJgProductData",
            ":janusgraph-util:createDockerVolumeJgCqlData",
            ":janusgraph-util:createDockerVolumeJgEsData",
        )
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }
}

