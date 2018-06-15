package org.quatrix.api;

import com.squareup.okhttp.OkHttpClient;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.*;

import java.math.BigDecimal;
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
    final ApiClient apiClient;
    final AuthApi authApi;
    final FileApi fileApi;

    private UUID sessionId = null;

    public QuatrixApiImpl(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.authApi = new AuthApi(apiClient);
        this.fileApi = new FileApi(apiClient);
    }

    public QuatrixApiImpl(AuthApi auth, FileApi fileApi, ApiClient apiClient) {
        this.apiClient = apiClient;
        this.authApi = auth;
        this.fileApi = fileApi;
    }

    @Override
    public SessionLoginResp login() throws QuatrixApiException {
        if (sessionId == null) {
            try {
                SessionLoginResp response = this.authApi.sessionLoginGet();
                sessionId = response.getSessionId();

                setupKeepAliveCallback(KEEP_ALIVE_DELAY, TimeUnit.MINUTES);

                return response;
            } catch (ApiException e) {
                throw asQuatrixException(e);
            }
        } else {
            return new SessionLoginResp().sessionId(sessionId);
        }
    }

    @Override
    public void logout() throws QuatrixApiException {
        try {
            this.authApi.sessionLogoutGet();
        } catch (ApiException e) {
            throw asQuatrixException(e);
        } finally {
            cancelKeepAliveCallback();
        }
    }

    @Override
    public FileMetadataGetResp getHomeDirMeta(BigDecimal content) throws QuatrixApiException {
        try {
            return this.fileApi.fileMetadataGet(content);
        } catch (ApiException e) {
            throw asQuatrixException(e);
        }
    }

    @Override
    public FileMetadataGetResp getFileMetadata(UUID uuid, BigDecimal content) throws QuatrixApiException {
        try {
            return this.fileApi.fileMetadataIdGet(uuid, content);
        } catch (ApiException e) {
            throw asQuatrixException(e);
        }
    }

    @Override
    public FileRenameResp renameFile(UUID uuid, FileRenameReq body) throws QuatrixApiException {
        try {
            return this.fileApi.fileRenameIdPost(uuid, body);
        } catch (ApiException e) {
            throw asQuatrixException(e);
        }
    }

    @Override
    public IdsResp deleteFiles(IdsReq req) throws QuatrixApiException {
        try {
            return this.fileApi.fileDeletePost(req);
        } catch (ApiException e) {
            throw asQuatrixException(e);
        }
    }

    @Override
    public FileResp createDir(MakeDirReq req) throws QuatrixApiException {
        try {
            return this.fileApi.fileMakedirPost(req);
        } catch (ApiException e) {
            throw asQuatrixException(e);
        }
    }

    @Override
    public JobResp copyFiles(CopyMoveFilesReq req) throws QuatrixApiException {
        try {
            return this.fileApi.fileCopyPost(req);
        } catch (ApiException e) {
            throw asQuatrixException(e);
        }
    }

    @Override
    public void close() throws QuatrixApiException {
        keepAliveCallExecutor.shutdownNow();

        try {
            // logout required for proper session end
            authApi.sessionLogoutGet();
        } catch (ApiException e) {
            throw asQuatrixException(e);
        } finally {
            final OkHttpClient httpClient = this.apiClient.getHttpClient();
            httpClient.getDispatcher().getExecutorService().shutdown();
            httpClient.getConnectionPool().evictAll();
        }
    }

    private QuatrixApiException asQuatrixException(ApiException ex) {
        return new QuatrixApiException(ex);
    }

    protected void setupKeepAliveCallback(long delay, TimeUnit timeUnit) {
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
