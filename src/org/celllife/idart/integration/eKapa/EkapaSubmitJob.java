package org.celllife.idart.integration.eKapa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.manager.TemporaryRecordsManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.database.hibernate.tmp.DeletedItem;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class EkapaSubmitJob implements Job {

	private final Logger log = Logger.getLogger(EkapaSubmitJob.class.getName());

	public static final String GROUP_NAME = "ekapa";
	public static final String JOB_NAME = "ekapaJob";

	private StoredProcs sp;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("ekapa submission job starting");

		getStoredProcsConnection();

		Transaction tx = null;
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			if (TemporaryRecordsManager.hasUnsubmittedRecords(hSession)) {

				if (getStoredProcsConnection()) {

					tx = hSession.beginTransaction();

					processPackagedDrugs(hSession);

					processAdherenceRecords(hSession);

					processDeletedItems(hSession);

					hSession.flush();
					tx.commit();
				}
			}
		} catch (Exception e) {
			log.error("Error submitting data to ekapa", e);
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			if (hSession != null) {
				hSession.close();
			}
			if (sp != null) {
				sp.closeConnection();
			}
		}
		log.info("ekapa submission job completed");
	}

	private void processDeletedItems(Session hSession) {
		List<DeletedItem> delList = TemporaryRecordsManager
				.getUnsubmittedDeletedItems(hSession);
		List<DeletedItem> submittedDeletedItems = new ArrayList<DeletedItem>();

		for (DeletedItem del : delList) {
			if (submitDeletedItem(del)) {
				log.info("Successfully removed deleted item from eKapa. Record id: "
						+ del.getDeletedItemId());
				submittedDeletedItems.add(del);
			}
		}
		TemporaryRecordsManager.deleteSubmittedDeletedItems(hSession,
				submittedDeletedItems);
	}

	private void processAdherenceRecords(Session hSession) {
		List<AdherenceRecord> adhList = TemporaryRecordsManager
				.getUnsubmittedAdherenceRecords(hSession);
		List<AdherenceRecord> submittedAdherenceRecords = new ArrayList<AdherenceRecord>();
		for (AdherenceRecord adh : adhList) {
			if (submitAdherenceRecord(adh)) {
				log.info("Successfully saved adherence record to eKapa. Record id: "
						+ adh.getId());
				submittedAdherenceRecords.add(adh);
			}
		}
		TemporaryRecordsManager.deleteSubmittedAdherenceRecords(hSession,
				submittedAdherenceRecords);
	}

	private void processPackagedDrugs(Session hSession) {
		List<PackageDrugInfo> pdiList = TemporaryRecordsManager
				.getUnsubmittedPackageDrugInfos(hSession);

		List<PackageDrugInfo> submittedPackageDrugInfos = new ArrayList<PackageDrugInfo>();

		for (PackageDrugInfo pdi : pdiList) {
			if (submitPackageDrugInfo(pdi)) {
				log.info("Successfully saved dispensing record to eKapa. Record id: "
						+ pdi.getId());
				submittedPackageDrugInfos.add(pdi);
			}
		}
		TemporaryRecordsManager.updateSubmittedPackageDrugInfos(hSession,
				submittedPackageDrugInfos);
	}

	/**
	 * Method getStoredProcsConnection.
	 * 
	 * @return boolean
	 */
	private boolean getStoredProcsConnection() {
		try {
			sp = new StoredProcs();
			if (!sp.init()) {
				sp.closeConnection();
				sp = null;
				return false;
			} else
				return true;
		} catch (SQLException e) {
			log.error("Could not create StoredProcs due to SQL Exception", e);
			sp = null;
			return false;
		}
	}

	/**
	 * Method submitAdherenceRecord.
	 * 
	 * @param adh
	 *            AdherenceRecord
	 * @return boolean
	 */
	private boolean submitAdherenceRecord(AdherenceRecord adh) {
		try {
			log.info("Attempting to submit AdherenceRecordTmp: "
					+ adh.getPillCountId() + "," + adh.getPawcNo() + ","
					+ adh.getCountDate() + "," + adh.getDaysSinceVisit() + ","
					+ adh.getDaysSinceVisit() + "," + adh.getDaysCarriedOver()
					+ "," + adh.getDaysInHand() + "," + adh.getAdherence()
					+ "," + "Not Available" + "," + adh.getCluser());
			return sp.submitPillCount(adh);

		} catch (SQLException e) {
			log.error(
					"SQLException while trying to submit temp records to eKapa",
					e);

			if ((e.getSQLState() != null)
					&& (!e.getSQLState().startsWith("08"))) {
				// not a connection error
				log.error("Marking adherence record as invalid. Id:"
						+ adh.getId());
				return true;
			}
			return false;
		}
	}

	/**
	 * Method submitPackageDrugInfo.
	 * 
	 * @param pdi
	 *            PackageDrugInfo
	 * @return boolean
	 */
	private boolean submitPackageDrugInfo(PackageDrugInfo pdi) {
		try {

			return sp.submitDispensingInfo(pdi);
		} catch (SQLException e) {
			log.error("SQLException while trying to submit temp records to eKapa");
			log.error(e);
			if ((e.getSQLState() != null)
					&& (!e.getSQLState().startsWith("08"))) {
				// not a connection error
				pdi.setInvalid(true);
				log.error("Marking pdi as invalid. Id:" + pdi.getId());
				return true;
			}
			return false;
		}
	}

	/**
	 * Method submitDeletedItem.
	 * 
	 * @param del
	 *            DeletedItem
	 * @return boolean
	 */
	private boolean submitDeletedItem(DeletedItem del) {
		try {
			if (del.getItemType().equals(DeletedItem.ITEM_ADHERANCE)) {
				return sp.deleteAdherenceRecord(del);
			} else if (del.getItemType().equals(DeletedItem.ITEM_PACKAGE_DRUG)) {
				return sp.deleteDispensingRecord(del);
			}
			return false;
		} catch (SQLException e) {
			log.error(
					"SQLException while trying to submit temp records to eKapa",
					e);

			if ((e.getSQLState() != null)
					&& (!e.getSQLState().startsWith("08"))) {
				// not a connection error
				log.error("Marking deleted item record as invalid. Id:"
						+ del.getId());
				return true;
			}
			return false;

		}
	}

}
