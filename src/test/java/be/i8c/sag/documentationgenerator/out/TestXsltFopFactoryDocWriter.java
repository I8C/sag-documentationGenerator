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
package be.i8c.sag.documentationgenerator.out;

import be.i8c.sag.util.FileNames;
import be.i8c.sag.util.TestingUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.MimeConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static be.i8c.sag.documentationgenerator.out.DocumentationHelper.getConfig;
import static be.i8c.sag.documentationgenerator.out.DocumentationHelper.getDocument;

/**
 * Test class for the XsltFopFactoryDocWriter class
 *
 * @see XsltFopFactoryDocWriter
 */
public class TestXsltFopFactoryDocWriter {

    /**
     * Generate PDF documentation
     *
     * @throws IOException When the file couldn't be created
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws SAXException When the manifest file couldn't be parsed to a Document
     * @throws DocumentationGenerationException When the documentation could not be created
     * @throws URISyntaxException When the URL could not be converted to an URI
     */
    @Test
    public void testGeneratePdf() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, DocumentationGenerationException {
        File outputFile = new File(TestingUtils.getOutputDir(), "GeneratedDocumentation.pdf");
        outputFile.createNewFile();

        FileOutputStream fop = new FileOutputStream(outputFile);
        IDocumentationWriter docWriter = new XsltFopFactoryDocWriter(getConfig(), Thread.currentThread().getContextClassLoader().getResourceAsStream(FileNames.Input.INTERMEDIATE_PDF), fop, MimeConstants.MIME_PDF);
        docWriter.createDocumentation(getDocument());
    }

    /**
     * Test if a result is generated
     *
     * @throws IOException When the file couldn't be created
     * @throws DocumentationGenerationException When the documentation could not be created
     * @throws FOPException When failing to create a new FOP instance
     * @throws URISyntaxException When the URL could not be converted to an URI
     */
    @Test
    public void testGetResult() throws IOException, DocumentationGenerationException, FOPException, URISyntaxException {
        File outputFile = new File(TestingUtils.getOutputDir(), "GeneratedDocumentation.pdf");
        outputFile.createNewFile();

        FileOutputStream fop = new FileOutputStream(outputFile);
        XsltFopFactoryDocWriter docWriter = new XsltFopFactoryDocWriter(getConfig(), Thread.currentThread().getContextClassLoader().getResourceAsStream(FileNames.Input.INTERMEDIATE_PDF), fop, MimeConstants.MIME_PDF);
        Assert.assertNotNull(docWriter.getResult());
    }

    /**
     * Clean up all of the generated files
     */
    @After
    public void tearDown() {
        TestingUtils.cleanOutput();
    }
}
