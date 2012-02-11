package org.celllife.idart.database.hibernate.tmp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "adherencerecordtmp")
public class AdherenceRecord {

	@Id
	@GeneratedValue
	int id;

	int pillCountId = 0;

	Date countDate = null;

	int daysSinceVisit = 0;

	int daysSupplied = 0;

	int daysCarriedOver = 0;

	int daysInHand = 0;

	String adherenceReason = "NOT YET AVAILABLE";

	String cluser = null;

	String pawcNo = "";

	public AdherenceRecord() {
		super();

	}

	/**
	 * Constructor for AdherenceRecord.
	 * @param pillCountId int
	 * @param countDate Date
	 * @param daysSinceVisit int
	 * @param daysSupplied int
	 * @param daysCarriedOver int
	 * @param daysInHand int
	 * @param user String
	 * @param pawcNo String
	 */
	public AdherenceRecord(int pillCountId, Date countDate, int daysSinceVisit,
			int daysSupplied, int daysCarriedOver, int daysInHand, String user,
			String pawcNo) {
		super();
		this.pillCountId = pillCountId;
		this.countDate = countDate;
		this.daysSinceVisit = daysSinceVisit;
		this.daysSupplied = daysSupplied;
		this.daysCarriedOver = daysCarriedOver;
		this.daysInHand = daysInHand;
		this.cluser = user;
		this.pawcNo = pawcNo;

	}

	/**
	 * Method getAdherence.
	 * @return double
	 */
	public double getAdherence() {

		double numerator = (daysSupplied + daysCarriedOver - daysInHand);
		double denominator = daysSinceVisit;
		return (numerator / denominator) * 100.00;
	}

	/**
	 * Method getAdherenceReason.
	 * @return String
	 */
	public String getAdherenceReason() {
		return adherenceReason;
	}

	/**
	 * Method getCountDate.
	 * @return Date
	 */
	public Date getCountDate() {
		return countDate;
	}

	/**
	 * Method getDaysCarriedOver.
	 * @return int
	 */
	public int getDaysCarriedOver() {
		return daysCarriedOver;
	}

	/**
	 * Method getDaysInHand.
	 * @return int
	 */
	public int getDaysInHand() {
		return daysInHand;
	}

	/**
	 * Method getDaysSinceVisit.
	 * @return int
	 */
	public int getDaysSinceVisit() {
		return daysSinceVisit;
	}

	/**
	 * Method getDaysSupplied.
	 * @return int
	 */
	public int getDaysSupplied() {
		return daysSupplied;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getPawcNo.
	 * @return String
	 */
	public String getPawcNo() {
		return pawcNo;
	}

	/**
	 * Method getPillCountId.
	 * @return int
	 */
	public int getPillCountId() {
		return pillCountId;
	}

	/**
	 * Method getCluser.
	 * @return String
	 */
	public String getCluser() {
		return cluser;
	}

	/**
	 * Method setAdherenceReason.
	 * @param adherenceReason String
	 */
	public void setAdherenceReason(String adherenceReason) {
		this.adherenceReason = adherenceReason;
	}

	/**
	 * Method setCountDate.
	 * @param countDate Date
	 */
	public void setCountDate(Date countDate) {
		this.countDate = countDate;
	}

	/**
	 * Method setDaysCarriedOver.
	 * @param daysCarriedOver int
	 */
	public void setDaysCarriedOver(int daysCarriedOver) {
		this.daysCarriedOver = daysCarriedOver;
	}

	/**
	 * Method setDaysInHand.
	 * @param daysInHand int
	 */
	public void setDaysInHand(int daysInHand) {
		this.daysInHand = daysInHand;
	}

	/**
	 * Method setDaysSinceVisit.
	 * @param daysSinceVisit int
	 */
	public void setDaysSinceVisit(int daysSinceVisit) {
		this.daysSinceVisit = daysSinceVisit;
	}

	/**
	 * Method setDaysSupplied.
	 * @param daysSupplied int
	 */
	public void setDaysSupplied(int daysSupplied) {
		this.daysSupplied = daysSupplied;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method setPawcNo.
	 * @param pawcNo String
	 */
	public void setPawcNo(String pawcNo) {
		this.pawcNo = pawcNo;
	}

	/**
	 * Method setPillCountId.
	 * @param pillCountId int
	 */
	public void setPillCountId(int pillCountId) {
		this.pillCountId = pillCountId;
	}

	/**
	 * Method setCluser.
	 * @param user String
	 */
	public void setCluser(String user) {
		this.cluser = user;
	}
}
