package org.mule.modules.quatrix.automation.functional;

import com.quatrix.api.model.FileMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.util.FileUtils;

import java.io.File;
import java.util.UUID;

public class FileDownloadIT extends QuatrixConnectorAbstractTestCase {

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
                metadata.getId().toString(),
                testFile.getName(),
                false
        ).getId();
    }

    @After
    public void tearDown() throws Exception {
        getConnector().deleteFile(uploadedFileId.toString());
    }

    @Test
    public void testDownloadFile() throws Exception {
        File file = getConnector().downloadFile(uploadedFileId.toString());
        Assert.assertNotNull(file);
        Assert.assertEquals(testFile.length(), file.length());
        Assert.assertEquals(testFileCrc, FileUtils.checksumCRC32(file));
    }
}

