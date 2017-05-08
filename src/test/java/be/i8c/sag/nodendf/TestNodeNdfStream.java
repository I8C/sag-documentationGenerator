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
package be.i8c.sag.nodendf;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Test class for the NodeNdfStream class
 *
 * @see NodeNdfStream
 */
public class TestNodeNdfStream {

    /**
     * Test if the name gets returned correctly
     */
    @Test
    public void testGetName(){
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[32]);
        NodeNdfStream nodeNdfStream = new NodeNdfStream(stream, "MyName", "my.package");
        Assert.assertTrue(nodeNdfStream.getName().equals("MyName"));
    }

    /**
     * Test if the prefix gets returned correctly
     */
    @Test
    public void testGetQualifiedPrefix(){
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[32]);
        NodeNdfStream nodeNdfStream = new NodeNdfStream(stream, "MyName", "my.package");
        Assert.assertTrue(nodeNdfStream.getQualifiedPrefix().equals("my.package"));
    }

    /**
     * Test if the stream gets returned correctly
     */
    @Test
    public void testGetStream(){
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[32]);
        NodeNdfStream nodeNdfStream = new NodeNdfStream(stream, "MyName", "my.package");
        Assert.assertEquals(stream, nodeNdfStream.getStream());
    }

    /**
     * Test if the compare methods work correctly
     */
    @Test
    public void testCompareGreaterThan() throws Exception {
        Assert.assertTrue(
                new NodeNdfStream(null, "Test", "bb.test.package")
                        .compareTo(new NodeNdfStream(null, "Test", "aa.test.package")) > 0);

        Assert.assertTrue(
                new NodeNdfStream(null, "TestB", "test.package")
                        .compareTo(new NodeNdfStream(null, "TestA", "test.package")) > 0);
    }

    /**
     * Test if the compare methods work correctly
     */
    @Test
    public void testCompareLesserThan() throws Exception {
        Assert.assertTrue(
                new NodeNdfStream(null, "Test", "aa.test.package")
                        .compareTo(new NodeNdfStream(null, "Test", "bb.test.package")) < 0);

        Assert.assertTrue(
                new NodeNdfStream(null, "TestA", "test.package")
                        .compareTo(new NodeNdfStream(null, "TestB", "test.package")) < 0);
    }
}
