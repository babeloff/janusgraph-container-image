/**
 * https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins
 */
plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(group="org.yaml", name="snakeyaml", version="1.29")
}