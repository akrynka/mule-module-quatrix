/**
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 **/

package org.mule.modules.quatrix;

import com.quatrix.api.QuatrixApi;
import com.quatrix.api.QuatrixApiException;
import com.quatrix.api.model.FileIds;
import com.quatrix.api.model.FileInfo;
import com.quatrix.api.model.FileMetadata;
import com.quatrix.api.model.FileRenameResult;
import com.quatrix.api.model.Job;
import com.quatrix.api.model.UploadResult;
import org.mule.api.MuleException;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.api.annotations.param.Default;
import org.mule.modules.quatrix.strategy.QuatrixBasicConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.UUID;

/**
 * Cloud Connector
 *
 * @author MuleSoft, Inc.
 */
@Connector(name = "quatrix", friendlyName = "Quatrix")
public class QuatrixConnector {

    private QuatrixApi quatrixApi;

    /**
     * Connection Strategy
     */
    @Config
    QuatrixBasicConfig connectionStrategy;

    @Stop
    public void onStop() throws MuleException {
        try {
            this.quatrixApi.logout();
        } catch (QuatrixApiException e) {
            throw new MuleException(e) {};
        }
    }

    /**
     *  Get user home directory metadata.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:home-metadata}
     *
     * @param content if true then directory content will be included in response
     * @return {@link FileMetadata}
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     */
    @Processor
    public FileMetadata homeMetadata(@Default("true") boolean content) throws QuatrixApiException {
        return this.quatrixApi.getHomeDirMeta(content);
    }

    /**
     * Get file or directory metadata.
     *
     * {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:file-metadata}
     *
     * @param fileId File or directory id
     * @param content if true then directory content will be included in response
     * @return {@link FileMetadata}
     */
    @Processor
    public FileMetadata fileMetadata(String fileId, @Default("true") boolean content) throws QuatrixApiException {
        return this.quatrixApi.getFileMetadata(UUID.fromString(fileId), content);
    }

    /**
     *  Renames target file or directory with provided fileId using new name defined in newFileName.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:rename-file}
     *
     * @param fileId File or directory id
     * @param newFileName result file or directory name
     * @param resolve if 'true' then possible name conflict will be resolved automatically
     * @return {@link FileRenameResult}
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     */
    @Processor
    public FileRenameResult renameFile(String fileId, String newFileName, @Default("true") Boolean resolve)
            throws QuatrixApiException {
        return this.quatrixApi.renameFile(UUID.fromString(fileId), newFileName, resolve);
    }

    /**
     *  Delete file by id from remote file system.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:delete-file}
     *
     * @param fileId File id
     * @return {@link FileIds}
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     */
    @Processor
    public FileIds deleteFile(String fileId) throws QuatrixApiException {
        return this.quatrixApi.deleteFile(UUID.fromString(fileId));
    }

    /**
     *  Download single or multiple files. If defined more than 1 file will get ZIP archive.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:download-file}
     *
     * @param fileId File ids for download
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     * @return {@link File}
     */
    @Processor
    public File downloadFile(String fileId) throws QuatrixApiException {
        return this.quatrixApi.download(UUID.fromString(fileId));
    }

    /**
     *  Upload single file.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:upload-file}
     *
     * @param filePath File path on local file system
     * @param parentId Id of target folder where file should be placed
     * @param fileName File name
     * @param resolve if true then API automatically resolve any file name conflicts
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     */
    @Processor
    public UploadResult uploadFile(String filePath, String parentId, String fileName, @Default("true") boolean resolve)
            throws QuatrixApiException {

        return this.quatrixApi.upload(
            Paths.get(filePath).toFile(),
            UUID.fromString(parentId),
            fileName, resolve
        );
    }

    /**
     * Copy files to new target.
     *
     * {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:copy-file}
     *
     * @param fileId list of files
     * @param destDir destination directory
     * @param resolve if 'true' then possible name conflict will be resolved automatically
     *
     * @return {@link Job}
     *
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     */
    @Processor
    public Job copyFile(String fileId, String destDir, @Default("true") boolean resolve)
            throws QuatrixApiException {

        return this.quatrixApi.copyFiles(
            Collections.singletonList(UUID.fromString(fileId)), UUID.fromString(destDir), resolve
        );
    }

    /**
     *  Create directory.
     *
     *  {@sample.xml ../../../doc/Quatrix-connector.xml.sample quatrix:create-dir}
     *
     * @param target destination directory
     * @param dirName name directory
     * @param resolve if 'true' then possible name conflict will be resolved automatically
     * @return {@link FileInfo}
     * @throws com.quatrix.api.QuatrixApiException if Quatrix API is not available or network issues
     */
    @Processor
    public FileInfo createDir(String target, String dirName, @Default("true") boolean resolve)
            throws QuatrixApiException {

        return this.quatrixApi.createDir(UUID.fromString(target), dirName, resolve);
    }

    /**
     * Get Connection Strategy
     */
    public QuatrixBasicConfig getConnectionStrategy() {
        return connectionStrategy;
    }

    /**
     * Set Connection Strategy
     *
     * @param connectionStrategy Connection Strategy
     */
    public void setConnectionStrategy(QuatrixBasicConfig connectionStrategy) {
        this.quatrixApi = connectionStrategy.getQuatrix();
        this.connectionStrategy = connectionStrategy;
    }
}
