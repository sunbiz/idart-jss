package model.manager.exports;

public interface PackageExportObject extends Cloneable{
	
	public Object getData(DataExportFunctions functions, int index);

	/**
	 * @return the title
	 */
	public abstract String getTitle();

	/**
	 * @return the dataType
	 */
	public abstract Class getDataType();

	/**
	 * @return the columnIndex
	 */
	public abstract int getColumnIndex();

	public abstract String getSubTitle();

	/**
	 * @return the columnWidth
	 */
	public abstract int getColumnWidth();

	public void setXY(int columnIndex, int rowCounter);

	public PackageExportObject clone();

	public void setColumnIndex(int i);

	public void setTitle(String string);

}