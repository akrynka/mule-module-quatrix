package org.mule.modules.quatrix.api;

import org.mule.modules.quatrix.model.FileIds;
import org.mule.modules.quatrix.model.FileInfo;
import org.mule.modules.quatrix.model.FileMetadata;
import org.mule.modules.quatrix.model.FileRenameResult;
import org.mule.modules.quatrix.model.Job;
import org.mule.modules.quatrix.model.Session;
import org.mule.modules.quatrix.model.UploadResult;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface QuatrixApi {

    Session login() throws QuatrixApiException;

    void logout() throws QuatrixApiException;

    FileMetadata getHomeDirMeta(boolean includeContent) throws QuatrixApiException;

    FileMetadata getFileMetadata(UUID uuid, boolean includeContent) throws QuatrixApiException;

    FileRenameResult renameFile(UUID uuid, String name, boolean resolveConfilct) throws QuatrixApiException;

    FileIds deleteFile(UUID fileId) throws QuatrixApiException;

    FileInfo createDir(UUID targetDir, String name, boolean resolveConflict) throws QuatrixApiException;

    Job copyFiles(List<UUID> ids, UUID targetDir, boolean resolveConflict) throws QuatrixApiException;

    File download(UUID fileId) throws QuatrixApiException;

    UploadResult upload(File file, UUID parentDir, String name, boolean resolveConflict) throws QuatrixApiException;
}
