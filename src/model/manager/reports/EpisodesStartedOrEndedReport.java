package model.manager.reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import model.manager.PrescriptionManager;
import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class EpisodesStartedOrEndedReport extends AbstractJasperReport {

	private final Clinic clinic;
	private final boolean isEpisodesStartedReport;
	private final String orderByField2;
	private final String orderByField1;
	private Date startDate;
	private final Date endDate;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
	private File csvFile;

	public EpisodesStartedOrEndedReport(Shell parent, Clinic clinic,
			Date theStartDate, Date theEndDate,
			boolean isEpisodesStartedReport, String orderByField1,
			String orderByField2) {
		super(parent);
		this.clinic = clinic;
		this.isEpisodesStartedReport = isEpisodesStartedReport;
		this.orderByField1 = orderByField1;
		this.orderByField2 = orderByField2;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {
		final List<String[]> theStringList = new ArrayList<String[]>();

		// print the header
		theStringList.add(0, new String[] { "patientId", "patientInfo",
				"regimen", "startDate", "startReason", "startNotes", "endDate",
				"endReason", "endNotes" });

		theStringList.addAll(getEpisodeStartedOrEndedReportData());

		csvFile = createCSVFile("episodeStartedOrEnded.csv", theStringList,
				true);
	}

	@SuppressWarnings("unchecked")
	private List<String[]> getEpisodeStartedOrEndedReportData() {

		List<String[]> theReturnList = new ArrayList<String[]>();

		List<Episode> episodes = new ArrayList<Episode>();
		if (!isEpisodesStartedReport) {
			
			Criteria crit = hSession.createCriteria(Episode.class).createAlias(
					"patient", "patient").createAlias("clinic", "clinic").add(
					Restrictions.eq("clinic.id", clinic.getId())).add(
					(Restrictions.between("stopDate", iDARTUtil
							.getBeginningOfDay(startDate), iDARTUtil
							.getEndOfDay(endDate)))).addOrder(
					Order.asc(orderByField1)).addOrder(
					Order.asc(orderByField2));
			
			
			episodes = crit.list();
			
			
			
		} else {

			Criteria crit = hSession.createCriteria(Episode.class).createAlias(
					"patient", "patient").createAlias("clinic", "clinic").add(
					Restrictions.eq("clinic.id", clinic.getId())).add(
					(Restrictions.between("startDate", iDARTUtil
							.getBeginningOfDay(startDate), iDARTUtil
							.getEndOfDay(endDate)))).addOrder(
					Order.asc(orderByField1)).addOrder(
					Order.asc(orderByField2));

			episodes = crit.list();

		}

		for (Episode ep : episodes) {
			String[] episodeArray = new String[9];

			episodeArray[0] = ep.getPatient().getPatientId();
			episodeArray[1] = ep.getPatient().getLastname() + ", "
					+ ep.getPatient().getFirstNames() + " ("
					+ ep.getPatient().getSex() + ")";
			Prescription pre = null;
			if (isEpisodesStartedReport) {

				pre = PrescriptionManager.getFirstPrescriptionForEpisode(
						hSession, ep);

				episodeArray[2] = pre != null ? PrescriptionManager
						.getShortPrescriptionContentsString(pre) : "N/A";

			} else {

				pre = PrescriptionManager.getLastPrescriptionForEpisode(
						hSession, ep);
				episodeArray[2] = pre != null ? PrescriptionManager
						.getShortPrescriptionContentsString(pre) : "N/A";
			}
			episodeArray[3] = sdf.format(ep.getStartDate());
			episodeArray[4] = ep.getStartReason();
			episodeArray[5] = ep.getStartNotes();
			episodeArray[6] = ep.getStopDate() != null ? sdf.format(ep
					.getStopDate()) : "";
			episodeArray[7] = ep.getStopReason();
			episodeArray[8] = ep.getStopNotes();

			theReturnList.add(episodeArray);
		}

		return theReturnList;
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", getReportPath());
		map.put("clinicName", clinic.getClinicName());
		map.put("isStarted", new Boolean(isEpisodesStartedReport));
		map.put("orderBy1", orderByField1);
		map.put("orderBy2", orderByField2);
		map.put("startDate", getBeginningOfDay(startDate));
		map.put("endDate", getEndOfDay(endDate));
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected Object getDataSource() throws ReportException {
		try {
			JRCsvDataSource jcvs = new JRCsvDataSource(new File("Reports"
					+ java.io.File.separator + csvFile.getName()));
			jcvs.setUseFirstRowAsHeader(true);
			return jcvs;
		} catch (Exception e) {
			throw new ReportException("Error getting data source", e);
		}
	}

	@Override
	protected String getReportFileName() {
		return "episodesStartedOrEndedReport";
	}

	/**
	 * This method checks which regimen the patient is on by looking at the
	 * prescribed drugs.
	 * 
	 * @param session
	 * @param prescription
	 * @return
	 * @throws HibernateException
	 */
	public String getRegimenForPrescription(Session session,
			Prescription prescription) throws HibernateException {

		if (prescription == null)
			return null;

		Map<Regimen, Set<Integer>> regimenIdMap = PrescriptionManager
				.getRegimenIdMap(session);

		Set<Integer> prescriptionDrugSet = new HashSet<Integer>();

		for (PrescribedDrugs pre : prescription.getPrescribedDrugs()) {
			prescriptionDrugSet.add(pre.getDrug().getId());
		}

		for (Entry<Regimen, Set<Integer>> entry : regimenIdMap.entrySet()) {

			if (prescriptionDrugSet.containsAll(entry.getValue())) {
				return entry.getKey().getDrugGroup();
			}
		}

		return null;
	}
}
