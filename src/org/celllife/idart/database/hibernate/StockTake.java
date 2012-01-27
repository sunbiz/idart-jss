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

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
@Entity
public class StockTake {

	@Id
	@GeneratedValue
	private Integer id;

	private String stockTakeNumber;

	private Date startDate;

	private Date endDate;

	@OneToMany(mappedBy = "stockTake")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<StockAdjustment> adjustments;

	private boolean open;

	/**
	 * Method getAdjustments.
	 * @return Set<StockAdjustment>
	 */
	public Set<StockAdjustment> getAdjustments() {
		return adjustments;
	}

	/**
	 * Method setAdjustments.
	 * @param adjustments Set<StockAdjustment>
	 */
	public void setAdjustments(Set<StockAdjustment> adjustments) {
		this.adjustments = adjustments;
	}

	/**
	 * Method getEndDate.
	 * @return Date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Method setEndDate.
	 * @param endDate Date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method getStartDate.
	 * @return Date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Method setStartDate.
	 * @param startDate Date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Method getStockTakeNumber.
	 * @return String
	 */
	public String getStockTakeNumber() {
		return stockTakeNumber;
	}

	/**
	 * Method setStockTakeNumber.
	 * @param stockTakeNumber String
	 */
	public void setStockTakeNumber(String stockTakeNumber) {
		this.stockTakeNumber = stockTakeNumber;
	}

	/**
	 * Method isOpen.
	 * @return boolean
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Method setOpen.
	 * @param open boolean
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

}
