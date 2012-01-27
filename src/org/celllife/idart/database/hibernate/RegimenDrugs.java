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
public class RegimenDrugs {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "regimen", insertable = false, updatable = false)
	private Regimen regimen;

	@ManyToOne
	@JoinColumn(name = "drug")
	private Drug drug;

	private int timesPerDay;

	private char modified;

	private double amtPerTime;

	private String notes;

	public RegimenDrugs() {
		super();
	}

	/**
	 * Method getAmtPerTime.
	 * @return double
	 */
	public double getAmtPerTime() {
		return amtPerTime;
	}

	/**
	 * Method setAmtPerTime.
	 * @param amtPerTime double
	 */
	public void setAmtPerTime(double amtPerTime) {
		this.amtPerTime = amtPerTime;
	}

	/**
	 * Method getDrug.
	 * @return Drug
	 */
	public Drug getDrug() {
		return drug;
	}

	/**
	 * Method setDrug.
	 * @param drug Drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
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
	 * Method getRegimen.
	 * @return Regimen
	 */
	public Regimen getRegimen() {
		return regimen;
	}

	/**
	 * Method setRegimen.
	 * @param regimen Regimen
	 */
	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}

	/**
	 * Method getTimesPerDay.
	 * @return int
	 */
	public int getTimesPerDay() {
		return timesPerDay;
	}

	/**
	 * Method setTimesPerDay.
	 * @param timesPerDay int
	 */
	public void setTimesPerDay(int timesPerDay) {
		this.timesPerDay = timesPerDay;
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

}
