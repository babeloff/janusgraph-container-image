package org.janusgraph.plugin.docker;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
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
abstract public class DockerStopTask extends DefaultTask {

    @Input
    abstract public Property<String> getTitle();

    @TaskAction
    public void create() {
        final DockerClient client = DockerWorker
                .startDockerClient();
        try {
            client
                    .stopContainerCmd(getTitle().get())
                    .exec();
        } catch (NotFoundException ex) {
            getLogger().error("could not stop running container: not found");
            return;
        } catch (NotModifiedException ex) {
            getLogger().error("could not modify running container");
            return;
        }

    }
}
