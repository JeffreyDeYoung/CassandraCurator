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

import com.github.cassandracurator.exceptions.CannotConnectException;
import com.github.cassandracurator.exceptions.ConnectionException;
import java.io.File;
import java.io.IOException;

/**
 * Dao for interacting with remote systems using standard terminal commands.
 * @author jeffrey
 */
public interface RemoteCommandDao {
    
    /**
     * Establishes a connection to a remote system.
     * @throws CannotConnectException if there is an problem connecting.
     */
    public void connect() throws CannotConnectException;
    
    /**
     * Logs off of the remote system.
     */
    public void logOff();
    
    /**
     * Sends a command to a remote system.
     * @param commandToSend String of a valid terminal command to send to the remote system.
     * @return The response from the remote system as a String.
     * @throws ConnectionException if there is a problem with the connection.
     */
    public String sendCommand(String commandToSend) throws ConnectionException;

    /**
     * Pushes a file from the local machine to the remote machine.
     * @param localFile Local file to push.
     * @param remotePath Path on the remote machine to push to.
     * @throws ConnectionException if there is a problem with the connection.
     * @throws IOException if there is a problem reading or sending the file.
     */
    public void pushFile(File localFile, String remotePath) throws ConnectionException, IOException;
    
        /**
     * Pushes a file from the local machine to the remote machine.
     * @param localFile Local file to write the remote file to.
     * @param remotePathToPull File path on the remote machine to full from.
     * @throws ConnectionException if there is a problem with the connection.
     * @throws IOException if there is a problem pulling or writing the file.
     */
    public void pullFile(String remotePathToPull, File localFile) throws ConnectionException, IOException;
}
