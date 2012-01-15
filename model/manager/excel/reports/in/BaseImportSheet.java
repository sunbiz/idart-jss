package model.manager.excel.reports.in;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.manager.excel.download.XLReadManager;
import model.manager.excel.download.XLWriteManager;
import model.manager.excel.interfaces.ReadExcelReportInterface;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class BaseImportSheet implements ReadExcelReportInterface {

	private int errorCol = -1;
	private XLReadManager xlr = null;
	protected XLWriteManager xlw = null;
	protected int dataStartRow = 0;
	private final String sheetName;
	private Session session;
	private int errorCount;

	public BaseImportSheet(String sheetName) {
		super();
		this.sheetName = sheetName;
	}
	
	@Override
	public void init() {
	}
	
	public String getSheetName() {
		return sheetName;
	}
	
	@Override
	public void setSession(Session hSession) {
		this.session = hSession;
	}
	
	protected Session getSession() {
		return session;
	}

	/**
	 * Method closeSheet.
	 * 
	 * @see model.manager.excel.interfaces.ReadExcelReportInterface#closeSheet()
	 */
	@Override
	public void closeSheet() {
		xlr = null;
	}

	/**
	 * Method openSheet.
	 * 
	 * @param stream
	 *            byte[]
	 * @see model.manager.excel.interfaces.ReadExcelReportInterface#openSheet(File)
	 */
	@Override
	public boolean openSheet(File file) {
		try {
			xlr = new XLReadManager(file, sheetName);
			return xlr.getReadableSheet() != null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * checks if all fields are blank, if so, returns true
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	protected List<String> getRow(int row) {
		List<String> rowList = new ArrayList<String>();
		boolean allEmpty = true;
		for (int i = 0; i < errorCol; i++) {
			String txt = xlr.readCell(i, row, true);
			rowList.add(txt);
			if (!txt.isEmpty()) {
				allEmpty = false;
			}
		}
	
		if (allEmpty){
			rowList.clear();
		}
		return rowList;
	}
	
	/**
	 * Method persistData.
	 * 
	 * @param session
	 *            Session
	 * @see model.manager.excel.interfaces.ReadExcelReportInterface#persistData(Session)
	 */
	@Override
	public void persistData(IProgressMonitor monitor) {
		Transaction tx = null;
		try {
			/**
			 * open error file for writing
			 */
			xlw = new XLWriteManager(getSheetName());

			monitor.setTaskName("Writing columns to error file");
			copyColumnHeadersToErrorFile();

			int rows = xlr.getReadableSheet().getRows();
			for (int i = dataStartRow; i < rows; i++) {
				monitor.setTaskName("Processing row: " + i);
				if (monitor.isCanceled()){
					return;
				}
				/**
				 * Check if its end of file
				 */
				List<String> row = getRow(i);
				if (row.isEmpty())
					return;
				
				tx = session.beginTransaction();
				if (!readRow(i, row)){
					errorCount++;
					tx.rollback();
				} else {
					tx.commit();
				}
				monitor.worked(100);
			}
			// close write file
			session.flush();
		} catch (Exception e) {
			errorCount++;
			xlw.writeCell(0, 0, e.getMessage());
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			xlw.closeFile();
		}
	}
	
	@Override
	public int getErrorCount() {
		return errorCount;
	}
	
	@Override
	public File getErrorFile(){
		return xlw.getErrorFile();
	}
	
	/**
	 * Method findColumns.
	 * 
	 * @return boolean
	 * @see model.manager.excel.interfaces.ReadExcelReportInterface#findColumns()
	 */
	@Override
	public void findColumns() {
		List<String> rowList = new ArrayList<String>();
		int maxColumns = xlr.getReadableSheet().getColumns();
		boolean allEmpty = true;
		do {
			rowList.clear();
			for (int i = 0; i < maxColumns; i++) {
				String txt = xlr.readCell(i, dataStartRow, false).trim();
				rowList.add(txt);
				if (!txt.isEmpty()) {
					allEmpty = false;
				}
			} 
			dataStartRow++;
		} while (allEmpty);

		findColumns(rowList);
	}

	protected abstract void findColumns(List<String> rowList);

	protected abstract boolean readRow(int rowNumber, List<String> row);

	/**
	 * Method to write the column headings for the error file The error file
	 * will have the same headings as the input file plus an extra column fo the
	 * reason of error
	 * 
	 */
	protected void copyColumnHeadersToErrorFile() {
		int maxColumns = xlr.getReadableSheet().getColumns();
		// marker to mark where last REAL column is
		int marker = maxColumns;
		// write columns as is from input file
		for (int i = 0; i < maxColumns; i++) {
			String string = xlr.readCell(i, dataStartRow - 1, false);
			if (!string.isEmpty()) {
				xlw.writeHeadingCell(i, string);
			} else {
				marker = i;
				break;
			}
		}
		// add a column to indicate reason for error only if it hasn't been
		// added already
		if (errorCol == -1) {
			xlw.writeHeadingCell(marker, "Reason for Error");
			errorCol = marker;
		}
		xlw.incrRowCount();
	}

	protected void copyRowToErrorFile(int row, String errorMessage) {
		int maxColumns = xlr.getReadableSheet().getColumns();
		int writeRow = xlw.getRowCount();
		for (int i = 0; i < maxColumns; i++) {
			String entry = xlr.readCell(i, row, false);
			xlw.writeCell(i, writeRow, entry);
		}
		xlw.writeCell(errorCol, writeRow, errorMessage);
		xlw.incrRowCount();
	}
	
	public void writeTemplateSheet(String filePath){
		xlw = new XLWriteManager("Sheet1", filePath);
		
		int rowNum = 0;
		List<String> row = getTemplateHeaders();
		for (int i = 0; i < row.size(); i++) {
			xlw.writeHeadingCell(i, rowNum, row.get(i));
		}
		rowNum++;
		
		row = getTemplateCompulsoryValues();
		for (int i = 0; i < row.size(); i++) {
			xlw.writeCell(i, rowNum, row.get(i));
		}
		rowNum++;
		
		row = getTemplateColumnTypes();
		for (int i = 0; i < row.size(); i++) {
			xlw.writeCell(i, rowNum, row.get(i));
		}
		xlw.closeFile();
	}

	protected List<String> getTemplateHeaders() {
		return new ArrayList<String>();
	}

	protected List<String> getTemplateCompulsoryValues() {
		return new ArrayList<String>();
	}

	protected List<String> getTemplateColumnTypes() {
		return new ArrayList<String>();
	}

}