package org.janusgraph.plugin.docker;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class DockerComposeRunTask extends DefaultTask {

    @Input
    abstract public Property<String> getTitle();

    @Input
    abstract public Property<String> getAlias();

    @InputFile
    abstract public RegularFileProperty getYaml();

    @TaskAction
    public void compose() {
        this.getProject().exec( execSpec -> {
            execSpec.executable("docker");
            execSpec.setStandardInput(System.in);
            execSpec.setStandardOutput(System.out);
            Map<String, String> env = new HashMap<>();
            env.put("COMPOSE_PROJECT_NAME", getTitle().get());
            execSpec.environment(env);
            List<String> args = new ArrayList<>();
            args.add("compose");
            args.add("-f");
            args.add(getYaml().get().getAsFile().getAbsolutePath());
            args.add("run");
            args.add("--rm");
            args.add(getAlias().get());
            execSpec.args(args);
        });
    }

}
