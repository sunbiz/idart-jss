package org.celllife.idart.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlWriter;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.testng.Assert;

public class TestUtilities {

	private static Logger log = Logger.getLogger(TestUtilities.class);
	private Session sess;

	public TestUtilities() {
	}

	@SuppressWarnings("unchecked")
	public Patient createPatient(String patientID) {
		Patient pat1 = new Patient();
		pat1.setAccountStatus(new Boolean(true));
		pat1.setAddress1("12 Gabriel Road");
		pat1.setAddress2("7800");
		pat1.setAddress3("Cape Town");
		pat1.setCellphone("0824567898");
		pat1.setClinic(AdministrationManager.getClinic(sess,
				AdministrationManager.getDefaultClinicName(sess)));
		pat1.setDateOfBirth(new Date());
		pat1.setFirstNames("John");
		pat1.setLastname("Smith");
		pat1.setModified('Y');
		pat1.setPatientId(patientID);

		pat1.setPrescriptions(new HashSet());
		pat1.setSex('M');
		sess.save(pat1);
		return pat1;
	}

	public Doctor createDoctor() {
		Doctor doctor = new Doctor();
		doctor.setActive(true);
		doctor.setEmailAddress("test@JUnit.com");
		doctor.setFirstname("Test");
		doctor.setLastname("Doctor");
		doctor.setMobileno("0823456789");
		doctor.setModified('Y');
		doctor.setTelephoneno("0211234567");
		sess.save(doctor);
		return doctor;
	}

	public Prescription createPrescription(Patient patient, List<Drug> drugs,
			String prescriptionID) {
		Prescription prescription = new Prescription();
		prescription.setClinicalStage(1);
		prescription.setCurrent('T');
		prescription.setDate(new Date());
		prescription.setDoctor(createDoctor());
		prescription.setDuration(4);
		prescription.setEndDate((new Date()));
		prescription.setModified('T');
		prescription.setPatient(patient);
		prescription.setWeight(79.0);
		addDrugsToPrescription(prescription, drugs);
		prescription.setPrescriptionId(prescriptionID);
		prescription.setReasonForUpdate("Initial Prescription");
		sess.save(prescription);
		return prescription;
	}

	private List<PrescribedDrugs> addDrugsToPrescription(
			Prescription prescription, List<Drug> drugs) {

		List<PrescribedDrugs> result = new ArrayList<PrescribedDrugs>();
		for (Drug drug : drugs) {
			PrescribedDrugs pd = new PrescribedDrugs();
			pd.setAmtPerTime(1);
			pd.setDrug(drug);
			pd.setModified('T');
			pd.setPrescription(prescription);
			pd.setTimesPerDay(1);
			result.add(pd);
		}
		prescription.setPrescribedDrugs(result);
		return result;
	}

	public Packages createPackage(Patient patient, String packageID,
			int weeksSupply, List<Drug> drugs, int[] accumulatedDrugs) {
		Packages pack = new Packages();
		pack.setPackagedDrugs(getPackagedDrugs(pack, drugs, weeksSupply));
		pack.setPrescription(createPrescription(patient, drugs,
				"TestPrecrip123"));
		if (accumulatedDrugs != null) {
			pack.setAccumulatedDrugs(createAccumulatedDrugs(pack, drugs,
					accumulatedDrugs));
		}

		pack.setDateLeft(new Date());
		pack.setDateReceived(new Date());
		pack.setModified('Y');
		pack.setPackageId(packageID);
		pack.setPackDate(new Date());
		pack.setPickupDate(new Date());
		pack.setWeekssupply(weeksSupply);
		sess.save(pack);
		return pack;
	}

	private Set<AccumulatedDrugs> createAccumulatedDrugs(Packages withPackage,
			List<Drug> drugs, int[] accumulatedDrugs) {

		Set<AccumulatedDrugs> result = new HashSet<AccumulatedDrugs>();
		Packages previousPackage = createPackage(withPackage.getPrescription()
				.getPatient(), "test0", 4, drugs, null);

		int accum = 0;
		for (int i = 0; i < drugs.size(); i++) {
			AccumulatedDrugs ad = new AccumulatedDrugs();
			ad.setWithPackage(withPackage);
			ad.setPillCount(createPillcount(accumulatedDrugs[i], drugs.get(i),
					previousPackage));
			result.add(ad);
			accum++;
		}

		return result;
	}

	private PillCount createPillcount(int accum, Drug drug,
			Packages previousPackage) {

		PillCount pc = new PillCount();
		pc.setAccum(accum);
		pc.setDateOfCount(new Date());
		pc.setDrug(drug);
		pc.setPreviousPackage(previousPackage);
		sess.save(pc);
		return pc;
	}

	private List<PackagedDrugs> getPackagedDrugs(Packages parentPackage,
			List<Drug> drugs, int weeksSupply) {

		List<PackagedDrugs> result = new ArrayList<PackagedDrugs>();

		for (Drug drug : drugs) {
			PackagedDrugs pd = new PackagedDrugs();
			pd.setAmount(30 * weeksSupply / 4);
			pd.setModified('T');
			pd.setParentPackage(parentPackage);
			pd.setStock(StockManager.getSoonestExpiringStock(sess, drug, 30,
					(AdministrationManager.getStockCenters(sess)).get(0)));
			result.add(pd);
		}

		return result;
	}

	/**
	 * Returns a list of drugs from the database count is the number of drugs in
	 * the list
	 * 
	 * @param count
	 * @return
	 */
	public List<Drug> getDrugs(int count) {

		List<Drug> drugs = DrugManager.getAllDrugs(sess);
		List<Drug> result = new ArrayList<Drug>();

		int index = 0;
		for (Drug drug : drugs) {
			if (index == count) {
				break;
			}

			result.add(drug);
			index++;
		}

		return result;
	}

	/**
	 * Exports data from the tables specified in the <i>tables</i> array.
	 * 
	 * @param tables
	 * @param exportFilePath
	 * @throws Exception
	 */
	public void exportTableData(String[] tables, String exportFilePath,
			IDatabaseConnection conn) throws Exception {
		IDatabaseConnection connection = new DatabaseConnection(
				JDBCUtil.currentSession());
		IDataSet dataSet = connection.createDataSet(tables);
		exportDataSet(dataSet, exportFilePath, conn);
	}

	/**
	 * Exports the data in the table identified by <i>table</i> and all
	 * dependant data.
	 * 
	 * @param table
	 * @param exportFilePath
	 * @throws Exception
	 */
	public void exportTableWithDependencies(String table,
			String exportFilePath, IDatabaseConnection conn) throws Exception {
		IDatabaseConnection connection = new DatabaseConnection(
				JDBCUtil.currentSession());
		String[] depTableNames = TablesDependencyHelper.getAllDependentTables(
				connection, table);
		IDataSet depDataset = connection.createDataSet(depTableNames);
		exportDataSet(depDataset, exportFilePath, conn);
	}

	/**
	 * Exports an instance of IDataSet to a FlatXMLFile at the location
	 * specified by exportFilePath.
	 * 
	 * @param dataSet
	 * @param exportFilePath
	 *            location of the file to export the IDataSet to. File does not
	 *            have to exist.
	 * @throws Exception
	 */
	public void exportDataSet(IDataSet dataSet, String exportFilePath,
			IDatabaseConnection conn) throws Exception {
		FlatXmlWriter datasetWriter = new FlatXmlWriter(new FileOutputStream(
				exportFilePath));
		ITableFilter filter = new DatabaseSequenceFilter(conn);
		IDataSet filteredDataSet = new FilteredDataSet(filter, dataSet);
		datasetWriter.setDocType(TestConstants.dtdFileLocation);
		datasetWriter.write(filteredDataSet);
	}

	public void emptyDatabase(IDatabaseConnection conn) throws DataSetException, SQLException,
			DatabaseUnitException {
		ITableFilter filter = new DatabaseSequenceFilter(conn);
		IDataSet dataset = new FilteredDataSet(filter, conn.createDataSet());
		dataset = new FilteredDataSet(new IgnoreTableFilterExtension(
				new String[] { "nationalclinics" }), dataset);
		DatabaseOperation.DELETE_ALL.execute(conn, dataset);
	}

	public void exportFullDataSet(String exportFilePath,
			IDatabaseConnection conn) throws Exception {
		ITableFilter filter = new DatabaseSequenceFilter(conn);
		IDataSet dataset = new FilteredDataSet(filter, conn.createDataSet());
		dataset = new FilteredDataSet(new IgnoreTableFilterExtension(
				new String[] { "nationalclinics" }), dataset);
		exportDataSet(dataset, exportFilePath, conn);
	}

	public void exportDTD(IDatabaseConnection conn) throws DataSetException,
			FileNotFoundException, IOException, SQLException {
		ITableFilter filter = new DatabaseSequenceFilter(conn);
		IDataSet dataset = new FilteredDataSet(filter, conn.createDataSet());
		FlatDtdDataSet.write(dataset, new FileOutputStream(
				TestConstants.dataDirectory + TestConstants.dtdFileLocation));
	}

	/**
	 * Inserts an instance of IDataSet into the database using the CLEAN_INSERT
	 * method.
	 * 
	 * @param dataSet
	 * @param conn
	 * @return true if insert successful
	 * @throws DatabaseUnitException
	 * @throws SQLException
	 */
	public boolean insertDataSet(IDataSet dataSet, IDatabaseConnection conn)
			throws DatabaseUnitException, SQLException {
		if (dataSet != null) {
			log.debug("Inserting data.");
			DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
			return true;
		} else {
			log.info("No data inserted.");
			return false;
		}
	}

	public void setSession(Session sess) {
		this.sess = sess;
	}

	@SuppressWarnings("unchecked")
	public static Object invokeMethod(Object targetObject, Class targetClass,
			String methodName, Class[] argClasses, Object[] argObjects)
			throws InvocationTargetException {

		try {
			Method method = targetClass.getDeclaredMethod(methodName,
					argClasses);
			method.setAccessible(true);
			return method.invoke(targetObject, argObjects);
		} catch (NoSuchMethodException e) {
			// Should happen only rarely, because most times the
			// specified method should exist. If it does happen, just let
			// the test fail so the programmer can fix the problem.
			Assert.fail(e.getMessage());
		} catch (SecurityException e) {
			// Should happen only rarely, because the setAccessible(true)
			// should be allowed in when running unit tests. If it does
			// happen, just let the test fail so the programmer can fix
			// the problem.
			Assert.fail(e.getMessage());
		} catch (IllegalAccessException e) {
			// Should never happen, because setting accessible flag to
			// true. If setting accessible fails, should throw a security
			// exception at that point and never get to the invoke. But
			// just in case, wrap it in a TestFailedException and let a
			// human figure it out.
			Assert.fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			// Should happen only rarely, because usually the right
			// number and types of arguments will be passed. If it does
			// happen, just let the test fail so the programmer can fix
			// the problem.
			Assert.fail(e.getMessage());
		}
		return null;
	}
}
