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

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for creating documentation from the intermediate XML
 */
public class XsltFopFactoryDocWriter extends XsltDocumentationWriter {

    /**
     * The format of the document
     * @see org.apache.fop.apps.MimeConstants
     */
    private String outputFormat;

    /**
     * Constructor sets intermediate in- and output streams,
     * the FOP config file and the output format
     *
     * @param fopConfigFile FOPFactory configuration file.
     * @param xsltStream    Input stream of the xslt file
     * @param stream        OutputStream for the generated documentation file.
     * @param outputFormat  The format of the document
     * @see org.apache.fop.apps.MimeConstants
     */
    public XsltFopFactoryDocWriter(File fopConfigFile, InputStream xsltStream, OutputStream stream, String outputFormat) {
        super(fopConfigFile, xsltStream, stream);
        this.outputFormat = outputFormat;
    }

    /**
     * Get a result to transform the source into a document
     *
     * @return A result
     * @throws DocumentationGenerationException When generating the documentation has failed
     * @throws FOPException When creating a FOP instance has failed
     */
    @Override
    protected Result getResult() throws DocumentationGenerationException, FOPException {
        FopFactory fopFactory;
        try {
            logger.trace("Create the fopFactory from the temporary config file.");
            fopFactory = FopFactory.newInstance(fopConfigFile);
        } catch (IOException | SAXException e) {
            throw new DocumentationGenerationException("Error setting the fop.xconf for fopfactory ",e);
        }
        Fop fop = fopFactory.newFop(outputFormat, stream);
        return new SAXResult(fop.getDefaultHandler());
    }
}
