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
public class PrescribedDrugs {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "prescription", insertable = false, updatable = false)
	private Prescription prescription;

	@ManyToOne
	@JoinColumn(name = "drug")
	private Drug drug;

	private int timesPerDay;

	private char modified;

	private double amtPerTime;

	public PrescribedDrugs() {
		super();

	}

	/**
	 * @param prescription
	 * @param drug
	 * @param timesPerDay
	 * @param amtPerTime
	 * @param modified
	 */
	public PrescribedDrugs(Prescription prescription, Drug drug,
			int timesPerDay, double amtPerTime, char modified) {
		super();
		this.prescription = prescription;
		this.drug = drug;
		this.timesPerDay = timesPerDay;
		this.amtPerTime = amtPerTime;
		this.modified = modified;

	}

	/**
	 * Method getAmtPerTime.
	 * @return double
	 */
	public double getAmtPerTime() {
		return amtPerTime;
	}

	/**
	 * Method getDrug.
	 * @return Drug
	 */
	public Drug getDrug() {
		return drug;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getPrescription.
	 * @return Prescription
	 */
	public Prescription getPrescription() {
		return prescription;
	}

	/**
	 * Method getTimesPerDay.
	 * @return int
	 */
	public int getTimesPerDay() {
		return timesPerDay;
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
	 * Method setAmtPerTime.
	 * @param amtPerTime double
	 */
	public void setAmtPerTime(double amtPerTime) {
		this.amtPerTime = amtPerTime;
	}

	/**
	 * Method setDrug.
	 * @param drug Drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setPrescription.
	 * @param prescription Prescription
	 */
	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	/**
	 * Method setTimesPerDay.
	 * @param timesPerDay int
	 */
	public void setTimesPerDay(int timesPerDay) {
		this.timesPerDay = timesPerDay;
	}

	public boolean isARV() {
		return this.drug.isARV();
	}
}
