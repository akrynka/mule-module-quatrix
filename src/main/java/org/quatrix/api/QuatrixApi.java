package org.quatrix.api;

import org.quatrix.model.*;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface QuatrixApi {

    Session login() throws QuatrixApiException;

    void logout() throws QuatrixApiException;

    FileMetadata getHomeDirMeta(boolean includeContent) throws QuatrixApiException;

    FileMetadata getFileMetadata(UUID uuid, boolean includeContent) throws QuatrixApiException;

    FileRenameResult renameFile(UUID uuid, String name, boolean resolveConfilct) throws QuatrixApiException;

    FileIds deleteFiles(List<UUID> ids) throws QuatrixApiException;

    FileInfo createDir(UUID targetDir, String name, boolean resolveConflict) throws QuatrixApiException;

    Job copyFiles(List<UUID> ids, UUID targetDir, boolean resolveConflict) throws QuatrixApiException;

    File download(List<UUID> fileIds);

    UploadResult upload(File file, UUID parentDir, String name, boolean resolveConflict);

    void close() throws QuatrixApiException;
}
