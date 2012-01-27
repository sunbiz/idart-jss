package org.celllife.idart.database.hibernate;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

/**
 */
@Entity
public class ChemicalCompound {

	@Id
	@GeneratedValue
	private Integer id;
	
	private String acronym;

	private String name;

	@OneToMany(mappedBy = "chemicalCompound")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<ChemicalDrugStrength> chemicalDrugStrengths;
	
	@ManyToMany(mappedBy="chemicalCompounds")
	private Set<AtcCode> atccodes;

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
		return acronym == null ? name : acronym;
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

	public void setAtccodes(Set<AtcCode> atccodes) {
		this.atccodes = atccodes;
	}

	public Set<AtcCode> getAtccodes() {
		if (atccodes == null){
			atccodes = new HashSet<AtcCode>();
		}
		return atccodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ChemicalCompound other = (ChemicalCompound) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChemicalCompound [acronym=").append(acronym)
				.append(", name=").append(name).append("]");
		return builder.toString();
	}
	
	
}
