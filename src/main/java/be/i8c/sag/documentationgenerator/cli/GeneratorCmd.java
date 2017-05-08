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

import be.i8c.sag.documentationgenerator.GeneratorFile;
import be.i8c.sag.util.FileNames;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The main class for the commandline interface.
 * The GeneratorCmd will get invoked from the commandline.
 */
public final class GeneratorCmd {

	private static final Logger logger = LoggerFactory.getLogger(GeneratorCmd.class);

	private static final Option packageDirectory = Option.builder(Arguments.Names.PACKAGE_DIR)
																											 .longOpt(Arguments.Names.PACKAGE_DIR_LONG)
																											 .hasArg()
																											 .required()
																											 .desc("Directory where the packages to generate documentation for can be found. The packages should be right at the root "
																	   + "of this directory and contain a manifest.v3 file to be recognized as packages. This is a mandatory argument!")
																											 .build();

	private static final Option outputDirectory = Option.builder(Arguments.Names.OUTPUT_DIR)
														.longOpt(Arguments.Names.OUTPUT_DIR_LONG)
														.hasArg()
														.desc("Directory where the generated documentation will be written to.  The default value is '"
																	  + Arguments.DefaultValues.OUTPUT_DIR + "'.")
														.build();

	private static final Option xsltDirectory = Option.builder(Arguments.Names.XSLT_DIR)
													  .longOpt(Arguments.Names.XSLT_DIR_LONG)
													  .hasArg()
													  .desc("Directory where the xslt files can be found that will be used when generating the documentation from the intermediate "
																	+ "xml. When the argument is not provided, a default file is used.")
													  .build();

	private static final Option configDirectory = Option.builder(Arguments.Names.CONFIG_DIR)
														.longOpt(Arguments.Names.CONFIG_DIR_LONG)
														.hasArg()
														.desc("Directory that contains various configuration files (e.g. Apache FOP config file that is needed when generating "
																	  + "documentation in pdf format). When the argument is not provided, a default file is used.")
														.build();

	private static final Option packageNameRegex = Option.builder(Arguments.Names.PACKAGE_NAME_REGEX)
														 .longOpt(Arguments.Names.PACKAGE_NAME_REGEX_LONG)
														 .hasArg()
														 .desc("Documentation will only be generated for the packages whose names match the provided regular expression. For example, "
																	   + "if you have a package Acme, the regex expression Ac.* can be used to find all packages starting with Ac or \"^(?!Wm).*$\""
																	   + "can be used to select all packages that do not start with Wm or \"^(?!.*cm).*$\" for all packages that do not contain cm. "
																	   + "All packages in the package directory will be used by default when no package name indication is provided.  The default value is " + Arguments.DefaultValues.PACKAGE_NAME_REGEX)
														 .build();

	private static final Option packageNames = Option.builder(Arguments.Names.PACKAGE_NAMES)
													 .longOpt(Arguments.Names.PACKAGE_NAMES_LONG)
													 .hasArg()
													 .desc("Documentation will only be generated for the packages whose names are provided. Will be ignored when option '"
																   + "PACKAGE_NAME_REGEX' is also provided. All packages in the package directory will be used by default when no package "
																   + "name indication is provided.")
													 .build();

	private static final Option folderQualifiedNameRegex = Option.builder(Arguments.Names.FOLDER_QUALIFIER_NAME_REGEX)
																 .longOpt(Arguments.Names.FOLDER_QUALIFIER_NAME_REGEX_LONG)
																 .hasArg()
																 .desc("Documentation will only be generated for components whose fully qualified folder names match the provided regular "
																			   + "expression. Documentation will be generated for all encountered components by default when no folder name "
																			   + "indication is provided. The default value is '" + Arguments.DefaultValues.FOLDER_QUALIFIER_NAME_REGEX + '\'')
																 .build();

	private static final Option writeXml = Option.builder(Arguments.Names.GENERATE_XML)
												 .longOpt(Arguments.Names.GENERATE_XML_LONG)
												 .desc("Set option if you want the xml documentation to be written to the output directory during the generation process. By "
															   + "default it won't be generated. If the -"+Arguments.Names.XSLT_DIR+" argument is specified, it will look look "
															   + "for the file " + FileNames.Input.INTERMEDIATE_XML + " in that directory")
												 .build();

	private static final Option writePdf = Option.builder(Arguments.Names.GENERATE_PDF)
												 .longOpt(Arguments.Names.GENERATE_PDF_LONG)
												 .desc("Set option if you want the pdf documentation to be written to the output directory during the generation process. By "
															   + "default it will be generated if no other generation arguments are given. If the -"+Arguments.Names.XSLT_DIR
															   + " argument is specified, it will look look for the file " + FileNames.Input.INTERMEDIATE_PDF + " in that directory")
												 .build();

	private static final Option writeHtml = Option.builder(Arguments.Names.GENERATE_HTML)
												  .longOpt(Arguments.Names.GENERATE_HTML_LONG)
												  .desc("Set option if you want the html documentation to be written to the output directory during the generation process. By "
																+ "default it won't be generated."+Arguments.Names.XSLT_DIR+" argument is specified, it will look look "
																+ "for the file " + FileNames.Input.INTERMEDIATE_HTML + " in that directory")
												  .build();

	private static final Option writeMd = Option.builder(Arguments.Names.GENERATE_MD)
																								.longOpt(Arguments.Names.GENERATE_MD_LONG)
																								.desc("Set option if you want the markdown documentation to be written to the output directory during the generation process. By "
																													+ "default it won't be generated."+Arguments.Names.XSLT_DIR+" argument is specified, it will look look "
																													+ "for the file " + FileNames.Input.INTERMEDIATE_MD + " in that directory")
																								.build();

	private static final Option writeTxt = Option.builder(Arguments.Names.GENERATE_TXT)
												 .longOpt(Arguments.Names.GENERATE_TXT_LONG)
												 .desc("Set option if you want the text documentation to be written to the output directory during the generation process. By "
															   + "default it won't be generated."+Arguments.Names.XSLT_DIR+" argument is specified, it will look look "
															   + "for the file " + FileNames.Input.INTERMEDIATE_TEXT + " in that directory")
												 .build();

	private static final Option writeIntermediateXml = Option.builder(Arguments.Names.INTERMEDIATE_XML)
															 .longOpt(Arguments.Names.INTERMEDIATE_XML_LONG)
															 .desc("Set option if you want the intermediate xml to be written to the output directory during the generation process. By "
																		   + "default the intermediate xml is only held in memory and not written to file.")
															 .build();

	private static Options createOptions(){
		Options CLI_OPTIONS = new Options();
		CLI_OPTIONS.addOption(packageDirectory);
		CLI_OPTIONS.addOption(outputDirectory);
		CLI_OPTIONS.addOption(xsltDirectory);
		CLI_OPTIONS.addOption(configDirectory);
		CLI_OPTIONS.addOption(packageNameRegex);
		CLI_OPTIONS.addOption(packageNames);
		CLI_OPTIONS.addOption(folderQualifiedNameRegex);
		CLI_OPTIONS.addOption(writeIntermediateXml);
		CLI_OPTIONS.addOption(writeXml);
    CLI_OPTIONS.addOption(writeHtml);
    CLI_OPTIONS.addOption(writeMd);
		CLI_OPTIONS.addOption(writePdf);
		CLI_OPTIONS.addOption(writeTxt);
		return CLI_OPTIONS;
	}

	public static void main(String[] args) throws Exception {
		Options CLI_OPTIONS = createOptions();

		if(args.length==0){
			logger.debug("Show arguments explanation if no arguments are provided.");
			printOptions(CLI_OPTIONS);
		}
		else{
			Arguments arguments;
			try {
				logger.debug("Attempt to parse arguments.");
				arguments = new Arguments(new DefaultParser().parse(CLI_OPTIONS, args));
			} catch (ParseException e) {
				throw new ParseException("Error occurred parsing the arguments that were passed to the client: " + e.getMessage());
			}

			GeneratorFile documentationGenerator = new GeneratorFile(arguments.PACKAGE_DIR, arguments.OUTPUT_DIR, arguments.CONFIG_DIR);

			logger.debug("Setting package name regex to: {}.", packageNameRegex);
			documentationGenerator.setPackageNameRegex(arguments.PACKAGE_NAME_REGEX);
			if (arguments.PACKAGE_NAMES != null && arguments.PACKAGE_NAMES.length > 0) {
				documentationGenerator.setPackageNames(arguments.PACKAGE_NAMES);
			}

			logger.debug("Setting folder qualified name regex to: {}.", arguments.FOLDER_QUALIFIER_NAME_REGEX);
			documentationGenerator.setFolderQualifiedNameRegex(arguments.FOLDER_QUALIFIER_NAME_REGEX);



			if (!arguments.GENERATE_HTML &&
					!arguments.GENERATE_MD  &&
					!arguments.GENERATE_TXT &&
					!arguments.GENERATE_PDF &&
					!arguments.GENERATE_XML &&
					!arguments.INTERMEDIATE_XML)
			{
				logger.warn("None of the following arguments have been given. -{}, -{}, -{}, -{}, -{}, -{}. Generating PDF as default",
										Arguments.Names.GENERATE_HTML,
										Arguments.Names.GENERATE_MD,
										Arguments.Names.GENERATE_PDF,
										Arguments.Names.GENERATE_TXT,
										Arguments.Names.GENERATE_XML,
										Arguments.Names.INTERMEDIATE_XML);
				documentationGenerator.addPdfOutput();
			}else 
			{
				if (arguments.GENERATE_HTML) {
					if (arguments.XSLT_DIR != null)
						documentationGenerator.addHtmlOutput(new File(arguments.XSLT_DIR, FileNames.Input.INTERMEDIATE_HTML));
					else
						documentationGenerator.addHtmlOutput();
				}

				if (arguments.GENERATE_MD) {
					if (arguments.XSLT_DIR != null)
						documentationGenerator.addMdOutput(new File(arguments.XSLT_DIR, FileNames.Input.INTERMEDIATE_MD));
					else
						documentationGenerator.addMdOutput();
				}

				if (arguments.GENERATE_PDF) {
					if (arguments.XSLT_DIR != null)
						documentationGenerator.addPdfOutput(new File(arguments.XSLT_DIR, FileNames.Input.INTERMEDIATE_XML));
					else
						documentationGenerator.addPdfOutput();
				}

				if (arguments.GENERATE_TXT) {
					if (arguments.XSLT_DIR != null)
						documentationGenerator.addTxtOutput(new File(arguments.XSLT_DIR, FileNames.Input.INTERMEDIATE_TEXT));
					else
						documentationGenerator.addTxtOutput();
				}

				if (arguments.GENERATE_XML) {
					if (arguments.XSLT_DIR != null)
						documentationGenerator.addXmlOutput(new File(arguments.XSLT_DIR, FileNames.Input.INTERMEDIATE_XML));
					else
						documentationGenerator.addXmlOutput();
				}

				if (arguments.INTERMEDIATE_XML)
					documentationGenerator.addIntermediateOutput();
			}

			documentationGenerator.generateDocumentation();
		}
	}

	private static void printOptions(Options CLI_OPTIONS) {
		String header = "\ni8c Documentation Generator For WebMethods Packages, arguments between [] are optional\n------------------------------------------------------------------------\n\n";
		String footer = "";
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar documentGenerator-<version>(-jar-with-dependencies).jar", header, CLI_OPTIONS, footer, true);
	}
}