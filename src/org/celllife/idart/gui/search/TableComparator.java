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

import java.util.Comparator;

/**
 * This class does the comparisons for sorting SearchEntry objects.
 */
public class TableComparator implements Comparator<SearchEntry> {

	/** Constant for First Name column */
	public static final int COL1_NAME = 0;

	/** Constant for Last Name column */
	public static final int COL2_NAME = 1;

	/** Constant for ascending */
	public static final int ASCENDING = 0;

	/** Constant for descending */
	public static final int DESCENDING = 1;

	private int column;

	private int direction;

	/**
	 * Compares two Player objects
	 * 
	 * @param p1 SearchEntry
	 * @param p2 SearchEntry
	 * @return int
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(SearchEntry p1, SearchEntry p2) {
		int rc = 0;

		// Determine which field to sort on, then sort
		// on that field
		switch (column) {
		case COL1_NAME:
			rc = p1.getColumnOneName().toLowerCase().compareTo(
					p2.getColumnOneName().toLowerCase());
			break;
		case COL2_NAME:
			rc = p1.getColumnTwoName().toLowerCase().compareTo(
					p2.getColumnTwoName().toLowerCase());
			break;
		}

		// Check the direction for sort and flip the sign
		// if appropriate
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

	/**
	 * Sets the column for sorting
	 * 
	 * @param column
	 *            the column
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * Sets the direction for sorting
	 * 
	 * @param direction
	 *            the direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Method getDirection.
	 * @return int
	 */
	public int getDirection() {
		return this.direction;
	}

	/**
	 * Reverses the direction
	 */
	public void reverseDirection() {
		direction = 1 - direction;
	}
}
