package org.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileInfo;
import org.quatrix.model.FileMetadata;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CreateDirTestCase extends QuatrixParentTestCase {
    private String uid;

    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("homeMetaTestData");
        uid = ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getId().toString();
        initializeTestRunMessage("createDirTestData");
        upsertOnTestRunMessage("target", uid);
    }

    @Category(SmokeTests.class)
    @Test
    public void testCreateDir() throws Exception {
        try {
            FileInfo fileInfo = runFlowAndGetPayload("create-dir");
            assertNotNull(fileInfo);
            assertNotNull(fileInfo.getName());
            uid = fileInfo.getId().toString();
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void removeDir() throws Exception {
        initializeTestRunMessage("deleteFilesTestData");
        upsertOnTestRunMessage("ids", uid);
        runFlowAndGetPayload("delete-files");
    }
}
