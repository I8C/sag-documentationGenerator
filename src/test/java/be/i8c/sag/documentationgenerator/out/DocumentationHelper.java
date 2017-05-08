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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DocumentationHelper {
    /**
     * Generate a document from an internal file
     *
     * @return A document
     * @throws IOException When the file couldn't be created
     * @throws ParserConfigurationException When a document builder could't be made
     * @throws SAXException When the manifest file couldn't be parsed to a Document
     */
    public static Document getDocument() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("IntermediateXmlTest_Success2.xml"));
    }

    /**
     * Create a config file from an internal file
     *
     * @return A config file from an internal file
     * @throws URISyntaxException When the URL could not be converted to an URI
     */
    public static File getConfig() throws URISyntaxException {
        return new File(Thread.currentThread().getContextClassLoader().getResource("fop.xconf").toURI());
    }
}
