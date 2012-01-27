package model.manager.reports;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.eclipse.swt.widgets.Shell;

public class TransactionLoggingReport extends AbstractJasperReport {

	private final Date startDate;
	private final Date endDate;

	public TransactionLoggingReport(Shell parent, Date startDate, Date endDate) {
		super(parent);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("path", getReportPath());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());

		map.put("startDate", new Timestamp(getBeginningOfDay(startDate)
				.getTime()));
		map.put("endDate", new Timestamp(getEndOfDay(endDate).getTime()));
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "transactionLogging";
	}

}
