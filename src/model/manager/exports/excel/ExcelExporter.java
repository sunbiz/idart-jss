package model.manager.exports.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.excel.interfaces.GenerateExcelReportInterface;
import model.manager.exports.DataExportFunctions;
import model.nonPersistent.EntitySet;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;

public abstract class ExcelExporter {

	static Logger log = Logger.getLogger(ExcelExporter.class);
	static final int PAGE_SIZE = 100;
	protected Session session;
	protected HashMap<Integer, List<Integer>> patientPackageMap;
	private DataExportFunctions functions;
	private IProgressMonitor monitor;

	public ExcelExporter() {
		super();
	}

	public void generate(ExcelReportObject report) throws ReportException {
	
		File file = new File(report.getPath());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			throw new ReportException(e1);
		}
	
		functions = new DataExportFunctions();
		session = HibernateUtil.getNewSession();
		functions.setSession(session);
		setupFunctions(functions, report);
	
		EntitySet entitySet = getPatientSet(report);
		if (monitor != null){
			monitor.beginTask("Generating report", entitySet.getSize());
		}
		GenerateExcelReportInterface excelReport = getExcelReport(report);
		excelReport.writeTitle();
		int extraColumns = getNumberOfExtraColumn();
		excelReport.writeHeadings(extraColumns);
	
		int page = 0;
		int total = entitySet.size();
		int pages = total / PAGE_SIZE;
		try {
			while (!isCancelled()) {
				// Set up list of patients if one wasn't passed into this method
				EntitySet pagedEntitySet = entitySet.getPage(page * PAGE_SIZE,
						PAGE_SIZE);
				if (pagedEntitySet.size() == 0) {
					break;
				}
	
				log.debug("Starting data export page " + page + " of " + pages);
				updateMonitorMessage("Processed " + page * PAGE_SIZE
						+ " patients of " + total);
	
				try {
					exportPage(pagedEntitySet, functions, excelReport);
				} catch (Exception e) {
					throw new ReportException("Error running data export.", e);
				} finally {
					log.debug("Completed data export page " + page + " of "
							+ pages);
	
					updateMonitorStatus(PAGE_SIZE);
					page++;
	
					pagedEntitySet = null;
					functions.clear();
	
					log.debug("Clearing hibernate session");
					session.clear();
	
					// clear out the excess objects
					System.gc();
					System.gc();
				}
			}
	
			excelReport.writeFooter();
			try {
				fos.write(excelReport.getReport());
			} catch (IOException e) {
				throw new ReportException(e);
			}
		} catch (ReportException e) {
			throw e;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				log.error("Failed to close file stream.", e);
			}
			entitySet = null;
			report = null;
			session.close();
		}
	}

	protected int getNumberOfExtraColumn() {
		return 0;
	}

	protected abstract GenerateExcelReportInterface getExcelReport(ExcelReportObject report);
	protected abstract void exportPage(EntitySet pagedEntitySet, DataExportFunctions functions, GenerateExcelReportInterface excelReport) throws Exception;

	protected void setupFunctions(DataExportFunctions functions, ExcelReportObject report) {
		functions.setAllPatients(false);
		functions.setExportStartDate(report.getStartDate());
		functions.setExportEndDate(report.getEndDate());
	}

	protected abstract EntitySet getPatientSet(ExcelReportObject report);

	private void updateMonitorStatus(int work) {
		if (monitor != null) {
			monitor.worked(work);
		}
	}

	private void updateMonitorMessage(String message) {
		if (monitor != null) {
			monitor.subTask(message);
		}
	}

	private boolean isCancelled() {
		if (monitor != null)
			return monitor.isCanceled();
		return false;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

}