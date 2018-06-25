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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class UploadResultTestCase extends QuatrixParentTestCase {

    private UUID uploadedFile;
    private String fileName = UUID.randomUUID().toString();

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("uploadFileTestData");
        initializeTestRunMessage("homeMetaTestData");

        upsertOnTestRunMessage("parentId", ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getId().toString());
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testUpload.txt")).toURI());
        upsertOnTestRunMessage("filePath", path.toString());
        upsertOnTestRunMessage("fileName", fileName);
        upsertOnTestRunMessage("resolveConflict", "true");
    }

    @Category(SmokeTests.class)
    @Test
    public void testUploadFile() {
        try {
            UploadResult uploadResult = runFlowAndGetPayload("upload-file");
            uploadedFile = uploadResult.getId();
            assertNotNull(uploadResult);
            assertNotNull(uploadResult.getId());

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {
        if (uploadedFile != null) {
            initializeTestRunMessage("deleteFilesTestData");
            upsertOnTestRunMessage("ids", uploadedFile);
            runFlowAndGetPayload("delete-files");
        }
    }
}
