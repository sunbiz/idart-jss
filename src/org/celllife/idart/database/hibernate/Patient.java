/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.celllife.idart.database.hibernate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import model.manager.AdministrationManager;
import model.manager.PatientManager;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.misc.DateFieldComparator;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;

/**
 */
/**
 * @author Simon
 *
 */
@Entity
public class Patient {

	@Id
	@GeneratedValue
	private Integer id;

	private Boolean accountStatus;
	private String address1;
	private String address2;
	private String address3;
	private String cellphone;
	private Date dateOfBirth;

	@ManyToOne
	@JoinColumn(name = "clinic")
	private Clinic clinic;
	private String nextOfKinName;
	private String nextOfKinPhone;
	private String firstNames;
	private String homePhone;
	private String lastname;
	private char modified;

	@Column(unique = true, nullable = false)
	private String patientId;

	@OneToMany(mappedBy = "patient")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<Prescription> prescriptions;

	@OneToMany(mappedBy = "patient")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<Pregnancy> pregnancies;

	@OneToMany(mappedBy = "patient")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<PatientAttribute> attributes;

	private String province;

	private char sex;

	private String workPhone;

	private String race;

	@OneToMany
	@JoinColumn(name = "patient")
	@IndexColumn(name = "index")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<Episode> episodes;

	@OneToMany(mappedBy = "patient")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<Appointment> appointments;

	@OneToMany(mappedBy = "patient")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<AlternatePatientIdentifier> alternateIdentifiers;
	
	@OneToMany(mappedBy = "patient")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private Set<PatientIdentifier> patientIdentifiers;


	public Patient() {
		super();
		this.id = -1;
	}

	/**
	 * Method getAccountStatus.
	 * 
	 * @return Boolean
	 * @deprecated use use getAccountStatusWithCheck
	 */
	@Deprecated
	public Boolean getAccountStatus() {
		return accountStatus;
	}

	/**
	 * Method getAccountStatus.
	 * 
	 */
	public Boolean getAccountStatusWithCheck() {
		accountStatusCheck();
		return accountStatus;
	}

	/**
	 * Method getAddress1.
	 * 
	 * @return String
	 */
	public String getAddress1() {
		if (address1 == null)
			return "";
		return address1;
	}

	/**
	 * Method getAddress2.
	 * 
	 * @return String
	 */
	public String getAddress2() {
		if (address2 == null)
			return "";
		return address2;
	}

	/**
	 * Method getAddress3.
	 * 
	 * @return String
	 */
	public String getAddress3() {
		if (address3 == null)
			return "";
		return address3;
	}

	/**
	 * Method to concatenate the address fields into a single address
	 * 
	 * @return
	 */
	public String getFullAddress() {
		return ((address1 == null || "".equals(address1)) ? "" : address1)
				+ ((address2 == null || "".equals(address2)) ? "" : "; "
						+ address2)
				+ ((address3 == null || "".equals(address3)) ? "" : "; "
						+ address3);
	}

	/**
	 * Method getAge.
	 * 
	 * @return int
	 */
	public int getAge() {
		return getAgeAt(null);
	}

	public int getAgeAt(Date date) {
		return iDARTUtil.getAgeAt(getDateOfBirth() == null ? new Date()
				: getDateOfBirth(), date);
	}

	/**
	 * Method getAppointments.
	 * 
	 * @return Set<Appointment>
	 */
	public Set<Appointment> getAppointments() {

		if (appointments == null) {
			appointments = new HashSet<Appointment>();
		}
		return appointments;
	}

	/**
	 * Method getCellphone.
	 * 
	 * @return String
	 */
	public String getCellphone() {
		if (cellphone == null)
			return "";
		return cellphone;
	}

	/**
	 * Method getClinic.
	 * 
	 * @return Clinic
	 */
	public Clinic getCurrentClinic() {
		return clinic;
	}

	/**
	 * Method getDateOfBirth.
	 * 
	 * @return Date
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Method getFirstNames.
	 * 
	 * @return String
	 */
	public String getFirstNames() {
		if (firstNames == null)
			return "";
		return firstNames;
	}

	/**
	 * Method getHomePhone.
	 * 
	 * @return String
	 */
	public String getHomePhone() {
		if (homePhone == null)
			return "";
		return homePhone;
	}

	/**
	 * Method getId.
	 * 
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getLastname.
	 * 
	 * @return String
	 */
	public String getLastname() {
		if (lastname == null)
			return "";
		return lastname;
	}

	/**
	 * Method getModified.
	 * 
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method getNextOfKinName.
	 * 
	 * @return String
	 */
	public String getNextOfKinName() {
		if (nextOfKinName == null)
			return "";
		return nextOfKinName;
	}

	/**
	 * Method getNextOfKinPhone.
	 * 
	 * @return String
	 */
	public String getNextOfKinPhone() {
		if (nextOfKinPhone == null)
			return "";
		return nextOfKinPhone;
	}

	/**
	 * Method getPatientId.
	 * 
	 * @return String
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * Method getPrescriptions.
	 * 
	 * @return Set<Prescription>
	 */
	public Set<Prescription> getPrescriptions() {
		if (prescriptions == null) {
			prescriptions = new HashSet<Prescription>();
		}
		return prescriptions;
	}

	/**
	 * Method getProvince.
	 * 
	 * @return String
	 */
	public String getProvince() {
		if (province == null)
			return "";
		return province;
	}

	/**
	 * Method getRace.
	 * 
	 * @return String
	 */
	public String getRace() {
		if (race == null)
			return "";
		return race;
	}

	/**
	 * Method getSex.
	 * 
	 * @return char
	 */
	public char getSex() {
		return sex;
	}

	/**
	 * Method getWorkPhone.
	 * 
	 * @return String
	 */
	public String getWorkPhone() {
		if (workPhone == null)
			return "";
		return workPhone;
	}

	/**
	 * Method getAlternateIdentifiers.
	 * 
	 * @return Set<AlternatePatientIdentifier>
	 */
	public Set<AlternatePatientIdentifier> getAlternateIdentifiers() {
		if (alternateIdentifiers == null) {
			alternateIdentifiers = new HashSet<AlternatePatientIdentifier>();
		}

		return alternateIdentifiers;
	}
	
	public Set<PatientIdentifier> getPatientIdentifiers() {
		if (patientIdentifiers == null) {
			patientIdentifiers = new HashSet<PatientIdentifier>();
		}

		return patientIdentifiers;
	}

	/**
	 * Method getEpisodes.
	 * 
	 * @return List<Episode>
	 */
	public List<Episode> getEpisodes() {
		if (episodes == null) {
			episodes = new ArrayList<Episode>();
		}

		return episodes;
	}

	/**
	 * Method setAccountStatus.
	 * 
	 * @param accountStatus
	 *            Boolean
	 */
	public void setAccountStatus(Boolean accountStatus) {
		this.accountStatus = accountStatus;
	}

	public void accountStatusCheck() {
		Episode mostRecentEpisode = getMostRecentEpisode();
		if (mostRecentEpisode != null) {
			accountStatus = mostRecentEpisode.isOpen();
		} else {
			// if patient has no episodes set accountStatus = false
			accountStatus = false;
		}
	}

	/**
	 * Method setAddress1.
	 * 
	 * @param address1
	 *            String
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * Method setAddress2.
	 * 
	 * @param address2
	 *            String
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * Method setAddress3.
	 * 
	 * @param address3
	 *            String
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	/**
	 * Method setAppointments.
	 * 
	 * @param appointments
	 *            Set<Appointment>
	 */
	public void setAppointments(Set<Appointment> appointments) {
		this.appointments = appointments;
	}

	/**
	 * Method setCellphone.
	 * 
	 * @param cellphone
	 *            String
	 */
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	/**
	 * Method setClinic.
	 * 
	 * @param clinic
	 *            Clinic
	 */
	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	/**
	 * Method setDateOfBirth.
	 * 
	 * @param dateOfBirth
	 *            Date
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * Method setFirstNames.
	 * 
	 * @param firstNames
	 *            String
	 */
	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}

	/**
	 * Method setHomePhone.
	 * 
	 * @param homePhone
	 *            String
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * Method setId.
	 * 
	 * @param id
	 *            int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setLastname.
	 * 
	 * @param lastname
	 *            String
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Method setModified.
	 * 
	 * @param modified
	 *            char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setNextOfKinName.
	 * 
	 * @param nextOfKinName
	 *            String
	 */
	public void setNextOfKinName(String nextOfKinName) {
		this.nextOfKinName = nextOfKinName;
	}

	/**
	 * Method setNextOfKinPhone.
	 * 
	 * @param nextOfKinPhone
	 *            String
	 */
	public void setNextOfKinPhone(String nextOfKinPhone) {
		this.nextOfKinPhone = nextOfKinPhone;
	}

	/**
	 * Method setPatientId.
	 * 
	 * @param patientId
	 *            String
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * Method setPrescriptions.
	 * 
	 * @param prescriptions
	 *            Set<Prescription>
	 */
	public void setPrescriptions(Set<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	/**
	 * Method setProvince.
	 * 
	 * @param province
	 *            String
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * Method setRace.
	 * 
	 * @param race
	 *            String
	 */
	public void setRace(String race) {
		this.race = race;
	}

	/**
	 * Method setSex.
	 * 
	 * @param sex
	 *            char
	 */
	public void setSex(char sex) {
		this.sex = sex;
	}

	/**
	 * Method setWorkPhone.
	 * 
	 * @param workPhone
	 *            String
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * Method setAlternateIdentifiers.
	 * 
	 * @param alternateIdentifiers
	 *            Set<AlternatePatientIdentifier>
	 */
	public void setAlternateIdentifiers(
			Set<AlternatePatientIdentifier> alternateIdentifiers) {
		for (AlternatePatientIdentifier altId : alternateIdentifiers) {
			altId.setPatient(this);
		}
		this.alternateIdentifiers = alternateIdentifiers;
	}
	
	public void setPatientIdentifiers(Set<PatientIdentifier> patientIdentifiers) {
		for (PatientIdentifier pid : patientIdentifiers) {
			pid.setPatient(this);
		}
		this.patientIdentifiers = patientIdentifiers;
	}

	/**
	 * Method setPregnancies.
	 * 
	 * @param pregnancies
	 *            Set<Pregnancy>
	 */
	public void setPregnancies(Set<Pregnancy> pregnancies) {
		for (Pregnancy pregnancy : pregnancies) {
			pregnancy.setPatient(this);
		}
		this.pregnancies = pregnancies;
	}

	/**
	 * Method setEpisodes.
	 * 
	 * @param episodes
	 *            List<Episode>
	 */
	public void setEpisodes(List<Episode> episodes) {
		for (Episode episode : episodes) {
			episode.setPatient(this);
		}
		this.episodes = episodes;
	}

	/**
	 * Method hasPreviousEpisodes.
	 * 
	 * @return boolean
	 */
	public boolean hasPreviousEpisodes() {
		boolean prevEpi = PatientManager.hasPreviousEpisodes(this);
		return prevEpi;
	}

	/**
	 * @return the attributes
	 */
	public Set<PatientAttribute> getAttributes() {
		if (attributes == null) {
			attributes = new HashSet<PatientAttribute>();
		}
		return attributes;
	}

	/**
	 * @param attibutes
	 *            the attibutes to set
	 */
	public void setAttributes(Set<PatientAttribute> attibutes) {
		for (PatientAttribute pa : attibutes) {
			pa.setPatient(this);
		}
		
		this.attributes = attibutes;
	}

	/**
	 * <table>
	 * <tr>
	 * <td width = 350>
	 * <p align='justify'>
	 * Add OR Update an attribute of a Patient. Specify the<b> type name as a
	 * <font color=orange>String</font></b>. Specify the <b>attribute value as
	 * an <font color=orange>object</font></b>. If attribute type not present in
	 * patient, one is created. If type is present, just its value is changed to
	 * the object passed.
	 * </tr>
	 * </td>
	 * </table>
	 * 
	 * @param typeName
	 * @param value
	 */
	public void setAttributeValue(String typeName, Object value) {
		PatientAttribute pa = getAttributeByName(typeName);
		String valueString = iDARTUtil.toString(value.getClass(), value);
		if (pa != null) {
			pa.setValue(valueString);
		} else {
			Session sess = HibernateUtil.getNewSession();
			PatientAttribute patt = new PatientAttribute();
			patt.setPatient(this);
			AttributeType at = PatientManager.getAttributeTypeObject(sess,
					typeName);
			if (at != null) {
				patt.setType(at);
				patt.setValue(valueString);
				getAttributes().add(patt);
			} else {
				// Message that attribute type not in database...
				// In the case of ARV Start Date it is.
			}
		}
	}
	
	public void setPatientAttribute(PatientAttribute at){
		if (getAttributeByName(at.getType().getName()) == null){
			at.setPatient(this);
			getAttributes().add(at);
		}
	}

	/**
	 * 
	 * Get a patient attribute by passing a attribute type name which a patient
	 * might have. Null is returned when one is not found.
	 * 
	 * @param attTypeName
	 * @return PatientAttribute
	 */
	public PatientAttribute getAttributeByName(String attTypeName) {
		PatientAttribute attr = null;
		for (PatientAttribute pa : getAttributes()) {
			if (pa.getType().getName().equals(attTypeName)) {
				attr = pa;
				break;
			}
		}
		return attr;
	}

	/**
	 * 
	 * Remove a patient attribute by specifying a attribute type name. If
	 * attribute not found, method returns FALSE.
	 * 
	 * @param typeName
	 * @return boolean
	 */
	public boolean removePatientAttribute(String typeName) {
		boolean found = false;
		PatientAttribute patt = getAttributeByName(typeName);
		if (patt != null) {
			found = true;
			getAttributes().remove(patt);
		}
		return found;
	}

	public String episodeDetails() {
		String result = "";
		for (Episode ep : episodes) {
			result += ep.getStartReason()
					+ " on "
					+ new SimpleDateFormat("dd MMM yy").format(ep
							.getStartDate());

			if (ep.getStopDate() != null) {
				result += " -> "
						+ ep.getStopReason()
						+ " on "
						+ new SimpleDateFormat("dd MMM yy").format(ep
								.getStopDate());
			}

			result += "; ";
		}

		if (result.length() > 1) {
			result = result.substring(0, result.length() - 2);
		}
		return result;

	}

	/**
	 * 
	 * @return
	 */
	public Set<Pregnancy> getPregnancies() {
		if (pregnancies == null) {
			pregnancies = new HashSet<Pregnancy>();
		}
		return pregnancies;
	}

	/**
	 * Returns the patients most recent episode or null if the patient has no
	 * episodes.
	 * 
	 * @return most recent episode or null
	 */
	public Episode getMostRecentEpisode() {
		if (getEpisodes().size() > 0)
			return episodes.get(episodes.size() - 1);
		else
			return null;
	}

	public boolean isPregnantAtDate(Date date) {
		for (Pregnancy preg : pregnancies) {
			if (preg.dateFallsInPregnancy(date))
				return true;
		}
		return false;
	}

	public boolean isFemale() {
		return sex == 'F';
	}

	public boolean isMale() {
		return sex == 'M';
	}

	public String getEpisdodeStartReasonInPeriod(Date startDate, Date endDate) {
		for (Episode ep : episodes) {
			Date date = ep.getStartDate();
			if (iDARTUtil.before(startDate, date)
					&& iDARTUtil.after(endDate, date))
				return ep.getStartReason();
		}
		return "";
	}

	/**
	 * This method gets a clinic that a patient belonged to at a specific date
	 * in time
	 * 
	 * @param date
	 * @return
	 */
	public Clinic getClinicAtDate(Date date) {
		Episode ep = getEpisodeAtDate(date);
		if (ep == null)
			return null;
		else
			return ep.getClinic();

	}

	/**
	 * This method gets an episode at a specific date in time
	 * 
	 * @param date
	 * @return
	 */
	public Episode getEpisodeAtDate(Date date) {
		// reverse list so that we always consider later episodes first (for
		// cases where two episodes match, the more recent one will be used)
		List<Episode> episodeTmp = new ArrayList<Episode>();
		episodeTmp.addAll(episodes);
		Collections.reverse(episodeTmp);
		for (Episode ep : episodeTmp) {
			Date startDate = ep.getStartDate();
			Date endDate = ep.getStopDate();
			if (DateFieldComparator.compare(startDate, date,
					Calendar.DAY_OF_MONTH) <= 0
					&& (endDate == null || DateFieldComparator.compare(endDate,
							date, Calendar.DAY_OF_MONTH) >= 0))
				return ep;
		}
		return null;
	}

	public void updateClinic() {
		Episode episode = getMostRecentEpisode();
		if (episode != null) {
			setClinic(episode.getClinic());
		} else {
			setClinic(AdministrationManager.getMainClinic(HibernateUtil
					.getNewSession()));
		}
	}

	public Prescription getCurrentPrescription() {
		Prescription result = null;
		for (Prescription p : prescriptions) {
			if (p.getCurrent() == 'T') {
				result = p;
				break;
			}
		}
		return result;
	}

	public Prescription getMostRecentPrescription() {
		Prescription mostRecent = null;
		for (Prescription script : prescriptions) {
			if (mostRecent == null) {
				mostRecent = script;
			} else if (iDARTUtil.after(script.getDate(), mostRecent.getDate())) {
				mostRecent = script;
			}
		}
		return mostRecent;
	}

	public PatientIdentifier getPreferredIdentifier() {
		List<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>(getPatientIdentifiers());
		Collections.sort(identifiers, new Comparator<PatientIdentifier>() {
			@Override
			public int compare(PatientIdentifier o1, PatientIdentifier o2) {
				return o1.getType().getIndex() - o2.getType().getIndex();
			}
		});
		return identifiers.get(0);
	}
	
	/**
	 * Returns the identifier of a particular type or null if the patient does not
	 * have an identifier of that type.
	 * @param type
	 * @return
	 */
	public PatientIdentifier getIdentifier(IdentifierType type){
		if (type == null){
			return null;
		}
		for (PatientIdentifier pid : getPatientIdentifiers()) {
			if (pid.getType().getId() == type.getId())
				return pid;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getFirstNames() + " " + getLastname();
	}
}
