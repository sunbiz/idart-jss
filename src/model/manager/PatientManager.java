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

package model.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import model.manager.exports.iedea.ArtDto;
import model.nonPersistent.PatientIdAndName;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.Logging;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.Pregnancy;
import org.celllife.idart.database.hibernate.Prescription;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

/**
 */
public class PatientManager {

	private static Logger log = Logger.getLogger(PatientManager.class);

	/**
	 * Method addAttributeTypeToDatabase.
	 * 
	 * @param sess
	 *            Session
	 * @param type
	 *            Class<?>
	 * @param name
	 *            String
	 * @param description
	 *            String
	 * @return 
	 */
	public static AttributeType addAttributeTypeToDatabase(Session sess, Class<?> type,
			String name, String description) {
		AttributeType attype = new AttributeType();
		attype.setDataType(type);
		attype.setDescription(description);
		attype.setName(name);
		sess.save(attype);
		return attype;
	}

	/**
	 * Adds an episode to a Patient's episode set if it doesn't already exist in
	 * the set.
	 * 
	 * @param patient
	 * @param episode
	 */
	public static void addEpisodeToPatient(Patient patient, Episode episode) {
		List<Episode> episodes = patient.getEpisodes();
		episode.setPatient(patient);
		if (!episodes.contains(episode)) {
			episodes.add(episode);
		}
	}

	/**
	 * Add a PatientAttribute to a patient.
	 * 
	 * @param sess
	 * @param patt
	 * @throws HibernateException
	 */
	public static boolean addPatientAttributeToPatient(Session sess,
			Patient patient, PatientAttribute pa) throws HibernateException {
		patient.setAttributeValue(pa.getType().getName(), pa.getValue());
		if (patient.getId() > 0) { // Only save a patient that has already been
			// created.
			savePatient(sess, patient);
			return true;
		}
		return false;
	}

	/**
	 * Method calculateQtyToDispense.
	 * 
	 * @param apt
	 *            double
	 * @param tpd
	 *            int
	 * @param packSize
	 *            int
	 * @return int
	 */
	@SuppressWarnings("cast")
	public static int calculateQtyToDispense(double apt, int tpd, int packSize) {

		double qtyToDispense = 0;

		double amountPerTime = apt;
		double timesPerDay = tpd;

		// Assume there are 30 days in a month
		qtyToDispense = amountPerTime * timesPerDay * 30;
		qtyToDispense = Math.ceil((double) qtyToDispense / (double) packSize);
		return (int) qtyToDispense;

	}

	/**
	 * Changes the scheduled appointment date if the patient 
	 * has an appointment. If not, create a new appointment
	 * 
	 * @param session
	 * @param thePatient
	 * @param appointmentDate
	 * @throws HibernateException
	 */
	public static void setNextAppointmentDate(Session session,
			Patient thePatient, Date appointmentDate) throws HibernateException {
		
		Appointment lastestApp = getLatestAppointmentForPatient(thePatient, true);

		if (lastestApp != null) {
			// appointment date was changed
			lastestApp.setAppointmentDate(appointmentDate);
		
		}
		else {
			thePatient.getAppointments().add(
					getNewAppointmentForPatient(session, thePatient, appointmentDate));
		}
		
	}

	/**
	 * Sets the visit date for the current appointment and 
	 * then creates a new appointment.
	 * 
	 * @param session
	 * @param thePatient
	 * @param visitDate
	 *            the date of the patient visit
	 * @param theAppDate
	 *            the date of the next appointment
	 * @throws HibernateException
	 */
	public static void setNextAppointmentDateAtVisit(Session session,
			Patient thePatient, Date visitDate, Date theAppDate)
			throws HibernateException {
		
		Appointment lastestApp = getLatestAppointmentForPatient(thePatient, true);

		if (lastestApp != null) {
			// Patient visited clinic
			lastestApp.setVisitDate(visitDate);
			
		}

		thePatient.getAppointments().add(
				getNewAppointmentForPatient(session, thePatient, theAppDate));
	}
	
	public static void setVisitDateOnly(Patient patient, Date visitDate) throws HibernateException {
		
		Appointment lastestApp = getLatestAppointmentForPatient(patient, true);

		if (lastestApp != null) {
			// Patient visited clinic
			lastestApp.setVisitDate(visitDate);
		}
	}

	/**
	 * Goes through all patients that are preganant and checks that the confirm
	 * date is < 9 months ago. If not, the patient is unlikely to still be
	 * pregnant, and pregnant is set to false
	 * 
	 * @param sess
	 *            Session
	 * @throws HibernateException
	 */
	public static void checkPregnancies(Session sess) throws HibernateException

	{
		List<Pregnancy> pregnancyList = PatientManager
				.getAllCurrentPregnancies(sess);
		Iterator<Pregnancy> it = pregnancyList.iterator();
		while (it.hasNext()) {
			Pregnancy p = it.next();
			GregorianCalendar cal = new GregorianCalendar();
			if (p.getConfirmDate() != null) {
				cal.setTime(p.getConfirmDate());
				cal.add(Calendar.MONTH, 9);

				GregorianCalendar calNow = new GregorianCalendar();
				calNow.setTime(new Date());

				if (cal.before(calNow)) // pregnancy was confirmed more that 9
				// months ago
				{
					p.setEndDate(new Date());
				}
			}
		}
	}

	/**
	 * Method getAllAttributeTypes.
	 * 
	 * @param sess
	 *            Session
	 * @return List<AttributeType>
	 */
	public static List<AttributeType> getAllAttributeTypes(Session sess) {
		@SuppressWarnings("unchecked")
		List<AttributeType> aTypeLst = sess.createQuery("from AttributeType")
				.list();
		return aTypeLst;
	}
	
	public static List<IdentifierType> getAllIdentifierTypes(Session sess) {
		@SuppressWarnings("unchecked")
		List<IdentifierType> aTypeLst = sess.createQuery("from IdentifierType")
				.list();
		return aTypeLst;
	}

	/**
	 * Returns all pregnacies
	 * 
	 * @param sess
	 *            Session
	 * @return List<Pregnancy> &#064;throws HibernateException
	 * 
	 */
	public static List<Pregnancy> getAllCurrentPregnancies(Session sess)
			throws HibernateException {
		@SuppressWarnings("unchecked")
		List<Pregnancy> result = sess.createQuery(
				"from Pregnancy as p where p.endDate is null").list();
		return result;
	}

	/**
	 * Method getAllPatients.
	 * 
	 * @param session
	 *            Session
	 * @param c
	 *            Clinic
	 * @return List<Patient>
	 * @throws HibernateException
	 */
	public static List<Patient> getAllPatients(Session session, Clinic c)
			throws HibernateException {
		@SuppressWarnings("unchecked")
		List<Patient> patients = session
				.createQuery(
						"select patient from Patient as patient where patient.clinic.id = :clinicId")
				.setInteger("clinicId", c.getId()).list();
		return patients;
	}

	/**
	 * Method getAllPatientsWithScripts.
	 * 
	 * @param sess
	 *            Session
	 * @param clinicId
	 *            int
	 * @return List<PatientIdAndName>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> getAllPatientsWithScripts(
			Session sess, int clinicId) throws HibernateException {
		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();

		List<Object[]> result = sess
				.createQuery(
						"select pre.id, pre.patient.patientId, pre.patient.firstNames, pre.patient.lastname "
								+ "from Prescription pre"
								+ " where pre.patient.accountStatus=true"
								+ " and pre.current = 'T' and "
								+ "pre.patient.clinic.id =:clinicId")
				.setInteger("clinicId", clinicId).list();

		if (result != null) {
			for (Object[] obj : result) {

				returnList.add(new PatientIdAndName((Integer) obj[0], (String) obj[1],
						(String) obj[3] + ", " + (String) obj[2]));
			}
		}
		return returnList;
	}

	/**
	 * Method getAttributeTypeObject.
	 * 
	 * @param sess
	 *            Session
	 * @param name
	 *            String
	 * @return AttributeType
	 */
	public static AttributeType getAttributeTypeObject(Session sess, String name) {
		String SQL = "select atype from AttributeType as atype where atype.name = :name";
		AttributeType attr = null;
		@SuppressWarnings("unchecked")
		List<AttributeType> myList = sess.createQuery(SQL)
				.setString("name", name).list();
		if (myList.size() > 0) {
			AttributeType at = myList.get(0);
			attr = at;
		}
		return attr;
	}

	/**
	 * Returns the current pregnancy for a Patient
	 * 
	 * @param sess
	 *            Session
	 * @param patientId
	 *            int
	 * @return Pregnancy
	 * @throws HibernateException
	 */
	public static Pregnancy getCurrentPregnancy(Session sess, int patientId)
			throws HibernateException {
		Pregnancy result = (Pregnancy) sess
				.createQuery(
						"from Pregnancy as p where p.patient.id=:patid and p.endDate is null order by p.confirmDate desc")
				.setInteger("patid", patientId).uniqueResult();
		return result;
	}

	/**
	 * Method getDrugsInPackageToDelete.
	 * 
	 * @param session
	 *            Session
	 * @param thePackage
	 *            Packages
	 * @return List<Object[]>
	 * @throws HibernateException
	 */
	public static List<Object[]> getDrugsInPackageToDelete(Session session,
			Packages thePackage) throws HibernateException {
		@SuppressWarnings("unchecked")
		List<Object[]> drugsInPackage = session
				.createQuery(
						"select d.name, pd.amount, s.batchNumber, pd.id "
								+ "from Drug as d, PackagedDrugs as pd, Stock as s "
								+ "where pd.parentPackage = :thePackageId "
								+ "and pd.stock = s.id " + "and s.drug = d.id")
				.setInteger("thePackageId", thePackage.getId()).list();

		return drugsInPackage;

	}

	/**
	 * Return a patients first Episode or a new Episode if the patient does not
	 * have any Episodes.
	 * 
	 * @param patient
	 * @return first Episode for patient p
	 */
	public static Episode getFirstEpisode(Patient patient) {
		List<Episode> episodes = patient.getEpisodes();
		if (episodes == null || episodes.size() == 0)
			return new Episode();
		return episodes.get(0);
	}

	/**
	 * Method getLastPackageOnScript.
	 * 
	 * @param pre
	 *            Prescription
	 * @return Packages
	 */
	public static Packages getLastPackageOnScript(Prescription pre) {
		Packages pack = null;
		Date latestDate = new Date(0);
		Set<Packages> packages = pre.getPackages();

		for (Packages p : packages) {
			if (p.getPickupDate().after(latestDate)) {
				latestDate = p.getPickupDate();
				pack = p;
			}
		}

		return pack;
	}

	/***************************************************************************
	 * 
	 * This method finds the last package the patient picked up
	 * 
	 * @param pat
	 *            Patient
	 * @return Packages
	 */

	public static Packages getLastPackagePickedUp(Patient pat) {

		Packages pack = null;
		// fist, is there a package picked up on the current prescription?
		pack = getLastPackageOnScript(pat.getCurrentPrescription());

		// if not, look at all previous prescriptions
		if (pack == null) {
			Date latestDate = new Date(0);
			Iterator<Prescription> preItr = pat.getPrescriptions().iterator();

			while (preItr.hasNext()) {
				Packages p = getLastPackageOnScript(preItr.next());
				if ((p != null) && (p.getPickupDate().after(latestDate))) {
					latestDate = p.getPickupDate();
					pack = p;
				}
			}

		}

		return pack;

	}

	/**
	 * Return a patients most recent Episode or a new Episode if the patient
	 * does not have any Episodes.
	 * 
	 * @param patient
	 * @return most recent Episode for patient patient
	 */
	public static Episode getMostRecentEpisode(Patient patient) {
		Episode episode = patient.getMostRecentEpisode();
		if (episode == null)
			return new Episode();
		return episode;
	}

	/**
	 * Return the date on which a patient was marked or null if does not have
	 * any Episodes or not marked as such.
	 * 
	 * @param patient
	 * @return date
	 */
	public static Date getLastReasonOccurrence(Patient patient, String reason,
			boolean startNotStop) {
		List<Episode> episodes = patient.getEpisodes();
		if (episodes == null || episodes.size() == 0)
			return null;
		Date d = null;
		if (startNotStop) {
			for (int i = 0; i < (episodes.size() - 1); i++) {
				Episode e = episodes.get(i);
				if (e.getStartReason().equalsIgnoreCase(reason)) {
					d = e.getStartDate();
				}
			}
		} else {
			for (int i = 0; i < (episodes.size() - 1); i++) {
				Episode e = episodes.get(i);
				if (e.getStopReason().equalsIgnoreCase(reason)) {
					d = e.getStopDate();
				}
			}
		}
		return d;
	}

	// METHODS FOR APPOINTMENT MANAGER -----------------------------------------
	/**
	 * Creates a new appointment for this paitent, setting it to the given date
	 * 
	 * @param sess
	 * @param thePatient
	 * @param theDate
	 * @return Appointment
	 * @throws HibernateException
	 */
	private static Appointment getNewAppointmentForPatient(Session sess,
			Patient thePatient, Date theDate) throws HibernateException {
		Appointment theApp = new Appointment();
		theApp.setAppointmentDate(theDate);
		theApp.setPatient(thePatient);
		return theApp;
	}

	/**
	 * Method getNoOfEpisodes.
	 * 
	 * @param p
	 *            Patient
	 * @return int
	 */
	public static int getNoOfEpisodes(Patient p) {
		return p.getEpisodes().size();
	}

	/**
	 * Method getPatient.
	 * 
	 * @param session
	 *            Session
	 * @param patientId
	 *            int
	 * @return Patient
	 * @throws HibernateException
	 */
	public static Patient getPatient(Session session, int patientId)
			throws HibernateException {
		Patient pat = null;
		pat = (Patient) session
				.createQuery(
						"select patient from Patient as patient where patient.id = :patientId")
				.setInteger("patientId", patientId).setMaxResults(1)
				.uniqueResult();
		return pat;
	}

	/**
	 * Returns a patient using the patientId
	 * 
	 * @param session
	 *            Session
	 * @param patientId
	 * @return Patient
	 * @throws HibernateException
	 */
	public static Patient getPatient(Session session, String patientId)
			throws HibernateException {
		Patient pat = (Patient) session
				.createQuery(
						"select patient from Patient as patient where upper(patient.patientId) = ?")
				.setString(0, patientId.toUpperCase()).setMaxResults(1).uniqueResult();
		return pat;
	}
	
	/**
	 * 
	 * Obtaining a list of PatientAttribute for patient iD
	 * 
	 * @param sess
	 * @param patientId
	 * @return List<PatientAttribute>
	 * @throws HibernateException
	 */
	public static List<PatientAttribute> getPatientAttributes(Session sess,
			int patientId) throws HibernateException {
		String sql = "select patt  from PatientAttribute as patt "
				+ " where patt.patient.id = :patientId";
		@SuppressWarnings("unchecked")
		List<PatientAttribute> patt = sess.createQuery(sql)
				.setInteger("patientId", patientId).list();
		return patt;
	}

	public static List<Patient> getPatientsByAltId(Session session,
			IdentifierType type, String patientId) throws HibernateException {
		String queryString = "select alt.patient from AlternatePatientIdentifier as alt " +
						"where alt.identifier = :identifier ";
		
		if (type != null)
			queryString +=  "and alt.type = :type";
		
		Query query = session.createQuery(queryString);
		query.setParameter("identifier", patientId);
		
		if (type != null)
			query.setParameter("type", type).list();
		
		@SuppressWarnings("unchecked")
		List<Patient> patients = query.list();
		return patients;
	}

	public static Appointment getLatestAppointmentForPatient(Patient pat, boolean active) {
		Appointment returnApp = null;
		for (Appointment app : pat.getAppointments()) {
			if (active) {
				if (app.isActive()) {
					if ((returnApp == null)
							|| ((app.getAppointmentDate() != null && returnApp
									.getAppointmentDate() != null) && (app
									.getAppointmentDate().after(returnApp
									.getAppointmentDate())))) {
						returnApp = app;
					}
				}
			}
			else {
				if ((returnApp == null)
						|| ((app.getAppointmentDate() != null && returnApp
								.getAppointmentDate() != null) && (app
								.getAppointmentDate().after(returnApp
								.getAppointmentDate())))) {
					returnApp = app;
				}
			}
		}
		return returnApp;
	}

	/**
	 * Returns true only a patient has one or more closed episodes
	 * 
	 * @param p
	 * @return boolean
	 */

	public static boolean hasPreviousEpisodes(Patient p) {
		List<Episode> episodes = p.getEpisodes();
		boolean isMostRecentOpen = getMostRecentEpisode(p).isOpen();
		return (episodes != null && (!isMostRecentOpen || episodes.size() > 1));
	}

	public static Appointment getLatestActiveAppointmentForPatient(Patient pat) {
		Appointment returnApp = null;
		for (Appointment app : pat.getAppointments()) {
			if (!app.isActive() || app.getAppointmentDate() == null) {
				continue;
			}

			if (returnApp == null) {
				returnApp = app;
			}

			if (app.getAppointmentDate().after(returnApp.getAppointmentDate())) {
				returnApp = app;
			}
		}
		return returnApp;
	}

	// METHODS FOR PREGNANCY MANAGER -----------------------------------------

	/**
	 * Inserts an episode into the list such that the start date of the inserted
	 * element is chronologically before the next episode and after the previous
	 * episode.
	 * 
	 * Assumes episodes are sorted by startDate
	 * 
	 * Note: This method does not take into account the fact that the last
	 * episode may be open
	 * 
	 * @param p
	 * @param insert
	 */
	public static void insertPatientEpisodeAccordingToDate(Patient p,
			Episode insert) {
		List<Episode> episodes = p.getEpisodes();
		insert.setPatient(p);
		if (!episodes.contains(insert)) {
			for (Episode e : episodes) {
				if (insert.getStartDate().before(e.getStartDate())) {
					episodes.add(episodes.indexOf(e), insert);
					return;
				}
			}
		}
	}

	/**
	 * Checks if the patient id already exists
	 * 
	 * @param session
	 *            Session
	 * @param patientId
	 * @return true if it exists else false
	 * @throws HibernateException
	 */
	public static boolean patientIdExists(Session session, String patientId)
			throws HibernateException {
		boolean result = false;
		@SuppressWarnings("unchecked")
		List<Patient> patient = session
				.createQuery(
						"select patient from Patient as patient where patient.patientId = :id")
				.setParameter("id", patientId).list();
		if (patient.size() > 0) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// ------ METHODS FOR PATIENT PACKAGING MANAGER -------------

	/**
	 * Method save.
	 * 
	 * @param session
	 *            Session
	 * @param newAppointmentForPatient
	 *            Appointment
	 */
	public static void save(Session session,
			Appointment newAppointmentForPatient) {
		Patient p = newAppointmentForPatient.getPatient();
		PatientManager.savePatient(session, p);

	}

	/**
	 * @param sess
	 *            Session
	 * @param pre
	 *            Prescription
	 * @param packageToSave
	 * @param packagedDrugsSet
	 * @param accumulatedDrugs
	 *            Set<AccumulatedDrugs>
	 * @param previousPack
	 *            Packages
	 * @throws HibernateException
	 */
	public static void save(Session sess, Prescription pre,
			Packages packageToSave, List<PackagedDrugs> packagedDrugsSet,
			Set<AccumulatedDrugs> accumulatedDrugs, Packages previousPack)
			throws HibernateException {

		packageToSave.setPackagedDrugs(packagedDrugsSet);
		packageToSave.setAccumulatedDrugs(accumulatedDrugs);
		packageToSave.setPrescription(pre);
		pre.getPackages().add(packageToSave);
		sess.save(packageToSave);

		for (PackagedDrugs pd : packagedDrugsSet) {

			StockManager.updateStockLevel(sess, pd.getStock());

		}

	}

	// METHODS FOR PATIENT MANAGER -----------------------------------------
	/**
	 * Saves a patient. If the patient is updated and marked as inactive, the
	 * end dates for the patient's current prescription is set
	 * 
	 * @param sess
	 *            Session
	 * @param thePatient
	 * @throws HibernateException
	 */
	public static void savePatient(Session sess, Patient thePatient)
			throws HibernateException {
		// if this patient is a NEW PATIENT
		if (!sess.contains(thePatient)) {
			sess.save(thePatient);

		}
		// else, this patient needs to be updated
		else {
			if (thePatient.getAccountStatusWithCheck() == false) {
				Prescription pre = thePatient.getCurrentPrescription();
				if (pre != null) {
					pre.setEndDate(getMostRecentEpisode(thePatient)
							.getStopDate());
					pre.setCurrent('F');
					log.info("Updated prescription end date for patient "
							+ thePatient.getPatientId() + ", prescription "
							+ pre.getPrescriptionId());
				}
			}

		}
	}

	public static void deleteSecondaryPatient(Session session,
			Patient thePatient) {

		// delete all of thePatient's prescriptions
		thePatient.getPrescriptions().clear();

		// remove any reference between the patient to be deleted and their
		// prescriptions
		// (which have been transferred to the primary patient before this
		// method is called)
		thePatient.setPrescriptions(null);

		// delete the patient from the active session
		session.delete(thePatient);
		session.flush();
	}

	/**
	 * This method logs the patient data to the log table. The patient that is
	 * logged is the secondary patient in the merge as this patient gets deleted
	 * from the patient table
	 * 
	 * @param session
	 * @param p1
	 * @param p2
	 * @param hasPillcounts
	 */
	public static void logPatientMerge(Session session, Patient p1, Patient p2,
			boolean hasPillcounts) {
		// log this transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(session));
		logging.setItemId(String.valueOf(p2.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Patient Merge");

		PatientAttribute arvStartDateAttribute = p2
				.getAttributeByName(PatientAttribute.ARV_START_DATE);

		logging.setMessage("'"
				+ p2.getPatientId()
				+ "'(secondary patient)  was merged into '"
				+ p1.getPatientId()
				+ "'(primary patient)  on "
				+ new SimpleDateFormat("dd MMM yyyy").format(new Date())
				+ ". Secondary Patient (firstName("
				+ p2.getFirstNames()
				+ "), LastName("
				+ p2.getLastname()
				+ "), DOB("
				+ new SimpleDateFormat("dd/MM/yyyy").format(p2.getDateOfBirth())
				+ "), Sex(" + p2.getSex() + "), Address(" + p2.getFullAddress()
				+ "), Clinic("
				+ p2.getCurrentClinic().getClinicName() + "), ARV Start Date("
				+ arvStartDateAttribute == null ? arvStartDateAttribute
				.getValue() : "Unknown" + "), Ph Home(" + p2.getHomePhone()
				+ "), Ph Work(" + p2.getWorkPhone() + "), Ph Cell("
				+ p2.getCellphone() + "), Rx Supporter("
				+ p2.getNextOfKinName() + "), Rx Supporter Ph("
				+ p2.getNextOfKinPhone() + "), Episodes: "
				+ p2.episodeDetails());

		if (hasPillcounts) {
			logging.setMessage(logging.getMessage()
					+ ". Pill counts for both patients were deleted. ");
		}

		session.save(logging);

	}

	/**
	 * Updates a patients accountStatus based on their last episode.
	 * accountStatus = true if most recent episode is open
	 * 
	 * @param p
	 */
	public static void updateAccountStatus(Patient p) {
		p.setAccountStatus(getMostRecentEpisode(p).isOpen());
	}
	
	/**
	 * Validates an Episode's fields.
	 */
	public static Map<String, String> validateEpisode(Episode e) {
		return validateEpisode(e.getStartDate(), e.getStartReason(), e.getStopDate(), e.getStopReason());
	}

	/**
	 * Validates an Episode's fields.: Returns false if any of the following
	 * rules are matched:
	 * <ul>
	 * <li>startReason is empty string OR startDate is null</li>
	 * <li>stopReason is empty string XOR stopDate is null</li>
	 * <li>stopDate is before startDate</li>
	 * </ul>
	 * 
	 * @param startDate
	 * @param startReason
	 * @param stopDate
	 * @param stopReason
	 * @return Map with the following String keys:<br/>
	 *         String result = true if the episode fields are valid<br/>
	 *         String title = error dialog title<br/>
	 *         String message = error message<br/>
	 */
	public static Map<String, String> validateEpisode(Date startDate,
			String startReason, Date stopDate, String stopReason) {
		Boolean result = true;
		String title = "";
		String message = "";
		if (startReason.trim().equals("") || startDate == null) {
			title = "Invalid episode";
			message = "Episode must have a start reason and a start date.";
			result = false;
		} else if (stopReason.trim().equals("") ^ stopDate == null) {
			title = "Invalid episode";
			message = "Episode must have a stop reason and a stop date.";
			result = false;
		} else if (stopDate != null) {
			if (stopDate.before(startDate)) {
				title = "Stop date before start date";
				message = "Episode stop date is before episode start date.";
				result = false;
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result.toString());
		map.put("title", title);
		map.put("message", message);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static List<String[]> getPatientsExpetcted(Session session, Date date, String clinicName, String orderByProperty, String orderByDirection, String[] headers) {
		
		List<String[]> result = new ArrayList<String[]>();
		
		List<Object[]> objects = new ArrayList<Object[]>();
		
		String query = "select a.patID,a.patientID as patientID, a.name as name, a.contactno, a.dateexpected, a.scriptduration as scriptduration, coalesce(b.packcount,0) as packcount "
				+ "from (select distinct pat.id as patID, pat.patientid as patientID, ( pat.lastname||', '|| pat.firstnames) as name, "
				+ "case when ((homephone is null)or(homephone like ''))  then '' else homephone || ' (h) ' end || " 
				+ "case when ((cellphone is null)or(cellphone like ''))  then '' else cellphone || ' (c) 'end || "
				+ "case when ((workphone is null)or(workphone like '')) then '' else workphone || ' (w) ' end as contactno, "
				+ "date(app.appointmentDate) as dateexpected, "
				+ "CASE WHEN pre.duration=2 THEN '2 wks' "
				+ "WHEN pre.duration=4 THEN '1 mnth' "
				+ "ELSE (pre.duration/4)||' mnths' "
				+ "END as scriptduration "
				+ "from Patient as pat, prescription as pre, appointment as app, Episode ep, Clinic c "
				+ "where ((:date1 between pre.date and pre.endDate)or((:date2 > pre.date) and pre.endDate is null)) "
				+ "and ((:date3 between ep.startdate and ep.stopdate) or (:date4 > ep.startdate and ep.stopdate is null)) "
				+ "and pre.patient=pat.id and pat.clinic = c.id and c.clinicName = :clinic and ep.patient=pat.id "
				+ "and pat.accountstatus = true and date(:date5)=(date(app.appointmentDate)) and app.patient = pat.id "
				+ "and app.visitDate is null ) as a "
				+ "left outer join "
				+ "(select pat.id as patid,pat.patientid ,count(distinct pack.id) as packcount "
				+ "from patient as pat, package as pack, prescription as pre "
				+ "where pre.id = pack.prescription and pat.id = pre.patient and ((:date6 between pre.date and pre.endDate)or((:date7 > pre.date) and pre.endDate is null)) "
				+ "group by pat.patientid, pat.id) as b on b.patID = a.patID order by " 
				+ orderByProperty + " " + orderByDirection;
		
		
		objects = session.createSQLQuery(query).setDate("date1", date)
					.setDate("date2", date)
					.setDate("date3", date)
					.setDate("date4", date)
					.setString("clinic", clinicName)
					.setDate("date5", date)
					.setDate("date6", date)
					.setDate("date7", date)
					.list();
		
		for (Object[] objects2 : objects) {
			String[] str = new String[7];
			str[0] = objects2[0].toString();
			str[1] = objects2[1].toString();
			str[2] = objects2[2].toString();
			str[3] = objects2[3].toString();
			str[4] = objects2[4].toString();
			str[5] = objects2[5].toString();
			str[6] = objects2[6].toString();
			
			result.add(str);
		}
		
		result.add(0, headers);
		return result;
		
	}
	public static void saveCellphoneNumber(Session session, String newCellNo, int id) throws HibernateException {
		session.createQuery("Update Patient set cellphone = :newCellNo where id = :id").setString("newCellNo", newCellNo).setInteger("id", id).executeUpdate();
	}

	/**
	 * Checks for duplicate patient ARV Start Date attribute types
	 * 
	 * @param sess
	 */
	public static void checkPatientAttributes(Session sess) {
		List<AttributeType> allAttributeTypes = PatientManager.getAllAttributeTypes(sess);
		List<AttributeType> arvTypes = new ArrayList<AttributeType>();
		Pattern pattern = Pattern.compile("[arvARV]{3}.[startSTART]{5}.[dateDATE]{4}");
		for (AttributeType type : allAttributeTypes) {
			if (type.getDataType().equals(Date.class)
					&& pattern.matcher(type.getName()).find()){
				log.debug("Found attribute type similar to ARV Start Date: " + type.getName());
				arvTypes.add(type);
			}
		}
		if (arvTypes.isEmpty()) {
			log.warn("Patient Attribute: ARV Start Date not present in Database. Creating attribute.");
			PatientManager.addAttributeTypeToDatabase(sess, Date.class,
					PatientAttribute.ARV_START_DATE, "Date for First ARV package dispensing.");
		} else if (arvTypes.size() > 1){
			log.warn(arvTypes.size() + " ARV Start Date attribute types found. Attempting to remove duplicates.");
			AttributeType correct = null;
			for (AttributeType type : arvTypes) {
				if (type.getName().equals(PatientAttribute.ARV_START_DATE)){
					log.debug("Found correct type: id=" + type.getId());
					correct = type;
					break;
				}
			}
			if (correct == null){
				correct = arvTypes.get(0);
				log.warn("No correct type found, selecting first type: id=" + correct.getId());

				correct.setName(PatientAttribute.ARV_START_DATE);
				sess.save(correct);
			}
			
			arvTypes.remove(correct);
			for (AttributeType incorrect : arvTypes) {
				log.warn("Replacing incorrect type ("+incorrect.getId()+")with correct type");
				SQLQuery query = sess.createSQLQuery("update patientattribute set type_id = :correct " +
						"where type_id = :incorrect");
				query.setParameter("correct", correct.getId());
				query.setParameter("incorrect", incorrect.getId());
				query.executeUpdate();
				
				log.warn("Deleting incorrect type: id="+incorrect.getId());
				sess.delete(incorrect);
			}
		}
		
	}

	public static boolean checkPatientIdentifier(Session session, 
			Patient patient, IdentifierType type, String identifierValue) {
		String queryString = "from PatientIdentifier where " +
						"type = :type and value = :value";
		
		if (patient != null && patient.getId() != -1){
			queryString += " and patient != :patient";
		}
		Query query = session.createQuery(queryString);

		if (patient != null && patient.getId() != -1){
			query.setParameter("patient", patient);
		}
		
		query.setParameter("type", type);
		query.setParameter("value", identifierValue);
		List<?> list = query.list();
		return !list.isEmpty();
	}

	public static List<Integer> getPatientsWithAttribute(Session session, String attributeTypeName) {
		Query query = session.createQuery("select att.patient.id from PatientAttribute att where " +"att.type.name = :name");
		query.setParameter("name", attributeTypeName);
		
		@SuppressWarnings("unchecked")
		List<Integer> list = query.list();
		return list;
	}

	public static List<ArtDto> getIedeaArtData(Session session, Patient patient) {
		SQLQuery query = session.createSQLQuery("select f.form as form, atc.code as code, min(pickupdate) as startdate, max(pickupdate) as enddate" 
			+ " from prescription sc, package p, packageddrugs pd, stock s, drug d, form f, atccode atc"
			+ " where sc.id = p.prescription"
			+ " and p.id = pd.parentpackage"
			+ " and pd.stock = s.id"
			+ " and s.drug = d.id"
			+ " and d.form = f.id"
			+ " and d.atccode_id = atc.id"
			+ " and d.sidetreatment = 'F'"
			+ " and sc.patient = :patid"
			+ " and pickupdate is not null"
			+ " group by f.form, atc.code");
		query.setInteger("patid", patient.getId());
		query.setResultTransformer(new AliasToBeanResultTransformer(ArtDto.class));
		
		@SuppressWarnings("unchecked")
		List<ArtDto> list = query.list();
		
		return list;
	}
}