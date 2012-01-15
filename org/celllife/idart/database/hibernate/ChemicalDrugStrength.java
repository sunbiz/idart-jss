package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class ChemicalDrugStrength {

	@ManyToOne
	@JoinColumn(name = "chemicalCompound")
	private ChemicalCompound chemicalCompound;

	@Id
	@GeneratedValue
	private Integer id;

	private int strength;

	@ManyToOne
	@JoinColumn(name = "drug")
	private Drug drug;

	public ChemicalDrugStrength() {
	}

	/**
	 * Constructor for ChemicalDrugStrength.
	 * 
	 * @param chemicalCompound
	 *            ChemicalCompound
	 * @param strength
	 *            int
	 * @param drug
	 *            Drug
	 */
	public ChemicalDrugStrength(ChemicalCompound chemicalCompound,
			int strength, Drug drug) {
		super();
		this.chemicalCompound = chemicalCompound;
		this.strength = strength;
		this.drug = drug;
	}

	/**
	 * Method getChemicalCompound.
	 * 
	 * @return ChemicalCompound
	 */
	public ChemicalCompound getChemicalCompound() {
		return chemicalCompound;
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
	 * Method getStrength.
	 * 
	 * @return int
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * Method getDrug.
	 * 
	 * @return Drug
	 */
	public Drug getDrug() {
		return drug;
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
	 * Method setStrength.
	 * 
	 * @param strength
	 *            int
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/**
	 * Method setChemicalCompound.
	 * 
	 * @param chemicalCompound
	 *            ChemicalCompound
	 */
	public void setChemicalCompound(ChemicalCompound chemicalCompound) {
		this.chemicalCompound = chemicalCompound;
	}

	/**
	 * Method setDrug.
	 * 
	 * @param drug
	 *            Drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (strength ^ (strength >>> 32));
		result = PRIME
		* result
		+ ((chemicalCompound.getName() == null) ? 0 : chemicalCompound
				.getName().hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ChemicalDrugStrength other = (ChemicalDrugStrength) obj;
		if (strength != other.strength)
			return false;
		if (chemicalCompound.getName() == null) {
			if (other.chemicalCompound.getName() != null)
				return false;
		} else if (!chemicalCompound.getName().equals(
				other.getChemicalCompound().getName()))
			return false;
		return true;

	}
}
