package org.janusgraph.plugin.docker;

import org.gradle.api.*;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;

public class DockerPlugin implements Plugin<Project> {

    private String capitalize(final String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    @Override
    public void apply(final Project project) {
        ObjectFactory objects = project.getObjects();
        applyVolumes(project, objects);
        applyNetworks(project, objects);
    }

    private void applyVolumes(final Project project, final ObjectFactory objects) {
        NamedDomainObjectFactory<DockerVolumeExtension> factory = name -> {
            Object dve = objects.newInstance(DockerVolumeExtension.class, name);
            if (dve instanceof DockerVolumeExtension) {
                return (DockerVolumeExtension) dve;
            }
            return null;
        };
        NamedDomainObjectContainer<DockerVolumeExtension> dockerVolumeContainer =
                objects.domainObjectContainer(DockerVolumeExtension.class, factory);

        project.getExtensions().add("dockerVolumes", dockerVolumeContainer);

        dockerVolumeContainer.all(dockerVolume -> {
            String taskName = "createDockerVolume" + capitalize(dockerVolume.getName());
            Action<? super DockerVolumeCreateTask> configurationAction = task -> {
                task.getTitle().set(dockerVolume.getTitle());

                Provider<RegularFile> statusFile = project.getLayout().getBuildDirectory()
                        .file("docker-volume-status/" + taskName + ".yaml");
                task.getStatusFile().convention(statusFile);
            };
            TaskContainer tc = project.getTasks();
            Class<DockerVolumeCreateTask> clazz = DockerVolumeCreateTask.class;
            try {
                TaskProvider<DockerVolumeCreateTask> provider =
                        tc.register(taskName, clazz, configurationAction);
                provider.get().setGroup("docker");
            } catch (InvalidUserDataException ex) {

            }
        });


        dockerVolumeContainer.all(dockerVolume -> {
            String taskName = "removeDockerVolume" + capitalize(dockerVolume.getName());
            Action<? super DockerVolumeRemoveTask> configurationAction = task -> {
                task.getTitle().set(dockerVolume.getTitle());
            };
            TaskContainer tc = project.getTasks();
            Class<DockerVolumeRemoveTask> clazz = DockerVolumeRemoveTask.class;
            try {
                TaskProvider<DockerVolumeRemoveTask> provider =
                        tc.register(taskName, clazz, configurationAction);
                provider.get().setGroup("docker");
            } catch (InvalidUserDataException ex) {

            }
        });
    }

    private void applyNetworks(final Project project, final ObjectFactory objects) {
        NamedDomainObjectFactory<DockerNetworkExtension> factory = name -> {
            Object dve = objects.newInstance(DockerNetworkExtension.class, name);
            if (dve instanceof DockerNetworkExtension) {
                return (DockerNetworkExtension) dve;
            }
            return null;
        };
        NamedDomainObjectContainer<DockerNetworkExtension> dockerNetworkContainer =
                objects.domainObjectContainer(DockerNetworkExtension.class, factory);

        project.getExtensions().add("dockerNetworks", dockerNetworkContainer);

        dockerNetworkContainer.all(dockerNetwork -> {
            String taskName = "createDockerNetwork" + capitalize(dockerNetwork.getName());
            Action<? super DockerNetworkCreateTask> configurationAction = task -> {
                task.getTitle().set(dockerNetwork.getTitle());
                Provider<RegularFile> statusFile = project.getLayout().getBuildDirectory()
                                .file("docker-network-status/" + taskName + ".yaml");
                task.getStatusFile().convention(statusFile);
            };
            TaskContainer tc = project.getTasks();
            Class<DockerNetworkCreateTask> clazz = DockerNetworkCreateTask.class;
            try {
                TaskProvider<DockerNetworkCreateTask> provider =
                        tc.register(taskName, clazz, configurationAction);
                provider.get().setGroup("docker");
            } catch (InvalidUserDataException ex) {

            }
        });


        dockerNetworkContainer.all(dockerVolume -> {
            String taskName = "removeDockerNetwork" + capitalize(dockerVolume.getName());
            Action<? super DockerNetworkRemoveTask> configurationAction = task -> {
                task.getTitle().set(dockerVolume.getTitle());
            };
            TaskContainer tc = project.getTasks();
            Class<DockerNetworkRemoveTask> clazz = DockerNetworkRemoveTask.class;
            try {
                TaskProvider<DockerNetworkRemoveTask> provider =
                        tc.register(taskName, clazz, configurationAction);
                provider.get().setGroup("docker");
            } catch (InvalidUserDataException ex) {

            }
        });
    }
}
