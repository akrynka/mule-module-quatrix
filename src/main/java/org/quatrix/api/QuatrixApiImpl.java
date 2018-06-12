package org.quatrix.api;

import com.squareup.okhttp.OkHttpClient;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.FileMetadataGetResp;
import io.swagger.client.model.SessionLoginResp;

import java.math.BigDecimal;
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

    public QuatrixApiImpl(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.authApi = new AuthApi(apiClient);
        this.fileApi = new FileApi(apiClient);

        setupKeepAliveCallback(KEEP_ALIVE_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public SessionLoginResp login() throws QuatrixApiException {
        try {
            return this.authApi.sessionLoginGet();
        } catch (ApiException e) {
            throw asQuatrixException(e);
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
    public AuthApi auth() {
        return authApi;
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
                    authApi.sessionLoginGet();
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }, delay, delay, timeUnit);
    }
}
