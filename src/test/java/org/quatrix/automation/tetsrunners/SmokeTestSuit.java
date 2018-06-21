package org.quatrix.automation.tetsrunners;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.quatrix.automation.SmokeTests;
import org.quatrix.automation.testcases.FileMetadataTestCase;

@RunWith(Categories.class)
@Categories.IncludeCategory(SmokeTests.class)
@Suite.SuiteClasses({
        FileMetadataTestCase.class
})
public class SmokeTestSuit {
}
