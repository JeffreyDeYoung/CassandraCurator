/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cassandracurator.command.impl;

import com.github.cassandracurator.exceptions.ConnectionException;
import com.github.cassandradockertesthelper.DockerHelper;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: this class is intentionally not parameterized; there is nothing
 * Cassandra version specific here, so we don't want to waste 15 minutes of test time by
 * testing it over and over again.
 *
 * @author jeffrey
 */
public class SSHCommandDaoImplTest
{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SSHCommandDaoImplTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of connect method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testConnect() throws Exception
    {
        System.out.println("connect");
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            assertNotNull(ip);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();//mainly making sure we don't error here
            instance.logOff();
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of connect method, of class SSHCommandDaoImpl. Tests the
     * user/password alternate constructor.
     */
    @org.junit.Test
    public void testConnectUserPass() throws Exception
    {
        System.out.println("connect");
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            assertNotNull(ip);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "test_pass");
            instance.connect();//mainly making sure we don't error here
            instance.logOff();
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of pullFile method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testPullFile() throws Exception
    {
        System.out.println("pullFile");
        File localFile = new File("./src/test/resources/testfiles/testfile.txt");
        File fromRemote = new File("./src/test/resources/testfiles/testfile.fromremote");
        if (fromRemote.exists())
        {
            fromRemote.delete();
        }
        String remotePath = "/testfile.txt";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            instance.pushFile(localFile, "/");//push up a test file
            instance.pullFile(remotePath, fromRemote);//pull it back down        
            assertTrue(fromRemote.exists());
            assertEquals(FileUtils.readFileToString(localFile), FileUtils.readFileToString(fromRemote));
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
            fromRemote.delete();
        }
    }

    /**
     * Test of pushFile method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testPushFile() throws Exception
    {
        System.out.println("pushFile");
        File localFile = new File("./src/test/resources/testfiles/testfile.txt");
        String remotePath = "/";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            instance.pushFile(localFile, remotePath);
            assertTrue(instance.sendCommand("ls /").contains("testfile.txt"));
            assertTrue(instance.sendCommand("cat /testfile.txt").equals(FileUtils.readFileToString(localFile)));
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testSendCommand() throws Exception
    {
        System.out.println("sendCommand");
        String commandToSend = "ls";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testSendCommand1() throws Exception
    {
        System.out.println("sendCommand1");
        String commandToSend = "echo this is a test";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "this is a test";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testSendCommand2() throws Exception
    {
        System.out.println("sendCommand2");
        String commandToSend;
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            commandToSend = "ifconfig | grep " + ip + " | wc -l";//make sure this is the ip we think we are connected to
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "1";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test(expected = ConnectionException.class)
    public void testSendCommandFailure() throws Exception
    {
        System.out.println("sendCommand");
        String commandToSend = "ls";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.sendCommand(commandToSend);
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of not directly related to this class, but we need to test that the
     * docker file can start cassandra, and this is the earliest in the test
     * plan that it makes sense. If this test fails, there's a good chance that
     * there's actually a problem with the docker image or startup process.
     */
    @org.junit.Test
    public void testStartCassandra() throws Exception
    {
        System.out.println("startCassandra");
        String commandToSend = "service cassandra start";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try
        {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
            Thread.sleep(5000);
            String statusCommand = "service cassandra status";
            String resultStatus = instance.sendCommand(statusCommand);
            assertEquals("* Cassandra is running", resultStatus);
        } finally
        {
            DockerHelper.spinDownDockerBox(id);
        }
    }
}
