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

import java.io.InputStream;
import java.util.Comparator;

/**
 * This class represents the node.idf file in a package.
 */
public class NodeNdfStream implements Comparable<NodeNdfStream>, Comparator<NodeNdfStream>{

	/** The input stream of the file */
	private InputStream stream;
	/** The name of the node */
	private String name;
	/** The path or prefix of the node */
	private String qualifiedPrefix;
	
	public NodeNdfStream(InputStream stream, String name, String qualifiedPrefix) {
		this.stream = stream;
		this.name = name;
		this.qualifiedPrefix = qualifiedPrefix;
	}

	/**
	 * Get the name of this node
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the Qualified Prefix of this node
	 *
	 * @return The Qualified Prefix
	 */
	public String getQualifiedPrefix() {
		return qualifiedPrefix;
	}

	/**
	 * Compares its two arguments for order.  Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal
	 * to, or greater than the second.
	 *
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the
	 *         first argument is less than, equal to, or greater than the
	 *         second.
	 * @throws NullPointerException if an argument is null and this
	 *         comparator does not permit null arguments
	 */
	@Override
	public int compare(NodeNdfStream o1, NodeNdfStream o2) {
		return ( (o1.getQualifiedPrefix()+o1.getName()).compareTo( o2.getQualifiedPrefix()+o2.getName()));
	}

	/**
	 * Get the input stream to read this node
	 *
	 * @return The input stream for this node
     */
	public InputStream getStream() {
		return stream;
	}

	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @param   o the object to be compared.
	 * @return  a negative integer, zero, or a positive integer as this object
	 *          is less than, equal to, or greater than the specified object.
	 * @throws NullPointerException if the specified object is null
     */
	@Override
	public int compareTo(NodeNdfStream o) {
		return compare(this, o);
	}
}
