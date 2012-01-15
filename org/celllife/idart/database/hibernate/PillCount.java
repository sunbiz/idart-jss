package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class PillCount {

	@Id
	@GeneratedValue
	private Integer id;

	private int accum;

	@ManyToOne
	@JoinColumn(name = "previousPackage")
	private Packages previousPackage;

	private Date dateOfCount;

	@ManyToOne
	@JoinColumn(name = "drug")
	private Drug drug;

	public PillCount() {
	}

	/**
	 * @param accum
	 * @param previouspackage
	 * @param dateofcount
	 * @param drug
	 */
	public PillCount(int accum, Packages previouspackage, Date dateofcount,
			Drug drug) {
		super();
		this.accum = accum;
		this.previousPackage = previouspackage;

		this.dateOfCount = dateofcount;
		this.drug = drug;
	}

	/**
	 * Method getAccum.
	 * @return int
	 */
	public int getAccum() {
		return accum;
	}

	/**
	 * Method setAccum.
	 * @param accum int
	 */
	public void setAccum(int accum) {
		this.accum = accum;
	}

	/**
	 * Method getDateOfCount.
	 * @return Date
	 */
	public Date getDateOfCount() {
		return dateOfCount;
	}

	/**
	 * Method setDateOfCount.
	 * @param dateofcount Date
	 */
	public void setDateOfCount(Date dateofcount) {
		this.dateOfCount = (Date)dateofcount.clone();
	}

	/**
	 * Method getDrug.
	 * @return Drug
	 */
	public Drug getDrug() {
		return drug;
	}

	/**
	 * Method setDrug.
	 * @param drug Drug
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
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
	 * Method getPreviousPackage.
	 * @return Packages
	 */
	public Packages getPreviousPackage() {
		return previousPackage;
	}

	/**
	 * Method setPreviousPackage.
	 * @param previouspackage Packages
	 */
	public void setPreviousPackage(Packages previouspackage) {
		this.previousPackage = previouspackage;
	}

}
