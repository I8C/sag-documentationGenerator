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

import be.i8c.sag.documentationgenerator.out.IDocumentationWriter;
import be.i8c.sag.documentationgenerator.out.IntermediateDocumentationWriter;
import be.i8c.sag.nodendf.NodeNdfStream;
import be.i8c.sag.nodendf.NodePackage;
import be.i8c.sag.util.TestingUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.*;
import java.util.Collections;

/**
 * Test class for the GeneratorCore class
 *
 * @see GeneratorCore
 */
public class TestGeneratorCore {

    /**
     * Test the function generateDocumentation
     *
     * @throws IOException When the output file could not be created
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
    @Test
    public void testGenerateDocumentation() throws IOException, GeneratorException {
        GeneratorCore core = new GeneratorCore();
        core.generateDocumentation(createPackages(), createDocumentationsGens());
    }

    /**
     * Test the function generateIntermediateXml
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
    @Test
    public void testGenerateIntermediateXml() throws GeneratorException, FileNotFoundException {
        GeneratorCore core = new GeneratorCore();
        Document document = core.generateIntermediateXml(createPackages());
        Assert.assertNotNull(document);
    }

    /**
     * Test the function generateDocumentationFromIntermediateXml
     * @throws IOException When the output file could not be created
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
    @Test
    public void testGenerateDocumentationFromIntermediateXml() throws IOException, GeneratorException {
        GeneratorCore core = new GeneratorCore();
        Document document = core.generateIntermediateXml(createPackages());
        Assert.assertNotNull(document);
        core.generateDocumentationFromIntermediateXml(document, createDocumentationsGens());
    }

    /**
     * Return a singleton with an IDocumentWriter
     *
     * @return A singleton with an IDocumentWriter
     * @throws IOException When the output file could not be created
     */
    private Iterable<IDocumentationWriter> createDocumentationsGens() throws IOException {
        File output = new File(TestingUtils.getOutputDir(), "test.xml");
        if (!output.exists())
            output.createNewFile();

        output.deleteOnExit();
        return Collections.singleton(new IntermediateDocumentationWriter(new FileOutputStream(output)));
    }

    /**
     * Create a single package Acme from the package directory
     * and return it as an array.
     *
     * @return An array with a single package
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
    private NodePackage[] createPackages() throws FileNotFoundException, GeneratorException {
        NodePackage nodePackage = new NodePackage("Acme", new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3")));

        File file = new File(TestingUtils.getPackageDir(), "Acme/ns/acme/ff/node.idf".replace('/', File.separatorChar));
        String fileName = file.getParentFile().toString();
        String service = file.getParentFile().getName();
        String prefix = fileName.substring(fileName.indexOf("ns" + File.separator) + 3, fileName.indexOf(File.separator + service)).replace(File.separator, ".");

        nodePackage.addNodeNdfStream(new NodeNdfStream(new FileInputStream(file), service, prefix));

        return new NodePackage[]{nodePackage};
    }

    /**
     * Clean up all of the generated files
     */
    @After
    public void tearDown() {
        TestingUtils.cleanOutput();
    }
}
