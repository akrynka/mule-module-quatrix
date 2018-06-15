package org.quatrix.api;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.IdsReq;
import io.swagger.client.model.IdsResp;
import io.swagger.client.model.SessionLoginResp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class QuatrixApiTest {

    private ApiClient client = Mockito.mock(ApiClient.class);
    private AuthApi authApi = Mockito.mock(AuthApi.class);
    private FileApi fileApi = Mockito.mock(FileApi.class);
    private ScheduledExecutorService executorService = Mockito.mock(ScheduledExecutorService.class);
    private QuatrixApiImpl api = new QuatrixApiImpl(authApi, fileApi, client);


    @Before
    public void setUp() {
        Mockito.reset(client, authApi, fileApi, executorService);
        api.keepAliveCallExecutor = executorService;
    }

    @Test
    public void testLogin() throws QuatrixApiException, ApiException {
        final UUID testUuid = UUID.randomUUID();

        Mockito.when(authApi.sessionLoginGet()).thenReturn(new SessionLoginResp().sessionId(testUuid));
        SessionLoginResp response = api.login();

        Assert.assertEquals(testUuid, response.getSessionId());

        Mockito.verify(executorService).scheduleAtFixedRate(
                Mockito.any(Runnable.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(TimeUnit.class));
    }

    @Test
    public void testDeleteFiles() throws QuatrixApiException, ApiException {
        final IdsReq req = new IdsReq();

        Mockito.when(fileApi.fileDeletePost(req)).thenReturn(new IdsResp().ids(req.getIds()));
        IdsResp response = api.deleteFiles(req);

        Assert.assertEquals(req.getIds(), response.getIds());
    }
}
