/**
 * 
 */
package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class IdentifierType {

	public IdentifierType() {
		super();
		id = -1;
	}

	@Id
	@GeneratedValue
	private Integer id;
	private String name;
	private int index;
	private boolean voided = false;
	
	public IdentifierType(String name, int index) {
		super();
		this.name = name;
		this.index = index;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setVoided(boolean voided) {
		this.voided = voided;
	}

	public boolean isVoided() {
		return voided;
	}

}
