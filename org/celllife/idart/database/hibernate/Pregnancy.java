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

/*
 * Created on 2005/03/17
 *
 */
package org.celllife.idart.database.hibernate;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class Pregnancy {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "patient")
	private Patient patient;

	private Date confirmDate;

	private Date endDate;

	private char modified;

	public Pregnancy() {

		super();
	}

	/**
	 * @param patient
	 * @param confirmDate
	 *            Date
	 * @param endDate
	 *            Date
	 * @param modified
	 */
	public Pregnancy(Patient patient, Date confirmDate, Date endDate,
			char modified) {
		super();
		this.patient = patient;
		this.confirmDate = confirmDate;
		this.endDate = endDate;
		this.modified = modified;

	}

	/**
	 * Method getConfirmDate.
	 * 
	 * @return Date
	 */
	public Date getConfirmDate() {
		return confirmDate;
	}

	/**
	 * Method setEndDate.
	 * 
	 * @param endDate
	 *            Date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Method getEndDate.
	 * 
	 * @return Date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Method setConfirmDate.
	 * 
	 * @param confirmDate
	 *            Date
	 */
	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
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
	 * Method setId.
	 * 
	 * @param id
	 *            int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method getPatient.
	 * 
	 * @return Patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Method setPatient.
	 * 
	 * @param patient
	 *            Patient
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
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
	 * Method setModified.
	 * 
	 * @param modified
	 *            char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	public boolean dateFallsInPregnancy(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(confirmDate);
		cal.add(Calendar.MONTH, 9);
		return date.after(confirmDate) && date.before(cal.getTime());
	}

}