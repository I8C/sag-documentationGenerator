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
package be.i8c.sag.documentationgenerator.intermediatexml;

import be.i8c.sag.documentationgenerator.GeneratorException;
import be.i8c.sag.documentationgenerator.XsltTransformer;
import be.i8c.sag.documentationgenerator.intermediate.IntermediateXml;
import be.i8c.sag.documentationgenerator.intermediate.IntermediateXmlGenerationException;
import be.i8c.sag.nodendf.NodeNdfStream;
import be.i8c.sag.nodendf.NodePackage;
import be.i8c.sag.util.TestingUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Test class for the IntermediateXml class
 *
 * @see IntermediateXml
 */
public class TestIntermediateXml {

    /**
     * Test the function generateXml
     *
     * @throws IntermediateXmlGenerationException When the intermediate xml could not be generated
     */
	@Test
	public void testGenerateXml() throws IntermediateXmlGenerationException {
		
		NodePackage[] nps = {};
		IntermediateXml iXml = new IntermediateXml();
		Document doc = iXml.generateXml(nps);
		Diff myDiff = DiffBuilder.compare(Input.fromStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("IntermediateXmlTest_Success.xml")))
		          .withTest(Input.fromDocument(doc))
		          .ignoreComments()
		          .build();

		Assert.assertFalse(myDiff.toString(), myDiff.hasDifferences());
	}

    /**
     * Test the function createPlainElement
     *
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     */
	@Test
	public void testCreatePlainElement() throws ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		IntermediateXml iXml = new IntermediateXml();
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		Object plainElement = TestingUtils.invoke(
				iXml,
				IntermediateXml.class,
				"createPlainElement",
				new Class[]{Document.class, Node.class, String.class},
				new Object[]{doc, doc, "Packages"}
		);

		if (plainElement instanceof Element)
			Assert.assertNotNull(plainElement);
		else
			Assert.fail("Method response is not of class Element");
	}

    /**
     * Test the function createPackageElement
     *
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
	@Test
	public void testCreatePackageElement() throws ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException, GeneratorException {
		IntermediateXml iXml = new IntermediateXml();

		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		Element packagesEl = (Element) TestingUtils.invoke(
				iXml,
				IntermediateXml.class,
				"createPlainElement",
				new Class[]{Document.class, Node.class, String.class},
				new Object[]{doc, doc, "Packages"}
		);

		for(NodePackage nodePackage: createPackages()){
			Element packageEl = (Element) TestingUtils.invoke(
					iXml,
					IntermediateXml.class,
					"createPackageElement",
					new Class[]{Element.class, NodePackage.class},
					new Object[]{packagesEl, nodePackage}
			);
            Assert.assertNotNull(packageEl);
		}
	}

    /**
     * Test the function createFolderElement
     *
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
	@Test
	public void testCreateFolderElement() throws ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException, GeneratorException {
        IntermediateXml iXml = new IntermediateXml();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element packagesEl = (Element) TestingUtils.invoke(
                iXml,
                IntermediateXml.class,
                "createPlainElement",
                new Class[]{Document.class, Node.class, String.class},
                new Object[]{doc, doc, "Packages"}
        );

        for(NodePackage nodePackage: createPackages()){
            Element packageEl = (Element) TestingUtils.invoke(
                    iXml,
                    IntermediateXml.class,
                    "createPackageElement",
                    new Class[]{Element.class, NodePackage.class},
                    new Object[]{packagesEl, nodePackage}
            );

            Element foldersEl = (Element) TestingUtils.invoke(
                    iXml,
                    IntermediateXml.class,
                    "createPlainElement",
                    new Class[]{Document.class, Node.class, String.class},
                    new Object[]{doc, packageEl, "Folders"}
            );
            for(Map.Entry<String, List<NodeNdfStream>> entry: nodePackage.getNodeNdfStreams().entrySet()){
                Element folderEl = (Element) TestingUtils.invoke(
                        iXml,
                        IntermediateXml.class,
                        "createFolderElement",
                        new Class[]{Document.class, Element.class, String.class},
                        new Object[]{doc, foldersEl, entry.getKey()}
                );
                Assert.assertNotNull(folderEl);
            }
        }
	}

    /**
     * Test the function createSubFolderElements
     *
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     */
	@Test
	public void testCreateSubFolderElements() throws ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException, GeneratorException {
        IntermediateXml iXml = new IntermediateXml();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element packagesEl = (Element) TestingUtils.invoke(
                iXml,
                IntermediateXml.class,
                "createPlainElement",
                new Class[]{Document.class, Node.class, String.class},
                new Object[]{doc, doc, "Packages"}
        );

        for(NodePackage nodePackage: createPackages()){
            Element packageEl = (Element) TestingUtils.invoke(
                    iXml,
                    IntermediateXml.class,
                    "createPackageElement",
                    new Class[]{Element.class, NodePackage.class},
                    new Object[]{packagesEl, nodePackage}
            );

            Element foldersEl = (Element) TestingUtils.invoke(
                    iXml,
                    IntermediateXml.class,
                    "createPlainElement",
                    new Class[]{Document.class, Node.class, String.class},
                    new Object[]{doc, packageEl, "Folders"}
            );

            for(Map.Entry<String, List<NodeNdfStream>> entry: nodePackage.getNodeNdfStreams().entrySet()){
                Element folderEl = (Element) TestingUtils.invoke(
                        iXml,
                        IntermediateXml.class,
                        "createFolderElement",
                        new Class[]{Document.class, Element.class, String.class},
                        new Object[]{doc, foldersEl, entry.getKey()}
                );

                TestingUtils.invoke(
                        iXml,
                        IntermediateXml.class,
                        "createSubFolderElements",
                        new Class[]{Document.class, Element.class, Collection.class},
                        new Object[]{doc, folderEl, entry.getValue()}
                );
            }
        }
	}

    /**
     * Test the function addToTreeMapOrCreate
     *
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     * @throws TransformerConfigurationException When the transformer could not be created
     */
    @Test
    public void testAddToTreeMapOrCreate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException, GeneratorException, TransformerConfigurationException {
        IntermediateXml iXml = new IntermediateXml();

        for(NodePackage nodePackage: createPackages()){
            for(Map.Entry<String, List<NodeNdfStream>> entry: nodePackage.getNodeNdfStreams().entrySet()){
                TreeMap<String,ArrayList<Element>> orderedElements = new TreeMap<>();
                for (NodeNdfStream node : entry.getValue()) {
                    Element nodeEl = (Element) TestingUtils.invoke(
                            iXml,
                            IntermediateXml.class,
                            "createXmlElement",
                            new Class[]{StreamSource.class, Transformer.class},
                            new Object[]{new StreamSource(node.getStream()), XsltTransformer.getXsltTransformer("NodeNdfToIntermediateV1.xslt")}
                    );
                    String nodeType = nodeEl.getNodeName();
                    TestingUtils.invoke(
                            iXml,
                            IntermediateXml.class,
                            "addToTreeMapOrCreate",
                            new Class[]{String.class, Element.class, TreeMap.class},
                            new Object[]{nodeType, nodeEl, orderedElements}
                    );
                    Assert.assertTrue(orderedElements.size() == 1);
                    Assert.assertTrue(orderedElements.get(nodeType).size() == 1);
                }
            }
        }
    }

    /**
     * Test the function setElementAttribute
     *
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     */
    @Test
	public void testSetElementAttribute() throws ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final String newName = "CustomName";
        IntermediateXml iXml = new IntermediateXml();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element packagesEl = (Element) TestingUtils.invoke(
                iXml,
                IntermediateXml.class,
                "createPlainElement",
                new Class[]{Document.class, Node.class, String.class},
                new Object[]{doc, doc, "Packages"}
        );

        TestingUtils.invoke(
                iXml,
                IntermediateXml.class,
                "setElementAttribute",
                new Class[]{Element.class, String.class, String.class},
                new Object[]{packagesEl, "Name", newName}
        );

        Assert.assertTrue(packagesEl.getAttribute("Name").equals(newName));
	}

    /**
     * Test the function createXmlElement(Element, StreamSource, Transformer)
     *
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     * @throws TransformerConfigurationException When the transformer could not be created
     */
	@Test
	public void testCreateXmlElement1() throws ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException, GeneratorException, TransformerConfigurationException {
        IntermediateXml iXml = new IntermediateXml();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element packagesEl = (Element) TestingUtils.invoke(
                iXml,
                IntermediateXml.class,
                "createPlainElement",
                new Class[]{Document.class, Node.class, String.class},
                new Object[]{doc, doc, "Packages"}
        );

        for(NodePackage nodePackage: createPackages()){
            Element packageEl = (Element) TestingUtils.invoke(
                    iXml,
                    IntermediateXml.class,
                    "createXmlElement",
                    new Class[]{Element.class, StreamSource.class, Transformer.class},
                    new Object[]{packagesEl, new StreamSource(nodePackage.getManifest()), XsltTransformer.getXsltTransformer("ManifestV3ToIntermediateV1.xslt")}
            );
            Assert.assertNotNull(packageEl);
        }
    }

    /**
     * Test the function createXmlElement(StreamSource, Transformer)
     *
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws FileNotFoundException When the packages are not able to be found
     * @throws GeneratorException When the packages can't be created
     * @throws TransformerConfigurationException When the transformer could not be created
     */
	@Test
	public void testCreateXmlElement2() throws FileNotFoundException, GeneratorException, TransformerConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        IntermediateXml iXml = new IntermediateXml();

        for(NodePackage nodePackage: createPackages()){
            Element packageEl = (Element) TestingUtils.invoke(
                    iXml,
                    IntermediateXml.class,
                    "createXmlElement",
                    new Class[]{StreamSource.class, Transformer.class},
                    new Object[]{new StreamSource(nodePackage.getManifest()), XsltTransformer.getXsltTransformer("ManifestV3ToIntermediateV1.xslt")}
            );
            Assert.assertNotNull(packageEl);
        }
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
}
