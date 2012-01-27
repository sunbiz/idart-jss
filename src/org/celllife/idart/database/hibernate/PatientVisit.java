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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 */
@Entity
@Table(name = "patientvisit")
public class PatientVisit {

	@Id
	@GeneratedValue
	private Integer id;
	private int patient_id;
	@Temporal(TemporalType.DATE)
	private Date dateofvisit;
	private String isscheduled;
	private int patientvisitreason_id;
	private String diagnosis;
	private String notes;

	public PatientVisit() {
		super();

	}

	public PatientVisit(int patient_id, Date dateofvisit, String isscheduled,
			int visitreason, String diagnosis, String notes) {

		super();
		this.patient_id = patient_id;
		this.dateofvisit = dateofvisit;
		this.isscheduled = isscheduled;
		this.patientvisitreason_id = visitreason;
		this.diagnosis = diagnosis;
		this.notes = notes;
	}

	public int getId() {
		return id;
	}

	public Date getDateofVisit() {
		return dateofvisit;
	}

	public int getpatientid() {
		return patient_id;
	}

	public String getisScheduled() {
		return isscheduled;
	}

	public int getVisitReason() {
		return patientvisitreason_id;
	}

	public String getdiagnosis() {
		return diagnosis;
	}

	public String getnotes() {
		return notes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setpatientid(int patient_id) {
		this.patient_id = patient_id;
	}

	public void setDateofVisit(Date dateofvisit) {
		this.dateofvisit = dateofvisit;
	}

	public void setisScheduled(String isscheduled) {
		this.isscheduled = isscheduled;
	}

	public void setVisitReason(int visitreason) {
		this.patientvisitreason_id = visitreason;
	}

	public void setdiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public void setnotes(String notes) {
		this.notes = notes;
	}

}
