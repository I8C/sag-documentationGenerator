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

import be.i8c.sag.documentationgenerator.out.*;
import be.i8c.sag.nodendf.NodeNdfStream;
import be.i8c.sag.nodendf.NodePackage;
import be.i8c.sag.util.FileNames;
import be.i8c.sag.util.FileUtils;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Allows to specify what and how the documentation should be generated.
 */
public class GeneratorFile {

	private static final Logger logger = LoggerFactory.getLogger(GeneratorFile.class);

  /** A list of all the packages that will be generated into the documentation. */
	private ArrayList<NodePackage> packageNodes = new ArrayList<>();

  /** The fop.conf file */
	private File fopConfigFile;
  /** All documents files that need to be generated. */
  private List<IDocumentationWriter> documents = new ArrayList<>();

  /** The directory where the output will be written to. */
	private Path outputDirectory;

  /** A filter that will only allow packages with a specific name to be generated. */
	private HashSet<String> packageNames;
  /** A filter that will only generated documentation for packages matching the regex. */
	private String packageNameRegex = "^(?!Wm).*";
  /** A filter that will only generated documentation for folders matching the regex. */
	private String folderQualifiedNameRegex = ".*";
  /** The directory where the packages are located. */
	private String packagesDirectory;

  /** Used to check if a file is a valid XML file. */
	private Validator validator;
  /**
   * Used to parse an XML file to a Document.
   * @see Document
   */
  private DocumentBuilder parser = null;

	/**
	 * Here all files will be put into the appropriate streams for the Core
	 *
	 * @param packagesDirectory The directory where the packages are located
	 * @param outputDirectory The directory where the documentations need to be placed
	 * @param configDirectory The directory where the configuration file is located.
	 *   Should be null to use the default.
	 * @throws GeneratorException When setting one of these directories has failed
     */
	public GeneratorFile(String packagesDirectory, String outputDirectory, String configDirectory) throws GeneratorException{
		try {
			this.packagesDirectory = packagesDirectory;
			this.setOutputDirectory(outputDirectory);
			this.setFopConfigFile(configDirectory);
		} catch (DocumentationGenerationException e) {
			throw new GeneratorException("Setting of directory failed", e);
		}
	}

	/**
	 * Starts to create the documentation
	 *
	 * @throws GeneratorException When the generation was not successful
     */
	public void generateDocumentation() throws GeneratorException{
		try {
			FileUtils.extractAssets(new File(outputDirectory.toFile(), FileUtils.ASSETS_FOLDER_NAME));
		} catch (IOException e) {
			logger.error("Failed to extract resources: {}", e.getMessage());
		}

        if (this.documents.size() < 1)
            throw new GeneratorException("No documentation method was specified");

		GeneratorCore core = new GeneratorCore();
		logger.debug("Generator adding all packages and their info to a list of NodePackages.");
		makeNodes();
		NodePackage[] packageNode = packageNodes.toArray(new NodePackage[packageNodes.size()]);

        core.generateDocumentation(packageNode, this.documents);
        logger.trace("GeneratorCore successfully generated documentation.");
	}

	/**
	 * Creating output stream
	 *
	 * @param outputDir The directory where the documentations need to be placed
	 * @throws GeneratorException If the outputDir is not a directory
     */
	public void setOutputDirectory(String outputDir) throws GeneratorException {
		File d = new File(outputDir);
		if(!(d.exists() && d.isDirectory())){
			if(!d.exists())logger.debug("Creating {} because directory doesn't exist.", d);
			if(!d.isDirectory())logger.debug("Creating {} because directory is not a directory.", d);
			d.mkdirs();
		}
		outputDirectory = setDirectory(outputDir, "output");
		logger.info("Output folder: '{}'", outputDirectory.toAbsolutePath());
	}

	/**
	 * General function for checking and setting the directory.
	 *
	 * @param directoryString The location of the path that needs to be created
	 * @param directoryType Used for debugging
	 * @return The path to that directory
	 * @throws GeneratorException If the directoryString is not a directory
	 * @see Path
     */
	private Path setDirectory(String directoryString, String directoryType) throws GeneratorException {
		Path directoryToSet = Paths.get(directoryString);
		logger.debug("Setting {} as {} directory", directoryString, directoryType);
		if (Files.isDirectory(directoryToSet)) {
			return directoryToSet;
		} else {
			throw new GeneratorException("Unable to set " + directoryType + " directory: '" + directoryString + "' not found or is not a directory.");
		}
	}

	/**
	 * Creating the fopConfigFile stream
	 *
	 * @param configDirectory Passing null will use the internal file,
	 *   passing a directory will use the fop.xconf file in that directory
	 * @throws GeneratorException When an external fop.xconf file is used but not found
	 * @throws DocumentationGenerationException When it was unable to create a temporary fop.xconf file
     */
  public void setFopConfigFile(String configDirectory) throws GeneratorException, DocumentationGenerationException {
        if (configDirectory == null) {
            logger.debug("Using default fop.conf");
			try {
				this.fopConfigFile = FileUtils.createTempFopConf(getClass().getResourceAsStream("/fop.xconf"));
			} catch (IOException e) {
				throw new DocumentationGenerationException("Error creating temporary fop.xconf ",e);
			}
		} else {
			logger.debug("Using external fop.conf");
            File conf = new File(configDirectory + "/fop.xconf");
            if (conf.isFile()) {
                logger.debug("Accessing {}", conf);
                this.fopConfigFile = conf;
                logger.debug("Success setting {} as the fop fopConfigFile.", conf);
            } else {
                logger.error("Given {} is not a file", conf);
                throw new GeneratorException(configDirectory + "/fop.xconf is not a file");
            }
        }
    }

	/**
	 * Set all of the package names that will be accepted
	 * This is capital sensitive
	 *
	 * Setting this will override the packageNameRegex check,
	 * see {@link #setPackageNameRegex(String) setPackageNameRegex}
	 *
	 * @param packageNames All the packages that can be accepted
     */
  public void setPackageNames(String[] packageNames) {
		logger.debug("Setting the packageNames hashSet and setting packageNameRegex to '.*'");
		if (packageNames.length != 0) {
			this.packageNames = new HashSet<>(Arrays.asList(packageNames));
			this.packageNameRegex = ".*";
		}
	}

	/**
	 * Set the regex check to only accept a package when
	 * it matches the check
	 * Default value: '^(?!Wm).*' which means
	 * accept everything that does not start with 'Wm' (public packages)
	 *
	 * Setting this will override the packageName check,
	 * see {@link #setPackageNames(String[]) setPackageNames}
	 *
	 * @param packageNameRegex The package regex check
     */
	public void setPackageNameRegex(String packageNameRegex) {
		logger.debug("setting packageNameRegex to '{}' and setting packageNames hashSet to 'null'", packageNameRegex);
		this.packageNameRegex = packageNameRegex;
		this.packageNames = null;
	}

	/**
	 * Set the regex check to only accept a node when
	 * the folder matches the check
	 * Default value is '.*' (accept all)
	 *
	 * @param folderQualifiedNameRegex The folder regex check
     */
	public void setFolderQualifiedNameRegex(String folderQualifiedNameRegex) {
		logger.debug("Setting folderQualifiedNameRegex. to {}", folderQualifiedNameRegex);
		this.folderQualifiedNameRegex = folderQualifiedNameRegex;
	}

	/**
	 * Add a PDF document generator to the documents that need to be created
	 * Will use the default xslt file that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
     */
	public void addPdfOutput() throws IOException {
		addPdfOutput(getClass().getResourceAsStream('/'+FileNames.Input.INTERMEDIATE_PDF));
	}

	/**
	 * Add a PDF document generator to the documents that need to be created
	 * @param xsltFile The external xslt file that will be used for the transformation
	 *                    
	 * @throws IOException When it fails to be able to write to the output file
     */
	public void addPdfOutput(File xsltFile) throws IOException {
		addPdfOutput(new FileInputStream(xsltFile));
	}

	/**
	 * Add a PDF document generator to the documents that need to be created
	 * @param xsltStream The data stream that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
     */
	public void addPdfOutput(InputStream xsltStream) throws IOException
	{
		// Extract all assets separately since the working directory is not the same as the output directory
		File pdfAssets = new File("pdf-assets");
		pdfAssets.deleteOnExit();
		FileUtils.extractAssets(pdfAssets, true);

		//Add the PDF generator
		File outputFile = new File(outputDirectory+File.separator+FileNames.Output.GENERATED_PDF);
		if (!outputFile.exists())
			outputFile.createNewFile();
		this.documents.add(new XsltFopFactoryDocWriter(
				fopConfigFile,
				xsltStream,
				new FileOutputStream(outputFile),
				MimeConstants.MIME_PDF
		));
	}

	/**
	 * Add a Text document generator to the documents that need to be created
	 * Will use the default xslt file that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addTxtOutput() throws IOException {
		addTxtOutput(getClass().getResourceAsStream('/'+FileNames.Input.INTERMEDIATE_TEXT));
	}

	/**
	 * Add a Text document generator to the documents that need to be created
	 * @param xsltFile The external xslt file that will be used for the transformation
	 *                    
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addTxtOutput(File xsltFile) throws IOException {
		addTxtOutput(new FileInputStream(xsltFile));
	}

	/**
	 * Add a Text document generator to the documents that need to be created
	 * @param xsltStream The data stream that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addTxtOutput(InputStream xsltStream) throws IOException
	{
		File outputFile = new File(outputDirectory+File.separator+FileNames.Output.GENERATED_TEXT);
		if (!outputFile.exists())
			outputFile.createNewFile();
		this.documents.add(new XsltStreamResultDocWriter(
				fopConfigFile,
				xsltStream,
				new FileOutputStream(outputFile)
		));
	}

	/**
	 * Add an XML document generator to the documents that need to be created
	 * Will use the default xslt file that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addXmlOutput() throws IOException {
		addXmlOutput(getClass().getResourceAsStream('/'+FileNames.Input.INTERMEDIATE_XML));
	}

	/**
	 * Add an XML document generator to the documents that need to be created
	 * @param xsltFile The external xslt file that will be used for the transformation
	 *                    
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addXmlOutput(File xsltFile) throws IOException {
		addXmlOutput(new FileInputStream(xsltFile));
	}

	/**
	 * Add an XML document generator to the documents that need to be created
	 * @param xsltStream The data stream that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addXmlOutput(InputStream xsltStream) throws IOException
	{
		File outputFile = new File(outputDirectory+File.separator+FileNames.Output.GENERATED_XML);
		if (!outputFile.exists())
			outputFile.createNewFile();
		this.documents.add(new XsltStreamResultDocWriter(
				fopConfigFile,
				xsltStream,
				new FileOutputStream(outputFile)
		));
	}

	/**
	 * Add an HTML document generator to the documents that need to be created
	 * Will use the default xslt file that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addHtmlOutput() throws IOException {
		addHtmlOutput(getClass().getResourceAsStream('/'+FileNames.Input.INTERMEDIATE_HTML));
	}

	/**
	 * Add an HTML document generator to the documents that need to be created
	 * @param xsltFile The external xslt file that will be used for the transformation
	 *                    
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addHtmlOutput(File xsltFile) throws IOException {
		addHtmlOutput(new FileInputStream(xsltFile));
	}

	/**
	 * Add an HTML document generator to the documents that need to be created
	 * @param xsltStream The data stream that will be used for the transformation
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
  public void addHtmlOutput(InputStream xsltStream) throws IOException {
		File outputFile = new File(outputDirectory+File.separator+FileNames.Output.GENERATED_HTML);
        if (!outputFile.exists())
            outputFile.createNewFile();
		this.documents.add(new XsltStreamResultDocWriter(
				fopConfigFile,
				xsltStream,
				new FileOutputStream(outputFile)
		));
    }

	/**
	 * Add a Markdown document generator to the documents that need to be created
	 * Will use the default xslt file that will be used for the transformation
	 *
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addMdOutput() throws IOException {
		addMdOutput(getClass().getResourceAsStream('/'+FileNames.Input.INTERMEDIATE_MD));
	}

	/**
	 * Add a Markdown document generator to the documents that need to be created
	 * @param xsltFile The external xslt file that will be used for the transformation
	 *
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addMdOutput(File xsltFile) throws IOException {
		addMdOutput(new FileInputStream(xsltFile));
	}

	/**
	 * Add a Markdown document generator to the documents that need to be created
	 * @param xsltStream The data stream that will be used for the transformation
	 *
	 * @throws IOException When it fails to be able to write to the output file
	 */
	public void addMdOutput(InputStream xsltStream) throws IOException {
		File outputFile = new File(outputDirectory+File.separator+FileNames.Output.GENERATED_MD);
		if (!outputFile.exists())
			outputFile.createNewFile();
		this.documents.add(new XsltStreamResultDocWriter(
				fopConfigFile,
				xsltStream,
				new FileOutputStream(outputFile)
		));
	}

	/**
	 * Add an Intermediate XML document generator to the documents that need to be created
	 * 
	 * @throws IOException When it fails to be able to write to the output file
	 */
  public void addIntermediateOutput() throws IOException {
        File outputFile = new File(outputDirectory + File.separator + FileNames.Output.GENERATED_INTERMEDIATE_XML);
        if (!outputFile.exists())
            outputFile.createNewFile();
        this.documents.add(new IntermediateDocumentationWriter(new FileOutputStream(outputFile)));
    }

	/**
	 * Creating the nodes from the packages
	 *
	 * @throws GeneratorException When the generation was not successful
	 */
  public void makeNodes() throws GeneratorException {
        if (packagesDirectory == null) {
            throw new GeneratorException("No packages directory given!");
        }
        File[] packages = new File(packagesDirectory).listFiles();
        if (packages == null) {
            throw new GeneratorException("No packages found in '" + packagesDirectory + "'.");
        }
        prepareXmlValidator();
        for (File p : packages) {
            if (!p.isDirectory()) continue;

            String packageName = p.getAbsoluteFile().getName();
            if ((this.packageNames != null || !packageName.matches(this.packageNameRegex)) && (this.packageNames == null || !this.packageNames.contains(packageName)))
                continue;

            File manifestFile = new File(p + "/manifest.v3");
            if (!manifestFile.exists()) {
                logger.warn("Manifest file not found for package {}. The Package will be ignored.", p.getName());
                continue;
            }
            if (!isValidXml(manifestFile)) {
                logger.warn("Manifest file of package {} is not a valid xml. The Package will be ignored", p.getName());
                continue;
            }
            logger.trace("Found existing and valid xml manifest.v3 for {}", packageName);
            NodePackage np;
            try {
                np = new NodePackage(packageName, new FileInputStream(p + "/manifest.v3"));
            } catch (FileNotFoundException e1) {
                throw new GeneratorException("Manifest file not found for package " + p.getName(), e1);
            }
            String pattern = "node.ndf";
            Finder finder = new Finder(pattern);
            try {
                Files.walkFileTree(p.toPath(), finder);
            } catch (IOException e) {
                throw new GeneratorException("Error searching for " + pattern + " files", e);
            }
            for (File file : finder.list) {
                String fileName = file.getParentFile().toString();
                String service = file.getParentFile().getName();
                String prefix = fileName.substring(fileName.indexOf("ns" + File.separator) + 3, fileName.indexOf(File.separator + service)).replace(File.separator, ".");

                if (!prefix.matches(folderQualifiedNameRegex)) continue;

                if (!isValidXml(file)) {
                    logger.warn("{}:{} doesn't have a valid node.ndf file. This service will be ignored", prefix, service);
                    continue;
                }
                try {
                    logger.trace("Adding the service {}:{} to nodes", prefix, service);
                    np.addNodeNdfStream(new NodeNdfStream(new FileInputStream(file), service, prefix));
                } catch (FileNotFoundException e) {
                    throw new GeneratorException("Error adding the service " + prefix + ":" + service + " to nodes", e);
                }
            }
            packageNodes.add(np);
        }
    }

	/**
	 * Initialize the validator and parser
	 *
	 * @throws GeneratorException When it can't create a parser or validator
     */
  private void prepareXmlValidator() throws GeneratorException{

		// parse an XML document into a DOM tree
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e2) {
			throw new GeneratorException("Error creating new documentBuilder",e2);
		}
	    // create a SchemaFactory capable of understanding WXS schemas
	    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	    // load a WXS schema, represented by a Schema instance
	    Source schemaFile = new StreamSource(getClass().getResourceAsStream("/nodeSchema.xsd"));
	    Schema schema;
		try {
			schema = factory.newSchema(schemaFile);
		} catch (SAXException e1) {
			throw new GeneratorException("Error creating validating schema for xml check", e1);
		}

	    // create a Validator instance, which can be used to validate an instance document
	    this.validator = schema.newValidator();
	}

	/**
	 * Check if the file has a valid XML format
	 *
	 * @param file The XML file to perform the checks on
	 * @return True if the file has a valid XML format,
	 * 		   False if not
     */
	private boolean isValidXml(File file) {
	    Document document;
		try {
			document = parser.parse(file);
		} catch (SAXException | IOException e) {
			logger.warn("Error parsing file '{}'. {}",file.getAbsolutePath(), e);
			return false;
		}
	    // validate the DOM tree
	    try {
	        validator.validate(new DOMSource(document));
	    } catch (SAXException | IOException e) {
	    	logger.warn("Error validating {} in directory '{}'.", file.getName(), file.getParent(), e);
	        return false;
	    }
		return true;
	}

	/**
	 * Finder class for finding all files that match a pattern in a folder and subfolders
	 * @author i8c
	 */
	private class Finder extends SimpleFileVisitor<Path>{
		private final PathMatcher matcher;
		private ArrayList<File> list = new ArrayList<>();

		/**
		 * Finds all files matching a pattern
		 *
		 * @param pattern The patterns it needs to match minus the 'glob:' prefix
		 * @see PathMatcher
		 */
		Finder(String pattern) {
			matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        }

		/**
		 * Compares the glob pattern against the file or directory name.
		 *
		 * @param file The file or directory to compare with the matcher
		 */
		void find(Path file) {
			Path name = file.getFileName();
			if (name != null && matcher.matches(name)) {
				list.add(file.toFile());
			}
		}

		/**
		 *  Invoke the pattern matching method on each file.
		 */
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			find(file);
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			logger.error(exc.getMessage(), exc);
			return CONTINUE;
		}
	}

}
