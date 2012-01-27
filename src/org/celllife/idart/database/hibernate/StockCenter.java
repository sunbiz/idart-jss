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

/**
 * 
 */
package org.celllife.idart.database.hibernate;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
@Entity
public class StockCenter {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(unique = true, nullable = false)
	private String stockCenterName;
	
	private boolean preferred;
	
	@OneToMany(mappedBy = "stockCenter")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<Stock> stock;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public StockCenter(String stockCenterName, Set<Stock> stock) {

		this.stockCenterName = stockCenterName;
		this.stock = stock;
	}

	public StockCenter() {
		super();
	}

	public Set<Stock> getStock() {
		return stock;
	}

	public void setStock(Set<Stock> stock) {
		this.stock = stock;
	}

	/**
	 * @return the stockCenterName
	 */
	public String getStockCenterName() {
		return stockCenterName;
	}

	/**
	 * @param stockCenterName the stockCenterName to set
	 */
	public void setStockCenterName(String stockCenterName) {
		this.stockCenterName = stockCenterName;
	}

	/**
	 * @return the preferred
	 */
	public boolean isPreferred() {
		return preferred;
	}

	/**
	 * @param preferred the preferred to set
	 */
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	
}
