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

/**
 * An interface that has the ability to create documentation files
 */
public interface IDocumentationWriter {

	/**
	 * Generates the documentation from the intermediate XML using FOPFactory.
	 * @param intermediateXml intermediate xml contains all packages and their information
	 * @throws DocumentationGenerationException When generating the documentation has failed
	 */
	void createDocumentation(Document intermediateXml) throws DocumentationGenerationException;
}
