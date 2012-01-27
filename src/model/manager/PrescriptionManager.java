package model.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class PrescriptionManager {

	public static List<Prescription> getValidPrescriptions(Session session,
			int clinicId, boolean isPaeds, Date startDate, Date endDate, Date cutOff) {
		return getValidPrescriptions(session, clinicId, isPaeds, startDate, endDate,
				cutOff, false);
	}
	
	public static List<Prescription> getValidPrescriptions(Session session,
			int clinicId, boolean isPaeds, Date startDate, Date endDate,
			Date cutOff, boolean limitByEpisodes) {

		Criteria criteria;

		if (clinicId != 0) {

			criteria = session.createCriteria(Prescription.class).createAlias(
					"patient", "patient").createAlias("patient.episodes",
					"episode").createAlias("episode.clinic", "clinic").add(
					Restrictions.or(Restrictions.and(Restrictions.le("date",
							endDate), Restrictions.isNull("endDate")),
							Restrictions.and(Restrictions.le("date", endDate),
									Restrictions.ge("endDate", endDate)))).add(
					Restrictions.eq("clinic.id", clinicId));
		} else {
			criteria = session.createCriteria(Prescription.class).createAlias(
					"patient", "patient").createAlias("patient.episodes",
					"episode").add(
					Restrictions.or(Restrictions.and(Restrictions.le("date",
							endDate), Restrictions.isNull("endDate")),
							Restrictions.and(Restrictions.le("date", endDate),
									Restrictions.ge("endDate", endDate))));				
		}

		if (cutOff != null) {
			if (isPaeds) {
				criteria.add(Restrictions.gt("patient.dateOfBirth", cutOff));
			} else {
				criteria.add(Restrictions.le("patient.dateOfBirth", cutOff));
			}
		}

		if (limitByEpisodes && startDate != null && startDate.before(endDate)) {
			criteria.add(
					Restrictions.or(
					Restrictions.or(Restrictions.between("episode.startDate", startDate, endDate), 
							Restrictions.between("episode.stopDate", startDate, endDate)),
							Restrictions.and(Restrictions.lt("episode.startDate", startDate),
									Restrictions.isNull("episode.stopDate"))));
		}

		 criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Prescription> scripts = criteria.list();
		return scripts;
	}

	public static Map<Regimen, Set<Integer>> getRegimenIdMap(Session session) {
		List<Regimen> regimens = session.createQuery("from Regimen as reg")
				.list();
		Iterator<Regimen> regIt = regimens.iterator();
		Map<Regimen, Set<Integer>> regimenIdMap = new HashMap<Regimen, Set<Integer>>();
		while (regIt.hasNext()) {
			Set<Integer> regDrugSet = new HashSet<Integer>();

			// get all the drugs in this set of regimenDrugs
			Regimen theReg = regIt.next();

			List<RegimenDrugs> regDrugs = theReg.getRegimenDrugs();

			Iterator<RegimenDrugs> regDrugsIt = regDrugs.iterator();

			while (regDrugsIt.hasNext()) {
				RegimenDrugs rd = regDrugsIt.next();
				// we only care about ARVs
				if (rd.getDrug().isARV()) {
					regDrugSet.add(rd.getDrug().getId());
				}
			}

			if (!regDrugSet.isEmpty()) {
				regimenIdMap.put(theReg, regDrugSet);
			}
		}
		return regimenIdMap;
	}

	/**
	 * This method will return the first prescription
	 * 
	 * @return
	 */
	public static Prescription getFirstPrescriptionForEpisode(Session session,
			Episode episode) {

		if (episode.getStopDate() == null) {
			return (Prescription) session.createQuery(
					"select pre from Prescription pre where "
							+ "pre.patient = :pId and "
							+ "(pre.endDate > :startDate  or "
							+ "pre.endDate is null ) "
							+ "order by pre.date asc").setLong("pId",
					episode.getPatient().getId()).setDate("startDate",
					episode.getStartDate()).setMaxResults(1).uniqueResult();
		}

		return (Prescription) session.createQuery(
				"select pre from Prescription pre where "
						+ "pre.patient = :pId and "
						+ "((pre.date between :startDate and :endDate) or "
						+ "(pre.endDate between :startDate and :endDate) or "
						+ "(pre.date < :startDate and pre.endDate is null)) "
						+ "order by pre.date asc").setLong("pId",
				episode.getPatient().getId()).setDate("startDate",
				episode.getStartDate()).setDate("endDate",
				episode.getStopDate()).setMaxResults(1).uniqueResult();

	}

	/**
	 * @param pack
	 * @return a string of the form d4t 30, EFV 600 etc
	 * @throws HibernateException
	 */
	public static String getShortPrescriptionContentsString(Prescription pres) {
		String drugsInPrescription = "";

		List<Drug> result = new ArrayList<Drug>();
		if (pres.getPrescribedDrugs() != null) {
			for (PrescribedDrugs pd : pres.getPrescribedDrugs()) {
				result.add(pd.getDrug());
			}

			drugsInPrescription = DrugManager.getDrugListString(result, ", ", true);
		}

		return drugsInPrescription;
	}

	/**
	 * This method will return the first prescription
	 * 
	 * @return
	 */
	public static Prescription getLastPrescriptionForEpisode(Session session,
			Episode episode) {

		return (Prescription) session.createQuery(
				"select pre from Prescription pre where "
						+ "pre.patient = :pId and "
						+ "((pre.date between :startDate and :endDate) or "
						+ "(pre.endDate between :startDate and :endDate) or "
						+ "(pre.date < :startDate and pre.endDate is null)) "
						+ "order by pre.date asc").setLong("pId",
				episode.getPatient().getId()).setDate("startDate",
				episode.getStartDate()).setDate("endDate",
				episode.getStopDate()).setMaxResults(1).uniqueResult();

	}

}
