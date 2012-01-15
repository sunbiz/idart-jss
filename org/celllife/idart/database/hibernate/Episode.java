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
public class Episode {

	public static final String REASON_DECEASED = "Deceased";

	public static final String REASON_NEW_PATIENT = "New Patient";

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "patient", insertable = false, updatable = false)
	private Patient patient;

	@ManyToOne
	@JoinColumn(name = "clinic")
	private Clinic clinic;

	private Date startDate;

	private Date stopDate;

	private String startReason;

	private String stopReason;

	private String startNotes;

	private String stopNotes;

	/**
	 * Constructor for Episode.
	 * 
	 * @param patient
	 *            Patient
	 * @param startDate
	 *            Date
	 * @param stopDate
	 *            Date
	 * @param startReason
	 *            String
	 * @param stopReason
	 *            String
	 * @param startNotes
	 *            String
	 * @param stopNotes
	 *            String
	 * @param clinic
	 */
	public Episode(Patient patient, Date startDate, Date stopDate,
			String startReason, String stopReason, String startNotes,
			String stopNotes, Clinic clinic) {
		super();
		this.patient = patient;
		this.startDate = startDate;
		this.stopDate = stopDate;
		this.startReason = startReason;
		this.stopReason = stopReason;
		this.startNotes = startNotes;
		this.stopNotes = stopNotes;
		this.clinic = clinic;
	}

	/**
	 * Default constructor
	 */
	public Episode() {
		super();
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
	 * Method getStartDate.
	 * @return Date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Method setStartDate.
	 * @param startDate Date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Method getStartNotes.
	 * @return String
	 */
	public String getStartNotes() {
		return startNotes == null ? "" : startNotes;
	}

	/**
	 * Method setStartNotes.
	 * @param startNotes String
	 */
	public void setStartNotes(String startNotes) {
		this.startNotes = startNotes;
	}

	/**
	 * Method getStartReason.
	 * @return String
	 */
	public String getStartReason() {
		return startReason == null ? "" : startReason;
	}

	/**
	 * Method setStartReason.
	 * @param startReason String
	 */
	public void setStartReason(String startReason) {
		this.startReason = startReason;
	}

	/**
	 * Method getStopDate.
	 * @return Date
	 */
	public Date getStopDate() {
		return stopDate;
	}

	/**
	 * Method setStopDate.
	 * @param stopDate Date
	 */
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	/**
	 * Method getStopNotes.
	 * @return String
	 */
	public String getStopNotes() {
		return stopNotes == null ? "" : stopNotes;
	}

	/**
	 * Method setStopNotes.
	 * @param stopNotes String
	 */
	public void setStopNotes(String stopNotes) {
		this.stopNotes = stopNotes;
	}

	/**
	 * Method getStopReason.
	 * @return String
	 */
	public String getStopReason() {
		return stopReason == null ? "" : stopReason;
	}

	/**
	 * Method setStopReason.
	 * @param stopReason String
	 */
	public void setStopReason(String stopReason) {
		this.stopReason = stopReason;
	}

	/**
	 * Method isOpen.
	 * @return boolean
	 */
	public boolean isOpen() {
		return (startDate != null && stopDate == null);
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
	 * Method copy.
	 * @return Episode
	 */
	public Episode copy() {
		Episode copy = new Episode();
		copy.setPatient(this.patient);
		copy.setStartDate(this.startDate);
		copy.setStartNotes(this.startNotes);
		copy.setStartReason(this.startReason);
		copy.setStopDate(this.stopDate);
		copy.setStopNotes(this.stopNotes);
		copy.setStopReason(this.stopReason);
		return copy;
	}

	/**
	 * Method hashCode.
	 * @return int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patient == null) ? 0 : patient.hashCode());
		result = prime * result
		+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
		+ ((startNotes == null) ? 0 : startNotes.hashCode());
		result = prime * result
		+ ((startReason == null) ? 0 : startReason.hashCode());
		result = prime * result
		+ ((stopDate == null) ? 0 : stopDate.hashCode());
		result = prime * result
		+ ((stopNotes == null) ? 0 : stopNotes.hashCode());
		result = prime * result
		+ ((stopReason == null) ? 0 : stopReason.hashCode());
		return result;
	}

	/**
	 * Method equals.
	 * @param obj Object
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Episode other = (Episode) obj;
		if (patient == null) {
			if (other.patient != null)
				return false;
		} else if (!patient.equals(other.patient))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (startNotes == null) {
			if (other.startNotes != null)
				return false;
		} else if (!startNotes.equals(other.startNotes))
			return false;
		if (startReason == null) {
			if (other.startReason != null)
				return false;
		} else if (!startReason.equals(other.startReason))
			return false;
		if (stopDate == null) {
			if (other.stopDate != null)
				return false;
		} else if (!stopDate.equals(other.stopDate))
			return false;
		if (stopNotes == null) {
			if (other.stopNotes != null)
				return false;
		} else if (!stopNotes.equals(other.stopNotes))
			return false;
		if (stopReason == null) {
			if (other.stopReason != null)
				return false;
		} else if (!stopReason.equals(other.stopReason))
			return false;
		if (clinic == null) {
			if (other.clinic != null)
				return false;
		} else if (!clinic.getClinicName().equals(other.clinic.getClinicName()))
			return false;
		return true;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Clinic getClinic() {
		return clinic;
	}

}
