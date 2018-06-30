package org.mule.modules.quatrix.automation.testcases;

import com.quatrix.api.model.FileMetadata;
import org.junit.Assert;
import org.junit.Test;

public class FileMetadataTestCaseIT extends QuatrixAbstractTestCases {

    @Test
    public void testGetHomeDirMetadata() throws Exception {

        FileMetadata metadata = getConnector().homeMetadata(true);
        Assert.assertNotNull(metadata);
        Assert.assertNotNull(metadata.getContent());
        Assert.assertFalse(metadata.getContent().isEmpty());
    }

    @Test
    public void testGetHomeMetaDataWithoutContent() throws Exception {
        FileMetadata metadata = getConnector().homeMetadata(false);
        Assert.assertNotNull(metadata);
        Assert.assertTrue(metadata.getContent().isEmpty());
    }
}
