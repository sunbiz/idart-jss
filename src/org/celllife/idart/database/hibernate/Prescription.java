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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

/**
 */
@Entity
public class Prescription {

	@Id
	@GeneratedValue
	private Integer id;

	private int clinicalStage;

	private char current;

	private Date date;

	@ManyToOne
	@JoinColumn(name = "doctor")
	private Doctor doctor;

	private int duration;

	private char modified;

	@OneToMany(mappedBy = "prescription")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		private Set<Packages> packages;

	@ManyToOne
	@JoinColumn(name = "patient", nullable = false)
	private Patient patient;

	@OneToMany
	@JoinColumn(name = "prescription")
	@IndexColumn(name = "prescribeddrugsindex")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		private List<PrescribedDrugs> prescribedDrugs;

	private String prescriptionId;

	private String reasonForUpdate;

	private String notes;

	private Double weight;

	private Date endDate;
	
	private String drugTypes;

	@Transient
	private HashSet<Drug> arvDrugSet;

	/**
	 * @param clinicalStage
	 * @param current
	 * @param date
	 * @param doctor
	 * @param duration
	 * @param id
	 * @param modified
	 * @param packages
	 * @param patient
	 * @param prescribedDrugs
	 * @param prescriptionId
	 * @param reasonForUpdate
	 * @param notes
	 * @param clinic
	 */
	public Prescription(int clinicalStage, char current, Date date,
			Doctor doctor, int duration, int id, char modified,
			Set<Packages> packages, Patient patient,
			List<PrescribedDrugs> prescribedDrugs, String prescriptionId,
			String reasonForUpdate, String notes, Clinic clinic) {
		super();
		this.clinicalStage = clinicalStage;
		this.current = current;
		this.date = date;
		this.doctor = doctor;
		this.duration = duration;
		this.id = id;
		this.modified = modified;
		this.packages = packages;
		this.patient = patient;
		this.prescribedDrugs = prescribedDrugs;
		this.prescriptionId = prescriptionId;
		this.reasonForUpdate = reasonForUpdate;
		this.notes = notes;

	}

	public Prescription() {
		super();

	}

	/**
	 * Method getClinicalStage.
	 * 
	 * @return int
	 */
	public int getClinicalStage() {
		return clinicalStage;
	}

	/**
	 * Method getCurrent.
	 * 
	 * @return char
	 */
	public char getCurrent() {
		return current;
	}

	/**
	 * Method getDate.
	 * 
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Method getDoctor.
	 * 
	 * @return Doctor
	 */
	public Doctor getDoctor() {
		return doctor;
	}

	/**
	 * Method getDuration.
	 * 
	 * @return int
	 */
	public int getDuration() {
		return duration;
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
	 * Method getPackages.
	 * 
	 * @return Set<Packages>
	 */
	public Set<Packages> getPackages() {
		return packages;
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
	 * Method getPrescribedDrugs.
	 * 
	 * @return List<PrescribedDrugs>
	 */
	public List<PrescribedDrugs> getPrescribedDrugs() {
		return prescribedDrugs;
	}

	/**
	 * Method getPrescriptionId.
	 * 
	 * @return String
	 */
	public String getPrescriptionId() {
		return prescriptionId;
	}

	/**
	 * Method getWeight.
	 * 
	 * @return Double
	 */
	public Double getWeight() {
		return weight;
	}

	/**
	 * Method getReasonForUpdate.
	 * 
	 * @return String
	 */
	public String getReasonForUpdate() {
		return reasonForUpdate;
	}

	/**
	 * Method setClinicalStage.
	 * 
	 * @param clinicalStage
	 *            int
	 */
	public void setClinicalStage(int clinicalStage) {
		this.clinicalStage = clinicalStage;
	}

	/**
	 * Method setCurrent.
	 * 
	 * @param currentt
	 *            char
	 */
	public void setCurrent(char currentt) {
		this.current = currentt;
	}

	/**
	 * Method setDate.
	 * 
	 * @param date
	 *            Date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Method setDoctor.
	 * 
	 * @param doctor
	 *            Doctor
	 */
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	/**
	 * Method setDuration.
	 * 
	 * @param duration
	 *            int
	 */
	public void setDuration(int duration) {
		this.duration = duration;
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
	 * Method setPackages.
	 * 
	 * @param packages
	 *            Set<Packages>
	 */
	public void setPackages(Set<Packages> packages) {
		this.packages = packages;
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
	 * Method setPrescribedDrugs.
	 * 
	 * @param prescribedDrugs
	 *            List<PrescribedDrugs>
	 */
	public void setPrescribedDrugs(List<PrescribedDrugs> prescribedDrugs) {
		this.prescribedDrugs = prescribedDrugs;
	}

	/**
	 * Method setPrescriptionId.
	 * 
	 * @param prescriptionId
	 *            String
	 */
	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	/**
	 * Method setReasonForUpdate.
	 * 
	 * @param reasonForUpdate
	 *            String
	 */
	public void setReasonForUpdate(String reasonForUpdate) {
		this.reasonForUpdate = reasonForUpdate;
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
	 * Method setNotes.
	 * 
	 * @param notes
	 *            String
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Method setWeight.
	 * 
	 * @param weight
	 *            Double
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
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
	 * Method setEndDate.
	 * 
	 * @param endDate
	 *            Date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean containsARVDrug() {
		return !getARVDrugSet().isEmpty();
	}

	public Set<Drug> getARVDrugSet() {
		if (arvDrugSet == null) {
			arvDrugSet = new HashSet<Drug>();
		}
		for (PrescribedDrugs pd : prescribedDrugs) {
			Drug theDrug = pd.getDrug();
			if (theDrug.isARV()) {
				arvDrugSet.add(theDrug);
			}
		}
		return arvDrugSet;
	}

	public boolean isCurrent() {
		return Character.toUpperCase(current) == 'T';
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

	
}
