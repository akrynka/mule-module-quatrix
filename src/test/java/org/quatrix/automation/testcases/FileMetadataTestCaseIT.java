package org.quatrix.automation.testcases;

import org.junit.Assert;
import org.junit.Test;
import org.quatrix.model.FileMetadata;

public class FileMetadataTestCaseIT extends QuatrixAbstractTestCases {

    @Test
    public void testGetHomeDirMetadata() {

        FileMetadata metadata = getConnector().homeMetadata(true);
        Assert.assertNotNull(metadata);
        Assert.assertNotNull(metadata.getContent());
        Assert.assertFalse(metadata.getContent().isEmpty());
    }

    @Test
    public void testGetHomeMetaDataWithoutContent() {
        FileMetadata metadata = getConnector().homeMetadata(false);
        Assert.assertNotNull(metadata);
        Assert.assertTrue(metadata.getContent().isEmpty());
    }
}
