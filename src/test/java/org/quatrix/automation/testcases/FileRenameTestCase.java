package org.quatrix.automation.testcases;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.FileRenameResult;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FileRenameTestCase extends QuatrixParentTestCase {

    //TODO(ringil): replace with upload flow
    @Before
    public void setUp() throws Exception {
        initializeTestRunMessage("renameFileTestData");
        upsertOnTestRunMessage("uuid", UUID.fromString("c87c1171-c655-49b7-a1b6-9da056095d22"));
    }

    @Category(SmokeTests.class)
    @Test
    public void testRenameFile() {
        try {
            FileRenameResult fileRenameResult = runFlowAndGetPayload("rename-file");
            assertNotNull(fileRenameResult);
            assertNotNull(fileRenameResult.getName());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
