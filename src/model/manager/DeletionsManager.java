package model.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Logging;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.database.hibernate.tmp.DeletedItem;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.facade.PillCountFacade;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class DeletionsManager {

	private static Logger log = Logger.getLogger(DeletionsManager.class);

	private static PillCountFacade pillFacade = null;

	/**
	 * If this is the first package, and it is a new patient episode, then
	 * return true.
	 * 
	 * @param sess
	 * @param packs
	 *            package being deleted
	 * @return true If this is the first package, and it is a new patient
	 *         episode
	 */
	public static boolean isFirstPackageOnNewPatientEpisode(final Packages packs) {
		// get new session in case package has been deleted from current
		// session.
		Session sess = HibernateUtil.getNewSession();
		Patient p = packs.getPrescription().getPatient();
		Episode epi = PatientManager.getMostRecentEpisode(p);
		String startReason = epi.getStartReason();
		if (startReason.equalsIgnoreCase(Episode.REASON_NEW_PATIENT)) {
			List<Packages> packlist = PackageManager.getAllPackagesForPatient(
					sess, p);
			Packages firstARVPackage = PackageManager
					.getFirstPackageWithARVs(packlist);
			if (firstARVPackage != null
					&& firstARVPackage.getId() == packs.getId())
				return true;
		}
		return false;
	}

	/**
	 * Method removeThisPackage.
	 * 
	 * @param session
	 *            Session
	 * @param packageToRemove
	 *            Packages
	 * @throws HibernateException
	 */
	public static void removePackage(Session session,
			Packages packageToRemove) throws HibernateException {

		int prescriptionId = packageToRemove.getPrescription().getId();
		List<DeletedItem> delList = new ArrayList<DeletedItem>();

		// get stock batches to check units remaining for, once package has
		// been removed
		// also record the drugs in the package for the log
		Set<Stock> batchesToCheck = new HashSet<Stock>();

		Iterator<PackagedDrugs> packDrugIt = packageToRemove.getPackagedDrugs()
				.iterator();
		while (packDrugIt.hasNext()) {
			PackagedDrugs pd = packDrugIt.next();
			Stock theStock = StockManager.getStock(session, pd.getStock()
					.getId());
			batchesToCheck.add(theStock);
		}
		String drugsInPack = PackageManager.getShortPackageContentsString(
				session, packageToRemove);

		// AccumulatedDrugs to remove for this package
		Set<AccumulatedDrugs> accumsToRemove = packageToRemove
				.getAccumulatedDrugs();

		// get next appointment if it has been set
		Appointment appToDelete = null;
		Patient pat = null;
		if (packageToRemove.getDateReceived() != null) {
			pat = (Patient) session.createQuery(
					"select pre.patient "
							+ "from Prescription as pre where pre.id = "
							+ prescriptionId).uniqueResult();

			appToDelete = PatientManager.getLatestAppointmentForPatient(pat, true);

			if (appToDelete != null) {
				//session.createQuery("delete Appointment where id = :appId")
				//		.setInteger("appId", appToDelete.getId()).executeUpdate();
				
				pat.getAppointments().remove(appToDelete);
				
				// get previous appointment and make it the active one
				Appointment appToUpdate = PatientManager.getLatestAppointmentForPatient(pat, false);
				if( appToUpdate != null) {
					appToUpdate.setVisitDate(null);
				}
				session.save(pat);
			}
		}

		// Delete PillCounts
		removePillCountInfo(session, packageToRemove, delList);

		// get any temp records that have not yet been sent
		log.info("deleting PackageDrugInfo records for package: " + packageToRemove.getId());
		TemporaryRecordsManager.deletePackageDrugInfosForPackage(session, packageToRemove);

		Iterator<PackagedDrugs> packDrugToRemoveItr = packageToRemove
				.getPackagedDrugs().iterator();
		while (packDrugToRemoveItr.hasNext()) {

			PackagedDrugs pd = packDrugToRemoveItr.next();
			String packageDrugsDelete = "delete PackagedDrugs where id = :pId";
			session.createQuery(packageDrugsDelete).setInteger("pId",
					pd.getId()).executeUpdate();
			delList.add(new DeletedItem(pd.getId(), DeletedItem.ITEM_PACKAGE_DRUG));
			log.info("deleting PackageDrug" + pd.getId());

		}

		Iterator<AccumulatedDrugs> accDrugToRemoveItr = accumsToRemove
				.iterator();
		while (accDrugToRemoveItr.hasNext()) {
			String accDrugsDelete = "delete AccumulatedDrugs where id = :pId";
			session.createQuery(accDrugsDelete).setInteger("pId",
					accDrugToRemoveItr.next().getId()).executeUpdate();

		}

		String packageDelete = "delete Packages where id = :pId";
		session.createQuery(packageDelete).setInteger("pId",
				packageToRemove.getId()).executeUpdate();

		// check to see if, by returning stock, a batch that has
		// hasUnitsRemaining ='F' should now have hasUnitsRemaing='T'

		Iterator<Stock> stockIt = batchesToCheck.iterator();

		while (stockIt.hasNext()) {
			Stock theStock = stockIt.next();
			if (theStock.getHasUnitsRemaining() == 'F') {
				log.info("Updating batch: " + theStock.getBatchNumber());
				theStock.setHasUnitsRemaining('T');
				session.saveOrUpdate(theStock);

			}
			StockManager.updateStockLevel(session, theStock);
		}

		// create a log entry
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(session));
		logging.setItemId(String.valueOf(packageToRemove.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Delete Package");
		logging.setMessage("Deleted package " + packageToRemove.getPackageId()
				+ " (" + drugsInPack + ")");
		session.save(logging);

		TemporaryRecordsManager.saveDeletedItemsToDB(session, delList);

	}

	/**
	 * Method removeRegimen.
	 * 
	 * @param sess
	 *            Session
	 * @param reg
	 *            Regimen
	 * @throws HibernateException
	 */
	public static void removeRegimen(Session sess, Regimen reg)
			throws HibernateException {

		// delete regimen drugs, if any
		for (RegimenDrugs rd : reg.getRegimenDrugs()) {
			String regDelete = "delete RegimenDrugs where id = :rdId";
			sess.createQuery(regDelete).setInteger("rdId", rd.getId())
					.executeUpdate();
			log.info("deleting RegimenDrugs record " + rd.getId());
		}
		String regDrugsDelete = "delete Regimen where id = :rId";
		sess.createQuery(regDrugsDelete).setInteger("rId", reg.getId())
				.executeUpdate();

		log.info("deleting Regimen record " + reg.getId());

	}

	/**
	 * Method removeThisDrug.
	 * 
	 * @param session
	 *            Session
	 * @param pdToRemove
	 *            PackagedDrugs
	 * @throws HibernateException
	 */
	@SuppressWarnings( { "cast", "unchecked" })
	public static void removePackagedDrug(Session session,
			PackagedDrugs pdToRemove, Packages fromPackage)
			throws HibernateException {

		List<DeletedItem> delList = new ArrayList<DeletedItem>();
		delList.add(new DeletedItem(pdToRemove.getId(), DeletedItem.ITEM_PACKAGE_DRUG));

		// get stock batch to check hasUnitsRemaining
		Stock theStockToCheck = StockManager.getStock(session, pdToRemove
				.getStock().getId());

		// get PackageDrugInfo to delete, if any
		PackageDrugInfo pdi = TemporaryRecordsManager.getPDIforPackagedDrug(
				session, pdToRemove);

		// delete pdi, if any
		if (pdi != null) {
			String pdiDelete = "delete PackageDrugInfo where id = :pdiId";
			session.createQuery(pdiDelete).setInteger("pdiId", pdi.getId())
					.executeUpdate();
			log.info("deleting PackageDrugInfo record " + pdi.getId());
		}

		// delete the packaged drug
		String packageDrugsDelete = "delete PackagedDrugs where id = :pId";
		session.createQuery(packageDrugsDelete).setInteger("pId",
				pdToRemove.getId()).executeUpdate();

		log.info("deleting PackageDrug record " + pdToRemove.getId());

		StockManager.updateStockLevel(session, theStockToCheck);

		// create a list of the drugs are remaining in the package
		List<PackagedDrugs> remainingDrugs;
		String remainingDrugsQuery = "from PackagedDrugs pd where pd.parentPackage = :packageId";
		remainingDrugs = (List<PackagedDrugs>) session.createQuery(
				remainingDrugsQuery).setInteger("packageId",
				fromPackage.getId()).list();

		// reset indices for remaining drugs such that they start at 0 and
		// maintain the same ordering
		for (int i = 0; i < remainingDrugs.size(); i++) {
			String pdIndexUpdate = "update PackagedDrugs set packageddrugsindex = :newIndex where id = :currentPd";
			session.createQuery(pdIndexUpdate).setInteger("newIndex", i)
					.setInteger("currentPd", remainingDrugs.get(i).getId())
					.executeUpdate();
			log.info("Updating PackagedDrug Index to " + i);
		}

		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(session));
		logging.setItemId(String.valueOf(pdToRemove.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Delete drug from Package");
		logging.setMessage("Deleted drug "
				+ pdToRemove.getStock().getDrug().getName() + " x "
				+ pdToRemove.getAmount() + " from package "
				+ pdToRemove.getParentPackage().getId());
		logging.setMessage("Reset Package's PackagedDrug indices");
		session.save(logging);

		// store the DeletedItem to be deleted from eKapa later
		TemporaryRecordsManager.saveDeletedItemsToDB(session, delList);

	}

	/**
	 * Method removeUndispensedStock.
	 * 
	 * @param session
	 *            Session
	 * @param theStock
	 *            Stock
	 * @throws HibernateException
	 */
	public static void removeUndispensedStock(Session session, Stock theStock)
			throws HibernateException {

		// delete any stock levels for this stock
		StockLevel sl = StockManager.getCurrentStockLevel(session, theStock);

		if (sl != null) {
			String stockLevelDelete = "delete StockLevel where id = :stockLevelId";
			session.createQuery(stockLevelDelete).setInteger("stockLevelId",
					sl.getId()).executeUpdate();
			log.info("deleting Stock level for stock " + theStock.getId());
		}

		String stockDelete = "delete Stock where id = :stockId";
		session.createQuery(stockDelete)
				.setInteger("stockId", theStock.getId()).executeUpdate();
		log.info("deleting Stock record " + theStock.getId());

		// log the transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(session));
		logging.setItemId(String.valueOf(theStock.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Delete Stock");
		logging
				.setMessage("Deleted "
						+ theStock.getUnitsReceived()
						+ " units of '"
						+ theStock.getDrug().getName()
						+ "' (batch "
						+ ((theStock.getBatchNumber() == null || theStock.getBatchNumber().isEmpty()) ? "not captured"
								: theStock.getBatchNumber())
						+ ", expires "
						+ new SimpleDateFormat("MMM yy").format(theStock
								.getExpiryDate())
						+ "). Received at "
						+ theStock.getStockCenter().getStockCenterName()
						+ " on "
						+ new SimpleDateFormat("dd MMM yy").format(theStock
								.getDateReceived())
						+ ". Unit cost ZAR "
						+ (theStock.getUnitPrice() == null ? "not captured"
								: theStock.getUnitPrice()));

		session.save(logging);

	}

	/**
	 * Method removeUndispensedPrescription.
	 * 
	 * @param session
	 *            Session
	 * @param thePrescription
	 *            Prescription
	 * @throws HibernateException
	 */
	public static void removeUndispensedPrescription(Session session,
			Prescription thePrescription) throws HibernateException {

		// first need to delete the prescription from the patient's
		// Set<Prescriptions>
		thePrescription.getPatient().getPrescriptions().remove(thePrescription);

		// delete the prescription from the session and flush to the database
		// immediately
		// hibernate annotations are defined such that this will cascade
		// relevant PrescribedDrug deletes
		session.delete(thePrescription);
		session.flush();

		// log the transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(session));
		logging.setItemId(String.valueOf(thePrescription.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Delete Prescription");
		logging.setMessage("Deleted Prescription "
				+ thePrescription.getPrescriptionId() + " from Patient "
				+ thePrescription.getPatient().getPatientId());
		session.save(logging);
	}

	/**
	 * Method removeAccumulatedDrug.
	 * 
	 * @param session
	 *            Session
	 * @param drugs
	 *            AccumulatedDrugs
	 * @throws HibernateException
	 */
	public static void removeAccumulatedDrug(Session session,
			AccumulatedDrugs drugs) throws HibernateException {

		String accDrugsDelete = "delete AccumulatedDrugs where id = :pId";
		session.createQuery(accDrugsDelete).setInteger("pId", drugs.getId())
				.executeUpdate();

		// log the transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(session));
		logging.setItemId(String.valueOf(drugs.getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Delete Accumulated Drugs");
		logging.setMessage("Deleted Accumulated Drugs " + drugs.getId());
		session.save(logging);

	}

	/**
	 * This Method is used to remove a pillcount from a specific package.
	 * 
	 * @param session
	 * @param fromPackage
	 * @param delList
	 * @throws HibernateException
	 */
	private static void removePillCountInfo(Session session,
			Packages fromPackage, List<DeletedItem> delList)
			throws HibernateException {

		List<AdherenceRecord> adhList = new ArrayList<AdherenceRecord>();
		pillFacade = new PillCountFacade(session);
		// pill counts to remove for this package
		Set<PillCount> pcsToRemove = pillFacade
				.getPillCountsReturnedFromThisPackage(fromPackage);

		// get temp records to remove
		for (PillCount pc : pcsToRemove) {
			AdherenceRecord adh = TemporaryRecordsManager
					.getAdherenceRecordsForPillCount(session, pc);
			if (adh != null) {
				adhList.add(adh);
			}
		}

		// Delete temp records
		Iterator<AdherenceRecord> adhToRemoveItr = adhList.iterator();
		while (adhToRemoveItr.hasNext()) {
			AdherenceRecord adh = adhToRemoveItr.next();
			String adhDelete = "delete AdherenceRecord where id = :adhId";

			session.createQuery(adhDelete).setInteger("adhId", adh.getId())
					.executeUpdate();

			log.info("deleting adherence record " + adh.getId());
		}

		Iterator<PillCount> pcToRemoveItr = pcsToRemove.iterator();
		while (pcToRemoveItr.hasNext()) {
			PillCount pc = pcToRemoveItr.next();
			String pcDelete = "delete PillCount where id = :pcId";

			session.createQuery(pcDelete).setInteger("pcId", pc.getId())
					.executeUpdate();

			delList.add(new DeletedItem(pc.getId(), DeletedItem.ITEM_ADHERANCE));

			log.info("deleting pill count " + pc.getId());
		}
	}

	/**
	 * This Method is used to remove a pillcount from a specific package. The
	 * List of deleted items are saved by the method and populated by the helper
	 * method
	 * 
	 * @param session
	 * @param fromPackage
	 * @throws HibernateException
	 */
	public static void removePillCountInfo(Session session, Packages fromPackage)
			throws HibernateException {

		List<DeletedItem> delList = new ArrayList<DeletedItem>();

		// Call helper method
		removePillCountInfo(session, fromPackage, delList);
		TemporaryRecordsManager.saveDeletedItemsToDB(session, delList);
	}
}
