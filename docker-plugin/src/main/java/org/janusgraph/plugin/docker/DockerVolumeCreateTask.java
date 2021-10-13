package org.janusgraph.plugin.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.model.Network;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.util.Map;

abstract public class DockerVolumeCreateTask extends DefaultTask {

    @Input
    abstract public Property<String> getTitle();

    @Optional
    @OutputFile
    public abstract RegularFileProperty getStatusFile();

    @TaskAction
    public void create() {
        final DockerClient client = DockerWorker.startDockerClient();
        CreateVolumeResponse rc = client
                .createVolumeCmd()
                .withName(getTitle().get())
                .withDriver("local")
                .exec();

        if (rc == null || rc.getName() == null) {
            getLogger().error("could not create local volume");
            return;
        }
        InspectVolumeResponse volumeInfo = client
                .inspectVolumeCmd(rc.getName())
                .exec();

        File statusFile = getStatusFile().get().getAsFile();
        try (Writer writer = new BufferedWriter(new FileWriter(statusFile))) {
            writer.write("name: ");
            writer.write(volumeInfo.getName());
            writer.write("\n");

            writer.write("mount: ");
            writer.write(volumeInfo.getMountpoint());
            writer.write("\n");

            writer.write("driver: ");
            writer.write(volumeInfo.getDriver());
            writer.write("\n");

            if (volumeInfo.getLabels() != null) {
                writer.write("labels: ");
                for (Map.Entry<String, String> entry : volumeInfo.getLabels().entrySet()) {
                    writer.write("  ");
                    writer.write(entry.getKey());
                    writer.write(": ");
                    writer.write(entry.getValue());
                    writer.write("\n");
                }
            }
        } catch (IOException ex) {
            getLogger().error("could not open status file");
        }
    }

}
