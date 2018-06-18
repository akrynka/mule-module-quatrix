package org.quatrix.api;

import com.google.gson.Gson;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.SessionLoginResp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class QuatrixApiMockTest {

    private ApiClient apiClient = new ApiClient();
    private AuthApi authApi = new AuthApi();
    private FileApi fileApi = new FileApi();
    private QuatrixApiImpl api = new QuatrixApiImpl(authApi, fileApi, apiClient);
    private ScheduledExecutorService executorService = Mockito.mock(ScheduledExecutorService.class);
    private Gson gson = new Gson();
    private MockServerClient mockServerClient;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 9000);

    @Before
    public void init(){
        apiClient.setBasePath("http://localhost:9000");
        apiClient.setUsername("alexeykrynka@gmail.com");
        apiClient.setPassword("Quatrix_Connector");

        fileApi.setApiClient(apiClient);
        authApi.setApiClient(apiClient);

        Mockito.reset(executorService);
        api.keepAliveCallExecutor = executorService;
    }

    @Test
    public void testSuccessfulLogin() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("\"Content-type\", \"application/json\""))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson
                                .toJson(new SessionLoginResp()
                                        .sessionId(testUuid))));

        SessionLoginResp response = api.login();
        assertThat(testUuid, equalTo(response.getSessionId()));
    }

    @Test
    public void testMFARequiredLogin() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        apiClient.setUsername(null);
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("\"Content-type\", \"application/json\""))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(207)
                        .withBody(gson
                                .toJson(new SessionLoginResp()
                                        .sessionId(testUuid))));

        SessionLoginResp response = api.login();
        assertThat(testUuid, equalTo(response.getSessionId()));
    }

    @Test(expected = QuatrixApiException.class)
    public void testUnauthorizedLogin() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        apiClient.setUsername("alexeykrynka@gmail.com");
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("\"Content-type\", \"application/json\""))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401)
                        .withBody(gson
                                .toJson(new SessionLoginResp()
                                        .sessionId(testUuid))));

        SessionLoginResp response = api.login();
    }


}

