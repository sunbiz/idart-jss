package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class StockLevel {

	@Id
	@GeneratedValue
	Integer id;

	@ManyToOne
	@JoinColumn(name = "batch", unique = true)
	Stock batch;

	int fullContainersRemaining;

	int loosePillsRemaining;

	public StockLevel() {
		super();
	}

	/**
	 * Constructor for StockLevel.
	 * @param batch Stock
	 * @param fullContainersRemaining int
	 * @param loosePillsRemaining int
	 */
	public StockLevel(Stock batch, int fullContainersRemaining,
			int loosePillsRemaining) {
		super();
		this.batch = batch;
		this.fullContainersRemaining = fullContainersRemaining;
		this.loosePillsRemaining = loosePillsRemaining;
	}

	/**
	 * Method getBatch.
	 * @return Stock
	 */
	public Stock getBatch() {
		return batch;
	}

	/**
	 * Method setBatch.
	 * @param batch Stock
	 */
	public void setBatch(Stock batch) {
		this.batch = batch;
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
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method getFullContainersRemaining.
	 * @return int
	 */
	public int getFullContainersRemaining() {
		return fullContainersRemaining;
	}

	/**
	 * Method setFullContainersRemaining.
	 * @param fullContainersRemaining int
	 */
	public void setFullContainersRemaining(int fullContainersRemaining) {
		this.fullContainersRemaining = fullContainersRemaining;
	}

	/**
	 * Method getLoosePillsRemaining.
	 * @return int
	 */
	public int getLoosePillsRemaining() {
		return loosePillsRemaining;
	}

	/**
	 * Method setLoosePillsRemaining.
	 * @param loosePillsRemaining int
	 */
	public void setLoosePillsRemaining(int loosePillsRemaining) {
		this.loosePillsRemaining = loosePillsRemaining;
	}

}
