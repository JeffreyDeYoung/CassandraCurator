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

    public static void startCassandra(RemoteCommandDao command) throws ConnectionException, IOException, InterruptedException
    {
        command.sendCommand("/etc/cassandra/setcassandraip.sh");//set the ips in the cassandra yaml correctly; I can't get the dockerfile to do this automatically
        command.sendCommand("service cassandra start");//make the call to start cassandra
        Thread.sleep(15000);//Sleep to let cassandra finish starting up
        //TODO: cleanup, java doc, finish, etc, not working

    }
}
