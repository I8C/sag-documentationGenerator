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

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for creating documentation from the intermediate XML
 */
public class XsltStreamResultDocWriter extends XsltDocumentationWriter {

    /**
     * Constructor sets intermediate in- and output streams and the FOP config file
     *
     * @param fopConfigFile FOPFactory configuration file.
     * @param xsltStream    Input stream of the xslt file
     * @param stream        OutputStream for the generated documentation file.
     */
    public XsltStreamResultDocWriter(File fopConfigFile, InputStream xsltStream, OutputStream stream) {
        super(fopConfigFile, xsltStream, stream);
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
        return new StreamResult(stream);
    }
}
