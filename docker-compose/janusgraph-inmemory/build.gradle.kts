/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    base
}

version = "2021.10.6"

tasks {

    register<Copy>("configureJanusgraphV06MemoryServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/babeloff/janusgraph-v06:latest")
        expand(
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.6")
    }

    register<org.janusgraph.plugin.docker.DockerComposeDownTask>("downJanusgraphV06MemoryServer") {
        group = "compose"
//        logger.quiet("docker compose up task $path")
        title.set("janusgraph-inmemory-server")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }

    register<org.janusgraph.plugin.docker.DockerComposeUpTask>("upJanusgraphV06MemoryServer") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-inmemory:configureJanusgraphV06MemoryServer")
        group = "compose"
//        logger.quiet("docker compose up task $path")
        title.set("janusgraph-inmemory-server")
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }
}

