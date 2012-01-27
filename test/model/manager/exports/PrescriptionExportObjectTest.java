package model.manager.exports;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.exports.columns.SimpleColumnsEnum;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.test.IDARTtest;
import org.testng.annotations.Test;

public class PrescriptionExportObjectTest extends IDARTtest {

	/**
	 * Not a real test, just a convenient way of looking at the output
	 */
	@Test(groups = "manualTests")
	public void testDataExportReportObject() {
		PrescriptionExportObject ro = new PrescriptionExportObject();
		ro.setAllPatients(true);
		ro.addSimpleColumn(SimpleColumnsEnum.patientId);
		ro.addColumns(ScriptColumn.ALL);
		System.out.println(ro.generateTemplate());
	}

	/**
	 * Not a real test, just a convenient way of looking at the output
	 */
	@Test(groups = "manualTests")
	public void testDataExportReportObject1() {
		PrescriptionExportObject ro = new PrescriptionExportObject();
		ro.setAllPatients(true);
		ro.addSimpleColumn(SimpleColumnsEnum.patientId);
		ro.addSimpleColumn(SimpleColumnsEnum.firstNames);
		ro.addSimpleColumn(SimpleColumnsEnum.lastName);
		ro.addColumns(ScriptColumn.ALL);
		DataExporter exporter = new DataExporter(HibernateUtil.getNewSession());
		try {
			exporter.generateExport(ro, "prescriptionexport.csv");
		} catch (ReportException e) {
			e.printStackTrace();
		}
	}
}
