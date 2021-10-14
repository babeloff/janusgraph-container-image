/**
 * https://github.com/JetBrains/jetbrains-gradle-plugins/blob/master/docs/DOCKER.md
 *
 */

plugins {
    id("org.jetbrains.gradle.docker")
    id("com.pswidersk.yaml-secrets-plugin")
    id("annex-docker-plugin")
}

version = "2021.10.14"

docker {

    if (System.getenv("USE_DOCKER_REST") == "true")
        useDockerRestApi()

    registries {
        dockerHub {
            username = secrets.get<String?>("dockerhub","registry.username")
                ?: System.getenv("CONTAINER_REGISTRY_USERNAME")
                ?: extra.properties["CONTAINER_REGISTRY_USERNAME"] as? String
                ?: error("Container registry username is not defined")
            password = secrets.get<String?>("dockerhub","registry.password")
                ?: System.getenv("CONTAINER_REGISTRY_PASSWORD")
                ?: extra.properties["CONTAINER_REGISTRY_PASSWORD"] as? String
                ?: error("Container registry password is not defined")
        }
    }
    images {
        // project image
        this.named("janusgraphV06") {
            files {
                from(file("Dockerfile"))
                from(file("src"))
            }
            imageName = "janusgraph-v06" // default
            imageVersion = project.version.toString() // default
        }
    }
}

tasks {
    val containerName = "janusgraph-v06"

    register<org.janusgraph.plugin.docker.DockerStopTask>("stopJanusgraphV06") {
        group = "application"
        title.set(containerName)
        logger.info("$this")
    }

    register<org.janusgraph.plugin.docker.DockerRunTask>("runJanusgraphV06") {
        dependsOn("dockerJanusgraphV06Build")
        group = "application"
        image.set(docker.images["janusgraphV06"].imageNameWithTag)
        title.set(containerName)
        portSpecs.set(listOf("8080:8080"))
        logger.info("$this")
    }
}

