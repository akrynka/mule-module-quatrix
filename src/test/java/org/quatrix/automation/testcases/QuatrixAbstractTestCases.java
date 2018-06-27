package org.quatrix.automation.testcases;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.quatrix.QuatrixConnector;
import org.quatrix.model.FileInfo;
import org.quatrix.model.FileMetadata;
import org.quatrix.model.UploadResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Base class for test cases.
 */
public abstract class QuatrixAbstractTestCases extends AbstractTestCase<QuatrixConnector> {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public QuatrixAbstractTestCases() {
        super(QuatrixConnector.class);
    }

    protected String testFileName() {
        return String.format("%s", UUID.randomUUID().toString());
    }

    protected File createTestTmpFile() throws IOException {
        File file = testFolder.newFile(testFileName());
        Files.write(file.toPath(), "test content".getBytes());

        return file;
    }

    protected UploadResult uploadToHomeDir(File file, boolean resolve) {
        UUID homeDirId = getHomeDirMetaData().getId();
        return getConnector().uploadFile(
            file.getAbsolutePath(),
            homeDirId,
            file.getName(),
            resolve
        );
    }

    protected FileMetadata getHomeDirMetaData() {
        return getConnector().homeMetadata(false);
    }

}
