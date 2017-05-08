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

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * Create a Transformer from an XSLT file
 * @see Transformer
 */
public class XsltTransformer {

	/**
   * The factory that will generate Transformer objects.
   * @see Transformer
   */
	private final static TransformerFactory transformerFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);

	/**
	 * Create a transformer by supplying a file from within the jar
	 *
	 * @param xsltName The name of the file within the jar
	 * @return A valid non-null instance of a Transformer.
	 * @throws TransformerConfigurationException if a Transformer can not be created.
     */
	public static Transformer getXsltTransformer(String xsltName) throws TransformerConfigurationException {
		InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsltName);
		Templates xslt = transformerFactory.newTemplates(new StreamSource(resource));
		return xslt.newTransformer();
	}

	/**
	 * Create a transformer by supplying an input stream of an XSLT file
	 *
	 * @param xsltSource The input stream to create the transformer
	 * @return A valid non-null instance of a Transformer.
	 * @throws TransformerConfigurationException if a Transformer can not be created.
     */
	public static Transformer getXsltTransformer(InputStream xsltSource) throws TransformerConfigurationException {
		return transformerFactory.newTransformer(new StreamSource(xsltSource));
	}
}
