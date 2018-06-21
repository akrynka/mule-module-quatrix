/**
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 **/

package org.quatrix.strategy;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.*;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.quatrix.api.QuatrixApi;
import org.quatrix.api.QuatrixApiException;
import org.quatrix.api.QuatrixApiImpl;
import org.quatrix.api.config.ApiConfig;
import org.quatrix.api.config.ApiConfigBuilder;
import org.quatrix.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic auth connection management for Quatrix API.
 *
 * @author Aleksey K
 */
@ConnectionManagement(configElementName = "config-type", friendlyName = "Basic Auth connection config")
public class QuatrixConnectorConnectionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(QuatrixConnectorConnectionStrategy.class);


    private QuatrixApi quatrixApi;
    private String sessionId;

    @Configurable
    @Default("https://api.quatrix.it/api/1.0")
    @FriendlyName("Quatrix API url")
    private String basePath;

    @Configurable
    @Default("5")
    @FriendlyName("Delay in minutes for sending keep alive requests")
    private long keepAliveCallDelay;

    /**
     * Connect to Quatrix API.
     *
     * @param username A username
     * @param password A password
     * @throws ConnectionException if unable to login
     */
    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey String username, @Password String password) throws ConnectionException {
        final ApiConfig config = new ApiConfigBuilder()
                .setBasePath(basePath)
                .setKeepAliveCallDelay(keepAliveCallDelay)
                .setUsername(username)
                .setPassword(password)
                .build();

        this.quatrixApi = new QuatrixApiImpl(config);
        try {
            Session session = quatrixApi.login();
            this.sessionId = session.getId().toString();
        } catch (QuatrixApiException e) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "", e.getMessage());
        }
    }

    @Disconnect
    public void disconnect() {
        try {
            quatrixApi.close();
        } catch (QuatrixApiException e) {
            logger.error("Error during disconnect", e);
        } finally {
            this.quatrixApi = null;
            this.sessionId = null;
        }
    }

    @ValidateConnection
    public boolean isConnected() {
        return sessionId != null;
    }

    @ConnectionIdentifier
    public String connectionId() {
        return sessionId;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public long getKeepAliveCallDelay() {
        return keepAliveCallDelay;
    }

    public void setKeepAliveCallDelay(long keepAliveCallDelay) {
        this.keepAliveCallDelay = keepAliveCallDelay;
    }

    public QuatrixApi getQuatrix() {
        return quatrixApi;
    }
}