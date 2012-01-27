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
 * This class represents the bars, spaces and caption (text) that make up a
 * fully encoded barcode. The {@link BarcodeStrategy#encode encode} method of
 * {@link BarcodeStrategy} returns an instance of this class.
 * 
 */
public class EncodedBarcode implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 551560882624252471L;

	/** The bars and spaces in the barcode */
	public BarcodeElement[] elements;

	/** The text caption that is displayed underneath the barcode */
	public String barcodeLabelText;

	/** Initializing constructor * @param elements BarcodeElement[]
	 * @param barcodeLabelText String
	 * @param elements BarcodeElement[]
	 * @param barcodeLabelText String
	 */
	public EncodedBarcode(BarcodeElement[] elements, String barcodeLabelText) {
		this.elements = elements;
		this.barcodeLabelText = barcodeLabelText;
	}
}
