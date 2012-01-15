/**
 * {iDart - Pharmacy dispensing tool for cronic diseases}
 * Copyright (C) 2006 Cell-Life
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 **/

package org.celllife.idart.misc;

/**
 * Created on 10/05/2007
 * 
 * Class that checks if text is valid integer. I write this class because the
 * methods in this class should be used more often to validate user input in the
 * gui's. For example if the user is expected to give the Quantity of pills
 * counted, you would only want integer inputs.
 * 
 */

public class FloatValidator {

	/**
	 * Default Constructor
	 */
	public FloatValidator() {
		super();
	}

	/**
	 * method to check if String is an integer
	 * 
	 * @param theString
	 *            the String to check
	 * @return true if the String is an integer else false
	 */
	public static boolean isFloat(String theString) {
		try {
			// First check if String contains no characters
			if (theString.length() == 0)
				return false;
			// First check for negative number
			if (theString.charAt(0) == '-') {
				// First character is a minus sign, we check if the String
				// contains any more characters.
				if (theString.length() == 1)
					return false;
				// if this statement is executed, it either results in an
				// exception or the String is parsed to an int
				Double.parseDouble(theString.substring(1));
			}
			// First character is not a minus sign, so we try to parse it
			else {
				Double.parseDouble(theString);
			}
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * method to check if String is a +ve integer
	 * 
	 * @param theString
	 *            the String to check
	 * @return true if the String is a +ve integer else false
	 */
	public static boolean isPositiveFloat(String theString) {
		try {
			if (!isFloat(theString))
				return false;
			else {
				if ("-".equalsIgnoreCase(theString.substring(0, 1)))
					return false;
				else
					return true;
			}

		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * method to check if String is a -ve integer
	 * 
	 * @return true if the String is a -ve integer else false
	 */
	public static boolean isNegativeFloat() {
		try {

		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}