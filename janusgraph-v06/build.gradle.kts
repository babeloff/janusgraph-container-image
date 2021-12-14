/**
 * https://github.com/JetBrains/jetbrains-gradle-plugins/blob/master/docs/DOCKER.md
 *
 */
//import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.filters.FixCrLfFilter
import java.util.Properties

plugins {
    id("com.google.cloud.tools.jib")
    id("com.pswidersk.yaml-secrets-plugin")
    id("property-to-yaml-converter")
    id("download")
    id("application")
}

version = "2021.10.14"

val jgVersion = "0.6.0"
val yqVersion = "4.13.2"
val containerName = "janusgraph-v06"


jib {
    extraDirectories {
        paths {
            path {
                setFrom(layout.buildDirectory.dir("image"))
            }
        }

        permissions = mapOf(
            "/usr/local/bin/docker-entrypoint.sh" to "555",
            "/usr/local/bin/load-init-db.sh" to "555",
            "/usr/bin/yq" to "555",
            "/opt/janusgraph/bin/janusgraph-server.sh" to "555",
        )
    }
    from {
        image = "openjdk:11-jre-slim-bullseye"
    }
    to {
        image = "mesolab/$containerName:prod"
    }
    container {
        // nobody in nobody group
//        user = "1000:1000"

        args = listOf("janusgraph")
        creationTime = "2021-12-13T00:00:01Z"
        entrypoint = listOf("/bin/bash", "/usr/local/bin/docker-entrypoint.sh")
        labels.set(mapOf(
            "org.opencontainers.image.title" to "JanusGraph Docker Image",
            "org.opencontainers.image.description" to "UnOfficial JanusGraph Docker image",
            "org.opencontainers.image.url" to "https://janusgraph.org/",
            "org.opencontainers.image.documentation" to "https://docs.janusgraph.org/v0.6/",
//            "org.opencontainers.image.revision" to "${REVISION}",
            "org.opencontainers.image.source" to "https://github.com/JanusGraph/janusgraph-docker/",
            "org.opencontainers.image.vendor" to "JanusGraph",
            "org.opencontainers.image.version" to jgVersion,
//            "org.opencontainers.image.created" to "${CREATED}",
            "org.opencontainers.image.license" to "Apache-2.0",
        ))
        ports = listOf("8182")
        environment = mapOf(
            "JG_VERSION" to jgVersion,
            "JG_SHOW" to "env server graph",
            "JG_HOME" to "/opt/janusgraph",
            "JG_CONFIG_DIR" to "/etc/opt/janusgraph",
            "JG_DATA_DIR" to "/var/lib/janusgraph",
            "JG_SVC_TIMEOUT" to "30",
            "JG_STORAGE_TIMEOUT" to "60",
            "JG_SVC_TEMPLATE" to "janusgraph-server",
            "JG_GRAPH_TEMPLATE" to "janusgraph-cql-es-graph",
            "JG_INIT_DB_DIR" to "/docker-entrypoint-init-db.d",
            "JG_SVC__00graphProperties" to ".graphs.graph = \"/etc/opt/janusgraph/janusgraph-graph.properties\"",
            "JG_SVC__00threadPoolWorker" to ".threadPoolWorker = 1",
            "JG_SVC__00gremlinPool" to ".gremlinPool = 8",
        )
        workingDirectory = "/opt/janusgraph"
    }
}


tasks {

    register<de.undercouch.gradle.tasks.download.Download>("downloadYq") {
        group = "download"
        src("https://github.com/mikefarah/yq/releases/download/v${yqVersion}/yq_linux_amd64")
        dest(layout.buildDirectory.file("image/usr/bin/yq"))
        onlyIfModified(true)
    }

    register<de.undercouch.gradle.tasks.download.Download>("downloadJanusgraph") {
        group = "download"
        src(listOf(
            "https://github.com/JanusGraph/janusgraph/releases/download/v${jgVersion}/janusgraph-${jgVersion}.zip",
            "https://github.com/JanusGraph/janusgraph/releases/download/v${jgVersion}/janusgraph-${jgVersion}.zip.asc",
            "https://github.com/JanusGraph/janusgraph/releases/download/v${jgVersion}/KEYS"
        ))
        dest(layout.buildDirectory.dir("janusgraphZip"))
        onlyIfModified(true)
    }

    // If there are properties files create an equivalent yaml file
    register<Copy>("unzipJanusgraph") {
        dependsOn("downloadJanusgraph")
        group = "prepare"
        from(zipTree(layout.buildDirectory.file("janusgraphZip/janusgraph-${jgVersion}.zip")))
        into(layout.buildDirectory)
    }

    val ztree = layout.buildDirectory.dir("janusgraph-0.6.0")

    // For each properties files create an equivalent yaml file
    register<Copy>("prepareJanusgraph") {
        dependsOn("unzipJanusgraph")
        group = "prepare"

        from(ztree) {
            exclude("conf/*.properties")
        }
        from(layout.projectDirectory.dir("src/conf")) {
            into("conf")
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        into(layout.buildDirectory.dir("image/opt/janusgraph"))
    }

    register<org.apache.janusgraph.PropertyToYamlTask>("prepareJanusgraphProperties") {
        dependsOn("unzipJanusgraph")
        group = "prepare"
        propertiesDir.set(layout.buildDirectory.dir("janusgraph-0.6.0/conf"))
        yamlDir.set(layout.buildDirectory.dir("image/opt/janusgraph/conf"))
    }

    // For each properties file create an equivalent yaml file
    register<Copy>("prepareScripts") {
        group = "prepare"

        from(layout.projectDirectory.dir("src/scripts")) {
            include("*.sh")
            filter(FixCrLfFilter::class,
                "eol" to FixCrLfFilter.CrLf.newInstance("lf"))
        }
        into(layout.buildDirectory.dir("image/usr/local/bin"))
    }

    register("buildImage") {
        group = "build"
        dependsOn( listOf(
            "prepareJanusgraph",
            "prepareJanusgraphProperties",
            "downloadYq",
            "prepareScripts"))
    }

    jib.configure {
        dependsOn("buildImage")
    }

    jibDockerBuild.configure {
        dependsOn("buildImage")
    }

//    register<org.janusgraph.plugin.docker.DockerStopTask>("stopJanusgraphV06") {
//        group = "application"
//        title.set(containerName)
//        logger.info("$this")
//    }
//
//    register<org.janusgraph.plugin.docker.DockerRunTask>("runJanusgraphV06") {
//        dependsOn("jibDockerBuild")
//        group = "application"
//        image.set(docker.images["janusgraphV06"].imageNameWithTag)
//        title.set(containerName)
//        portSpecs.set(listOf("8080:8080"))
//        logger.info("$this")
//    }

}
