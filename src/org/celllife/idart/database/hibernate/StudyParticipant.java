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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.nomsic.randb.model.Cell;

/**
 * @author Rashid
 *
 */
@Entity
public class StudyParticipant {
	
	public static final String GP_CONTROL = "CONTROL"; //$NON-NLS-1$ 
	public static final String GP_ACTIVE = "ACTIVE"; //$NON-NLS-1$ 
	public static final String[] GROUPS = new String[] { GP_ACTIVE, GP_CONTROL };

	@Id
	@GeneratedValue
	private Integer id;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@ManyToOne
	@JoinColumn(name="patient", nullable=false)
	private Patient patient;
	
	@ManyToOne
	@JoinColumn(name = "study", nullable = false)
	private Study study;
	
	@Column(nullable = false)
	private String studyGroup;
	
	@Column(nullable = true)
	private String randomizationId;
	
	@Column(nullable = true)
	private String alternateCellphone;
	
	@Column(nullable = false)
	private String network;
	
	@Column(nullable = true)
	private String language;
	
	@Transient
	private Cell cell;

	public StudyParticipant() {
		super();
	}
	
	public StudyParticipant(Patient patient, Study study, String studyGroup, Date startDate, Date endDate) {
		super();
		this.patient = patient;
		this.study = study;
		this.studyGroup = studyGroup;
		this.startDate = startDate;
		this.endDate = endDate;
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}


	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}


	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}


	/**
	 * @param patient the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}


	/**
	 * @return the study
	 */
	public Study getStudy() {
		return study;
	}


	/**
	 * @param study the study to set
	 */
	public void setStudy(Study study) {
		this.study = study;
	}


	/**
	 * @return the studyGroup
	 */
	public String getStudyGroup() {
		return studyGroup;
	}

	public boolean isInStudy(){
		return studyGroup!= null && studyGroup.equalsIgnoreCase(GP_ACTIVE);
	}

	/**
	 * @param studyGroup the studyGroup to set
	 */
	public void setStudyGroup(String studyGroup) {
		this.studyGroup = studyGroup;
	}

	public void setRandomizationId(String uuid) {
		this.randomizationId = uuid;
	}
	
	public String getRandomizationId() {
		return randomizationId;
	}
	
	public String getAlternateCellphone() {
		return alternateCellphone;
	}

	public void setAlternateCellphone(String alternateCellphone) {
		this.alternateCellphone = alternateCellphone;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public void setRandCell(Cell cell) {
		this.cell = cell;
		studyGroup = cell.getGroup();
		randomizationId = cell.getUuid().toString();
	}
	
	public Cell getRandCell(){
		return cell;
	}
}
