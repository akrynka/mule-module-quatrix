package org.mule.modules.quatrix.automation.testrunners;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mule.modules.quatrix.automation.testcases.CopyFileTestCaseIT;
import org.mule.modules.quatrix.automation.testcases.CreateDirTestCaseIT;
import org.mule.modules.quatrix.automation.testcases.FileDeleteTestCaseIT;
import org.mule.modules.quatrix.automation.testcases.FileDownloadTestCaseIT;
import org.mule.modules.quatrix.automation.testcases.FileMetadataTestCaseIT;
import org.mule.modules.quatrix.automation.testcases.FileRenameTestCaseIT;
import org.mule.modules.quatrix.automation.testcases.UploadFileTestCaseIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CopyFileTestCaseIT.class,
        CreateDirTestCaseIT.class,
        FileDeleteTestCaseIT.class,
        FileDownloadTestCaseIT.class,
        FileMetadataTestCaseIT.class,
        FileRenameTestCaseIT.class,
        UploadFileTestCaseIT.class
})
public class FullTestSuiteIT {
}
