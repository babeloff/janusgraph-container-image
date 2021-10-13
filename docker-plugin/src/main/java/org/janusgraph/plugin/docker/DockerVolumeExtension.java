package org.janusgraph.plugin.docker;

import org.gradle.api.provider.Property;

abstract public class DockerVolumeExtension {
    private final String name;

    @javax.inject.Inject
    public DockerVolumeExtension(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract public Property<String> getTitle();
}