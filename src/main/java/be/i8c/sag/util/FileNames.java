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
package be.i8c.sag.util;

/**
 * Store all the names of files the generator
 * may interact with.
 */
public class FileNames {
    public static class Input{
        public static final String INTERMEDIATE_HTML = "IntermediateV1ToHtml.xslt";
        public static final String INTERMEDIATE_MD = "IntermediateV1ToMarkdown.xslt";
        public static final String INTERMEDIATE_PDF = "IntermediateV1ToPdf.xslt";
        public static final String INTERMEDIATE_TEXT= "IntermediateV1ToText.xslt";
        public static final String INTERMEDIATE_XML = "IntermediateV1ToXml.xslt";
    }
    public static class Output{
        public static final String GENERATED_HTML = "GeneratedDocumentation.html";
        public static final String GENERATED_MD = "GeneratedDocumentation.md";
        public static final String GENERATED_PDF = "GeneratedDocumentation.pdf";
        public static final String GENERATED_TEXT = "GeneratedDocumentation.txt";
        public static final String GENERATED_XML = "GeneratedDocumentation.xml";
        public static final String GENERATED_INTERMEDIATE_XML = "IntermediateXml.xml";
    }
}
