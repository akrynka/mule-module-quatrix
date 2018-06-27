package org.quatrix.api;

import com.squareup.okhttp.OkHttpClient;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.CopyMoveFilesReq;
import io.swagger.client.model.FileRenameReq;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.MakeDirReq;
import org.quatrix.api.config.ApiConfig;
import org.quatrix.model.FileIds;
import org.quatrix.model.FileInfo;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.FileRenameResult;
import org.quatrix.model.Job;
import org.quatrix.model.Session;
import org.quatrix.model.UploadResult;
import org.quatrix.util.QuatrixThrowable;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class QuatrixApiImpl implements QuatrixApi {

    private static final long KEEP_ALIVE_DELAY = 5L;

    ScheduledExecutorService keepAliveCallExecutor
            = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> keepAliveCallFuture;

    /**
     * General API client which makes all real calls to an API. <br />
     */
    private ApiClient apiClient;
    private AuthApi authApi;
    private FileApi fileApi;
    private FileTransferApi fileTransferApi;

    private UUID sessionId = null;

    public QuatrixApiImpl(ApiConfig config) {
        this.apiClient = createClient(config);
        this.authApi = new AuthApi(this.apiClient);
        this.fileApi = new FileApi(this.apiClient);
        this.fileTransferApi = new FileTransferApiImpl(this.apiClient);
    }

    public void setKeepAliveCallExecutor(ScheduledExecutorService keepAliveCallExecutor) {
        this.keepAliveCallExecutor = keepAliveCallExecutor;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setAuthApi(AuthApi authApi) {
        this.authApi = authApi;
    }

    public void setFileApi(FileApi fileApi) {
        this.fileApi = fileApi;
    }

    public void setFileTransferApi(FileTransferApi fileTransferApi) {
        this.fileTransferApi = fileTransferApi;
    }

    @Override
    public Session login() {
        if (sessionId == null) {
            try {
                Session session = Session.from(this.authApi.sessionLoginGet());
                sessionId = session.getId();
                this.apiClient.setApiKey(sessionId.toString());

                setupKeepAliveCallback(KEEP_ALIVE_DELAY, TimeUnit.MINUTES);

                return session;
            } catch (ApiException e) {
                return QuatrixThrowable.throwNow(e);
            }
        } else {
            return new Session(sessionId);
        }
    }

    @Override
    public void logout() {
        try {
            this.authApi.sessionLogoutGet();
        } catch (ApiException e) {
            QuatrixThrowable.throwNow(e);
        } finally {
            this.apiClient.setApiKey(null);
            cancelKeepAliveCallback();
        }
    }

    @Override
    public FileMetadata getHomeDirMeta(boolean includeContent) {
        final BigDecimal content = includeContent ? BigDecimal.ONE : BigDecimal.ZERO;
        try {
            return FileMetadata.from(this.fileApi.fileMetadataGet(content));
        } catch (ApiException e) {
            return QuatrixThrowable.throwNow(e);
        }
    }

    @Override
    public FileMetadata getFileMetadata(UUID uuid, boolean includeContent) {
        final BigDecimal content = includeContent ? BigDecimal.ONE : BigDecimal.ZERO;
        try {
            return FileMetadata.from(this.fileApi.fileMetadataIdGet(uuid, content));
        } catch (ApiException e) {
            return QuatrixThrowable.throwNow(e);
        }
    }

    @Override
    public FileRenameResult renameFile(UUID uuid, String name, boolean resolveConflict) {
        final FileRenameReq req = new FileRenameReq()
                .name(name)
                .resolve(resolveConflict);

        try {
            return FileRenameResult.from(this.fileApi.fileRenameIdPost(uuid, req));
        } catch (ApiException e) {
            return QuatrixThrowable.throwNow(e);
        }
    }

    @Override
    public FileIds deleteFile(UUID fileId) {
        final IdsReq req = new IdsReq().addIdsItem(fileId);

        try {
            return FileIds.from(this.fileApi.fileDeletePost(req));
        } catch (ApiException e) {
            return QuatrixThrowable.throwNow(e);
        }
    }

    @Override
    public FileInfo createDir(UUID targetDir, String name, boolean resolveConflict) {
        final MakeDirReq req = new MakeDirReq()
                .target(targetDir)
                .name(name)
                .resolve(resolveConflict);

        try {
            return FileInfo.from(this.fileApi.fileMakedirPost(req));
        } catch (ApiException e) {
            return QuatrixThrowable.throwNow(e);
        }
    }

    @Override
    public Job copyFiles(List<UUID> ids, UUID targetDir, boolean resolveConflict) {
        final CopyMoveFilesReq req = new CopyMoveFilesReq()
                .target(targetDir)
                .ids(ids)
                .resolve(resolveConflict);

        try {
            return Job.from(this.fileApi.fileCopyPost(req));
        } catch (ApiException e) {
            return QuatrixThrowable.throwNow(e);
        }
    }

    @Override
    public File download(UUID fileId) {
        return fileTransferApi.downloadFile(fileId);
    }

    @Override
    public UploadResult upload(File file, UUID parentDir, String name, boolean resolveConflict) {
        return fileTransferApi.uploadFile(parentDir, file, name, resolveConflict);
    }

    @Override
    public void close() {
        keepAliveCallExecutor.shutdownNow();

        try {
            logout();
        } finally {
            final OkHttpClient httpClient = this.apiClient.getHttpClient();
            httpClient.getDispatcher().getExecutorService().shutdown();
            httpClient.getConnectionPool().evictAll();
        }
    }

    private ApiClient createClient(ApiConfig config) {
        final ApiClient client = new ApiClient();
        if (config.getBasePath() != null) {
            client.setBasePath(config.getBasePath());
        }
        client.setUsername(config.getUsername());
        client.setPassword(config.getPassword());

        return client;
    }

    private void setupKeepAliveCallback(long delay, TimeUnit timeUnit) {
        keepAliveCallFuture = keepAliveCallExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    authApi.sessionKeepaliveGet();
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }, delay, delay, timeUnit);
    }

    private void cancelKeepAliveCallback() {
        if (keepAliveCallFuture != null) {
            keepAliveCallFuture.cancel(true);
        }
    }
}
