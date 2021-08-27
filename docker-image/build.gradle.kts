/**
 * https://github.com/JetBrains/jetbrains-gradle-plugins/blob/master/docs/DOCKER.md
 *
 */

plugins {
    id("org.jetbrains.gradle.docker")
}

version = "0.5.3"

docker {

    if (System.getenv("USE_DOCKER_REST") == "true")
        useDockerRestApi()

    registries {

        val username = System.getenv("CONTAINER_REGISTRY_USERNAME")
            ?: extra.properties["CONTAINER_REGISTRY_USERNAME"] as? String
        val password = System.getenv("CONTAINER_REGISTRY_SECRET")
            ?: extra.properties["CONTAINER_REGISTRY_SECRET"] as? String

        if (username != null && password != null)
            create("testRegistry") {
                this.username = username
                this.password = password
                url = System.getenv("CONTAINER_REGISTRY_URL")
                    ?: extra.properties["CONTAINER_REGISTRY_URL"] as? String
                            ?: error("Container registry url not defined in env")
                imageNamePrefix = System.getenv("CONTAINER_REGISTRY_IMAGE_PREFIX")
                    ?: extra.properties["CONTAINER_REGISTRY_IMAGE_PREFIX"] as? String
                            ?: error("Container registry image prefix not defined in env")
            }
    }
    images {
        janusgraph2 {
            setupJvmApp(org.jetbrains.gradle.plugins.docker.JvmBaseImages.OpenJDK11Slim)
        }
        register("janusgraph2") {
            files {
                from(file("Dockerfile"))
                from(file("src"))
            }
//            buildArgs = mutableMapOf("MY_ENV_VAR" to System.getenv("MY_ENV_VAR"))
            imageName = "janusgraph2" // default
            imageVersion = project.version.toString() // default
        }
    }
}

tasks {
    val containerName = "janusgraph2"

    named<org.jetbrains.gradle.plugins.docker.tasks.DockerExecBuild>("dockerDockerImageBuild") {
        val foo = this
    }
//    task<Exec>("stopImage") {
//        group = "application"
//        executable = "docker"
//        args(listOf("stop", containerName))
//    }

//    task<Exec>("runImage") {
//        dependsOn("janusgraph2")
//        executable = "docker"
//        group = "application"
//        args(listOf(
//            "run",
//            "-d",
//            "-p",
//            "8080:8080",
//            "--name",
//            containerName,
//            docker.images["janusgraph2"].imageNameWithTag
//        ))
//    }
}

