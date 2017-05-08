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
package be.i8c.sag.nodendf;

import be.i8c.sag.documentationgenerator.GeneratorException;
import be.i8c.sag.util.TestingUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Test class for the NodePackage class
 *
 * @see NodePackage
 */
public class TestNodePackage {
    /**
     * Create a NodePackage
     *
     * @throws FileNotFoundException When the manifest file can not be found
     * @throws GeneratorException When a node could not be made
     */
    @Test
    public void testCreate() throws FileNotFoundException, GeneratorException {
        new NodePackage("Acme", new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3")));
    }

    /**
     * Add a NodeNdfStream to a NodePackage
     *
     * @throws FileNotFoundException When the manifest file can not be found
     * @throws GeneratorException When a node could not be made
     *
     * @see NodeNdfStream
     */
    @Test
    public void testAddNodeNdfStream() throws FileNotFoundException, GeneratorException {
        NodePackage nodePackage = new NodePackage("Acme", new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3")));

        File file = new File(TestingUtils.getPackageDir(), "Acme/ns/acme/ff/node.idf".replace('/', File.separatorChar));
        String fileName = file.getParentFile().toString();
        String service = file.getParentFile().getName();
        String prefix = fileName.substring(fileName.indexOf("ns" + File.separator) + 3, fileName.indexOf(File.separator + service)).replace(File.separator, ".");

        nodePackage.addNodeNdfStream(new NodeNdfStream(new FileInputStream(file), service, prefix));
    }


    /**
     * Get a NodeNdfStream from a NodePackage
     *
     * @throws FileNotFoundException When the manifest file can not be found
     * @throws GeneratorException When a node could not be made
     *
     * @see NodeNdfStream
     */
    @Test
    public void testGetNodeNdfStreams() throws FileNotFoundException, GeneratorException {
        NodePackage nodePackage = new NodePackage("Acme", new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3")));

        File file = new File(TestingUtils.getPackageDir(), "Acme/ns/acme/ff/node.idf".replace('/', File.separatorChar));
        String fileName = file.getParentFile().toString();
        String service = file.getParentFile().getName();
        String prefix = fileName.substring(fileName.indexOf("ns" + File.separator) + 3, fileName.indexOf(File.separator + service)).replace(File.separator, ".");

        NodeNdfStream stream = new NodeNdfStream(new FileInputStream(file), service, prefix);
        nodePackage.addNodeNdfStream(stream);

        Assert.assertNotNull(nodePackage.getNodeNdfStreams());
        Assert.assertTrue(nodePackage.getNodeNdfStreams().size() == 1);

        List<NodeNdfStream> storedStreams = nodePackage.getNodeNdfStreams().get(prefix);
        Assert.assertNotNull(storedStreams);
        Assert.assertTrue(storedStreams.size() == 1);

        Assert.assertEquals(storedStreams.get(0), stream);
    }


    /**
     * Get the manifest file of the NodePackage
     *
     * @throws FileNotFoundException When the manifest file can not be found
     * @throws GeneratorException When a node could not be made
     */
    @Test
    public void testGetManifest() throws FileNotFoundException, GeneratorException {
        FileInputStream manifest = new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3"));
        NodePackage nodePackage = new NodePackage("Acme", manifest);

        Assert.assertNotSame(nodePackage.getManifest(), manifest);
    }

    /**
     * Invoke the initialization method for the NodePackage
     *
     * @throws FileNotFoundException When the manifest file can not be found
     * @throws GeneratorException When a node could not be made
     * @throws NoSuchMethodException If the method name, used for reflection, does not exist
     * @throws InvocationTargetException If the underlying method, used for reflection, throws an exception.
     * @throws IllegalAccessException When the method, used for reflection, can not be accessed
     */
    @Test
    public void testAddPackageName() throws FileNotFoundException, GeneratorException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        FileInputStream manifest = new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3"));
        NodePackage nodePackage = new NodePackage("Acme", manifest);

        FileInputStream manifest2 = new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3"));

        Object response = TestingUtils.invoke(nodePackage, NodePackage.class, "addPackageName", new Class[]{InputStream.class, String.class}, new Object[]{manifest2, "Acme"});
        if (response instanceof InputStream)
            Assert.assertNotNull(response);
    }

    /**
     * Check if the manifest has a package name
     *
     * @throws IOException When the input could not be read or when a temporary file could not be created
     * @throws GeneratorException When a node could not be made
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws SAXException When the manifest file couldn't be parsed to a Document
     * @throws NoSuchMethodException If the method name, used for reflection, does not exist
     * @throws InvocationTargetException If the underlying method, used for reflection, throws an exception.
     * @throws IllegalAccessException When the method, used for reflection, can not be accessed
     */
    @Test
    public void testCheckManifestForPackageName() throws IOException, GeneratorException, ParserConfigurationException, SAXException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        FileInputStream manifest = new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3"));
        NodePackage nodePackage = new NodePackage("Acme", manifest);

        FileInputStream manifest2 = new FileInputStream(new File(TestingUtils.getPackageDir(), "Acme" + File.separator + "manifest.v3"));

        File temp = File.createTempFile("tempManifest", ".v3");
        temp.deleteOnExit();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(manifest2);

        Object response = TestingUtils.invoke(nodePackage, NodePackage.class, "checkManifestForPackageName", new Class[]{Document.class}, new Object[]{doc});
        if (response instanceof Boolean)
            Assert.assertTrue((Boolean) response);
    }
}
