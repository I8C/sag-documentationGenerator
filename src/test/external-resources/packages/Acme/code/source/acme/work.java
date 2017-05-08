/*******************************************************************************
 * Copyright 2016 i8c (Integr8 Consulting)
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
package acme;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2016-08-04 13:42:15 CEST
// -----( ON-HOST: 10.0.2.116

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class work

{
	// ---( internal utility methods )---

	final static work _instance = new work();

	static work _newInstance() { return new work(); }

	static work _cast(Object o) { return (work)o; }

	// ---( server methods )---




	public static final void endsWith (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(endsWith)>> ---
		// @sigtype java 3.5
		// [i] field:0:required string
		// [i] field:0:required suffix
		// [o] field:0:required isSuffix
		// pipeline reading
		IDataMap idm = new IDataMap(pipeline);
			String	string = idm.getAsString( "string" );
			String	suffix = idm.getAsString( "suffix" );
		//process the data
		String isSuffix = string.endsWith(suffix)? "true":"false";
		// pipeline writing
		idm.put( "isSuffix", isSuffix );
		// --- <<IS-END>> ---

                
	}
}

