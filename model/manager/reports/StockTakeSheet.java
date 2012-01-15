package model.manager.reports;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.StockCenter;
import org.eclipse.swt.widgets.Shell;

public class StockTakeSheet extends AbstractJasperReport {

	private final StockCenter stockCenter;
	private final boolean includeEmptyBatches;

	public StockTakeSheet(Shell parent, StockCenter stockCenter,
			boolean includeEmptyBatches) {
		super(parent);
		this.stockCenter = stockCenter;
		this.includeEmptyBatches = includeEmptyBatches;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		// Set the parameters for the report
		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss.SSS");
		map.put("stockCenterName", stockCenter.getStockCenterName());
		map.put("stockCenterId", new Integer(stockCenter.getId()));
		map.put("date", cal.getTime());
		map.put("dateFormat", dateFormat.format(cal.getTime()));
		if (includeEmptyBatches) {
			map.put("hasUnitsRemaining", "F");
		} else {
			map.put("hasUnitsRemaining", "T");
		}

		map.put("path", getReportPath());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "stockTakeSheet";
	}

}
