package model.manager.exports;

public interface ExportColumn {
	String toTemplateString();

	String getTemplateColumnName();

	String getColumnName();

	String getColumnType();
}