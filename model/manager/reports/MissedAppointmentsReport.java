package model.manager.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.AdministrationManager;
import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.eclipse.swt.widgets.Shell;

public class MissedAppointmentsReport extends AbstractJasperReport {

	private final String clinicName;
	private final int minDays;
	private final Date theDate;
	private final int maxDays;

	public MissedAppointmentsReport(Shell parent, String clinicName,
			int minDays, int maxDays, Date theDate) {
		super(parent);
		this.clinicName = clinicName;
		this.minDays = minDays;
		this.maxDays = maxDays;
		this.theDate = theDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Clinic c = AdministrationManager.getClinic(hSession, clinicName);

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("path", getReportPath());
		map.put("clinic", clinicName);
		map.put("clinicid", new Integer(c.getId()));
		map.put("minDays", minDays);
		map.put("maxDays", maxDays);
		map.put("showPatientNames", true);
		map.put("date", theDate);
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "missedAppointmentsReport";
	}

}
