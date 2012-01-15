package model.manager.excel.interfaces;

import java.util.List;

import model.manager.exports.DataExportFunctions;

/**
 * 
 */
public interface GenerateExcelReportInterface {

	/**
	 * Method fillInValues.
	 * @param data List<Object[]>
	 */
	public void fillInValues(List<Object[]> data);

	/**
	 * Method getReport.
	 * @return byte[]
	 */
	public byte[] getReport();

	/**
	 * Method injectData.
	 * @param list List<Object[]>
	 */
	public void injectData(List<Object[]> list);

	public abstract void writeHeadings(int numextracolumns);

	public abstract void writeTitle();

	public abstract void writeRow(DataExportFunctions functions);

	public abstract void incrementRowCounter();

	public abstract void incrementRowCounter(int amount);

	public abstract void writeDataCell(int columnIndex, Object data);

	public abstract void writeDataCell(int columnIndex, int rowIndex,
			Object data);

	public abstract void writeExtraColumns(DataExportFunctions functions,
			int num);

	public void writeFooter();
}
