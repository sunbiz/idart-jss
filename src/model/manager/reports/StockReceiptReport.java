package model.manager.reports;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.eclipse.swt.widgets.Shell;

public class StockReceiptReport extends AbstractJasperReport {

	private final Date endDate;
	private final Date startDate;

	public StockReceiptReport(Shell parent, Date startDate, Date endDate) {
		super(parent);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Boolean sameDay = false;
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)
				&& startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH)
				&& startCal.get(Calendar.DAY_OF_MONTH) == endCal
				.get(Calendar.DAY_OF_MONTH)) {
			sameDay = true;
		} else {
			sameDay = false;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("p_path", getReportPath());
		map.put("p_startDate", startDate);
		map.put("p_endDate", endDate);
		map.put("p_sameDay", sameDay);
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "stockReceipt";
	}

}
