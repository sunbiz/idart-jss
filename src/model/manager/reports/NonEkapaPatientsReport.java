package model.manager.reports;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.manager.AdministrationManager;
import model.manager.PatientManager;
import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.integration.eKapa.EKapa;
import org.eclipse.swt.widgets.Shell;

public class NonEkapaPatientsReport extends AbstractJasperReport {

	private final String clinic;
	private File csvFile;

	public NonEkapaPatientsReport(Shell parent, String clinic) {
		super(parent);
		this.clinic = clinic;
	}

	@Override
	protected void generateData() throws ReportException {
		final Clinic c = AdministrationManager.getClinic(hSession, clinic);
		final SimpleDateFormat theDateFormat = new SimpleDateFormat(
		"dd MMM yyyy");

		final List<String[]> theStringList = new ArrayList<String[]>();
		List<Patient> thePatientList;
		try {
			thePatientList = EKapa.getNonEkapaPatients(hSession, c);
		} catch (SQLException e) {
			throw new ReportException("Error getting non Ekapa Patients", e);
		}

		for (Patient pat : thePatientList) {
			String[] s = new String[6];
			s[0] = pat.getPatientId();
			s[1] = pat.getLastname() + ", " + pat.getFirstNames();
			s[2] = theDateFormat.format(pat.getDateOfBirth());
			s[3] = pat.getSex() + "";
			Episode ep = PatientManager.getFirstEpisode(pat);
			if ((ep != null) && (ep.getStartDate() != null)) {
				s[4] = theDateFormat.format(ep.getStartDate());
			} else {
				s[4] = "";
			}
			theStringList.add(s);
		}

		// print the header
		theStringList.add(0, new String[] { "patientid", "patientname",
				"dateofbirth", "sex", "episode start date" });

		csvFile = createCSVFile("tmpNonEkapaPatients.csv", theStringList, true);
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", getReportPath());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
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

	@Override
	protected String getReportFileName() {
		return "nonEkapaPatientsReport";
	}
}
