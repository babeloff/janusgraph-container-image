/**
 * https://tomgregory.com/automating-docker-builds-with-gradle/
 *
 */

plugins {
    base
}

version = "0.5.3"

dependencies {
//    docker (project(":docker-image"))
}
//
//dockerCompose {
//    logger.info("docker extension configured with ${template.path} and ${dockerComposeFile.path}")
//}
//
//tasks {
//    named<com.palantir.gradle.docker.DockerComposeUp>("dockerComposeUp") {
//        logger.quiet("docker compose up task ${path}")
//    }
//}
//
