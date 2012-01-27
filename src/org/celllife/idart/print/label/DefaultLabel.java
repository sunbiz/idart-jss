/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
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
 * 
 */

package org.celllife.idart.print.label;

import java.util.Vector;

/*
 * Objects of this class consist of the label to be printed, in Printable format
 * (for printers with a driver) and also in raw text format for printers without
 * a driver, eg. Zebra label printers on linux
 */
/**
 */
public interface DefaultLabel {

	/**
	 * Method getEPL2Commands.
	 * @return Vector<String>
	 */
	abstract Vector<String> getEPL2Commands();

	public static int ENGLISH = 0;

	public static int XHOSA = 1;

	public static int SOTHO = 2;

	public static int AFRIKAANS = 3;

}
