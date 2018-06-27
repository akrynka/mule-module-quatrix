package org.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quatrix.model.UploadResult;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

public class UploadFileTestCaseIT extends QuatrixAbstractTestCases {

    private UUID targetDirId;
    private File testFile;
    private UploadResult uploadResult;

    @Before
    public void setUp() throws Exception {
        targetDirId = getHomeDirMetaData().getId();
        testFile = createTestTmpFile();
    }

    @Test
    public void testUploadFile() {
        uploadResult = getConnector().uploadFile(
                testFile.getAbsolutePath(),
                targetDirId,
                testFile.getName(),
                false
        );

        Assert.assertNotNull(uploadResult);
        Assert.assertEquals(BigDecimal.valueOf(testFile.length()), uploadResult.getSize());
    }

    @After
    public void tearDown() throws Exception {
       if (uploadResult != null) {
           getConnector().deleteFile(uploadResult.getId());
       }
    }
}
