package org.quatrix.api;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface FileTransferApi {

    void uploadFile(UUID dirId, File file, String name, boolean resolveConflict) throws QuatrixApiException;

    File downloadFile(List<UUID> fileIds) throws QuatrixApiException;
}
