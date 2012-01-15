package org.celllife.idart.gui.reportParameters;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.exports.excel.ExcelExporter;
import model.manager.exports.excel.ExcelReportObject;

import org.celllife.idart.misc.AbstractCancellableJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.program.Program;

/**
 * This class is used to run the data export and show a progress dialog.
 */
public class ExcelReportJob extends AbstractCancellableJob {

	private final ExcelReportObject reportObject;
	private final ExcelExporter exporter;

	/**
	 * @param deo
	 *            DataExportObject to export.
	 * @param exporter 
	 * @param fileName
	 *            File to write data to.
	 */
	public ExcelReportJob(ExcelReportObject deo, ExcelExporter exporter) {
		this.reportObject = deo;
		this.exporter = exporter;
	}

	@Override
	public void performJob(final IProgressMonitor monitor)
			throws ReportException {
		exporter.setMonitor(monitor);
		exporter.generate(reportObject);
		Program.launch(reportObject.getPath());
	}
}
