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

package org.celllife.idart.gui.search;

// Send questions, comments, bug reports, etc. to the authors:

// Rob Warner (rwarner@interspatial.com)
// Robert Harris (rbrt_harris@yahoo.com)

/**
 * This class represents a SearchEntry.
 */
public class SearchEntry {

	private String columnOneName;

	private String columnTwoName;

	/**
	 * Constructs a SearchEntry
	 * 
	 * @param colOneName String
	 * @param colTwoName String
	 */
	public SearchEntry(String colOneName, String colTwoName) {
		this.columnOneName = colOneName;
		this.columnTwoName = colTwoName;
	}

	/**
	 * Gets the columnOneName
	 * 
	 * @return String
	 */
	public String getColumnOneName() {
		return columnOneName;
	}

	/**
	 * Gets the columnTwoName
	 * 
	 * @return String
	 */
	public String getColumnTwoName() {
		return columnTwoName;
	}
}
