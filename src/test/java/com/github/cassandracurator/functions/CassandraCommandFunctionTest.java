/*
 * Copyright 2016 jeffrey.
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
package com.github.cassandracurator.functions;

import com.github.cassandracurator.command.RemoteCommandDao;
import com.github.cassandracurator.command.impl.SSHCommandDaoImpl;
import com.github.cassandracurator.exceptions.ConnectionException;
import com.github.cassandradockertesthelper.cassandradockertesthelper.CassandraDockerParameterizedTestParent;
import com.github.cassandradockertesthelper.cassandradockertesthelper.DockerHelper;
import java.io.File;
import java.io.IOException;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jeffrey
 */
public class CassandraCommandFunctionTest extends CassandraDockerParameterizedTestParent
{

    private String dockerIp = null;
    private String dockerId = null;
    private RemoteCommandDao commandDao = null;

    public CassandraCommandFunctionTest(File dockerFile)
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
        commandDao = new SSHCommandDaoImpl(dockerIp, "root", 22, "./src/test/resources/docker/insecure_key", null);
        commandDao.connect();

    }
    
    /**
     * Test of startCassandra method, of class CassandraCommandFunction.
     */
    @Test
    public void testStartCassandra() throws Exception
    {
        System.out.println("startCassandra version: " + super.getCassandraVersion());
        boolean expResult = true;
        boolean result = CassandraCommandFunction.startCassandra(commandDao);
        assertEquals(expResult, result);
        //run it again
        result = CassandraCommandFunction.startCassandra(commandDao);
        assertEquals(expResult, result);
    }

    /**
     * Test of isCassandraRunning method, of class CassandraCommandFunction.
     */
    @Test
    public void testIsCassandraRunning() throws Exception
    {
        System.out.println("isCassandraRunning version: " + super.getCassandraVersion());
        boolean expResult = false;
        //make sure cassandra is not running
        boolean result = CassandraCommandFunction.isCassandraRunning(commandDao);
        assertEquals(expResult, result);
        //start cassandra
        result = CassandraCommandFunction.startCassandra(commandDao);
        assertEquals(true, result);
        //make sure it is now running
        result = CassandraCommandFunction.isCassandraRunning(commandDao);
        assertEquals(true, result);

    }

}
