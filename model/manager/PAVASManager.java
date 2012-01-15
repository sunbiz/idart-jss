/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package model.manager;

import java.util.Date;
import java.util.List;

import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.PatientStatTypes;
import org.celllife.idart.database.hibernate.PatientStatistic;
import org.celllife.idart.database.hibernate.PatientVisit;
import org.celllife.idart.database.hibernate.PatientVisitReason;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class PAVASManager {

	// private static Log log = LogFactory.getLog(PAVASManager.class);

	/**
	 * Constructor
	 */
	public PAVASManager() {
		super();
	}

	public static void savePatientVisit(Session session, PatientVisit pvisit)
	throws HibernateException {
		session.save(pvisit);
	}

	public static void savePatientStatistic(Session session,
			PatientStatistic pstat) throws HibernateException {
		session.save(pstat);

	}

	public static List<PatientVisit> getVisitsforPatient(Session session, int p)
	throws HibernateException {

		@SuppressWarnings("unchecked")
		List<PatientVisit> result = session
		.createQuery(
				"from PatientVisit as pv where pv.patient_id=:p order by pv.dateofvisit DESC")
				.setInteger("p", p).list();
		return result;

	}

	public static List<PatientStatistic> getStatsforPatient(Session session,
			int p) throws HibernateException {

		@SuppressWarnings("unchecked")
		List<PatientStatistic> result = session
		.createQuery(
				"from PatientStatistic as ps where ps.patient_id=:p order by ps.datetested DESC")
				.setInteger("p", p).list();
		return result;
	}

	public static List<PatientStatistic> getStatsforAllPatients(
			Session session, Date startdate, Date enddate)
			throws HibernateException {

		@SuppressWarnings("unchecked")
		List<PatientStatistic> result = session
		.createQuery(
				"from PatientStatistic as ps where ps.datetested>:sd and ps.datetested<:ed order by ps.datetested")
				.setDate("sd", startdate).setDate("ed", enddate).list();
		return result;
	}

	public static List<PatientVisit> getVisitsforAllPatients(Session session,
			Date startdate, Date enddate) throws HibernateException {

		@SuppressWarnings("unchecked")
		List<PatientVisit> result = session
		.createQuery(
				"from PatientVisit as pv where pv.dateofvisit>:sd and pv.dateofvisit<:ed order by pv.dateofvisit")
				.setDate("sd", startdate).setDate("ed", enddate).list();
		return result;
	}

	public static List<PatientStatistic> localEntryStatistics(Session session,
			int e) throws HibernateException {

		@SuppressWarnings("unchecked")
		List<PatientStatistic> result = session.createQuery(
		"from PatientStatistic as ps where ps.entry_id=:e").setInteger(
				"e", e).list();
		return result;
	}

	public static void deleteEntryStatistics(Session session, int e)
	throws HibernateException {
		session.createQuery("delete PatientStatistic where entry_id = :e")
		.setInteger("e", e).executeUpdate();
	}

	public static List<PatientVisitReason> getVisitReasons(Session session) {
		@SuppressWarnings("unchecked")
		List<PatientVisitReason> result = session.createQuery(
		"from PatientVisitReason as pvr").list();
		return result;
	}

	public static List<PatientStatTypes> getStatTypes(Session session) {
		@SuppressWarnings("unchecked")
		List<PatientStatTypes> result = session.createQuery(
		"from PatientStatTypes as pst").list();
		return result;
	}

	public static PatientVisit getPatientVisit(Session session, int p) {

		@SuppressWarnings("unchecked")
		PatientVisit result = (PatientVisit) session.createQuery(
		"from PatientVisit as pv where id=:p").setInteger("p", p)
		.uniqueResult();
		return result;

	}

	public static long getNumberofStats(Session session, int stattype,
			Date startdate, Date enddate) throws HibernateException {
		long result = (Long) session
		.createQuery(
				"select count(ps.id) from PatientStatistic as ps where ps.datetested>:sd and ps.datetested<:ed and ps.patientstattype_id=:st")
				.setInteger("st", stattype).setDate("sd", startdate).setDate(
						"ed", enddate).uniqueResult();
		return result;
	}

	public static long getTotalPatients(Session session, Date startdate,
			Date enddate) throws HibernateException {
		@SuppressWarnings("unchecked")
		List<Long> patients = session
		.createQuery(
				"select count(pv.patient_id) from PatientVisit as pv where pv.dateofvisit>:sd and pv.dateofvisit<:ed group by pv.patient_id")
				.setDate("sd", startdate).setDate("ed", enddate).list();
		long result = patients.size();
		return result;
	}

	public static long getTotalVisits(Session session, Date startdate,
			Date enddate) throws HibernateException {
		long result = (Long) session
		.createQuery(
				"select count(pv.id) from PatientVisit as pv where pv.dateofvisit>:sd and pv.dateofvisit<:ed")
				.setDate("sd", startdate).setDate("ed", enddate).uniqueResult();
		return result;
	}

	public static String getpatid(Session session, int pid)
	throws HibernateException {
		String result = (String) session.createQuery(
		"select patientId from Patient as p where p.id=:pid ")
		.setInteger("pid", pid).uniqueResult();
		return result;
	}

	public static long getTotalPatientsforReason(Session session,
			int visitreason, Date startdate, Date enddate)
	throws HibernateException {
		@SuppressWarnings("unchecked")
		List<Long> patients = session
		.createQuery(
				"select count(pv.patient_id) from PatientVisit as pv where pv.dateofvisit>:sd and pv.dateofvisit<:ed and pv.patientvisitreason_id=:vr group by pv.patient_id")
				.setInteger("vr", visitreason).setDate("sd", startdate)
				.setDate("ed", enddate).list();
		long result = patients.size();
		return result;
	}

	public static long getTotalVisitsforReason(Session session,
			int visitreason, Date startdate, Date enddate)
	throws HibernateException {
		long result = (Long) session
		.createQuery(
				"select count(pv.id) from PatientVisit as pv where pv.dateofvisit>:sd and pv.dateofvisit<:ed and pv.patientvisitreason_id=:vr")
				.setInteger("vr", visitreason).setDate("sd", startdate)
				.setDate("ed", enddate).uniqueResult();
		return result;
	}

	public static double getStatsMean(Session session, int stattype,
			Date startdate, Date enddate) throws HibernateException {
		double result = (Double) session
		.createQuery(
				"select AVG(ps.statnumeric) from PatientStatistic as ps where ps.datetested>:sd and ps.datetested<:ed and ps.patientstattype_id=:st")
				.setInteger("st", stattype).setDate("sd", startdate).setDate(
						"ed", enddate).uniqueResult();
		return result;
	}

	public static double getStatsMedian(Session session, int stattype,
			long noofstats, Date startdate, Date enddate)
	throws HibernateException {
		@SuppressWarnings("unchecked")
		List<PatientStatistic> pstat = session
		.createQuery(
				"from PatientStatistic as ps where ps.datetested>:sd and ps.datetested<:ed and ps.patientstattype_id=:st order by ps.statnumeric")
				.setInteger("st", stattype).setDate("sd", startdate).setDate(
						"ed", enddate).list();
		double result = 0;
		long mymed1 = 0;
		long mymed2 = 0;
		double mymedval1 = 0;
		double mymedval2 = 0;
		if (noofstats % 2 == 0) {
			// even
			mymed1 = (noofstats / 2) - 1;
			mymed2 = mymed1 + 1;
		} else {
			// odd
			mymed1 = ((noofstats + 1) / 2) - 1;
			mymed2 = mymed1;
		}

		for (int i = 0; i < pstat.size(); i++) {
			if (i == mymed1) {
				mymedval1 = pstat.get(i).getstatnumeric();
			}
			if (i == mymed2) {
				mymedval2 = pstat.get(i).getstatnumeric();
			}
		}
		result = (mymedval1 + mymedval2) / 2;
		return result;
	}

	public static boolean checkValidEndDate(Date startDate, Date endDate) {

		if (startDate.after(endDate))
			return false;
		else
			return true;
	}

	public static List<Episode> getCohort(Session session, Date startdate,
			Date enddate) throws HibernateException {

		@SuppressWarnings("unchecked")
		List<Episode> result = session
		.createQuery(
				"from Episode as e where e.startDate>:sd and e.startDate<:ed and e.startReason='New Patient'")
				.setDate("sd", startdate).setDate("ed", enddate).list();
		return result;
	}

	public static List<Episode> getEarlierEpisodes(Session session, int p,
			Date thedate) throws HibernateException {

		@SuppressWarnings("unchecked")
		List<Episode> result = session
		.createQuery(
				"from Episode as e where e.patient=:p and e.startDate<:thedate)")
				.setInteger("p", p).setDate("thedate", thedate).list();
		return result;
	}

	public static List<Episode> lostorDefaultEpisode(Session session, int p,
			Date thestartdate, Date theenddate) throws HibernateException {

		@SuppressWarnings("unchecked")
		List<Episode> result = session
		.createQuery(
				"from Episode as e where e.patient=:p and e.stopDate>:thestartdate and e.stopDate<:theenddate and (e.stopReason='Transferred Out' or e.stopReason='Defaulted'")
				.setInteger("p", p).setDate("thestartdate", thestartdate)
				.setDate("thenddate", theenddate).list();
		return result;
	}

	public static List<PatientStatistic> getStatBetweenDates(Session session,
			int p, int stat, Date startdt, Date enddt)
			throws HibernateException {

		@SuppressWarnings("unchecked")
		List<PatientStatistic> result = session
		.createQuery(
				"from PatientStatistic as ps where ps.patient_id=:p and ps.patientstattype_id=:stat and ps.datetested>:startdt and ps.datetested<:enddt order by ps.datetested DESC")
				.setInteger("p", p).setInteger("stat", stat).setDate("enddt",
						enddt).setDate("startdt", startdt).list();
		return result;
	}

}
