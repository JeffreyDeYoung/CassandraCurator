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

/**
 * Dao for executing remote Cassandra commands. Mostly nodetool related action.
 * @author jeffrey
 */
public interface CassandraCommandDao
{
    /**
     * Gets the response from nodetool status.
     * @return The nodetool status response as a string.
     */
    public String getNodetoolStatus();
    
    /**
     * Gets the response from nodetool status for a particular keyspace.
     * @param keyspace The keyspace you want to get the status for.
     * @return 
     */
    public String getNodetoolStatus(String keyspace);
    
    
    
}
