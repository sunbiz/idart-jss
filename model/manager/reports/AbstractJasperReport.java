package model.manager.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

import com.jasperassistant.designer.viewer.ViewerApp;

/**
 * Abstract class with functionality for viewing a JasperReports. Extend this
 * class for each report implementing the {@link #generateData()},
 * {@link #getParameterMap()}, {@link #getReportFileName()}
 * 
 * @author Simon Kelly
 * 
 */
public abstract class AbstractJasperReport implements iDARTReport {

	protected static Logger log = Logger.getLogger(PepfarReport.class);
	protected final Shell parent;
	protected final Session hSession;
	private JasperPrint jp;

	public AbstractJasperReport(Shell parent) {
		this.parent = parent;
		this.hSession = HibernateUtil.getNewSession();
	}

	public Session getHSession() {
		return hSession;
	}

	/**
	 * This method is called before the report is generated and can be used to
	 * generate CSV files etc.
	 * 
	 * @throws ReportException
	 */
	protected abstract void generateData() throws ReportException;

	/**
	 * @return the filename of the report (excluding the extension)
	 */
	protected abstract String getReportFileName();

	/**
	 * @return the parameter map to pass to the report
	 * @throws ReportException
	 */
	protected abstract Map<String, Object> getParameterMap()
			throws ReportException;

	protected Object getDataSource() throws ReportException {
		try {
			return JDBCUtil.currentSession();
		} catch (SQLException e) {
			throw new ReportException("Error getting data source", e);
		}
	}

	/**
	 * @return the path to where the iDART reports are kept
	 *         (<idartroot>/Reports/)
	 * 
	 * @throws ReportException
	 */
	protected String getReportPath() throws ReportException {
		File path = new File("Reports" + java.io.File.separator);
		try {
			return path.getCanonicalPath();
		} catch (IOException e) {
			throw new ReportException("Error getting report path", e);
		}
	}

	/**
	 * Give the input date this method returns a new date on the same day at
	 * 00:00:00
	 * 
	 * @param theDate
	 * @return
	 */
	protected static Date getBeginningOfDay(Date theDate) {
		return iDARTUtil.getBeginningOfDay(theDate);
	}

	/**
	 * Give the input date this method returns a new date on the same day at
	 * 23:59:59
	 * 
	 * @param theDate
	 * @return
	 */
	protected static Date getEndOfDay(Date theDate) {
		return iDARTUtil.getEndOfDay(theDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seemodel.manager.iDARTReport#fillReport(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void fillReport(IProgressMonitor monitor) throws ReportException {
		try {
			monitor.setTaskName("Getting report file");
			String reportFileName = getReportFileName();

			if (reportFileName == null || reportFileName.isEmpty())
				throw new ReportException("Unable to find report file.");

			FileInputStream fi = getJasperReportFromJRXMLorJASPER(reportFileName);
			monitor.worked(5);

			monitor.setTaskName("Generating report data");
			generateData();
			monitor.worked(45);

			Object dataSource = getDataSource();
			if (dataSource == null)
				throw new ReportException("Unable to get report datasource.");

			monitor.setTaskName("Getting report parameters");
			Map<String, Object> parameters = getParameterMap();
			monitor.worked(5);

			if (parameters == null)
				throw new ReportException("Null parameter map for report");

			monitor.setTaskName("Generating report");
			if (dataSource instanceof Connection) {
				Connection connection = (Connection) dataSource;
				jp = JasperFillManager.fillReport(fi, parameters, connection);
			} else if (dataSource instanceof JRDataSource) {
				JRDataSource jrDataSource = (JRDataSource) dataSource;
				jp = JasperFillManager.fillReport(fi, parameters, jrDataSource);
			}
			monitor.worked(45);
		} catch (JRException e) {
			throw new ReportException("Error filling report", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.manager.iDARTReport#viewReport()
	 */
	@Override
	public void viewReport() {
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				if (jp != null) {
					if (jp.getPages().size() > 0) {
						ViewerApp viewer = new ViewerApp();
						viewer.getReportViewer().setDocument(jp);
						viewer.open();
					} else {
						MessageBox mNoPages = new MessageBox(parent,
								SWT.ICON_ERROR | SWT.OK);
						mNoPages.setText("Report Has No Pages");
						mNoPages
								.setMessage("The report you are trying to generate does not contain any data. \n\nPlease check the input values you have entered (such as dates) for this report, and try again.");
						mNoPages.open();
					}
				}
			}
		};
		Display.getCurrent().asyncExec(runner);
	}

	/**
	 * Returns the Fileinputstream for file.jasper From file.jrxml if
	 * file.jasper does not exist
	 * 
	 * @param file
	 *            String
	 * @return FileInputStream
	 */
	protected FileInputStream getJasperReportFromJRXMLorJASPER(String file) {
		String path = "Reports" + java.io.File.separator + file;
		FileInputStream result = null;
		try {
			FileInputStream jasper = new FileInputStream(path + ".jasper");
			result = jasper;
		} catch (FileNotFoundException fe) {
			log.info(path + ".jasper not found, generating file from " + path
					+ ".jrxml");
			try {
				FileInputStream jrxml = new FileInputStream(path + ".jrxml");
				FileOutputStream jasperOut = new FileOutputStream(path
						+ ".jasper");
				JasperCompileManager.compileReportToStream(jrxml, jasperOut);
				result = new FileInputStream(path + ".jasper");
			} catch (FileNotFoundException e) {
				log.error("\nTime of crash " + new Date() + " " + path
						+ ".jrxml not found! Missing Report File");
			} catch (JRException jre) {
				log.error("\nTime of crash " + new Date()
						+ " JasperReport Exception");
				jre.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * Given a list of String[]'s this method will write them to a CSV file.
	 * Each String[] will be on a new line and each String in the array is
	 * separated by a comma.
	 * 
	 * @param fileName
	 * @param fileContents
	 * @param escapeAllStrings
	 *            if true wrap strings in inverted commas
	 * @return
	 */
	protected File createCSVFile(String fileName, List<String[]> fileContents,
			boolean escapeAllStrings) {
		File csvFile = null;
		try {
			// write to csv file
			csvFile = new File("Reports" + java.io.File.separator + fileName);
			FileWriter out = new FileWriter(csvFile);

			// print the row strings
			for (String[] theStringArr : fileContents) {

				for (int i = 0; i < theStringArr.length; i++) {
					if (theStringArr[i] != null) {
						if (i != 0) {
							out.write(",");
						}
						if (escapeAllStrings) {
							out.write("\"" + theStringArr[i] + "\"");
						} else {
							out.write(theStringArr[i]);
						}
					} else {
						out.write(",");
					}
				}
				out.write(",\n");
			}
			out.close();
		} catch (IOException e) {
			log.error("Error writing csv report file: " + e.getMessage() + ":"
					+ e.getStackTrace());
		}
		return csvFile;
	}
}