/**
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 **/
        
/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.quatrix;

import io.swagger.client.ApiClient;
import io.swagger.client.model.FileMetadataGetResp;
import io.swagger.client.model.FileRenameReq;
import io.swagger.client.model.FileRenameResp;
import org.mule.api.MuleException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.quatrix.api.QuatrixApi;
import org.quatrix.api.QuatrixApiImpl;
import org.quatrix.strategy.QuatrixConnectorConnectionStrategy;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="quatrix", schemaVersion="1.0-SNAPSHOT", friendlyName="quatrix")
public class QuatrixConnector
{

    //TODO: move to config class
    //A @Configurable field can not repeat the name of any parameter that belongs to the @Connect method
//    @Configurable
    private String username;

    //TODO: move to config class
    //A @Configurable field can not repeat the name of any parameter that belongs to the @Connect method
//    @Configurable
    private String password;

    private QuatrixApi quatrixApi;

    /**
     * Connection Strategy
     */
    @ConnectionStrategy
    QuatrixConnectorConnectionStrategy connectionStrategy;

    /**
     * This method initiates quatrix api client and setup basic auth params.
     */
    @Start
    public void init() {
        ApiClient apiClient = new ApiClient();
        apiClient.setUsername(username);
        apiClient.setPassword(password);
        this.quatrixApi = new QuatrixApiImpl(apiClient);
    }

    @Stop
    public void onStop() throws MuleException {
        this.quatrixApi.close();
    }

    /**
     *  Get user home directory metadata.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:home-metadata}
     *
     * @param content if '1' then directory content will be included in response
     * @return {@link FileMetadataGetResp}
     * @throws MuleException if Quatrix API is not available or network issues
     */
    @Processor
    public FileMetadataGetResp getHomeMetadata(@Optional @Default("1") BigDecimal content) throws MuleException {
        return this.quatrixApi.getHomeDirMeta(content);
    }

    /**
     *  Get user home directory metadata.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:home-metadata}
     *
     * @param uuid
     * @param name
     * @param resolve if 'true' rename file operation will be resolved
     * @return {@link FileMetadataGetResp}
     * @throws FileRenameResp if Quatrix API is not available or network issues
     */
    @Processor
    public FileRenameResp renameFile(UUID uuid, String name, @Optional @Default("true") Boolean resolve) throws MuleException {
        FileRenameReq body = new FileRenameReq();
        body.setName(name);
        body.setResolve(resolve);

        return this.quatrixApi.renameFile(uuid, body);
    }

    //TODO: implement
    @Processor
    public void deleteFile() {

    }

    //TODO: implement
    @Processor
    public void downloadFile() {

    }

    //TODO: implement
    @Processor
    public void uploadFile() {

    }

    @Processor
    public void copyFile() {

    }

    //TODO: implement
    @Processor
    public void createDir() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get Connection Strategy
     */
    public QuatrixConnectorConnectionStrategy getConnectionStrategy() {
        return connectionStrategy;
    }

    /**
     * Set Connection Strategy
     *
     * @param connectionStrategy Connection Strategy
     */
    public void setConnectionStrategy(QuatrixConnectorConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }
}
