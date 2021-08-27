plugins {
    base
}

tasks {
    /**
     * If there is ever a docker plugin for running docker commands:
     * * create the volumes
     * * create the networks
     */

    register<Exec>("dockerCreateJGVolumeCorpus") {
        group = "docker"
        executable("docker")
        args(listOf("volume", "create", "--driver", "local", "--name=jg-corpus-data"))
    }
    register<Exec>("dockerCreateJGVolumeProduct") {
        group = "docker"
        executable("docker")
        args(listOf("volume", "create", "--driver", "local", "--name=jg-product-data"))
    }
    register<Exec>("dockerCreateJGVolumeCql") {
        group = "docker"
        executable("docker")
        args(listOf("volume", "create", "--driver", "local", "--name=jg-cql-data"))
    }
    register<Exec>("dockerCreateJGVolumeEs") {
        group = "docker"
        executable("docker")
        args(listOf("volume", "create", "--driver", "local", "--name=jg-es-data"))
    }
    register<Exec>("dockerCreateJGVolumeScripts") {
        group = "docker"
        executable("docker")
        args(listOf("volume", "create", "--driver", "local", "--name=jg-scripts"))
    }
    register("dockerCreateJGServerVolumes") {
        group = "docker"
        dependsOn("dockerCreateJGVolumeCorpus",
            "dockerCreateJGVolumeProduct",
            "dockerCreateJGVolumeCql",
            "dockerCreateJGVolumeEs"
        )
    }
    register("dockerCreateJGClientVolumes") {
        group = "docker"
        dependsOn("dockerCreateJGVolumeScripts",
            "dockerCreateJGVolumeProduct")
    }
    register("dockerCreateJGVolumes") {
        group = "docker"
        dependsOn("dockerCreateJGClientVolumes",
            "dockerCreateJGServerVolumes",
        )
    }
}