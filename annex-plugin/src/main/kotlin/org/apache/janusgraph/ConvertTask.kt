package org.apache.janusgraph

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.StringWriter
import java.util.*

/**
 * This task is used to generate copyright files
 * used as input to the code generators.
 * - StringTemplate Group files: *_copyrights.stg
 * - C99/C++ header files: copyrights.h
 */
@CacheableTask
abstract class ConvertTask : DefaultTask() {

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
//            val fileName = this.file.name
            val pfis = FileInputStream(propertyFile)
            val props = Properties().apply { load(pfis) }
            props.propertyNames().apply {
                logger.quiet("name: $this")
            }
            val yaml = Yaml()
            val writer = StringWriter()
            yaml.dump(props, writer)
            logger.quiet("yaml: $writer")
        }

    }
}