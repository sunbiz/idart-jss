package model.manager.exports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import model.manager.PatientSetManager;
import model.manager.exports.columns.AppointmentDetailsEnum;
import model.manager.exports.columns.DrugDetailsEnum;
import model.manager.exports.columns.EpisodeDetailsEnum;
import model.manager.exports.columns.PackageDetailsEnum;
import model.manager.exports.columns.PrescriptionDetailsEnum;
import model.nonPersistent.EntitySet;
import model.nonPersistent.ExportDrugInfo;
import model.nonPersistent.ExportPackageInfo;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.Session;

public class DataExportFunctions {

	private enum OrderEnum {
		NEWEST, OLDEST;
	}

	public static final Logger log = Logger.getRootLogger();

	private Date exportStartDate;
	private Date exportEndDate;
	private Integer patientId;
	private boolean isAllPatients;
	private EntitySet entitySet;
	private Session sess;
	private Integer patientCounter = 0; // used for garbage collection (Clean up
	// every x patients)
	private char separator;
	protected DateFormat dateFormatLong = null;
	protected DateFormat dateFormatShort = null;
	protected DateFormat dateFormatYmd = null;
	protected Map<String, DateFormat> formats = new HashMap<String, DateFormat>();

	// Map<tablename+columnname, Map<patientId, columnvalue>>
	protected Map<String, Map<Integer, Object>> patientAttributeMap;
	// Map<patientid, <packageid, exportedPackageInfo>>
	protected Map<Integer, Map<Integer, ExportPackageInfo>> exportPackageInfos;
	// Map<PatientID, List<List<Episode detail>>>
	private Map<Integer, List<List<Object>>> patientIdEpisodeMap;
	// Map<PatientID, List<List<Prescription detail>>>
	private Map<Integer, List<List<Object>>> patientIdPrescriptionMap;
	// Map<PatientID, List<List<Appointment detail>>>
	private Map<Integer, List<List<Object>>> patientIdAppointmentMap;
	// Map<PatientID, isPregnantAnDate>
	private List<Integer> patientIdPregnantAtDate;
	// Map<PatientID, Date>
	protected Map<Integer, Date> expectedRunoutDates;

	private static final char SEPERATOR_REPLACEMENT = ';';

	private Integer scriptId;
	private Integer packageId;

	// Map<PatientID, Map<ScriptID, Map<ScriptField, Value>>>
	private Map<Integer, Map<Integer, Map<String, Object>>> patientScriptMap;

	/**
	 * @return the expectedRunoutDates
	 */
	public Map<Integer, Date> getExpectedRunoutDates() {
		return expectedRunoutDates;
	}

	/**
	 * @param exportPackageInfos
	 *            the exportPackageInfos to set
	 */
	public void setExportPackageInfos(Map<Integer, Map<Integer, ExportPackageInfo>> exportPackageInfos) {
		this.exportPackageInfos = exportPackageInfos;
	}

	public DataExportFunctions() {
		dateFormatLong = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, iDartProperties.currentLocale);
		dateFormatShort = new SimpleDateFormat("yy-MM-dd",
				iDartProperties.currentLocale);
		dateFormatYmd = new SimpleDateFormat("yyyy-MM-dd",
				iDartProperties.currentLocale);
	}

	public void clear() {
		if (patientAttributeMap != null) {
			for (Map<Integer, Object> map : patientAttributeMap.values()) {
				map.clear();
			}
		}

		if (patientAttributeMap != null) {
			patientAttributeMap.clear();
			patientAttributeMap = null;
		}

		if (patientScriptMap != null) {
			patientScriptMap.clear();
			patientScriptMap = null;
		}

		if (exportPackageInfos != null) {
			exportPackageInfos.clear();
			exportPackageInfos = null;
		}

		if (patientIdEpisodeMap != null) {
			patientIdEpisodeMap.clear();
			patientIdEpisodeMap = null;
		}

		if (patientIdPrescriptionMap != null) {
			patientIdPrescriptionMap.clear();
			patientIdPrescriptionMap = null;
		}

		if (patientIdAppointmentMap != null) {
			patientIdAppointmentMap.clear();
			patientIdAppointmentMap = null;
		}

		if (patientIdPregnantAtDate != null) {
			patientIdPregnantAtDate.clear();
			patientIdPregnantAtDate = null;
		}

		if (expectedRunoutDates != null) {
			expectedRunoutDates.clear();
			expectedRunoutDates = null;
		}

		patientCounter = 0;
		entitySet = null;
		patientId = null;
		scriptId = null;
		packageId = null;
	}

	public void setPatientId(Integer patientId) {
		// remove last patient from maps to allow for garbage collection
		if (this.patientId != null) {
			if (patientAttributeMap != null) {
				for (Map<Integer, Object> map : patientAttributeMap.values()) {
					map.remove(this.patientId);
				}
			}

			if (patientScriptMap != null) {
				patientScriptMap.remove(this.patientId);
			}
			if (exportPackageInfos != null) {
				exportPackageInfos.remove(this.patientId);
			}
			if (patientIdEpisodeMap != null) {
				patientIdEpisodeMap.remove(this.patientId);
			}
			if (patientIdPrescriptionMap != null) {
				patientIdPrescriptionMap.remove(this.patientId);
			}
			if (patientIdAppointmentMap != null) {
				patientIdAppointmentMap.remove(this.patientId);
			}
			if (patientIdPregnantAtDate != null) {
				patientIdPregnantAtDate.remove(this.patientId);
			}
			if (expectedRunoutDates != null) {
				expectedRunoutDates.remove(this.patientId);
			}
		}

		// reclaim some memory
		garbageCollect();

		this.patientId = patientId;
	}

	public void setScriptId(Integer scriptId) {
		this.scriptId = scriptId;
	}

	public Set<Integer> getScriptIds() {
		if (patientScriptMap == null) {
			patientScriptMap = PatientSetManager.getPrescriptionIds(sess,
					getPatientSetIfNotAllPatients());
		}

		Map<Integer, Map<String, Object>> map = patientScriptMap.get(patientId);
		if (map != null)
			return map.keySet();
		else
			return null;
	}

	public Object getScriptField(String fieldName) {
		if (patientScriptMap == null) {
			patientScriptMap = PatientSetManager.getPrescriptionIds(sess,
					getPatientSetIfNotAllPatients());
		}

		return patientScriptMap.get(patientId).get(scriptId).get(fieldName);
	}

	public Object getPatientAttribute(String attributeName) {

		String key = "attribte." + attributeName;
		if (patientAttributeMap == null) {
			patientAttributeMap = new HashMap<String, Map<Integer, Object>>();
		}

		Map<Integer, Object> patientIdAttrMap;
		if (patientAttributeMap.containsKey(key)) {
			patientIdAttrMap = patientAttributeMap.get(key);
		} else {
			patientIdAttrMap = PatientSetManager.getPatientAttributes(sess,
					getPatientSetIfNotAllPatients(), attributeName);

			patientAttributeMap.put(key, patientIdAttrMap);
		}

		Object object = patientIdAttrMap.get(patientId);
		return object;
	}
	
	public Object getPatientIdentifier(String identifierName) {

		String key = "identifier." + identifierName;
		if (patientAttributeMap == null) {
			patientAttributeMap = new HashMap<String, Map<Integer, Object>>();
		}

		Map<Integer, Object> patientIdAttrMap;
		if (patientAttributeMap.containsKey(key)) {
			patientIdAttrMap = patientAttributeMap.get(key);
		} else {
			patientIdAttrMap = PatientSetManager.getPatientIdentifiers(sess,
					getPatientSetIfNotAllPatients(), identifierName);

			patientAttributeMap.put(key, patientIdAttrMap);
		}

		Object object = patientIdAttrMap.get(patientId);
		return object;
	}

	/**
	 * Retrieves properties on the patient like patient.patientName.familyName
	 * If returnAll is set, returns an array of every matching property for the
	 * patient instead of just the preferred one
	 * 
	 * @param className
	 * @param property
	 * @param returnAll
	 * @return
	 */
	public Object getPatientField(String className, String property,
			boolean returnAll) {

		String key = className + "." + property;

		if (returnAll) {
			key += "--all";
		}

		if (patientAttributeMap == null) {
			patientAttributeMap = new HashMap<String, Map<Integer, Object>>();
		}

		Map<Integer, Object> patientIdAttrMap;
		if (patientAttributeMap.containsKey(key)) {
			patientIdAttrMap = patientAttributeMap.get(key);
		} else {
			// log.debug("getting patient attrs: " + key);
			patientIdAttrMap = PatientSetManager.getPatientFields(sess,
					getPatientSetIfNotAllPatients(), className, property,
					returnAll);

			patientAttributeMap.put(key, patientIdAttrMap);
		}

		return patientIdAttrMap.get(patientId);
	}

	public int getPaitentAgeAt(Date date) {
		if (date == null)
			date = new Date();

		Date dob = (Date) getPatientField("Patient", "dateOfBirth");
		return iDARTUtil.getAgeAt(dob, date);
	}
	
	public int getPaitentAgeAtEndDate() {
		if (exportEndDate == null)
			exportEndDate = new Date();
		return getPaitentAgeAt(exportEndDate);
	}

	/**
	 * Returns a list of the episode details as specified for the oldest episode
	 * of the current patient.
	 * 
	 * @see #getEpisodeDetails(List)
	 * @param details
	 *            The list of details required for each episode.
	 * @return
	 */
	public List<Object> getOldestEpisodeDetails(List<String> details) {
		List<List<Object>> episodes = getNEpisodeDetails(1, details,
				OrderEnum.OLDEST, null, null);
		return episodes.get(0);
	}
	
	/**
	 * Returns a list of the episode details as specified for the oldest episode
	 * of the current patient.
	 * 
	 * @see #getEpisodeDetails(List)
	 * @param details
	 *            The list of details required for each episode.
	 * @return
	 */
	public List<Object> getOldestEpisodeDetailsBetweenDates(List<String> details, Date startDate, Date endDate) {
		List<List<Object>> episodes = getNEpisodeDetails(1, details,
				OrderEnum.OLDEST, startDate, endDate);
		return episodes.get(0);
	}

	/**
	 * Returns a list of the prescription details as specified for the oldest
	 * prescription of the current patient.
	 * 
	 * @see #getPrescriptionDetails(List)
	 * @param details
	 *            The list of details required for each prescription.
	 * @return
	 */
	public List<Object> getOldestPrescriptionDetails(List<String> details) {
		List<List<Object>> prescription = getNPrescriptionDetails(1, details,
				OrderEnum.OLDEST);
		return prescription.get(0);
	}

	/**
	 * Returns a list of the appointment details as specified for the oldest
	 * appointment of the current patient.
	 * 
	 * @see #getAppointmentDetails(List)
	 * @param details
	 *            The list of details required for each appointment.
	 * @return
	 */
	public List<Object> getOldestAppointmentDetails(List<String> details) {
		List<List<Object>> appointment = getNAppointmentDetails(1, details,
				OrderEnum.OLDEST);
		return appointment.get(0);
	}

	/**
	 * Returns a list of the episode details as specified for the newest episode
	 * of the current patient.
	 * 
	 * @see #getEpisodeDetails(List)
	 * @param details
	 *            The list of details required for each episode.
	 * @return
	 */
	public List<Object> getNewestEpisodeDetails(List<String> details) {
		List<List<Object>> episodes = getNEpisodeDetails(1, details,
				OrderEnum.NEWEST, null, null);
		return episodes.get(0);
	}

	/**
	 * Returns a list of the prescription details as specified for the newest
	 * prescription of the current patient.
	 * 
	 * @see #getPrescriptionDetails(List)
	 * @param details
	 *            The list of details required for each prescription.
	 * @return
	 */
	public List<Object> getNewestPrescriptionDetails(List<String> details) {
		List<List<Object>> prescription = getNPrescriptionDetails(1, details,
				OrderEnum.NEWEST);
		return prescription.get(0);
	}

	/**
	 * Returns a list of the appointment details as specified for the newest
	 * appointment of the current patient.
	 * 
	 * @see #getAppointmentDetails(List)
	 * @param details
	 *            The list of details required for each appointment.
	 * @return
	 */
	public List<Object> getNewestAppointmentDetails(List<String> details) {
		List<List<Object>> appointment = getNAppointmentDetails(1, details,
				OrderEnum.NEWEST);
		return appointment.get(0);
	}

	/**
	 * Returns a list of the episode details as specified for the newest N
	 * episodes of the current patient.
	 * 
	 * @see #getEpisodeDetails(List)
	 * @param n
	 *            The number of episodes to fetch.
	 * @param details
	 *            The list of details required for each episode.
	 * @return
	 */
	public List<List<Object>> getNewestNEpisodeDetails(Integer n,
			List<String> details) {
		return getNEpisodeDetails(n, details, OrderEnum.NEWEST, null, null);
	}

	/**
	 * Returns a list of the prescription details as specified for the newest N
	 * prescription of the current patient.
	 * 
	 * @see #getPrescriptionDetails(List)
	 * @param n
	 *            The number of prescription to fetch.
	 * @param details
	 *            The list of details required for each prescription.
	 * @return
	 */
	public List<List<Object>> getNewestNPrescriptionDetails(Integer n,
			List<String> details) {
		return getNPrescriptionDetails(n, details, OrderEnum.NEWEST);
	}

	/**
	 * Returns a list of the appointment details as specified for the newest N
	 * appointment of the current patient.
	 * 
	 * @see #getAppointmentDetails(List)
	 * @param n
	 *            The number of appointment to fetch.
	 * @param details
	 *            The list of details required for each appointment.
	 * @return
	 */
	public List<List<Object>> getNewestNAppointmentDetails(Integer n,
			List<String> details) {
		return getNAppointmentDetails(n, details, OrderEnum.NEWEST);
	}

	/**
	 * Returns a list of the episode details as specified for the oldest N
	 * episodes of the current patient.
	 * 
	 * @see #getEpisodeDetails(List) *
	 * @param n
	 *            The number of episodes to fetch.
	 * @param details
	 *            The list of details required for each episode.
	 * @return
	 */
	public List<List<Object>> getOldestNEpisodeDetails(Integer n,
			List<String> details) {
		return getNEpisodeDetails(n, details, OrderEnum.OLDEST, null, null);
	}

	/**
	 * Returns a list of the prescription details as specified for the oldest N
	 * prescription of the current patient.
	 * 
	 * @see #getPrescriptionDetails(List) *
	 * @param n
	 *            The number of prescription to fetch.
	 * @param details
	 *            The list of details required for each prescription.
	 * @return
	 */
	public List<List<Object>> getOldestNPrescriptionDetails(Integer n,
			List<String> details) {
		return getNPrescriptionDetails(n, details, OrderEnum.OLDEST);
	}

	/**
	 * Returns a list of the appointment details as specified for the oldest N
	 * appointment of the current patient.
	 * 
	 * @see #getAppointmentDetails(List) *
	 * @param n
	 *            The number of appointment to fetch.
	 * @param details
	 *            The list of details required for each appointment.
	 * @return
	 */
	public List<List<Object>> getOldestNAppointmentDetails(Integer n,
			List<String> details) {
		return getNAppointmentDetails(n, details, OrderEnum.OLDEST);
	}

	public List<List<Object>> getFirstAndLastPrescriptionDetails(
			List<String> details) {

		List<List<Object>> result = new ArrayList<List<Object>>();

		List<List<Object>> completeList = getNPrescriptionDetails(-1, details,
				OrderEnum.NEWEST);

		if (completeList.size() > 0) {
			result.add(completeList.get(0));

			if (completeList.size() > 1) {
				result.add(completeList.get(completeList.size() - 1));
			}
		}

		return result;
	}

	/**
	 * Returns a list of the episode details as specified for the first or last
	 * N episodes of the current patient. If the list of episodes is less than N
	 * then it is expanded with empty rows unitl list.size == N;
	 * 
	 * @see #getEpisodeDetails(List)
	 * @param n
	 *            The number of episodes for which to return details
	 * @param details
	 *            The list of details required for each episode.
	 * @param startDate 
	 * @param endDate 
	 * @return a list containing a list of the details for each episode of the
	 *         current patient
	 */
	public List<List<Object>> getNEpisodeDetails(Integer n,
			List<String> details, OrderEnum mod, Date startDate, Date endDate) {
		if (details == null) {
			details = new Vector<String>();
		}

		List<List<Object>> returnList = getEpisodeDetails(details, startDate, endDate);

		if (returnList == null) {
			returnList = new Vector<List<Object>>();
		}

		if (n.equals(-1))
			return returnList;

		// bring the list size up to 'n'
		List<Object> blankRow = new Vector<Object>();
		for (@SuppressWarnings("unused")
		String detail : details) {
			blankRow.add("");
		}
		while (returnList.size() < n) {
			returnList.add(blankRow);
		}

		if (mod.equals(OrderEnum.NEWEST))
			return returnList.subList(0, n);
		else if (mod.equals(OrderEnum.OLDEST)) {
			int size = returnList.size();
			return returnList.subList(size - n, size);
		} else
			return returnList.subList(0, 0);

	}

	/**
	 * Returns a list of the prescription details as specified for the first or
	 * last N prescription of the current patient. If the list of prescription
	 * is less than N then it is expanded with empty rows unitl list.size == N;
	 * 
	 * @see #getPrescriptionDetails(List)
	 * @param n
	 *            The number of prescription for which to return details
	 * @param details
	 *            The list of details required for each prescription.
	 * @return a list containing a list of the details for each prescription of
	 *         the current patient
	 */
	public List<List<Object>> getNPrescriptionDetails(Integer n,
			List<String> details, OrderEnum mod) {
		if (details == null) {
			details = new Vector<String>();
		}

		List<List<Object>> returnList = getPrescriptionDetails(details);

		if (returnList == null) {
			returnList = new Vector<List<Object>>();
		}

		if (n.equals(-1))
			return returnList;

		// bring the list size up to 'n'
		List<Object> blankRow = new Vector<Object>();
		for (@SuppressWarnings("unused")
		String detail : details) {
			blankRow.add("");
		}
		while (returnList.size() < n) {
			returnList.add(blankRow);
		}

		if (mod.equals(OrderEnum.NEWEST))
			return returnList.subList(0, n);
		else if (mod.equals(OrderEnum.OLDEST)) {
			int size = returnList.size();
			return returnList.subList(size - n, size);
		} else
			return returnList.subList(0, 0);

	}

	/**
	 * Returns a list of the appointment details as specified for the first or
	 * last N appointment of the current patient. If the list of appointment is
	 * less than N then it is expanded with empty rows unitl list.size == N;
	 * 
	 * @see #getAppointmentDetails(List)
	 * @param n
	 *            The number of appointment for which to return details
	 * @param details
	 *            The list of details required for each appointment.
	 * @return a list containing a list of the details for each appointment of
	 *         the current patient
	 */
	public List<List<Object>> getNAppointmentDetails(Integer n,
			List<String> details, OrderEnum mod) {
		if (details == null) {
			details = new Vector<String>();
		}

		List<List<Object>> returnList = getAppointmentDetails(details);

		if (returnList == null) {
			returnList = new Vector<List<Object>>();
		}

		if (n.equals(-1))
			return returnList;

		// bring the list size up to 'n'
		List<Object> blankRow = new Vector<Object>();
		for (@SuppressWarnings("unused")
		String detail : details) {
			blankRow.add("");
		}
		while (returnList.size() < n) {
			returnList.add(blankRow);
		}

		if (mod.equals(OrderEnum.NEWEST))
			return returnList.subList(0, n);
		else if (mod.equals(OrderEnum.OLDEST)) {
			int size = returnList.size();
			return returnList.subList(size - n, size);
		} else
			return returnList.subList(0, 0);

	}

	/**
	 * Returns a list of the episode details as specified for all the episodes
	 * of the current patient.
	 * 
	 * Available details are values of the {@link EpisodeDetailsEnum} and can be
	 * supplied in any order.
	 * 
	 * @param details
	 *            The list of details required for each episode.
	 * @return a list of the details for each episode of the current patient or
	 *         null if none exist.
	 */
	public List<List<Object>> getEpisodeDetails(List<String> details, Date startDate, Date endDate) {
		if (details == null) {
			details = new Vector<String>();
		}

		if (patientIdEpisodeMap == null) {
			// log.debug("getting obs list for concept: " + c + " and attr: " +
			// attr);
			patientIdEpisodeMap = PatientSetManager.getEpisodeDetails(sess,
					getPatientSetIfNotAllPatients(), details, startDate, endDate);
		}
		return patientIdEpisodeMap.get(patientId);
	}

	/**
	 * Returns a list of the prescription details as specified for all the
	 * prescriptions of the current patient.
	 * 
	 * Available details are values of the {@link PrescriptionDetailsEnum} and
	 * can be supplied in any order.
	 * 
	 * @param details
	 *            The list of details required for each prescription.
	 * @return a list of the details for each prescription of the current
	 *         patient or null if none exist.
	 */
	public List<List<Object>> getPrescriptionDetails(List<String> details) {
		if (details == null) {
			details = new Vector<String>();
		}

		if (patientIdPrescriptionMap == null) {
			patientIdPrescriptionMap = PatientSetManager
					.getPrescriptionDetails(sess,
							getPatientSetIfNotAllPatients(), details);
		}

		return patientIdPrescriptionMap.get(patientId);
	}

	/**
	 * Returns a list of the appointment details as specified for all the
	 * appointment of the current patient.
	 * 
	 * Available details are values of the {@link AppointmentDetailsEnum} and
	 * can be supplied in any order.
	 * 
	 * @param details
	 *            The list of details required for each appointment.
	 * @return a list of the details for each appointment of the current patient
	 *         or null if none exist.
	 */
	public List<List<Object>> getAppointmentDetails(List<String> details) {
		if (details == null) {
			details = new Vector<String>();
		}

		if (patientIdAppointmentMap == null) {
			// log.debug("getting obs list for concept: " + c + " and attr: " +
			// attr);
			patientIdAppointmentMap = PatientSetManager.getAppointmentDetails(
					sess, getPatientSetIfNotAllPatients(), details);
		}
		return patientIdAppointmentMap.get(patientId);
	}

	/**
	 * Returns a list of the pregnancy details as specified for all the
	 * pregnancies of the current patient.
	 * 
	 * Available details are values of the {@link PregnancyDetailsEnum} and can
	 * be supplied in any order.
	 * 
	 * @param details
	 *            The list of details required for each pregnancy.
	 * @return a list of the details for each pregnancy of the current patient
	 *         or null if none exist.
	 */
	public boolean isPregnantAtDate(Date date) {
		if (date == null) {
			date = new Date();
		}

		if (patientIdPregnantAtDate == null) {
			patientIdPregnantAtDate = PatientSetManager.getPregnantAtDate(sess,
					getPatientSetIfNotAllPatients(), date);
		}
		return patientIdPregnantAtDate.contains(patientId);
	}
	
	public boolean isPregnantAtEndDate() {
		if (exportEndDate == null)
			exportEndDate = new Date();
		return isPregnantAtDate(exportEndDate);
	}

	/**
	 * <table>
	 * <tr>
	 * <td align=justify width = 420>This method calculates the date which a
	 * specific drug will run out.<br>
	 * This method does not return any values. <br>
	 * Should you need to obtain a "run out" date, please use
	 * getExpectedRunoutDates(), which returns a list of run out dates, each
	 * referenced by a Patient Id, in a hash map.</td>
	 * </tr>
	 * <tr>
	 * <td align=center>Run out date =</td>
	 * </tr>
	 * <tr>
	 * <td align=center>(QTY Disp + Accumulated Drugs)<br>
	 * -------------------------</td>
	 * </tr>
	 * <tr>
	 * <td align=center>(times p/day * amount p/time)</td>
	 * </tr>
	 * </table>
	 * 
	 * @see getExpectedRunoutDates()
	 * 
	 * 
	 */
	protected void cacheCurrentExpectedRunoutDate() {
		initExportPackageInfos();

		expectedRunoutDates = new HashMap<Integer, Date>();

		Collection<Map<Integer, ExportPackageInfo>> packageInfos = exportPackageInfos.values();

		for (Map<Integer, ExportPackageInfo> epiList : packageInfos) {
			ExportPackageInfo epi = getPackageInfoAtIndex(epiList, 0);
			int patientID = epi.getPatientID();
			Date expectedRunoutDate = epi.getExpectedRunoutDate();
			if (expectedRunoutDate != null) {
				expectedRunoutDates.put(patientID, expectedRunoutDate);
			}
		}
	}
	
	private void initExportPackageInfos() {
		if (exportPackageInfos == null) {
			exportPackageInfos = PatientSetManager.getPackageDetail(sess,
					getPatientSetIfNotAllPatients(), 0, exportStartDate,
					exportEndDate);
		}
	}

	private ExportPackageInfo getPackageInfoAtIndex(
			Map<Integer, ExportPackageInfo> epiList, int index) {
		List<ExportPackageInfo> values = new ArrayList<ExportPackageInfo>(epiList.values());
		Collections.sort(values, new Comparator<ExportPackageInfo>() {
			@Override
			public int compare(ExportPackageInfo o1, ExportPackageInfo o2) {
				return o2.getDateCollected().compareTo(o1.getDateCollected());
			}
		});
		return values.get(index);
	}

	/**
	 * Retrieves properties on the patient like patient.firstNames
	 * 
	 * @param className
	 * @param property
	 * @return
	 */
	public Object getPatientField(String className, String property) {
		return getPatientField(className, property, false);
	}

	/**
	 * @return Returns the patientSet.
	 */
	public EntitySet getPatientSet() {
		return entitySet;
	}

	/**
	 * Returns the patient set only if it is a subset of all patients. Returns
	 * null other wise
	 * 
	 * @return PatientSet object with patients or null if it isn't needed
	 */
	public EntitySet getPatientSetIfNotAllPatients() {
		if (isAllPatients)
			return null;
		return getPatientSet();
	}

	/**
	 * Call the system garbage collector. This method only calls every 500
	 * patients
	 */
	protected void garbageCollect() {
		if (patientCounter++ % 500 == 0) {
			System.gc();
			System.gc();
		}
	}

	public boolean isAllPatients() {
		return isAllPatients;
	}

	public void setAllPatients(boolean isAllPatients) {
		this.isAllPatients = isAllPatients;
	}

	public void setPatientSet(EntitySet patientSet) {
		this.entitySet = patientSet;
	}

	/**
	 * Retrieves properties on the package. See {@link PackageDetailsEnum} for
	 * list of properties.
	 * 
	 * @param property
	 * @return
	 */
	public Object getMostRecentPackageDetail(String property) {
		initExportPackageInfos();

		Map<Integer, ExportPackageInfo> epiList = exportPackageInfos.get(patientId);
		if (epiList != null) {
			ExportPackageInfo packageAtIndex = getPackageInfoAtIndex(epiList, 0);
			if (packageAtIndex != null)
				return packageAtIndex.getFieldValue(PackageDetailsEnum.valueOf(property));
		}
		
		return null;
	}

	/**
	 * Returns the package detail for the current package. See
	 * {@link PackageDetailsEnum} for list of properties.
	 * 
	 * Use {@link DataExportFunctions#setPackageId(Integer)} to set the
	 * packageId.
	 * 
	 * @param property
	 * @return
	 */
	public Object getPackageDetailForCurrentPackage(String property) {
		initExportPackageInfos();

		Map<Integer, ExportPackageInfo> map = exportPackageInfos.get(patientId);
		ExportPackageInfo exportPackageInfo = map.get(packageId);
		if (exportPackageInfo != null) {
			PackageDetailsEnum enumVal = PackageDetailsEnum.valueOf(property);
			return exportPackageInfo.getFieldValue(enumVal);
		}
		
		return null;
	}

	/**
	 * Returns the details of the arv contents of the package
	 * 
	 * @return
	 */
	public Object getPackageContentSummary() {
		initExportPackageInfos();

		ExportPackageInfo exportPackageInfo = exportPackageInfos.get(patientId)
		.get(packageId);
		
		if (exportPackageInfo != null) {
			StringBuffer drugListString = new StringBuffer();
			Iterator<ExportDrugInfo> drugIt = exportPackageInfo.getListOfExportDrugInfo().iterator();
			while (drugIt.hasNext()) {
				ExportDrugInfo theDrug = drugIt.next();
				if (theDrug.isARV()) {
					drugListString.append(theDrug.getChemicalCompoundName());
					drugListString.append(" + ");
				}
			}
			if(drugListString.length() > 2) {
				return drugListString.substring(0, drugListString.length() - 3);
			}
		}
		
		return null;
	}
	/**
	 * Return a property of a the export drug for the current package. See
	 * {@link DrugDetailsEnum} for available properties;
	 * 
	 * @param property
	 *            one of the values of {@link DrugDetailsEnum}
	 * @param drugId
	 *            the database id of the drug
	 * @return
	 */
	public Object getExportDrugDetailCurrentPackage(String property, int drugId) {
		initExportPackageInfos();

		ExportPackageInfo exportPackageInfo = exportPackageInfos.get(patientId)
				.get(packageId);
		StringBuffer output = new StringBuffer();
		if (exportPackageInfo != null) {
			List<ExportDrugInfo> drugInfos = exportPackageInfo.getDrugsWithId(drugId);
			int size = drugInfos.size();
			for (int i = 0; i < size; i++) {
				output.append(drugInfos.get(i).getField(DrugDetailsEnum.valueOf(property)));
				if (i < size - 1)
					output.append(",");
			}
		}
		return output.toString();
	}

	public Date getExpectedRunoutDate() {
		// check if the run out dates for all the patients have already been
		// calculated.
		// If not, call calculateExpectedRunoutDate() which calculates the
		// dates for all the patients
		// If yes, then look up the current patients expected run out date
		if (expectedRunoutDates == null || expectedRunoutDates.size() == 0) {
			cacheCurrentExpectedRunoutDate();
		}

		return expectedRunoutDates.get(patientId);
	}
	
	public void setSession(Session sess) {
		this.sess = sess;
	}

	/**
	 * @return Returns the separator.
	 */
	public char getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            The separator to set.
	 */
	public void setSeparator(char separator) {
		this.separator = separator;
	}

	/**
	 * Format the given date according to the type ('short', 'long', 'ymd')
	 * 
	 * @param type
	 * @param d
	 * @return
	 */
	public String formatDate(String type, Date d) {
		if (d == null)
			return "";

		if (type == null || type.length() == 0) {
			type = "ymd";
		}

		if ("long".equals(type))
			return dateFormatLong.format(d);
		else if ("ymd".equals(type))
			return dateFormatYmd.format(d);
		else if ("short".equals(type))
			return dateFormatShort.format(d);
		else {
			if (formats.containsKey(type))
				return formats.get(type).format(d);
			else {
				DateFormat df = new SimpleDateFormat(type,
						iDartProperties.currentLocale);
				formats.put(type, df);
				return df.format(d);
			}
		}
	}

	public String getValueAsString(Object o) {
		if (o == null)
			return "";

		else if (o instanceof Date)
			return formatDate(null, (Date) o);
		else
			return o.toString();
	}

	/**
	 * replaceSubstr, replaces all instances of oldStr found in str with newStr.
	 * this method exists already in the String class, but is recreated for
	 * convenience.
	 * 
	 * @param str
	 * @param oldStr
	 * @param newStr
	 * @return str.replaceAll(oldStr, newStr);
	 */
	public String replaceSeperator(String str) {
		if (str == null)
			return "";
		return str.replace(separator, SEPERATOR_REPLACEMENT).trim();
	}

	public Date getExportStartDate() {
		return exportStartDate;
	}

	public void setExportStartDate(Date exportStartDate) {
		this.exportStartDate = exportStartDate;
	}

	public Date getExportEndDate() {
		return exportEndDate;
	}

	public void setExportEndDate(Date exportEndDate) {
		this.exportEndDate = exportEndDate;
	}

	public void setPackageId(Integer packageId) {
		this.packageId = packageId;
	}

	public Integer getPackageId() {
		return packageId;
	}
}