/**
 * https://github.com/JetBrains/jetbrains-gradle-plugins/blob/master/docs/DOCKER.md
 *
 */

plugins {
    id("org.jetbrains.gradle.docker")
    id("com.pswidersk.yaml-secrets-plugin")
}

version = "2021.10.6"

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

    task<Exec>("stopJanusgraphV06") {
        group = "application"
        executable = "docker"
        args(listOf(
            "stop",
            containerName))
        logger.info("$this")
    }

    task<Exec>("runJanusgraphV06") {
        dependsOn("dockerJanusgraphV06Build")
        executable = "docker"
        group = "application"
        args(listOf(
            "run",
            "-d",
            "-p",
            "8080:8080",
            "--name",
            containerName,
            docker.images["janusgraphV06"].imageNameWithTag
        ))
        logger.info("$this")
    }
}

