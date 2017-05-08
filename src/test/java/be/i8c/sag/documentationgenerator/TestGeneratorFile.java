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

import be.i8c.sag.documentationgenerator.out.DocumentationGenerationException;
import be.i8c.sag.util.FileNames;
import be.i8c.sag.util.TestingUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
/**
 * Test class for the GeneratorFile class
 *
 * @see GeneratorFile
 */
public class TestGeneratorFile {

    /**
     * Try to prepare the XML validators
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field/method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     */
    @Test
    public void testPrepareXmlValidator() throws GeneratorException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {

        Field parser = GeneratorFile.class.getDeclaredField("parser");
        Field validator = GeneratorFile.class.getDeclaredField("validator");
        parser.setAccessible(true);
        validator.setAccessible(true);
        GeneratorFile generatorFile = getGeneratorFile();

        Assert.assertNull(parser.get(generatorFile));
        Assert.assertNull(validator.get(generatorFile));

        TestingUtils.invoke(
                generatorFile,
                GeneratorFile.class,
                "prepareXmlValidator",
                new Class[]{},
                new Object[]{}
        );

        Assert.assertNotNull(parser.get(generatorFile));
        Assert.assertNotNull(validator.get(generatorFile));

        parser.setAccessible(false);
        validator.setAccessible(false);
    }

    /**
     * Validate if a file is a has a valid XML content
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IllegalAccessException When the target method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     */
    @Test
    public void testIsValidXml() throws GeneratorException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        GeneratorFile generatorFile = getGeneratorFile();

        TestingUtils.invoke(
                generatorFile,
                GeneratorFile.class,
                "prepareXmlValidator",
                new Class[]{},
                new Object[]{}
        );

        Object isValid = TestingUtils.invoke(
                generatorFile,
                GeneratorFile.class,
                "isValidXml",
                new Class[]{File.class},
                new Object[]{new File(TestingUtils.getExternalResourceDir(), "packages"+File.separator+"Acme"+File.separator+"manifest.v3")}
        );

        if (isValid instanceof Boolean)
            Assert.assertTrue((Boolean) isValid);
        else
            Assert.fail("Expected boolean");

        isValid = TestingUtils.invoke(
                generatorFile,
                GeneratorFile.class,
                "isValidXml",
                new Class[]{File.class},
                new Object[]{new File(TestingUtils.getExternalResourceDir(), "packages"+File.separator+"Acme"+File.separator+"pub"+File.separator+"index.html")}
        );

        if (isValid instanceof Boolean)
            Assert.assertFalse((Boolean) isValid);
        else
            Assert.fail("Expected boolean");
    }

    /**
     * Test the constructor function
     *
     * @throws GeneratorException When the generation was not successful
     */
    @Test
    public void testConstructor() throws GeneratorException {
        try{
            new GeneratorFile(
                    TestingUtils.getPackageDir().getAbsolutePath(),
                    null,
                    null);
            Assert.fail("Should throw a NullPointerException");
        }catch (NullPointerException e){
            //Success
        }
    }

    /**
     * Try to create the nodes
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field can not be accessed
     */
    @Test
    public void testMakeNodes() throws GeneratorException, NoSuchFieldException, IllegalAccessException {
        try{
            new GeneratorFile(
                    null,
                    TestingUtils.getOutputDir().getAbsolutePath(),
                    null).makeNodes();
            Assert.fail("Should throw a GeneratorException");
        }catch (GeneratorException e){
            //Success
        }

        GeneratorFile generatorFile = getGeneratorFile();
        generatorFile.makeNodes();

        Field packageNodes = GeneratorFile.class.getDeclaredField("packageNodes");
        packageNodes.setAccessible(true);
        Assert.assertTrue(((Collection)packageNodes.get(generatorFile)).size() > 0);
        packageNodes.setAccessible(false);
    }

    /**
     * Change the output directory
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field can not be accessed
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testSetOutputDirectory() throws GeneratorException, NoSuchFieldException, IllegalAccessException, IOException {
        try{
            GeneratorFile generatorFile = getGeneratorFile();
            generatorFile.setOutputDirectory(null);
            Assert.fail("Should throw a NullPointerException");
        }catch (NullPointerException e) {
            //Success
        }

        File output = new File(System.getProperty("java.io.tmpdir"), "DocumentGenerator");
        output.mkdirs();
        GeneratorFile generatorFile = getGeneratorFile();
        generatorFile.setOutputDirectory(output.getAbsolutePath());
        generatorFile.addPdfOutput();
        generatorFile.generateDocumentation();
        Assert.assertNotNull(output.listFiles());
        Assert.assertTrue(output.listFiles().length > 0);
        TestingUtils.deleteDir(output);
    }

    /**
     * Transform a string into a Path object
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field/method can not be accessed
     * @throws NoSuchMethodException When the target method does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws IOException When failing to create a temporary file
     */
    @Test
    public void testSetDirectory() throws GeneratorException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException {
        GeneratorFile generatorFile = getGeneratorFile();
        Object response = TestingUtils.invoke(
                generatorFile,
                GeneratorFile.class,
                "setDirectory",
                new Class[]{String.class, String.class},
                new Object[]{TestingUtils.getOutputDir().getAbsolutePath(), "output"}
        );

        if (response instanceof Path)
            Assert.assertNotNull(response);
        else
            Assert.fail("Response is not a Path object");

        try{
            File file = File.createTempFile("IAmNotADirectory", "txt");
            file.deleteOnExit();
            TestingUtils.invoke(
                    generatorFile,
                    GeneratorFile.class,
                    "setDirectory",
                    new Class[]{String.class, String.class},
                    new Object[]{file.getAbsolutePath(), "output"}
            );
            Assert.fail("Should throw a GeneratorException wrapped in a InvocationTargetException");
        }catch (InvocationTargetException e){
            if (!(e.getCause() instanceof GeneratorException)) {
                throw e;
            }else
                ;// Success
        }
    }

    /**
     * Set the FOP config file
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field can not be accessed
     * @throws DocumentationGenerationException When failing to set the FOP config file
     */
    @Test
    public void testSetFopConfigFile() throws GeneratorException, NoSuchFieldException, IllegalAccessException, DocumentationGenerationException {
        GeneratorFile generatorFile = getGeneratorFile();

        generatorFile.setFopConfigFile(null);
        Field fopConfigFile = GeneratorFile.class.getDeclaredField("fopConfigFile");
        fopConfigFile.setAccessible(true);
        Assert.assertNotNull(fopConfigFile.get(generatorFile));
        fopConfigFile.setAccessible(false);

        generatorFile.setFopConfigFile(TestingUtils.getExternalResourceDir().getAbsolutePath());
        fopConfigFile = GeneratorFile.class.getDeclaredField("fopConfigFile");
        fopConfigFile.setAccessible(true);
        Assert.assertNotNull(fopConfigFile.get(generatorFile));
        fopConfigFile.setAccessible(false);

        try{
            generatorFile.setFopConfigFile(new File(TestingUtils.getExternalResourceDir(), "FakeDirectory").getAbsolutePath());
            Assert.fail("Should throw a GeneratorException");
        }catch (GeneratorException e){
            // Success
        }
    }

    /**
     * Filter packages by name
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field can not be accessed
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testSetPackageNames() throws GeneratorException, NoSuchFieldException, IllegalAccessException, IOException {

        Field fieldPackageNames = GeneratorFile.class.getDeclaredField("packageNames");
        Field fieldPackageNameRegex = GeneratorFile.class.getDeclaredField("packageNameRegex");
        fieldPackageNames.setAccessible(true);
        fieldPackageNameRegex.setAccessible(true);

        //Try setting with an empty array
        {
            GeneratorFile generatorFile = getGeneratorFile();
            generatorFile.addTxtOutput();
            Object packageNames, packageNameRegex;

            packageNames = fieldPackageNames.get(generatorFile);
            packageNameRegex = fieldPackageNameRegex.get(generatorFile);

            //Nothing should changed when doing this
            generatorFile.setPackageNames(new String[0]);

            Assert.assertEquals(packageNames, fieldPackageNames.get(generatorFile));
            Assert.assertEquals(packageNameRegex, fieldPackageNameRegex.get(generatorFile));

        }

        // Try setting with a package that exists
        {
            GeneratorFile generatorFile = getGeneratorFile();
            generatorFile.addTxtOutput();

            String[] filter = new String[]{"Acme"};
            generatorFile.setPackageNames(filter);

            HashSet<String> packages = (HashSet<String>) fieldPackageNames.get(generatorFile);
            Assert.assertEquals(filter.length, packages.size());
            for (String s : filter)
                Assert.assertTrue(packages.contains(s));
            Assert.assertTrue(fieldPackageNameRegex.get(generatorFile).equals(".*"));

            generatorFile.generateDocumentation();
        }

        // Try setting with a package that does not exists
        {
            GeneratorFile generatorFile = getGeneratorFile();
            generatorFile.addTxtOutput();

            String[] filter = new String[]{"A-Non-Existing-Package"};
            generatorFile.setPackageNames(filter);

            try {
                generatorFile.generateDocumentation();
                Assert.fail("Should throw a GeneratorException");
            }catch (GeneratorException e){
                // Success
            }
        }

        fieldPackageNames.setAccessible(false);
        fieldPackageNameRegex.setAccessible(false);
    }

    /**
     * Filter packages with a regular expression
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field can not be accessed
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testSetPackageNameRegex() throws GeneratorException, NoSuchFieldException, IllegalAccessException, IOException {
        Field fieldPackageNames = GeneratorFile.class.getDeclaredField("packageNames");
        Field fieldPackageNameRegex = GeneratorFile.class.getDeclaredField("packageNameRegex");
        fieldPackageNames.setAccessible(true);
        fieldPackageNameRegex.setAccessible(true);

        // Try setting with a package that exists
        {
            GeneratorFile generatorFile = getGeneratorFile();
            generatorFile.addTxtOutput();

            Assert.assertTrue(fieldPackageNameRegex.get(generatorFile).equals("^(?!Wm).*"));
            generatorFile.setPackageNameRegex("Acme");
            Assert.assertTrue(fieldPackageNameRegex.get(generatorFile).equals("Acme"));

            generatorFile.generateDocumentation();
        }

        // Try setting with a package that does not exists
        {
            GeneratorFile generatorFile = getGeneratorFile();
            generatorFile.addTxtOutput();

            Assert.assertTrue(fieldPackageNameRegex.get(generatorFile).equals("^(?!Wm).*"));
            generatorFile.setPackageNameRegex("A-Regex-That-Will-Not-Match-Anything");
            Assert.assertTrue(fieldPackageNameRegex.get(generatorFile).equals("A-Regex-That-Will-Not-Match-Anything"));

            try {
                generatorFile.generateDocumentation();
                Assert.fail("Should throw a GeneratorException");
            }catch (GeneratorException e){
                // Success
            }
        }

        fieldPackageNames.setAccessible(false);
        fieldPackageNameRegex.setAccessible(false);
    }

    /**
     * Filter the folders with a regular expression
     *
     * @throws GeneratorException When the generation was not successful
     * @throws NoSuchFieldException When the target field does not exist
     * @throws IllegalAccessException When the target field can not be accessed
     */
    @Test
    public void testSetFolderQualifiedNameRegex() throws GeneratorException, NoSuchFieldException, IllegalAccessException {
        GeneratorFile generatorFile = getGeneratorFile();

        Field folderQualifiedNameRegex = GeneratorFile.class.getDeclaredField("folderQualifiedNameRegex");
        folderQualifiedNameRegex.setAccessible(true);

        Assert.assertNotNull(folderQualifiedNameRegex.get(generatorFile));
        generatorFile.setFolderQualifiedNameRegex(".*");
        generatorFile.setFolderQualifiedNameRegex(TestingUtils.getExternalResourceDir().getAbsolutePath());
        Assert.assertNotNull(folderQualifiedNameRegex.get(generatorFile));

        folderQualifiedNameRegex.setAccessible(false);
    }

    /**
     * Generate a PDF document with default settings
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorPdf() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addPdfOutput();
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_PDF).isFile());
    }

    /**
     * Generate a HTML document with default settings
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorHtml() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addHtmlOutput();
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_HTML).isFile());
    }

    /**
     * Generate a HTML document with default settings
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorMd() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addMdOutput();
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_MD).isFile());
    }

    /**
     * Generate a Text document with default settings
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorTxt() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addTxtOutput();
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_TEXT).isFile());
    }

    /**
     * Generate a XML document with default settings
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorXml() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addXmlOutput();
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_XML).isFile());
    }

    /**
     * Generate a PDF document using an external xslt file
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorPdfExternal() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addPdfOutput(new File(TestingUtils.getExternalResourceDir(), "xslt"+File.separator+FileNames.Input.INTERMEDIATE_PDF));
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_PDF).isFile());
    }

    /**
     * Generate a HTML document using an external xslt file
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorHtmlExternal() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addHtmlOutput(new File(TestingUtils.getExternalResourceDir(), "xslt"+File.separator+FileNames.Input.INTERMEDIATE_HTML));
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_HTML).isFile());
    }

    /**
     * Generate a HTML document using an external xslt file
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorMdExternal() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addMdOutput(new File(TestingUtils.getExternalResourceDir(), "xslt"+File.separator+FileNames.Input.INTERMEDIATE_MD));
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_MD).isFile());
    }

    /**
     * Generate a Text document using an external xslt file
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorTxtExternal() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addTxtOutput(new File(TestingUtils.getExternalResourceDir(), "xslt"+File.separator+FileNames.Input.INTERMEDIATE_TEXT));
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_TEXT).isFile());
    }

    /**
     * Generate a XML document using an external xslt file
     *
     * @throws GeneratorException When the generation was not successful
     * @throws IOException When the output file could not be created
     */
    @Test
    public void testGeneratorXmlExternal() throws GeneratorException, IOException {
        GeneratorFile gen = getGeneratorFile();
        gen.addXmlOutput(new File(TestingUtils.getExternalResourceDir(), "xslt"+File.separator+FileNames.Input.INTERMEDIATE_XML));
        gen.generateDocumentation();

        Assert.assertTrue(new File(TestingUtils.getOutputDir(), FileNames.Output.GENERATED_XML).isFile());
    }

    /**
     * Invoke generating documentation without specifying an output type
     *
     * @throws GeneratorException When the generation was not successful
     */
    @Test
    public void testGeneratorDefault() throws GeneratorException {
        GeneratorFile gen = getGeneratorFile();

        try {
            gen.generateDocumentation();
            Assert.fail("Expeting an error 'No documentation method was specified'");
        }catch (GeneratorException e){
            // Success
        }
    }

    /**
     * Clean up all of the generated files
     */
    @After
    public void tearDown() {
        TestingUtils.cleanOutput();
    }

    /**
     * Create a default GeneratorFile
     *
     * @return A default GeneratorFile
     *
     * @throws GeneratorException
     */
    private static GeneratorFile getGeneratorFile() throws GeneratorException {
        return new GeneratorFile(
                TestingUtils.getPackageDir().getAbsolutePath(),
                TestingUtils.getOutputDir().getAbsolutePath(),
                null);
    }
}
