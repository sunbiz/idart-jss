package model.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import model.manager.exports.ScriptColumn;
import model.nonPersistent.EntitySet;
import model.nonPersistent.ExportDrugInfo;
import model.nonPersistent.ExportPackageInfo;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.APIException;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.util.DAOException;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class PatientSetManager {

	private static Logger log = Logger.getLogger(PatientSetManager.class);

	@SuppressWarnings("unchecked")
	public static List<Patient> getPatientsInSet(Session sess,
			Collection<Integer> ids) {
		return sess.createCriteria(Patient.class).add(
				Restrictions.in("id", ids)).list();
	}

	/**
	 * Used to get data such about a patient
	 * 
	 * e.g. className = Patient, property = firstNames
	 * 
	 * className = Appointment, property = appointmentDate, returnAll = true
	 * 
	 * @param sess
	 * @param patients
	 * @param className
	 * @param property
	 *            the name of the property to fetch
	 * @param returnAll
	 *            if false return only the first value of the given field or
	 *            return all values if true (only applies to fields that have a
	 *            multiplicity greater than one
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public static Map<Integer, Object> getPatientFields(Session sess,
			EntitySet patients, String className, String property,
			boolean returnAll) throws DAOException {

		Map<Integer, Object> patFields = new HashMap<Integer, Object>();

		className = "org.celllife.idart.database.hibernate." + className;

		// default query
		Criteria criteria = null;
		String patientClassName = Patient.class.getCanonicalName();

		// make 'patient.**' reference 'patient' like alias instead of object
		if (className.equals(patientClassName)) {
			criteria = sess.createCriteria(patientClassName, "patient");
		} else {
			criteria = sess.createCriteria(className);
		}

		// cater for extended properties such as clinic.name
		if (property.contains(".")) {
			List<String[]> aliases = createAliasesForExtendedProperty(property);
			for (String[] strings : aliases) {
				if (strings.length > 1) {
					criteria.createAlias(strings[0], strings[1]);
				} else if (strings.length > 1) {
					property = strings[0];
				}
			}
		}

		criteria.setCacheMode(CacheMode.IGNORE);

		// set up the query
		ProjectionList projectionList = Projections.projectionList();

		projectionList.add(Projections.property("patient.id"));
		projectionList.add(Projections.property(property));

		if (patients != null) {
			criteria
					.add(Restrictions.in("patient.id", patients.getEntityIds()));
		}
		criteria.setProjection(projectionList);

		List<Object[]> rows = criteria.list();

		// set up the return map
		if (returnAll) {
			for (Object[] row : rows) {
				Integer ptId = (Integer) row[0];
				Object columnValue = row[1];
				if (!patFields.containsKey(ptId)) {
					Object[] arr = { columnValue };
					patFields.put(ptId, arr);
				} else {
					Object[] oldArr = (Object[]) patFields.get(ptId);
					Object[] newArr = new Object[oldArr.length + 1];
					System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
					newArr[oldArr.length] = columnValue;
					patFields.put(ptId, newArr);
				}
			}
		} else {
			for (Object[] row : rows) {
				Integer ptId = (Integer) row[0];
				Object columnValue = row[1];
				if (!patFields.containsKey(ptId)) {
					patFields.put(ptId, columnValue);
				}
			}
		}

		return patFields;
	}

	/**
	 * This method is used to expand a dot separated property into a list of
	 * aliases that can be used to build up a {@link Criteria}
	 * 
	 * foo returns {foo}
	 * 
	 * foo.bar returns {foo, foo}, {bar}
	 * 
	 * foo.bar.bob returns {foo, foo}, {foo.bar, bar}, {bob}
	 * 
	 * foo.bar.bob.hat returns {foo, foo}, {foo.bar, bar}, {bar.bob, bob}, {hat}
	 * 
	 * @param property
	 * @param criteria
	 * @return
	 */
	private static List<String[]> createAliasesForExtendedProperty(
			String property) {
		List<String[]> aliases = new Vector<String[]>();
		if (!property.contains(".")) {
			aliases.add(new String[] { property });
			return aliases;
		}

		String[] splitProperty = property.split("[.]");
		aliases.add(new String[] { splitProperty[0], splitProperty[0] });

		for (int i = 1; i < splitProperty.length - 1; i++) {
			String aliasField = splitProperty[i - 1] + "." + splitProperty[i];
			aliases.add(new String[] { aliasField, splitProperty[i] });
		}
		aliases.add(new String[] { splitProperty[splitProperty.length - 1] });

		return aliases;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, Map<Integer, ExportPackageInfo>> getPackageDetail(
			Session sess, EntitySet patients, Integer mostRecentNum,
			Date startDate, Date endDate) {
		Map<Integer, Map<Integer, ExportPackageInfo>> patientPackagesMap = new HashMap<Integer, Map<Integer, ExportPackageInfo>>();

		String classNamePackage = Packages.class.getCanonicalName();

		List<Integer> patientIds;
		if (patients != null) {
			patientIds = patients.getEntityIds();
		} else {
			Query patientQuery = sess.createQuery("select id from Patient");
			patientIds = patientQuery.list();
		}

		for (Integer id : patientIds) {
			Criteria critPackg = sess.createCriteria(classNamePackage,
					"package").addOrder(Order.desc("package.pickupDate")).add(
					Restrictions.isNotNull("package.prescription"));
			critPackg.createAlias("package.prescription", "prescription")
					.createAlias("prescription.patient", "patient");
			critPackg.add(Restrictions.eq("patient.id", id));
			critPackg.add(Restrictions.isNotNull("package.pickupDate"));

			if (startDate != null) {
				critPackg.add(Restrictions.ge("packDate", startDate));
			}

			if (endDate != null) {
				critPackg.add(Restrictions.lt("packDate", endDate));
			}

			if (mostRecentNum > 0) {
				critPackg.setMaxResults(mostRecentNum);
			}

			List<Packages> packages = critPackg.list();

			for (Packages packag : packages) {
				Integer pid = packag.getPrescription().getPatient().getId();
				ExportPackageInfo exportPackageInfo = getExportPacakgeInfoFromPackage(
						packag, sess);
				if (!patientPackagesMap.containsKey(pid)) {
					Map<Integer, ExportPackageInfo> packagesList = new HashMap<Integer, ExportPackageInfo>();
					packagesList.put(packag.getId(), exportPackageInfo);
					patientPackagesMap.put(pid, packagesList);
				} else {
					Map<Integer, ExportPackageInfo> packagesList = patientPackagesMap
							.get(pid);
					packagesList.put(packag.getId(), exportPackageInfo);
				}
			}
		}

		return patientPackagesMap;
	}

	private static ExportPackageInfo getExportPacakgeInfoFromPackage(
			Packages packages, Session session) {
		ExportPackageInfo epi = new ExportPackageInfo();
		epi.setDateCollected(packages.getPickupDate());
		epi.setPatientID(packages.getPrescription().getPatient().getId());
		epi.setPackageID(packages.getPackageId());
		epi.setDispenseDate(packages.getPackDate());
		
		// Obtaining prescription
		Prescription prescr = packages.getPrescription();
		// Obtaining prescribed drugs
		List<PrescribedDrugs> lstPrescrDrgs = prescr.getPrescribedDrugs();
		// Obtaining accumulated drugs
		Set<AccumulatedDrugs> lstAccDrgs = packages.getAccumulatedDrugs();
		// Obtaining packaged drugs
		List<PackagedDrugs> lstPackagedDrugs = packages.getPackagedDrugs();

		for (PackagedDrugs pd : lstPackagedDrugs) {
			// For some reason it seems that the list may be initialised but
			// it may contain null elements.
			if (pd != null) {

				ExportDrugInfo edi = new ExportDrugInfo();
				// Obtaining EDI info
				String shortName = DrugManager.getShortGenericDrugName(pd
						.getStock().getDrug(), true);
				int qtyDisp = pd.getAmount();
				double amtPerTime = 0;
				int timesPerDay = 0;
				int accumDrgs = 0;

				for (PrescribedDrugs prd : lstPrescrDrgs) {
					if (prd.getDrug().getId() == pd.getStock().getDrug()
							.getId()) {
						amtPerTime = prd.getAmtPerTime();
						timesPerDay = prd.getTimesPerDay();
						break;
					}

				}

				for (AccumulatedDrugs acumdrg : lstAccDrgs) {
					if (acumdrg.getPillCount().getDrug().getId() == pd
							.getStock().getDrug().getId()) {
						accumDrgs = acumdrg.getPillCount().getAccum();
						break;
					}
				}

				edi.setAccumulatedDrugs(accumDrgs);
				edi.setAmountPerTime(amtPerTime);
				edi.setChemicalCompoundName(shortName);
				edi.setQuantityDispensed(qtyDisp);
				edi.setTimesPerDay(timesPerDay);
				edi.setDrugId(pd.getStock().getDrug().getId());
				edi.setBatch(pd.getStock().getBatchNumber());
				edi.setIsARV(pd.getStock().getDrug().isARV());

				epi.getListOfExportDrugInfo().add(edi);
			}
		}

		Map<Regimen, Set<Integer>> regimenIdMap = PrescriptionManager.getRegimenIdMap(session);
		
		Set<Integer> drugIdSet = epi.getARVDrugIdSet();
		for (Entry<Regimen, Set<Integer>> entry : regimenIdMap.entrySet()) {
			if (drugIdSet.containsAll(entry.getValue())) {
				Regimen theReg = entry.getKey();
				epi.setRegimen(theReg.getDrugGroup());
				epi.setDrugGroupName(theReg.getRegimenName());
				break;
			}
		}
		return epi;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, List<List<Object>>> getEpisodeDetails(
			Session session, EntitySet patients, List<String> details, Date startDate, Date endDate) {
		Map<Integer, List<List<Object>>> ret = new HashMap<Integer, List<List<Object>>>();

		Criteria criteria = session.createCriteria(
				"org.celllife.idart.database.hibernate.Episode", "episode");
		criteria.createAlias("episode.patient", "patient");
		
		if (startDate != null){
			criteria.add(Restrictions.ge("episode.startDate", startDate));
		}

		if (endDate != null){
			criteria.add(Restrictions.or(
					Restrictions.isNull("episode.stopDate"),
					Restrictions.le("episode.stopDate", endDate)));
		}

		criteria.setCacheMode(CacheMode.IGNORE);

		List<String> columns = new Vector<String>();

		if (details == null || details.size() <= 0) {
			columns = getDefaultColumns(Episode.class);
		} else {
			for (String attribute : details) {
				if (attribute == null) {
					attribute = "";
				} else if (attribute.equals("startDate")) {
					// pass -- same column name
				} else if (attribute.equals("startReason")) {
					// pass -- same column name
				} else if (attribute.equals("startNotes")) {
					// pass -- same column name
				} else if (attribute.equals("stopDate")) {
					// pass -- same column name
				} else if (attribute.equals("stopReason")) {
					// pass -- same column name
				} else if (attribute.equals("stopNotes")) {
					// pass -- same column name
				} else if (attribute.equals("clinic.clinicName")) {
					criteria.createAlias("clinic", "clinic");
				} else {
					throw new APIException("Attribute: " + attribute
							+ " is not recognized. Please add reference in "
							+ PatientSetManager.class);
				}

				columns.add(attribute);
			}
		}

		// set up the query
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("patient.id"));
		for (String col : columns) {
			if (col.length() > 0) {
				projections.add(Projections.property(col));
			}
		}
		criteria.setProjection(projections);

		// only restrict on patient ids if some were passed in
		if (patients != null) {
			criteria
					.add(Restrictions.in("patient.id", patients.getEntityIds()));
		}

		criteria.addOrder(org.hibernate.criterion.Order
				.desc("episode.startDate"));

		log.debug("criteria: " + criteria);

		List<Object[]> rows = criteria.list();

		// set up the return map
		for (Object[] rowArray : rows) {
			Integer ptId = (Integer) rowArray[0];

			// get all columns
			int index = 1;
			List<Object> row = new Vector<Object>();
			while (index < rowArray.length) {
				Object value = rowArray[index++];
				row.add(value == null ? "" : value);
			}

			// if we haven't seen a different row for this patient already:
			if (!ret.containsKey(ptId)) {
				List<List<Object>> arr = new Vector<List<Object>>();
				arr.add(row);
				ret.put(ptId, arr);
			}
			// if we have seen a row for this patient already
			else {
				List<List<Object>> oldArr = ret.get(ptId);
				oldArr.add(row);
				ret.put(ptId, oldArr);
			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, List<List<Object>>> getPrescriptionDetails(
			Session sess, EntitySet patients, List<String> details) {

		Map<Integer, List<List<Object>>> ret = new HashMap<Integer, List<List<Object>>>();

		String classNamePrescription = Prescription.class.getCanonicalName();

		List<Integer> patientIds;
		if (patients != null) {
			patientIds = patients.getEntityIds();
		} else {
			Query patientQuery = sess.createQuery("select id from Patient");
			patientIds = patientQuery.list();
		}

		for (Integer id : patientIds) {
			Criteria critScript = sess.createCriteria(classNamePrescription,
					"prescription").addOrder(Order.desc("prescription.date"))
					.add(Restrictions.isNotEmpty("prescription.packages"));
			critScript.createAlias("prescription.patient", "patient");
			critScript.add(Restrictions.eq("patient.id", id));

			List<Prescription> prescriptions = critScript.list();

			for (Prescription script : prescriptions) {
				Integer pid = script.getPatient().getId();
				if (!ret.containsKey(pid)) {
					List<List<Object>> scriptList = new ArrayList<List<Object>>();
					scriptList.add(getScriptInfoList(script, details));
					ret.put(pid, scriptList);
				} else {
					List<List<Object>> scriptList = ret.get(pid);
					scriptList.add(getScriptInfoList(script, details));
				}
			}
		}

		return ret;
	}

	private static List<Object> getScriptInfoList(Prescription script,
			List<String> details) {

		List<Object> info = new ArrayList<Object>();

		for (String attrib : details) {
			if (attrib.equalsIgnoreCase("drugs")) {
				String drugInfo = getDrugDetailsFromScript(script);
				info.add(drugInfo);
			} else if (attrib.equalsIgnoreCase("date")) {
				info.add(script.getDate());
			} else if (attrib.equalsIgnoreCase("clinicalStage")) {
				info.add(script.getClinicalStage());
			} else if (attrib.equalsIgnoreCase("current")) {
				info.add(script.getCurrent());
			} else if (attrib.equalsIgnoreCase("duration")) {
				info.add(script.getDuration());
			} else if (attrib.equalsIgnoreCase("reasonForUpdate")) {
				info.add(script.getReasonForUpdate());
			} else if (attrib.equalsIgnoreCase("notes")) {
				info.add(script.getNotes());
			} else if (attrib.equalsIgnoreCase("weight")) {
				info.add(script.getWeight());
			} else if (attrib.equalsIgnoreCase("endDate")) {
				info.add(script.getEndDate());
			}
		}

		return info;
	}

	private static String getDrugDetailsFromScript(Prescription script) {
		List<PrescribedDrugs> prescribedDrugs = script.getPrescribedDrugs();
		String drugInfo = "";
		for (PrescribedDrugs pd : prescribedDrugs) {
			String drugName = DrugManager.getShortGenericDrugName(pd.getDrug(), true);
			String amtPerTime = String.valueOf(pd.getAmtPerTime());
			String timesPerDay = String.valueOf(pd.getTimesPerDay());
			drugInfo += drugName + "(" + amtPerTime + "x" + timesPerDay + ") ";
		}
		return drugInfo;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, List<List<Object>>> getAppointmentDetails(
			Session session, EntitySet patients, List<String> details) {
		Map<Integer, List<List<Object>>> ret = new HashMap<Integer, List<List<Object>>>();

		Criteria criteria = session.createCriteria(
				"org.celllife.idart.database.hibernate.Appointment",
				"appointment");
		criteria.createAlias("appointment.patient", "patient");

		criteria.setCacheMode(CacheMode.IGNORE);

		List<String> columns = new Vector<String>();

		if (details == null || details.size() <= 0) {
			columns = getDefaultColumns(Appointment.class);
		} else {
			for (String attribute : details) {
				if (attribute == null) {
					attribute = "";
				} else if (attribute.equals("appointmentDate")) {
					// pass -- same column name
				} else if (attribute.equals("active")) {
					// pass -- same column name
				} else {
					throw new APIException("Attribute: " + attribute
							+ " is not recognized. Please add reference in "
							+ PatientSetManager.class);
				}

				columns.add(attribute);
			}
		}

		// set up the query
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("patient.id"));
		for (String col : columns) {
			if (col.length() > 0) {
				projections.add(Projections.property("appointment." + col));
			}
		}
		criteria.setProjection(projections);

		// only restrict on patient ids if some were passed in
		if (patients != null) {
			criteria
					.add(Restrictions.in("patient.id", patients.getEntityIds()));
		}

		criteria.addOrder(org.hibernate.criterion.Order
				.desc("appointment.appointmentDate"));

		log.debug("criteria: " + criteria);

		List<Object[]> rows = criteria.list();

		// set up the return map
		for (Object[] rowArray : rows) {
			Integer ptId = (Integer) rowArray[0];

			// get all columns
			int index = 1;
			List<Object> row = new Vector<Object>();
			while (index < rowArray.length) {
				Object value = rowArray[index++];
				row.add(value == null ? "" : value);
			}

			// if we haven't seen a different row for this patient already:
			if (!ret.containsKey(ptId)) {
				List<List<Object>> arr = new Vector<List<Object>>();
				arr.add(row);
				ret.put(ptId, arr);
			}
			// if we have seen a row for this patient already
			else {
				List<List<Object>> oldArr = ret.get(ptId);
				oldArr.add(row);
				ret.put(ptId, oldArr);
			}
		}

		return ret;
	}

	private static List<String> getDefaultColumns(Class<?> class1) {
		List<String> columns = new Vector<String>();
		if (class1.equals(Episode.class)) {
			columns.add("startDate");
			columns.add("startReason");
			columns.add("stopDate");
			columns.add("stopReason");
		} else if (class1.equals(Prescription.class)) {
			columns.add("date");
			columns.add("reasonForUpdate");
		} else if (class1.equals(Appointment.class)) {
			columns.add("appointmentDate");
		}
		return columns;
	}

	public static Map<Integer, Object> getPatientAttributes(Session sess,
			EntitySet patients, String attributeName) {
		Map<Integer, Object> patFields = new HashMap<Integer, Object>();

		Criteria criteria = null;
		criteria = sess.createCriteria(PatientAttribute.class, "attr");
		criteria.createAlias("attr.patient", "patient").createAlias(
				"attr.type", "type");
		criteria.add(Restrictions.eq("type.name", attributeName));
		criteria.setCacheMode(CacheMode.IGNORE);

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("patient.id"));
		projectionList.add(Projections.property("attr.value"));

		if (patients != null) {
			criteria
					.add(Restrictions.in("patient.id", patients.getEntityIds()));
		}
		criteria.setProjection(projectionList);

		List<Object[]> rows = criteria.list();

		for (Object[] row : rows) {
			Integer ptId = (Integer) row[0];
			Object columnValue = row[1];
			if (!patFields.containsKey(ptId)) {
				patFields.put(ptId, columnValue);
			}
		}

		return patFields;
	}
	
	public static Map<Integer, Object> getPatientIdentifiers(Session sess,
			EntitySet patients, String identifierName) {
		Map<Integer, Object> patFields = new HashMap<Integer, Object>();

		Criteria criteria = null;
		criteria = sess.createCriteria(PatientIdentifier.class, "id");
		criteria.createAlias("id.patient", "patient").createAlias(
				"id.type", "type");
		criteria.add(Restrictions.eq("type.name", identifierName));
		criteria.setCacheMode(CacheMode.IGNORE);

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("patient.id"));
		projectionList.add(Projections.property("id.value"));

		if (patients != null) {
			criteria
					.add(Restrictions.in("patient.id", patients.getEntityIds()));
		}
		criteria.setProjection(projectionList);

		List<Object[]> rows = criteria.list();

		for (Object[] row : rows) {
			Integer ptId = (Integer) row[0];
			Object columnValue = row[1];
			if (!patFields.containsKey(ptId)) {
				patFields.put(ptId, columnValue);
			}
		}

		return patFields;
	}

	public static Map<Integer, Map<Integer, Map<String, Object>>> getPrescriptionIds(
			Session sess, EntitySet patients) {
		Map<Integer, Map<Integer, Map<String, Object>>> patientScriptMap = new HashMap<Integer, Map<Integer, Map<String, Object>>>();

		String classNamePrescription = Prescription.class.getCanonicalName();

		List<Integer> patientIds;
		if (patients != null) {
			patientIds = patients.getEntityIds();
		} else {
			Query patientQuery = sess.createQuery("select id from Patient");
			patientIds = patientQuery.list();
		}

		for (Integer id : patientIds) {
			Criteria critScript = sess.createCriteria(classNamePrescription,
					"prescription").addOrder(Order.desc("prescription.date"))
					.add(Restrictions.isNotEmpty("prescription.packages"));
			critScript.createAlias("prescription.patient", "patient");
			critScript.add(Restrictions.eq("patient.id", id));

			List<Prescription> prescriptions = critScript.list();

			for (Prescription script : prescriptions) {
				Integer pid = script.getPatient().getId();
				if (!patientScriptMap.containsKey(pid)) {
					Map<Integer, Map<String, Object>> scriptList = new HashMap<Integer, Map<String, Object>>();
					scriptList.put(script.getId(), getScriptInfo(script));
					patientScriptMap.put(pid, scriptList);
				} else {
					Map<Integer, Map<String, Object>> scriptList = patientScriptMap
							.get(pid);
					scriptList.put(script.getId(), getScriptInfo(script));
				}
			}
		}

		return patientScriptMap;

	}

	/**
	 * Order important!!
	 * 
	 * @param script
	 * @return
	 */
	private static Map<String, Object> getScriptInfo(Prescription script) {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(ScriptColumn.COL_ID, script.getPrescriptionId());
		info.put(ScriptColumn.COL_DATE, script.getDate());
		info.put(ScriptColumn.COL_DURATION, script.getDuration());

		String drugInfo = getDrugDetailsFromScript(script);
		info.put(ScriptColumn.COL_DRUGINFO, drugInfo);
		return info;
	}

	/**
	 * Returns a list of Patient ID's for patients who are pregnant at the
	 * specified date.
	 * 
	 * @param session
	 * @param patients
	 * @param date
	 * @return
	 */
	public static List<Integer> getPregnantAtDate(Session session,
			EntitySet patients, Date date) {
		List<Integer> patientsPregnantAtDate = new ArrayList<Integer>();

		Criteria criteria = session.createCriteria(
				"org.celllife.idart.database.hibernate.Pregnancy", "pregnancy");
		criteria.add(Restrictions.and(Restrictions.le("confirmDate", date),
				Restrictions.gt("endDate", date)));
		criteria.createAlias("pregnancy.patient", "patient");

		criteria.setCacheMode(CacheMode.IGNORE);

		// set up the query
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("patient.id"));
		criteria.setProjection(projections);

		// only restrict on patient ids if some were passed in
		if (patients != null) {
			criteria
					.add(Restrictions.in("patient.id", patients.getEntityIds()));
		}

		log.debug("criteria: " + criteria);

		List<Object> rows = criteria.list();

		// set up the return map
		for (Object rowVal : rows) {
			patientsPregnantAtDate.add((Integer) rowVal);
		}

		return patientsPregnantAtDate;
	}

}
