package model.manager.exports;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.exports.columns.PackageDetailsEnum;
import model.nonPersistent.ExportDrugInfo;
import model.nonPersistent.ExportPackageInfo;

import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.test.HibernateTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataExportFunctionsTest extends HibernateTest {

	private static final String PAT_ID_1 = "1001";
	private static final String PAT_ID_2 = "1002";
	public static int[] patientID = { 100001, 100002, 100003 };
	public static int[] packageID = { 321001, 321002, 321003 };
	public static String[] drugnames = { "D4T", "AZT", "EFV" };
	public static int[] quantityDispensed = { 30, 45, 60, 90 };
	public static double[] amountsPerTime = { 0.5d, 1.0d, 2.5d, 3.0d };
	public static int[] timesPerDay = { 1, 2, 3, 4, 5 };
	public static int[] accumulatedDrugs = { 1, 3, 5, 9, 11 };

	public DataExportFunctions def;

	List<Object[]> ExpectedRunoutDateCalculations;

	SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

	public static Date dateCollected = new Date();

	@Test
	public void getLastEpisodeDetailsTest() {
		DataExportFunctions fn = new DataExportFunctions();
		fn.setSession(getSession());
		fn.setAllPatients(true);
		fn.setSeparator(',');
		
		Patient patient = PatientManager.getPatient(getSession(), PAT_ID_1);
		fn.setPatientId(patient.getId());
		
		List<Object> details = fn.getOldestEpisodeDetails(Arrays
				.asList(new String[] { "startDate", "stopDate" }));
		Assert.assertTrue(details.size() == 2);
		Assert.assertEquals(details.get(0).toString(), "2007-02-10 00:00:00.0");
		Assert.assertEquals(details.get(1).toString(), "2007-03-30 00:00:00.0");
	}

	public List<Object[]> generateExportPackageDrugInfoList(int num) {
		List<Object[]> retval = new ArrayList<Object[]>();

		for (int i = 0; i < num; i++) {
			String drug = drugnames[((Double) ((drugnames.length) * Math
					.random())).intValue()];
			int quantDisp = quantityDispensed[((Double) ((quantityDispensed.length) * Math
					.random())).intValue()];
			double amntsPT = amountsPerTime[((Double) ((amountsPerTime.length) * Math
					.random())).intValue()];
			int timesPD = timesPerDay[((Double) ((timesPerDay.length) * Math
					.random())).intValue()];
			int accum = accumulatedDrugs[((Double) ((accumulatedDrugs.length) * Math
					.random())).intValue()];

			ExportDrugInfo edi = new ExportDrugInfo(drug, quantDisp, accum,
					amntsPT, timesPD);
			int ExpectedDaysRunOut = ((Double) ((quantDisp + accum) / (amntsPT * timesPD)))
			.intValue();

			Object[] temp = new Object[2];

			temp[0] = edi;
			temp[1] = ExpectedDaysRunOut;

			retval.add(temp);
		}

		return retval;
	}

	@BeforeClass
	public void setup() {
		def = new DataExportFunctions();
		ExpectedRunoutDateCalculations = generateExportPackageDrugInfoList(7);

	}

	@Test(invocationCount = 7)
	public void testExpectedRunoutDate() {
		Map<Integer, Map<Integer, ExportPackageInfo>> exportPackageInfos = new HashMap<Integer, Map<Integer,ExportPackageInfo>>();
		List<ExportDrugInfo> lstExportDrugInfo = new ArrayList<ExportDrugInfo>();

		int minDaysLeft = Integer.MAX_VALUE;

		for (Object[] mapi : ExpectedRunoutDateCalculations) {
			ExportDrugInfo edi = (ExportDrugInfo) mapi[0];
			int expectedRunoutdays = Integer.parseInt((mapi[1]).toString());
			if (expectedRunoutdays < minDaysLeft) {
				minDaysLeft = expectedRunoutdays;
			}
			lstExportDrugInfo.add(edi);
		}

		int patID = patientID[((Double) ((patientID.length) * Math.random()))
		                      .intValue()];
		int packID = packageID[((Double) ((packageID.length) * Math.random()))
		                       .intValue()];

		Date Premise_date = new Date();
		ExportPackageInfo epi = new ExportPackageInfo(Premise_date, patID, ""
				+ packID, lstExportDrugInfo);

		Calendar cal = Calendar.getInstance();
		cal.setTime(Premise_date);
		cal.add(Calendar.DAY_OF_MONTH, minDaysLeft);

		String Premise_expectedRunoutDate = sdf.format(cal.getTime());

		Map<Integer, ExportPackageInfo> LstExportPackageInfo = new HashMap<Integer, ExportPackageInfo>();
		LstExportPackageInfo.put(packID, epi);
		exportPackageInfos.put(patID, LstExportPackageInfo);
		def.setExportPackageInfos(exportPackageInfos);

		def.cacheCurrentExpectedRunoutDate();

		Map<Integer, Date> expectedRunoutDates = def.getExpectedRunoutDates();

		String Calculated_expectedRunoutDate = sdf.format(expectedRunoutDates
				.get(patID));

		assertTrue("Date is correct", (Calculated_expectedRunoutDate
				.equals(Premise_expectedRunoutDate)));
	}

	@Test()
	public void testReturnMostRecentCollectionDate() {
		def = new DataExportFunctions();
		def.setSession(getSession());
		Patient patient = PatientManager.getPatient(getSession(), PAT_ID_2);
		def.setPatientId(patient.getId());
		Object o = def
		.getMostRecentPackageDetail(PackageDetailsEnum.COLLECTION_DATE
				.name());
		// Fail test if object returned is null;
		Assert.assertNotNull(o);
		// Checking against package pick up date.
		try {
			Date pickupDate = sdf.parse("29102007");
			// Checking date
			Assert.assertEquals(o, pickupDate);
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@DataProvider(name = "drugNames")
	public Object[][] drugNamesProvider() {
		return new Object[][] {
				{
					PAT_ID_1,
					"D4T 30 (30 + 2); "
					+ "3TC 150 (30 + 2); "
					+ "EFV 600 (30);" },
					{
						PAT_ID_2,
						"D4T 30 (45 + 4); "
						+ "3TC 150 (45 + 4); "
						+ "EFV 600 (45 + 2);" } };
	}

	@Test(dataProvider = "drugNames")
	public void testReturnMostRecentCollectedDrugs(String patientId,
			String expected) {
		def = new DataExportFunctions();
		def.setSession(getSession());
		Patient patient = PatientManager.getPatient(getSession(), patientId);
		def.setPatientId(patient.getId());
		Object o = def.getMostRecentPackageDetail(PackageDetailsEnum.COLLECTED_DRUGS
				.name());
		// Fail test if object returned is null;
		Assert.assertNotNull(o);
		Assert.assertEquals(o.toString(), expected);
	}
	
	public void createTestData() {
		startTransaction();
		
		Patient p = utils.createPatient(PAT_ID_1);
		Episode e1 = new Episode();
		e1.setStartDate(iDARTUtil.parse(Date.class, "10 Feb 2007"));
		e1.setStartReason("transfer in");
		e1.setStartNotes("e1");
		e1.setStopDate(iDARTUtil.parse(Date.class, "30 Mar 2007"));
		e1.setStopReason("Transfer out");
		e1.setClinic(p.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p, e1);
		getSession().save(e1);
		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.add(DrugManager.getDrug(getSession(), "[D4T] Stavudine 30mg"));
		drugs.add(DrugManager.getDrug(getSession(), "[3TC] Lamivudine 150mg"));
		drugs.add(DrugManager.getDrug(getSession(), "[EFV] Efavirenz 600mg"));
		
		Packages package2 = utils.createPackage(p, "exportPackageId1", 4, drugs, new int[]{2,2,0});
		package2.setPickupDate(new Date());
		getSession().save(package2);
		
		p = utils.createPatient(PAT_ID_2);
		Packages package1 = utils.createPackage(p, "exportPackageId2", 6, drugs, new int[]{4,4,2});
		package1.setPickupDate(iDARTUtil.parse(Date.class, "29 Oct 2007"));
		getSession().save(package1);
		
		AccumulatedDrugs acd = (AccumulatedDrugs) package1.getAccumulatedDrugs().toArray()[0];
		Packages previousPackage = acd.getPillCount().getPreviousPackage();
		previousPackage.setPickupDate(iDARTUtil.parse(Date.class, "01 Oct 2007"));
		getSession().save(previousPackage);
		
		getSession().flush();
		endTransactionAndCommit();
	}

}
