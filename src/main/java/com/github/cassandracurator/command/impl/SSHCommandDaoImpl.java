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
 * CommandDao implemented with SSH. Used for interacting with remote machines
 * (servers) over SSH.
 *
 * @author jeffrey
 */
public class SSHCommandDaoImpl implements RemoteCommandDao {

    /**
     * Host we are connecting to.
     */
    private String host;
    /**
     * Username we are connecting with.
     */
    private String userName;
    /**
     * Port that we are connecting via SSH on.
     */
    private int port;
    /**
     * Password we are using.
     */
    private String password;
    /**
     * PEM (private key) file we are using to connect.
     */
    private String pem;
    /**
     * Password for the associated PEM file.
     */
    private String pemPassphrase;

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Current SSH session.
     */
    private Session session = null;

    /**
     * Constructor to use when connecting to a server over SSH when using a username and password.
     * @param host Host (ip or DNS) that you are trying to connect to.
     * @param userName Username that you are using to connect.
     * @param port Port to connect to SSH over.
     * @param password Password for the associated user.
     */
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

    /**
     * Constructor to use when connecting to a server over SSH when using a PEM (private key) file.
     * @param host Host (ip or DNS) that you are trying to connect to.
     * @param userName Username that you are using to connect.
     * @param port Port to connect to SSH over.
     * @param pem PEM (private key file) that you are using for authentication.
     * @param pemPassphrase Password to the PEM file. Pass null if the PEM is not password protected.
     */
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

    /**
     * Connects to a remote server via SSH.
     *
     * @throws CannotConnectException If there is a problem connecting to the
     * server.
     */
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

    /**
     * Logs off of the remote machine. Try to call in finally blocks to ensure
     * your connection gets properly ended.
     */
    @Override
    public void logOff() {
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * Pulls a file from the remote to the local machine.
     *
     * @param localFile Local file to write the remote file to.
     * @param remoteFileToPull File path on the remote machine to pull the file
     * from.
     * @throws ConnectionException if there is a problem with the connection.
     * @throws IOException if there is a problem pulling or writing the file.
     */
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

    /**
     * Pushes a file from the local machine to the remote machine.
     *
     * @param localFile Local file to push.
     * @param remotePath Directory path on the remote machine to push to.
     * @throws ConnectionException if there is a problem with the connection.
     * @throws IOException if there is a problem reading or sending the file.
     */
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

    /**
     * Sends a command to a remote system.
     *
     * @param commandToSend String of a valid terminal command to send to the
     * remote system.
     * @return The response from the remote system as a String.
     * @throws ConnectionException if there is a problem with the connection.
     */
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

    /**
     * Checks to see if we currently have a valid connection.
     *
     * @throws ConnectionException If we are not connected.
     */
    private void checkConnection() throws ConnectionException {
        if (session == null || !session.isConnected()) {
            throw new ConnectionException("Not connected to server. Call connect first.");
        }
    }

}
