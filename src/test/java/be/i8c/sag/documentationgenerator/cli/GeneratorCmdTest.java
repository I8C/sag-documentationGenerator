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
package be.i8c.sag.documentationgenerator.cli;

import be.i8c.sag.documentationgenerator.GeneratorException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

public class GeneratorCmdTest {

	@Test
	public void noPackageTest() {
		try {
			GeneratorCmd.main(new String[0]);
			//fail("No error thrown for empty package");
		} catch (GeneratorException e) {
			Assert.assertTrue(e.getMessage().contains("No packages directory"));
			Assert.assertTrue(e.getMessage().contains("No packages found"));
			Assert.assertTrue(e.getMessage().contains("Setting of directory failed"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
