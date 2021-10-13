/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    id("the-docker-plugin")
}

version = "2021.10.6"

tasks {

    register<Copy>("configureJanusgraphV06DynamicServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/babeloff/janusgraph-v06:latest")
        expand(
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.6")
    }

    register<org.janusgraph.plugin.docker.DockerComposeDownTask>("downJanusgraphV06DynamicServer") {
        group = "compose"
        //        logger.quiet("docker compose up task $path")
        title.set("janusgraph-dynamic-server")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }

    register<org.janusgraph.plugin.docker.DockerComposeUpTask>("upJanusgraphV06DynamicServer") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-dynamic:configureJanusgraphV06DynamicServer",
            ":docker-compose:createDockerVolumeJgCorpusData",
            ":docker-compose:createDockerVolumeJgProductData",
            ":docker-compose:createDockerVolumeJgCqlData",
            ":docker-compose:createDockerVolumeJgEsData",
        )
        group = "compose"
//        logger.quiet("docker compose up task $path")
        title.set("janusgraph-dynamic-server")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }
}

