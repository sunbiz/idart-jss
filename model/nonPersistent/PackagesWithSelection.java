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

package model.nonPersistent;

import org.celllife.idart.database.hibernate.Packages;

public class PackagesWithSelection {
	
	private Packages packages;
	
	private boolean isSelected;

	/**
	 * Default Constructor
	 */
	public PackagesWithSelection() {
		super();
	}

	/**
	 * @param packages
	 * @param isSelected
	 */
	public PackagesWithSelection(Packages packages, boolean isSelected) {
		super();
		this.packages = packages;
		this.isSelected = isSelected;
	}

	/**
	 * @return the packages
	 */
	public Packages getPackages() {
		return packages;
	}

	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Packages packages) {
		this.packages = packages;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	

}
