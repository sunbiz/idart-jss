package model.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import model.manager.exports.columns.EpisodeDetailsEnum;
import model.nonPersistent.ExportDrugInfo;
import model.nonPersistent.ExportPackageInfo;

import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttributeTest;
import org.celllife.idart.database.hibernate.Pregnancy;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.test.HibernateTest;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PatientSetManagerTest extends HibernateTest {

	@DataProvider(name = "patientFields")
	public Object[][] createFields() {
		return new Object[][] {
				{ "Patient", "firstNames",
					makeMap("1009", "p4fn", "1010", "p5fn") },
					{ "Patient", "lastname",
						makeMap("1009", "p4ln", "1010", "p5ln") },
						{ "Patient", "cellphone",
							makeMap("1009", "123456", "1010", "456123") },
							{ "Patient", "patientId", 
								makeMap("1009", "1009", "1010", "1010") },
							{ "Patient", "clinic.clinicName",
								makeMap("1009", "Main Clinic", "1010", "Main Clinic") }, };
	}

	/**
	 * Creates a Map<Integer, String> from a list of arguments as follows: 1,
	 * "string1", 2, "String 2" etc.
	 *
	 * @param args
	 * @return
	 */
	private Map<Object, Object> makeMap(Object... args) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (int i = 0; i < args.length; i++) {
			map.put(args[i], args[i + 1]);
			i++;
		}
		return map;
	}

	@Test(dataProvider = "patientFields")
	public void getPatientFieldsTest(String className, String fieldName,
			Map<String, String> results) {
		Map<Integer, Object> patientIdAttrMap = PatientSetManager.getPatientFields(getSession(), null,
				className, fieldName, false);
		for (String patid : results.keySet()) {
			int id = PatientManager.getPatient(getSession(), patid).getId();
			Assert.assertTrue(patientIdAttrMap.containsKey(id));
			Assert.assertEquals(patientIdAttrMap.get(id), results.get(patid));
		}
	}

	@Test
	public void getEpisodeDetailsTest() {
		List<String> details = new Vector<String>();
		details.add(EpisodeDetailsEnum.startDate.template());
		details.add(EpisodeDetailsEnum.startReason.template());

		Map<Integer, List<List<Object>>> episodes = PatientSetManager
		.getEpisodeDetails(getSession(), null, details, null, null);
		
		int id = PatientManager.getPatient(getSession(), "setManagerTest").getId();
		int id2 = PatientManager.getPatient(getSession(), "setManagerTest2").getId();
		
		Assert.assertTrue(episodes.containsKey(id));
		Assert.assertTrue(episodes.containsKey(id2));

		List<Object> p1e1 = episodes.get(id).get(0);
		Assert.assertEquals(p1e1.get(0).toString(), "2008-09-01 00:00:00.0");
		Assert.assertEquals(p1e1.get(1).toString(), Episode.REASON_NEW_PATIENT);

		List<Object> p2e0 = episodes.get(id2).get(0);
		Assert.assertEquals(p2e0.get(0).toString(), "2007-09-01 00:00:00.0");
		Assert.assertEquals(p2e0.get(1).toString(), "Re-referred In");

		List<Object> p2e1 = episodes.get(id2).get(1);
		Assert.assertEquals(p2e1.get(0).toString(), "2007-03-01 00:00:00.0");
		Assert.assertEquals(p2e1.get(1).toString(), "Re-Transferred In");

		List<Object> p2e2 = episodes.get(id2).get(2);
		Assert.assertEquals(p2e2.get(0).toString(), "2007-02-01 00:00:00.0");
		Assert.assertEquals(p2e2.get(1).toString(), "Transferred In");

	}

	@Test
	public void testGetPregnancyDetails(){
		List<Integer> pregnantAtDate = PatientSetManager.getPregnantAtDate(getSession(), null, iDARTUtil.parse(Date.class, "15 Jun 2007"));
		Assert.assertEquals(pregnantAtDate.size(), 1);
		int id = PatientManager.getPatient(getSession(), "setManagerTest").getId();
		Assert.assertEquals(pregnantAtDate.get(0), Integer.valueOf(id));
	}

	@Test
	public static void getExportPackageInfoTest() {
		// Getting test patients
		Map<Integer, Map<Integer, ExportPackageInfo>> mapExportPackgInfo = null;
		mapExportPackgInfo = PatientSetManager.getPackageDetail(getSession(), null, 1, null, null);
		// Fail test if mapExportPackgInf is null
		Assert.assertNotNull(mapExportPackgInfo);

		List list = getSession().createCriteria(Packages.class, "package").createAlias(
				"package.prescription", "prescription").createAlias(
						"prescription.patient", "patient").add(
								Restrictions.isNotNull("package.prescription")).setProjection(
										Projections.countDistinct("patient.id")).list();
		int count = (Integer) list.get(0);
		Assert.assertEquals(mapExportPackgInfo.size(), count);

		// Testing inserted values for patient 0001
		// First Episode ---
		// First Drug --- [D4T] Stavudine 30mg, qty=60, timesPerDay=2,
		// amntperTime=1, accumulated=0
		// Second Drug --- [3TC] Lamivudine 150mg, qty=60, timesPerDay=2,
		// amntperTime=1, accumulated=0
		// Third Drug --- [EFV] Efavirenz 600mg, qty=30, timesPerDay=1,
		// amntperTime=1, accumulated=0
		// Date collected: 10-04-2007
		// patient.id = 1002
		//

		// Getting patient with row id 1002, for patient 0001
		int pid = PatientManager.getPatient(getSession(), "setManagerTest3").getId();
		Map<Integer, ExportPackageInfo> epiLst = mapExportPackgInfo.get(pid);
		int packid = PackageManager.getPackage(getSession(), "071001A-001-2").getId();
		ExportPackageInfo epi = epiLst.get(packid);
		Assert.assertEquals(epi.getDateCollected(),
				iDARTUtil.parse(Date.class, "29 Oct 2007"));

		Assert.assertEquals(epi.getPackageID(), "071001A-001-2");
		Assert.assertEquals(epi.getPatientID().intValue(),pid);

		for (ExportDrugInfo edi : epi.getListOfExportDrugInfo()) {
			String compound = edi.getChemicalCompoundName();
			if (compound.contains("[D4T]")) {
				Assert.assertEquals(compound, DrugManager
						.getShortGenericDrugName(DrugManager.getDrug(getSession(),
						"[D4T] Stavudine 30mg"), true));
				Assert.assertEquals(edi.getAmountPerTime(), 1.0);
				Assert.assertEquals(edi.getAccumulatedDrugs(), 4);
				Assert.assertEquals(edi.getQuantityDispensed(), 30);
				Assert.assertEquals(edi.getTimesPerDay(), 2);

			} else if (compound.contains("[3TC]")) {
				Assert.assertEquals(compound, DrugManager
						.getShortGenericDrugName(DrugManager.getDrug(getSession(),
						"[3TC] Lamivudine 150mg"), true));
				Assert.assertEquals(edi.getAmountPerTime(), 1.0);
				Assert.assertEquals(edi.getAccumulatedDrugs(), 4);
				Assert.assertEquals(edi.getQuantityDispensed(), 30);
				Assert.assertEquals(edi.getTimesPerDay(), 2);

			} else if (compound.contains("[EFV]")) {
				Assert.assertEquals(compound, DrugManager
						.getShortGenericDrugName(DrugManager.getDrug(getSession(),
						"[EFV] Efavirenz 600mg"), true));
				Assert.assertEquals(edi.getAmountPerTime(), 1.0);
				Assert.assertEquals(edi.getAccumulatedDrugs(), 2);
				Assert.assertEquals(edi.getQuantityDispensed(), 30);
				Assert.assertEquals(edi.getTimesPerDay(), 1);

			}
		}

	}
	
	public void createTestData(){
		startTransaction();
		
		Patient p1 = utils.createPatient("setManagerTest");
		p1.getPregnancies().add(new Pregnancy(p1, 
				iDARTUtil.parse(Date.class, "14 Jun 2007"), 
				iDARTUtil.parse(Date.class, "16 Jun 2007"), 'T'));
		getSession().save(p1);
		
		Episode episode = new Episode();
		episode.setStartDate(iDARTUtil.parse(Date.class, "01 Sep 2008"));
		episode.setStartReason(Episode.REASON_NEW_PATIENT);
		episode.setClinic(p1.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p1, episode);
		getSession().save(episode);
		
		Patient p2 = utils.createPatient("setManagerTest2");
		episode = new Episode();
		episode.setStartDate(iDARTUtil.parse(Date.class, "01 Sep 2007"));
		episode.setStartReason("Re-referred In");
		episode.setClinic(p2.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p2, episode);
		getSession().save(episode);
		
		episode = new Episode();
		episode.setStartDate(iDARTUtil.parse(Date.class, "01 Mar 2007"));
		episode.setStartReason("Re-Transferred In");
		episode.setClinic(p2.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p2, episode);
		getSession().save(episode);
		
		episode = new Episode();
		episode.setStartDate(iDARTUtil.parse(Date.class, "01 Feb 2007"));
		episode.setStartReason("Transferred In");
		episode.setClinic(p2.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p2, episode);
		getSession().save(episode);
		
		Patient p3 = utils.createPatient("setManagerTest3");
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.add(DrugManager.getDrug(getSession(), "[D4T] Stavudine 30mg"));
		drugs.add(DrugManager.getDrug(getSession(), "[3TC] Lamivudine 150mg"));
		drugs.add(DrugManager.getDrug(getSession(), "[EFV] Efavirenz 600mg"));
		Packages package1 = utils.createPackage(p3, "071001A-001-2", 4, drugs, new int[]{4,4,2});
		package1.setPickupDate(iDARTUtil.parse(Date.class, "29 Oct 2007"));
		getSession().save(package1);
		
		AccumulatedDrugs acd = (AccumulatedDrugs) package1.getAccumulatedDrugs().toArray()[0];
		Packages previousPackage = acd.getPillCount().getPreviousPackage();
		previousPackage.setPickupDate(iDARTUtil.parse(Date.class, "01 Oct 2007"));
		getSession().save(previousPackage);
		
		Patient p4 = utils.createPatient("1009");
		p4.setFirstNames("p4fn");
		p4.setLastname("p4ln");
		p4.setCellphone("123456");
		getSession().save(p4);
		
		Patient p5 = utils.createPatient("1010");
		p5.setFirstNames("p5fn");
		p5.setLastname("p5ln");
		p5.setCellphone("456123");
		getSession().save(p5);
		
		getSession().flush();
		
		endTransactionAndCommit();
	}
}
