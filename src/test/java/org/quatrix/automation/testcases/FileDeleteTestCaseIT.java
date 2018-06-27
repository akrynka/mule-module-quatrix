package org.quatrix.automation.testcases;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quatrix.model.FileIds;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.UploadResult;

import java.io.File;

public class FileDeleteTestCaseIT extends QuatrixAbstractTestCases {

    private File testFile;
    private UploadResult uploadedFile;

    @Before
    public void setUp() throws Exception {
        testFile = createTestTmpFile();
        FileMetadata homeDir = getConnector().homeMetadata(false);

        uploadedFile = getConnector().uploadFile(
                testFile.getAbsolutePath(),
                homeDir.getId(),
                testFile.getName(),
                false
        );
    }

    @Test
    public void testDeleteFile() {
        FileIds ids = getConnector().deleteFile(uploadedFile.getId());
        Assert.assertNotNull(ids);
        Assert.assertFalse(ids.getIds().isEmpty());
    }
}
