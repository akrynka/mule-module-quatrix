package org.mule.modules.quatrix.automation.testcases;

import com.quatrix.api.model.FileIds;
import com.quatrix.api.model.FileMetadata;
import com.quatrix.api.model.UploadResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
