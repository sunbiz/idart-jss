package org.celllife.idart.test;

import java.io.File;
import java.util.Date;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.DrugLabelTest;
import model.manager.DrugManager;
import model.manager.PatientEpisodeTest;
import model.manager.PatientSetManagerTest;
import model.manager.StockManager;
import model.manager.exports.DataExportFunctionsTest;
import model.manager.exports.columns.SimpleColumnsEnumTest;

import org.apache.log4j.xml.DOMConfigurator;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.DatabaseTools;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PatientAttributeTest;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.dbunit.database.DatabaseConnection;

public class CreateTestData extends HibernateTest {

	private static String dataFileLocation = TestConstants.dataDirectory
	+ "testDataCombined.xml";
	
	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		iDartProperties.setiDartProperties();
		log.info("iDART system initialised");
		
		DatabaseTools._().dropDatabase();
		DatabaseTools._().createDatabase();

		DatabaseTools._().createDatabase(false, false);
		DatabaseTools._().runScript(
				iDartProperties.updateScriptDir + File.separator
						+ "information.pgkeys.sql");
		DatabaseTools._().update();

		
		CreateTestData exporter = new CreateTestData();
		exporter.createStockData();
		exporter.createTestClinic();

		new DrugLabelTest().createData();
		new PatientEpisodeTest().createData();
		new DataExportFunctionsTest().createTestData();
		new PatientAttributeTest().createTestData();
		new PatientSetManagerTest().createTestData();
		new SimpleColumnsEnumTest().createTestData();

		TestUtilities utils = new TestUtilities();
		DatabaseConnection conn = new DatabaseConnection(JDBCUtil.currentSession());
		utils.exportFullDataSet(dataFileLocation, 	conn);
		utils.exportDTD(conn);
	}

	private void createTestClinic() {
		startTransaction();
		Clinic clinic = new Clinic();
		clinic.setClinicName("Test Clinic");
		getSession().save(clinic);
		endTransactionAndCommit();
	}

	private void createStockData() throws Exception {
		startTransaction();

		List<StockCenter> scs = AdministrationManager
				.getStockCenters(getSession());
		List<Drug> drugs = DrugManager.getAllDrugs(getSession());
		for (StockCenter stockCenter : scs) {
			int i = 1;
			for (Drug drug : drugs) {
				Stock s = new Stock();
				s.setBatchNumber(String.valueOf(i++));
				s.setDateReceived(new Date());
				s.setDrug(drug);
				s.setExpiryDate(new Date());
				s.setHasUnitsRemaining('T');
				s.setManufacturer("manufacturer");
				s.setModified('T');
				s.setStockCenter(stockCenter);
				s.setShelfNumber("shelfNumber");
				s.setUnitsReceived(100);
				getSession().save(s);
			}
		}
		getSession().flush();

		StockManager.updateStockLevels(getSession());

		endTransactionAndCommit();
	}
}
