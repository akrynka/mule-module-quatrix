package org.mule.modules.quatrix.automation.runner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mule.modules.quatrix.automation.system.QuatrixBasicConfigIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        QuatrixBasicConfigIT.class
})
public class SystemTestSuite {
}
