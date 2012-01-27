package org.celllife.idart.gui.reportParameters;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.reports.iDARTReport;

import org.celllife.idart.misc.AbstractCancellableJob;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is used to run the data export and show a progress dialog.
 */
public class ReportJob extends AbstractCancellableJob {

	private final iDARTReport reportObject;

	/**
	 * @param fileName
	 *            File to write data to.
	 * @param deo
	 *            DataExportObject to export.
	 */
	public ReportJob(iDARTReport deo) {
		this.reportObject = deo;
	}

	@Override
	public void performJob(final IProgressMonitor monitor) throws ReportException {
		reportObject.fillReport(monitor);
	}
}
