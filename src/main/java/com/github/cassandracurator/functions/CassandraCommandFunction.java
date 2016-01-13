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
import com.github.cassandracurator.exceptions.ConnectionException;
import java.io.IOException;

/**
 *
 * @author jeffrey
 */
public class CassandraCommandFunction
{

    private static final String CASSANDRA_START_COMMAND = "service cassandra start";
    private static final String CASSANDRA_STATUS_COMMAND = "service cassandra status";

    private static final String EXPECTED_RUNNING_MESSAGE = "* Cassandra is running";

    /**
     * Starts an Cassandra service using the specified RemoteCommandDao.
     *
     * @param command RemoteCommandDao that is <b>already connected</b> to the
     * server you wish to start the Cassandra instance on.
     * @throws ConnectionException If we can't connect or there is an connection
     * problem to the server.
     * @throws IOException If there is an IO issue talking to the server.
     * @return True if Cassandra was started; false if it failed to start.
     */
    public static boolean startCassandra(RemoteCommandDao command) throws ConnectionException, IOException
    {
        if (isCassandraRunning(command))
        {
            return true;// don't try to start cassandra again if it's already running; will just slow us down
        }
        //cassandra is not currently running
        command.sendCommand("/etc/cassandra/setcassandraip.sh");//set the ips in the cassandra yaml correctly; I can't get the dockerfile to do this automatically -- this will probably bite us in the future
        command.sendCommand(CASSANDRA_START_COMMAND);//make the call to start cassandra
        try
        {
            Thread.sleep(30000);//Sleep to let cassandra finish starting up
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);//in my over 13 years working with java, I have never seen an InterruptedException thrown for calling sleep; so I think this is pretty safe.
        }
        return isCassandraRunning(command);//check to see if we succeeded
    }

    /**
     * Checks to see if the Cassandra service is running using the
     * RemoteCommandDao.
     *
     * @param command RemoteCommandDao that is <b>already connected</b> to the
     * server you wish to start the Cassandra instance on.
     * @return True if Cassandra is running; false if it is not.
     * @throws ConnectionException If we can't connect or there is an connection
     * problem to the server.
     * @throws IOException If there is an IO issue talking to the server.
     */
    public static boolean isCassandraRunning(RemoteCommandDao command) throws ConnectionException, IOException
    {
        String statusResponse = command.sendCommand(CASSANDRA_STATUS_COMMAND);
        if (statusResponse.equals(EXPECTED_RUNNING_MESSAGE))
        {
            return true;
        } else
        {
            return false;
        }
    }
}
