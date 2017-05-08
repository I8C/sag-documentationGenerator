# Index
- [Synopsis](#synopsis)
- [Build](#build)
- [Run](#run)
- [License](#license)
- [Contribute & report issues](#contribute-report-issues)

# Synopsis

The Documentation Generator for Software AG (SAG) webMethods by [Integr8 Consulting (i8c)](http://www.i8c.be), is a tool that automatically
generates documentation for code developed on the SAG webMethods Integration Server (IS).
It extracts the comments added by the developer during development and writes them into a file to create well structured,
easily readable and formatted code documentation.
In the current version, the content from the following comments fields are extracted to generate the documentation:  
For IS services:  
* comments on the service  
* comments on the in- and output parameters of the service  

For IS document types:  
* comments on the document type itself
* comments on the document type elements

## Supported output formats:
- PDF
- HTML
- XML
- TXT
- MD
  
Software AG's *webMethods Integration Server Built-In Services Reference* documentation served as the reference example for the PDF format,
as it is probably one of the most frequently consulted manuals. The other formats have been derived from this format.

# Build
**What you’ll need:**
- About 10 minutes
- JDK 1.8 or later
- Maven 3.0+  
Optionally:
- Git (to clone/download the code)
- Your IDE (SAG Designer) to perform the build

This project uses Maven to manage its dependencies, build the product, run tests and generate the javadocs.
If you wish to make changes to this project, here are some things you may wish to know.

## Getting the source code with git
Clone this repository using Git from the command line:
```sh
git clone <repo URL>
```

## Installing the jars into your local maven repo
> mvn install

Maven install will first build the jar and its javadocs just like _mvn package_
but will also install it into your local maven repository so you
can use it for other projects.

It is advised to use the maven install before doing anything else
if you just pulled the source.

## Building the jars
> mvn package

The result of this action will create the javadocs and 2 jars.
A jar with only the compiled code of this project and
a jar injected with all of the dependencies.
These jars can be found in the _target_ folder
and the javadocs on the documentation folder.

## Updating the javadocs
> mvn javadoc:javadoc

When you have changed the source code and updated the javadoc comments
you will want to update the javadocs listed under documentation also.
Invoking this command will generate the javadocs
and place them under `<project root>/documentation/java-docs`

The javadocs automatically get updated when the maven package phase is triggered.

## Running the unit tests
> mvn test

This project already contains unit tests for all methods. When you change code,
make sure you run these tests before making the changes permanent.
The unit tests are also automatically invokes when trying to install or package the jars with maven.

## Skipping the unit tests
> mvn -DskipTests=true ```<package|run>```

**!!This is not advised!!**  
You can skip the unit tests by adding the right argument.

## Build with SAG Designer

Start SAG Designer and select File -> Import.
In the Import pop-up, select Maven -> Existing Maven Projects and click Next.
Select the folder where the pom.xml file is located in the next window and click Finish.

Switch to the Java Perspective (upper right corner in the SAG Designer IDE).
In the Package Explorer window on the left,
right-click on the imported project and select
Run As -> Maven install from the menu and verify that the project is successfully build.
A target folder will be created where you can find the executable .jar file.

# Run

_Using the jar build in the previous section (located in the target folder):_

## Run from the cli

```sh
java [options] -jar <jar file> [arguments]
```
- **Options** are handled by the JVM or dependent libraries
- **Arguments** are passed to and handled by the application itself

### Options
#### Logging
You can specify the logging level of the application. You can change this the level with the following arguments:  
  
  
| Log level | command line option                                                                   |  
| --------- | ------------------------------------------------------------------------------------  |  
| INFO      | This is active by default                                                             |  
| DEBUG     | -Dlogback.configurationFile=logback-debug.xml                                         |  
| TRACE     | -Dlogback.configurationFile=logback-trace.xml                                         |  
| CUSTOM    | Make sure to include the './' at the beginning of the path if it is a relative path. This is not needed for absolute paths. For example:   -Dlogback.configurationFile=./relative/path/to/custom/logback.xml  -Dlogback.configurationFile=/absolute/path/to/custom/logback.xml  |  
  
### Arguments

-pd or -PACKAGE_DIRECTORY
> Directory where the packages to generate documentation for can be found. The packages should be right at the root of this directory and contain a manifest.v3 file
> to be recognized as packages. **This is a mandatory argument!**

 -od or -OUTPUT_DIRECTORY

> Directory where the generated documentation will be written to. **Default when not provided is 'test/output/'.**

 -xd or -XSLT_DIRECTORY

> Directory where the xslt files can be found that will be used when generating the documentation from the intermediate xml. **When the argument is not provided,
a default file is used.**

 -cd or -CONFIG_DIRECTORY

> Directory that contains various configuration files (e.g. Apache FOP config file that is needed when generating documentation in pdf format).
**When the argument is not provided, a default file is used.**

 -pnr or -PACKAGE_NAME_REGEX

> Documentation will only be generated for the packages whose names match the provided regular expression. For example, if you have a package Acme,
the regex expression "\^Ac.\*" can be used to find all packages starting with Ac or "\^(?!Wm).\*$" can be used to select all packages that do not start with
Wm or "\^(?!.*cm).\*$" for all packages that do not contain cm. By default all packages that start with Wm will be ignored
when no package name indication is provided..

 -pn or -PACKAGE_NAMES

> Documentation will only be generated for the packages whose names are provided. Will be ignored when option 'PACKAGE_NAME_REGEX' is also provided.
All packages in the package directory will be used by default when no package name indication is provided.

 -fqnr or -FOLDER_QUALIFIED_NAME_REGEX

> Documentation will only be generated for components whose fully qualified folder names match the provided regular expression. Documentation will be generated for
all encountered components by default when no folder name indication is provided.

 -ix or -INTERMEDIATE_XML

> Set option if you want the intermediate xml to be written to the output directory during the generation process. 
> **By default the intermediate xml** is only held in memory and **not written to file.**

 -pdf or --GENERATE_PDF

> Set option if you want the pdf documentation to be written to the output directory during the generation process. By
> *default* it *will* be generated if no other generation arguments are given.

 -html or --GENERATE_HTML

> Set option if you want the html documentation to be written to the output directory during the generation process. 
> *By default* it *won't* be generated.

 -md  or --GENERATE_MARKDOWN
> Set option if you want the markdown documentation to be written to the output directory during the generation process.
> *By default* it *won't be* generated.

 -txt or --GENERATE_TXT

> Set option if you want the text documentation to be written to the output directory during the generation process.
> *By default* it *won't* be generated.

 -xml or --GENERATE_XML

> Set option if you want the xml documentation to be written to the output directory during the generation process.
> *By default* it *won't* be generated.

## Examples
*Basic*
```sh
java -jar documentGenerator-1.0.0-jar-with-dependencies.jar
-pd C:\SoftwareAG\IntegrationServer\instances\default\packages
-od C:\SoftwareAG\IntegrationServer\instances\default\packages
-pdf -html -txt -xml
```
Will generate pdf, xml, txt and xml documentation for all packages in the default IS instance and save the generated document in the same folder.

*Using external xslt files*
```sh
java -jar documentGenerator-1.0.0-jar-with-dependencies.jar
-pd C:\SoftwareAG\IntegrationServer\instances\default\packages
-pdf -html -txt -xml
-xd C:\SoftwareAG\IntegrationServer\instances\default\xslt
```
Will generate the documentation like above but will use your own custom xslt files.
The _-xd_ option is the directory with the xslt files in them.
The files that will be looked for are:
- **Html:** IntermediateV1ToHtml.xslt
- **Markdown:** IntermediateV1ToMarkdown.xslt
- **Pdf:** IntermediateV1ToPdf.xslt
- **Text:** IntermediateV1ToText.xslt
- **Xml:** IntermediateV1ToXml.xslt

## Run as Maven Plugin
- It is possible to use the document generator as a maven plugin.
In your maven pom file include the dependency:
```xml
<dependency>
	<groupId>i8c-sag-devops</groupId>
	<artifactId>documentGenerator-lib</artifactId>
	<version>1.0.0</version>
    <classifier>jar-with-dependencies</classifier>
</dependency>
```
 
- And add the plugin:
```xml
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>exec-maven-plugin</artifactId>
	<version>1.5.0</version>
	<executions>
		<execution>
			<goals>
				<goal>java</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<mainClass>be.i8c.sag.documentationgenerator.cli.GeneratorCmd</mainClass>
		<arguments>
			<argument>-PACKAGE_DIRECTORY=Your/Packages/Directory</argument>
		</arguments>
	</configuration>
</plugin>
```
 
- A mandatory argument is that you should replace the **PACKAGE_DIRECTORY**
argument with the path to your package directory.
More optional arguments can be used,
their functions are described in the option list below.



# License

This project is licensed under the terms of the Apache 2.0 license which can be found [here](LICENSE).

# Contribute & report issues
If you wish to contribute, feel free to fork the code and send us a merge request.  
If you run into problems, please create an issue with a detailed report about the problem that occured, together with your contact details.  
