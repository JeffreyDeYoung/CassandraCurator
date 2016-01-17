/*
 * Copyright 2015 jeffrey.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cassandracurator.command.impl;

import com.datastax.driver.core.KeyspaceMetadata;
import com.github.cassandracurator.command.RemoteCommandDao;
import com.github.cassandracurator.domain.Server;
import com.github.cassandracurator.exceptions.ConnectionException;
import com.github.cassandracurator.functions.CassandraCommandFunction;
import com.github.cassandradockertesthelper.AbstractCassandraDockerParameterizedTest;
import com.github.cassandradockertesthelper.DockerHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jeffrey
 */
public class CQLDaoImplTest extends AbstractCassandraDockerParameterizedTest
{

    private String dockerIp = null;
    private String dockerId = null;

    public CQLDaoImplTest(File dockerFile)
    {
        super(dockerFile);//call to super class to actually setup this test.
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
    public void setUp() throws ConnectionException, IOException, InterruptedException
    {
        dockerId = super.spinUpNewCassandraDockerBox();
        dockerIp = DockerHelper.getDockerIp(dockerId);
        RemoteCommandDao commandDao = new SSHCommandDaoImpl(dockerIp, "root", 22, "./src/test/resources/docker/insecure_key", null);
        commandDao.connect();
        CassandraCommandFunction.startCassandra(commandDao);
    }

    /**
     * Test of getKeyspaces method, of class CQLDaoImpl.
     */
    @Test
    public void testGetKeyspaces()
    {
        System.out.println("executeCQLCommandServersList");
        List<Server> servers = new ArrayList<>();
        servers.add(new Server(dockerIp, "test"));
        CQLDaoImpl instance = new CQLDaoImpl(servers);
        List<KeyspaceMetadata> result = instance.getKeyspaces();
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    /**
     * Test of executeCQLCommand method, of class CQLDaoImpl.
     */
    @Test
    public void testExecuteCQLCreateCommand()
    {
        System.out.println("executeCQLCreateCommand");
        String command = "create keyspace test WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };";
        List<Server> servers = new ArrayList<>();
        servers.add(new Server(dockerIp, "test"));
        CQLDaoImpl instance = new CQLDaoImpl(servers);
        instance.executeCQLCommand(command);
        List<KeyspaceMetadata> result = instance.getKeyspaces();
        assertNotNull(result);
        assertTrue(result.size() >= 3);
        boolean found = false;
        for (KeyspaceMetadata r : result)
        {
            if (r.getName().equals("test"))
            {
                found = true;
            }
        }
        assertTrue("The created keyspace was not found.", found);
    }

}
