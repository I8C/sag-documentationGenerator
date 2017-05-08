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

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to handle the arguments given from the commandline interface
 *
 * @see CommandLine
 */
public class Arguments {
    private static final Logger logger = LoggerFactory.getLogger(Arguments.class);

  /** The directory where the packages are located. */
    public final String PACKAGE_DIR;
  /**
   * (Optional)
   * The directory where the generated files will be written to.
   * Default: The working directory.
   */
    public final String OUTPUT_DIR;
  /**
   * (Optional)
   * Only packages matching the regex will be used.
   * Overrides PACKAGE_NAMES.
   * Default: <code>^(?!Wm).*</code>
   */
    public final String PACKAGE_NAME_REGEX;
  /**
   * (Optional)
   * Documentation will only be generated for components whose fully
   * qualified folder names match the provided regular expression
   * Default: <code>.*</code>
   */
    public final String FOLDER_QUALIFIER_NAME_REGEX;
  /**
   * (Optional)
   * Only packages matching a name will be used.
   * Overrides PACKAGE_NAME_REGEX.
   * Default: <code>new String[0];</code>
   */
    public final String[] PACKAGE_NAMES;
  /**
   * (Optional)
   * Use custom xslt files.
   * If the value is null, the internal files will be used.
   * Default: <code>null</code>
   */
    public final String XSLT_DIR;
  /**
   * (Optional)
   * Use a custom fop.conf file.
   * If the value is null, the internal file will be used.
   * Default: <code>null</code>
   */
    public final String CONFIG_DIR;

  /** If true, generate XML documentation */
    public final boolean GENERATE_XML;
  /** If true, generate PDF documentation */
    public final boolean GENERATE_PDF;
  /** If true, generate HTML documentation */
  public final boolean GENERATE_HTML;
  /** If true, generate Markdown documentation */
  public final boolean GENERATE_MD;
  /** If true, generate Text documentation */
    public final boolean GENERATE_TXT;
  /** If true, write the intermediate XML file */
    public final boolean INTERMEDIATE_XML;

    public Arguments(CommandLine commandLine) throws IllegalArgumentException {
        // Required
        PACKAGE_DIR = commandLine.getOptionValue(Names.PACKAGE_DIR_LONG);
        if (PACKAGE_DIR == null)
            throw new IllegalArgumentException(Names.PACKAGE_DIR_LONG + " (" + Names.PACKAGE_DIR + ") is a required argument!");
        log("PACKAGE_DIR", PACKAGE_DIR);

        // Have default values
        OUTPUT_DIR = commandLine.getOptionValue(Names.OUTPUT_DIR_LONG, DefaultValues.OUTPUT_DIR);
        log("OUTPUT_DIR", OUTPUT_DIR);
        PACKAGE_NAME_REGEX = commandLine.getOptionValue(Names.PACKAGE_NAME_REGEX_LONG, DefaultValues.PACKAGE_NAME_REGEX);
        log("PACKAGE_NAME_REGEX", PACKAGE_NAME_REGEX);
        String[] packageNames = commandLine.getOptionValues(Names.PACKAGE_NAMES_LONG);
        if (packageNames == null)
            PACKAGE_NAMES = DefaultValues.PACKAGE_NAMES;
        else
            PACKAGE_NAMES = packageNames;
        log("PACKAGE_NAMES", PACKAGE_NAMES);
        FOLDER_QUALIFIER_NAME_REGEX = commandLine.getOptionValue(Names.FOLDER_QUALIFIER_NAME_REGEX_LONG, DefaultValues.FOLDER_QUALIFIER_NAME_REGEX);
        log("FOLDER_QUALIFIER_NAME_REGEX", FOLDER_QUALIFIER_NAME_REGEX);

        // Are optional
        XSLT_DIR = commandLine.getOptionValue(Names.XSLT_DIR_LONG);
        log("XSLT_DIR", XSLT_DIR);
        CONFIG_DIR = commandLine.getOptionValue(Names.CONFIG_DIR_LONG);
        log("CONFIG_DIR", CONFIG_DIR);

        // Options are either present or not
        GENERATE_XML = commandLine.hasOption(Names.GENERATE_XML_LONG);
        log("GENERATE_XML", GENERATE_XML);
        GENERATE_PDF = commandLine.hasOption(Names.GENERATE_PDF_LONG);
        log("GENERATE_PDF", GENERATE_PDF);
        GENERATE_HTML = commandLine.hasOption(Names.GENERATE_HTML_LONG);
        log("GENERATE_HTML", GENERATE_HTML);
        GENERATE_MD = commandLine.hasOption(Names.GENERATE_MD_LONG);
        log("GENERATE_MD", GENERATE_MD);
        GENERATE_TXT = commandLine.hasOption(Names.GENERATE_TXT_LONG);
        log("GENERATE_TXT", GENERATE_TXT);
        INTERMEDIATE_XML = commandLine.hasOption(Names.INTERMEDIATE_XML_LONG);
        log("INTERMEDIATE_XML", INTERMEDIATE_XML);
    }

    private void log(String fieldName, Object value) {
        logger.debug("Argument {} has received the value {}", fieldName, value);
    }

  /**
   * Names of the parameters passed through
   */
    public static final class Names {
        public static final String PACKAGE_DIR = "pd";
        public static final String PACKAGE_DIR_LONG = "PACKAGE_DIRECTORY";
        public static final String OUTPUT_DIR = "od";
        public static final String OUTPUT_DIR_LONG = "OUTPUT_DIRECTORY";
        public static final String XSLT_DIR = "xd";
        public static final String XSLT_DIR_LONG = "XSLT_DIRECTORY";
        public static final String CONFIG_DIR = "cd";
        public static final String CONFIG_DIR_LONG = "CONFIG_DIRECTORY";
        public static final String PACKAGE_NAME_REGEX = "pnr";
        public static final String PACKAGE_NAME_REGEX_LONG = "PACKAGE_NAME_REGEX";
        public static final String PACKAGE_NAMES = "pn";
        public static final String PACKAGE_NAMES_LONG = "PACKAGE_NAMES";
        public static final String FOLDER_QUALIFIER_NAME_REGEX = "fqnr";
        public static final String FOLDER_QUALIFIER_NAME_REGEX_LONG = "FOLDER_QUALIFIED_NAME_REGEX";

        public static final String GENERATE_XML = "xml";
        public static final String GENERATE_XML_LONG = "GENERATE_XML";
        public static final String GENERATE_PDF = "pdf";
        public static final String GENERATE_PDF_LONG = "GENERATE_PDF";
        public static final String GENERATE_HTML = "html";
        public static final String GENERATE_HTML_LONG = "GENERATE_HTML";
        public static final String GENERATE_MD = "md";
        public static final String GENERATE_MD_LONG = "GENERATE_MARKDOWN";
        public static final String GENERATE_TXT = "txt";
        public static final String GENERATE_TXT_LONG = "GENERATE_TXT";
        public static final String INTERMEDIATE_XML = "ix";
        public static final String INTERMEDIATE_XML_LONG = "INTERMEDIATE_XML";
    }

  /**
   * Default values of the parameters passed through
   */
    public static final class DefaultValues{

        public static final String OUTPUT_DIR = System.getProperty("user.dir");
        public static final String PACKAGE_NAME_REGEX = "^(?!Wm).*";
        public static final String FOLDER_QUALIFIER_NAME_REGEX = ".*";
        public static final String[] PACKAGE_NAMES = new String[0];
    }
}
