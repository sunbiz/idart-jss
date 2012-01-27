/**
 * 
 */
package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.celllife.idart.misc.iDARTUtil;

/**
 */
@Entity
public class PatientAttribute implements PatientAttributeInterface{

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "patient")
	private Patient patient;

	private String value;

	@OneToOne
	private AttributeType type;

	/**
	 * @return the type
	 */
	public AttributeType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(AttributeType type) {
		this.type = type;
	}

	public PatientAttribute() {
		super();
		id = -1;
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
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient
	 *            the patient to set
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * Method getObjectValue.
	 * @return Object
	 */
	public Object getObjectValue() {
		return iDARTUtil.parse(type.getDataType(), value);
	}

	/**
	 * Method setObjectValue.
	 * @param v Object
	 */
	public void setObjectValue(Object v){
		this.value = iDARTUtil.toString(type.getDataType(), v);
	}





}
