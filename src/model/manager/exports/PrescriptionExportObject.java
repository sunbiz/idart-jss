package model.manager.exports;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A DataExport contains a list of {@link ExportColumn}'s along with a set of
 * patients (or allPatients) for which the data represented by the
 * ExportColumn's whill be exported.
 */
public class PrescriptionExportObject extends BaseReportObject implements
		ReportObject, Serializable {

	private static final long serialVersionUID = 2848990186087487681L;

	private static Logger log = Logger
	.getLogger(PrescriptionExportObject.class);

	public PrescriptionExportObject() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.manager.exports.ReportObject#generateTemplate()
	 */
	@Override
	public String generateTemplate() {
		StringBuilder sb = new StringBuilder();

		// print out the columns
		sb.append(generateHeaderTemplate());

		sb.append("\n");

		// print out the data
		sb.append(generateDataTemplate());

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.manager.exports.ReportObject#generateDataTemplate()
	 */
	@Override
	public String generateDataTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append("$!{fn.setPatientSet($patientSet)}");
		sb.append("#foreach($patientId in $patientSet.entityIds)\n");
		sb.append("$!{fn.setPatientId($patientId)}");
		sb.append("#foreach($scriptId in $fn.getScriptIds())\n");
		sb.append("$!{fn.setScriptId($scriptId)}");
		if (getColumns().size() > 0) {
			sb.append(getColumns().get(0).toTemplateString());
			for (int i = 1; i < getColumns().size(); i++) {
				sb.append("$!{fn.getSeparator()}");
				sb.append(getColumns().get(i).toTemplateString());
			}
		} else {
			log.warn("Report " + getName() + "has no columns");
		}
		sb.append("\n#end\n");
		sb.append("\n#end\n");
		return sb.toString();
	}

	protected String generateHeaderTemplate() {
		StringBuilder sb = new StringBuilder();
		if (getColumns().size() >= 1) {
			sb.append(getColumns().get(0).getTemplateColumnName());
			for (int i = 1; i < getColumns().size(); i++) {
				sb.append("$!{fn.getSeparator()}");
				sb.append(getColumns().get(i).getTemplateColumnName());
			}
		}
		return sb.toString();
	}
}
