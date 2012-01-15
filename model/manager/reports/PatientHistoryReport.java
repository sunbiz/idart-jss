package model.manager.reports;

import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Patient;
import org.eclipse.swt.widgets.Shell;

public class PatientHistoryReport extends AbstractJasperReport {

	private final Patient patient;

	public PatientHistoryReport(Shell parent, Patient patient) {
		super(parent);
		this.patient = patient;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patientId", patient.getPatientId());
		map.put("age", patient.getAge());
		map.put("HIBERNATE_SESSION", hSession);
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		map.put("path", getReportPath());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "patientHistory";
	}

}
