package org.quatrix.automation.testcases;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileIds;
import org.quatrix.model.FileRenameResult;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FileDeleteTestCase extends QuatrixParentTestCase {

    //TODO(ringil): replace with upload flow
    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("deleteFilesTestData");
        upsertOnTestRunMessage("ids", UUID.fromString("4c1405b9-9c82-4b85-9a9c-993029e6706c"));
    }

    @Category(SmokeTests.class)
    @Test
    public void testRenameFile() {
        try {
            FileIds fileIds = runFlowAndGetPayload("delete-files");
            assertNotNull(fileIds);
            assertNotNull(fileIds.getIds().get(0));
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
