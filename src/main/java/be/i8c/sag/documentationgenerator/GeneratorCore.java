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

import be.i8c.sag.documentationgenerator.intermediate.IntermediateXml;
import be.i8c.sag.documentationgenerator.intermediate.IntermediateXmlGenerationException;
import be.i8c.sag.documentationgenerator.out.DocumentationGenerationException;
import be.i8c.sag.documentationgenerator.out.IDocumentationWriter;
import be.i8c.sag.nodendf.NodePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This class can invoke IDocumentationWriters to generate
 * documentation based off the given NodePackages
 *
 * @see IDocumentationWriter
 * @see NodePackage
 */
public class GeneratorCore {
	
	private static final Logger logger = LoggerFactory.getLogger(GeneratorCore.class);

	/**
	 * Start generating the documentation for all of the package nodes
	 *
	 * @param nodePackages The package nodes that will need to be documented
	 * @param documentations The documentation output methods
	 * @throws GeneratorException When it failed to generate the intermediate XML document
     */
	public void generateDocumentation(NodePackage[] nodePackages, Iterable<IDocumentationWriter> documentations) throws GeneratorException{
		logger.debug("Create intermediate xml from nodePackages array.");
		Document intermediateXml = generateIntermediateXml(nodePackages);
		generateDocumentationFromIntermediateXml(intermediateXml, documentations);
	}

	/**
	 * Generate the intermediate XML document
	 *
	 * @param nodePackages The package nodes that will need to be documented
	 * @return A document that can be used to generate the output
	 * @throws GeneratorException When it failed to generate the intermediate XML document
	 * @see Document
     */
	public Document generateIntermediateXml(NodePackage[] nodePackages) throws GeneratorException{
		IntermediateXml iXml;
		logger.info("{}{}", nodePackages.length, (nodePackages.length==1)? " Package":" Packages");
		if(!(nodePackages.length==0)){
			try {
				logger.debug("Creating new intermediate xml.");
				iXml = new IntermediateXml();
				return iXml.generateXml(nodePackages);
			} catch (IntermediateXmlGenerationException e) {
				throw new GeneratorException("Error generating intermediate xml", e);
			}
		} else {
			throw new GeneratorException("Error creating intermediate xml: No packages found");
		}
	}

	/**
	 * Start creating the output files
	 *
	 * @param document A document that can be used to generate the output
	 * @param documentations All of the output formats that need to generate the documentation
     */
    public void generateDocumentationFromIntermediateXml(Document document, Iterable<IDocumentationWriter> documentations) {
		logger.trace("Creating documentation file(s)");
        for (IDocumentationWriter documentation : documentations)
        {
            try {
                documentation.createDocumentation(document);
            } catch (DocumentationGenerationException e) {
                logger.error("Could not generate the document", e);
            }
        }
    }
}
