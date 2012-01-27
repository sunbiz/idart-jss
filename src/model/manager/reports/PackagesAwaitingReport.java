package model.manager.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.eclipse.swt.widgets.Shell;

public class PackagesAwaitingReport extends AbstractJasperReport {

	private final String clinicName;
	private List<String[]> theStringList = new ArrayList<String[]>();
	private File csvFile;

	public PackagesAwaitingReport(Shell parent, String clinicName,
			List<String[]> theList) {
		super(parent);
		this.clinicName = clinicName;
		this.theStringList = theList;
	}

	@Override
	protected void generateData() throws ReportException {

		csvFile = createCSVFile("tmpPackagesAwaiting.csv", theStringList, true);

	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clinicName", clinicName);
		map.put("path", getReportPath());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "packagesAwaiting";
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
