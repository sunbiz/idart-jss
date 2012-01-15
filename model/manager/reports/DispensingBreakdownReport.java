package model.manager.reports;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.eclipse.swt.widgets.Shell;

public class DispensingBreakdownReport extends AbstractJasperReport {

	private final String clinicName;
	private final String year;
	private final String month;

	public DispensingBreakdownReport(Shell parent, String clinicName,
			String Month, String Year) {
		super(parent);
		this.clinicName = clinicName;
		month = Month;
		year = Year;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
		java.sql.Timestamp theSQLDate;
		Date theJavaDate;
		String date = "01-" + month + "-" + year;
		try {
			theJavaDate = sdf.parse(date);
		} catch (ParseException p) {
			throw new ReportException("Unable to parse date: " + date);
		}

		theSQLDate = new Timestamp(theJavaDate.getTime());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", getReportPath());
		map.put("clinic", clinicName);
		map.put("startday", theSQLDate);
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());

		return map;
	}

	@Override
	protected String getReportFileName() {
		return "monthlyPatientsItemsRecord";
	}

}
