package org.mule.modules.quatrix.automation.system;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.modules.quatrix.strategy.QuatrixBasicConfig;
import org.mule.tools.devkit.ctf.configuration.util.ConfigurationUtils;
import org.mule.tools.devkit.ctf.exceptions.ConfigurationLoadingFailedException;

import java.util.Properties;

public class QuatrixBasicConfigIT {

    private Properties validCredentials;

    @Before
    public void setup() throws ConfigurationLoadingFailedException {
        validCredentials = ConfigurationUtils.getAutomationCredentialsProperties();
    }

    @Test
    public void testValidCredentialsConnectivity() {
        final QuatrixBasicConfig config = new QuatrixBasicConfig();
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
        final QuatrixBasicConfig config = new QuatrixBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        config.connect(null, null);
    }

    @Test(expected = ConnectionException.class)
    public void testInvalidCredentialsConnectivity() throws ConnectionException {
        final QuatrixBasicConfig config = new QuatrixBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        config.connect("invalid", "invalid");
    }

    @Test(expected = ConnectionException.class)
    public void testConnectivityWithNullBasePath() throws ConnectionException {
        final QuatrixBasicConfig config = new QuatrixBasicConfig();

        final String username = validCredentials.getProperty("config.username");
        final String password = validCredentials.getProperty("config.password");

        config.connect(username, password);
    }

    @Test(expected = ConnectionException.class)
    public void testConnectivityWithNullUsername() throws ConnectionException {
        final QuatrixBasicConfig config = new QuatrixBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        final String password = validCredentials.getProperty("config.password");

        config.connect(null, password);
    }

    @Test(expected = ConnectionException.class)
    public void testConnectivityWithNullPassword() throws ConnectionException {
        final QuatrixBasicConfig config = new QuatrixBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));

        final String username = validCredentials.getProperty("config.username");

        config.connect(username, null);
    }

    @Test
    public void testDisconnect() throws ConnectionException {
        final QuatrixBasicConfig config = new QuatrixBasicConfig();
        config.setBasePath(validCredentials.getProperty("config.basePath"));
        final String username = validCredentials.getProperty("config.username");
        final String password = validCredentials.getProperty("config.password");

        config.connect(username, password);
        Assert.assertTrue(config.isConnected());
        Assert.assertNotNull(config.connectionId());

        config.disconnect();

        Assert.assertFalse(config.isConnected());
        Assert.assertNull(config.connectionId());
    }
}
