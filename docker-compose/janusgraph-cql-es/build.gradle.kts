/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    base
}

version = "2021.10.6"

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

    task<Exec>("downJanusgraphV06CqlEsServer") {
//        logger.quiet("docker compose up task $path")
        executable = "docker"
        group = "compose"
        environment("COMPOSE_PROJECT_NAME", "janusgraph-cql-es-server")
        args(listOf(
            "compose",
            "-f",
            layout.buildDirectory.file("docker-compose.yaml").get().asFile.path,
            "down"
        ))
        logger.info("$this")
    }

    task<Exec>("upJanusgraphV06CqlEsServer") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-cql-es:configureJanusgraphV06CqlEsServer",
            ":docker-compose:createDockerVolumeJgCorpusData",
            ":docker-compose:createDockerVolumeJgProductData",
            ":docker-compose:createDockerVolumeJgCqlData",
            ":docker-compose:createDockerVolumeJgEsData")
//        logger.quiet("docker compose up task $path")
        executable = "docker"
        group = "compose"
        environment("COMPOSE_PROJECT_NAME", "janusgraph-cql-es-server")
        args(listOf(
            "compose",
            "-f",
            layout.buildDirectory.file("docker-compose.yaml").get().asFile.path,
            "up"
        ))
        logger.info("$this")
    }
}

