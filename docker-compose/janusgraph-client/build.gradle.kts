/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 */

plugins {
    id("the-docker-plugin")
}

version = "2021.10.6"

tasks {

    register<Copy>("configureJanusgraphV06Client") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/phreed/janusgraph-v06:latest")
        expand(
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.6",
            "schemaPath" to layout.projectDirectory.dir ("../schema"))
    }

    register<org.janusgraph.plugin.docker.DockerComposeRunTask>("startJanusgraphV06Client") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-client:configureJanusgraphV06Client",
            ":docker-compose:createDockerVolumeJgScript",
            ":docker-compose:createDockerVolumeJgProductData",
            )
        group = "compose"
//        logger.quiet("docker compose up task $path")
        group = "compose"
        title.set("janusgraph-client")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        alias.set("jg-client")
        logger.info("$this")
    }
}

