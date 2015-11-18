/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cassandracurator.command.impl;

import com.github.cassandracurator.exceptions.ConnectionException;
import com.github.cassandracurator.testhelper.DockerHelper;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jeffrey
 */
public class SSHCommandDaoImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SSHCommandDaoImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testConnect() throws Exception {
        System.out.println("connect");
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try {
            String ip = DockerHelper.getDockerIp(id);
            assertNotNull(ip);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();//mainly making sure we don't error here
            instance.logOff();
        } finally {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of pullFile method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    @Ignore
    public void testPullFile() throws Exception {
        System.out.println("pullFile");
        String remotePathToPull = "";
        File localFile = null;
        SSHCommandDaoImpl instance = null;
        instance.pullFile(remotePathToPull, localFile);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of pushFile method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    @Ignore
    public void testPushFile() throws Exception {
        System.out.println("pushFile");
        File localFile = null;
        String remotePath = "";
        SSHCommandDaoImpl instance = null;
        instance.pushFile(localFile, remotePath);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testSendCommand() throws Exception {
        System.out.println("sendCommand");
        String commandToSend = "ls";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
        } finally {
            DockerHelper.spinDownDockerBox(id);
        }
    }

    /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testSendCommand1() throws Exception {
        System.out.println("sendCommand1");
        String commandToSend = "echo this is a test";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "this is a test";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
        } finally {
            DockerHelper.spinDownDockerBox(id);
        }
    }
        /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    public void testSendCommand2() throws Exception {
        System.out.println("sendCommand2");
        String commandToSend;
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try {
            String ip = DockerHelper.getDockerIp(id);
            commandToSend = "ifconfig | grep " + ip + " | wc -l";//make sure this is the ip we think we are connected to
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.connect();
            String expResult = "1";
            String result = instance.sendCommand(commandToSend);
            assertEquals(expResult, result);
        } finally {
            DockerHelper.spinDownDockerBox(id);
        }
    }
    
        /**
     * Test of sendCommand method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test(expected = ConnectionException.class)
    public void testSendCommandFailure() throws Exception{
        System.out.println("sendCommand");
        String commandToSend = "ls";
        String id = DockerHelper.spinUpDockerBox("cassandra2.1.0", new File("./src/test/resources/docker/cassandra2.1.0"));
        try {
            String ip = DockerHelper.getDockerIp(id);
            SSHCommandDaoImpl instance = new SSHCommandDaoImpl(ip, "root", 22, "./src/test/resources/docker/insecure_key", null);
            instance.sendCommand(commandToSend);
        } finally {
            DockerHelper.spinDownDockerBox(id);
        }
    }
}
