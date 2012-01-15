package model.manager.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.eclipse.swt.widgets.Shell;

public class StockReportPerDrug extends AbstractJasperReport {

	private final int drugId;
	private final Date startDate;
	private final Date endDate;

	public StockReportPerDrug(Shell parent, int drugId, Date startDate,
			Date endDate) {
		super(parent);
		this.drugId = drugId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("p_drugId", new Integer(drugId));
		map.put("p_path", getReportPath());
		map.put("p_startDate", startDate);
		map.put("p_endDate", endDate);

		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "stockPerDrug";
	}

}
