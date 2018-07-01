package org.mule.modules.quatrix.automation.functional;

import com.quatrix.api.model.FileInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateDirIT extends QuatrixConnectorAbstractTestCase {
    private UUID homeDirUuid;
    private UUID newDirUuid;
    private String newDirName = String.format("%s.test", UUID.randomUUID().toString());

    @Before
    public void setUp() throws Exception {
        homeDirUuid = getConnector().homeMetadata(false).getId();
    }

    @Test
    public void testCreateDir() throws Exception {
        FileInfo newDir = getConnector().createDir(homeDirUuid.toString(), newDirName, false);
        assertNotNull(newDirName);
        assertEquals(newDirName, newDir.getName());
        newDirUuid = newDir.getId();
    }

    @After
    public void removeDir() throws Exception {
        getConnector().deleteFile(newDirUuid.toString());
    }
}
