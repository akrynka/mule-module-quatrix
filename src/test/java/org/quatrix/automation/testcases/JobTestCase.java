package org.quatrix.automation.testcases;

import com.sun.tools.javac.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.Job;
import org.springframework.util.Assert;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JobTestCase extends QuatrixParentTestCase {

    @Before
    public void setUp() {
        initializeTestRunMessage("copyFilesTestData");

        upsertOnTestRunMessage("ids", List.of(UUID.fromString("d0335328-7063-457d-b8c8-c0ae40225a99")));
        upsertOnTestRunMessage("target", UUID.fromString("cbc54965-71f4-490b-b5a5-e5b976ebc67a"));
    }

    @Category(SmokeTests.class)
    @Test
    public void testCopyFiles() {
        try {
            Job copyJob = runFlowAndGetPayload("copy-files");
            Assert.notNull(copyJob);
            Assert.notNull(copyJob.getId());
        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }
}
