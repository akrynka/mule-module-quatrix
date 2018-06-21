package org.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileIds;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.UploadResult;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FileDownloadTestCase extends QuatrixParentTestCase {

    private UUID fileUuid;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("downloadFileTestData");
        initializeTestRunMessage("uploadFileTestData");
        initializeTestRunMessage("homeMetaTestData");

        upsertOnTestRunMessage("parentId", ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getContent().get(4).getId().toString());

        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testUpload.txt")).toURI());
        upsertOnTestRunMessage("filePath", path.toString());

        upsertOnTestRunMessage("fileName", "someFile.json");
        upsertOnTestRunMessage("resolveConflict", "true");

        fileUuid = ((UploadResult) runFlowAndGetPayload("upload-file")).getId();
        upsertOnTestRunMessage("fileIds", ((UploadResult) runFlowAndGetPayload("upload-file")).getId());
    }

    @After
    public void tearDown() throws Exception {
        upsertOnTestRunMessage("ids", fileUuid);
        runFlowAndGetPayload("delete-files");
    }

    @Category(SmokeTests.class)
    @Test
    public void testDownloadFile() {
        try {
            File file = runFlowAndGetPayload("download-file");
            assertNotNull(file);
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}

