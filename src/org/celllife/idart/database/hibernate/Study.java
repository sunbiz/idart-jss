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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
/**
 * @author Rashid
 *
 */
@Entity
public class Study {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false, unique = true)
	private String studyName;
	
	@OneToMany
	@JoinColumn(name = "study")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<StudyParticipant> studyParticipants;

	/**
	 * 
	 */
	public Study() {
		super();
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
	 * @return the studyName
	 */
	public String getStudyName() {
		return studyName;
	}

	/**
	 * @param studyName the studyName to set
	 */
	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}
	/**
	 * @return the studyParticipants
	 */
	public Set<StudyParticipant> getStudyParticipants() {
		return studyParticipants;
	}

	/**
	 * @param studyParticipants the studyParticipants to set
	 */
	public void setStudyParticipants(Set<StudyParticipant> studyParticipants) {
		this.studyParticipants = studyParticipants;
	}

	
	
	
}
