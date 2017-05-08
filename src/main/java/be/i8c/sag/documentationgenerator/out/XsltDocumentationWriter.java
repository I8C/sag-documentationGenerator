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

import be.i8c.sag.documentationgenerator.XsltTransformer;
import org.apache.fop.apps.FOPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for creating documentation from the intermediate XML
 */
public abstract class XsltDocumentationWriter implements IDocumentationWriter {

	protected static final Logger logger = LoggerFactory.getLogger(XsltDocumentationWriter.class);
	/** The fop.conf file */
	protected File fopConfigFile;
	/** A stream containing the XSLT transformer */
	protected InputStream xsltStream;
	/** A stream defining where the file should be written to */
	protected OutputStream stream;

	/**
	 * Constructor sets intermediate XML and the
	 * @param fopConfigFile FOPFactory configuration file.
	 * @param xsltStream Input stream of the xslt file
	 * @param stream OutputStream for the generated documentation file.
	 */
	public XsltDocumentationWriter(File fopConfigFile, InputStream xsltStream, OutputStream stream) {
		this.fopConfigFile = fopConfigFile;
		this.xsltStream = xsltStream;
		this.stream = stream;
	}


	/**
	 * Generates the documentation from the intermediate XML using FOPFactory.
	 * @param intermediateXml intermediate xml contains all packages and their information
	 * @throws DocumentationGenerationException When generating the documentation has failed
	 */
	@Override
	public void createDocumentation(Document intermediateXml) throws DocumentationGenerationException {
		try {
			Transformer transformer = XsltTransformer.getXsltTransformer(xsltStream);
			Source source = new DOMSource(intermediateXml);
			Result result = getResult();
			transformer.transform(source, result);
		} catch (FOPException | TransformerException e) {
			throw new DocumentationGenerationException("Error occurred trying to generate documentation from the intermediate xml using xslt file.", e);
		}finally {
			try {
				xsltStream.close();
			} catch (IOException e) {
				logger.error("Couldn't properly close an xslt input stream, "+xsltStream, e);
			}
			try {
				stream.flush();
				stream.close();
			} catch (IOException e) {
				logger.error("Couldn't properly close an output stream, "+stream, e);
			}
		}
	}

	/**
	 * Get a result to transform the source into a document
	 *
	 * @return A result
	 * @throws DocumentationGenerationException When generating the documentation has failed
	 * @throws FOPException When creating a FOP instance has failed
     */
	protected abstract Result getResult() throws DocumentationGenerationException, FOPException;
}
