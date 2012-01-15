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

/*
 * Class from jbarcodebean, http://jbarcodebean.sourceforge.net/
 */
package org.celllife.idart.print.barcode;

/**
 * Class representing a single barcode module (bar or space).
 * 
 */
public class BarcodeElement implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4585356693187173559L;

	/**
	 * The width of the element expressed as a multiple of the narrowest module
	 * (bar/space) width.
	 */
	public byte width;

	/**
	 * <tt>true</tt> = bar (ResourceUtils.getColor(iDartColor.BLACK)),
	 * <tt>false</tt> = space (ResourceUtils.getColor(iDartColor.WHITE)).
	 */
	public boolean bar;
}
