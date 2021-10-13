package org.janusgraph.plugin.docker;


import com.github.dockerjava.api.DockerClient;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

abstract public class DockerVolumeRemoveTask extends DefaultTask {

    @Input
    abstract public Property<String> getTitle();

    @TaskAction
    public void create() {
        final DockerClient client = DockerWorker.startDockerClient();
        client
                .removeVolumeCmd(getTitle().get())
                .exec();
    }
}