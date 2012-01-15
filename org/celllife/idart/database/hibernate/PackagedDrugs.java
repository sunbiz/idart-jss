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

package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class PackagedDrugs {

	@Id
	@GeneratedValue
	private Integer id;

	private int amount;

	@ManyToOne
	@JoinColumn(name = "parentPackage")
	private Packages parentPackage;

	@ManyToOne
	@JoinColumn(name = "stock")
	private Stock stock;

	private char modified;

	public PackagedDrugs() {
		super();
	}

	/***************************************************************************
	 * 
	 * @param amount
	 * @param parentPackage
	 * @param stock
	 * @param modified
	 */
	public PackagedDrugs(int amount, Packages parentPackage, Stock stock,
			char modified) {

		super();
		this.amount = amount;
		this.parentPackage = parentPackage;
		this.stock = stock;
		this.modified = modified;

	}

	/**
	 * Method getAmount.
	 * @return int
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Method getStock.
	 * @return Stock
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getParentPackage.
	 * @return Packages
	 */
	public Packages getParentPackage() {
		return parentPackage;
	}

	/**
	 * Method getModified.
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method setModified.
	 * @param modified char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setAmount.
	 * @param amount int
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Method setStock.
	 * @param stock Stock
	 */
	public void setStock(Stock stock) {
		this.stock = stock;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setParentPackage.
	 * @param parentPackage Packages
	 */
	public void setParentPackage(Packages parentPackage) {
		this.parentPackage = parentPackage;
	}

}
