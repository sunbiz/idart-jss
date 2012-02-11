package org.celllife.idart.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.celllife.idart.database.ConnectException;
import org.celllife.idart.database.DatabaseEmptyException;
import org.celllife.idart.database.DatabaseException;
import org.celllife.idart.database.DatabaseTools;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class HibernateTest extends IDARTtest {

	protected static final String informationSchema = "metadata/database/information.pgkeys.sql";
	private static Session sess;
	private static Transaction tx;
	private IDatabaseConnection conn;
	public TestUtilities utils;

	public HibernateTest() {
		super();
		utils = new TestUtilities();
	}

	@Override
	@BeforeSuite
	public void initialiseIDARTSystem() throws Exception {
		super.initialiseIDARTSystem();
		try {
			log.info("Checking database");
			DatabaseTools._().checkDatabase();
		} catch (DatabaseEmptyException e) {
			log.info("Creating database schema");
			DatabaseTools._().createDatabase(false, true);
		} catch (ConnectException e) {
			throw new DatabaseException(e);
		}

		updateDatabase();
		safeEmptyDatabase();
	}

	public void updateDatabase() throws DatabaseException {
		log.info("Updating database");
		DatabaseTools._().update();
	}

	public void safeEmptyDatabase() throws SQLException, DatabaseUnitException,
			IOException {
		log.info("Removing all data from database.");
		getConnection();

		try {
			utils.emptyDatabase(conn);
		} catch (SQLException se) {
			if (se.getMessage().contains("information_schema")) {
				log.warn("Database missing _pg_keypositions, attemting to create.");
				runSQLScript(informationSchema, false);
				utils.emptyDatabase(conn);
			}
		}

		try {
			insertTestData();
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Database setup failed.";
		}
	}

	protected void runSQLScript(String scriptPath, boolean selectOnly)
			throws IOException {
		File script = new File(scriptPath);
		log.debug(script.getCanonicalPath());
		if (script.exists()) {
			JDBCUtil.executeSQL(FileUtils.readFileToString(script), selectOnly);
		}
	}

	@BeforeMethod
	public void startTransaction() {
		setSession(null);
		tx = null;
		try {
			setSession(HibernateUtil.getNewSession());
			utils.setSession(getSession());
			tx = getSession().beginTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			assert false : "Database setup failed.";
		}
	}

	protected void insertTestData() throws Exception {
		IDataSet testDs = new FlatXmlDataSet(new File(
				TestConstants.dataDirectory + "testDataCombined.xml"));
		utils.insertDataSet(testDs, getConnection());
	}

	@AfterMethod
	public void endTransactionAndRollBack() {
		tx.rollback();
		getSession().close();
	}

	public void endTransactionAndCommit() {
		tx.commit();
		getSession().close();
	}

	public IDatabaseConnection getConnection() throws SQLException {
		if (conn == null) {
			conn = new DatabaseConnection(JDBCUtil.currentSession());
		}
		return conn;
	}

	private static void setSession(Session sess) {
		HibernateTest.sess = sess;
	}

	public static Session getSession() {
		return sess;
	}
}
