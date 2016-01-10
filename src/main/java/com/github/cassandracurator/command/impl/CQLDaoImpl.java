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

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.github.cassandracurator.command.CQLDao;
import com.github.cassandracurator.domain.Server;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dao for interacting with Cassandra via CQL. This class isn't to be
 * particularly efficient, just useful enough to perform administrative
 * functions.
 *
 * @author jeffrey
 */
public class CQLDaoImpl implements CQLDao
{

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Servers we are executing the commands against. If you want to execute a
     * command against a particular node only pass the specific server in the
     * list.
     */
    private List<Server> servers;

    /**
     * CQL session we are using to connect with.
     */
    private Session session;

    /**
     * Constructor.
     *
     * @param servers Servers we are executing the commands against. If you want
     * to execute a command against a particular node only pass the specific
     * server in the list.
     */
    public CQLDaoImpl(List<Server> servers)
    {
        this.servers = servers;
        Builder clusterBuilder = Cluster.builder();
        logger.debug("Establishing a CQL Dao for the following servers:");
        for (Server s : servers)
        {
            logger.debug("\t" + s.toString());
            clusterBuilder.addContactPoint(s.getIp());
        }
        Cluster cluster = clusterBuilder.build();
        this.session = cluster.connect();
    }

    /**
     * Constructor.
     *
     * @param session Established CQL session you wish to use to run the query.
     */
    public CQLDaoImpl(Session session)
    {
        logger.debug("Establishing a CQL Dao with pre-existing session.");
        this.session = session;
    }

    /**
     * Executes a CQL command. Reminder: this isn't intended to be efficient;
     * it's for periodic administrative functions only.
     *
     * @param command Command to execute.
     * @return ResultSet with the resulting response, if applicable to the
     * command.
     */
    @Override
    public synchronized ResultSet executeCQLCommand(String command)
    {
        logger.info("Executing CQL command: " + command);
        return session.execute(command);
    }
}
