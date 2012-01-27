package model.manager.exports;

import java.io.Serializable;

import model.manager.exports.columns.SimpleColumnsEnum;

/**
 * Object representation of a simple data export column.
 */
public class SimpleColumn implements ExportColumn, Serializable {

	private static final long serialVersionUID = -1654610615079562788L;

	public static final String COLUMN_TYPE = "SimpleColumn";

	private String columnName;
	private SimpleColumnsEnum returnValue;

	public SimpleColumn() {
	}

	public SimpleColumn(SimpleColumnsEnum columnValue) {
		returnValue = columnValue;
		columnName = columnValue.getColumnName();
	}

	@Override
	public String toTemplateString() {
		return returnValue.getTemplate();
	}

	@Override
	public String getTemplateColumnName() {
		return columnName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public SimpleColumnsEnum getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(SimpleColumnsEnum returnValue) {
		this.returnValue = returnValue;
	}

	@Override
	public String getColumnType() {
		return COLUMN_TYPE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result
		+ ((returnValue == null) ? 0 : returnValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SimpleColumn other = (SimpleColumn) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (returnValue == null) {
			if (other.returnValue != null)
				return false;
		} else if (!returnValue.equals(other.returnValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return columnName;
	}

}