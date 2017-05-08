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
package be.i8c.sag.documentationgenerator;

import be.i8c.sag.util.TestingUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Test class for the XsltTransformer class
 *
 * @see XsltTransformer
 */
public class TestXsltTransformer {
    /**
     * Create a transformer by supplying a file from within the jar
     *
     * @throws TransformerConfigurationException When the transformer could not be created
     */
    @Test
    public void testGetXsltTransformerByName() throws TransformerConfigurationException {
        Assert.assertNotNull(XsltTransformer.getXsltTransformer("NodeNdfToIntermediateV1.xslt"));
    }

    /**
     * Create a transformer by supplying a file from a stream
     *
     * @throws TransformerConfigurationException When the transformer could not be created
     * @throws FileNotFoundException When the file was not found
     */
    @Test
    public void testGetXsltTransformerByStream() throws TransformerConfigurationException, FileNotFoundException {
        Assert.assertNotNull(XsltTransformer.getXsltTransformer(
                new FileInputStream(new File(TestingUtils.getExternalResourceDir(), "xslt"+File.separator+"NodeNdfToIntermediateV1.xslt"))
        ));
    }
}
