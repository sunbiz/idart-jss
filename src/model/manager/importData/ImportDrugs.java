package model.manager.importData;

import model.manager.excel.reports.in.DrugSheet;

public class ImportDrugs extends BaseImport {

	public ImportDrugs() {
		super();
	}

	@Override
	protected DrugSheet getReportSheets(String sheetName) {
		return new DrugSheet();
	}

}