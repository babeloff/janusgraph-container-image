/**
 * https://github.com/JetBrains/jetbrains-gradle-plugins/blob/master/docs/DOCKER.md
 *
 */

plugins {
    id("org.jetbrains.gradle.docker")
    id("com.pswidersk.yaml-secrets-plugin")
}

version = "0.6.0"

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
        janusgraph2 {
            files {
                from(file("Dockerfile"))
                from(file("src"))
            }
            imageName = "janusgraph2" // default
            imageVersion = project.version.toString() // default
        }
    }
}

tasks {
    val containerName = "janusgraph2"

    task<Exec>("stopJanusgraph2") {
        group = "application"
        executable = "docker"
        args(listOf(
            "stop",
            containerName))
    }

    task<Exec>("runJanusgraph2") {
        dependsOn("dockerJanusgraph2Build")
        executable = "docker"
        group = "application"
        args(listOf(
            "run",
            "-d",
            "-p",
            "8080:8080",
            "--name",
            containerName,
            docker.images["janusgraph2"].imageNameWithTag
        ))
    }
}

