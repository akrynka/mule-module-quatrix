package org.mule.modules.quatrix.automation.functional;

import com.quatrix.api.model.FileMetadata;
import com.quatrix.api.model.UploadResult;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mule.modules.quatrix.QuatrixConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Base class for test cases.
 */
public abstract class QuatrixConnectorAbstractTestCase extends AbstractTestCase<QuatrixConnector> {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public QuatrixConnectorAbstractTestCase() {
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

    protected UploadResult uploadToHomeDir(File file, boolean resolve) throws Exception {
        UUID homeDirId = getHomeDirMetaData().getId();
        return getConnector().uploadFile(
            file.getAbsolutePath(),
            homeDirId.toString(),
            file.getName(),
            resolve
        );
    }

    protected FileMetadata getHomeDirMetaData() throws Exception {
        return getConnector().homeMetadata(false);
    }

}
