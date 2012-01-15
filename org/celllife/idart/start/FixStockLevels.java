package org.celllife.idart.start;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.StockAdjustment;
import org.celllife.idart.database.hibernate.StockTake;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FixStockLevels {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		Logger log = Logger.getRootLogger();
		DOMConfigurator.configure("log4j.xml");

		// load encrypted system properties
		log.info("Loading Encrypted System Properties");
		iDartProperties.setiDartProperties();
		
		Session session = HibernateUtil.getNewSession();
		Transaction tx = session.beginTransaction();
		/*
		 * This Query gets all stock batches which have negative balances remaining
		 */
		List<Object[]> obj = session.createSQLQuery("select * " +
				"from (select COALESCE(a.received - COALESCE(b.issued, 0) - COALESCE(c.adjusted, 0), 0) as bal, a.id " +
				"from (select (sum(s.unitsreceived)*d.packsize) as received, s.id from  stock as s, drug as d " +
				"where s.drug = d.id group by s.id, d.packsize order by s.id ) as a " +
				"left outer join (select sum(pd.amount) as issued, s.id from stock as s, packageddrugs as pd, package as p " +
				"where pd.stock = s.id and pd.parentpackage = p.id and p.stockReturned = false " +
				"group by s.id order by s.id ) as b on a.id = b.id  " +
				"left outer join (select sum(sa.adjustedValue) as adjusted, s.id from stock as s, " +
				"stockAdjustment as sa where sa.stock = s.id group by s.id order by s.id ) as c " +
				"on a.id = c.id ) as d where d.bal < 0 group by id, bal order by id").list();
		
		if(obj != null && obj.size() > 0) {
			StockTake stockTake = new StockTake();
			stockTake.setOpen(true);
			stockTake.setStartDate(new Date());
			stockTake.setStockTakeNumber("Update Stock Levels " + new Date());
			session.save(stockTake);
			session.flush();
			
			for (Object[] objects : obj) {
				StockAdjustment st = new StockAdjustment();
				st.setAdjustedValue(((BigInteger)objects[0]).intValue());
				st.setCaptureDate(new Date());
				st.setNotes("Updated Negative Stock Levels");
				st.setStock(StockManager.getStock(session, ((Integer)objects[1]).intValue()));
				st.setStockCount(0);
				st.setStockTake(stockTake);
				session.save(st);
			}
			
			// Close stock Take
			stockTake.setEndDate(new Date());
			stockTake.setOpen(false);
			session.saveOrUpdate(stockTake);
			session.flush();
			
		}
		tx.commit();
		tx = null;
		session.close();
		log.error("We have lift off");
	}

}
