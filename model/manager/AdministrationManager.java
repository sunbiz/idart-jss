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

import java.util.Date;
import java.util.List;
import java.util.Set;

import model.nonPersistent.PharmacyDetails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.Logging;
import org.celllife.idart.database.hibernate.NationalClinics;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.SimpleDomain;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.Study;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.database.hibernate.User;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 */
public class AdministrationManager {

	private static Log log = LogFactory.getLog(AdministrationManager.class);

	// ------- METHODS FOR DOCTOR MANAGER --------------------------------

	/**
	 * Method getAllDoctors.
	 * 
	 * @param sess
	 *            Session
	 * @return List<Doctor>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Doctor> getAllDoctors(Session sess)
	throws HibernateException {
		List<Doctor> result = sess.createQuery(
		"select d from Doctor as d order by upper(d.lastname)").list();

		return result;
	}

	/**
	 * Saves the current doctor
	 * 
	 * @param s
	 *            Session
	 * @param theDoctor
	 *            Doctor
	 * @throws HibernateException
	 */
	public static void saveDoctor(Session s, Doctor theDoctor)
	throws HibernateException {
		// if this is the 1st time we're accessing the doctor List
		s.save(theDoctor);
	}

	/**
	 * Method getDoctor.
	 * 
	 * @param sess
	 *            Session
	 * @param doctorsFullName
	 *            String
	 * @return Doctor
	 */
	public static Doctor getDoctor(Session sess, String doctorsFullName) {
		Doctor theDoc = null;
		List<Doctor> docList = AdministrationManager.getAllDoctors(sess);
		if (docList != null) {
			for (int i = 0; i < docList.size(); i++) {
				theDoc = docList.get(i);
				if (theDoc.getFullname().equals(doctorsFullName)) {
					break;
				}
			}
		}
		return theDoc;
	}

	// ------- METHODS FOR CLINIC MANAGER --------------------------------

	/**
	 * Return the Default Clinic's name
	 * 
	 * @param sess
	 * @return String
	 */
	public static String getDefaultClinicName(Session sess) {

		Clinic mainClinic = getMainClinic(sess);

		if (mainClinic != null)
			return mainClinic.getClinicName();
		else {
			log.warn("Returning first clinic found, not default clinic");
			return getClinicNames(sess).get(0);
		}

	}

	/**
	 * Return the Default Clinic (usually located at the main StockCenter)
	 * 
	 * @param sess
	 * @return Clinic
	 * @throws HibernateException
	 */
	public static Clinic getMainClinic(Session sess) throws HibernateException {

		Clinic c = (Clinic) sess.createQuery(
		"select c from Clinic as c where c.mainClinic = true")
		.uniqueResult();
		if (c == null) {
			log.warn("Default clinic not found");
		}
		return c;

	}

	/**
	 * Return all Clinic Names
	 * 
	 * @param sess
	 * @return List<String>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getClinicNames(Session sess)
	throws HibernateException {
		List<String> clinicList = sess
		.createQuery(
		"select c.clinicName from Clinic as c order by c.mainClinic DESC")
		.list();

		return clinicList;
	}

	/**
	 * Return all Clinics
	 * 
	 * @param sess
	 * @return List<Clinic>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Clinic> getClinics(Session sess)
	throws HibernateException {
		List<Clinic> clinicList = sess.createQuery(
						"select c from Clinic as c order by c.mainClinic desc, c.clinicName asc")
		.list();

		return clinicList;
	}
	
	public static List<NationalClinics> getClinicsDetails(Session sess)
	throws HibernateException {
		@SuppressWarnings("unchecked")
		List<NationalClinics> clinicList = sess.createQuery(
						"from NationalClinics")
		.list();

		return clinicList;
	}

	/**
	 * Get the clinic with this name
	 * 
	 * @param sess
	 * @param name
	 * @return Clinic
	 * @throws HibernateException
	 */
	public static Clinic getClinicbyName(Session sess, String name)
	throws HibernateException {

		Clinic clinic = (Clinic) sess.createQuery(
		"select c from Clinic as c where c.clinicName like :theName")
		.setString("theName", name).setMaxResults(1).uniqueResult();

		return clinic;
	}

	/**
	 * Method getRemoteClinics.
	 * 
	 * @param sess
	 *            Session
	 * @return List<Clinic>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Clinic> getRemoteClinics(Session sess)
	throws HibernateException {
		List<Clinic> clinicList = sess.createQuery(
				"select c.name from" + " Clinic c where c.mainClinic = false")
				.list();
		return clinicList;
	}

	/**
	 * Method getClinic.
	 * 
	 * @param session
	 *            Session
	 * @param clinicName
	 *            String
	 * @return Clinic
	 * @throws HibernateException
	 */
	public static Clinic getClinic(Session session, String clinicName)
	throws HibernateException {
		Clinic myClinic = null;
		myClinic = (Clinic) session
		.createQuery(
		"select c from Clinic as c where upper(c.clinicName) = :clinic_Name")
		.setString("clinic_Name", clinicName.toUpperCase())
		.uniqueResult();
		return myClinic;
	}
	
	public static NationalClinics getSearchDetails(Session session, String facilityName, String province)
	throws HibernateException {
		NationalClinics myClinic = null;
		myClinic = (NationalClinics) session
		.createQuery(
		"from NationalClinics where facilityName like :facilityname and province like :province")
		.setString("facilityname", facilityName).setString("province", province)
		.uniqueResult();
		return myClinic;
	}

	/**
	 * This method saves the clinic objects passed to it
	 * 
	 * @param s
	 *            Session
	 * @param theClinic
	 * @throws HibernateException
	 */
	public static void saveClinic(Session s, Clinic theClinic)
	throws HibernateException {

		s.save(theClinic);

	}

	/**
	 * Checks if the clinic exists
	 * 
	 * @param session
	 *            Session
	 * @param clinicName
	 *            the clinic name to check
	 * @return true if the clinic exists else false
	 * @throws HibernateException
	 */
	public static boolean clinicExists(Session session, String clinicName)
	throws HibernateException {
		boolean result = false;
		Clinic clinic = getClinic(session, clinicName);
		if (clinic == null) {
			result = false;
		} else {
			result = true;
		}
		return result;
	}

	// ------- METHODS FOR USER MANAGER --------------------------------

	/**
	 * Method saveUser.
	 * 
	 * @param session
	 *            Session
	 * @param userName
	 *            String
	 * @param password
	 *            String
	 * @param clinics
	 *            Set<Clinics>
	 * @return boolean
	 */
	public static boolean saveUser(Session session, String userName,
			String password, String role, Set<Clinic> clinics) {
		if (!userExists(session, userName)) {
			User user = new User(userName, password, role, 'T',
					clinics);
			session.save(user);

			// log the transaction
			Logging logging = new Logging();
			logging.setIDart_User(LocalObjects.getUser(session));
			logging.setItemId(String.valueOf(user.getId()));
			logging.setModified('Y');
			logging.setTransactionDate(new Date());
			logging.setTransactionType("Added New User");
			logging.setMessage("Added New User " + user.getUsername()
					+ " with clinic access " + getClinicAccessString(user));
			session.save(logging);

			return true;
		}
		return false;
	}

	/**
	 * Method getClinicAccessString.
	 * 
	 * @param u
	 *            User
	 * @return String
	 */
	public static String getClinicAccessString(User u) {
		StringBuffer clinicList = new StringBuffer();
		for (Clinic s : u.getClinics()) {
			clinicList.append(s.getClinicName());
			clinicList.append(", ");
		}
		// remove last comma and spac
		if (clinicList.length() > 2) {
			clinicList = clinicList.delete(clinicList.length() - 2, clinicList.length());
		}
		return clinicList.toString();

	}

	/**
	 * Method userExists.
	 * 
	 * @param session
	 *            Session
	 * @param name
	 *            String
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public static boolean userExists(Session session, String name) {
		List<User> userList = session.createQuery(
		"from User u where upper(u.username) = :name").setString(
				"name", name.toUpperCase()).list();
		if (userList.size() > 0)
			return true;
		return false;
	}

	/**
	 * Returns a string list of the Usernames for the StockCenter
	 * 
	 * @param sess
	 *            Session
	 * @return List
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getUserList(Session sess)
	throws HibernateException {
		String query = "select user.username from User as user order by user.username asc";
		List<String> result = sess.createQuery(query).list();
		return result;
	}

	/**
	 * Returns a list of all the users
	 * 
	 * @param sess
	 *            Session
	 * @return List
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<User> getUsers(Session sess) throws HibernateException {
		String query = "from User";
		List<User> result = sess.createQuery(query).list();
		return result;
	}

	/**
	 * Method getUserByName.
	 * 
	 * @param sess
	 *            Session
	 * @param username
	 *            String
	 * @return User
	 * @throws HibernateException
	 */
	public static User getUserByName(Session sess, String username)
	throws HibernateException {

		User user = (User) sess
		.createQuery(
		"select user from User as user where user.username = :theUserName")
		.setString("theUserName", username).setMaxResults(1)
		.uniqueResult();
		return user;
	}

	/**
	 * Method getUserById.
	 * 
	 * @param sess
	 *            Session
	 * @param theId
	 *            int
	 * @return User
	 * @throws HibernateException
	 */
	public static User getUserById(Session sess, int theId)
	throws HibernateException {

		if (sess == null) {
			sess = HibernateUtil.getNewSession();
		}

		User user = (User) sess.createQuery(
		"select user from User as user where user.id = :theId")
		.setInteger("theId", theId).setMaxResults(1).uniqueResult();
		return user;
	}

	/**
	 * @param s
	 * @param u
	 * @param password
	 * @throws HibernateException
	 */
	public static void updateUserPassword(Session s, User u, String password)
	throws HibernateException {
		log.info("Updating password for user " + u.getUsername());

		u.setPassword(password);

		u.setModified('T');

		// log the transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(s));
		logging.setItemId(String.valueOf(u.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Updated User");
		logging.setMessage("Updated User " + u.getUsername()
				+ ": Password change.");
		s.save(logging);

	}

	/**
	 * @param s
	 * @param u
	 * @param clinicsSet
	 * @throws HibernateException
	 */
	public static void updateUserClinics(Session s, User u,
			Set<Clinic> clinicsSet) throws HibernateException {

		log.info("Updating clinic access for user " + u.getUsername());
		String oldClinicAccessStr = getClinicAccessString(u);

		u.setClinics(clinicsSet);

		String newClinicAccessStr = getClinicAccessString(u);

		u.setModified('T');

		// log the transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(s));
		logging.setItemId(String.valueOf(u.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Updated User");
		logging.setMessage("Updated User " + u.getUsername()
				+ ": Clinic access change from " + oldClinicAccessStr + " to "
				+ newClinicAccessStr);
		s.save(logging);

	}

	/**
	 * @param u
	 * @param clinics
	 * @throws HibernateException
	 */
	public static void updateUserClinicAccess(User u, Set<Clinic> clinics)
	throws HibernateException {

		u.setClinics(clinics);
		u.setModified('T');

	}

	// ------- METHODS FOR StockCenter MANAGER --------------------------------

	/**
	 * Returns a StockCenter by name
	 * 
	 * @param session
	 *            Session
	 * @return StockCenter
	 */
	public static StockCenter getStockCenter(Session session, String name) {
		StockCenter result = null;
		result = (StockCenter) session
		.createQuery(
		"select sc from StockCenter as sc where upper(stockCenterName) = :stockCenterName")
		.setString("stockCenterName", name.toUpperCase())
		.setMaxResults(1).uniqueResult();
		return result;
	}

	/**
	 * Returns all Stock Centers
	 * 
	 * @param session
	 *            Session
	 * @return List<StockCenter>
	 */
	@SuppressWarnings("unchecked")
	public static List<StockCenter> getStockCenters(Session session) {
		List<StockCenter> result = session.createQuery("select sc from StockCenter as sc").list();
		return result;
	}

	/**
	 * Returns the preferred Stock Center
	 * 
	 * @param session
	 *            Session
	 * @return List<StockCenter>
	 */
	public static StockCenter getPreferredStockCenter(Session session) {
		StockCenter result = (StockCenter) session.createQuery(
		"select sc from StockCenter as sc where sc.preferred = true")
		.uniqueResult();
		return result;
	}

	/**
	 * Method saveStockCenter.
	 * 
	 * @param session
	 *            Session
	 * @param theStockCenter
	 *            StockCenter
	 */
	public static void saveStockCenter(Session session,
			StockCenter theStockCenter) {

		if (theStockCenter.isPreferred()) {
			session.createQuery("Update StockCenter set preferred = false")
			.executeUpdate();
		}
		session.saveOrUpdate(theStockCenter);
	}

	// ------- METHODS FOR SIMPLE DOMAIN MANAGER
	// --------------------------------

	/**
	 * Method simpleDomainExists.
	 * 
	 * @param session
	 *            Session
	 * @param name
	 *            String
	 * @param description
	 *            String
	 * @param value
	 *            String
	 * @return boolean
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static boolean simpleDomainExists(Session session, String name,
			String description, String value) throws HibernateException {
		List<SimpleDomain> domainList = session
		.createQuery(
				"from SimpleDomain sd where upper(sd.name) =:name"
				+ " and upper(sd.description) =:description and upper(sd.value) =:value")
				.setString("name", name.toUpperCase()).setString("description",
						description.toUpperCase()).setString("value",
								value.toUpperCase()).list();
		if (domainList.size() > 0)
			return true;
		return false;
	}

	/**
	 * Method addSimpleDomain.
	 * 
	 * @param session
	 *            Session
	 * @param sDomain
	 *            SimpleDomain
	 * @throws HibernateException
	 */
	public static void addSimpleDomain(Session session, SimpleDomain sDomain)
	throws HibernateException {

		if (!simpleDomainExists(session, sDomain.getName(), sDomain
				.getDescription(), sDomain.getValue())) {
			session.save(sDomain);
		}
	}

	/**
	 * Method getClinicalStages.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getClinicalStages(Session sess)
	throws HibernateException {
		List<SimpleDomain> result = null;
		String qString = "select s from SimpleDomain as s where s.name='clinical_stage' order by s.value asc";
		Query q = sess.createQuery(qString);
		result = q.list();
		return result;
	}

	/**
	 * Method getPrescriptionDurations.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getPrescriptionDurations(Session sess)
	throws HibernateException {
		List<SimpleDomain> result = sess
		.createQuery(
		"select s from SimpleDomain as s where s.name='prescriptionDuration' order by s.id")
		.list();

		return result;
	}

	/**
	 * Method getReasonForUpdate.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getReasonForUpdate(Session sess)
	throws HibernateException {
		String qString = "select s from SimpleDomain as s where s.name like 'reason_for_update' order by s.value";
		Query q = sess.createQuery(qString);
		List<SimpleDomain> result = q.list();

		return result;
	}

	/**
	 * Method getProvinces.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getProvinces(Session sess)
	throws HibernateException {
		String qString = "select distinct(province) from NationalClinics" +
				" order by province";
		Query q = sess.createQuery(qString);
		List<String> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getDistrict(Session sess,String prov)
	throws HibernateException {
		String qString = "select distinct(district) from NationalClinics as s where s.province=:province" +
				" order by district";
		Query q = sess.createQuery(qString).setString("province", prov);
		List<String> result = q.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getSubDistrict(Session sess,String dist)
	throws HibernateException {
		String qString = "select distinct(subDistrict) from NationalClinics where district = :district" +
				" order by subDistrict";
		Query q = sess.createQuery(qString).setString("district", dist);
		List<String> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getFacilityName(Session sess,String subdis)
	throws HibernateException {
		String qString = "select distinct(facilityName) from NationalClinics where subDistrict = :subdistrict" +
				" order by facilityName";
		Query q = sess.createQuery(qString).setString("subdistrict", subdis);
		List<String> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getFacilityType(Session sess,String facname)
	throws HibernateException {
		String qString = "select distinct(facilityType) from NationalClinics where facilityName = :facilityname";
		Query q = sess.createQuery(qString).setString("facilityname", facname);
		List<String> result = q.list();
		return result;
	}
	
	/**
	 * Method to get a NationalClinic based on given fields
	 * @param sess
	 * @param prov
	 * @param district
	 * @param sdistrict
	 * @param facility
	 * @param fType
	 * @return
	 * @throws HibernateException
	 */
	public static NationalClinics getNationalClinic(Session sess,String prov,String district,String sdistrict,String facility)
	throws HibernateException {
		String qString = "from NationalClinics " +
								  "where province = :province " +
								  "and district = :district " +
								  "and subdistrict = :sdistrict " +
								  "and facilityname = :facility ";
		Query q = sess.createQuery(qString).setString("province", prov)
				.setString("district", district)
				.setString("sdistrict", sdistrict)
				.setString("facility", facility);
		NationalClinics  result = (NationalClinics) q.uniqueResult();
		return result ;
	}
	
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getRegimens(Session sess)
	throws HibernateException {
		String qString = "select s from SimpleDomain as s where s.name= :regimen order by s.value";
		Query q = sess.createQuery(qString).setString("regimen","regimen");
		List<SimpleDomain> result = q.list();
		return result;
	}

	/**
	 * Method getReportParameters.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getReportParameters(Session sess)
	throws HibernateException {
		String qString = "select s from SimpleDomain as s where s.description='report_parameter'";
		Query q = sess.createQuery(qString);
		List<SimpleDomain> result = q.list();

		if (result == null) {
			log.warn("No report parameter entries found in SimpleDomain");
		}
		return result;
	}

	/**
	 * Method getActivationReasons.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getActivationReasons(Session sess)
	throws HibernateException {
		String qString = "select s from SimpleDomain as s where s.name='activation_reason' order by s.value asc";
		Query q = sess.createQuery(qString);
		List<SimpleDomain> result = q.list();

		return result;
	}

	/**
	 * Method getDeactivationReasons.
	 * 
	 * @param sess
	 *            Session
	 * @return List<SimpleDomain>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleDomain> getDeactivationReasons(Session sess)
	throws HibernateException {
		String qString = "select s from SimpleDomain as s where s.name='deactivation_reason' order by s.value";
		Query q = sess.createQuery(qString);
		List<SimpleDomain> result = q.list();

		return result;
	}

	/**
	 * @param sess
	 *            Session
	 * @return all the user-defined drug groups eg 1A-30, 1A-40 etc
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Object[]> getDrugGroupNamesAndRegs(Session sess)
	throws HibernateException {

		String qString = "select regimenName, drugGroup from Regimen r";
		Query q = sess.createQuery(qString);
		List<Object[]> result = q.list();

		return result;
	}

	/**
	 * @param sess
	 *            Session
	 * @return all the user-defined drug groups eg 1A-30, 1A-40 etc
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Regimen> getDrugGroups(Session sess)
	throws HibernateException {

		String qString = "from Regimen r order by r.regimenName";
		Query q = sess.createQuery(qString);
		List<Regimen> result = q.list();

		return result;
	}

	/**
	 * This method saves the simpleDomain objects passed to it
	 * 
	 * @param sess
	 *            Session
	 * @param simpleDomain
	 * @throws HibernateException
	 */
	public static void saveSimpleDomain(Session sess, SimpleDomain simpleDomain)
	throws HibernateException {

		sess.save(simpleDomain);
	}

	// ------- METHODS FOR LOGGIN MANAGER --------------------------------

	// ------- METHODS FOR FORM MANAGER --------------------------------

	/**
	 * Used to populate combo boxes with drug forms
	 * 
	 * @param sess
	 *            Session
	 * @return all the form names eg tablets, solution etc
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Form> getForms(Session sess) throws HibernateException {
		String qString = "from Form as f order by f.form";
		Query q = sess.createQuery(qString);
		List<Form> result = q.list();

		return result;
	}

	/**
	 * This method gets a form from the database.
	 * 
	 * @param session
	 *            the current hibernate session
	 * @param formName
	 *            the name of the form to get
	 * @return the form
	 */
	public static Form getForm(Session session, String formName) {
		return (Form) (session.createQuery(
		"from Form as f where upper(f.form) = :form").setString("form",
				formName.toUpperCase()).uniqueResult());
	}

	/**
	 * Method to save only unique form
	 * 
	 * @param session
	 *            the current hibernate session
	 * @param form
	 *            the form to save
	 * @throws HibernateException
	 */
	public static void saveForm(Session session, Form form)
	throws HibernateException {
		if (!formExists(session, form.getForm())) {
			session.save(form);
		}
	}

	/**
	 * Method to check if form already exists
	 * 
	 * @param session
	 *            the current hibernate session
	 * @param formName
	 *            the name of the form
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public static boolean formExists(Session session, String formName) {
		List<Form> result = session.createQuery(
		"from Form as f where upper(f.form) = :form").setString("form",
				formName.toUpperCase()).list();
		if (result.size() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public static PharmacyDetails getPharmacyDetails(Session sess)
	throws HibernateException {
		String qString = "select s from SimpleDomain as s where s.description='pharmacy_detail'";
		Query q = sess.createQuery(qString);
		List<SimpleDomain> result = q.list();

		if (result == null) {
			log.warn("No report parameter entries found in SimpleDomain");
			// return result;
		}

		PharmacyDetails phd = new PharmacyDetails();
		// pharmacist
		// assistant_pharmacist
		// pharmacy_name
		// pharmacy_street
		// pharmacy_city
		// pharmacy_contact_no
		//
		if (result != null) {
			for (SimpleDomain s : result) {
				if (s.getName().equalsIgnoreCase("pharmacist")) {
					phd.setPharmacist(s.getValue());
				} else if (s.getName().equalsIgnoreCase("assistant_pharmacist")) {
					phd.setAssistantPharmacist(s.getValue());
				} else if (s.getName().equalsIgnoreCase("pharmacy_name")) {
					phd.setPharmacyName(s.getValue());
				} else if (s.getName().equalsIgnoreCase("pharmacy_street")) {
					phd.setStreet(s.getValue());
				} else if (s.getName().equalsIgnoreCase("pharmacy_city")) {
					phd.setCity(s.getValue());
				} else if (s.getName().equalsIgnoreCase("pharmacy_contact_no")) {
					phd.setContactNo(s.getValue());
				}
			}
		}
		log.debug(phd.toString());
		return phd;

	}

	public static void savePharmacyDetails(Session session,
			PharmacyDetails pharmDet) {

		String qString = "UPDATE SimpleDomain SET value = '$value' WHERE name = '$name' and "
			+ "description = 'pharmacy_detail'";

		session.createQuery(
				qString.replace("$name", "pharmacist").replace("$value",
						pharmDet.getPharmacist())).executeUpdate();
		session.createQuery(
				qString.replace("$name", "assistant_pharmacist").replace(
						"$value", pharmDet.getAssistantPharmacist()))
						.executeUpdate();
		session.createQuery(
				qString.replace("$name", "pharmacy_name").replace("$value",
						pharmDet.getPharmacyName())).executeUpdate();
		session.createQuery(
				qString.replace("$name", "pharmacy_street").replace("$value",
						pharmDet.getStreet())).executeUpdate();
		session.createQuery(
				qString.replace("$name", "pharmacy_city").replace("$value",
						pharmDet.getCity())).executeUpdate();
		session.createQuery(
				qString.replace("$name", "pharmacy_contact_no").replace(
						"$value", pharmDet.getContactNo())).executeUpdate();

	}
	
	// --------------------- Methods for Study Manager ------------------
	public static Study getCidaStudy(Session session) {
		
		Study result = (Study)session.createQuery("select study from Study study " +
				"where id = 1").uniqueResult();
		
		return result;
		
	}
	
	public static List<StudyParticipant> getP() {
		return null;
	}
	
}
