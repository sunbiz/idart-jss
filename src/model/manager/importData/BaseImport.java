package model.manager.importData;

import java.io.File;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.excel.interfaces.ReadExcelReportInterface;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;

public abstract class BaseImport {

	private ReadExcelReportInterface importSheet;

	public BaseImport() {
		super();
	}

	/**
	 * 
	 * @param filename
	 * @param sheetName
	 * @throws ReportException
	 */
	public void importData(String filename, String sheetName,
			IProgressMonitor monitor) throws ReportException {
		try {
			monitor.beginTask("Preparing for import", IProgressMonitor.UNKNOWN);
			File file = new File(filename);
			Session hSession = HibernateUtil.getNewSession();

			importSheet = getReportSheets(sheetName);
			importSheet.setSession(hSession);
			importSheet.init();
			monitor.setTaskName("Checking for import:"
					+ importSheet.getClass().getSimpleName());
			checkReport(file, importSheet);

			importSheet.persistData(monitor);
			importSheet.closeSheet();

			hSession.close();
		} catch (Exception ie) {
			throw new ReportException(ie.getMessage(), ie);
		}
	}
	
	public ReadExcelReportInterface getImportSheet() {
		return importSheet;
	}

	protected abstract ReadExcelReportInterface getReportSheets(String sheetName);

	/**
	 * Process check to see if correct Sheet
	 * 
	 * @param stream
	 * @param rri
	 * @return boolean
	 * 
	 * @throws ReportException
	 */
	private void checkReport(File file, ReadExcelReportInterface rri)
		throws ReportException {
		if (!rri.openSheet(file)){
			throw new ReportException("Unable to open the Excel sheet");
		}

		rri.findColumns();
		String error = rri.checkColumns();
		if (error != null)
			throw new ReportException(error);
	}
}
