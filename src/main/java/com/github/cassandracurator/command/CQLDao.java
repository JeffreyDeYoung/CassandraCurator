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
package com.github.cassandracurator.command;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ResultSet;
import java.util.List;

/**
 * Dao for interacting with Cassandra via CQL. This class isn't intended to be
 * particularly efficient, just useful enough to perform administrative
 * functions.
 *
 * @author jeffrey
 */
public interface CQLDao
{

    /**
     * Executes a CQL command. Reminder: this isn't intended to be efficient;
     * it's for periodic administrative functions only.
     *
     * @param command Command to execute.
     * @return ResultSet with the resulting response, if applicable to the
     * command.
     */
    public ResultSet executeCQLCommand(String command);

    /**
     * Gets information about our keyspaces in this cluster.
     *
     * @return A List of KeyspaceMetadata for this cluster; each item should
     * represent one keyspace.
     */
    public List<KeyspaceMetadata> getKeyspaces();
}
