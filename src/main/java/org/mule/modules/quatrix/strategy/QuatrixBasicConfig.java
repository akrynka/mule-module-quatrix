/**
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 **/

package org.mule.modules.quatrix.strategy;

import com.quatrix.api.QuatrixApi;
import com.quatrix.api.QuatrixApiException;
import com.quatrix.api.QuatrixApiImpl;
import com.quatrix.api.config.ApiConfig;
import com.quatrix.api.config.ApiConfigBuilder;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic auth connection management for Quatrix API.
 *
 * @author Aleksey K
 */
@ConnectionManagement(friendlyName = "Basic Auth connection config")
public class QuatrixBasicConfig {

    private static final Logger logger = LoggerFactory.getLogger(QuatrixBasicConfig.class);

    private QuatrixApi quatrixApi;

    @Configurable
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
        if (basePath == null) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, "", "basePath required");
        }

        if (username == null) {
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "", "username required");
        }

        if (password == null) {
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "", "password required");
        }

        final ApiConfig config = new ApiConfigBuilder()
                .setBasePath(basePath)
                .setKeepAliveCallDelay(keepAliveCallDelay)
                .setUsername(username)
                .setPassword(password)
                .build();

        this.quatrixApi = new QuatrixApiImpl(config);
        try {
            quatrixApi.login();
        } catch (QuatrixApiException e) {
            logger.error("Login failed", e);
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "", e.getMessage());
        }
    }

    @Disconnect
    public void disconnect() {
        try {
            quatrixApi.logout();
        } catch (QuatrixApiException e) {
            logger.error("Error during disconnect", e);
        } finally {
            this.quatrixApi = null;
        }
    }

    @ValidateConnection
    public boolean isConnected() {
        return quatrixApi != null && quatrixApi.session() != null;
    }

    @ConnectionIdentifier
    public String connectionId() {
        if (quatrixApi != null && quatrixApi.session() != null) {
            return quatrixApi.session().getId().toString();
        }

        return null;
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