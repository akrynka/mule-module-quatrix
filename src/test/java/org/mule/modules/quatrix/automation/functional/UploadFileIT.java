package org.mule.modules.quatrix.automation.functional;

import com.quatrix.api.model.UploadResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

public class UploadFileIT extends QuatrixConnectorAbstractTestCase {

    private UUID targetDirId;
    private File testFile;
    private UploadResult uploadResult;

    @Before
    public void setUp() throws Exception {
        targetDirId = getHomeDirMetaData().getId();
        testFile = createTestTmpFile();
    }

    @Test
    public void testUploadFile() throws Exception {
        uploadResult = getConnector().uploadFile(
                testFile.getAbsolutePath(),
                targetDirId.toString(),
                testFile.getName(),
                false
        );

        Assert.assertNotNull(uploadResult);
        Assert.assertEquals(BigDecimal.valueOf(testFile.length()), uploadResult.getSize());
    }

    @After
    public void tearDown() throws Exception {
       if (uploadResult != null) {
           getConnector().deleteFile(uploadResult.getId().toString());
       }
    }
}
