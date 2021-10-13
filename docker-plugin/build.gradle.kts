/**
 * https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins
 */
plugins {
    id("java-gradle-plugin")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

gradlePlugin {
    plugins {
        create("theDockerPlugin") {
            id = "the-docker-plugin"
            implementationClass = "org.janusgraph.plugin.docker.DockerPlugin"
        }
    }
}

dependencies {
    implementation("com.github.docker-java:docker-java-core:3.2.12")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.2.12")
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
    }
}
