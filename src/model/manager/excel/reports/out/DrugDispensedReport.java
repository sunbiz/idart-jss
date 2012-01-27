package model.manager.excel.reports.out;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.manager.excel.download.XLWriteManager;
import model.manager.excel.interfaces.GenerateExcelReportInterface;
import model.manager.exports.DataExportFunctions;
import model.manager.exports.DrugDispensedObject;
import model.manager.exports.PackageExportObject;
import model.manager.exports.excel.ExcelReportObject;

import org.celllife.idart.commonobjects.LocalObjects;

/**
 */
public class DrugDispensedReport implements GenerateExcelReportInterface {

	private static final int COLUMN_INDEX_OFFSET = 1;

	private int rowCounter;
	private String sheetNAME;

	private XLWriteManager xlwm = null;

	private ExcelReportObject reportObject;
	
	/**
	 * Constructor for DailyDispensingReport.
	 * 
	 * @param path
	 *            String
	 * @param reportObject
	 */
	public DrugDispensedReport(String path, ExcelReportObject reportObject) {
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

	/**
	 * Method writeHeadings.
	 * 
	 * @see model.manager.excel.interfaces.GenerateExcelReportInterface#writeHeadings()
	 */
	@Override
	public void writeHeadings(int numextracolumns) {
		List<PackageExportObject> columns = reportObject.getColumns();
		int drugColumnOffset = 0;
		for (int i = 0; i < columns.size(); i++) {

			PackageExportObject col = columns.get(i);
			col.setColumnIndex(COLUMN_INDEX_OFFSET + i
					+ drugColumnOffset);

			writeHeading(col);
			if (col instanceof DrugDispensedObject){
				DrugDispensedObject ddo = (DrugDispensedObject) col;
				if (reportObject.isShowBatchInfo()
						&& ddo.getDrugId() > 0)
					drugColumnOffset++;
	
				if (ddo.getDrugId() > 0) {
					xlwm.highlightCell(col.getColumnIndex(),
							rowCounter);
				}
			}

			xlwm.resizeColumn(col.getColumnIndex(),
					col.getColumnWidth());

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
		DrugDispensedObject ddo = null;
		if (column instanceof DrugDispensedObject) {
			ddo = (DrugDispensedObject) column;
		}
		if (ddo != null && ddo.getDrugId() > 0) {
			if (reportObject.isShowBatchInfo()) {
				xlwm.mergeCells(column.getColumnIndex(), rowCounter, column
						.getColumnIndex() + 1, rowCounter);
				xlwm.mergeCells(column.getColumnIndex(), rowCounter + 1, column
						.getColumnIndex() + 1, rowCounter + 1);
			}
			xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),
					rowCounter, column.getTitle());
			xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),
					rowCounter + 1, column.getSubTitle());
			xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),
					rowCounter + 2, "Qty");

			if (reportObject.isShowBatchInfo()) {
				xlwm.writeSubHeadingCellTextCentererd(
						column.getColumnIndex() + 1, rowCounter + 2, "Batch");
			}
		} else {
			int rowsToMerge = 2;
			xlwm.mergeCells(column.getColumnIndex(), rowCounter, column
					.getColumnIndex(), rowCounter + rowsToMerge);
			xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),
					rowCounter, column.getTitle());
		}
	}

	@Override
	public void writeTitle() {
		rowCounter = 0;
		xlwm.resizeColumn(rowCounter, 3);
		xlwm.resizeRow(rowCounter, 240);
		incrementRowCounter();

		xlwm.writeTitleCell(1, rowCounter,
				"Drugs Dispensed Report (Clinics, Patients & Drugs)");
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
		xlwm.writeSubHeadingCellTextLeft(1, rowCounter, "Pharmacy");
		xlwm.mergeCells(5, rowCounter, 9, rowCounter);
		xlwm.writeSubHeadingCellTextLeft(5, rowCounter, reportObject
				.getPharmacy());
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
	
	@Override
	public void writeRow(DataExportFunctions functions) {
		List<PackageExportObject> columns = reportObject.getColumns();
		incrementRowCounter();
		for (PackageExportObject column : columns) {
			DrugDispensedObject col = (DrugDispensedObject) column;
			writeDataCell(column.getColumnIndex(), column.getData(functions, 0));
			if (reportObject.isShowBatchInfo() && col.getDrugId() > 0){
				writeDataCell(column.getColumnIndex()+1, column.getData(functions, 1));
			}
		}
	}

	@Override
	public void writeFooter() {
		List<PackageExportObject> columns = reportObject.getColumns();
		incrementRowCounter();
		incrementRowCounter();
		boolean writeFooterLabels = true;
		for (PackageExportObject column : columns) {
			if (!(column instanceof DrugDispensedObject)){
				continue;
			}
			DrugDispensedObject col = (DrugDispensedObject) column;
			int drugid = col.getDrugId();
			if (drugid > 0){
				if (writeFooterLabels){
					xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex()-1,rowCounter, "Total Units Dispensed");
					xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex()-1,getRowCounter()+1, "Pack Size");
					xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex()-1,getRowCounter()+2, "Total Packs Dispensed");
					if (reportObject.isShowBatchInfo())
						xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex()-1,getRowCounter()+5, "Batch Summary");
					writeFooterLabels = false;
				}
				
				// write totals
				int totalUnitsDispensed = col.getTotalUnitsDispensed();
				xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),rowCounter, String.valueOf(totalUnitsDispensed));
				int drugPackSize = col.getDrugPackSize();
				writeDataCell(column.getColumnIndex(),getRowCounter()+1, drugPackSize);
				writeDataCell(column.getColumnIndex(),getRowCounter()+2, getPacksDispensed(totalUnitsDispensed, drugPackSize));
				
				xlwm.writeSubHeadingCellTextCentererd(column.getColumnIndex(),getRowCounter()+3, column.getSubTitle());
				if (reportObject.isShowBatchInfo()){
					// blank cells in batch column of totals rows
					writeDataCell(column.getColumnIndex()+1, "");
					writeDataCell(column.getColumnIndex()+1,getRowCounter()+1, "");
					writeDataCell(column.getColumnIndex()+1,getRowCounter()+2, "");
					
					// merge sub-title cells
					mergeCells(column.getColumnIndex(), getRowCounter()+3, column.getColumnIndex()+1, getRowCounter()+3);
				
					// write batch summary
					Map<String,Integer> batchTotalsMap = col.getBatchTotalsMap();
					int tmprowCounter = 5;
					if (batchTotalsMap != null) {
						for (Entry<String, Integer> entry : batchTotalsMap
								.entrySet()) {
							Integer batchTotal = entry.getValue();
							String batchName = entry.getKey();
							writeDataCell(column.getColumnIndex(),
									getRowCounter() + tmprowCounter, batchTotal);
							writeDataCell(column.getColumnIndex() + 1,
									getRowCounter() + tmprowCounter, batchName);
							tmprowCounter++;
						}
					} else{
						writeDataCell(column.getColumnIndex(),
								getRowCounter() + tmprowCounter, "");
						writeDataCell(column.getColumnIndex() + 1,
								getRowCounter() + tmprowCounter, "");
					}
				}
			}
		}
	}
	
	private Object getPacksDispensed(int totalUnitsDispensed, int drugPackSize) {
		int packs = totalUnitsDispensed / drugPackSize;
		int remainder = totalUnitsDispensed % drugPackSize;
		String out = String.valueOf(packs);
		if (remainder > 0){
			out += " (" + remainder + ")";
		}
		return out;
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

	@Override
	public void incrementRowCounter() {
		rowCounter++;
	}

	@Override
	public void incrementRowCounter(int amount) {
		rowCounter += amount;
	}

	@Override
	public void writeDataCell(int columnIndex, Object data) {
		writeDataCell(columnIndex, rowCounter, data);
	}

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

	@Override
	public void writeExtraColumns(DataExportFunctions functions, int num) {
	}
}
