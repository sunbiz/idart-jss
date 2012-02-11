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
import javax.persistence.Transient;

/**
 */
@Entity
public class PatientIdentifier {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;

	private String value;

	@OneToOne
	@JoinColumn(name = "type_id")
	private IdentifierType type;
	
	@Transient
	private String valueEdit;

	public PatientIdentifier() {
		super();
		id = -1;
	}
	
	public PatientIdentifier(Patient patient, String value, IdentifierType type) {
		super();
		this.patient = patient;
		this.value = value;
		this.type = type;
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
	 * @return the type
	 */
	public IdentifierType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(IdentifierType type) {
		this.type = type;
	}
	
	public String getValueEdit() {
		return valueEdit;
	}
	
	public void setValueEdit(String valueEdit) {
		this.valueEdit = valueEdit;
	}
}
