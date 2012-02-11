package model.manager.reports;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import model.manager.AdministrationManager;
import model.manager.PrescriptionManager;
import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class ClinicIndicatorReport extends AbstractJasperReport {

	private final String clinicName;
	private final int minDays;
	private final int cutoffAge;
	private final Date startDate;
	private final Date endDate;
	private Map<String, Integer> adultsStats;
	private Map<String, Integer> paedsStats;

	public ClinicIndicatorReport(Shell parent, String clinicName,
			Date theStartDate, Date theEndDate, int minDays, final int cutoffAge) {
		super(parent);
		this.clinicName = clinicName;
		this.minDays = minDays;
		this.cutoffAge = cutoffAge;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {

		adultsStats = getNoOfPatientsOnRegimens(hSession, "%"
				.equalsIgnoreCase(clinicName) ? 0 : AdministrationManager
				.getClinic(hSession, clinicName).getId(), false, startDate,
				endDate, cutoffAge);
		paedsStats = getNoOfPatientsOnRegimens(hSession, "%"
				.equalsIgnoreCase(clinicName) ? 0 : AdministrationManager
				.getClinic(hSession, clinicName).getId(), true, startDate,
				endDate, cutoffAge);

	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("adultsOnRegimens", adultsStats);
		map.put("paedsOnRegimens", paedsStats);
		map.put("path", getReportPath());

		map.put("clinic", clinicName);
		map.put("startDate", new Timestamp(startDate.getTime()));
		map.put("endDate", new Timestamp(endDate.getTime()));
		map.put("cutoffAge", cutoffAge);
		map.put("minDays", minDays);

		// paeds must have been born after this date
		Calendar cutOffDate = Calendar.getInstance();
		cutOffDate.setTime(endDate);
		cutOffDate.add(Calendar.YEAR, -cutoffAge);
		map.put("cutoffDate", new Timestamp(cutOffDate.getTimeInMillis()));
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "monthlyClinicIndicatorReport";
	}

	/**
	 * @param session
	 * @param clinicId
	 * @param isPaeds
	 * @param theMonthStartDate
	 * @param theMonthEndDate
	 * @param cutoffAge
	 * @return
	 * @throws HibernateException
	 */
	public static Map<String, Integer> getNoOfPatientsOnRegimens(
			Session session, int clinicId, boolean isPaeds,
			Date theMonthStartDate, Date theMonthEndDate, int cutoffAge) throws HibernateException {

		Map<String, Integer> results = new HashMap<String, Integer>();

		int noOfPatientsNotOnARV = 0;
		int noOfRegimenPrescriptions = 0;

		Calendar cal = Calendar.getInstance();
		cal.setTime(theMonthEndDate);
		cal.add(Calendar.YEAR, -cutoffAge);
		Date cutOff = cal.getTime();

		List<Prescription> scripts = PrescriptionManager.getValidPrescriptions(
				session, clinicId, isPaeds, theMonthStartDate,  theMonthEndDate,  cutOff, true);

		Map<Regimen, Set<Integer>> regimenIdMap = PrescriptionManager
				.getRegimenIdMap(session);

		Iterator<Prescription> preIt = scripts.iterator();
		while (preIt.hasNext()) {
			// get all the drugs in this set of prescribedDrugs
			Prescription thePre = preIt.next();
			List<PrescribedDrugs> preDrugs = (thePre).getPrescribedDrugs();

			Iterator<PrescribedDrugs> preDrugsIt = preDrugs.iterator();
			Set<Integer> preDrugSet = new HashSet<Integer>();
			Boolean containsARV = false;
			while (preDrugsIt.hasNext()) {
				PrescribedDrugs pd = preDrugsIt.next();
				if (pd.isARV()) {
					containsARV = true;
				}
				preDrugSet.add(pd.getDrug().getId());
			}

			// check that there are ARV drugs on this prescription
			if (!containsARV) {
				noOfPatientsNotOnARV++;
			} else {
				for (Entry<Regimen, Set<Integer>> entry : regimenIdMap
						.entrySet()) {
					if (preDrugSet.containsAll(entry.getValue())) {
						Regimen theReg = entry.getKey();
						if (results.get(theReg.getDrugGroup()) != null) {
							results.put(theReg.getDrugGroup(), results.get(
									theReg.getDrugGroup()).intValue() + 1);
						}else {
							results.put(theReg.getDrugGroup(), 1);
						}
						noOfRegimenPrescriptions++;
					}
				}
			}
		}

		int other = scripts.size()
				- (noOfRegimenPrescriptions + noOfPatientsNotOnARV);

		// can be negative because single patient may be on more than 1 regimen
		// at a time.
		if (other < 0) {
			results.put("Other", 0);
		} else {
			results.put("Other", other);
		}
		return results;
	}

}
