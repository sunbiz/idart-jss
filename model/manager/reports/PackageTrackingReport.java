package model.manager.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.eclipse.swt.widgets.Shell;

public class PackageTrackingReport extends AbstractJasperReport {

	private final String patientId;

	public PackageTrackingReport(Shell parent, String patientId) {
		super(parent);
		this.patientId = patientId;
	}

	@Override
	protected void generateData() throws ReportException {

	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		
		// Set the parameters for the report
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("path", getReportPath());
		map.put("HIBERNATE_SESSION", hSession);
		map.put("patId", patientId);
		map.put("date", new Date());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "packageTrackingReport";
	}

}
