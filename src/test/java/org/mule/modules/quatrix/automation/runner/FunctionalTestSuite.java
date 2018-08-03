package org.mule.modules.quatrix.automation.runner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mule.modules.quatrix.QuatrixConnector;
import org.mule.modules.quatrix.automation.functional.CopyFileIT;
import org.mule.modules.quatrix.automation.functional.CreateDirIT;
import org.mule.modules.quatrix.automation.functional.FileDeleteIT;
import org.mule.modules.quatrix.automation.functional.FileDownloadIT;
import org.mule.modules.quatrix.automation.functional.FileMetadataIT;
import org.mule.modules.quatrix.automation.functional.FileRenameIT;
import org.mule.modules.quatrix.automation.functional.UploadFileIT;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CopyFileIT.class,
        CreateDirIT.class,
        FileDeleteIT.class,
        FileDownloadIT.class,
        FileMetadataIT.class,
        FileRenameIT.class,
        UploadFileIT.class
})
public class FunctionalTestSuite {

    @BeforeClass
    public static void initialiseSuite(){
        ConnectorTestContext.initialize(QuatrixConnector.class);
    }

    @AfterClass
    public static void shutdownSuite() throws Exception {
        ConnectorTestContext.shutDown();
    }
}
