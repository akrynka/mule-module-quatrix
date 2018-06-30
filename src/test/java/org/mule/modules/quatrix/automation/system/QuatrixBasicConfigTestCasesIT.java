package org.mule.modules.quatrix.automation.system;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.modules.quatrix.strategy.QuatrixConnectorBasicConfig;
import org.mule.tools.devkit.ctf.configuration.util.ConfigurationUtils;
import org.mule.tools.devkit.ctf.exceptions.ConfigurationLoadingFailedException;

import java.util.Properties;

public class QuatrixBasicConfigTestCasesIT {

    private Properties validCredentials;

    @Before
    public void setup() throws ConfigurationLoadingFailedException {
        validCredentials = ConfigurationUtils.getAutomationCredentialsProperties();
    }

    @Test
    public void testValidCredentialsConnectivity() {
        final QuatrixConnectorBasicConfig config = new QuatrixConnectorBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        final String username = validCredentials.getProperty("config.username");
        final String password = validCredentials.getProperty("config.password");

        try {
            config.connect(username, password);
        } catch (ConnectionException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = ConnectionException.class)
    public void testNullCredentialsConnectivity() throws ConnectionException {
        final QuatrixConnectorBasicConfig config = new QuatrixConnectorBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        config.connect(null, null);
    }

    @Test(expected = ConnectionException.class)
    public void testInvalidCredentialsConnectivity() throws ConnectionException {
        final QuatrixConnectorBasicConfig config = new QuatrixConnectorBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        config.connect("invalid", "invalid");
    }
}
