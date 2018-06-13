package org.quatrix.api;

import io.swagger.client.model.*;

import java.math.BigDecimal;
import java.util.UUID;

//TODO: extend with other API methods
public interface QuatrixApi {

    SessionLoginResp login() throws QuatrixApiException;

    void logout() throws QuatrixApiException;

    FileMetadataGetResp getHomeDirMeta(BigDecimal content) throws QuatrixApiException;

    FileMetadataGetResp getFileMetadata(UUID uuid, BigDecimal content) throws QuatrixApiException;

    FileRenameResp renameFile(UUID uuid, FileRenameReq body) throws QuatrixApiException;

    IdsResp deleteFiles(IdsReq req) throws QuatrixApiException;

    FileResp createDir(MakeDirReq req) throws QuatrixApiException;

    JobResp copyFile(CopyMoveFilesReq req) throws QuatrixApiException;

    void close() throws QuatrixApiException;
}
