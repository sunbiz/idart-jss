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

import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

/**
 */
@Entity
public class Regimen {

	@Id
	@GeneratedValue
	private Integer id;

	private String regimenName;

	private String drugGroup;

	private char modified;

	@OneToMany
	@JoinColumn(name = "regimen")
	@IndexColumn(name = "regimenDrugsIndex")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<RegimenDrugs> regimenDrugs;

	private String notes;

	public Regimen() {
		super();
	}

	/**
	 * 
	 * @param regimenName
	 * @param notes
	 */
	public Regimen(String regimenName, String notes) {
		super();
		this.regimenName = regimenName;
		this.notes = notes;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public Integer getId() {
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

	/**
	 * Method getRegimenDrugs.
	 * @return List<RegimenDrugs>
	 */
	public List<RegimenDrugs> getRegimenDrugs() {
		return regimenDrugs;
	}

	/**
	 * Method setRegimenDrugs.
	 * @param regimenDrugs List<RegimenDrugs>
	 */
	public void setRegimenDrugs(List<RegimenDrugs> regimenDrugs) {
		this.regimenDrugs = regimenDrugs;
	}

	/**
	 * Method getRegimenName.
	 * @return String
	 */
	public String getRegimenName() {
		return regimenName;
	}

	/**
	 * Method setRegimenName.
	 * @param regimenName String
	 */
	public void setRegimenName(String regimenName) {
		this.regimenName = regimenName;
	}

	/**
	 * Method getDrugGroup.
	 * @return String
	 */
	public String getDrugGroup() {
		return drugGroup;
	}

	/**
	 * Method setDrugGroup.
	 * @param drugGroup String
	 */
	public void setDrugGroup(String drugGroup) {
		this.drugGroup = drugGroup;
	}

	/**
	 * Method equals.
	 * @param regimen Regimen
	 * @return boolean
	 */
	public boolean equals(Regimen regimen) {
		boolean noMatch = false;
		if (this.getRegimenDrugs().size() == regimen.getRegimenDrugs().size()) {
			for (Iterator<RegimenDrugs> iter = this.getRegimenDrugs()
					.iterator(); iter.hasNext();) {
				Drug currentDrug = (iter.next()).getDrug();
				for (Iterator<RegimenDrugs> iterator = regimen
						.getRegimenDrugs().iterator(); iterator.hasNext();) {
					if ((iterator.next()).getDrug().equals(
							currentDrug))
						noMatch = true;
				}
				if (!noMatch)
					return false;
			}

			return true;

		}

		return false;
	}

}
