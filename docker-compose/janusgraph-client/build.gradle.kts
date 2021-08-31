/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    base
}

version = "0.5.3"

tasks {

    task<Copy>("configureJanusgraph2Client") {
        group = "compose"
        from(layout.projectDirectory.dir("src"))
        into(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
//        expand("dockerImage" to "docker.io/babeloff/janusgraph2:latest")
        expand(
            "dockerImage" to "janusgraph2",
            "dockerImageVersion" to "0.5.3",
            "schemaPath" to layout.projectDirectory.dir ("../schema"))
    }

    task<Exec>("startJanusgraph2Client") {
        dependsOn(
            ":janusgraph2:dockerJanusgraph2Build",
            ":docker-compose:janusgraph-client:configureJanusgraph2Client",
            ":docker-compose:dockerCreateJGClientVolumes")
//        logger.quiet("docker compose up task $path")
        executable = "docker"
        group = "compose"
        standardInput = System.`in`
        standardOutput = System.out

        environment("COMPOSE_PROJECT_NAME", "janusgraph-client")
        args(listOf(
            "compose",
            "-f",
            layout.buildDirectory.file("docker-compose.yaml").get().asFile.path,
            "run",
            "jg-client"
        ))
    }
}

