package org.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quatrix.model.FileInfo;
import org.quatrix.model.Job;
import org.quatrix.model.UploadResult;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class CopyFileTestCaseIT extends QuatrixAbstractTestCases {

    private File testFile;
    private UUID targetDirId;
    private UploadResult uploadedFile;

    @Before
    public void setUp() throws Exception {
        testFile = createTestTmpFile();
        String homeDirId = getHomeDirMetaData().getId().toString();
        FileInfo dir = getConnector().createDir(homeDirId, testFileName(), false);
        targetDirId = dir.getId();

        uploadedFile = getConnector().uploadFile(
                testFile.getAbsolutePath(),
                targetDirId,
                testFileName(),
                false
        );

    }

    @Test
    public void testCopyFiles() {
        Job job = getConnector().copyFile(
                uploadedFile.getId(),
                targetDirId,
                true
        );
        assertNotNull(job);
        assertNotNull(job.getId());

        //TODO: assert target dir has two files
    }

    @After
    public void tearDown() throws Exception {
        getConnector().deleteFile(targetDirId);
    }
}
