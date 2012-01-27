package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class AccumulatedDrugs {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "withPackage")
	private Packages withPackage;

	@ManyToOne
	@JoinColumn(name = "pillCount")
	private PillCount pillCount;

	/**
	 * Constructor for AccumulatedDrugs.
	 * @param withPackage Packages
	 * @param pillCount PillCount
	 */
	public AccumulatedDrugs(Packages withPackage, PillCount pillCount) {
		super();
		this.withPackage = withPackage;
		this.pillCount = pillCount;
	}

	public AccumulatedDrugs() {
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method getWithPackage.
	 * @return Packages
	 */
	public Packages getWithPackage() {
		return withPackage;
	}

	/**
	 * Method setWithPackage.
	 * @param withpackage Packages
	 */
	public void setWithPackage(Packages withpackage) {
		this.withPackage = withpackage;
	}

	/**
	 * Method getPillCount.
	 * @return PillCount
	 */
	public PillCount getPillCount() {
		return pillCount;
	}

	/**
	 * Method setPillCount.
	 * @param pillCount PillCount
	 */
	public void setPillCount(PillCount pillCount) {
		this.pillCount = pillCount;
	}

}
