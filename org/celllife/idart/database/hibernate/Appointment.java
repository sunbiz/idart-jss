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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class Appointment {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "patient")
	private Patient patient;

	private Date appointmentDate;
	
	private Date visitDate;

	public Appointment() {

		super();
	}

	/**
	 * Constructor for Appointment.
	 * @param patient Patient
	 * @param confirmDate Date
	 * @param modified char
	 */
	public Appointment(Patient patient, Date appointmentDate, Date visitDate) {
		super();
		this.patient = patient;
		this.appointmentDate = appointmentDate;
		this.visitDate = visitDate;
	}

	/**
	 * Method getAppointmentDate.
	 * @return Date
	 */
	public Date getAppointmentDate() {
		return appointmentDate;
	}

	/**
	 * Method setAppointmentDate.
	 * @param appointmentDate Date
	 */
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
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
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method getPatient.
	 * @return Patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Method setPatient.
	 * @param patient Patient
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return the visitDate
	 */
	public Date getVisitDate() {
		return visitDate;
	}

	/**
	 * @param visitDate the visitDate to set
	 */
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	/**
	 * Method to check if appointment is active
	 * @return
	 */
	public boolean isActive() {
		if(visitDate == null)
			return true;
		
		else return false;
	}
	
}