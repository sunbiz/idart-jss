package model.manager.exports;

import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.EpisodeDetailsEnum;
import model.manager.exports.columns.IColumnEnum;
import model.manager.exports.columns.SimpleColumnsEnum;
import model.manager.exports.xml.ReportObjectXMLDecoder;
import model.manager.exports.xml.ReportObjectXMLEncoder;

import org.celllife.idart.test.IDARTtest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportObjectXMLTest extends IDARTtest {

	@SuppressWarnings( { "unused", "unchecked" })
	private static final Class testClass = ReportObjectXMLTest.class;

	@Test()
	public void testXMLEncoderWithPatientExportObject() {
		PatientExportObject obj = new PatientExportObject();
		obj.addSimpleColumn(SimpleColumnsEnum.patientId);
		obj.addSimpleColumn(SimpleColumnsEnum.firstNames);
		obj.addColumn(new EpisodeColumnsGroup(
				ColumnModifier.MODIFIER_NEWEST_NUM, 5, new IColumnEnum[] {
						EpisodeDetailsEnum.startDate,
						EpisodeDetailsEnum.stopDate,
						EpisodeDetailsEnum.startReason,
						EpisodeDetailsEnum.stopReason }));
		obj.setDescription("");
		obj.setAllPatients(true);
		obj.setName("test name");
		obj.setReportObjectId(123);

		ReportObjectXMLEncoder enc = new ReportObjectXMLEncoder(obj);
		String xml = enc.toXmlString();

		// log.debug(xml);

		ReportObjectXMLDecoder dec = new ReportObjectXMLDecoder(xml);
		PatientExportObject rep = (PatientExportObject) dec.toBaseReportObject();

		Assert.assertEquals(obj, rep);
	}

	@Test()
	public void testXMLEncoderWithPrescriptionExportObject() {
		PrescriptionExportObject obj = new PrescriptionExportObject();
		obj.addSimpleColumn(SimpleColumnsEnum.patientId);
		obj.addSimpleColumn(SimpleColumnsEnum.firstNames);
		obj.addColumns(ScriptColumn.ALL);
		obj.setDescription("");
		obj.setAllPatients(true);
		obj.setName("test name");
		obj.setReportObjectId(123);

		ReportObjectXMLEncoder enc = new ReportObjectXMLEncoder(obj);
		String xml = enc.toXmlString();

		ReportObjectXMLDecoder dec = new ReportObjectXMLDecoder(xml);
		PrescriptionExportObject rep = (PrescriptionExportObject) dec
		.toBaseReportObject();

		Assert.assertEquals(obj, rep);
	}
}
