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

package org.celllife.idart.database.hibernate.tmp;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.User;

/**
 */
@Entity
@Table(name = "packagedruginfotmp")
public class PackageDrugInfo {

	private String amountPerTime;

	@Id
	@GeneratedValue
	private int id;

	private String batchNumber;

	private String clinic;

	private int dispensedQty;

	private String qtyInHand;

	private String qtyInLastBatch;

	private String summaryQtyInHand;

	@ManyToOne(cascade = { CascadeType.REMOVE })
	@JoinColumn(name = "packagedDrug", nullable = false)
	private PackagedDrugs packagedDrug;

	private String formLanguage1;

	private String formLanguage2;

	private String formLanguage3;

	private String drugName;

	private Date expiryDate;

	private String notes;

	private String patientId;

	private String patientFirstName;

	private String patientLastName;

	private String specialInstructions1;

	private String specialInstructions2;

	private int stockId;

	private int timesPerDay;

	private int numberOfLabels;

	private boolean sideTreatment;

	@ManyToOne
	@JoinColumn(name = "cluser", nullable = false)
	private User cluser;

	private Date dispenseDate;
	
	private Date pickupDate;

	private int packageIndex; // eg. 2 if this is package 2 of 4

	private int weeksSupply;

	private boolean invalid = false;

	private int prescriptionDuration;

	private String dateExpectedString;

	private boolean sentToEkapa;

	private String packageId;

	private boolean dispensedForLaterPickup = false;

	private boolean firstBatchInPrintJob = true;

	/**
	 * Default Constructor
	 */
	public PackageDrugInfo() {
		super();
	}

	/**
	 * Constructor for PackageDrugInfo.
	 * 
	 * @param amountPerTime
	 *            String
	 * @param batchNumber
	 *            String
	 * @param clinic
	 *            String
	 * @param dispensedQty
	 *            int
	 * @param formLanguage1
	 *            String
	 * @param formLanguage2
	 *            String
	 * @paList<PackageDrugInfo> pdi) { this.drugListInfo = pdi;
	 *                          populateDrugs(drugListInfo);ram formLanguage3
	 *                          String
	 * @param drugName
	 *            String
	 * @param expiryDate
	 *            Date
	 * @param notes
	 *            String
	 * @param patientId
	 *            String
	 * @param patientFirstName
	 *            String
	 * @param patientLastName
	 *            String
	 * @param specialInstructions1
	 *            String
	 * @param specialInstructions2
	 *            String
	 * @param stockId
	 *            int
	 * @param timesPerDay
	 *            int
	 * @param numberOfLabels
	 *            int
	 * @param sideTreatment
	 *            boolean
	 * @param user
	 *            User
	 * @param dispenseDate
	 *            Date
	 * @param packageIndex
	 *            int
	 * @param weeksSupply
	 *            int
	 * @param packagedDrug
	 *            PackagedDrugs
	 * @param qtyInHand
	 *            String
	 * @param summaryQtyInHand
	 *            String
	 * @param qtyInLastBatch
	 *            String
	 * @param prescriptionDuration
	 *            String
	 * @param dateExpectedString
	 *            String
	 * @param packageId
	 *            String
	 */
	public PackageDrugInfo(String amountPerTime, String batchNumber,
			String clinic, int dispensedQty, String formLanguage1,
			String formLanguage2, String formLanguage3, String drugName,
			Date expiryDate, String notes, String patientId,
			String patientFirstName, String patientLastName,
			String specialInstructions1, String specialInstructions2,
			int stockId, int timesPerDay, int numberOfLabels,
			boolean sideTreatment, User user, Date dispenseDate,
			int packageIndex, int weeksSupply, PackagedDrugs packagedDrug,
			String qtyInHand, String summaryQtyInHand, String qtyInLastBatch,
			int prescriptionDuration, String dateExpectedString,
			String packageId) {
		super();
		this.amountPerTime = amountPerTime;
		this.batchNumber = batchNumber;
		this.clinic = clinic;
		this.dispensedQty = dispensedQty;
		this.formLanguage1 = formLanguage1;
		this.formLanguage2 = formLanguage2;
		this.formLanguage3 = formLanguage3;
		this.drugName = drugName;
		this.expiryDate = expiryDate;
		this.notes = notes;
		this.patientId = patientId;
		this.patientFirstName = patientFirstName;
		this.patientLastName = patientLastName;
		this.specialInstructions1 = specialInstructions1;
		this.specialInstructions2 = specialInstructions2;
		this.stockId = stockId;
		this.timesPerDay = timesPerDay;
		this.numberOfLabels = numberOfLabels;
		this.sideTreatment = sideTreatment;
		this.cluser = user;
		this.dispenseDate = dispenseDate;
		this.packageIndex = packageIndex;
		this.weeksSupply = weeksSupply;
		this.packagedDrug = packagedDrug;
		this.qtyInHand = qtyInHand;
		this.summaryQtyInHand = summaryQtyInHand;
		this.qtyInLastBatch = qtyInLastBatch;
		this.prescriptionDuration = prescriptionDuration;
		this.dateExpectedString = dateExpectedString;
		this.packageId = packageId;
		sentToEkapa = false;
	}

	/**
	 * Method getAmountPerTime.
	 * 
	 * @return String
	 */
	public String getAmountPerTime() {
		return amountPerTime;
	}

	/**
	 * Method getBatchNumber.
	 * 
	 * @return String
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * Method getClinic.
	 * 
	 * @return String
	 */
	public String getClinic() {
		return clinic;
	}

	/**
	 * Method getDispenseDate.
	 * 
	 * @return Date
	 */
	public Date getDispenseDate() {
		return dispenseDate;
	}

	/**
	 * Method getDispensedQty.
	 * 
	 * @return int
	 */
	public int getDispensedQty() {
		return dispensedQty;
	}

	/**
	 * Method getDrugName.
	 * 
	 * @return String
	 */
	public String getDrugName() {
		return drugName;
	}

	/**
	 * Method getExpiryDate.
	 * 
	 * @return Date
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Method getFormLanguage1.
	 * 
	 * @return String
	 */
	public String getFormLanguage1() {
		return formLanguage1;
	}

	/**
	 * Method getFormLanguage2.
	 * 
	 * @return String
	 */
	public String getFormLanguage2() {
		return formLanguage2;
	}

	/**
	 * Method getFormLanguage3.
	 * 
	 * @return String
	 */
	public String getFormLanguage3() {
		return formLanguage3;
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
	 * Method getNotes.
	 * 
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Method getNumberOfLabels.
	 * 
	 * @return int
	 */
	public int getNumberOfLabels() {
		return numberOfLabels;
	}

	/**
	 * Method getPackageIndex.
	 * 
	 * @return int
	 */
	public int getPackageIndex() {
		return packageIndex;
	}

	/**
	 * Method getPatientId.
	 * 
	 * @return String
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * Method getSpecialInstructions1.
	 * 
	 * @return String
	 */
	public String getSpecialInstructions1() {
		return specialInstructions1;
	}

	/**
	 * Method getSpecialInstructions2.
	 * 
	 * @return String
	 */
	public String getSpecialInstructions2() {
		return specialInstructions2;
	}

	/**
	 * Method getStockId.
	 * 
	 * @return int
	 */
	public int getStockId() {
		return stockId;
	}

	/**
	 * Method getTimesPerDay.
	 * 
	 * @return int
	 */
	public int getTimesPerDay() {
		return timesPerDay;
	}

	/**
	 * Method getCluser.
	 * 
	 * @return User
	 */
	public User getCluser() {
		return cluser;
	}

	/**
	 * Method getWeeksSupply.
	 * 
	 * @return int
	 */
	public int getWeeksSupply() {
		return weeksSupply;
	}

	/**
	 * Method isSideTreatment.
	 * 
	 * @return boolean
	 */
	public boolean isSideTreatment() {
		return sideTreatment;
	}

	/**
	 * Method setAmountPerTime.
	 * 
	 * @param amt
	 *            double
	 */
	public void setAmountPerTime(double amt) {
		if (new BigDecimal(amt).scale() == 0) {
			amountPerTime = "" + new BigDecimal(amt).unscaledValue().intValue();
		} else {
			amountPerTime = "" + amt;
		}
	}

	/**
	 * Method setAmountPerTime.
	 * 
	 * @param amountPerTime
	 *            String
	 */
	public void setAmountPerTime(String amountPerTime) {
		this.amountPerTime = amountPerTime;
	}

	/**
	 * Method setBarcode.
	 * 
	 * @param id
	 *            int
	 */
	public void setBarcode(int id) {
		this.id = id;
	}

	/**
	 * Method setBatchNumber.
	 * 
	 * @param batchNumber
	 *            String
	 */
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * Method setClinic.
	 * 
	 * @param clinic
	 *            String
	 */
	public void setClinic(String clinic) {
		this.clinic = clinic;
	}

	/**
	 * Method setDispenseDate.
	 * 
	 * @param dispenseDate
	 *            Date
	 */
	public void setDispenseDate(Date dispenseDate) {
		this.dispenseDate = dispenseDate;
	}

	/**
	 * Method setDispensedQty.
	 * 
	 * @param dispensedQty
	 *            int
	 */
	public void setDispensedQty(int dispensedQty) {
		this.dispensedQty = dispensedQty;
	}

	/**
	 * Method setDrugName.
	 * 
	 * @param drugName
	 *            String
	 */
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	/**
	 * Method setExpiryDate.
	 * 
	 * @param expiryDate
	 *            Date
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Method setFormLanguage1.
	 * 
	 * @param formLanguage1
	 *            String
	 */
	public void setFormLanguage1(String formLanguage1) {
		this.formLanguage1 = formLanguage1;
	}

	/**
	 * Method setFormLanguage2.
	 * 
	 * @param formLanguage2
	 *            String
	 */
	public void setFormLanguage2(String formLanguage2) {
		this.formLanguage2 = formLanguage2;
	}

	/**
	 * Method setFormLanguage3.
	 * 
	 * @param formLanguage3
	 *            String
	 */
	public void setFormLanguage3(String formLanguage3) {
		this.formLanguage3 = formLanguage3;
	}

	/**
	 * Method setId.
	 * 
	 * @param id
	 *            int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method setNotes.
	 * 
	 * @param notes
	 *            String
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Method setNumberOfLabels.
	 * 
	 * @param numberOfLabels
	 *            int
	 */
	public void setNumberOfLabels(int numberOfLabels) {
		this.numberOfLabels = numberOfLabels;
	}

	/**
	 * Method setPackageIndex.
	 * 
	 * @param packageIndex
	 *            int
	 */
	public void setPackageIndex(int packageIndex) {
		this.packageIndex = packageIndex;
	}

	/**
	 * Method setPatientId.
	 * 
	 * @param thePatientId
	 *            String
	 */
	public void setPatientId(String thePatientId) {
		this.patientId = thePatientId;
	}

	/**
	 * Method setSideTreatment.
	 * 
	 * @param printSideTreatmentLabels
	 *            boolean
	 */
	public void setSideTreatment(boolean printSideTreatmentLabels) {
		this.sideTreatment = printSideTreatmentLabels;
	}

	/**
	 * Method setSpecialInstruction1.
	 * 
	 * @param specialInstructions1
	 *            String
	 */
	public void setSpecialInstruction1(String specialInstructions1) {
		this.specialInstructions1 = specialInstructions1;
	}

	/**
	 * Method setSpecialInstruction2.
	 * 
	 * @param specialInstructions2
	 *            String
	 */
	public void setSpecialInstruction2(String specialInstructions2) {
		this.specialInstructions2 = specialInstructions2;
	}

	/**
	 * Method setSpecialInstructions1.
	 * 
	 * @param specialInstructions1
	 *            String
	 */
	public void setSpecialInstructions1(String specialInstructions1) {
		this.specialInstructions1 = specialInstructions1;
	}

	/**
	 * Method setSpecialInstructions2.
	 * 
	 * @param specialInstructions2
	 *            String
	 */
	public void setSpecialInstructions2(String specialInstructions2) {
		this.specialInstructions2 = specialInstructions2;
	}

	/**
	 * Method setStockId.
	 * 
	 * @param stockId
	 *            int
	 */
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	/**
	 * Method setTimesPerDay.
	 * 
	 * @param timesPerDay
	 *            int
	 */
	public void setTimesPerDay(int timesPerDay) {
		this.timesPerDay = timesPerDay;
	}

	/**
	 * Method setCluser.
	 * 
	 * @param user
	 *            User
	 */
	public void setCluser(User user) {
		this.cluser = user;
	}

	/**
	 * Method setWeeksSupply.
	 * 
	 * @param weeksSupply
	 *            int
	 */
	public void setWeeksSupply(int weeksSupply) {
		this.weeksSupply = weeksSupply;
	}

	/**
	 * Method getPackagedDrug.
	 * 
	 * @return PackagedDrugs
	 */
	public PackagedDrugs getPackagedDrug() {
		return packagedDrug;
	}

	/**
	 * Method setPackagedDrug.
	 * 
	 * @param packagedDrug
	 *            PackagedDrugs
	 */
	public void setPackagedDrug(PackagedDrugs packagedDrug) {
		this.packagedDrug = packagedDrug;
	}

	/**
	 * Method isInvalid.
	 * 
	 * @return boolean
	 */
	public boolean isInvalid() {
		return invalid;
	}

	/**
	 * Method setInvalid.
	 * 
	 * @param invalid
	 *            boolean
	 */
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * @return the patientFirstName
	 */
	public String getPatientFirstName() {
		return patientFirstName;
	}

	/**
	 * @param patientFirstName
	 *            the patientFirstName to set
	 */
	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	/**
	 * @return the patientLastName
	 */
	public String getPatientLastName() {
		return patientLastName;
	}

	/**
	 * @param patientLastName
	 *            the patientLastName to set
	 */
	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	public String getPatientName() {
		return getPatientFirstName() + " " + getPatientLastName();
	}

	/**
	 * @return the qtyInHand
	 */
	public String getQtyInHand() {
		return qtyInHand;
	}

	/**
	 * @param qtyInHand
	 *            the qtyInHand to set
	 */
	public void setQtyInHand(String qtyInHand) {
		this.qtyInHand = qtyInHand;
	}

	/**
	 * @return the summaryQtyInHand
	 */
	public String getSummaryQtyInHand() {
		return summaryQtyInHand;
	}

	/**
	 * @param summaryQtyInHand
	 *            the summaryQtyInHand to set
	 */
	public void setSummaryQtyInHand(String summaryQtyInHand) {
		this.summaryQtyInHand = summaryQtyInHand;
	}

	/**
	 * @return the qtyInLastBatch
	 */
	public String getQtyInLastBatch() {
		return qtyInLastBatch;
	}

	/**
	 * @param qtyInLastBatch
	 *            the qtyInLastBatch to set
	 */
	public void setQtyInLastBatch(String qtyInLastBatch) {
		this.qtyInLastBatch = qtyInLastBatch;
	}

	/**
	 * @return the firstBatchInPrintJob
	 */
	public boolean isFirstBatchInPrintJob() {
		return firstBatchInPrintJob;
	}
	
	/**
	 * @return the firstBatchInPrintJob
	 */
	public boolean getFirstBatchInPrintJob() {
		return firstBatchInPrintJob;
	}

	/**
	 * @param firstBatchInPrintJob
	 *            the firstBatchInPrintJob to set
	 */
	public void setFirstBatchInPrintJob(boolean firstBatchInPrintJob) {
		this.firstBatchInPrintJob = firstBatchInPrintJob;
	}

	/**
	 * @return the prescriptionDuration
	 */
	public int getPrescriptionDuration() {
		return prescriptionDuration;
	}

	/**
	 * @param prescriptionDuration
	 *            the prescriptionDuration to set
	 */
	public void setPrescriptionDuration(int prescriptionDuration) {
		this.prescriptionDuration = prescriptionDuration;
	}

	/**
	 * @return the dateExpectedString
	 */
	public String getDateExpectedString() {
		return dateExpectedString;
	}

	/**
	 * @param dateExpectedString
	 *            the dateExpectedString to set
	 */
	public void setDateExpectedString(String dateExpectedString) {
		this.dateExpectedString = dateExpectedString;
	}

	/**
	 * @return the sentToEkapa
	 */
	public boolean isSentToEkapa() {
		return sentToEkapa;
	}

	/**
	 * @param sentToEkapa
	 *            the sentToEkapa to set
	 */
	public void setSentToEkapa(boolean sentToEkapa) {
		this.sentToEkapa = sentToEkapa;
	}

	/**
	 * @return the packageId
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId
	 *            the packageId to set
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	/**
	 * @return the dispensedForLaterPickup
	 */
	public boolean isDispensedForLaterPickup() {
		return dispensedForLaterPickup;
	}

	/**
	 * @param dispensedForLaterPickup
	 *            the dispensedForLaterPickup to set
	 */
	public void setDispensedForLaterPickup(boolean dispensedForLaterPickup) {
		this.dispensedForLaterPickup = dispensedForLaterPickup;
	}

	public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
	}

	public Date getPickupDate() {
		return pickupDate;
	}

}
