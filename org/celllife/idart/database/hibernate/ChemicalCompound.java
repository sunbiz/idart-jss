package org.celllife.idart.database.hibernate;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
@Entity
public class ChemicalCompound {

	@Id
	@GeneratedValue
	private int id;

	private String acronym;

	private String name;

	@OneToMany
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<ChemicalDrugStrength> chemicalDrugStrengths;

	public ChemicalCompound() {
	}

	/**
	 * Constructor for ChemicalCompound.
	 * @param name String
	 * @param acronym String
	 */
	public ChemicalCompound(String name, String acronym) {
		this.name = name;
		this.acronym = acronym;
	}

	/**
	 * Method getAcronym.
	 * @return String
	 */
	public String getAcronym() {
		return acronym == null ? "" : acronym;
	}

	/**
	 * Method getName.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method getChemicalDrugStrengths.
	 * @return Set<ChemicalDrugStrength>
	 */
	public Set<ChemicalDrugStrength> getChemicalDrugStrengths() {
		return chemicalDrugStrengths;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setAcronym.
	 * @param acronym String
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
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
	 * Method setName.
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

}
