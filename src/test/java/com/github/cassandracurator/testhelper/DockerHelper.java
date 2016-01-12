package com.github.cassandracurator.testhelper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.NotFoundException;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Ulimit;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for helping spin up and down docker instances. Note: this is also a
 * test class for itself.
 *
 * @author jeffrey
 */
@RunWith(value = Parameterized.class)
public class DockerHelper extends TestCase
{

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(DockerHelper.class);

    /**
     * Docker config; set to localhost:2375. Shared with all methods in this
     * class.
     */
    private static final DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder().withUri("http://localhost:2375")
            .build();
    /**
     * Docker client. Shared with all methods in this class.
     */
    private static final DockerClient docker = DockerClientBuilder.getInstance(config).build();

    /**
     * Docker file that this particular test is running with.
     */
    private final File dockerFile;

    /**
     * Constructor. For testing THIS class only. This helper is also a test
     * class for itself. To use this as a simple helper, just call the static
     * methods. The only reason this constructor exists is to provide a
     * constructor for the parameterized tests.
     *
     * @param dockerFile DockerFile to test against.
     */
    public DockerHelper(File dockerFile)
    {
        this.dockerFile = dockerFile;
    }

    @Parameters(name = "Cassandra file: {0}")
    public static final Collection<File[]> generateDockerFileNames()
    {
        File dir = new File("./src/test/resources/docker/");
        File[] cassandraDockerFiles = dir.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return (name.startsWith("cassandra") && !name.endsWith("~"));//cassandra docker boxes that are not gedit backups                
            }
        });
        List<File[]> toReturn = new ArrayList<>();
        for(File f : cassandraDockerFiles){
            File[] fileArray = new File[1];
            fileArray[0] = f;
            toReturn.add(fileArray);
        }
        return toReturn;
    }

    /**
     * Spins up a new docker box. Important: don't forget to spin it back down
     * in a finally block.
     *
     * @param dockerBoxName Name of the docker box to start.
     * @param baseFile Base file to create the docker box from.
     * @return A docker container Id that can be used to reference the spun up
     * box. It is important to save this so you can spin it back down and
     * perform actions on it.
     */
    public static String spinUpDockerBox(String dockerBoxName, File baseFile)
    {

        if (baseFile == null || !baseFile.exists())
        {
            throw new IllegalArgumentException("Docker file must exist.");
        }
        logger.debug("Spinning up Docker Box with name: " + dockerBoxName + ", and basefile: " + baseFile.getAbsolutePath());
        BuildImageResultCallback callback = new BuildImageResultCallback()
        {
            @Override
            public void onNext(BuildResponseItem item)
            {
//                logger.debug("" + item);
                super.onNext(item);
            }
        };
        //higher ulimits let cassandra run (as a service? from the command line it starts up fine)
        Ulimit[] ulimits = new Ulimit[1];
        ulimits[0] = new Ulimit("nofile", 262144, 262144);

        String imageId = docker.buildImageCmd(baseFile).exec(callback).awaitImageId();
        
        CreateContainerResponse container = docker.createContainerCmd(dockerBoxName)
                .withCmd("/sbin/my_init")
                .withUlimits(ulimits)
                .withPrivileged(true)
                .withPublishAllPorts(true)
                .exec();
        logger.trace("Container: " + container.toString());
        logger.trace("Container id: " + container.getId());
        logger.trace("ImageId: " + imageId);
        docker.startContainerCmd(container.getId()).exec();
        try
        {
            Thread.sleep(1500);//sleep for a second and a half to let it come up on line before proceeding
        } catch (InterruptedException e)
        {;
        }
        if (logger.isTraceEnabled())
        {
            Info dockerInfo = docker.infoCmd().exec();
            logger.trace("Info: " + dockerInfo.toString());
            InspectContainerResponse res = docker.inspectContainerCmd(container.getId()).exec();
            logger.trace("Ip: " + res.getNetworkSettings().getIpAddress());
        }
        return container.getId();
    }

    /**
     * Gets the IP address of a running docker box.
     *
     * @param containerId Container to get the IP address of.
     * @return The IP address of the specified container.
     */
    public static String getDockerIp(String containerId)
    {
        InspectContainerResponse res = docker.inspectContainerCmd(containerId).exec();
        String ip = res.getNetworkSettings().getIpAddress();
        logger.debug("IP for id: " + containerId + " is: " + ip);
        return ip;
    }

    /**
     * Determines if a box is currently running or not.
     *
     * @param containerId Container id of the box you wish to check if it's
     * running or not.
     * @return True if the box is running, false otherwise.
     */
    public static boolean isBoxRunning(String containerId)
    {
        try
        {
            return docker.inspectContainerCmd(containerId).exec().getState().isRunning();
        } catch (NotFoundException e)
        {
            return false;//it can't be found, so it must not be running          
        }
    }

    /**
     * Spins down a docker box.
     *
     * @param containerId Container id of the box you wish to spin down.
     */
    public static void spinDownDockerBox(String containerId)
    {
        logger.debug("Spinning down docker box with containerId: " + containerId);
        docker.stopContainerCmd(containerId).exec();
        docker.waitContainerCmd(containerId).exec();
    }

    //helper, but also self testing
    @Test
    public void testCycle() throws Exception
    {
        logger.info("Testing docker helper. " + dockerFile.getName());
        String id = DockerHelper.spinUpDockerBox(dockerFile.getName(), dockerFile);
        assertTrue(DockerHelper.isBoxRunning(id));
        assertNotNull(DockerHelper.getDockerIp(id));
        DockerHelper.spinDownDockerBox(id);
        assertFalse(DockerHelper.isBoxRunning(id));
    }

    @Test
    public void testCycleTwoBoxes() throws Exception
    {
        logger.info("Testing docker helper with two boxes. "  + dockerFile.getName());
        String id1 = DockerHelper.spinUpDockerBox(dockerFile.getName(), dockerFile);
        String id2 = DockerHelper.spinUpDockerBox(dockerFile.getName(), dockerFile);
        assertTrue(DockerHelper.isBoxRunning(id1));
        assertTrue(DockerHelper.isBoxRunning(id2));
        assertNotNull(DockerHelper.getDockerIp(id1));
        assertNotNull(DockerHelper.getDockerIp(id2));
        assertNotSame(DockerHelper.getDockerIp(id1), DockerHelper.getDockerIp(id2));
        DockerHelper.spinDownDockerBox(id1);
        assertFalse(DockerHelper.isBoxRunning(id1));
        assertTrue(DockerHelper.isBoxRunning(id2));
        DockerHelper.spinDownDockerBox(id2);
        assertFalse(DockerHelper.isBoxRunning(id2));
    }
}
