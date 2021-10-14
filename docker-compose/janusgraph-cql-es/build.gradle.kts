/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    id("the-docker-plugin")
}

version = "2021.10.14"

tasks {

    task<Copy>("configureJanusgraphV06CqlEsServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/babeloff/janusgraph-v06:latest")
        expand(
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.6")
    }

    task<org.janusgraph.plugin.docker.DockerComposeDownTask>("downJanusgraphV06CqlEsServer") {
        group = "compose"
//        logger.quiet("docker compose up task $path")
        title.set("janusgraph-cql-es-server")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }

    task<org.janusgraph.plugin.docker.DockerComposeUpTask>("upJanusgraphV06CqlEsServer") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-cql-es:configureJanusgraphV06CqlEsServer",
            ":docker-compose:createDockerVolumeJgCorpusData",
            ":docker-compose:createDockerVolumeJgProductData",
            ":docker-compose:createDockerVolumeJgCqlData",
            ":docker-compose:createDockerVolumeJgEsData")
        group = "compose"
//        logger.quiet("docker compose up task $path")
        title.set("janusgraph-cql-es-server")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }
}

