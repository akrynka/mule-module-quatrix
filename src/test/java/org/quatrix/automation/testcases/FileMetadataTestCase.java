package org.quatrix.automation.testcases;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileMetadata;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FileMetadataTestCase extends QuatrixParentTestCase {

    @Before
    public void setUp() {
        initializeTestRunMessage("homeMetaTestData");
    }

    @Category(SmokeTests.class)
    @Test
    public void testGetHomeDirMetadata() {
        try {
            FileMetadata metadata = runFlowAndGetPayload("get-home-dir-meta");
            assertNotNull(metadata);
            assertNotNull(metadata.getId());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
