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
package com.github.cassandracurator.domain;

import java.util.Objects;

/**
 * Represents a single Cassandra server.
 *
 * @author jeffrey
 */
public class Server
{

    /**
     * Ip address for this server.
     */
    private String ip;

    /**
     * Optional Friendly name for this server. Something that is a helpful human
     * readable name. Optional.
     */
    private String friendlyName;

    /**
     * Optional DNS name for this server. Will not be used for anything
     * programatic; the IP address will be used for that, this will only be used
     * for logging and UI purposes. Optional.
     */
    private String dnsName;

    /**
     * Name of the Cassandra cluster that this server is associated with.
     */
    private String clusterName;

    /**
     * Constructor that sets the required fields.
     * @param ip Ip address for this server.
     * @param clusterName Cluster name for this server.
     */
    public Server(String ip, String clusterName)
    {
        this.ip = ip;
        this.clusterName = clusterName;
    }

    /**
     * Constructor that sets all fields.
     * @param ip Ip address for this server.
     * @param friendlyName Human readable name for this server.
     * @param dnsName DNS name for this server.
     * @param clusterName Cluster name for this server.
     */
    public Server(String ip, String friendlyName, String dnsName, String clusterName)
    {
        this.ip = ip;
        this.friendlyName = friendlyName;
        this.dnsName = dnsName;
        this.clusterName = clusterName;
    }

    /**
     * Ip address for this server.
     *
     * @return the ip
     */
    public String getIp()
    {
        return ip;
    }   

    /**
     * Ip address for this server.
     *
     * @param ip the ip to set
     */
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /**
     * Optional Friendly name for this server. Something that is a helpful human
     * readable name. Optional.
     *
     * @return the friendlyName
     */
    public String getFriendlyName()
    {
        return friendlyName;
    }

    /**
     * Optional Friendly name for this server. Something that is a helpful human
     * readable name. Optional.
     *
     * @param friendlyName the friendlyName to set
     */
    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    /**
     * Optional DNS name for this server. Will not be used for anything
     * programatic; the IP address will be used for that, this will only be used
     * for logging and UI purposes. Optional.
     *
     * @return the dnsName
     */
    public String getDnsName()
    {
        return dnsName;
    }

    /**
     * Optional DNS name for this server. Will not be used for anything
     * programatic; the IP address will be used for that, this will only be used
     * for logging and UI purposes. Optional.
     *
     * @param dnsName the dnsName to set
     */
    public void setDnsName(String dnsName)
    {
        this.dnsName = dnsName;
    }

    /**
     * Name of the Cassandra cluster that this server is associated with.
     *
     * @return the clusterName
     */
    public String getClusterName()
    {
        return clusterName;
    }

    /**
     * Name of the Cassandra cluster that this server is associated with.
     *
     * @param clusterName the clusterName to set
     */
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    /**
     * Simple toString(). Used for logging, etc.
     *
     * @return A String representation of this object.
     */
    @Override
    public String toString()
    {
        return "Server{" + "ip=" + getIp() + ", friendlyName=" + getFriendlyName() + ", dnsName=" + getDnsName() + ", clusterName=" + getClusterName() + '}';
    }

    /**
     * Equals method for this object. We only check on the ip field; if the ips
     * are equal, we consider the objects equal.
     *
     * @param obj Object to check for equality on.
     * @return True of obj is equal to this object.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Server other = (Server) obj;
        if (!Objects.equals(this.ip, other.ip))
        {
            return false;
        }
        return true;
    }

}
