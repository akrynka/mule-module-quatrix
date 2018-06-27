package org.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.util.FileUtils;
import org.quatrix.model.FileMetadata;

import java.io.File;
import java.util.Collections;
import java.util.UUID;

public class FileDownloadTestCaseIT extends QuatrixAbstractTestCases {

    private File testFile;
    private long testFileCrc;
    private UUID uploadedFileId;

    @Before
    public void setUp() throws Exception {
        testFile = createTestTmpFile();
        testFileCrc = FileUtils.checksumCRC32(testFile);

        FileMetadata metadata = getConnector().homeMetadata(false);
        uploadedFileId = getConnector().uploadFile(
                testFile.getAbsolutePath(),
                metadata.getId(),
                testFile.getName(),
                false
        ).getId();
    }

    @After
    public void tearDown() throws Exception {
        getConnector().deleteFile(uploadedFileId);
    }

    @Test
    public void testDownloadFile() throws Exception {
        File file = getConnector().downloadFile(uploadedFileId);
        Assert.assertNotNull(file);
        Assert.assertEquals(testFile.length(), file.length());
        Assert.assertEquals(testFileCrc, FileUtils.checksumCRC32(file));
    }
}

