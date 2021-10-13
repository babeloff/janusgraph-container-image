/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    base
}

version = "2021.10.6"

tasks {

    task<Copy>("configureJanusgraphV06DynamicServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/babeloff/janusgraph-v06:latest")
        expand(
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.6")
    }

    task<Exec>("downJanusgraphV06DynamicServer") {
//        logger.quiet("docker compose up task $path")
        executable = "docker"
        group = "compose"
        environment("COMPOSE_PROJECT_NAME", "janusgraph-dynamic-server")
        args(listOf(
            "compose",
            "-f",
            layout.buildDirectory.file("docker-compose.yaml").get().asFile.path,
            "down"
        ))
        logger.info("$this")
    }

    task<Exec>("upJanusgraphV06DynamicServer") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-dynamic:configureJanusgraphV06DynamicServer",
            ":docker-compose:createDockerVolumeJgCorpusData",
            ":docker-compose:createDockerVolumeJgProductData",
            ":docker-compose:createDockerVolumeJgCqlData",
            ":docker-compose:createDockerVolumeJgEsData",
        )
//        logger.quiet("docker compose up task $path")
        executable = "docker"
        group = "compose"
        environment("COMPOSE_PROJECT_NAME", "janusgraph-dynamic-server")
        args(listOf(
            "compose",
            "-f",
            layout.buildDirectory.file("docker-compose.yaml").get().asFile.path,
            "up"
        ))
        logger.info("$this")
    }
}

