package model.manager.exports;

public abstract class AbstractExportObject implements PackageExportObject {

	private String title;
	private Class dataType;
	private int columnIndex = -1;
	private int columnWidth = 15;
	private String subtitle;
	protected int currentColumnIndex;
	protected int rowCounter;

	public AbstractExportObject() {
		super();
	}
	
	public AbstractExportObject(String title, Class dataType) {
		this.title = title;
		this.dataType = dataType;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.PackageExportObject#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.PackageExportObject#getDataType()
	 */
	@Override
	public Class getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(Class dataType) {
		this.dataType = dataType;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.PackageExportObject#getColumnIndex()
	 */
	@Override
	public int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * @param columnIndex
	 *            the columnIndex to set
	 */
	@Override
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.PackageExportObject#getSubTitle()
	 */
	@Override
	public String getSubTitle() {
		return subtitle;
	}
	
	public void setSubTitle(String subtitle) {
		this.subtitle = subtitle;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.PackageExportObject#getColumnWidth()
	 */
	@Override
	public int getColumnWidth() {
		return columnWidth;
	}

	/**
	 * @param columnWidth the columnWidth to set
	 */
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	@Override
	public DrugDispensedObject clone() {
		try {
			return (DrugDispensedObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setXY(int currentColumnIndex, int rowCounter) {
		this.currentColumnIndex = currentColumnIndex;
		this.rowCounter = rowCounter;
	}

}