package model.manager.exports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import model.manager.excel.conversion.exceptions.ReportException;
import model.nonPersistent.EntitySet;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.celllife.idart.commonobjects.iDartProperties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;

public final class DataExporter {

	private int PAGE_SIZE = 100;
	private final Session session;
	private static Logger log = Logger.getLogger(DataExporter.class);
	private IProgressMonitor monitor;

	public DataExporter(Session session) {
		this.session = session;
	}

	public DataExporter(Session session, IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
		this.session = session;
	}

	public void setPageSize(int pageSize) {
		PAGE_SIZE = pageSize;
	}

	/**
	 * Generates a data export file given a data export (columns) and patient
	 * set (rows).
	 *
	 * @param sess
	 *            Hibernate Session
	 * @param dataExport
	 *            DataExportObject to export
	 * @param patientSet
	 *            set of patients to export data for or null if all patients
	 * @param separator
	 *            column separator character to use
	 * @param subProgressMonitor
	 * @return 
	 * @throws Exception
	 */
	public File generateExport(ReportObject dataExport,
			String outputFileName) throws ReportException {
		// Set up functions used in the report ( $!{fn:...} )
		DataExportFunctions functions = new DataExportFunctions();
		functions.setSeparator(',');
		return generateExport(dataExport, functions, outputFileName);
	}

	/**
	 * Generates a data export file given a data export (columns) and patient
	 * set (rows).
	 *
	 * @param sess
	 *            Hibernate Session
	 * @param dataExport
	 *            DataExportObject to export
	 * @param patientSet
	 *            set of patients to export data for or null if all patients
	 * @param separator
	 *            column separator character to use
	 * @param subProgressMonitor
	 * @throws Exception
	 */
	public void generateExport(ReportObject dataExport, char separator,
			String outputFileName) throws ReportException {
		// Set up functions used in the report ( $!{fn:...} )
		DataExportFunctions functions = new DataExportFunctions();
		functions.setSeparator(separator);
		generateExport(dataExport, functions, outputFileName);
	}

	/**
	 *
	 * @param sess
	 * @param dataExport
	 * @param functions
	 * @throws ReportException
	 *             if any errors occur.
	 */
	public File generateExport(ReportObject dataExport,
			DataExportFunctions functions, String outputFileName)
	throws ReportException {

		functions.setSession(session);
		// defining log file here to attempt to reduce memory consumption

		VelocityEngine velocityEngine = new VelocityEngine();

		try {
			velocityEngine.init();
		} catch (Exception e) {
			log.error("Error initializing Velocity engine", e);
		}

		// Check if file has .csv extension
		if (!outputFileName.endsWith(".csv")) {
			outputFileName += ".csv";
		}

		File exportFile = new File(outputFileName);
		PrintWriter report;
		try {
			report = new PrintWriter(exportFile);
		} catch (FileNotFoundException e1) {
			System.out.println(" TEST "  + e1.getMessage());
			throw new ReportException("Unable to open file for exporting data.");
		}
		EntitySet entitySet = generatePatientSet(dataExport);

		int page = 0;
		int total = entitySet.size();
		int pages = total / PAGE_SIZE;
		if (monitor != null)
			monitor.beginTask("Exporting data", total);
		try {
			while (!isCancelled()) {
				// Set up list of patients if one wasn't passed into this method
				EntitySet pagedEntitySet = entitySet.getPage(page
						* PAGE_SIZE, PAGE_SIZE);
				if (pagedEntitySet.size() == 0) {
					break;
				}

				log.debug("Starting data export page " + page + " of " + pages);
				updateMonitorMessage("Processed " + page * PAGE_SIZE
						+ " patients of " + total);

				VelocityContext velocityContext = new VelocityContext();
				// add the error handler
				EventCartridge ec = new EventCartridge();
				ec.addEventHandler(new VelocityExceptionHandler());
				velocityContext.attachEventCartridge(ec);

				functions.setAllPatients(false);

				// Set up velocity utils
				velocityContext.put("fn", functions);
				velocityContext.put("patientSet", pagedEntitySet);

				String template;
				if (page == 0) {
					template = dataExport.generateTemplate();
				} else {
					template = dataExport.generateDataTemplate();
				}

				if (log.isDebugEnabled()) {
					log.debug("Template: "
							+ template.substring(0,
									template.length() < 3500 ? template
											.length() : 3500) + "...");
				}

				try {
					velocityEngine.evaluate(velocityContext, report,
							DataExporter.class.getName(), template);
				} catch (Exception e) {
					log.error("Error evaluating data export "
							+ dataExport.getReportObjectId(), e);
					log.error("Template: "
							+ template.substring(0,
									template.length() < 3500 ? template
											.length() : 3500) + "...");
					report.print("\n\nError exporting data");
					throw new ReportException("Error running data export.", e);
				} finally {
					log.debug("Completed data export page " + page + " of "
							+ pages);

					updateMonitorStatus(PAGE_SIZE);
					page++;

					report.flush();
					velocityContext.remove("fn");
					velocityContext.remove("patientSet");
					velocityContext = null;

					// reset the ParserPool to something else now?
					// using this to get to RuntimeInstance.init();
					try {
						velocityEngine.init();
					} catch (Exception e) {
						// do nothing
					}

					pagedEntitySet = null;
					functions.clear();
					template = null;

					log.debug("Clearing hibernate session");
					session.clear();

					// clear out the excess objects
					System.gc();
					System.gc();
				}
			}
		} catch (ReportException e) {
			throw e;
		} finally {
			report.close();
			entitySet = null;
			dataExport = null;
			velocityEngine = null;
			session.close();
		}
		return exportFile;
	}

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

	/**
	 * Generate the patientSet according to this report's characteristics
	 *
	 * @return patientSet to be used with report template
	 */
	@SuppressWarnings("unchecked")
	private EntitySet generatePatientSet(ReportObject dataExport) {
		EntitySet patientSet = dataExport.getPatientSet();

		if (patientSet == null || patientSet.size() == 0) {
			List<Integer> ids = session.createQuery("select id from Patient")
			.list();
			patientSet = new EntitySet();
			patientSet.setEntityIds(ids);
			dataExport.setAllPatients(false);
		}

		return patientSet;
	}

	/**
	 * Returns the path and name of the generated file
	 *
	 * @param dataExport
	 * @return
	 */
	public File getGeneratedFile(ReportObject dataExport) {
		File dir = new File(iDartProperties.exportDir);
		dir.mkdirs();

		String filename = dataExport.getName().replace(" ", "_");

		File file = new File(dir, filename);

		return file;
	}

	/**
	 * Private class used for velocity error masking
	 */
	public static class VelocityExceptionHandler implements
	MethodExceptionEventHandler {

		/**
		 * When a user-supplied method throws an exception, the
		 * MethodExceptionEventHandler is invoked with the Class, method name
		 * and thrown Exception. The handler can either return a valid Object to
		 * be used as the return value of the method call, or throw the
		 * passed-in or new Exception, which will be wrapped and propogated to
		 * the user as a MethodInvocationException
		 *
		 * @see org.apache.velocity.app.event.MethodExceptionEventHandler#methodException(java.lang.Class,
		 *      java.lang.String, java.lang.Exception)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Object methodException(Class claz, String method, Exception e)
		throws Exception {

			log.debug("Claz: " + claz.getName() + " method: " + method, e);

			// if formatting a date (and probably getting an
			// "IllegalArguementException")
			if ("format".equals(method))
				return null;

			// keep the default behaviour
			throw e;
		}

	}

}
