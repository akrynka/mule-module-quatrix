package org.mule.modules.quatrix.automation.testcases;

import com.quatrix.api.model.FileInfo;
import com.quatrix.api.model.Job;
import com.quatrix.api.model.UploadResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class CopyFileTestCase extends QuatrixAbstractTestCases {

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
                targetDirId.toString(),
                testFileName(),
                false
        );

    }

    @Test
    public void testCopyFiles() throws Exception {
        Job job = getConnector().copyFile(
                uploadedFile.getId().toString(),
                targetDirId.toString(),
                true
        );
        assertNotNull(job);
        assertNotNull(job.getId());

        //TODO: assert target dir has two files
    }

    @After
    public void tearDown() throws Exception {
        getConnector().deleteFile(targetDirId.toString());
    }
}
