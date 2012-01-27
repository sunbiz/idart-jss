package model.manager.reports;

import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.StockCenter;
import org.eclipse.swt.widgets.Shell;

public class MonthlyStockReceiptReport extends AbstractJasperReport {

	private final String month;
	private final StockCenter stockCenter;
	private final String year;

	public MonthlyStockReceiptReport(Shell parent, String Month, String Year,
			StockCenter stockCenter) {
		super(parent);
		month = Month;
		year = Year;
		this.stockCenter = stockCenter;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		int MonthInt = 0;
		if (month.equals("January")) {
			MonthInt = 1;
		} else if (month.equals("February")) {
			MonthInt = 2;
		} else if (month.equals("March")) {
			MonthInt = 3;
		} else if (month.equals("April")) {
			MonthInt = 4;
		} else if (month.equals("May")) {
			MonthInt = 5;
		} else if (month.equals("June")) {
			MonthInt = 6;
		} else if (month.equals("July")) {
			MonthInt = 7;
		} else if (month.equals("August")) {
			MonthInt = 8;
		} else if (month.equals("September")) {
			MonthInt = 9;
		} else if (month.equals("October")) {
			MonthInt = 10;
		} else if (month.equals("November")) {
			MonthInt = 11;
		} else if (month.equals("December")) {
			MonthInt = 12;
		}
		String startDayStr;
		if (MonthInt > 9) {
			startDayStr = year + "-" + MonthInt + "-01 00:00:00";
		} else {
			startDayStr = year + "-0" + MonthInt + "-01 00:00:00";
		}

		java.sql.Timestamp theDate = java.sql.Timestamp.valueOf(startDayStr);
		// Set the parameters for the report
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("p_path", getReportPath());
		map.put("stockCenterId", stockCenter.getId());
		map.put("stockCenterName", stockCenter.getStockCenterName());
		map.put("p_theDate", theDate);
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "stockReceiptPerMonth";
	}

}
