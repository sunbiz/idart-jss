package model.manager.reports;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.AdministrationManager;
import model.manager.PatientManager;
import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.eclipse.swt.widgets.Shell;

public class PatientsExpectedReport extends AbstractJasperReport {

	private final String clinicName;
	private final Date theDate;
	private final String orderByProperty;
	private final String orderByDirection;
	private File csvFile;
	private String[] headers = {"patID", "patientID", "name", "contactno", "dateexpected", "scriptduration", "packcount"};
	

	public PatientsExpectedReport(Shell parent, String clinicName, Date theDate, String orderByProperty, String orderByDirection) {
		super(parent);
		this.clinicName = clinicName;
		this.theDate = theDate;
		this.orderByProperty = orderByProperty;
		this.orderByDirection = orderByDirection;
		
	}

	@Override
	protected void generateData() throws ReportException {
		csvFile = createCSVFile("patientsExpected.csv", PatientManager.getPatientsExpetcted(getHSession(), theDate, clinicName, orderByProperty, orderByDirection, headers), true);
		
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Clinic c = AdministrationManager.getClinic(hSession, clinicName);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", getReportPath());
		map.put("clinic", c.getClinicName());
		map.put("clinicid", new Integer(c.getId()));
		map.put("date", theDate);
		map.put("showPatientNames", true);
		map.put("orderBy", orderByProperty);
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "patientsExpectedReport";
	}
	
	@Override
	public Object getDataSource() throws ReportException {
		try {
			JRCsvDataSource jcvs = new JRCsvDataSource(csvFile);
			jcvs.setUseFirstRowAsHeader(true);
			return jcvs;
		} catch (Exception e) {
			throw new ReportException("Error getting data source", e);
		}
	}

}
