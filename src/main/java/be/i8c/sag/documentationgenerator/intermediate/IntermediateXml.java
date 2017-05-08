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
package be.i8c.sag.documentationgenerator.intermediate;

import be.i8c.sag.documentationgenerator.TransformationException;
import be.i8c.sag.documentationgenerator.XsltTransformer;
import be.i8c.sag.nodendf.NodeNdfStream;
import be.i8c.sag.nodendf.NodePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.util.*;

/**
 * A class to generate a Document object from an array of packages
 * This document can be used to generate documentation files
 *
 * @see Document
 */
public class IntermediateXml {
	private static final Logger logger = LoggerFactory.getLogger(IntermediateXml.class);
	private static final String targetNamespace = "http://www.i8c.be/sag/documentationgenerator/intermediate/v1";

	/**
	 * Generate a document which contains all of the nodes and can be used to create documentation files
	 *
	 * @param nodePackages All of the packages that need to be included into the same document
	 * @return The nodes transformed into a document
	 * @throws IntermediateXmlGenerationException When the intermediate xml could not be generated
	 *
	 * @see Document
     */
	public Document generateXml(NodePackage[] nodePackages) throws IntermediateXmlGenerationException{
		try{
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.newDocument();
			Element packagesEl = createPlainElement(doc, doc, "Packages");
			for(NodePackage nodePackage: nodePackages){
				Element packageEl = createPackageElement(packagesEl,nodePackage);
				Element foldersEl = createPlainElement(doc, packageEl, "Folders");
				for(Map.Entry<String, List<NodeNdfStream>> entry: nodePackage.getNodeNdfStreams().entrySet()){
					Element folderEl = createFolderElement(doc, foldersEl, entry.getKey());
					createSubFolderElements(doc, folderEl, entry.getValue());
				}
			}
			return doc;
		}catch(IntermediateXmlGenerationException e){
			throw e;
		}catch(Exception e){
			throw new IntermediateXmlGenerationException("Exception while generating xml from Packages", e);
		}
	}

	private void createSubFolderElements(Document doc, Element parentElement, Collection<NodeNdfStream> nodeStreams) throws IntermediateXmlGenerationException {
		TreeMap<String,ArrayList<Element>> orderedElements = new TreeMap<>();
		for(NodeNdfStream node : nodeStreams){
			try{
				logger.trace("Create xml element from {} using xsltTransformer based on 'nodeNdfToIntermediateV1.xslt'.", node.getName());
				Element nodeEl = createXmlElement(new StreamSource(node.getStream()),XsltTransformer.getXsltTransformer("NodeNdfToIntermediateV1.xslt"));
				setElementAttribute(nodeEl, "name", node.getName());
				String nodeType = nodeEl.getNodeName();
				logger.trace("Add node {} to nodeTree", nodeType);
				addToTreeMapOrCreate(nodeType,nodeEl,orderedElements);
			}catch(Exception e){
				logger.error("Exception while generating subtype element {}", node.getName());
				throw new IntermediateXmlGenerationException("Exception while generating subtype element " + node.getName(), e);
			}
		}
		for(String nodeType:orderedElements.keySet()){
			Element nodeTypeEl = createPlainElement(doc, parentElement, nodeType + "s");
			for(Element node:orderedElements.get(nodeType)){
				nodeTypeEl.appendChild(parentElement.getOwnerDocument().adoptNode(node));
			}
		}
	}

	/**
	 * Add the element to the treemap with the given key
	 * If there is no ArrayList under the given key, create it first and add it
	 *
	 * @param key The element will be added for this key value
	 * @param nodeEl The element to add to the collection of the key
	 * @param treemap The TreeMap that contains all of the elements
     */
	private void addToTreeMapOrCreate(String key, Element nodeEl, TreeMap<String, ArrayList<Element>> treemap) {
		if(treemap.get(key)==null){
			logger.trace("{} doesn't exist in the treemap. Creating a new arraylist.", key);
			ArrayList<Element> set = new ArrayList<>();
			set.add(nodeEl);
			treemap.put(key, set);
		}else{
			logger.trace("{} exists in the treemap adding the element to the arraylist.", key);
			treemap.get(key).add(nodeEl);
		}
		logger.debug("Successfully added element to the treemap.");
	}

	/**
	 * Create a folder and place it as a child in the parent element
	 *
	 * @param doc The document that will create the folder
	 * @param parentElement The parent element of the folder
	 * @param qualifiedPrefix The qualified prefix of the folder
	 * @return The folder that has been created
	 * @throws IntermediateXmlGenerationException When the folder couldn't be created
     */
	private Element createFolderElement(Document doc, Element parentElement, String qualifiedPrefix) throws IntermediateXmlGenerationException {
		try{
			Element folderEl = doc.createElementNS(targetNamespace, "Folder");
			setElementAttribute(folderEl, "qualifiedName", qualifiedPrefix);
			parentElement.appendChild(folderEl);
			return folderEl;
		}catch(Exception e){
			throw new IntermediateXmlGenerationException("Exception while generating folder element " + qualifiedPrefix, e);
		}
	}

	/**
	 * Create a package and place it as a child in the parent element
	 *
	 * @param parentElement The parent element of the package
	 * @param nodePackage The package that will be added to the parent
	 * @return The package that has been created
	 * @throws IntermediateXmlGenerationException When the package couldn't be created
     */
	private Element createPackageElement(Element parentElement, NodePackage nodePackage) throws IntermediateXmlGenerationException {
		try{
			Element packageEl = createXmlElement(parentElement,new StreamSource(nodePackage.getManifest()),XsltTransformer.getXsltTransformer("ManifestV3ToIntermediateV1.xslt"));
			parentElement.appendChild(packageEl);
			return packageEl;
		}catch(Exception e){
			throw new IntermediateXmlGenerationException("Exception while generating package element " + nodePackage.getName(), e);
		}
		
	}

	/**
	 * Create a package and place it as a child in the parent element
	 *
	 * @param doc The document that will create the folder
	 * @param parent The parent node of the element
	 * @param name The name of the element that will be added to the parent
	 * @return The element that has been created
	 * @throws IntermediateXmlGenerationException When the element couldn't be created
	 */
	private Element createPlainElement(Document doc, Node parent, String name) throws IntermediateXmlGenerationException{
		try{
			Element plainEl = doc.createElementNS(targetNamespace, name);
			parent.appendChild(plainEl);
			return plainEl;
		}catch(Exception e){
			throw new IntermediateXmlGenerationException("Exception while generating plain element: " + name, e);
		}
		
	}
	
	void setElementAttribute(Element element, String attributeName, String attributeValue) {
		logger.trace("Setting element {} attribute name to {} and it's value to {}", element.getNodeName(), attributeName, attributeValue);
		element.setAttributeNS(null, attributeName, attributeValue);
	}

	/**
	 * Create a new element from a StreamSource and add it to the parent element
	 *
	 * @param parentElement The parent element of the newly created element
	 * @param streamSource The source of the element
	 * @param transformer The transformer that will transform the StreamSource into an Element
	 * @return The created element
	 * @throws TransformationException When the transformer could not transform the element
	 *
	 * @see XsltTransformer
	 * @see Transformer
	 * @see StreamSource
     */
	private Element createXmlElement(Element parentElement, StreamSource streamSource, Transformer transformer) throws TransformationException {
		Element intermediateXmlElement = createXmlElement(streamSource, transformer);
		Element el = (Element) parentElement.getOwnerDocument().adoptNode(intermediateXmlElement);
		parentElement.appendChild(el);
		return el;
	}

	/**
	 * Create a new element from a StreamSource
	 *
	 * @param streamSource The source of the element
	 * @param transformer The transformer that will transform the StreamSource into an Element
	 * @return The created element
	 * @throws TransformationException When the transformer could not transform the element
	 *
	 * @see XsltTransformer
	 * @see Transformer
	 * @see StreamSource
	 */
	private Element createXmlElement(StreamSource streamSource, Transformer transformer) throws TransformationException {
		DOMResult domResult = new DOMResult();

		try {
			transformer.transform(streamSource, domResult);
		} catch (TransformerException e) {
			throw new TransformationException("Error occurred trying to create intermediate xml element by xslt transformation", e);
		}
		
		Node intermediateXmlElement = domResult.getNode().getFirstChild();
		
		if (intermediateXmlElement != null && intermediateXmlElement instanceof Element) {
			return (Element) intermediateXmlElement;
		} else {
			throw new TransformationException("Xslt transformation to create intermediate xml element executed without errors but no result element was found.");
		}
	}
}
