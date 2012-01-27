package model.manager.exports;

import java.io.Serializable;

/**
 * Object representation of a simple data export column.
 */
public class ScriptColumn implements ExportColumn, Serializable {

	private static final long serialVersionUID = -7073466816205716139L;

	public static final String COL_ID = "prescriptionID";
	public static final String COL_DATE = "date";
	public static final String COL_DURATION = "duration";
	public static final String COL_DRUGINFO = "drugInfo";

	public static final ScriptColumn SCRIPTID = new ScriptColumn("Prescription ID",
			"$!{fn.replaceSeperator($fn.getScriptField(\"" + COL_ID + "\"))}");

	public static final ScriptColumn DATE = new ScriptColumn("Prescription Date",
			"$!{fn.formatDate('ymd', $fn.getScriptField(\"" + COL_DATE
			+ "\"))}");

	public static final ScriptColumn DURATION = new ScriptColumn("Duration",
			"$!{fn.getScriptField(\"" + COL_DURATION + "\")}");

	public static final ScriptColumn DRUGS = new ScriptColumn("Drugs",
			"$!{fn.replaceSeperator($fn.getScriptField(\"" + COL_DRUGINFO
			+ "\"))}");

	public static final ExportColumn[] ALL = new ExportColumn[] { SCRIPTID, DATE,
		DURATION, DRUGS };

	public static final String COLUMN_TYPE = "Episode";

	private String columnName;
	private String template;

	public ScriptColumn() {
	}

	public ScriptColumn(String name, String template) {
		columnName = name;
		this.setTemplate(template);
	}

	@Override
	public String toTemplateString() {
		return getTemplate();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result
		+ ((template == null) ? 0 : template.hashCode());
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
		ScriptColumn other = (ScriptColumn) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return columnName;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	@Override
	public String getColumnType() {
		return COLUMN_TYPE;
	}
}