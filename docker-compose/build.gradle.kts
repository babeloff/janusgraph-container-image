plugins {
    base
}

tasks {
    /**
     * If there is ever a docker plugin for running docker commands:
     * * create the volumes
     * * create the networks
     */
    val volumes = listOf(
        mapOf("name" to "jg-corpus-data", "task" to "Corpus"),
        mapOf("name" to "jg-product-data", "task" to "Product"),
        mapOf("name" to "jg-cql-data", "task" to "Cql"),
        mapOf("name" to "jg-es-data", "task" to "Es"),
        mapOf("name" to "jg-scripts", "task" to "Script"))

    volumes.forEach {
        register<Exec>("dockerCreateJGVolume${it["task"]}") {
            group = "docker"
            executable("docker")
            args(listOf("volume", "create", "--driver", "local", "--name=${it["name"]}"))
            logger.info("$this")
        }
        register<Exec>("dockerRemoveJGVolume${it["task"]}") {
            group = "docker"
            executable("docker")
            args(listOf("volume", "rm", "${it["name"]}"))
            logger.info("$this")
        }
    }
}