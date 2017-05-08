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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestingUtils {
    private static final File EXTERNAL_RESOURCE_DIR = new File("src/test/external-resources");
    private static final File PACKAGE_DIR = new File(EXTERNAL_RESOURCE_DIR, "packages");
    private static final File OUTPUT_DIR = new File("test-output");

    /**
     * Get the directory where the external resources
     * are located for testing
     *
     * @return The directory where the external resources are located
     */
    public static File getExternalResourceDir() {
        return EXTERNAL_RESOURCE_DIR;
    }

    /**
     * Get the directory where the packages
     * are located for testing
     *
     * @return The directory where the packages are located
     */
    public static File getPackageDir() {
        return PACKAGE_DIR;
    }

    /**
     * Get the directory where any files regarding testing should be written to
     *
     * @return The output directory
     */
    public static File getOutputDir() {
        OUTPUT_DIR.mkdirs();
        return OUTPUT_DIR;
    }

    /**
     * Remove the content of the output directory and remove it
     */
    public static void cleanOutput() {
        deleteDir(OUTPUT_DIR);
    }

    /**
     * Remove the content of the specified directory and remove it
     *
     * @param dir The directory that should be removed
     */
    public static void deleteDir(File dir) {
        File[] children = dir.listFiles();
        if (children == null || children.length == 0) {
            dir.delete();
        } else {
            for (File child : children)
                if (child.isDirectory())
                    deleteDir(child);
                else
                    child.delete();
            dir.delete();
        }
    }

    /**
     * Invoke a method via reflection
     *
     * @param objToInvoke The object you want to invoke
     * @param classToInvoke Which class owns the method
     * @param method The name of the method
     * @param argumentClasses The argument classes that should be passed to the method
     * @param arguments The arguments that should be passed to the method
     *
     * @return The return value of the method
     *
     * @throws NoSuchMethodException If the method name does not exist
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws IllegalAccessException When the method can not be accessed
     */
    public static Object invoke(Object objToInvoke, Class<?> classToInvoke, String method, Class[] argumentClasses, Object[] arguments) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = classToInvoke.getDeclaredMethod(method, argumentClasses);
        Object o;

        if (m.isAccessible())
            o = m.invoke(objToInvoke, arguments);
        else{
            m.setAccessible(true);
            o = m.invoke(objToInvoke, arguments);
            m.setAccessible(false);
        }

        return o;
    }
}
