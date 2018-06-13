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
import java.util.concurrent.TimeUnit;

public final class QuatrixApiImpl implements QuatrixApi {
    private static final long KEEP_ALIVE_DELAY = 5;

    /**
     * General API client which makes all real calls to an API. <br />
     */
    private final ApiClient apiClient;
    private final ScheduledExecutorService keepAliveCallExecutor
            = Executors.newSingleThreadScheduledExecutor();
    private final AuthApi authApi;
    private final FileApi fileApi;

    private UUID sessionId = null;

    public QuatrixApiImpl(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.authApi = new AuthApi(apiClient);
        this.fileApi = new FileApi(apiClient);
    }

    @Override
    public SessionLoginResp login() throws QuatrixApiException {
        if (sessionId == null) {
            try {
                SessionLoginResp response = this.authApi.sessionLoginGet();
                setupKeepAliveCallback(KEEP_ALIVE_DELAY, TimeUnit.SECONDS);

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
        //TODO: implement
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
        //TODO: implement
        return null;
    }

    @Override
    public IdsResp deleteFile(IdsReq req) throws QuatrixApiException {
        //TODO: implement
        return null;
    }

    @Override
    public FileResp createDir(MakeDirReq req) throws QuatrixApiException {
        //TODO: implement
        return null;
    }

    @Override
    public JobResp copyFile(CopyMoveFilesReq req) throws QuatrixApiException {
        //TODO: implement
        return null;
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

    private void setupKeepAliveCallback(long delay, TimeUnit timeUnit) {
        keepAliveCallExecutor.scheduleAtFixedRate(new Runnable() {
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
}
