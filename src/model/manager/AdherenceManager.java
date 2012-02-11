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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class AdherenceManager {

	private static Log log = LogFactory.getLog(AdherenceManager.class);

	/**
	 * Save a list of pillcounts
	 * 
	 * @param session
	 *            Session
	 * @param pcList
	 * @throws HibernateException
	 */
	public static void save(Session session, Set<PillCount> pcList)
	throws HibernateException {

		if (pcList == null) {
			log.warn("Tried to save an empty list of pill counts");
			return;
		}

		for (PillCount pc : pcList) {
			session.save(pc);
		}

	}

	/**
	 * Calculates the adherence percentage for a PillCount given the quantity
	 * accumulated.
	 * 
	 * For a completed PillCount this method can be used as follows:
	 * getAdherencePercent(sess, pc.getAccum(), pc);
	 * 
	 * @param sess
	 * @param inHandAccumulated
	 * @param pc
	 * @return int
	 * @throws HibernateException
	 */
	public static int getAdherencePercent(Session sess, int inHandAccumulated,
			PillCount pc) throws HibernateException {
		int adherence = -1;
		Date d = pc.getPreviousPackage().getPickupDate();

		if (d == null) {
			log
			.info("Trying to calculate adherence, but patient hasn't rec. pkg.");
			return adherence;
		}

		// dispenseDate minus last visit
		int daysElapsed = iDARTUtil.getDaysBetween(pc.getDateOfCount(), d);
		// days elapsed * amount taken

		PrescribedDrugs preDrug = DrugManager.getPrescribedDrugForPackagedDrug(
				pc.getPreviousPackage(), pc.getDrug());

		if (preDrug == null) {
			log.error(String.format(
					"Missing drug in prescriotion(%s) for pillcount(%s)", pc
					.getPreviousPackage().getPrescription().getId(), pc
					.getId()));
			return adherence;
		}
		// getAccumulated = -1 if the value is invalid.
		if (inHandAccumulated < 0) {
			inHandAccumulated = 0;
			// consumed = all drugs taken last time, minus the ones in hand
		}

		int dispensed = getQuantityDispensed(preDrug.getDrug(), pc
				.getPreviousPackage());
		int accumulated = getQuantityAccumulated(preDrug.getDrug(), pc
				.getPreviousPackage());
		double consumed = dispensed + accumulated - inHandAccumulated;

		double denominator = daysElapsed * preDrug.getTimesPerDay()
		* preDrug.getAmtPerTime();
		if (denominator == 0) {

			log.info("Trying to calculate adherence, but last visit was today");
			return adherence;
		}

		log.info("Adherence calc: daysElapsed: " + daysElapsed
				+ ", Denominator: " + denominator + ", Consumed: " + consumed);
		adherence = (int) ((consumed / denominator) * 100);

		return adherence;
	}

	/**
	 * Get adherence record objects for this pack (for submission to eKapa)
	 * 
	 * @param sess
	 * @param pc
	 *            PillCount
	 * @return AdherenceRecord
	 * @throws HibernateException
	 */
	public static AdherenceRecord getAdherenceRecordForPillCount(Session sess,
			PillCount pc) throws HibernateException {
		AdherenceRecord ar = null;

		Calendar pickupDate = Calendar.getInstance();
		Packages pack = pc.getPreviousPackage();
		pickupDate.setTime(pack.getPickupDate());

		Calendar countDate = Calendar.getInstance();

		Drug drug = pc.getDrug();

		countDate.setTime(pc.getDateOfCount());
		long daysSinceVisit = (countDate.getTime().getTime() - pickupDate
				.getTime().getTime()) / 86400000;

		ar = new AdherenceRecord(pc.getId(), pc.getDateOfCount(),
				(int) daysSinceVisit, getDaysSuppliedForDrugInPackage(sess,
						drug, pack), getDaysAccumulatedForDrugInPackage(sess,
								drug, pack), getDaysInHandForDrugInPackage(sess, drug,
										pack, pc.getAccum()), LocalObjects.getUser(sess)
										.getUsername(), PatientManager.getPatient(sess,
												pack.getPrescription().getPatient().getId())
												.getPatientId());

		return ar;
	}

	/**
	 * Get days supply for a drug in a package
	 * 
	 * @param session
	 * @param d
	 * @param p
	 * @return int
	 * @throws HibernateException
	 */
	public static int getDaysSuppliedForDrugInPackage(Session session, Drug d,
			Packages p) throws HibernateException {

		Long unitsSupplied = (Long) session
		.createQuery(
				"select sum(pd.amount) from PackagedDrugs as pd where pd.parentPackage.id = :thePackageId"
				+ " and pd.stock.drug.id = :theDrugId")
				.setInteger("thePackageId", p.getId()).setInteger("theDrugId",
						d.getId()).uniqueResult();

		int unitsPerDay = getUnitsPerDayForDrugInPackage(session, d, p);

		if (unitsPerDay == 0)
			return 0;

		return unitsSupplied.intValue() / unitsPerDay;

	}

	/**
	 * Get days in hand for a drug in a package
	 * 
	 * @param sess
	 * @param d
	 * @param p
	 * @param accumUnits
	 * @return int
	 * @throws HibernateException
	 */
	public static int getDaysInHandForDrugInPackage(Session sess, Drug d,
			Packages p, int accumUnits) throws HibernateException {
		int unitsPerDay = getUnitsPerDayForDrugInPackage(sess, d, p);

		if (unitsPerDay == 0)
			return 0;

		return accumUnits / unitsPerDay;

	}

	/**
	 * Get the prescribed units per day for a drug in a package (calculated from
	 * the prescribeddrug timesperday and amountpertime)
	 * 
	 * @param session
	 * @param d
	 * @param p
	 * @return int
	 * @throws HibernateException
	 */
	public static int getUnitsPerDayForDrugInPackage(Session session, Drug d,
			Packages p) throws HibernateException {

		int unitsPerDay = 0;

		int prescriptionId = p.getPrescription().getId();

		unitsPerDay = ((Double) session
				.createQuery(
						"select (preDrug.amtPerTime * preDrug.timesPerDay) "
						+ "from PrescribedDrugs as preDrug where preDrug.prescription.id = :prescriptionId "
						+ " and preDrug.drug.id = :theDrugId")
						.setInteger("prescriptionId", prescriptionId).setInteger(
								"theDrugId", d.getId()).uniqueResult()).intValue();

		return unitsPerDay;
	}

	/**
	 * Get days accumulated for a drug in a package
	 * 
	 * @param session
	 * @param d
	 * @param p
	 * @return int
	 * @throws HibernateException
	 */
	public static int getDaysAccumulatedForDrugInPackage(Session session,
			Drug d, Packages p) throws HibernateException {

		 Long unitsAccum = (Long) session.createQuery(
				"select sum(ad.pillCount.accum) from AccumulatedDrugs as ad where ad.withPackage = :thePackageId"
				+ " and ad.pillCount.drug.id = :theDrugId")
				.setInteger("thePackageId", p.getId()).setInteger(
						"theDrugId", d.getId()).uniqueResult();

		int unitsPerDay = getUnitsPerDayForDrugInPackage(session, d, p);

		if (unitsPerDay == 0)
			return 0;

		if (unitsAccum == null)
			return 0;
		
		return unitsAccum.intValue() / unitsPerDay;
	}

	/**
	 * Caluclates the quantity of drug dispensed in a package.
	 * @param d
	 *            Drug
	 * @param p
	 *            Package
	 * 
	 * @return quantity of drug dispensed
	 * @throws HibernateException
	 */
	private static int getQuantityDispensed(Drug d, Packages p)
		throws HibernateException {
		int quantity = 0;
		List<PackagedDrugs> packagedDrugs = p.getPackagedDrugs();
		for (PackagedDrugs pd : packagedDrugs) {
			if (pd.getStock().getDrug().getId() == d.getId()){
				quantity += pd.getAmount();
			}
		}
		
		return quantity;
	}

	/**
	 * Calculates the quantity of a accumulated drugs for a package.
	 * @param d
	 *            Drug
	 * @param p
	 *            Package
	 * 
	 * @return quantity of drug accumulated
	 */
	private static int getQuantityAccumulated(Drug d, Packages p) {
		
		Set<AccumulatedDrugs> accumulatedDrugs = p.getAccumulatedDrugs();
		int quantity = 0;
		for (AccumulatedDrugs ad : accumulatedDrugs) {
			if (ad.getPillCount().getDrug().getId() == d.getId()){
				quantity += ad.getPillCount().getAccum();
			}
		}
		
		return quantity;
	}

}
