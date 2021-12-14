/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    id("annex-docker-plugin")
}

version = "2021.10.14"

tasks {

    register<Copy>("configureJanusgraphV06MemoryServer") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        expand(
//        "dockerImage" to "docker.io/mesolab/janusgraph-v06",
            "dockerImage" to "janusgraph-v06",
            "dockerImageVersion" to "2021.10.14")
    }

    val composeTitle = "janusgraph-inmemory-server"

    register<org.janusgraph.plugin.docker.DockerComposeDownTask>("downJanusgraphV06MemoryServer") {
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }

    register<org.janusgraph.plugin.docker.DockerComposeUpTask>("upJanusgraphV06MemoryServer") {
        dependsOn(
//            ":janusgraph-v06:jibDockerBuild",
            ":docker-compose:janusgraph-inmemory:configureJanusgraphV06MemoryServer")
        group = "compose"
        title.set(composeTitle)
        yaml.set(layout.buildDirectory.file("docker-compose.yaml"))
        logger.info("$this")
    }
}

