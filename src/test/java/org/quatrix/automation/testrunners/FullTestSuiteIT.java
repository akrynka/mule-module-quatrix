package org.quatrix.automation.testrunners;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.quatrix.automation.testcases.CopyFileTestCaseIT;
import org.quatrix.automation.testcases.CreateDirTestCaseIT;
import org.quatrix.automation.testcases.FileDeleteTestCaseIT;
import org.quatrix.automation.testcases.FileDownloadTestCaseIT;
import org.quatrix.automation.testcases.FileMetadataTestCaseIT;
import org.quatrix.automation.testcases.FileRenameTestCaseIT;
import org.quatrix.automation.testcases.UploadFileTestCaseIT;

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
