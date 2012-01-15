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
package model.nonPersistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.DrugManager;

import org.celllife.idart.database.hibernate.Drug;

/**
 */
public class DrugCombination {

	private Set<Drug> drugSet = null;

	private Set<Integer> adultsOnThis = null;

	private Set<Integer> paedsOnThis = null;

	private Set<String> regimens;

	/**
	 * Method getAdultsOnThis.
	 * 
	 * @return Set<Integer>
	 */
	public Set<Integer> getAdultsOnThis() {
		return adultsOnThis;
	}

	/**
	 * Method setAdultsOnThis.
	 * 
	 * @param adultsOnThis
	 *            Set<Integer>
	 */
	public void setAdultsOnThis(Set<Integer> adultsOnThis) {
		this.adultsOnThis = adultsOnThis;
	}

	/**
	 * Method getdrugSet.
	 * 
	 * @return Set<Drug>
	 */
	public Set<Drug> getdrugSet() {
		return drugSet;
	}

	/**
	 * Method setdrugSet.
	 * 
	 * @param drugSet
	 *            Set<Drug>
	 */
	public void setdrugSet(Set<Drug> drugSet) {
		this.drugSet = drugSet;
	}

	/**
	 * Method addAdultOnThis.
	 * 
	 * @param adultId
	 *            int
	 */
	public void addAdultOnThis(int adultId) {

		adultsOnThis.add(adultId);
	}

	/**
	 * Method addPaedOnThis.
	 * 
	 * @param paedsId
	 *            int
	 */
	public void addPaedOnThis(int paedsId) {

		paedsOnThis.add(paedsId);
	}

	/**
	 * Method getPaedsOnThis.
	 * 
	 * @return Set<Integer>
	 */
	public Set<Integer> getPaedsOnThis() {
		return paedsOnThis;
	}

	/**
	 * Method setPaedsOnThis.
	 * 
	 * @param paedsOnThis
	 *            Set<Integer>
	 */
	public void setPaedsOnThis(Set<Integer> paedsOnThis) {
		this.paedsOnThis = paedsOnThis;
	}

	/**
	 * Constructor for DrugCombination.
	 * 
	 * @param drugSet
	 *            Set<Drug>
	 */
	public DrugCombination(Set<Drug> drugSet) {
		super();
		this.drugSet = drugSet;
		adultsOnThis = new HashSet<Integer>();
		paedsOnThis = new HashSet<Integer>();
	}

	/**
	 * Method hashCode.
	 * 
	 * @return int
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((drugSet == null) ? 0 : drugSet.hashCode());
		return result;
	}

	/**
	 * Method equals.
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DrugCombination other = (DrugCombination) obj;
		if (drugSet == null) {
			if (other.drugSet != null)
				return false;
		} else if (!drugSet.equals(other.drugSet))
			return false;
		return true;
	}

	/**
	 * Method toString.
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		// sort alphabetically into a list
		List<Drug> drugList = new ArrayList<Drug>();
		drugList.addAll(drugSet);
		Collections.sort(drugList);
		String drugListString = DrugManager.getDrugListString(drugList, " + ", true);
		String out = "";
		if (regimens != null) {
			String regString = "(";
			for (String reg : regimens) {
				regString += reg + ";";
			}
			regString = regString.substring(0, regString.length() - 1) + ") ";
			out = regString + drugListString;
		} else {
			out = drugListString;
		}
		return out;
	}

	public Set<Integer> getDrugIdSet() {
		Set<Integer> drugIdSet = new HashSet<Integer>();
		for (Drug drug : drugSet) {
			drugIdSet.add(drug.getId());
		}
		return drugIdSet;
	}

	public void addRegimen(String drugGroup) {
		if (regimens == null) {
			regimens = new HashSet<String>();
		}
		regimens.add(drugGroup);
	}

}
