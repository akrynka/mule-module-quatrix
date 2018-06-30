package org.mule.modules.quatrix.automation.testcases;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.quatrix.model.FileIds;
import org.mule.modules.quatrix.model.FileMetadata;
import org.mule.modules.quatrix.model.UploadResult;

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
                homeDir.getId().toString(),
                testFile.getName(),
                false
        );
    }

    @Test
    public void testDeleteFile() throws Exception {
        FileIds ids = getConnector().deleteFile(uploadedFile.getId().toString());
        Assert.assertNotNull(ids);
        Assert.assertFalse(ids.getIds().isEmpty());
    }
}
