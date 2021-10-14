package org.janusgraph.plugin.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.model.Network;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.*;

abstract public class DockerNetworkCreateTask extends DefaultTask {

    @Input
    abstract public Property<String> getTitle();

    @Optional
    @OutputFile
    abstract public RegularFileProperty getStatusFile();

    @TaskAction
    public void create() {
        final DockerClient client = DockerWorker.startDockerClient();
        CreateNetworkResponse rc =  client
                .createNetworkCmd()
                .withName(getTitle().get())
                .withDriver("bridge")
                .exec();

        if (rc.getId() == null) {
            getLogger().error("could not create bridge network");
            return;
        }
        Network network = client.inspectNetworkCmd()
                .withNetworkId(rc.getId())
                .exec();

        File statusFile = getStatusFile().get().getAsFile();
        try (Writer writer = new BufferedWriter(new FileWriter(statusFile))) {
            writer.write("id: ");
            writer.write(network.getId());
            writer.write("\n");

            writer.write("name: ");
            writer.write(network.getName());
            writer.write("\n");

            writer.write("driver: ");
            writer.write(network.getDriver());
            writer.write("\n");

            writer.write("scope: ");
            writer.write(network.getScope());
            writer.write("\n");
        } catch (IOException ex) {
            getLogger().error("could not create network");
        }
    }
}
