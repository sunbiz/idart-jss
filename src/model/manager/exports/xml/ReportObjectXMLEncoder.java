/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package model.manager.exports.xml;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;

public class ReportObjectXMLEncoder {

	private final Logger log = Logger.getLogger(this.getClass());

	private Object objectToEncode;

	public ReportObjectXMLEncoder(Object objectToEncode) {
		this.objectToEncode = objectToEncode;
	}

	public String toXmlString() {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		// EnumDelegate enumDelegate = new EnumDelegate();

		XMLEncoder enc = new XMLEncoder(new BufferedOutputStream(arr));
		// enc.setPersistenceDelegate(SimpleColumnsEnum.class, enumDelegate);

		log.debug("objectToEncode.type: " + objectToEncode.getClass());
		enc.writeObject(this.objectToEncode);
		enc.close();

		return arr.toString();
	}

	/**
	 * @return Returns the objectToEncode.
	 */
	public Object getObjectToEncode() {
		return objectToEncode;
	}

	/**
	 * @param objectToEncode
	 *            The objectToEncode to set.
	 */
	public void setObjectToEncode(Object objectToEncode) {
		this.objectToEncode = objectToEncode;
	}
}
