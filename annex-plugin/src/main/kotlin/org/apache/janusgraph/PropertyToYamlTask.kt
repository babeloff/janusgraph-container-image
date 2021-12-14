package org.apache.janusgraph

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.util.*

//val inputStream = FileInputStream(File("C:\\Users\\fred\\IdeaProjects\\jci-a\\janusgraph-v06\\build\\janusgraph-0.6.0\\conf\\gremlin-server\\gremlin-server.yaml"));
//val yaml =  Yaml()
//val data: Map<String, Any> = yaml.load(inputStream)
//println(data)

/**
 * This task is used to generate copyright files
 * used as input to the code generators.
 * - StringTemplate Group files: *_copyrights.stg
 * - C99/C++ header files: copyrights.h
 */
@CacheableTask
abstract class PropertyToYamlTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val propertiesDir: DirectoryProperty

    @get:OutputDirectory
    abstract val yamlDir: DirectoryProperty

    @TaskAction
    fun convert() {
        val rawFileTree = propertiesDir.get().asFileTree
        if (rawFileTree.isEmpty) {
            logger.error("empty properties file tree")
            return
        }
        // presume the fileTree contains only properties files
        rawFileTree.visit {
            val propertyFile = this.file
            if (! propertyFile.isFile) {
                logger.debug("not a file $propertyFile")
                return@visit
            }
            val fileName = this.file.name
            if (!fileName.endsWith(".properties", true) ) {
                logger.debug("not a properties file $propertyFile")
                return@visit
            } else {
                logger.debug("is a properties file $propertyFile")
            }
            val pFis = FileInputStream(propertyFile)
            val props = Properties()
            props.load(pFis)
            val tree = mutableMapOf<String,Any>()
            val propPath = props.stringPropertyNames().toMutableList()

            logger.info("file: $propertyFile")
            propPath.forEach { propKey ->
                logger.debug("name: $propKey")
                val levels = propKey.split(".").toMutableList()
                val leafLevel = levels.removeLastOrNull()
                if (leafLevel == null) {
                    logger.warn("empty property name")
                    return@forEach
                }
                var branch = tree
                for (level in levels) {
                    if (level in branch) {
                        val next = branch[level]
                        if (next !is Map<*, *>) {
                            logger.warn("$next is not a map")
                            return@forEach
                        }
                        @Suppress("UNCHECKED_CAST")
                        branch = next as MutableMap<String,Any>
                    } else {
                        val next = mutableMapOf<String,Any>()
                        branch[level] = next
                        branch = next
                    }
                }
                branch[leafLevel] = props.getProperty(propKey)
            }

            val fileProvider = yamlDir.file(fileName.removeSuffix(".properties").plus(".yaml"))
            val writer = FileWriter(fileProvider.get().asFile)
            val yaml = Yaml()
            yaml.dump(tree, writer)
            logger.info("yaml: ${fileProvider.get().asFile}")
        }
    }
}