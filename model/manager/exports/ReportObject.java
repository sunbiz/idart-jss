package model.manager.exports;

import java.util.List;

import model.manager.exports.columns.SimpleColumnsEnum;
import model.nonPersistent.EntitySet;

public interface ReportObject {
	public Integer getReportObjectId();

	public void setReportObjectId(Integer id);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	/**
	 * Generate a template according to this reports columns. Template includes
	 * column headers.
	 * 
	 * @return template string to be evaluated
	 */
	public String generateTemplate();

	/**
	 * Generate a template according to this reports columns excluding column
	 * headers
	 * 
	 * @return template string to be evaluated
	 */
	public String generateDataTemplate();

	public EntitySet getPatientSet();

	public void setAllPatients(boolean b);

	public void addSimpleColumn(SimpleColumnsEnum selection);

	public void addColumn(ExportColumn exportColumn);

	public List<ExportColumn> getColumns();
}
