/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cassandracurator.command.impl;

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
            //instance.connect();
        } finally {
            DockerHelper.spinDownDockerBox(id);
        }
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of logOff method, of class SSHCommandDaoImpl.
     */
    @org.junit.Test
    @Ignore
    public void testLogOff() {
        System.out.println("logOff");
        SSHCommandDaoImpl instance = null;
        instance.logOff();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
    @Ignore
    public void testSendCommand() throws Exception {
        System.out.println("sendCommand");
        String commandToSend = "";
        SSHCommandDaoImpl instance = null;
        String expResult = "";
        String result = instance.sendCommand(commandToSend);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
