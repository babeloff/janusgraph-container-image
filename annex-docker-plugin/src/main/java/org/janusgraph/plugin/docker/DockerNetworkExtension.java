package org.janusgraph.plugin.docker;

import org.gradle.api.provider.Property;

abstract public class DockerNetworkExtension {
    private final String name;

    @javax.inject.Inject
    public DockerNetworkExtension(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract public Property<String> getTitle();
}