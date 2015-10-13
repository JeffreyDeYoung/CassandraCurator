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

import com.github.cassandracurator.command.RemoteCommandDao;
import com.github.cassandracurator.exceptions.CannotConnectException;
import com.github.cassandracurator.exceptions.ConnectionException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.File;
import java.io.IOException;

/**
 * CommandDao implemented with SSH.
 *
 * @author jeffrey
 */
public class SSHCommandDaoImpl implements RemoteCommandDao {

    private String host;
    private String userName;
    private int port;
    private String password;
    private String pem;

    private Session session = null;

    //TODO: Remove
    public SSHCommandDaoImpl() {
    }

    public SSHCommandDaoImpl(String host, String userName, int port, String password) {
        if (host == null) {
            throw new IllegalArgumentException("Host cannot be null");
        }
        this.host = host;
        if (userName == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        this.userName = userName;
        if (port == 0) {
            throw new IllegalArgumentException("Zero (0) is not a valid port");
        }
        this.port = port;
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        this.password = password;
    }

    @Override
    public void connect() throws CannotConnectException {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, host, port);
            if (pem != null) {//if pem is present, use that
                //TODO: finish this
            } else {//if pem not present, use the password
                session.setPassword(password);
            }
            session.connect();
        } catch (JSchException e) {
            throw new CannotConnectException(e);
        }
    }

    @Override
    public void logOff() {
        if(session != null){
            session.disconnect();
        }       
    }

    @Override
    public void pullFile(String remotePathToPull, File localFile) throws ConnectionException, IOException {
        checkConnection();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pushFile(File localFile, String remotePath) throws ConnectionException, IOException {
        checkConnection();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sendCommand(String commandToSend) throws ConnectionException {
        checkConnection();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void checkConnection() throws ConnectionException {
        if (session == null || !session.isConnected()) {
            throw new ConnectionException("Not connected to server. Call connect first.");
        }
    }

}
