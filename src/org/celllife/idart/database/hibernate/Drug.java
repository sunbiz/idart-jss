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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
@Entity
public class Drug implements Comparable<Drug> {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "form")
	private Form form;
	
	@ManyToOne
	@JoinColumn(name = "atccode_id")
	private AtcCode atccode;

	private String dispensingInstructions1;

	private String dispensingInstructions2;

	private char modified;

	private String name;

	private int packSize;

	@OneToMany(mappedBy = "drug")
	private Set<Stock> stock;

	private char sideTreatment;

	private double defaultAmnt;

	private int defaultTimes;

	private String stockCode;

	@OneToMany(mappedBy = "drug")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		private Set<ChemicalDrugStrength> chemicalDrugStrengths;

	/**
	 * Default constructor
	 */
	public Drug() {
		super();

	}

	/**
	 * Constructor for Drug.
	 * 
	 * @param form
	 *            Form
	 * @param dispensingInstructions1
	 *            String
	 * @param dispensingInstructions2
	 *            String
	 * @param modified
	 *            char
	 * @param name
	 *            String
	 * @param packSize
	 *            int
	 * @param stock
	 *            Set<Stock>
	 * @param sideTreatment
	 *            char
	 * @param defaultAmnt
	 *            double
	 * @param defaultTimes
	 *            int
	 */
	public Drug(Form form, String dispensingInstructions1,
			String dispensingInstructions2, char modified, String name,
			int packSize, Set<Stock> stock, char sideTreatment,
			double defaultAmnt, int defaultTimes) {
		super();

		this.form = form;
		this.dispensingInstructions1 = dispensingInstructions1;
		this.dispensingInstructions2 = dispensingInstructions2;
		this.modified = modified;
		this.name = name;
		this.packSize = packSize;
		this.stock = stock;
		this.sideTreatment = sideTreatment;
		this.defaultAmnt = defaultAmnt;
		this.defaultTimes = defaultTimes;
	}

	/**
	 * Method compareTo.
	 * @param d2 Drug
	 * @return int
	 */
	@Override
	public int compareTo(Drug d2) {
		return this.getName().compareToIgnoreCase(d2.getName());
	}

	/**
	 * Method getForm.
	 * @return Form
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * Method getStock.
	 * @return Set<Stock>
	 */
	public Set<Stock> getStock() {
		if(stock == null) {
			setStock(new HashSet<Stock>());
		}
		return stock;
	}

	/**
	 * Method getDispensingInstructions1.
	 * @return String
	 */
	public String getDispensingInstructions1() {
		return dispensingInstructions1;
	}

	/**
	 * Method getDispensingInstructions2.
	 * @return String
	 */
	public String getDispensingInstructions2() {
		return dispensingInstructions2;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getModified.
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method getName.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method getPackSize.
	 * @return int
	 */
	public int getPackSize() {
		return packSize;
	}

	/**
	 * Method getSideTreatment.
	 * @return char
	 */
	public char getSideTreatment() {
		return sideTreatment;
	}

	/**
	 * Method getChemicalDrugStrengths.
	 * @return Set<ChemicalDrugStrength>
	 */
	public Set<ChemicalDrugStrength> getChemicalDrugStrengths() {
		if(chemicalDrugStrengths == null) {
			setChemicalDrugStrengths(new HashSet<ChemicalDrugStrength>());
		}
		return chemicalDrugStrengths;
	}

	/**
	 * Method setForm.
	 * @param form Form
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * Method setDispensingInstructions1.
	 * @param dispensingInstruction1 String
	 */
	public void setDispensingInstructions1(String dispensingInstruction1) {
		this.dispensingInstructions1 = dispensingInstruction1;
	}

	/**
	 * Method setDispensingInstructions2.
	 * @param dispensingInstruction2 String
	 */
	public void setDispensingInstructions2(String dispensingInstruction2) {
		this.dispensingInstructions2 = dispensingInstruction2;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setModified.
	 * @param modified char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setName.
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method setSideTreatment.
	 * @param sideTreatment char
	 */
	public void setSideTreatment(char sideTreatment) {
		this.sideTreatment = sideTreatment;
	}

	/**
	 * Method setPackSize.
	 * @param packSize int
	 */
	public void setPackSize(int packSize) {
		this.packSize = packSize;
	}

	/**
	 * Method getDefaultAmnt.
	 * 
	 * @return double
	 */
	public double getDefaultAmnt() {
		return defaultAmnt;
	}

	/**
	 * Method setDefaultAmnt.
	 * 
	 * @param defaultAmnt
	 *            double
	 */
	public void setDefaultAmnt(double defaultAmnt) {
		this.defaultAmnt = defaultAmnt;
	}

	/**
	 * Method getDefaultTimes.
	 * @return int
	 */
	public int getDefaultTimes() {
		return defaultTimes;
	}

	/**
	 * Method setDefaultTimes.
	 * @param defaultTimes int
	 */
	public void setDefaultTimes(int defaultTimes) {
		this.defaultTimes = defaultTimes;
	}

	/**
	 * Method getStockCode.
	 * @return String
	 */
	public String getStockCode() {
		return stockCode;
	}

	/**
	 * Method setStockCode.
	 * @param stockCode String
	 */
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	/**
	 * Method setChemicalDrugStrengths.
	 * @param ChemicalDrugStrengths Set<ChemicalDrugStrength>
	 */
	public void setChemicalDrugStrengths(
			Set<ChemicalDrugStrength> ChemicalDrugStrengths) {
		this.chemicalDrugStrengths = ChemicalDrugStrengths;
	}

	/**
	 * Method setStock.
	 * @param stock Set<Stock>
	 */
	public void setStock(Set<Stock> stock) {
		this.stock = stock;
	}

	public boolean isARV() {
		return sideTreatment == 'F';
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((chemicalDrugStrengths == null) ? 0 : chemicalDrugStrengths
						.hashCode());
		result = prime * result + sideTreatment;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Drug other = (Drug) obj;
		if (chemicalDrugStrengths == null) {
			if (other.chemicalDrugStrengths != null)
				return false;
		} else if (!chemicalDrugStrengths.equals(other.chemicalDrugStrengths)  
				&& sideTreatment == 'F')
			return false;
		if (sideTreatment != other.sideTreatment)
			return false;
		return true;
	}

	public void setAtccode(AtcCode atccode) {
		this.atccode = atccode;
	}

	public AtcCode getAtccode() {
		return atccode;
	}

	public Set<AtcCode> getAtccodes() {
		Set<AtcCode> codes = new HashSet<AtcCode>();
		if (atccode != null)
			codes.add(atccode);
		
		if (chemicalDrugStrengths == null || chemicalDrugStrengths.isEmpty()){
			return codes;
		}
		for (ChemicalDrugStrength cds : chemicalDrugStrengths) {
			Set<AtcCode> atccodes = cds.getChemicalCompound().getAtccodes();
			if (atccodes != null){
				codes.addAll(atccodes);
			}
		}
		return codes;
	}

	public Set<ChemicalCompound> getChemicalCompounds(){
		Set<ChemicalCompound> ccs = new HashSet<ChemicalCompound>();
		Set<ChemicalDrugStrength> cds = getChemicalDrugStrengths();
		if (cds != null){
			for (ChemicalDrugStrength cd : cds) {
				ccs.add(cd.getChemicalCompound());
			}
		}
		return ccs;
	}
	
}
