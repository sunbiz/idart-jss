package model.manager.excel.reports.out;

import java.text.MessageFormat;
import java.util.List;

import model.manager.excel.download.XLWriteManager;
import model.manager.excel.interfaces.GenerateExcelReportInterface;
import model.manager.exports.DataExportFunctions;
import model.manager.exports.PackageExportObject;
import model.manager.exports.excel.ExcelReportObject;

import org.celllife.idart.commonobjects.LocalObjects;

/**
 */
public class CohortDrugCollectionsReport implements GenerateExcelReportInterface {

	private static final int COLUMN_INDEX_OFFSET = 1;

	private int rowCounter;
	private String sheetNAME;

	private XLWriteManager xlwm = null;

	private ExcelReportObject reportObject;

	private List<PackageExportObject> extraColumns;
	
	/**
	 * Constructor for DailyDispensingReport.
	 * 
	 * @param path
	 *            String
	 * @param reportObject
	 */
	public CohortDrugCollectionsReport(String path, ExcelReportObject reportObject) {
		super();
		if (reportObject.isShowBatchInfo()) {
			sheetNAME = "Drug Issues (Incl Batch Info)";
		} else {
			sheetNAME = "Drug Issues";
		}
		this.reportObject = reportObject;
		this.rowCounter = 1;
		xlwm = new XLWriteManager(this.sheetNAME, path);
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#writeHeadings(int)
	 */
	@Override
	public void writeHeadings(int numextracolumns) {
		List<PackageExportObject> columns = reportObject.getColumns();
		int drugColumnOffset = 0;
		for (int i = 0; i < columns.size(); i++) {

			PackageExportObject drugDispensedObject = columns.get(i);
			drugDispensedObject.setColumnIndex(COLUMN_INDEX_OFFSET + i
					+ drugColumnOffset);

			writeHeading(drugDispensedObject);

			xlwm.resizeColumn(drugDispensedObject.getColumnIndex(),
					drugDispensedObject.getColumnWidth());

		}
		extraColumns = reportObject.getEndColumns();
		if (extraColumns != null){
			for (int i = 0; i < numextracolumns; i++) {
				for (int j = 0; j < extraColumns.size(); j++) {
					PackageExportObject ddo = extraColumns.get(j);
					ddo = ddo.clone();
					ddo.setColumnIndex(COLUMN_INDEX_OFFSET + (i*extraColumns.size()) + j + columns.size());
		
					ddo.setTitle(ddo.getTitle() + " - " + (1+i));
					writeHeading(ddo);
		
					if (i%2 == 0) {
						xlwm.highlightCell(ddo.getColumnIndex(),
								rowCounter);
					}
		
					xlwm.resizeColumn(ddo.getColumnIndex(),
							ddo.getColumnWidth());
				}
			}
		}
		xlwm.resizeRow(rowCounter, 600);
		incrementRowCounter(2);
	}

	/**
	 * 
	 * @param column
	 * @param text
	 */
	private void writeHeading(PackageExportObject column) {
		int rowsToMerge = 2;
		xlwm.mergeCells(column.getColumnIndex(), rowCounter, column
				.getColumnIndex(), rowCounter + rowsToMerge);
		xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),
				rowCounter, column.getTitle());
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#writeTitle()
	 */
	@Override
	public void writeTitle() {
		rowCounter = 0;
		xlwm.resizeColumn(rowCounter, 3);
		xlwm.resizeRow(rowCounter, 240);
		incrementRowCounter();

		xlwm.writeTitleCell(1, rowCounter,
				"Cohort Drug Collections (showing early and late pickups)");
		xlwm.mergeCells(1, rowCounter, 9, rowCounter);
		xlwm.resizeRow(rowCounter, 600);
		incrementRowCounter();

		xlwm.resizeRow(rowCounter, 240);
		incrementRowCounter();

		xlwm.mergeCells(1, rowCounter, 4, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(1, rowCounter, "Facility Name");
		xlwm.mergeCells(5, rowCounter, 9, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(5, rowCounter, LocalObjects.pharmacy
				.getPharmacyName());
		incrementRowCounter();

		xlwm.mergeCells(1, rowCounter, 4, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(1, rowCounter,
				"Responsible Pharmacist");
		xlwm.mergeCells(5, rowCounter, 9, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(5, rowCounter, LocalObjects.pharmacy
				.getPharmacist());
		incrementRowCounter();

		xlwm.mergeCells(1, rowCounter, 4, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(1, rowCounter, "Period");
		xlwm.mergeCells(5, rowCounter, 9, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(5, rowCounter, MessageFormat.format(
				"{0,date,medium} - {1,date,medium}", reportObject
						.getStartDate(), reportObject.getEndDate()));

		incrementRowCounter();
		xlwm.resizeRow(rowCounter, 240);
		incrementRowCounter();

	}
	
	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#writeRow(model.manager.exports.DataExportFunctions)
	 */
	@Override
	public void writeRow(DataExportFunctions functions) {
		List<PackageExportObject> columns = reportObject.getColumns();
		for (PackageExportObject column : columns) {
			writeDataCell(column.getColumnIndex(), column.getData(functions, 0));
		}
	}

	/**
	 * Method fillInValues.
	 * 
	 * @param data
	 *            List<Object[]>
	 * @see 
	 *      model.manager.excel.interfaces.GenerateExcelReportInterface#fillInValues
	 *      (List<Object[]>)
	 */
	@Override
	public void fillInValues(List<Object[]> data) {
	}

	/**
	 * Method getReport.
	 * 
	 * @return byte[]
	 * @see model.manager.excel.interfaces.GenerateExcelReportInterface#getReport()
	 */
	@Override
	public byte[] getReport() {
		byte[] result = xlwm.closeFile();
		return result;
	}

	/**
	 * Method injectData.
	 * 
	 * @param list
	 *            List<Object[]>
	 * @see 
	 *      model.manager.excel.interfaces.GenerateExcelReportInterface#injectData
	 *      (List<Object[]>)
	 */
	@Override
	public void injectData(List<Object[]> list) {
		// fillInValues(list);
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#incrementRowCounter()
	 */
	@Override
	public void incrementRowCounter() {
		rowCounter++;
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#incrementRowCounter(int)
	 */
	@Override
	public void incrementRowCounter(int amount) {
		rowCounter += amount;
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#writeDataCell(int, java.lang.Object)
	 */
	@Override
	public void writeDataCell(int columnIndex, Object data) {
		writeDataCell(columnIndex, rowCounter, data);
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#writeDataCell(int, int, java.lang.Object)
	 */
	@Override
	public void writeDataCell(int columnIndex, int rowIndex, Object data) {
		xlwm.writeCellCentered(columnIndex, rowIndex, data);
	}

	public int getRowCounter() {
		return rowCounter;
	}

	public void mergeCells(int col_start, int row_start, int col_end,
			int row_end) {
		xlwm.mergeCells(col_start, row_start, col_end, row_end);
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.reports.out.ExcelReportObject#writeExtraColumns(model.manager.exports.DataExportFunctions, int)
	 */
	@Override
	public void writeExtraColumns(DataExportFunctions functions, int num) {
		if (extraColumns != null){
			int columns = reportObject.getColumns().size();
			for (int j = 0; j < extraColumns.size(); j++) {
				PackageExportObject column = extraColumns.get(j);
				int columnIndex = COLUMN_INDEX_OFFSET + columns + (extraColumns.size()*num) + j;
				column.setXY(columnIndex, rowCounter);
				writeDataCell(columnIndex, column.getData(functions, 0));
			}
		}
	}

	@Override
	public void writeFooter() {
	}
}
