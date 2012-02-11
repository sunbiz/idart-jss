package model.manager;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.database.hibernate.tmp.DeletedItem;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class TemporaryRecordsManager {

	private static Log log = LogFactory.getLog(TemporaryRecordsManager.class);

	/**
	 * Method savePackageDrugInfosToDB.
	 * 
	 * @param s
	 *            Session
	 * @param pdList
	 *            List<PackageDrugInfo>
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean savePackageDrugInfosToDB(Session s,
			List<PackageDrugInfo> pdList) throws HibernateException {
		log.info("Saving package drug infos.");
		for (PackageDrugInfo pdi : pdList) {
			s.save(pdi);
		}

		return true;

	}

	/**
	 * Method saveAdherenceRecordsToDB.
	 * 
	 * @param s
	 *            Session
	 * @param adList
	 *            List<AdherenceRecord>
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean saveAdherenceRecordsToDB(Session s,
			List<AdherenceRecord> adList) throws HibernateException {
		log.info("Saving AdheranceRecords");
		for (int i = 0; i < adList.size(); i++) {
			s.save(adList.get(i));
		}

		return true;

	}

	/**
	 * Method saveDeletedItemsToDB.
	 * 
	 * @param s
	 *            Session
	 * @param dList
	 *            List<DeletedItem>
	 * @throws HibernateException
	 */
	public static void saveDeletedItemsToDB(Session s, List<DeletedItem> dList)
	throws HibernateException {
		log.info("Saving DeletedItems.");
		for (int i = 0; i < dList.size(); i++) {
			s.save(dList.get(i));
		}

	}

	/**
	 * Method hasUnsubmittedRecords.
	 * 
	 * @param s
	 *            Session
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean hasUnsubmittedRecords(Session s)
	throws HibernateException {

		long numPdis = (Long) s.createQuery(
		"select count (pd.id) from PackageDrugInfo as pd where pd.sentToEkapa = false")
		.uniqueResult();

		long numAdh = (Long) s.createQuery(
		"select count (ad.id) from AdherenceRecord as ad")
		.uniqueResult();

		long numDel = (Long) s.createQuery(
		"select count (del.id) from DeletedItem as del").uniqueResult();

		return (numPdis > 0) || (numAdh > 0) || (numDel > 0);
	}

	/**
	 * Method getUnsubmittedPackageDrugInfos.
	 * 
	 * @param sess
	 *            Session
	 * @return List<PackageDrugInfo>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PackageDrugInfo> getUnsubmittedPackageDrugInfos(
			Session sess) throws HibernateException {
		 List<PackageDrugInfo> pdiList = sess
		.createQuery(
		"from PackageDrugInfo as pd where pd.invalid = false " +
		"and pd.sentToEkapa = false " +
		"and pd.pickupDate is not null " +
		"order by pd.id asc")
		.setMaxResults(10).list();

		return pdiList;
	}

	/**
	 * Method getUnsubmittedAdherenceRecords.
	 * 
	 * @param sess
	 *            Session
	 * @return List<AdherenceRecord>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<AdherenceRecord> getUnsubmittedAdherenceRecords(
			Session sess) throws HibernateException {
		List<AdherenceRecord> adhList = sess
		.createQuery(
		"from AdherenceRecord as ad where order by ad.id asc")
		.setMaxResults(10).list();

		return adhList;
	}

	/**
	 * Method getUnsubmittedDeletedItems.
	 * 
	 * @param sess
	 *            Session
	 * @return List<DeletedItem>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<DeletedItem> getUnsubmittedDeletedItems(Session sess)
	throws HibernateException {
		List<DeletedItem> delList = sess
		.createQuery(
		"from DeletedItem as del where order by del.id asc")
		.setMaxResults(10).list();

		return delList;
	}

	/**
	 * Method deleteSubmittedPackageDrugInfos.
	 * 
	 * @param s
	 *            Session
	 * @param pdList
	 *            List<PackageDrugInfo>
	 * @throws HibernateException
	 */
	public static void updateSubmittedPackageDrugInfos(Session s,
			List<PackageDrugInfo> pdList) throws HibernateException {
		for (PackageDrugInfo pdi : pdList) {
			pdi.setSentToEkapa(true);
			s.saveOrUpdate(pdi);
		}
	}

	/**
	 * Method deleteSubmittedAdherenceRecords.
	 * 
	 * @param s
	 *            Session
	 * @param adList
	 *            List<AdherenceRecord>
	 * @throws HibernateException
	 */
	public static void deleteSubmittedAdherenceRecords(Session s,
			List<AdherenceRecord> adList) throws HibernateException {

		String adDelete = "delete AdherenceRecord where id = :adId";

		for (int i = 0; i < adList.size(); i++) {

			s.createQuery(adDelete).setInteger("adId", adList.get(i).getId())
			.executeUpdate();
		}

	}

	/**
	 * Method deleteSubmittedDeletedItems.
	 * 
	 * @param s
	 *            Session
	 * @param delList
	 *            List<DeletedItem>
	 * @throws HibernateException
	 */
	public static void deleteSubmittedDeletedItems(Session s,
			List<DeletedItem> delList) throws HibernateException {

		String delDelete = "delete DeletedItem where id = :delId";

		for (int i = 0; i < delList.size(); i++) {

			s.createQuery(delDelete)
			.setInteger("delId", delList.get(i).getId())
			.executeUpdate();
		}

	}

	/**
	 * Method getPDIforPackagedDrug.
	 * 
	 * @param s
	 *            Session
	 * @param pd
	 *            PackagedDrugs
	 * @return PackageDrugInfo
	 * @throws HibernateException
	 */
	public static PackageDrugInfo getPDIforPackagedDrug(Session s,
			PackagedDrugs pd) throws HibernateException {
		PackageDrugInfo pdi = null;

		pdi = (PackageDrugInfo) s.createQuery(
		"from PackageDrugInfo as pdi where pdi.packagedDrug.id =:pdId")
		.setInteger("pdId", pd.getId()).setMaxResults(1).uniqueResult();

		return pdi;

	}

	/**
	 * Method getPDIsForPackage.
	 * 
	 * @param s
	 *            Session
	 * @param p
	 *            Packages
	 * @return List<PackageDrugInfo>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PackageDrugInfo> getPDIsForPackage(Session s, Packages p)
	throws HibernateException {
		List<PackageDrugInfo> pdiList = s
		.createQuery(
		"from PackageDrugInfo as pd where pd.packagedDrug.parentPackage.id =:packId")
		.setInteger("packId", p.getId()).list();

		return pdiList;
	}

	/**
	 * Method getAdherenceRecordsForPillCount.
	 * 
	 * @param s
	 *            Session
	 * @param pc
	 *            PillCount
	 * @return AdherenceRecord
	 * @throws HibernateException
	 */
	public static AdherenceRecord getAdherenceRecordsForPillCount(Session s,
			PillCount pc) throws HibernateException {
		AdherenceRecord adh = null;

		adh = (AdherenceRecord) s.createQuery(
		"from AdherenceRecord as ad where ad.pillCountId =:pcId")
		.setInteger("pcId", pc.getId()).setMaxResults(1).uniqueResult();

		return adh;
	}

	public static void deletePackageDrugInfosForPackage(Session session, Packages pack) {
		session.createQuery("delete PackageDrugInfo where packageId = :id")
			.setString("id", pack.getPackageId())
			.executeUpdate();
	}
}
