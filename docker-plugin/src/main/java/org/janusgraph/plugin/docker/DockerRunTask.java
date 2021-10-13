package org.janusgraph.plugin.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.*;

/**
 * The docker run command first creates a writeable container layer over the specified image,
 * and then starts it using the specified command.
 * That is, docker run is equivalent to the API /containers/create then /containers/(id)/start.
 * A stopped container can be restarted with all its previous changes intact using docker start.
 * @see <a href="https://docs.docker.com/engine/reference/run/">Docker Run Reference</a>
 * @see <a href="https://docs.docker.com/engine/reference/commandline/run/">Docker Run</a>
 */
abstract public class DockerRunTask extends DefaultTask {

    @Input
    abstract public Property<String> getTitle();

    @Input
    abstract public Property<String> getImage();

    @Optional
    @Input
    abstract public ListProperty<String> getPortSpecs();

    @Optional
    @OutputFile
    abstract public RegularFileProperty getStatusFile();

    @TaskAction
    public void create() {
        final DockerClient client = DockerWorker
                .startDockerClient();

        CreateContainerResponse resp = client
                .createContainerCmd(getImage().get())
                .withName(getTitle().get())
                .withPortSpecs(getPortSpecs().get())
                .exec();

        if (resp.getId() == null) {
            getLogger().error("could not create running container");
            return;
        }
        InspectContainerResponse icr = client
                .inspectContainerCmd(resp.getId())
                .exec();
        File statusFile = getStatusFile().get().getAsFile();
        try (Writer writer = new BufferedWriter(new FileWriter(statusFile))) {
            writer.write("id: ");
            writer.write(icr.getId());
            writer.write("\n");

            writer.write("name: ");
            writer.write(icr.getName());
            writer.write("\n");

            writer.write("driver: ");
            writer.write(icr.getDriver());
            writer.write("\n");

            writer.write("image: ");
            writer.write(icr.getImageId());
            writer.write("\n");
        } catch (IOException ex) {
            getLogger().error("could not run container");
        }
    }
}
