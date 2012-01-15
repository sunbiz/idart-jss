package org.celllife.idart.gui.dataExports;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.exports.DataExporter;
import model.manager.exports.ReportObject;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.misc.AbstractCancellableJob;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is used to run the data export and show a progress dialog.
 */
public class DataExportJob extends AbstractCancellableJob {

	private final ReportObject dataExportObject;
	private final String fileName;

	/**
	 * @param fileName
	 *            File to write data to.
	 * @param deo
	 *            DataExportObject to export.
	 */
	public DataExportJob(String fileName, ReportObject deo) {
		this.fileName = fileName;
		this.dataExportObject = deo;
	}

	@Override
	public void performJob(IProgressMonitor monitor) throws ReportException {
		DataExporter exporter = new DataExporter(
				HibernateUtil.getNewSession(), monitor);
		exporter.generateExport(dataExportObject, fileName);
	}
}
