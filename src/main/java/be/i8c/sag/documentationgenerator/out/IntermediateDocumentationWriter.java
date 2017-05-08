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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for creating documentation from the intermediate XML
 * @author i8c
 */
public class IntermediateDocumentationWriter implements IDocumentationWriter {

	private static final Logger logger = LoggerFactory.getLogger(IntermediateDocumentationWriter.class);
	/** A stream defining where the file should be written to */
	private OutputStream stream;

	/**
	 * @param stream OutputStream for the generated documentation file.
     */
	public IntermediateDocumentationWriter(OutputStream stream) {
		this.stream = stream;
	}

	/**
	 * Generates the documentation from the intermediate XML using FOPFactory.
	 * @param intermediateXml intermediate xml contains all packages and their information
	 * @throws DocumentationGenerationException When generating the documentation has failed
	 */
	@Override
	public void createDocumentation(Document intermediateXml) throws DocumentationGenerationException {
		try{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer t = tFactory.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(intermediateXml);
			StreamResult result = new StreamResult(stream);
			t.transform(source, result);
		} catch (TransformerException e){
			throw new DocumentationGenerationException("Error occurred trying to save intermediateXml File.", e);
		}finally {
			try {
				stream.flush();
				stream.close();
			} catch (IOException e) {
				logger.error("Couldn't properly close an output stream, " + stream, e);
			}
		}
	}
}
