package com.github.cassandracurator.testhelper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import java.io.File;

/**
 *
 * @author jeffrey
 */
public class DockerHelper {

    public static String spinUpDockerBox(String dockerBoxName) {

        File basefile = new File("./src/test/resources/docker/cassandra2.1.0");
        if (!basefile.exists()) {
            throw new IllegalArgumentException("Docker file must exist.");
        }
        DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder().withUri("http://localhost:2375")
                .build();

        BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                System.out.println("" + item);
                super.onNext(item);
            }
        };
        DockerClient docker = DockerClientBuilder.getInstance(config).build();
        String imageId = docker.buildImageCmd(basefile).exec(callback).awaitImageId();

        CreateContainerResponse container = docker.createContainerCmd("cassandra2.1.0")
                .withCmd("touch", "/test")
                .exec();

        docker.startContainerCmd(container.getId()).exec();
        return imageId;
    }

}
