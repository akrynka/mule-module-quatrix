package org.quatrix.automation.testcases;

import com.sun.tools.javac.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.tests.ConnectorTestUtils;
import org.quatrix.automation.QuatrixParentTestCase;
import org.quatrix.automation.SmokeTests;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.Job;
import org.quatrix.model.UploadResult;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JobTestCase extends QuatrixParentTestCase {

    private String copiedFileUid;
    private String uploadedFileUid;

    @Before
    public void setUp() throws Exception {
        // get root dir id
        initializeTestRunMessage("copyFilesTestData");
        initializeTestRunMessage("homeMetaTestData");
        String rootDirUid = ((FileMetadata) runFlowAndGetPayload("get-home-dir-meta")).getId().toString();

        //upload file
        initializeTestRunMessage("uploadFileTestData");
        upsertOnTestRunMessage("parentId", rootDirUid);
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testUpload.txt")).toURI());
        upsertOnTestRunMessage("filePath", path.toString());
        //upsertOnTestRunMessage("fileName", "");
        upsertOnTestRunMessage("resolveConflict", "true");

        // insert uploaded file
        UploadResult uploadedFile = runFlowAndGetPayload("upload-file");
        uploadedFileUid = uploadedFile.getId().toString();

        upsertOnTestRunMessage("ids", Collections.singletonList(uploadedFile.getId()));
        upsertOnTestRunMessage("target", rootDirUid);
        upsertOnTestRunMessage("resolve", "true");
    }

    @Category(SmokeTests.class)
    @Test
    public void testCopyFiles() {
        try {
            Job copyJob = runFlowAndGetPayload("copy-files");
            Assert.notNull(copyJob);
            Assert.notNull(copyJob.getId());

            copiedFileUid = copyJob.getId().toString();

        } catch (Exception e) {
            fail(ConnectorTestUtils.getStackTrace(e));
        }
    }

    @After
    public void tearDown() throws Exception {

        initializeTestRunMessage("deleteFilesTestData");
        upsertOnTestRunMessage("ids", uploadedFileUid);
        runFlowAndGetPayload("delete-files");
// TODO remove copied file
//        upsertOnTestRunMessage("ids", copiedFileUid);
//        runFlowAndGetPayload("delete-files");
    }
}
