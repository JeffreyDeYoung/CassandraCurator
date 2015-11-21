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
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private String pemPassphrase;

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Session session = null;

    //TODO: Javadoc
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

    public SSHCommandDaoImpl(String host, String userName, int port, String pem, String pemPassphrase) {
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
        if (pem == null) {
            throw new IllegalArgumentException("PEM cannot be null");
        }
        this.pem = pem;
        this.pemPassphrase = pemPassphrase;

    }

    @Override
    public void connect() throws CannotConnectException {
        logger.debug("Attempting to log on to: " + host + " via SSH.");
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, host, port);
            if (pem != null) {//if pem is present, use that
                //TODO: not sure if this is right
                if (pemPassphrase != null) {
                    jsch.addIdentity(pem, pemPassphrase);
                    jsch.addIdentity(pem);
                } else {
                    jsch.addIdentity(pem);
                }
                session.setConfig("StrictHostKeyChecking", "no");//TODO: handle this more responsibly
                session.setIdentityRepository(jsch.getIdentityRepository());
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
        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public void pullFile(String remoteFileToPull, File localFile) throws ConnectionException, IOException {
        logger.trace("Pulling file: '" + remoteFileToPull + "' from: " + host + " to: " + localFile.getAbsolutePath());        
        checkConnection();
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp) channel;
            c.get(remoteFileToPull, localFile + File.separator);
        } catch (JSchException | SftpException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public void pushFile(File localFile, String remotePath) throws ConnectionException, IOException {
        logger.trace("Pushing file: '" + localFile.getAbsolutePath() + "' to: " + host + ": " + remotePath);
        checkConnection();
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp) channel;
            c.put(localFile.getAbsolutePath(), remotePath + File.separator);
        } catch (JSchException | SftpException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public String sendCommand(String commandToSend) throws ConnectionException, IOException {
        logger.trace("Sending command: '" + commandToSend + "' to server: " + host);
        checkConnection();
        StringBuilder sb = new StringBuilder();
        try {
            Channel c = session.openChannel("exec");
            ((ChannelExec) c).setCommand(commandToSend);
            c.connect();
            InputStream outputFromCommand = c.getInputStream();
            int readByte = outputFromCommand.read();
            while (readByte != 0xffffffff) {
                sb.append((char) readByte);
                readByte = outputFromCommand.read();
            }
            c.disconnect();
            return sb.toString().trim();
        } catch (JSchException e) {
            throw new ConnectionException(e);
        }
    }

    private void checkConnection() throws ConnectionException {
        if (session == null || !session.isConnected()) {
            throw new ConnectionException("Not connected to server. Call connect first.");
        }
    }

}
