package model.manager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import model.nonPersistent.BatchDetail;
import model.nonPersistent.DrugDetail;
import model.nonPersistent.StockLevelInfo;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Logging;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockAdjustment;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.StockTake;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class StockManager {

	private static Logger log = Logger.getLogger(StockManager.class);

	// -------- METHODS FOR STOCK MANAGER -------------------------------

	/**
	 * 
	 * 
	 * @param session
	 *            Session
	 * @param stockId
	 * @return Stock
	 * @throws HibernateException
	 */
	public static Stock getStock(Session session, int stockId)
	throws HibernateException {
		Stock theStock = null;
		theStock = (Stock) session.createQuery(
				"select stock from Stock as stock where stock.id = '" + stockId
				+ "'").setMaxResults(1).uniqueResult();
		return theStock;
	}

	public static void deleteInvalidStockTakes(Session session)
	throws HibernateException {

		@SuppressWarnings("unchecked")
		List<StockTake> list = session.createQuery("from StockTake where endDate is null").list();
		for (StockTake stockTake : list) {
			session.delete(stockTake);
		}
	}

	/**
	 * This method should no longer be used. It does not make sense to assume
	 * that every entry in the stock table will have a unique combination of
	 * batchnumber and time. This method caused problems in the stockTakeGui.
	 * 
	 * @param session
	 *            Session
	 * @param batchNumber
	 *            String
	 * @param dateReceived
	 *            Date
	 * @return Stock
	 * @throws HibernateException
	 */
	@Deprecated
	public static Stock getBatch(Session session, String batchNumber,
			Date dateReceived) throws HibernateException {
		Stock theStock = null;
		theStock = (Stock) session.createQuery(
				"select stock from Stock as stock where stock.batchNumber =:batchNumber "
				+ "and stock.dateReceived =:dateReceived").setString(
						"batchNumber", batchNumber).setDate("dateReceived",
								dateReceived).setMaxResults(1).uniqueResult();
		return theStock;
	}

	/**
	 * Method getSoonestExpiringStock.
	 * 
	 * @param session
	 *            Session
	 * @param d
	 *            Drug
	 * @param units
	 *            int
	 * @param c
	 *            Clinic
	 * @return Stock
	 * @throws HibernateException
	 */
	public static Stock getSoonestExpiringStock(Session session, Drug d,
			int units, StockCenter stockCenter) throws HibernateException {

		List<Stock> theStockList = getCurrentStockForDrug(session, d,
				stockCenter);
		Stock theStock = null;
		Iterator<Stock> it = theStockList.iterator();

		boolean enoughStock = false;

		while (it.hasNext()) {
			theStock = it.next();

			StockLevel stockLevel = StockManager.getCurrentStockLevel(session,
					theStock);

			int numContainers = 0;
			int numUnits = 0;

			if (stockLevel != null) {
				numContainers = stockLevel.getFullContainersRemaining();
				numUnits = stockLevel.getLoosePillsRemaining();
			}
			int totalUnits = (numContainers * theStock.getDrug().getPackSize())
			+ numUnits;
			if (totalUnits >= units) {

				enoughStock = true;
				break;

			}

		}

		if (enoughStock)
			return theStock;
		else
			return null;
	}

	/**
	 * Method getCurrentStockForDrug.
	 * 
	 * @param session
	 *            Session
	 * @param drug
	 *            Drug
	 * @param clinic
	 *            Clinic
	 * @return List<Stock>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Stock> getCurrentStockForDrug(Session session,
			Drug drug, StockCenter stockCenter) throws HibernateException {
		List<Stock> result = null;

		result = session
		.createQuery(
				"select stock from Stock as stock "
				+ "where stock.stockCenter.id =:stockCenterId "
				+ "and stock.drug.id =:drugId "
				+ "and stock.hasUnitsRemaining='T' "
				+ "order by stock.expiryDate ASC, stock.dateReceived ASC, stock.id ASC")
				.setInteger("stockCenterId", stockCenter.getId()).setInteger(
						"drugId", drug.getId()).list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Stock> getAllStockForDrug(Session session, Drug drug) throws HibernateException {
		List<Stock> result = null;

		result = session
		.createQuery(
				"select stock from Stock as stock "
				+ "where stock.drug.id =:drugId "
				+ "order by stock.expiryDate ASC, stock.dateReceived ASC, stock.id ASC")
				.setInteger("drugId", drug.getId()).list();
		return result;
	}

	/**
	 * Get the latest unit price entered for a batch of this drug at this
	 * pharmacy
	 * 
	 * @param session
	 * @param drug
	 * @param pharm
	 * @return
	 */
	public static BigDecimal getLastUnitPriceForDrug(Session session,
			Drug drug, String stockCenterName) {

		return (BigDecimal) session
		.createQuery(
				"select stock.unitPrice from Stock as stock "
				+ "where stock.stockCenter.stockCenterName =:stockCenterId "
				+ "and stock.drug.id =:drugId "
				+ "and stock.unitPrice is not null "
				+ "order by stock.id DESC").setString(
						"stockCenterId", stockCenterName).setInteger("drugId",
								drug.getId()).setMaxResults(1).uniqueResult();

	}

	/**
	 * Method getStockForStockTake.
	 * 
	 * @param session
	 *            Session
	 * @param drugName
	 *            String
	 * @param clinicName
	 *            String
	 * @param includeZeroBatches
	 *            boolean
	 * @return List<Stock>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Stock> getStockForStockTake(Session session, Drug drug,
			StockCenter stockCenter, boolean includeZeroBatches)
			throws HibernateException {
		List<Stock> result = null;
		if (includeZeroBatches) {
			result = session.createQuery(
					"select stock from Stock as stock "
					+ "where stock.stockCenter.id =:stockCenterId "
					+ "and stock.drug.id =:drugId "
					+ "order by stock.batchNumber, "
					+ "stock.expiryDate ASC, stock.dateReceived")
					.setInteger("stockCenterId", stockCenter.getId())
					.setInteger("drugId", drug.getId()).list();
		} else {
			result = session.createQuery(
					"select stock from Stock as stock "
					+ "where stock.stockCenter.id =:stockCenterId "
					+ "and stock.drug.id =:drugId "
					+ "and stock.hasUnitsRemaining='T' "
					+ "order by stock.batchNumber, "
					+ "stock.expiryDate ASC, stock.dateReceived")
					.setInteger("stockCenterId", stockCenter.getId())
					.setInteger("drugId", drug.getId()).list();
		}
		return result;

	}

	/**
	 * Method getAllCurrentStock.
	 * 
	 * @param session
	 *            Session
	 * @return List<Stock>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Stock> getAllCurrentStock(Session session)
	throws HibernateException {
		List<Stock> result = null;

		result = session.createQuery(
				"select stock from Stock as stock "
				+ "where stock.hasUnitsRemaining='T'").list();
		return result;
	}

	/**
	 * Method getEmptyBatchesForDrug.
	 * 
	 * @param session
	 *            Session
	 * @param drugName
	 *            String
	 * @return List<Stock>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Stock> getEmptyBatchesForDrug(Session session,
			String drugName) throws HibernateException {
		List<Stock> result = null;
		result = session
		.createQuery(
				"select sl.batch from StockLevel as sl "
				+ "where sl.batch.drug.name =:drugName "
				+ "and sl.fullContainersRemaining = sl.batch.unitsReceived "
				+ "order by sl.batch.dateReceived DESC")
				.setString("drugName", drugName).list();
		return result;
	}

	/**
	 * Method getTotalStockLevelsForDrug.
	 * 
	 * @param sess
	 *            Session
	 * @param drug
	 *            Drug
	 * @param clinic
	 *            Clinic
	 * @return int[]
	 * @throws HibernateException
	 */
	public static int[] getTotalStockLevelsForDrug(Session sess, Drug drug,
			StockCenter stockCenter) throws HibernateException {
		return StockManager.getDrugTotalLevel(sess, drug, stockCenter);
	}

	/**
	 * Method reduceStock.
	 * 
	 * @param s
	 *            Session
	 * @param ti
	 *            TableItem
	 * @param packSize
	 *            int
	 * @param reason
	 *            String
	 * @param packs 
	 * @param pills 
	 * @param stockId 
	 * @param stockCenter
	 * @throws HibernateException
	 */
	public static void reduceStock(Session s, int packSize,
			String reason, int packs, int pills, int stockId) throws HibernateException {

		Packages packages = new Packages();
		packages.setModified('T');
		packages.setPackageId("destroyedStock");
		packages.setPackDate(new Date());
		packages.setWeekssupply(0);

		PackagedDrugs pDrug = new PackagedDrugs();
		pDrug.setAmount((packs * packSize) + pills);
		pDrug.setModified('T');
		pDrug.setParentPackage(packages);
		Stock theStock = StockManager.getStock(s, stockId);
		pDrug.setStock(theStock);

		List<PackagedDrugs> pdList = new ArrayList<PackagedDrugs>();
		pdList.add(pDrug);
		packages.setPackagedDrugs(pdList);

		PackageManager.savePackage(s, packages);
		
		StockManager.updateStockLevel(s, theStock);

		// log this transaction
		Logging logging = new Logging();
		logging.setIDart_User(LocalObjects.getUser(s));
		logging.setItemId(String.valueOf(pDrug.getStock().getId()));
		logging.setModified('Y');
		logging.setTransactionDate(new Date());
		logging.setTransactionType("Destroy Stock");
		logging.setMessage("Destroyed " + pDrug.getAmount() + " units of drug "
				+ pDrug.getStock().getDrug().getName() + " from batch "
				+ pDrug.getStock().getBatchNumber());
		if (!reason.equalsIgnoreCase("")) {
			logging.setMessage(logging.getMessage() + " Reason: " + reason);
		}
		s.save(logging);

	}

	/**
	 * method to get all the empty batches for a particular drug Used in Stock
	 * Take to display the batches which have no units remaining
	 * 
	 * @param session
	 *            Session
	 * @param theDrug
	 *            Drug
	 * @return List<Stock>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Stock> getEmptyBatchesList(Session session, Drug theDrug)
	throws HibernateException {
		List<Stock> result = null;
		result = session.createQuery(
				"select stock from Stock as stock " + "where stock.drug =:id "
				+ "and stock.hasUnitsRemaining='F' "
				+ "order by stock.expiryDate ASC, "
				+ "stock.dateReceived ASC").setInteger("id",
						theDrug.getId()).list();
		return result;
	}

	/**
	 * method to get all the batches for a particular drug
	 * 
	 * @param session
	 *            Session
	 * @param theDrug
	 *            Drug
	 * @return List<Stock>
	 */
	@SuppressWarnings("unchecked")
	public static List<Stock> getBatchesList(Session session, Drug theDrug) {
		List<Stock> result = null;
		result = session.createQuery(
				"select stock from Stock as stock " + "where stock.drug =:id "
				+ "order by stock.expiryDate ASC, "
				+ "stock.dateReceived ASC").setInteger("id",
						theDrug.getId()).list();
		return result;
	}

	// -------- METHODS FOR STOCK LEVEL MANAGER -------------------------------

	/**
	 * Method save.
	 * 
	 * @param s
	 *            Session
	 * @param stockLevel
	 *            StockLevel
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean save(Session s, StockLevel stockLevel)
	throws HibernateException {
		s.save(stockLevel);
		s.flush();
		return true;
	}

	/**
	 * Method save.
	 * 
	 * @param s
	 *            Session
	 * @param stockLevels
	 *            List<StockLevel>
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean save(Session s, List<StockLevel> stockLevels)
	throws HibernateException {
		for (StockLevel stockLevel : stockLevels) {
			s.save(stockLevel);
		}
		s.flush();
		return true;
	}

	/**
	 * Method calculateCurrentStockLevel.
	 * 
	 * @param session
	 *            Session
	 * @param theStock
	 *            Stock
	 * @return int[]
	 * @throws HibernateException
	 */
	public static StockLevelInfo getStockLevelInfo(Session session,
			Stock theStock) throws HibernateException {

		log.info("Calculating StockLevel for stock " + theStock.getId());

		StockLevelInfo info = new StockLevelInfo(theStock);

		int totalDrugsPackaged = ((Long) session.createQuery(
				"select coalesce(sum(p.amount),0) from PackagedDrugs p "
				+ "where p.stock.id = :stockid "
				+ "and p.parentPackage.stockReturned = false "
				+ "and p.parentPackage.prescription is not null")
				.setParameter("stockid", theStock.getId()).uniqueResult())
				.intValue();
		
		info.setDispensed(totalDrugsPackaged);

		int stockDestroyed = ((Long) session.createQuery(
				"select coalesce(sum(p.amount),0) from PackagedDrugs p "
				+ "where p.stock.id = :stockid "
				+ "and p.parentPackage.prescription is null")
				.setParameter("stockid", theStock.getId()).uniqueResult())
				.intValue();
		
		info.setDestroyed(stockDestroyed);

		int stockAdjusted = ((Long) session.createQuery(
				"select coalesce(sum(s.adjustedValue),0) from StockAdjustment s "
				+ "where s.stock.id = :id ").setInteger("id",
						theStock.getId()).uniqueResult()).intValue();
		
		info.setAdjusted(stockAdjusted);

		info.caluculateOnHand();
		return info;
	}

	/**
	 * This method looks in the stockLevel table for a stockLevel. If one is
	 * found then it is returned. If a stockLevel is not found, it calculates
	 * the stock level. If the calculated stock level is greater than 0 then a
	 * new stockLevel is created, saved and returned. If not, then null is
	 * returned.
	 * 
	 * @param s
	 * @param theStock
	 * @return
	 * @throws HibernateException
	 */
	public static StockLevel getCurrentStockLevel(Session s, Stock theStock)
	throws HibernateException {

		List<StockLevel> currentLevels = getStockLevels(s, theStock);

		if (currentLevels.isEmpty() && theStock.getHasUnitsRemaining() == 'F'){
				return null;
		} else if (currentLevels.size() == 1) {
			return currentLevels.get(0);
		} else if (currentLevels.size() > 1) {
			for (StockLevel sl : currentLevels) {
				log.info("Deleted duplicate stock levels for drug "
						+ sl.getBatch().getDrug().getName()
						+ "- will regenerate.");
				s.delete(sl);
			}
		}

		return updateStockLevel(s, theStock);
	}

	private static List<StockLevel> getStockLevels(Session s, Stock theStock) {
		@SuppressWarnings("unchecked")
		List<StockLevel> currentLevels = s.createQuery(
		"from StockLevel sl where sl.batch.id = :batchId").setInteger(
				"batchId", theStock.getId()).list();
		return currentLevels;
	}

	/**
	 * Method updateStockLevels.
	 * 
	 * @param sess
	 *            Session
	 * @throws HibernateException
	 */
	public static void updateStockLevels(Session sess)
	throws HibernateException {
		List<Stock> allBatches = StockManager.getAllCurrentStock(sess);
		
		for (Stock s : allBatches) {
			getCurrentStockLevel(sess, s);
		}
	}

	/**
	 * Method getDrugTotalLevel.
	 * 
	 * @param sess
	 *            Session
	 * @param d
	 *            Drug
	 * @param c
	 *            Clinic
	 * @return int[]
	 * @throws HibernateException
	 */
	public static int[] getDrugTotalLevel(Session sess, Drug d,
			StockCenter stockCenter) throws HibernateException {
		int[] resultSet = { 0, 0 };
		int totalPillsRemaining = 0;
		for (StockLevel s : getStockLevelsForDrug(sess, d, stockCenter)) {
			totalPillsRemaining += (s.getFullContainersRemaining() * d
					.getPackSize());
			totalPillsRemaining += s.getLoosePillsRemaining();
		}

		resultSet[0] = totalPillsRemaining / d.getPackSize();
		resultSet[1] = totalPillsRemaining % d.getPackSize();

		return resultSet;
	}

	/**
	 * Method that gets all the details for a specific batch
	 * 
	 * @param session
	 * @param stockCenter
	 * @return
	 */
	public static DrugDetail getDrugDetail(Session session, Drug drug,
			StockCenter stockCenter) {

		DrugDetail result = new DrugDetail(drug);
		result.setDrugDetails(drug.getName() + " (" + drug.getPackSize() + " "
				+ drug.getForm().getForm() + ")");

		List<Stock> stockList = getCurrentStockForDrug(session, drug, stockCenter);
		for (Stock theStock : stockList) {
			BatchDetail bd = new BatchDetail();
			bd.setDrugDetail(result);

			bd.setBatchName("Batch "
					+ theStock.getBatchNumber()
					+ " ("
					+ theStock.getManufacturer()
					+ " exp "
					+ new SimpleDateFormat("MMM yyyy").format(theStock
							.getExpiryDate())
							+ (theStock.getUnitPrice() != null ? " R"
							+ theStock
									.getUnitPrice()
									+ " per unit)" : ")"));
			bd.setDateReceived(theStock.getDateReceived());

			StockLevelInfo info = getStockLevelInfo(session, theStock);
			bd.setStockLevelInfo(info);
			result.AddBatchDetail(bd);
		}
		return result;
	}

	/**
	 * Method getStockLevelsForDrug.
	 * 
	 * @param s
	 *            Session
	 * @param d
	 *            Drug
	 * @param c
	 *            Clinic
	 * @return List<StockLevel>
	 * @throws HibernateException
	 */
	public static List<StockLevel> getStockLevelsForDrug(Session s, Drug d,
			StockCenter stockCenter) throws HibernateException {
		List<StockLevel> stockLevelList = new ArrayList<StockLevel>();
		List<Stock> stockList = StockManager.getCurrentStockForDrug(s, d,
				stockCenter);
		for (Stock stock : stockList) {
			StockLevel sl = getCurrentStockLevel(s, stock);
			if (sl != null) {
				stockLevelList.add(sl);
			}
		}
		return stockLevelList;
	}

	/**
	 * Method updateStockLevel.
	 * 
	 * @param sess
	 *            Session
	 * @param theStock
	 *            Stock
	 * @return 
	 * @throws HibernateException
	 */
	public static StockLevel updateStockLevel(Session sess, Stock theStock)
	throws HibernateException {
		sess.flush();
		
		StockLevel sl = null;
		List<StockLevel> levels = getStockLevels(sess, theStock);
		if (levels.size() > 1){
			for (StockLevel stockLevel : levels) {
				log.info("Deleted duplicate stock levels for drug "
						+ stockLevel.getBatch().getDrug().getName()
						+ "- will regenerate.");
				sess.delete(stockLevel);
			}
			levels.clear();
		} else if (levels.size() == 1){
			sl = levels.get(0);
		}
		
		StockLevelInfo actualLevels = getStockLevelInfo(sess, theStock);
		if (actualLevels.getOnhand() == 0) {
			if (sl != null) {
				log.info("Deleting empty batch number "
						+ theStock.getBatchNumber() + " of drug "
						+ theStock.getDrug().getName());
				sess.delete(sl);
			}
			
			if (theStock.getHasUnitsRemaining() == 'T') {
				log.info("Marking stock as empty: " + theStock.getBatchNumber() + " of drug "
						+ theStock.getDrug().getName());
				theStock.setHasUnitsRemaining('F');
				sess.saveOrUpdate(theStock);
			}
			sl = null;
		} else {
			if (sl == null){
				sl = new StockLevel();
				sl.setBatch(theStock);
			}
			sl.setFullContainersRemaining(actualLevels.getOnhandFull());
			sl.setLoosePillsRemaining(actualLevels.getOnhandLoose());
			sess.saveOrUpdate(sl);
			
			if (theStock.getHasUnitsRemaining() == 'F') {
				log.info("Marking stock as not empty: " + theStock.getBatchNumber() + " of drug "
						+ theStock.getDrug().getName());
				theStock.setHasUnitsRemaining('T');
				sess.saveOrUpdate(theStock);
			}
		}
		sess.flush();
		return sl;
	}

	/**
	 * @param session
	 *            Session
	 * @param stockAdjustmentId
	 * @return StockAdjustment
	 * @throws HibernateException
	 */
	public static StockAdjustment getStockAdjustment(Session session,
			int stockAdjustmentId) throws HibernateException {
		StockAdjustment theStockAdjustment = null;
		theStockAdjustment = (StockAdjustment) session.createQuery(
				"select stockAdjustment from StockAdjustmet as stockAdjustment "
				+ "where stockAdjustment.id = '" + stockAdjustmentId
				+ "'").setMaxResults(1).uniqueResult();
		return theStockAdjustment;
	}

	/**
	 * Deletes a Stock Take
	 * 
	 * @param session
	 *            Session
	 * @param stockTake
	 *            StockTake
	 * @throws HibernateException
	 */
	public static void deleteStockTake(Session session, StockTake stockTake)
	throws HibernateException {
		session.delete(stockTake);
	}

	public static void clearStockTakes(Session session)
	throws HibernateException {

		StockTake st = (StockTake) session.createQuery(
				"" + "from StockTake st where st.open = true").uniqueResult();

		if (st != null) {
			session.delete(st);
			session.flush();
		}
	}

	/**
	 * Deletes a Stock Adjustment
	 * 
	 * @param session
	 *            Session
	 * @param stockAdjustment
	 *            StockAdjustment
	 * @throws HibernateException
	 */
	public static void deleteStockAdjustment(Session session,
			StockAdjustment stockAdjustment) throws HibernateException {
		session.delete(stockAdjustment);
	}

	/**
	 * Saves stockAdjustment
	 * 
	 * @param session
	 *            Session
	 * @param stockAdj
	 * @throws HibernateException
	 */
	public static void saveStockAdjustment(Session session,
			StockAdjustment stockAdj) throws HibernateException {
		
		session.save(stockAdj);
		session.flush();
		
		StockManager.updateStockLevel(session, stockAdj.getStock());
	}

	/**
	 * Method getStockTake.
	 * 
	 * @param session
	 *            Session
	 * @return StockTake
	 * @throws HibernateException
	 */
	public static StockTake getStockTake(Session session)
	throws HibernateException {
		StockTake stockTake = null;
		// first check if the batch still has units remaining
		// if not, update the hasUnitsRemaining field
		stockTake = (StockTake) session.createQuery(
				"select st from StockTake st " + "where st.open = true")
				.uniqueResult();
		return stockTake;
	}

	/**
	 * Method createStockTake.
	 * 
	 * @param session
	 *            Session
	 * @param startDate
	 *            Date
	 * @return StockTake
	 * @throws HibernateException
	 */
	public static StockTake createStockTake(Session session, Date startDate)
	throws HibernateException {
		StockTake stockTake = new StockTake();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy/hh/mm");
		stockTake.setStartDate(startDate);
		stockTake.setOpen(true);
		stockTake.setStockTakeNumber(sdf.format(startDate));
		session.save(stockTake);
		return stockTake;
	}

	/**
	 * Method getAdjustment.
	 * 
	 * @param session
	 *            Session
	 * @param batchId
	 *            int
	 * @param stockTake
	 *            int
	 * @return StockAdjustment
	 * @throws HibernateException
	 */
	public static StockAdjustment getAdjustment(Session session, int batchId,
			int stockTake) throws HibernateException {
		StockAdjustment stockAdjustment;
		stockAdjustment = (StockAdjustment) session.createQuery(
				"select sa from StockAdjustment sa "
				+ "where sa.stock = :batchId and sa.stockTake = "
				+ ":stockTake").setInteger("batchId", batchId)
				.setInteger("stockTake", stockTake).setMaxResults(1)
				.uniqueResult();
		return stockAdjustment;
	}

	/**
	 * Method endStockTake.
	 * 
	 * @param session
	 *            Session
	 * @param endDate
	 *            Date
	 * @throws HibernateException
	 */
	public static void endStockTake(Session session, Date endDate)
	throws HibernateException {
		StockTake stockTake = getStockTake(session);
		// finalise current stockTake
		stockTake.setEndDate(endDate);
		stockTake.setOpen(false);
	}

	/**
	 * Method getVariance.
	 * 
	 * @param session
	 *            Session
	 * @return int
	 * @throws HibernateException
	 */
	public static int getVariance(Session session) throws HibernateException {
		StockTake stockTake = getStockTake(session);
		int variance = 0;
		variance = ((Long) session.createQuery(
				" select sum(sa.adjustedValue) "
				+ "from StockAdjustment sa where sa.stockTake = "
				+ ":stockTake").setInteger("stockTake",
						stockTake.getId()).uniqueResult()).intValue();
		return variance;
	}

	/**
	 * This method returns all the stock which have already been captured in the
	 * current stock take
	 * 
	 * @param session
	 *            Session
	 * @param st
	 *            StockTake
	 * @return the list of stock
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<StockAdjustment> getStockAdjustmentsInStockTake(
			Session session, StockTake st) throws HibernateException {
		List<StockAdjustment> returnList = new ArrayList<StockAdjustment>();
		returnList.addAll(session.createQuery(
				" select st " + "from StockAdjustment st "
				+ "where st.stockTake.id = :stockTakeId "
				+ "order by st.stock, st.captureDate asc").setInteger(
						"stockTakeId", st.getId()).list());
		return returnList;
	}
}
