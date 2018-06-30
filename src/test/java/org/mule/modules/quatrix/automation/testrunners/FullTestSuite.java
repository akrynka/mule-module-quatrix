package org.mule.modules.quatrix.automation.testrunners;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mule.modules.quatrix.automation.testcases.CopyFileTestCase;
import org.mule.modules.quatrix.automation.testcases.CreateDirTestCase;
import org.mule.modules.quatrix.automation.testcases.FileDeleteTestCase;
import org.mule.modules.quatrix.automation.testcases.FileDownloadTestCase;
import org.mule.modules.quatrix.automation.testcases.FileMetadataTestCase;
import org.mule.modules.quatrix.automation.testcases.FileRenameTestCase;
import org.mule.modules.quatrix.automation.testcases.UploadFileTestCase;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CopyFileTestCase.class,
        CreateDirTestCase.class,
        FileDeleteTestCase.class,
        FileDownloadTestCase.class,
        FileMetadataTestCase.class,
        FileRenameTestCase.class,
        UploadFileTestCase.class
})
public class FullTestSuite {
}
