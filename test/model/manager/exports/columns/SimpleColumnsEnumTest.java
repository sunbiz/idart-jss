package model.manager.exports.columns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import model.manager.DrugManager;
import model.manager.PatientManager;
import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.exports.DataExporter;
import model.manager.exports.PatientExportObject;
import model.nonPersistent.EntitySet;

import org.apache.commons.io.FileUtils;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.test.HibernateTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SimpleColumnsEnumTest extends HibernateTest {

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "columnProvider")
	public void testColumn(SimpleColumnsEnum column, String expected)
	throws ReportException, IOException {
		PatientExportObject ro = new PatientExportObject();
		ro.setAllPatients(false);
		int patid = PatientManager.getPatient(getSession(), "simplecolenumtest").getId();
		ro.setPatientSet(new EntitySet(Arrays.asList(patid)));
		ro.addSimpleColumn(column);
		DataExporter exporter = new DataExporter(HibernateUtil
				.getNewSession());
		exporter.generateExport(ro, "testExport.csv");
		List<String> lines = FileUtils.readLines(new File("testExport.csv"));
		Assert.assertEquals(lines.get(1), expected);
	}

	@DataProvider(name = "columnProvider")
	public Object[][] getColumnsWithValues() {
		return new Object[][] { { SimpleColumnsEnum.accountStatus, "true" },
				{ SimpleColumnsEnum.address, "12 Gabriel Road 8001 Cape Town" },
				{ SimpleColumnsEnum.cellphone, "0784568956" },
				{ SimpleColumnsEnum.clinic, "Main Clinic" },
				{ SimpleColumnsEnum.dateOfBirth, "1980-08-20" },
				{ SimpleColumnsEnum.expectedRunoutDate, "2008-08-09" },
				{ SimpleColumnsEnum.firstNames, "First Name13" },
				{ SimpleColumnsEnum.homePhone, "556 5555" },
				{ SimpleColumnsEnum.lastCollectedDate, "2008-07-10" },
				{ SimpleColumnsEnum.lastCollectedDrugs,
				"D4T 30 (30); 3TC 150 (30); EFV 600 (30);" },
				{ SimpleColumnsEnum.lastName, "Last Name17" },
				{ SimpleColumnsEnum.patientId, "simplecolenumtest" },
				{ SimpleColumnsEnum.province, "Western Cape3" },
				{ SimpleColumnsEnum.sex, "M" }, };
	}

	@Test
	public void checkAllValuesBeingTested() {
		Object[][] columnsBeingTested = getColumnsWithValues();
		SimpleColumnsEnum[] columnsInEnum = SimpleColumnsEnum.all;
		Assert.assertEquals(columnsInEnum.length, columnsBeingTested.length);
	}
	
	public void createTestData(){
		startTransaction();
		
		Patient p = utils.createPatient("simplecolenumtest");
		p.setAddress1("12 Gabriel Road");
		p.setAddress2("8001");
		p.setAddress3("Cape Town");
		p.setCellphone("0784568956");
		p.setDateOfBirth(iDARTUtil.parse(Date.class, "20 Aug 1980"));
		p.setFirstNames("First Name13");
		p.setHomePhone("556 5555");
		p.setLastname("Last Name17");
		p.setProvince("Western Cape3");
		p.setSex('M');
		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.add(DrugManager.getDrug(getSession(), "[D4T] Stavudine 30mg"));
		drugs.add(DrugManager.getDrug(getSession(), "[3TC] Lamivudine 150mg"));
		drugs.add(DrugManager.getDrug(getSession(), "[EFV] Efavirenz 600mg"));
		Packages pack = utils.createPackage(p, "enumpackid", 4, drugs, null);
		pack.setPickupDate(iDARTUtil.parse(Date.class, "10 Jul 2008"));
		
		endTransactionAndCommit();
	}
}
