package org.quatrix.api;

import com.google.gson.Gson;
import io.swagger.client.ApiClient;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.FileApi;
import io.swagger.client.model.*;
import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class QuatrixApiMockTest {

    private ApiClient apiClient = new ApiClient();
    private AuthApi authApi = new AuthApi();
    private FileApi fileApi = new FileApi();
    private QuatrixApiImpl api = new QuatrixApiImpl(authApi, fileApi, apiClient);
    private Gson gson = new Gson();
    private MockServerClient mockServerClient;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 9000);

    @Before
    public void init() {
        apiClient.setBasePath("http://localhost:9000");
        apiClient.setUsername("alexeykrynka@gmail.com");
        apiClient.setPassword("Quatrix_Connector");

        fileApi.setApiClient(apiClient);
        authApi.setApiClient(apiClient);

        mockServerClient.reset();
    }

    @Test
    public void testLoginSuccessful() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("Content-type", "application/json"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson.toJson(new SessionLoginResp()
                                .sessionId(testUuid))));

        SessionLoginResp response = api.login();
        assertThat(testUuid, equalTo(response.getSessionId()));
    }

    @Test
    public void testLoginMFARequired() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        apiClient.setUsername(null);
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("Content-type", "application/json"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(207)
                        .withBody(gson.toJson(new SessionLoginResp()
                                .sessionId(testUuid))));

        SessionLoginResp response = api.login();
        assertThat(testUuid, equalTo(response.getSessionId()));
    }

    @Test(expected = QuatrixApiException.class)
    public void testLoginUnauthorized() throws QuatrixApiException {
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/session/login")
                .withMethod("GET")
                .withHeader("Content-type", "application/json"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        SessionLoginResp response = api.login();
    }

    @Test
    public void testRenameFileSuccessful() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        FileRenameReq testBody = new FileRenameReq();

        mockServerClient.when(HttpRequest
                .request("/file/rename/" + testUuid)
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson.toJson(new FileRenameResp().id(testUuid))));

        FileRenameResp response = api.renameFile(testUuid, testBody);
        assertThat(testUuid, equalTo(response.getId()));
    }

    @Test(expected = QuatrixApiException.class)
    public void testRenameFileBadRequest() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        FileRenameReq testBody = new FileRenameReq();

        mockServerClient.when(HttpRequest
                .request("/file/rename/" + testUuid)
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400));

        FileRenameResp response = api.renameFile(testUuid, testBody);
    }

    @Test(expected = QuatrixApiException.class)
    public void testRenameFileUnauthorized() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/file/rename/" + testUuid)
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        FileRenameResp response = api.renameFile(testUuid, new FileRenameReq());
    }

    @Test
    public void testDeleteFilesSuccessful() throws QuatrixApiException {
        IdsReq req = new IdsReq();

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(200)
                        .withBody(gson.toJson(req)));

        IdsResp response = api.deleteFiles(req);
        assertThat(req.getIds(), equalTo(response.getIds()));
    }

    @Test
    public void testDeleteFilesMoreThen10Successful() throws QuatrixApiException {
        IdsReq req = new IdsReq().ids(insertIds());

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(202)
                        .withBody(gson.toJson(req)));

        IdsResp response = api.deleteFiles(req);
        assertThat(req.getIds(), equalTo(response.getIds()));
    }

    @Test(expected = QuatrixApiException.class)
    public void testDeleteFilesBadRequest() throws QuatrixApiException {
        IdsReq req = new IdsReq();

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400));

        IdsResp response = api.deleteFiles(req);
    }

    @Test(expected = QuatrixApiException.class)
    public void testDeleteFilesUnauthorized() throws QuatrixApiException {
        IdsReq req = new IdsReq();
        apiClient.setPassword(null);

        mockServerClient.when(HttpRequest
                .request("/file/delete")
                .withMethod("POST")
                .withHeader("Content-type", "application/json; charset=utf-8"))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        IdsResp response = api.deleteFiles(req);
    }

    @Test
    public void testMakeDirSuccessful() throws QuatrixApiException {
        final UUID testUuid = UUID.randomUUID();
        MakeDirReq req = new MakeDirReq();

        mockServerClient.when(HttpRequest
                .request("/file/makedir")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(201)
                        .withBody(gson.toJson(new FileResp().id(testUuid))));

        FileResp response = api.createDir(req);
        assertThat(testUuid, equalTo(response.getId()));
    }

    @Test(expected = QuatrixApiException.class)
    public void testMakeDirBadRequest() throws QuatrixApiException {
        MakeDirReq req = new MakeDirReq();

        mockServerClient.when(HttpRequest
                .request("/file/makedir")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400)
                );

        FileResp response = api.createDir(req);
    }

    @Test(expected = QuatrixApiException.class)
    public void testMakeDirUnauthorized() throws QuatrixApiException {
        MakeDirReq req = new MakeDirReq();

        mockServerClient.when(HttpRequest
                .request("/file/makedir")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401)
                );

        FileResp response = api.createDir(req);
    }

    @Test
    public void testFileCopySuccessful() throws QuatrixApiException {
        final UUID testId = UUID.randomUUID();
        CopyMoveFilesReq req = new CopyMoveFilesReq();

        mockServerClient.when(HttpRequest
                .request("/file/copy")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(202)
                        .withBody(gson.toJson(new JobResp().jobId(testId))));

        JobResp response = api.copyFiles(req);

        assertThat(testId, equalTo(response.getJobId()));
    }

    @Test(expected = QuatrixApiException.class)
    public void testFileCopyBadRequest() throws QuatrixApiException {
        CopyMoveFilesReq req = new CopyMoveFilesReq();

        mockServerClient.when(HttpRequest
                .request("/file/copy")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(400));

        JobResp response = api.copyFiles(req);
    }

    @Test(expected = QuatrixApiException.class)
    public void testFileCopyUnauthorized() throws QuatrixApiException {
        CopyMoveFilesReq req = new CopyMoveFilesReq();

        mockServerClient.when(HttpRequest
                .request("/file/copy")
                .withMethod("POST")
                .withBody(gson.toJson(req)))
                .respond(HttpResponse
                        .response()
                        .withStatusCode(401));

        JobResp response = api.copyFiles(req);
    }

    private List<UUID> insertIds() {
        List<UUID> listFilesForDelete = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            listFilesForDelete.add(UUID.randomUUID());
        }

        return listFilesForDelete;
    }
}

