/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    id("com.palantir.docker")
    id("com.palantir.docker-run")
}

version = "0.5.3"

docker {
    name = "project.name"
    setDockerfile(file("DockerFile"))
    tag("DockerHub", "phreed/janusgraph-docker2:${project.version}")
    labels(mapOf("implementation" to "janusgraph", "api" to "tinkerpop3"))
}

dockerRun {
    name = "${project.name}"
    image = "${project.name}:${project.version}"
    ports("8080:8080")
    clean = true
}

