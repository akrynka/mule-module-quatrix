package org.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.UploadResult;
import org.springframework.util.Assert;

import java.util.UUID;

import static org.junit.Assert.fail;

public class UploadResultTestCase extends QuatrixParentTestCase {

    private UUID fileUuid;

    @Before
    public void setUp() throws Exception {

        initializeTestRunMessage("uploadFileTestData");
        initializeTestRunMessage("homeMetaTestData");

        upsertOnTestRunMessage("parentId", ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getContent().get(4).getId().toString());
        upsertOnTestRunMessage("filePath", "/Users/apple_039/Documents/Foxtrot/quatrix-connector/src/main/resources/quatrix-swagger-api.json");
        upsertOnTestRunMessage("fileName", "someFile.json");
        upsertOnTestRunMessage("resolveConflict", "true");
    }

    @After
    public void tearDown() throws Exception {
        upsertOnTestRunMessage("ids", fileUuid);
        runFlowAndGetPayload("delete-files");
    }

    @Category(SmokeTests.class)
    @Test
    public void testUploadFile() {
        try {
            UploadResult uploadResult = runFlowAndGetPayload("upload-file");
            fileUuid = uploadResult.getId();
            Assert.notNull(uploadResult.getId());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
