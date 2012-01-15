package model.manager.importData;

import model.manager.excel.reports.in.PatientSheet;

public class ImportPatients extends BaseImport {

	public ImportPatients() {
		super();
	}

	@Override
	protected PatientSheet getReportSheets(String sheetName) {
		return new PatientSheet(sheetName);
	}

}