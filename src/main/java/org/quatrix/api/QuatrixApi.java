package org.quatrix.api;

import org.quatrix.model.FileIds;
import org.quatrix.model.FileInfo;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.FileRenameResult;
import org.quatrix.model.Job;
import org.quatrix.model.Session;
import org.quatrix.model.UploadResult;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface QuatrixApi {

    Session login();

    void logout();

    FileMetadata getHomeDirMeta(boolean includeContent);

    FileMetadata getFileMetadata(UUID uuid, boolean includeContent);

    FileRenameResult renameFile(UUID uuid, String name, boolean resolveConfilct);

    FileIds deleteFile(UUID fileId);

    FileInfo createDir(UUID targetDir, String name, boolean resolveConflict);

    Job copyFiles(List<UUID> ids, UUID targetDir, boolean resolveConflict);

    File download(UUID fileId);

    UploadResult upload(File file, UUID parentDir, String name, boolean resolveConflict);

    void close();
}
