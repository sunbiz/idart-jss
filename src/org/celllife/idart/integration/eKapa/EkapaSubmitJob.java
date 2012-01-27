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

					List<PackageDrugInfo> pdiList = TemporaryRecordsManager
					.getUnsubmittedPackageDrugInfos(hSession);
					List<AdherenceRecord> adhList = TemporaryRecordsManager
					.getUnsubmittedAdherenceRecords(hSession);
					List<DeletedItem> delList = TemporaryRecordsManager
					.getUnsubmittedDeletedItems(hSession);

					List<PackageDrugInfo> submittedPackageDrugInfos = new ArrayList<PackageDrugInfo>();
					List<AdherenceRecord> submittedAdherenceRecords = new ArrayList<AdherenceRecord>();
					List<DeletedItem> submittedDeletedItems = new ArrayList<DeletedItem>();

					boolean saveSuccessful = false;

					for (PackageDrugInfo pdi : pdiList) {

						saveSuccessful = submitPackageDrugInfo(pdi);

						if (saveSuccessful) {
							log
							.info("Successfully saved dispensing record to eKapa. Record id: "
									+ pdi.getId());
							submittedPackageDrugInfos.add(pdi);
						}

					}

					for (AdherenceRecord adh : adhList) {
						saveSuccessful = submitAdherenceRecord(adh);
						if (saveSuccessful) {
							log
							.info("Successfully saved adherence record to eKapa. Record id: "
									+ adh.getId());
							submittedAdherenceRecords.add(adh);
						}

					}

					for (DeletedItem del : delList) {
						saveSuccessful = submitDeletedItem(del);
						if (saveSuccessful) {
							log
							.info("Successfully removed deleted item from eKapa. Record id: "
									+ del.getDeletedItemId());
							submittedDeletedItems.add(del);
						}
					}

					TemporaryRecordsManager.updateSubmittedPackageDrugInfos(
							hSession, submittedPackageDrugInfos);
					TemporaryRecordsManager.deleteSubmittedAdherenceRecords(
							hSession, submittedAdherenceRecords);
					TemporaryRecordsManager.deleteSubmittedDeletedItems(
							hSession, submittedDeletedItems);
					hSession.flush();
					tx.commit();
				}
			}
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			log.error("Error submitting data to ekapa", e);
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

	/**
	 * Method getStoredProcsConnection.
	 * 
	 * @return boolean
	 */
	private boolean getStoredProcsConnection() {

		try {
			sp = new StoredProcs();
			boolean connected = sp.init();
			if (!connected) {
				sp.closeConnection();
				sp = null;
				return false;
			} else
				return true;
		} catch (SQLException e) {
			log.error("Could not create StoredProcs due to SQL Exception : "
					+ e.getMessage());
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
		boolean saveSuccessful = false;
		try {
			log.info("Attempting to submit AdherenceRecordTmp: "
					+ adh.getPillCountId() + "," + adh.getPawcNo() + ","
					+ adh.getCountDate() + "," + adh.getDaysSinceVisit() + ","
					+ adh.getDaysSinceVisit() + "," + adh.getDaysCarriedOver()
					+ "," + adh.getDaysInHand() + "," + adh.getAdherence()
					+ "," + "Not Available" + "," + adh.getCluser());
			saveSuccessful = sp.submitPillCount(adh);

		} catch (SQLException e) {
			saveSuccessful = false;
			log
			.error("SQLException while trying to submit temp records to eKapa");
			log.error(e);

			if ((e.getSQLState() != null)
					&& (!e.getSQLState().startsWith("08"))) // not
				// a
				// connection
				// error
			{
				adh.setInvalid(true);
				log.error("Marking adherence record as invalid. Id:"
						+ adh.getId());
			}

		}

		return saveSuccessful;
	}

	/**
	 * Method submitPackageDrugInfo.
	 * 
	 * @param pdi
	 *            PackageDrugInfo
	 * @return boolean
	 */
	private boolean submitPackageDrugInfo(PackageDrugInfo pdi) {
		boolean saveSuccessful = false;
		try {

			saveSuccessful = sp.submitDispensingInfo(pdi);
		} catch (SQLException e) {
			saveSuccessful = false;
			log
			.error("SQLException while trying to submit temp records to eKapa");
			log.error(e);
			if ((e.getSQLState() != null)
					&& (!e.getSQLState().startsWith("08"))) // not
				// a
				// connection
				// error
			{
				pdi.setInvalid(true);
				log.error("Marking pdi as invalid. Id:" + pdi.getId());
			}
		}

		return saveSuccessful;

	}

	/**
	 * Method submitDeletedItem.
	 * 
	 * @param del
	 *            DeletedItem
	 * @return boolean
	 */
	private boolean submitDeletedItem(DeletedItem del) {
		boolean saveSuccessful = false;
		try {

			if (del.getItemType().equals("AdherenceRecord")) {
				saveSuccessful = sp.deleteAdherenceRecord(del);
			} else if (del.getItemType().equals("PackageDrugInfo")) {
				saveSuccessful = sp.deleteDispensingRecord(del);
			}

		} catch (SQLException e) {
			saveSuccessful = false;
			log
			.error("SQLException while trying to submit temp records to eKapa");
			log.error(e);

			if ((e.getSQLState() != null)
					&& (!e.getSQLState().startsWith("08"))) // not
				// a
				// connection
				// error
			{
				del.setInvalid(true);
				log.error("Marking deleted item record as invalid. Id:"
						+ del.getId());
			}

		}

		return saveSuccessful;
	}

}
