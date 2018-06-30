package org.mule.modules.quatrix.automation.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.quatrix.model.FileRenameResult;
import org.mule.modules.quatrix.model.UploadResult;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileRenameTestCaseIT extends QuatrixAbstractTestCases {

    private File testFile;
    private UploadResult uploadResult;
    private String newFileName;

    @Before
    public void setUp() throws Exception {
        testFile = createTestTmpFile();
        uploadResult = uploadToHomeDir(testFile, false);
        newFileName = testFileName();
    }

    @After
    public void tearDown() throws Exception {
        getConnector().deleteFile(uploadResult.getId().toString());
    }

    @Test
    public void testRenameFile() throws Exception {
        FileRenameResult result = getConnector().renameFile(uploadResult.getId().toString(), newFileName, false);
        assertNotNull(result);
        assertEquals(uploadResult.getId(), result.getId());
        assertEquals(testFile.getName(), result.getOldName());
        assertEquals(newFileName, result.getName());
    }
}