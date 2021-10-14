package org.janusgraph.plugin.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

/**
 * The DockerWorker is a wrapper around the docker-java transports and exec.
 *
 */
class DockerWorker {

    static DockerClient startDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
//                .withDockerHost("localhost")
//                .withDockerTlsVerify(false)
                .build();

        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
//                .maxConnections(100)
//                .connectionTimeout(Duration.ofSeconds(30))
//                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
