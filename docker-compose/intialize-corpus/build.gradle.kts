/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 */

plugins {
    base
}

version = "2021.10.6"

tasks {

    task<Copy>("configureJanusgraphV06Client") {
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

    task<Exec>("startJanusgraphV06Client") {
        dependsOn(
            ":janusgraph-v06:dockerJanusgraphV06Build",
            ":docker-compose:janusgraph-client:configureJanusgraphV06Client",
            ":docker-compose:dockerCreateJGVolumeScript",
            ":docker-compose:dockerCreateJGVolumeProduct",
            )
//        logger.quiet("docker compose up task $path")
        executable = "docker"
        group = "compose"
        standardInput = System.`in`
        standardOutput = System.out

        environment("COMPOSE_PROJECT_NAME", "janusgraph-client")
        args(listOf(
            "compose",
            "-f",
            layout.buildDirectory.file("docker-compose-jg-corpus.yaml").get().asFile.path,
            "run",
            "--rm",
            "jg-client"
        ))
        logger.info("$this")
    }
}

