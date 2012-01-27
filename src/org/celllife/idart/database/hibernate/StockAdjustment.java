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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class StockAdjustment {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "stock")
	private Stock stock;

	private Date captureDate;

	private int stockCount;

	private int adjustedValue;

	private String notes;

	@ManyToOne
	@JoinColumn(name = "stockTake")
	private StockTake stockTake;

	public StockAdjustment() {
		super();

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
	 * Method getStockCount.
	 * @return int
	 */
	public int getStockCount() {
		return stockCount;
	}

	/**
	 * Method setStockCount.
	 * @param stockCount int
	 */
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}

	/**
	 * Method getCaptureDate.
	 * @return Date
	 */
	public Date getCaptureDate() {
		return captureDate;
	}

	/**
	 * Method setCaptureDate.
	 * @param captureDate Date
	 */
	public void setCaptureDate(Date captureDate) {
		this.captureDate = captureDate;
	}

	/**
	 * Method getStock.
	 * @return Stock
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * Method setStock.
	 * @param stock Stock
	 */
	public void setStock(Stock stock) {
		this.stock = stock;
	}

	/**
	 * Method getNotes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Method setNotes.
	 * @param notes String
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Method getStockTake.
	 * @return StockTake
	 */
	public StockTake getStockTake() {
		return stockTake;
	}

	/**
	 * Method setStockTake.
	 * @param stockTake StockTake
	 */
	public void setStockTake(StockTake stockTake) {
		this.stockTake = stockTake;
	}

	/**
	 * Method getAdjustedValue.
	 * @return int
	 */
	public int getAdjustedValue() {
		return adjustedValue;
	}

	/**
	 * Method setAdjustedValue.
	 * @param adjustedValue int
	 */
	public void setAdjustedValue(int adjustedValue) {
		this.adjustedValue = adjustedValue;
	}

}
