package model.manager.exports;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import model.manager.exports.columns.SimpleColumnsEnum;
import model.nonPersistent.EntitySet;

public abstract class BaseReportObject implements ReportObject {

	private Integer reportObjectId; // can be used as a database primary key
	private String name;
	private String description;
	private EntitySet patientSet;
	private boolean isAllPatients = false;
	private List<ExportColumn> columns = new Vector<ExportColumn>();

	public BaseReportObject() {
		// do nothing
	}

	public BaseReportObject(Integer reportObjectId, String name,
			String description) {
		this.reportObjectId = reportObjectId;
		this.name = name;
		this.description = description;
	}

	/**
	 * @return Returns the reportObjectId.
	 */
	@Override
	public Integer getReportObjectId() {
		return reportObjectId;
	}

	/**
	 * @param reportObjectId
	 *            The reportObjectId to set.
	 */
	@Override
	public void setReportObjectId(Integer reportObjectId) {
		this.reportObjectId = reportObjectId;

	}

	/**
	 * @return Returns the name;
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the description
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param name
	 *            The description to set.
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isAllPatients() {
		return isAllPatients;
	}

	@Override
	public void setAllPatients(boolean isAllPatients) {
		this.isAllPatients = isAllPatients;
	}

	@Override
	public EntitySet getPatientSet() {
		return patientSet;
	}

	public void setPatientSet(EntitySet patientSet) {
		this.patientSet = patientSet;
	}

	@Override
	public List<ExportColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ExportColumn> columns) {
		this.columns = columns;
	}

	/**
	 * Append a simple column
	 * 
	 * @param columnName
	 * @param columnValue
	 */
	@Override
	public void addSimpleColumn(SimpleColumnsEnum columnValue) {
		columns.add(new SimpleColumn(columnValue));
	}

	/**
	 * Append a simple column
	 * 
	 * @param columnName
	 * @param columnValue
	 */
	@Override
	public void addColumn(ExportColumn column) {
		columns.add(column);
	}

	/**
	 * Add multiple columns
	 * 
	 * @param columnArray
	 */
	public void addColumns(ExportColumn... columnArray) {
		for (ExportColumn column : columnArray) {
			columns.add(column);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAllPatients ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
		+ ((patientSet == null) ? 0 : patientSet.hashCode());
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
		BaseReportObject other = (BaseReportObject) obj;
		if (isAllPatients != other.isAllPatients)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (patientSet == null) {
			if (other.patientSet != null)
				return false;
		} else if (!patientSet.equals(other.patientSet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getReportObjectId() + ", " + this.getName() + ", "
		+ this.getDescription();
	}

	public List<String> getColumnsAsStringList() {
		List<String> cols = new ArrayList<String>();
		for (ExportColumn column : columns) {
			cols.add(column.toString()); // returns only the headings.
		}
		return cols;
	}
}
