package org.quatrix.api;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quatrix.api.config.ApiConfigBuilder;
import org.quatrix.model.FileIds;
import org.quatrix.model.FileRenameResult;
import org.quatrix.model.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuatrixRealApiTest {

    private ApiConfigBuilder apiConfigBuilder;
    private QuatrixApiImpl api;

    @Before
    public void init() {
        apiConfigBuilder = new ApiConfigBuilder();
            apiConfigBuilder.setUsername("alexeykrynka@gmail.com");
            apiConfigBuilder.setPassword("Quatrix_Connector");
            apiConfigBuilder.setBasePath("https://rabulus.quatrix.it/api/1.0");

        api = new QuatrixApiImpl(apiConfigBuilder.build());
        Session session = api.login();
    }

    @After
    public void unInit() {
        api.logout();
    }

    @Test
    public void testDeleteFiles() throws QuatrixApiException {
        List<UUID> ids = new ArrayList<>();

        testUploadFile();
        ids.add(api.getFileMetadata(UUID.fromString("26317fc8-19bd-40db-9177-50ad8c65c9d3"), true).getContent().get(0).getId());
        FileIds response = api.deleteFiles(ids);

        Assert.assertEquals(ids, response.getIds());
    }

    @Test
    public void testRenameFile() throws QuatrixApiException {
        String fileName = "rename.patch";
        boolean resolve = true;

        FileRenameResult response = api.renameFile(api.getFileMetadata(UUID.fromString("26317fc8-19bd-40db-9177-50ad8c65c9d3"), true).getContent().get(0).getId(), fileName, resolve);

        Assert.assertEquals(fileName, response.getName());
    }

    @Test
    public void testDownloadFile() throws QuatrixApiException {
        List<UUID> ids = new ArrayList<>();
        ids.add(api.getFileMetadata(UUID.fromString("26317fc8-19bd-40db-9177-50ad8c65c9d3"), true).getContent().get(0).getId());

        File file = api.download(ids);

        Assert.assertEquals(api.getFileMetadata(UUID.fromString("26317fc8-19bd-40db-9177-50ad8c65c9d3"), true).getName(), file.getName());
    }

    @Test
    public void testUploadFile() throws QuatrixApiException {
        File file = new File("/Users/apple_039/Documents/Foxtrot/quatrix-connector/src/test/java/HttpTest.java");

        api.upload(file, UUID.fromString("26317fc8-19bd-40db-9177-50ad8c65c9d3"), "aaaaaaa.java", true);

        Assert.assertEquals(file.getName(),  api.getFileMetadata(UUID.fromString("26317fc8-19bd-40db-9177-50ad8c65c9d3"), true).getName());
    }

    @Test(expected = QuatrixApiException.class)
    public void testLogout() throws QuatrixApiException {

        api.logout();
        testRenameFile();
    }
}
