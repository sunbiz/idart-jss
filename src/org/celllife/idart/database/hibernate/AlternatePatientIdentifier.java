package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 */
@Entity
public class AlternatePatientIdentifier {

	@Id
	@GeneratedValue
	private Integer id;
	private String identifier;

	@ManyToOne
	@JoinColumn(name = "patient")
	private Patient patient;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dateChanged;
	
	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}

	private boolean masterPatientID;

	@OneToOne
	@JoinColumn(name = "type_id")
	private IdentifierType type;

	public AlternatePatientIdentifier() {
		super();
	}

	/**
	 * Constructor for AlternatePatientIdentifier.
	 * @param identifier String
	 * @param patient Patient
	 * @param startDate Date
	 * @param masterPatientID boolean
	 */
	public AlternatePatientIdentifier(String identifier, Patient patient,
			Date dateChanged, boolean masterPatientID, IdentifierType type) {
		super();
		this.identifier = identifier;
		this.patient = patient;
		this.dateChanged = dateChanged;
		this.masterPatientID = masterPatientID;
		this.type = type;
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
	 * Method getIdentifier.
	 * @return String
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Method setIdentifier.
	 * @param identifier String
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Method getPatient.
	 * @return Patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Method setPatient.
	 * @param patient Patient
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * Method isMasterPatientID.
	 * @return boolean
	 */
	public boolean isMasterPatientID() {
		return masterPatientID;
	}

	/**
	 * Method setMasterPatientID.
	 * @param wasMasterPatientID boolean
	 */
	public void setMasterPatientID(boolean wasMasterPatientID) {
		this.masterPatientID = wasMasterPatientID;
	}

	public void setType(IdentifierType type) {
		this.type = type;
	}

	public IdentifierType getType() {
		return type;
	}

}
