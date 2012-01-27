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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
/**
 * @author mel
 *
 */
@Entity
public class Stock {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "drug")
	private Drug drug;

	private String batchNumber;

	private Date dateReceived;

	@ManyToOne
	@JoinColumn(name = "stockCenter")
	private StockCenter stockCenter;

	private Date expiryDate;

	private char modified;

	@OneToMany
	@JoinColumn(name = "stock")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<PackagedDrugs> packagedDrugs;

	private String shelfNumber;

	private int unitsReceived;

	private String manufacturer;

	private char hasUnitsRemaining;
	
	private BigDecimal unitPrice; 

	@OneToMany(mappedBy = "stock")
	private Set<StockAdjustment> stockAdjustments;

	
	public Stock() {
		super();
	}

	/**
	 * Method getDrug.
	 * @return Drug
	 */
	public Drug getDrug() {
		return drug;
	}

	/**
	 * Method getBatchNumber.
	 * @return String
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * Method getDateReceived.
	 * @return Date
	 */
	public Date getDateReceived() {
		return dateReceived;
	}

	

	/**
	 * Method getExpiryDate.
	 * @return Date
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getModified.
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method getPackagedDrugs.
	 * @return Set<PackagedDrugs>
	 */
	public Set<PackagedDrugs> getPackagedDrugs() {
		return packagedDrugs;
	}

	/**
	 * Method getShelfNumber.
	 * @return String
	 */
	public String getShelfNumber() {
		return shelfNumber;
	}

	/**
	 * Method getUnitsReceived.
	 * @return int
	 */
	public int getUnitsReceived() {
		return unitsReceived;
	}

	/**
	 * Method setDrug.
	 * @param drug Drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	/**
	 * Method setBatchNumber.
	 * @param batchNumber String
	 */
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * Method setDateReceived.
	 * @param dateReceived Date
	 */
	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	
	/**
	 * Method setExpiryDate.
	 * @param expiryDate Date
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method setModified.
	 * @param modified char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setPackagedDrugs.
	 * @param packagedDrugs Set<PackagedDrugs>
	 */
	public void setPackagedDrugs(Set<PackagedDrugs> packagedDrugs) {
		this.packagedDrugs = packagedDrugs;
	}

	/**
	 * Method setShelfNumber.
	 * @param shelfNumber String
	 */
	public void setShelfNumber(String shelfNumber) {
		this.shelfNumber = shelfNumber;
	}

	/**
	 * Method setUnitsReceived.
	 * @param unitsReceived int
	 */
	public void setUnitsReceived(int unitsReceived) {
		this.unitsReceived = unitsReceived;
	}

	/**
	 * Method getManufacturer.
	 * @return String
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * Method setManufacturer.
	 * @param manufacturer String
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * Method getHasUnitsRemaining.
	 * @return char
	 */
	public char getHasUnitsRemaining() {
		return hasUnitsRemaining;
	}

	/**
	 * Method setHasUnitsRemaining.
	 * @param hasUnitsRemaining char
	 */
	public void setHasUnitsRemaining(char hasUnitsRemaining) {
		this.hasUnitsRemaining = hasUnitsRemaining;
	}

	/**
	 * Method getStockAdjustments.
	 * @return Set<StockAdjustment>
	 */
	public Set<StockAdjustment> getStockAdjustments() {
		return stockAdjustments;
	}

	/**
	 * Method setStockAdjustments.
	 * @param stockAdjustments Set<StockAdjustment>
	 */
	public void setStockAdjustments(Set<StockAdjustment> stockAdjustments) {
		this.stockAdjustments = stockAdjustments;
	}

	public StockCenter getStockCenter() {
		return stockCenter;
	}

	public void setStockCenter(StockCenter stockCenter) {
		this.stockCenter = stockCenter;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

}
