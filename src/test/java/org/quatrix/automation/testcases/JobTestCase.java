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
import org.quatrix.model.Job;
import org.quatrix.model.UploadResult;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.fail;

public class JobTestCase extends QuatrixParentTestCase {

    private FileInfo fileInfo;

    @Before
    public void setUp() throws Exception {
        // get root dir id
        initializeTestRunMessage("copyFilesTestData");
        initializeTestRunMessage("homeMetaTestData");
        String rootDirUid = ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getId().toString();

        // make dir
        initializeTestRunMessage("createDirTestData");
        String uid = ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getId().toString();
        upsertOnTestRunMessage("target", uid);
        fileInfo = runFlowAndGetPayload("create-dir");

        //upload file
        initializeTestRunMessage("uploadFileTestData");
        upsertOnTestRunMessage("parentId", fileInfo.getId().toString());
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testUpload.txt")).toURI());
        upsertOnTestRunMessage("filePath", path.toString());
        upsertOnTestRunMessage("resolveConflict", "true");

        // insert uploaded file
        UploadResult uploadedFile = runFlowAndGetPayload("upload-file");

        upsertOnTestRunMessage("ids", Collections.singletonList(uploadedFile.getId()));
        upsertOnTestRunMessage("target", fileInfo.getId().toString());
        upsertOnTestRunMessage("resolve", "true");
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

    @After
    public void tearDown() throws Exception {
        // remove dir with files
        initializeTestRunMessage("deleteFilesTestData");
        upsertOnTestRunMessage("ids", fileInfo.getId().toString());
        runFlowAndGetPayload("delete-files");
    }
}
