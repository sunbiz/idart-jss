package model.manager.exports;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.EpisodeDetailsEnum;
import model.manager.exports.columns.SimpleColumnsEnum;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.test.IDARTtest;
import org.testng.annotations.Test;

public class PatientExportObjectTest extends IDARTtest{

	/**
	 * Not a real test, just a convenient way of looking at the output
	 */
	@Test(groups = "manualTests")
	public void testEpisodeColumn() {
		EpisodeColumnsGroup ep = getEpisodeColumn();
		System.out.println(ep.getTemplateColumnName());
		System.out.println(ep.toTemplateString());
		System.out.println(ep.toString());
	}

	/**
	 * Not a real test, just a convenient way of looking at the output
	 */
	@Test(groups = "manualTests")
	public void testDataExportReportObject() {
		PatientExportObject ro = new PatientExportObject();
		ro.setAllPatients(true);
		ro.addSimpleColumn(SimpleColumnsEnum.accountStatus);
		ro.addSimpleColumn(SimpleColumnsEnum.address);
		ro.addSimpleColumn(SimpleColumnsEnum.dateOfBirth);
		ro.addColumn(getEpisodeColumn());
		ro.addColumn(getEpisodeColumn());
		System.out.println(ro.toString());
		System.out.println(ro.getColumnsAsStringList());
		System.out.println(ro.generateTemplate());
	}

	private EpisodeColumnsGroup getEpisodeColumn() {
		EpisodeColumnsGroup ep = new EpisodeColumnsGroup();
		ep.setModifier(ColumnModifier.MODIFIER_NEWEST_NUM);
		ep.setModifierNum(3);
		ep.setColumns(new EpisodeDetailsEnum[] { EpisodeDetailsEnum.startDate,
				EpisodeDetailsEnum.startReason, EpisodeDetailsEnum.stopDate });
		return ep;
	}

	/**
	 * Not a real test, just a convenient way of looking at the output
	 */
	@Test(groups = "manualTests")
	public void testPatientExportReportObject() {
		PatientExportObject ro = new PatientExportObject();
		ro.setAllPatients(true);

		ro.addSimpleColumn(SimpleColumnsEnum.patientId);
		ro.addSimpleColumn(SimpleColumnsEnum.firstNames);
		ro.addSimpleColumn(SimpleColumnsEnum.lastName);
		ro.addSimpleColumn(SimpleColumnsEnum.accountStatus);
		ro.addSimpleColumn(SimpleColumnsEnum.dateOfBirth);
		ro.addSimpleColumn(SimpleColumnsEnum.sex);
		ro.addSimpleColumn(SimpleColumnsEnum.address);
		ro.addSimpleColumn(SimpleColumnsEnum.clinic);
		ro.addSimpleColumn(SimpleColumnsEnum.homePhone);
		ro.addSimpleColumn(SimpleColumnsEnum.cellphone);
		ro.addSimpleColumn(SimpleColumnsEnum.province);

		DataExporter exporter = new DataExporter(HibernateUtil.getNewSession());
		try {
			exporter.generateExport(ro, "patientexport.csv");
		} catch (ReportException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new PatientExportObjectTest().testDataExportReportObject();
	}
}
