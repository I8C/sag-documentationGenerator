/*******************************************************************************
 * Copyright (c) 2017, i8c N.V. (Integr8 Consulting; http://www.i8c.be)
 * All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package be.i8c.sag.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Test class for the FileUtils class
 *
 * @see FileUtils
 */
public class FileUtilsTest {

    /**
     * Create a file
     *
     * @throws IOException When failed to create the file
     */
    @Test
    public void testCreateFile() throws IOException {
        File file = FileUtils.createFile(new File(TestingUtils.getOutputDir(), UUID.randomUUID().toString() + "-TESTING.tmp"));
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    /**
     * Get the location of the library file
     */
    @Test
    public void testGetLibraryFile() {
        File file = FileUtils.getJarFile();
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    /**
     * Extract the 'assets' folder from within the jar
     *
     * @throws IOException When the files could not be extracted
     */
    @Test
    public void testExtractAssets() throws IOException {
        File outputResource = new File(TestingUtils.getOutputDir(), "resources");
        if (outputResource.exists())
            TestingUtils.cleanOutput();

        FileUtils.extractAssets(outputResource);
        Assert.assertNotNull(outputResource.listFiles());
        Assert.assertTrue(outputResource.listFiles().length > 0);
    }

    /**
     * Create a temporary FOP config file
     *
     * @throws IOException When failed to create the file
     */
    @Test
    public void testCreateTempFopConf() throws IOException {
        File file = FileUtils.createTempFopConf(getClass().getResourceAsStream("/fop.xconf"));

        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }

    /**
     * Clean up all of the generated files
     */
    @After
    public void tearDown() {
        TestingUtils.cleanOutput();
    }
}
