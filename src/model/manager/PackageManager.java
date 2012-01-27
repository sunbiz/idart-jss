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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.User;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.model.utils.PackageLifeStage;
import org.celllife.idart.print.label.DrugLabel;
import org.celllife.idart.print.label.PackageCoverLabel;
import org.celllife.idart.print.label.PrintThread;
import org.celllife.idart.print.label.ScriptSummaryLabel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 */
public class PackageManager {

	// --------- METHODS FOR PRESCRIPTION OBJECT MANAGER
	// ---------------------------------
	/**
	 * Method checkStockLevels.
	 * 
	 * @param sess
	 *            Session
	 * @param d
	 *            Drug
	 * @param c
	 *            Clinic
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean checkStockLevels(Session sess, Drug d,
			StockCenter stockCenter) throws HibernateException {
		boolean levelsFine = false;
		int[] totalLevel = StockManager.getDrugTotalLevel(sess, d, stockCenter);
		if ((totalLevel != null)
				&& ((totalLevel[0] > 0) || (totalLevel[1] > 0))) {
			levelsFine = true;
		} else {
			levelsFine = false;
		}
		return levelsFine;
	}

	// --------- METHODS FOR PRESCRIPTION MANAGER
	// ---------------------------------
	/**
	 * Obtains a new Prescription id based on the patient's id
	 * 
	 * @param session
	 *            Session
	 * @param pat
	 *            Patient
	 * @param theDate
	 *            Date
	 * @return String
	 * @throws HibernateException
	 */
	public static String getNewPrescriptionId(Session session, Patient pat,
			Date theDate) throws HibernateException {

		String newPrescriptionId = "";
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");

		List<Prescription> prescriptionList = getPrescriptionsOnDate(pat,
				theDate);

		char latest = 'A';
		if (prescriptionList.size() > 0) {
			for (Prescription prescription : prescriptionList) {
				String firstPartOfId = prescription.getPrescriptionId();
				char index = firstPartOfId.charAt(6);
				if (iDARTUtil.isAlpha(String.valueOf(index))) {
					if (index > latest) {
						latest = index;
					}
				}
			}
			latest++;
		}

		newPrescriptionId = df.format(theDate).concat(String.valueOf(latest))
				.concat("-").concat(pat.getPatientId());

		return newPrescriptionId;
	}

	/**
	 * Method getPrescriptionsOnDate.
	 * 
	 * @param pat
	 *            Patient
	 * @param theDate
	 *            Date
	 * @return List<Prescription> &#064;throws HibernateException
	 * 
	 */
	public static List<Prescription> getPrescriptionsOnDate(Patient pat,
			Date theDate) throws HibernateException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
		List<Prescription> scriptList = new ArrayList<Prescription>();
		for (Prescription pre : pat.getPrescriptions())
			if (sdf.format(pre.getDate()).equals(sdf.format(theDate))) {
				scriptList.add(pre);
			}
		return scriptList;
	}

	/**
	 * @param pre
	 * @return the duration string for this prescription e.g. 1 month
	 */
	public static String getDurationString(Prescription pre) {
		switch (pre.getDuration()) {
		case 1:
			return "1 week";
		case 2:
			return "1 week";
		case 4:
			return "1 month";
		default:
			return pre.getDuration() / 4 + " months";
		}
	}

	/**
	 * returns the prescription object given the prescriptionId
	 * 
	 * @param session
	 *            Session
	 * @param prescriptionId
	 * @return Prescription
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static Prescription getPrescription(Session session,
			String prescriptionId) throws HibernateException {
		Prescription id = null;
		List<Prescription> presc = null;
		presc = session.createQuery(
				"select prescription from Prescription as prescription "
						+ "where prescription.prescriptionId = '"
						+ prescriptionId + "'").list();

		Iterator<Prescription> iter = presc.iterator();
		if (iter.hasNext()) {
			id = iter.next();
		}
		return id;
	}

	/**
	 * Queries the the database for a list of return reasons to populate the
	 * package return reason combo box
	 * 
	 * @param session
	 * @return List<String>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getReturnReasons(Session session)
			throws HibernateException {

		List<Object> retReasonsO = session
				.createQuery(
						"select value from SimpleDomain as sd where sd.name like 'packageReturnReason'")
				.list();
		List<String> retReasonsS = new ArrayList<String>();
		for (int i = 0; i < retReasonsO.size(); i++) {
			String s = retReasonsO.get(i).toString();
			retReasonsS.add(s);
		}

		return retReasonsS;

	}

	/**
	 * Saves the newly created prescription, and sets the end date of the
	 * previous prescription if any
	 * 
	 * @param sess
	 *            Session
	 * @param preToBeSaved
	 *            Prescription
	 * @param previousPrescriptionDeleted
	 *            boolean
	 * @throws HibernateException
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	public static void saveNewPrescription(Session sess,
			Prescription preToBeSaved, boolean previousPrescriptionDeleted)
			throws HibernateException, IllegalArgumentException {

		if (!previousPrescriptionDeleted) {
			List<Prescription> updatedPrescriptions = sess
					.createQuery(
							"from Prescription as p where p.patient.id = :patid")
					.setInteger("patid", preToBeSaved.getPatient().getId())
					.list();

			for (int i = 0; i < updatedPrescriptions.size(); i++) {
				Prescription p = updatedPrescriptions.get(i);
				p.setModified('T');

				if (p.getDate().after(preToBeSaved.getDate()))
					throw new IllegalArgumentException("Start Date "
							+ preToBeSaved.getDate()
							+ "is before Start Date of previous prescription");

				if (p.getCurrent() != 'F') {
					p.setEndDate(preToBeSaved.getDate());
					p.setCurrent('F');

				}
			}

		}
		sess.save(preToBeSaved);
	}

	/**
	 * Returns the Patients most recent Prescription, if it has no packageddrugs
	 * 
	 * @param sess
	 *            Session
	 * @param patient
	 * @return Prescription
	 * @throws HibernateException
	 */
	public static Prescription getMostRecentPrescriptionWithoutPackages(
			Session sess, Patient patient) throws HibernateException {
		Prescription result = null;
		result = (Prescription) sess
				.createQuery(
						"select pre from Prescription as pre where pre.patient.id= :patientID "
								+ "and size(pre.packages)=0 order by pre.date desc, pre.prescriptionId desc")
				.setInteger("patientID", patient.getId()).setMaxResults(1)
				.uniqueResult();
		return result;
	}

	// --------- METHODS FOR PACKAGES MANAGER ---------------------------------

	/**
	 * Returns a patients most recent ARV package if one exists or null if one
	 * doesn't.
	 * 
	 * NOTE: This is the first package that contains ARV's
	 * 
	 * @param session
	 * @param thePatient
	 * @return the patients most recent package or null
	 * @throws HibernateException
	 */
	public static Packages getMostRecentARVPackage(Session session,
			Patient thePatient) throws HibernateException {

		List<Packages> patientsPackages = getAllPackagesForPatient(session,
				thePatient);

		for (Packages p : patientsPackages) {
			if (p.hasARVDrug()) {
				if (p.getPackDate() != null)
					return p;
			}
		}

		return null;
	}

	public static Packages getMostRecentCollectedPackage(Session session,
			Patient p) throws HibernateException {

		Packages pack = (Packages) session
				.createQuery(
						"select pack from Packages as pack where pack.prescription.patient.id = :thePatientId " +
						" and pack.pickupDate is not null" + 
						" order by pack.pickupDate desc")
				.setInteger("thePatientId", p.getId()).setMaxResults(1).uniqueResult();
		return pack;
	}

	/**
	 * Gets a list of all the packages that have been packed for a particular
	 * prescription.
	 * 
	 * @param session
	 * @param prescription
	 *            Prescription
	 * @return a list of Packages
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Packages> getPackagesForPrescription(Session session,
			Prescription prescription) throws HibernateException {
		List<Packages> prescriptionPackages;
		prescriptionPackages = session
				.createQuery(
						"select pack from Packages as pack where pack.prescription = :thePrescriptionId order by pack.packDate desc")
				.setInteger("thePrescriptionId", prescription.getId()).list();
		return prescriptionPackages;
	}

	/**
	 * Return a list of all packages for this patient, ordered by pack date
	 * 
	 * @param session
	 * @param p
	 * @return List<Packages>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Packages> getAllPackagesForPatient(Session session,
			Patient p) throws HibernateException {
		List<Packages> patientsPackages = session
				.createQuery(
						"select pack from Packages as pack where pack.prescription.patient.id = :thePatientId order by pack.packDate desc")
				.setInteger("thePatientId", p.getId()).list();
		return patientsPackages;
	}

	/**
	 * Return a list of all waiting packages for this patient, ordered by pack
	 * date
	 * 
	 * @param session
	 * @param p
	 * @return List<Packages>
	 */
	@SuppressWarnings("unchecked")
	public static List<Packages> getAllWaitingPackagesForPatient(
			Session session, Patient p) {

		List<Packages> packages = session
				.createQuery(
						"select pack from Packages as pack where pack.prescription.patient.id = :thePatientId and "
								+ "pack.dateReceived is not null and "
								+ "pack.dateLeft is not null and "
								+ "pack.pickupDate is null "
								+ "order by pack.packDate desc")
				.setInteger("thePatientId", p.getId()).list();
		return packages;

	}

	/**
	 * Return a list of all waiting packages for this patient, where the id is
	 * like "%id%" ordered by pack date
	 * 
	 * @param session
	 * @param patientId
	 * @return List<Packages>
	 */
	@SuppressWarnings("unchecked")
	public static List<Packages> getAllWaitingPackagesForPatientWithIdLike(
			Session session, String patientId) {

		List<Packages> packages = session
				.createQuery(
						"select pack from Packages as pack where UPPER(pack.prescription.patient.patientId) like :patientId and "
								+ "pack.packageReturned = false and "
								+ "pack.dateReceived is not null and "
								+ "pack.dateLeft is not null and "
								+ "pack.pickupDate is null "
								+ "order by pack.packDate desc")
				.setString("patientId", "%" + patientId.toUpperCase() + "%")
				.list();
		return packages;

	}

	/**
	 * Return a list of all packages for this patient, ordered by pickup date
	 * 
	 * @param session
	 * @param p
	 * @return List<Packages>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Packages> getAllCollectedPackagesForPatient(
			Session session, Patient p) throws HibernateException {
		List<Packages> patientsPackages = session
				.createQuery(
						"select pack from Packages as pack where pack.prescription.patient.id = :thePatientId "
								+ "and pack.pickupDate is not null "
								+ "and pack.packageReturned = false "
								+ "order by pack.pickupDate desc")
				.setInteger("thePatientId", p.getId()).list();
		return patientsPackages;
	}

	/**
	 * This method checks if a given patient has any uncollected packages
	 * 
	 * @param session
	 * @param p
	 * @return
	 */
	public static boolean patientHasUncollectedPackages(Session session,
			Patient p) {

		List<Packages> packList = getAllPackagesForPatient(session, p);
		for (Packages packages : packList) {
			if (packages.getPickupDate() == null
					&& packages.getDateReturned() == null)
				return true;
		}

		return false;
	}

	/**
	 * This method checks if a given patient has any uncollected packages. This
	 * method allows a maximum of 2 uncollected packages.
	 * 
	 * @param session
	 * @param p
	 * @return
	 */
	public static boolean patientHasMultipleUncollectedPackages(
			Session session, Patient p) {

		boolean result = false;
		int noOfUcollectedPackages = 0;
		List<Packages> packList = getAllPackagesForPatient(session, p);
		for (Packages packages : packList) {
			if (packages.getPickupDate() == null
					&& packages.getDateReturned() == null) {
				noOfUcollectedPackages++;
			}
		}

		if (noOfUcollectedPackages > 1) {
			result = true;
		}

		return result;
	}

	/**
	 * This method returns the packDate of an uncollected package
	 * 
	 * @param session
	 * @param p
	 * @return
	 */
	public static Date getPackDateForUncollectedPackage(Session session,
			Patient p) {

		List<Packages> packList = getAllPackagesForPatient(session, p);
		for (Packages packages : packList) {
			if (packages.getPickupDate() == null
					&& packages.getDateReturned() == null)
				return packages.getPackDate();
		}

		return null;
	}

	/**
	 * This method returns the packdates of the uncollected packages This method
	 * will be used specifically when the patient is allowed multiple
	 * uncollected packages.
	 * 
	 * @param session
	 * @param p
	 * @return
	 */
	public static List<Date> getPackDatesForUncollectedPackage(Session session,
			Patient p) {

		List<Date> dates = new ArrayList<Date>();
		List<Packages> packList = getAllPackagesForPatient(session, p);
		for (Packages packages : packList) {
			if (packages.getPickupDate() == null
					&& packages.getDateReturned() == null) {
				dates.add(packages.getPackDate());
			}
		}

		return dates;

	}

	/**
	 * This method returns the most recent uncollected package for a specific
	 * patient
	 * 
	 * @param session
	 * @param thePatient
	 * @return
	 */
	public static Packages getMostRecentUncollectedPackage(Session session,
			Patient thePatient) {

		List<Packages> patientsPackages = getAllPackagesForPatient(session,
				thePatient);
		Packages pack = null;

		if (patientHasUncollectedPackages(session, thePatient)) {

			for (Packages packages : patientsPackages) {
				if (packages.getPickupDate() == null) {
					if (pack == null) {
						pack = packages;
					} else {
						if (pack.getPackDate().before(packages.getPackDate())) {
							pack = packages;
						}
					}
				}
			}
		}

		return pack;
	}

	/**
	 * Return the last package a patient collected before this package (ie. the
	 * package that would have had pill counts recorded when this package was
	 * collected
	 * 
	 * @param session
	 * @param pack
	 * @return Packages
	 * @throws HibernateException
	 */
	public static Packages getPreviousPackageCollected(Session session,
			Packages pack) throws HibernateException {
		Packages previousPack = null;
		previousPack = (Packages) session
				.createQuery(
						"select pack from Packages as pack where pack.prescription.patient.id = :thePatientId "
								+ "and pack.pickupDate is not null "
								+ "and pack.packageReturned = false "
								+ "and pack.pickupDate < :thisPackPickupDate "
								+ "order by pack.pickupDate desc")
				.setInteger("thePatientId",
						pack.getPrescription().getPatient().getId())
				.setTimestamp("thisPackPickupDate", pack.getPickupDate())
				.setMaxResults(1).uniqueResult();
		return previousPack;
	}

	/**
	 * @param sess
	 * @param pack
	 * @return a string of the form d4t 30, EFV 600 etc
	 * @throws HibernateException
	 */
	public static String getShortPackageContentsString(Session sess,
			Packages pack) throws HibernateException {
		String drugsInPack = "";
		drugsInPack = DrugManager.getDrugListString(
				PackageManager.getDrugsInPackage(sess, pack), ", ", true);
		return drugsInPack;
	}

	/**
	 * @param sess
	 * @param pack
	 * @return a string of the form d4t 30mg 60, EFV 600mg 30 etc
	 * @throws HibernateException
	 */
	public static String getLongPackageContentsString(Session sess,
			Packages pack) throws HibernateException {
		StringBuffer drugListString = new StringBuffer();

		for (Drug d : PackageManager.getDrugsInPackage(sess, pack)) {

			drugListString.append(DrugManager.getShortGenericDrugName(d, true));
			drugListString
					.append(((d.getSideTreatment() == 'F') ? " mg " : ""));
			drugListString.append(DrugManager.getQuantityForDrugInPackage(pack,
					d));
			drugListString.append(",\n");
		}

		// remove the last comma and space
		if (drugListString.length() > 2) {
			return drugListString.substring(0, drugListString.length() - 2);
		}
		return drugListString.toString();
	}

	/**
	 * Method getDrugsInPackage.
	 * 
	 * @param session
	 *            Session
	 * @param thePackage
	 *            Packages
	 * @return List<Drug>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Drug> getDrugsInPackage(Session session,
			Packages thePackage) throws HibernateException {
		List<Drug> drugsInPackage = session
				.createQuery(
						"select pd.stock.drug from PackagedDrugs as pd "
								+ "where pd.parentPackage = :thePackageId order by pd.stock.drug.sideTreatment")
				.setInteger("thePackageId", thePackage.getId()).list();
		return drugsInPackage;
	}

	/**
	 * Check if a package contains an ARV drug.
	 * 
	 * @param pack
	 * @return true if a package contains an ARV drug.
	 */
	public static boolean packageContainsARVDrug(Packages pack) {
		java.util.List<PackagedDrugs> pdLst = pack.getPackagedDrugs();
		for (PackagedDrugs pd : pdLst) {
			Character Ch = pd.getStock().getDrug().getSideTreatment();
			if (Ch.equals('F'))
				return true;
		}
		return false;
	}

	/**
	 * Check if any package in a list of packages contains an ARV drug.
	 * 
	 * @param packages
	 * @return true if any package in list contains ARV drug.
	 */
	public static boolean packagesContainARVDrug(List<Packages> packages) {
		for (Packages pack : packages) {
			if (packageContainsARVDrug(pack))
				return true;
		}
		return false;
	}

	/**
	 * Method getFirstARVPackage.
	 * 
	 * @param packList
	 *            List<Packages>
	 * @return PackagedDrugs
	 */
	public static Packages getFirstPackageWithARVs(List<Packages> packList) {
		Packages earliestPack = null;
		for (Packages pkgs : packList) {
			if (packageContainsARVDrug(pkgs)) {
				if (earliestPack == null) {
					earliestPack = pkgs;
				} else if (pkgs.getPackDate()
						.before(earliestPack.getPackDate())) {
					earliestPack = pkgs;
				}
			}
		}
		return earliestPack;
	}

	// --------- METHODS FOR PACKAGES LEAVING MANAGER
	// ---------------------------------

	/**
	 * Populates the listbox with the packages awaiting dispatch for the given
	 * clinic
	 * 
	 * @param session
	 *            Session
	 * @param clinicName
	 *            String
	 * @return List<Packages>
	 * @throws HibernateException
	 */

	@SuppressWarnings("unchecked")
	public static List<Packages> getPackagesAwaitingScanOut(Session session,
			String clinicName) throws HibernateException {
		List<Packages> result;
		result = session
				.createQuery(
						"select distinct pack from Packages as pack where "
								+ "pack.clinic.clinicName =:clinic and "
								+ "pack.packageReturned = false and "
								+ "pack.packDate is not null and "
								+ "pack.dateLeft is null and "
								+ "pack.dateReceived is null and "
								+ "pack.pickupDate is null and pack.prescription is not null")
				.setString("clinic", clinicName).list();
		return result;
	}

	/**
	 * Method getPackagesProcessed.
	 * 
	 * @param session
	 *            Session
	 * @param c
	 *            Clinic
	 * @param startDate
	 *            Date
	 * @param endDate
	 *            Date
	 * @param lifeStage
	 *            PackageLifeStage
	 * @return List<Packages>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Packages> getPackagesProcessed(Session session,
			Clinic c, Date startDate, Date endDate, PackageLifeStage lifeStage)
			throws HibernateException {
		List<Packages> result;

		Query q = session
				.createQuery(
						"select pack from Packages pack where pack.clinic = :clinic "
								+ "and date(pack."
								+ lifeStage.getDatePropertyName()
								+ ") between date(:startDate) and date(:endDate) "
								+ "and pack.prescription is not null order by "
								+ lifeStage.getDatePropertyName() + " ASC")
				.setLong("clinic", c.getId()).setDate("startDate", startDate)
				.setDate("endDate", endDate);

		result = q.list();

		return result;
	}

	/**
	 * Populates the listbox with the packages awaiting dispatch for the given
	 * clinic
	 * 
	 * @param session
	 *            Session
	 * @param clinicName
	 *            String
	 * @return List<Packages>
	 * @throws HibernateException
	 */

	@SuppressWarnings("unchecked")
	public static List<Packages> getPackagesInTransit(Session session,
			String clinicName) throws HibernateException {
		List<Packages> result;
		result = session
				.createQuery(
						"select distinct pack from Packages as pack where "
								+ "pack.clinic.clinicName =:clinic and "
								+ "pack.packageReturned = false and "
								+ "pack.packDate is not null and "
								+ "pack.dateLeft is not null and "
								+ "pack.dateReceived is null and "
								+ "pack.pickupDate is null")
				.setString("clinic", clinicName).list();
		return result;
	}

	/**
	 * This method returns a list of packages that have not yet been collected
	 * at a clinic.
	 * 
	 * @param session
	 *            Session
	 * @param clinicName
	 *            String
	 * @return java.util.List<Packages>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static java.util.List<Packages> getPackagesAwaitingCollection(
			Session session, String clinicName) throws HibernateException {
		java.util.List<Packages> result;

		result = session
				.createQuery(
						"select distinct pack from Packages as pack where "
								+ "pack.clinic.clinicName =:clinic and "
								+ "pack.packageReturned = false and "
								+ "pack.dateReceived is not null and "
								+ "pack.dateLeft is not null and "
								+ "pack.pickupDate is null")
				.setString("clinic", clinicName).list();
		return result;
	}

	/**
	 * 
	 * @param session
	 * @param patientId
	 * @return
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PackageDrugInfo> getPackageDrugInfoForPatient(
			Session session, String patientId, String packageId)
			throws HibernateException {

		java.util.List<PackageDrugInfo> result;

		result = session
				.createQuery(
						"select packInfo from PackageDrugInfo as packInfo where "
								+ "packInfo.packageId =:packageId and "
								+ "packInfo.patientId =:patientId order by id")
				.setString("packageId", packageId)
				.setString("patientId", patientId).list();
		return result;
	}

	/**
	 * Gets a specific package based on the packageId
	 * 
	 * @param session
	 * @param packageId
	 * @return Packages
	 * @throws HibernateException
	 */
	public static Packages getPackage(Session session, String packageId)
			throws HibernateException {
		Packages result;
		result = (Packages) session
				.createQuery(
						"select pack from Packages as pack where "
								+ "pack.packageId = :packageId")
				.setString("packageId", packageId.toUpperCase()).uniqueResult();
		return result;
	}

	// --------- METHODS FOR PACKAGES LEAVING MANAGER
	// ---------------------------------

	/**
	 * @param sess
	 *            Session
	 * @param packageToSave
	 * @throws HibernateException
	 */
	public static void savePackage(Session sess, Packages packageToSave)
			throws HibernateException {

		Prescription pre = packageToSave.getPrescription();

		if (pre != null) {
			packageToSave.setPrescription(pre);
			pre.getPackages().add(packageToSave);
		}

		sess.save(packageToSave);

		for (PackagedDrugs pd : packageToSave.getPackagedDrugs()) {
			StockManager.updateStockLevel(sess, pd.getStock());
		}

	}

	/**
	 * Method update.
	 * 
	 * @param sess
	 *            Session
	 * @param p
	 *            Packages
	 * @throws HibernateException
	 */
	public static void update(Session sess, Packages p)
			throws HibernateException {

		sess.saveOrUpdate(p);
	}

	// ---------- METHODS FOR PATIENT PACKAGING MANAGER ---------------

	/**
	 * Method printLabels.
	 * 
	 * @param sess
	 *            Session
	 * @param pdisForLabels
	 *            List<PackageDrugInfo>
	 * @param qtysForLabels
	 * @param pInfo
	 *            PackageInfo
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean printLabels(Session sess,
			List<PackageDrugInfo> pdisForLabels,
			Map<Object, Integer> qtysForLabels) throws HibernateException {

		int qtySummaryLabel = qtysForLabels.get(ScriptSummaryLabel.KEY);
		int qtyPackageLabel = qtysForLabels.get(PackageCoverLabel.KEY);
		int qtyNextAppointment = qtysForLabels
				.get(CommonObjects.NEXT_APPOINTMENT_KEY);

		List<Object> printerQueue = new ArrayList<Object>();

		for (int i = 0; i < pdisForLabels.size(); i++) {

			PackageDrugInfo pdi = pdisForLabels.get(i);

			// Check if Drug Label should be printed
			int qtyDrugLabel = qtysForLabels.get(pdi);
			if (qtyDrugLabel > 0
					&& (!pdi.isSideTreatment() || iDartProperties.printSideTreatmentLabels)) {
				for (int j = 0; j < pdi.getNumberOfLabels(); j++) {

					Drug theDrug = DrugManager.getDrug(sess, pdi.getDrugName());
					Form theForm = theDrug.getForm();
					// if there are clinical notes ...
					if (pdi.getNotes() == null) {
						pdi.setNotes("");
					}

					// Case 1: 1st label but not last. Also, 1st label for the
					// drug
					if (j == 0 && j != pdi.getNumberOfLabels() - 1
							&& pdi.isFirstBatchInPrintJob()) {
						pdi.setQtyInHand(pdi.getQtyInHand());
						// Case 2: 1st Label but not last. This is not the
						// 1st label for the drug. This occurs when multiple
						// batches are dispensed from for a single drug
					} else if (j == 0 && j != pdi.getNumberOfLabels() - 1) {
						pdi.setQtyInHand(iDARTUtil.removeAccumulated(pdi
								.getQtyInHand()));
					} else if (j == 0 && j == pdi.getNumberOfLabels() - 1
							&& pdi.isFirstBatchInPrintJob()) {
						pdi.setQtyInHand(pdi.getQtyInHand());
					} else if (j == 0 && j == pdi.getNumberOfLabels() - 1
							&& !pdi.isFirstBatchInPrintJob()) {
						pdi.setQtyInHand((iDARTUtil.removeAccumulated(pdi
								.getQtyInHand())));
					}
					// Case 3: not the 1st and not the last label
					else if (j != 0 && (j != pdi.getNumberOfLabels() - 1)) {
						pdi.setQtyInHand(iDARTUtil.removeAccumulated(pdi
								.getQtyInHand()));
						// Case 4: last label in this pdi but not last label for
						// the drug
					} else if (j == (pdi.getNumberOfLabels() - 1)
							&& pdi.isFirstBatchInPrintJob()) {
						pdi.setQtyInHand(iDARTUtil.removeAccumulated(pdi
								.getQtyInHand()));
					}
					// Case 5: last label in this pdi and for the drug
					else if (j == (pdi.getNumberOfLabels() - 1)) {
						pdi.setQtyInHand(pdi.getQtyInLastBatch());
					}

					if ("".equalsIgnoreCase(pdi.getQtyInHand())) {
						pdi.setQtyInHand("("
								+ pdi.getPackagedDrug().getStock().getDrug()
										.getPackSize() + ")");
					}

					String appointment = "";
					if (!pdi.isDispensedForLaterPickup()
							&& printNextAppointment(qtyNextAppointment)) {
						appointment = pdi.getDateExpectedString();
					}
					Object labelToPrint = createLabel(false, pdi, theForm,
							appointment);

					for (int k = 0; k < qtyDrugLabel; k++) {
						printerQueue.add(labelToPrint);
					}
					pdi.setFirstBatchInPrintJob(false);
				}
			}
		}

		if (qtyPackageLabel > 0) {
			// Print out the package cover label
			Object pc;
			PackageDrugInfo pdi = pdisForLabels.get(0);
			/*
			 * if (iDartProperties.labelType.equals(LabelType.EKAPA)) { pc = new
			 * EkapaLabelPackageCover(pharmacy.getName(), pharmacy
			 * .getPharmacist(), pharmacy.getStreet() + ", " +
			 * pharmacy.getCity() + ", Tel: " + pharmacy.getContactNo(),
			 * pInfo.getPrescriptionId(), pInfo .getClinic(), "Packed " +
			 * pInfo.getTheDateDispensedString(), "Issue " +
			 * pInfo.getPackageIndex() + " / " + pInfo.getPrescriptionDuration()
			 * + " script", pInfo .getPatNo()); } else {
			 */
			pc = new PackageCoverLabel(
					pdi.getPackageId(),
					pdi.getClinic(),
					"Packed "
							+ (pdi.getDispenseDate() == null ? ""
									: new SimpleDateFormat("dd MMM yyyy")
											.format(pdi.getDispenseDate())),
					LocalObjects.pharmacy.getPharmacyName(),
					LocalObjects.pharmacy.getPharmacist(),
					LocalObjects.pharmacy.getStreet() + ", "
							+ LocalObjects.pharmacy.getCity() + ", Tel: "
							+ LocalObjects.pharmacy.getContactNo(),
					"Issue "
							+ pdi.getPackageIndex()
							+ " / "
							+ (pdi.getPrescriptionDuration() >= 4 ? 
									(pdi.getPrescriptionDuration() / 4)
									+ " month"
									: pdi.getPrescriptionDuration() + " week"),
					pdi.getPatientId(), pdi.getPatientName(),
					(pdi.getDateExpectedString() == null
							|| !printNextAppointment(qtyNextAppointment) || pdi
							.getDateExpectedString().isEmpty()) ? ""
							: "Date Exp " + pdi.getDateExpectedString());
			// }

			for (int k = 0; k < qtyPackageLabel; k++) {
				printerQueue.add(pc);
			}
		}

		if (qtySummaryLabel > 0) {

			PackageDrugInfo pdi = pdisForLabels.get(0);

			/*
			 * // Format the next appointment Date SimpleDateFormat sdf = new
			 * SimpleDateFormat("dd MMM yyyy"); try { if
			 * (!"".equalsIgnoreCase(pdi.getDateExpectedString())) {
			 * nextAppointmentDate = sdf.format(sdf
			 * .parse(nextAppointmentDate)); } } catch (ParseException e) {
			 * log.error("Invalid apppointment Date"); e.printStackTrace(); }
			 */
			// Loops for number of labels
			for (int i = 0; i < pdisForLabels.size(); i = i + 7) {

				List<PackageDrugInfo> drugInfo = new ArrayList<PackageDrugInfo>();
				for (int j = i; j < i + 7; j++) {
					if (j < pdisForLabels.size()) {
						drugInfo.add(pdisForLabels.get(j));
					}
				}

				Object sl;
				ScriptSummaryLabel esml = new ScriptSummaryLabel(drugInfo);
				esml.setPharmacyName(LocalObjects.pharmacy.getPharmacyName());
				esml.setDispDate((pdi.getDispenseDate() == null ? ""
						: iDARTUtil.format(pdi.getDispenseDate())));
				esml.setPatientFirstName(pdisForLabels.get(0)
						.getPatientFirstName());
				esml.setPatientLastName(pdisForLabels.get(0)
						.getPatientLastName());
				esml.setPrescriptionId(String.valueOf(pdi.getPackagedDrug()
						.getParentPackage().getPackageId()));
				esml
						.setIssuesString(pdi.getPackageIndex()
								+ " of a "
								+ (pdi.getPrescriptionDuration() >= 4 ? 
										(pdi.getPrescriptionDuration() / 4)	+ " month script"
										: pdi.getPrescriptionDuration()
												+ " week script"));
				esml
						.setBoldIssuesString(pdi.getPackageIndex() >= pdi.getPrescriptionDuration());
				if (printNextAppointment(qtyNextAppointment)) {
					esml.setNextAppointmentDate(pdi.getDateExpectedString());
				} else {
					esml.setNextAppointmentDate("");
				}
				esml.setFolderNumber(pdisForLabels.get(0).getPatientId());
				int issueNo = i / 7 + 1;
				int issuetotal = ((pdisForLabels.size() - 1) / 7) + 1;
				esml.setLabelNumber("(Label " + issueNo + " of " + issuetotal
						+ ")");

				sl = esml;

				for (int k = 0; k < qtySummaryLabel; k++) {
					printerQueue.add(sl);
				}
			}
		}

		new PrintThread(printerQueue);
		return true;
	}

	/**
	 * This method creates a String which will be displayed on the drug label
	 * For example. If a patient received 60 new drugs and 2 accumulated drugs
	 * then the String will be (60 + 2)
	 * 
	 * Note if the total amount dispensed is required, set the packSize = to the
	 * disp value. For example. If a patient receives 180 new drugs then the
	 * method will return (180)
	 * 
	 * Note: if you would like the method to return the amount of drugs in the
	 * last container then set the boolean returnQtyInLastContainer to true.
	 * 
	 * @param accDrugs
	 * @param disp
	 * @param currentDrugName
	 * @param firstBatch
	 * @return
	 */
	public static String getQuantityDispensedForLabel(
			Set<AccumulatedDrugs> accDrugs, int disp, String currentDrugName,
			int packSize, boolean returnQtyInLastContainer, boolean firstBatch) {

		int accum = 0;
		int noOfPacks = 0;

		if (accDrugs != null && firstBatch) {
			for (AccumulatedDrugs drugs : accDrugs) {
				if (currentDrugName.equalsIgnoreCase(drugs.getPillCount()
						.getDrug().getName())) {
					accum = drugs.getPillCount().getAccum();
				}
			}
		}

		noOfPacks = (int) Math.ceil(((double) disp / packSize));

		if (accum == 0) {
			if (disp > packSize) {
				if (returnQtyInLastContainer)
					return "(" + (disp - (packSize * (noOfPacks - 1))) + ")";

				return "(" + packSize + ")";
			} else
				return "(" + disp + ")";
		}

		else {
			if (disp > packSize) {
				if (returnQtyInLastContainer)
					return "(" + (disp - (packSize * (noOfPacks - 1))) + ")";

				return "(" + packSize + " + " + accum + ")";
			} else
				return "(" + disp + " + " + accum + ")";
		}
	}

	/**
	 * @param pInfo
	 * @param printPackageCover
	 * @param leaveQuantitiesBlank
	 * @param pdi
	 * @param theForm
	 * @param patientName
	 * @return
	 */
	private static Object createLabel(boolean leaveQuantitiesBlank,
			PackageDrugInfo pdi, Form theForm, String nextAppointmentDate) {

		String amountPerTime = "";
		String form = theForm.getFormLanguage1();
		String timesPerDay = "";
		if (leaveQuantitiesBlank) {
			amountPerTime = "";
		} else if ((pdi.getAmountPerTime().equals("0.5"))
				&& ((theForm.getFormLanguage1().startsWith("tablet")) || (theForm
						.getFormLanguage1().startsWith("lozen")))) {
			// note use of startsWith above for the cases of "lozenge (s)" and
			// "tablet (s)"

			amountPerTime = "Half";
			if (theForm.getFormLanguage1().startsWith("tablet")) {
				form = "a tablet";
			} else {
				form = "a lozenge";
			}

			timesPerDay = Integer.toString(pdi.getTimesPerDay());
		} else {
			amountPerTime = pdi.getAmountPerTime();
			timesPerDay = Integer.toString(pdi.getTimesPerDay());
		}

		Object labelToPrint;

		// Create Label
		DrugLabel pdl = new DrugLabel();
		pdl.setPharmHeaderName(LocalObjects.pharmacy.getPharmacyName());
		pdl.setPharmHeaderPharmacist(LocalObjects.pharmacy.getPharmacist());
		pdl.setPharmHeaderLocation(LocalObjects.pharmacy.getStreet() + ", "
				+ LocalObjects.pharmacy.getCity() + ", Tel: "
				+ LocalObjects.pharmacy.getContactNo());
		pdl.setDrug(pdi.getDrugName() + " " + pdi.getQtyInHand());

		pdl.setDispInstructions1((pdi.getSpecialInstructions1() == null ? ""
				: pdi.getSpecialInstructions1()));
		pdl.setDispTakeLang1(theForm.getActionLanguage1());
		pdl.setDispTakeLang2(theForm.getActionLanguage2());
		pdl.setDispTakeLang3(theForm.getActionLanguage3());
		pdl.setDispFormLang1(form);
		pdl.setDispFormLang2(theForm.getFormLanguage2());
		pdl.setDispFormLang3(theForm.getFormLanguage3());
		pdl.setDispTimesPerDayLang1((pdl.getDispTakeLang1() == null || pdl
				.getDispTakeLang1().equalsIgnoreCase("")) ? ""
				: CommonObjects.timesPerDayLanguage1);
		pdl.setDispTimesPerDayLang2((pdl.getDispTakeLang2() == null || pdl
				.getDispTakeLang2().equalsIgnoreCase("")) ? ""
				: CommonObjects.timesPerDayLanguage2);
		pdl.setDispTimesPerDayLang3((pdl.getDispTakeLang3() == null || pdl
				.getDispTakeLang3().equalsIgnoreCase("")) ? ""
				: CommonObjects.timesPerDayLanguage3);
		pdl.setDispTabletNum(amountPerTime);
		pdl.setDispTimesPerDay(timesPerDay);
		pdl.setPatientFirstName(pdi.getPatientFirstName());
		pdl.setPatientLastName(pdi.getPatientLastName());
		pdl.setPatientId(pdi.getPatientId());
		pdl.setPackageExpiryDate(new SimpleDateFormat("MM/yyyy").format(pdi
				.getExpiryDate()));
		// Format the date
		pdl.setPackagePackagedDate(new SimpleDateFormat("dd MMM yy").format(pdi
				.getDispenseDate()));
		pdl.setNextAppointmentDate(nextAppointmentDate);
		pdl.setDispInstructions2((pdi.getSpecialInstructions2() == null ? ""
				: pdi.getSpecialInstructions2()));
		pdl
				.setIssuesString(pdi.getPackageIndex()
						+ " of "
						+ (pdi.getPrescriptionDuration() >= 4 ? 
								(pdi.getPrescriptionDuration() / 4 + " month")
								: pdi.getPrescriptionDuration() + " week"));
		pdl.setBoldIssuesString(pdi.getPackageIndex() >= pdi.getPrescriptionDuration());
		pdl.setBatchNumber(pdi.getBatchNumber());
		pdl.setClinicNotes(pdi.getNotes());
		pdl.init();
		labelToPrint = pdl;

		return labelToPrint;
	}

	/**
	 * 
	 * This method finds the last package the patient picked up
	 * 
	 * @param sess
	 *            Session
	 * @param pat
	 *            Patient
	 * @return Packages
	 * @throws HibernateException
	 */

	public static Packages getLastPackagePickedUp(Session sess, Patient pat)
			throws HibernateException {

		List<Packages> patientsPackages = getAllCollectedPackagesForPatient(
				sess, pat);

		if (patientsPackages.size() >= 1)
			return patientsPackages.get(0);
		else
			return null;

	}

	/**
	 * 
	 * This method finds the last package made for the patient (even if it
	 * wasn't picked up)
	 * 
	 * @param sess
	 *            Session
	 * @param pat
	 *            Patient
	 * @return Packages
	 * @throws HibernateException
	 */

	public static Packages getLastPackageMade(Session sess, Patient pat)
			throws HibernateException {

		List<Packages> patientsPackages = getAllPackagesForPatient(sess, pat);

		if (patientsPackages.size() >= 1)
			return patientsPackages.get(0);
		else
			return null;

	}

	/**
	 * Returns the last patient who had a package created by the specified user
	 * 
	 * @param session
	 * @return
	 * @throws HibernateException
	 */
	public static Patient getLastPatientDispensedToByUser(Session session,
			User user) throws HibernateException {
		Query patIdQuery = session
				.createQuery("select patientId from PackageDrugInfo pdi where pdi.cluser = :user"
						+ " group by patientId, dispensedate order by dispensedate desc");
		patIdQuery.setParameter("user", user);
		String patientId = (String) patIdQuery.setMaxResults(1).uniqueResult();

		if (patientId == null)
			return null;

		Query query = session
				.createQuery("from Patient p where patientId = :patientid");
		query.setParameter("patientid", patientId);
		Patient result = (Patient) query.uniqueResult();

		return result;
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
		if (packages != null) {
			for (Packages p : packages) {
				if (p.getPickupDate() == null) {
					break;
				} else if (p.getPickupDate().after(latestDate)) {
					latestDate = p.getPickupDate();
					pack = p;
				}
			}
		}
		return pack;
	}

	/**
	 * This method removes all pillcounts from the database. This method is
	 * usually called after removing the accumulatedDrugs
	 * 
	 * @param session
	 * @param pillCounts
	 * @return
	 */
	public static boolean removePillcounts(Session session,
			Set<PillCount> pillCounts) {
		boolean result = false;
		for (PillCount count : pillCounts) {
			session.delete(count);
			result = true;
		}
		return result;
	}

	/**
	 * this method removes all the accumulated drugs from the database.
	 * 
	 * @param session
	 * @param accumulatedDrugs
	 */
	public static void removeAccumulatedDrugs(Session session,
			Set<AccumulatedDrugs> accumulatedDrugs) {
		for (AccumulatedDrugs count : accumulatedDrugs) {
			session.delete(count);
		}
	}

	/**
	 * Gets the value of the next appoinment date from the labels map. 1 = true,
	 * 0 = false if nothing is set in the map it defaults to true
	 * 
	 * @param value
	 * @return
	 */
	private static boolean printNextAppointment(int value) {

		if (Integer.valueOf(value) != null && value == 1) {
			return true;
		} else if (Integer.valueOf(value) != null && value == 0) {
			return false;
		} else {
			return true;
		}
	}
}
