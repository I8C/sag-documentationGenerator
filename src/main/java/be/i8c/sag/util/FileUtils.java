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

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Misc file utilities
 */
public class FileUtils {
    /** The internal directory which contains assets that may need to be extracted */
    public static final String ASSETS_FOLDER_NAME = "assets";

    /**
     * Receives the fop.xconf stream and puts it in a temporary file.
     *
     * @param stream The stream tat will be written to the temporary file
     * @return Returns temporary file with the content of the stream
     * @throws IOException when the file couldn't be created
     */
    public static File createTempFopConf(InputStream stream) throws IOException {
        File temp;
        temp = File.createTempFile("fop", ".xconf");
        temp.deleteOnExit();
        FileOutputStream out = new FileOutputStream(temp);
        IOUtils.copy(stream, out);
        return temp;
    }

    /**
     * Get the File that represents the location of this jar.
     * 
     * Note: The file may be a directory when you are running this
     * code from your IDE or without packing it into a jar.
     *
     * @return A File that represents the location of this jar.
     */
    public static File getJarFile()
    {
        return getJarFile(FileUtils.class);
    }


    /**
     * Get the File that represents the location of this jar that contains the clazz.
     *
     * Note: The file may be a directory when you are running this
     * code from your IDE or without packing it into a jar.
     *
     * @param clazz A class that is located in the jar file.
     * @return A File that represents the location of the jar that contains the clazz.
     */
    public static File getJarFile(Class<?> clazz)
    {
        String className = clazz.getSimpleName() + ".class";
        String classPath = "/" + clazz.getName().replace('.', '/') + ".class";

        URL generatorFileUrl = clazz.getResource(className);

        // We want to remove the last instance of the internal class path
        String jarPath = new StringBuilder(
            new StringBuilder(generatorFileUrl.getPath())
                .reverse().toString()
                .replaceFirst(new StringBuilder(classPath).reverse().toString(), ""))
            .reverse().toString();

        // Remove the file:/ and the '!' at the end of the path.
        // This only needs to be done if it's a file
        jarPath = jarPath.replaceAll("^file:/", "");
        jarPath = jarPath.replaceAll("!$", "");

        return new File(jarPath);
    }

    /**
     * Extract all files from the assets folder in the jar to the destinationDir.
     *
     * @param destinationDir The output directory
     * @throws IOException When failed to write to a file
     * @throws FileNotFoundException If a file could not be found
     */
    public static void extractAssets(File destinationDir) throws IOException {
        extractAssets(destinationDir, false);
    }

    /**
     * Extract all files from the assets folder in the jar to the destinationDir.
     * If deleteOnExit is true, all the files will be removed once the
     * application has closed.
     *
     * @param destinationDir The output directory
     * @param deleteOnExit If true, delete the files once the application has closed.
     * @throws IOException When failed to write to a file
     * @throws FileNotFoundException If a file could not be found
     */
    public static void extractAssets(File destinationDir, boolean deleteOnExit) throws IOException {
        // Find where this jar is located
        File jarFile = getJarFile();

        // Attempt to dynamically load in all files in the folder of the assets dir
        // Extract files of the code source when it's packed in a jar
        if (jarFile.isFile()) {
            extractDirectory(ASSETS_FOLDER_NAME, jarFile, destinationDir, deleteOnExit);
        }
        // Extract files of the code source when it's not packed in a jar
        // Mainly used during development
        else if (jarFile.isDirectory()) {
            File extractingDir = new File(jarFile, ASSETS_FOLDER_NAME);
            copyDirectory(extractingDir, destinationDir, deleteOnExit);
        }
    }

    /**
     * Extract files from a zipped (and jar) file
     *
     * @param internalDir The directory you want to copy
     * @param zipFile The file that contains the content you want to copy
     * @param to The directory you want to copy to
     * @param deleteOnExit If true, delete the files once the application has closed.
     * @throws IOException When failed to write to a file
     * @throws FileNotFoundException If a file could not be found
     */
    public static void extractDirectory(String internalDir, File zipFile, File to, boolean deleteOnExit) throws IOException
    {
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile))) {
            while (true) {
                ZipEntry zipEntry = zip.getNextEntry();
                if (zipEntry == null)
                    break;

                if (zipEntry.getName().startsWith(internalDir) && !zipEntry.isDirectory()) {
                    File f = createFile(new File(to, zipEntry.getName().replace((internalDir.endsWith("/") ? internalDir : internalDir + "/"), "")));
                    if (deleteOnExit)
                        f.deleteOnExit();
                    OutputStream bos = new FileOutputStream(f);
                    try{
                        IOUtils.copy(zip, bos);
                    }finally {
                        bos.flush();
                        bos.close();
                    }
                }

                zip.closeEntry();
            }
        }
    }

    /**
     * Copy a directory and it's content
     *
     * @param from The directory to copy
     * @param to Where to copy it to
     * @param deleteOnExit If true, delete the files once the application has closed.
     * @throws IOException When failed to write to a file
     * @throws FileNotFoundException If a file could not be found
     */
    public static void copyDirectory(File from, File to, boolean deleteOnExit) throws IOException
    {
        File[] children = from.listFiles();
        if (children != null) {
            for (File child : children) {
                // Extract the file
                InputStream in = new FileInputStream(child);
                File outputFile = createFile(new File(to, child.getName()));
                if (deleteOnExit)
                    outputFile.deleteOnExit();

                OutputStream out = new FileOutputStream(outputFile);
                try {
                    IOUtils.copy(in, out);
                } finally {
                    out.flush();
                    in.close();
                    out.close();
                }
            }
        }
    }

    /**
     * Creates the file if it doesn't exist yet
     *
     * @param file The file to create
     * @return The same file that was passed
     * @throws IOException When failed to write to a file
     */
    public static File createFile(File file) throws IOException {
        if (!file.exists())
        {
            if (file.getParentFile() != null)
                file.getParentFile().mkdirs();
            file.createNewFile();
        }

        return file;
    }
}
