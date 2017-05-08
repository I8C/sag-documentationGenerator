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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * This class represents a package with all of its content
 */
public class NodePackage {

	/** The name of the package */
	private String name;
	/** A map of all the nodes inside of the package */
	private TreeMap<String,List<NodeNdfStream>> nodeNdfStreams;
	/** An input stream to read the manifest.v3 file */
	private InputStream manifest;

	public NodePackage(String name, InputStream manifest) throws GeneratorException {
		super();
		this.name = name;
		this.manifest = addPackageName(manifest, name);
		this.nodeNdfStreams = new TreeMap<>();
	}

	/**
	 * Modify the manifest file to include the package name
	 *
	 * @param manifest1 The input stream of the manifest file
	 * @param packageName The name of this package
	 * @return An input stream of a modified manifest file
	 * @throws GeneratorException When failing to create the modified manifest
     */
	private InputStream addPackageName(InputStream manifest1, String packageName) throws GeneratorException {
		File temp;
		Document doc;
		try {
			temp = File.createTempFile("tempManifest", ".v3");
			temp.deleteOnExit();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(manifest1);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GeneratorException("Error creating tempManifest" );
		}
        if (checkManifestForPackageName(doc)) {
            Node Values = doc.getFirstChild();
            Element packageNode = doc.createElement("value");
            packageNode.setAttribute("name", "name");
            packageNode.appendChild(doc.createTextNode(packageName));
            Values.appendChild(packageNode);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new GeneratorException("Error creating tempManifest", e );
		}

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(temp.getAbsolutePath());
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new GeneratorException("Error writing manipulated manifest", e );
		}
		InputStream manifest2;
		try {
			manifest2 = new FileInputStream(temp.getCanonicalPath());
		} catch (IOException e) {
			throw new GeneratorException("Error reading adjusted manifest",e);
		}
		return manifest2;
	}

    /**
     * Check if there is a name for the package in the manifest file
     *
     * @param doc The manifest file converted into a document
     * @return True of the manifest contains a name, false if not
     * @throws GeneratorException When the manifest could not be checked for a name
     */
	private boolean checkManifestForPackageName(Document doc) throws GeneratorException {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr;
		Object result1;
		try {
			expr = xpath.compile("//value[@name='name']");
			result1 = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e1) {
			throw new GeneratorException("Error checking manifest file for packagename element", e1);
		}
		NodeList nodes = (NodeList) result1;
		return (nodes.getLength()<1)? true : false;
	}

    /**
     * Get the name of this package
     *
     * @return The name
     */
	public String getName() {
		return name;
	}

    /**
     *
     * @return A map of all of the child nodes
     * @see NodeNdfStream
     */
	public TreeMap<String,List<NodeNdfStream>> getNodeNdfStreams() {
		return nodeNdfStreams;
	}

    /**
     * Add a child node to the package
     *
     * @param nodeNdfStream The node to add
     * @see NodeNdfStream
     */
	public void addNodeNdfStream(NodeNdfStream nodeNdfStream) {
        List<NodeNdfStream> nodes = nodeNdfStreams.get(nodeNdfStream.getQualifiedPrefix());
		if(nodes != null){
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getName().equals(nodeNdfStream.getName()))
                {
                    nodes.set(i, nodeNdfStream);
                    return;
                }
            }
            nodes.add(nodeNdfStream);
        }else{
			nodeNdfStreams.put(nodeNdfStream.getQualifiedPrefix(), new ArrayList<NodeNdfStream>(){{add(nodeNdfStream);}});
		}
	}

    /**
     * Get the input stream for the manifest of the package
     *
     * @return The manifest input stream
     */
	public InputStream getManifest() {
		return manifest;
	}
}
