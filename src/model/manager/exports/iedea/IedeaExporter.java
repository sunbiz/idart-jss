package model.manager.exports.iedea;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.manager.PatientManager;
import model.manager.excel.conversion.exceptions.ReportException;
import model.nonPersistent.EntitySet;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;
import org.iedea.ARKEKapaExport;
import org.iedea.util.IedeaJaxbUtil;

public class IedeaExporter {

	static Logger log = Logger.getLogger(IedeaExporter.class);
	static final int PAGE_SIZE = 25;
	protected Session session;
	private IProgressMonitor monitor;

	public IedeaExporter() {
		super();
	}

	public void generate(String exportPath) throws ReportException {

		File file = new File(exportPath);
		if (!file.isDirectory()) {
			throw new ReportException("Please select a directory");
		}
		
		ARKEKapaExport arkExport = new ARKEKapaExport();
		IedeaExport export = new IedeaExport(arkExport);

		session = HibernateUtil.getNewSession();

		EntitySet entitySet = getPatientSet();
		if (monitor != null) {
			monitor.beginTask("Generating report", entitySet.getSize());
		}

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
					exportPage(pagedEntitySet, export);
				} catch (Exception e) {
					throw new ReportException("Error running data export.", e);
				} finally {
					log.debug("Completed data export page " + page + " of "
							+ pages);

					updateMonitorStatus(PAGE_SIZE);
					page++;

					pagedEntitySet = null;

					log.debug("Clearing hibernate session");
					session.clear();

					// clear out the excess objects
					System.gc();
					System.gc();
				}
			}

			IedeaJaxbUtil.instance().write(
					arkExport,
					new FileOutputStream(new File(file.getAbsolutePath(),
							"idart_tier-net_export_"
									+ new SimpleDateFormat("yyyy-MM-dd")
											.format(new Date())+ ".xml")));
		} catch (Exception e) {
			throw new ReportException(e);
		} finally {
			entitySet = null;
			session.close();
		}
	}

	private void exportPage(EntitySet pagedEntitySet, IedeaExport export) {
		for (Integer patientId : pagedEntitySet.getEntityIds()) {
			Patient patient = PatientManager.getPatient(session, patientId);
			try {
				export.writeDataExport(session, patient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate the patientSet according to this report's characteristics
	 * 
	 * @return patientSet to be used with report template
	 */
	private EntitySet getPatientSet() {
		@SuppressWarnings("unchecked")
		List<Integer> ids = session.createQuery("select id from Patient").list();
		EntitySet patientSet = new EntitySet();
		patientSet.setEntityIds(ids);

		return patientSet;
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

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

}