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
@Table(name = "patientstatistic")
public class PatientStatistic {

	@Id
	@GeneratedValue
	private Integer id;
	private int entry_id;
	private int patient_id;
	@Temporal(TemporalType.DATE)
	private Date datetested;
	@Temporal(TemporalType.DATE)
	private Date daterecorded;
	private int patientstattype_id;
	private double statnumeric;
	private String stattext;
	@Temporal(TemporalType.DATE)
	private Date statdate;

	public PatientStatistic() {
		super();

	}

	public PatientStatistic(int entry_id, int patient_id, Date datetested,
			Date daterecorded, int patientstattype_id, double statnumeric,
			String stattext, Date statdate) {
		super();
		this.entry_id = entry_id;
		this.patient_id = patient_id;
		this.datetested = datetested;
		this.daterecorded = daterecorded;
		this.patientstattype_id = patientstattype_id;
		this.statnumeric = statnumeric;
		this.stattext = stattext;
		this.statdate = statdate;
	}

	public int getId() {
		return id;
	}

	public int getentry_id() {
		return entry_id;
	}

	public int getpatient_id() {
		return patient_id;
	}

	public Date getdatetested() {
		return datetested;
	}

	public Date getdaterecorded() {
		return daterecorded;
	}

	public int getstattype() {
		return patientstattype_id;
	}

	public double getstatnumeric() {
		return statnumeric;
	}

	public String getstattext() {
		return stattext;
	}

	public Date getstatdate() {
		return statdate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setentry_id(int entry_id) {
		this.entry_id = entry_id;
	}

	public void setpatient_id(int patient_id) {
		this.patient_id = patient_id;
	}

	public void setdatetested(Date datetested) {
		this.datetested = datetested;
	}

	public void setdaterecorded(Date daterecorded) {
		this.daterecorded = daterecorded;
	}

	public void setstattype(int patientstattype_id) {
		this.patientstattype_id = patientstattype_id;
	}

	public void setstatnumeric(double statnumeric) {
		this.statnumeric = statnumeric;
	}

	public void setstattext(String stattext) {
		this.stattext = stattext;
	}

	public void setstatdate(Date statdate) {
		this.statdate = statdate;
	}
}
