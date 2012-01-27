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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

/**
 */
@Entity
@Table(name = "Package")
public class Packages {

	@Id
	@GeneratedValue
	private Integer id;

	private Date dateLeft;

	private Date dateReceived;

	private char modified;

	private String packageId;
	
	private String drugTypes;

	@OneToMany
	@JoinColumn(name = "parentPackage")
	@IndexColumn(name = "packageddrugsindex")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		private List<PackagedDrugs> packagedDrugs;

	@ManyToOne
	@JoinColumn(name = "clinic")
	private Clinic clinic;

	private Date packDate;

	private Date pickupDate;

	/**
	 * Packages representing destroyed stock have null prescription
	 */
	@ManyToOne
	@JoinColumn(name = "prescription", nullable = true)
	private Prescription prescription;

	private int weekssupply;

	private Date dateReturned;

	private boolean stockReturned;

	private boolean packageReturned;

	private String reasonForPackageReturn;

	@OneToMany(mappedBy = "withPackage")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		private Set<AccumulatedDrugs> accumulatedDrugs;

	@OneToMany(mappedBy = "previousPackage")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		private Set<PillCount> pillCounts; // pillcounts on return

	@Transient
	private HashSet<Drug> arvDrugSet;

	/**
	 * Constructor for Packages.
	 * 
	 * @param dateLeft
	 *            Date
	 * @param dateReceived
	 *            Date
	 * @param modified
	 *            char
	 * @param packageId
	 *            String
	 * @param packagedDrugs
	 *            List<PackagedDrugs>
	 * @param packDate
	 *            Date
	 * @param pickupDate
	 *            Date
	 * @param prescription
	 *            Prescription
	 * @param weekssupply
	 *            int
	 * @param pillCounts
	 *            Set<PillCount>
	 * @param accumulatedDrugs
	 *            Set<AccumulatedDrugs>
	 */
	public Packages(Date dateLeft, Date dateReceived, char modified,
			String packageId, List<PackagedDrugs> packagedDrugs, Date packDate,
			Date pickupDate, Prescription prescription, int weekssupply,
			Set<PillCount> pillCounts, Set<AccumulatedDrugs> accumulatedDrugs,
			Clinic clinic) {
		this.dateLeft = dateLeft;
		this.dateReceived = dateReceived;
		this.modified = modified;
		this.packageId = packageId;
		this.packagedDrugs = packagedDrugs;
		this.packDate = packDate;
		this.pickupDate = pickupDate;
		this.prescription = prescription;
		this.weekssupply = weekssupply;
		this.accumulatedDrugs = accumulatedDrugs;
		this.clinic = clinic;
	}

	public Packages() {
		super();
	}

	/**
	 * Method getDateLeft.
	 * 
	 * @return Date
	 */
	public Date getDateLeft() {
		return dateLeft;
	}

	/**
	 * Method getDateReceived.
	 * 
	 * @return Date
	 */
	public Date getDateReceived() {
		return dateReceived;
	}

	/**
	 * Method getId.
	 * 
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getModified.
	 * 
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method getPackageId.
	 * 
	 * @return String
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * Method getPackagedDrugs.
	 * 
	 * @return List<PackagedDrugs>
	 */
	public List<PackagedDrugs> getPackagedDrugs() {
		if (packagedDrugs == null) {
			packagedDrugs = new ArrayList<PackagedDrugs>();
		}
		return packagedDrugs;
	}

	/**
	 * Method getPackDate.
	 * 
	 * @return Date
	 */
	public Date getPackDate() {
		return packDate;
	}

	/**
	 * Method getPickupDate.
	 * 
	 * @return Date
	 */
	public Date getPickupDate() {
		return pickupDate;
	}

	/**
	 * Method getPrescription.
	 * 
	 * @return Prescription
	 */
	public Prescription getPrescription() {
		return prescription;
	}

	/**
	 * Method setDateLeft.
	 * 
	 * @param dateLeft
	 *            Date
	 */
	public void setDateLeft(Date dateLeft) {
		this.dateLeft = dateLeft;
	}

	/**
	 * Method setDateReceived.
	 * 
	 * @param dateReceived
	 *            Date
	 */
	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	/**
	 * Method setId.
	 * 
	 * @param id
	 *            int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setModified.
	 * 
	 * @param modified
	 *            char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setPackageId.
	 * 
	 * @param packageId
	 *            String
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	/**
	 * Method setPackagedDrugs.
	 * 
	 * @param packagedDrugs
	 *            List<PackagedDrugs>
	 */
	public void setPackagedDrugs(List<PackagedDrugs> packagedDrugs) {
		this.packagedDrugs = packagedDrugs;
	}

	/**
	 * Method setPackDate.
	 * 
	 * @param packDate
	 *            Date
	 */
	public void setPackDate(Date packDate) {
		this.packDate = packDate;
	}

	/**
	 * Method setPickupDate.
	 * 
	 * @param pickupDate
	 *            Date
	 */
	public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
	}

	/**
	 * Method setPrescription.
	 * 
	 * @param prescription
	 *            Prescription
	 */
	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	/**
	 * Method getWeekssupply.
	 * 
	 * @return int
	 */
	public int getWeekssupply() {
		return weekssupply;
	}

	/**
	 * Method setWeekssupply.
	 * 
	 * @param weekssupply
	 *            int
	 */
	public void setWeekssupply(int weekssupply) {
		this.weekssupply = weekssupply;
	}

	/**
	 * Method getAccumulatedDrugs.
	 * 
	 * @return Set<AccumulatedDrugs>
	 */
	public Set<AccumulatedDrugs> getAccumulatedDrugs() {
		if (accumulatedDrugs == null) {
			accumulatedDrugs = new HashSet<AccumulatedDrugs>();
		}
		return accumulatedDrugs;
	}

	/**
	 * Method setAccumulatedDrugs.
	 * 
	 * @param accumulatedDrugs
	 *            Set<AccumulatedDrugs>
	 */
	public void setAccumulatedDrugs(Set<AccumulatedDrugs> accumulatedDrugs) {
		this.accumulatedDrugs = accumulatedDrugs;
	}

	/**
	 * Method getPillCounts.
	 * 
	 * @return Set<PillCount>
	 */
	public Set<PillCount> getPillCounts() {
		if (pillCounts == null) {
			pillCounts = new HashSet<PillCount>();
		}
		return pillCounts;
	}

	/**
	 * Method setPillCounts.
	 * 
	 * @param pillCounts
	 *            Set<PillCount>
	 */
	public void setPillCounts(Set<PillCount> pillCounts) {
		this.pillCounts = pillCounts;
	}

	/**
	 * @return the dateReturned
	 */
	public Date getDateReturned() {
		return dateReturned;
	}

	/**
	 * @param dateReturned
	 *            the dateReturned to set
	 */
	public void setDateReturned(Date dateReturned) {
		this.dateReturned = dateReturned;
	}

	/**
	 * @return the packageReturned
	 */
	public boolean isPackageReturned() {
		return packageReturned;
	}

	/**
	 * @param packageReturned
	 *            the packageReturned to set
	 */
	public void setPackageReturned(boolean packageReturned) {
		this.packageReturned = packageReturned;
	}

	/**
	 * @return the stockReturned
	 */
	public boolean isStockReturned() {
		return stockReturned;
	}

	/**
	 * @param stockReturned
	 *            the stockReturned to set
	 */
	public void setStockReturned(boolean stockReturned) {
		this.stockReturned = stockReturned;
	}

	/**
	 * @return the reasonForStockReturn
	 */
	public String getReasonForStockReturn() {
		return reasonForPackageReturn;
	}

	/**
	 * @param reasonForStockReturn
	 *            the reasonForStockReturn to set
	 */
	public void setReasonForStockReturn(String reasonForStockReturn) {
		this.reasonForPackageReturn = reasonForStockReturn;
	}

	/**
	 * Method hasARVDrug.
	 * 
	 * @return boolean
	 */
	public boolean hasARVDrug() {
		for (PackagedDrugs pd : getPackagedDrugs()) {
			if (pd.getStock().getDrug().getSideTreatment() == 'F')
				return true;
		}

		return false;
	}

	/**
	 * @return the clinic
	 */
	public Clinic getClinic() {
		return clinic;
	}

	/**
	 * @param clinic
	 *            the clinic to set
	 */
	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Set<Drug> getARVDrugSet() {
		if (arvDrugSet == null) {
			arvDrugSet = new HashSet<Drug>();
		}
		for (PackagedDrugs pd : packagedDrugs) {
			Drug theDrug = pd.getStock().getDrug();
			if (theDrug.isARV()) {
				arvDrugSet.add(theDrug);
			}
		}
		return arvDrugSet;
	}

	/**
	 * @return the drugTypes
	 */
	public String getDrugTypes() {
		return drugTypes;
	}

	/**
	 * @param drugTypes the drugTypes to set
	 */
	public void setDrugTypes(String drugTypes) {
		this.drugTypes = drugTypes;
	}
	
	public int getNextIssueNo() {
		if(packageId == null || "".equalsIgnoreCase(packageId)) {
			return 1;
		}
		else {
			int months;
			if(weekssupply <= 4) {
				months = 1;
			}
			else {
				months = weekssupply / 4;
			}
		///	char c = packageId.charAt(packageId.length() - 1);
			return (Character.getNumericValue(packageId.charAt(packageId.length() - 1)) + months );
		}
	}
	
	
}
