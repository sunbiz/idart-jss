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
package org.celllife.idart.gui.patient.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import model.manager.AdherenceManager;
import model.manager.DrugManager;
import model.manager.PackageManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.Session;

/**
 *
 * This class builds the treatment history for a patient by means of looking at
 * all the non-returned packages which the patient has picked up from the
 * clinic.
 *
 */
public class PatientTreatmentHistory {

	private Logger log = Logger.getLogger(this.getClass());
	private Session hSession;
	private int[] lowestAdh;
	private Vector<String[]> tableItems = new Vector<String[]>();
	private List<Packages> packsList = new ArrayList<Packages>();

	/**
	 * Constructor for PatientTreatmentHistory.
	 * @param hSession Session
	 * @param localPatient Patient
	 */
	public PatientTreatmentHistory(Session hSession, Patient localPatient) {
		this.hSession = hSession;
		packsList = PackageManager.getAllPackagesForPatient(hSession,
				localPatient);
	}

	/**
	 * Method getTreatmentHistoryRecordList.
	 * @param tblTreatmentHist 
	 * @return List<String[]>
	 */
	public List<String[]> getTreatmentHistoryRecordList() {
		if (packsList != null && packsList.size() > 0) {
			process();
		}
		return tableItems;
	}

	private void process() {

		String[] record = new String[4];
		Date dteCurrPickUp = new Date();
		for (Packages packs : packsList) {
			if (!packs.isPackageReturned() && packs.getPickupDate()!= null) {
				Date dtePrevPickUp = packs.getPickupDate();
				int daysElapsed = 0;
				if (dtePrevPickUp != null) {
					daysElapsed = iDARTUtil.getDaysBetween(dteCurrPickUp,
							dtePrevPickUp);
					// Create a record and add it to a list
					record[0] = iDARTUtil.format(dtePrevPickUp);
					dteCurrPickUp = (Date)dtePrevPickUp.clone();
				} else {
					log.info("Client package has not yet been picked up");
				}
				record[1] = buildShortDrugNamesForPack(packs);
				record[2] = "0";//Days elapsed
				record[3] = "0%";//Lowest ADH percentage.

				// record[4] = "" + packs.getId(); // pack id
				tableItems.add(record.clone());
				int inx = tableItems.size() - 1;
				if (inx > 0) {
					// Add date to previous pack.
					tableItems.get(inx - 1)[2] = String.valueOf(daysElapsed);
					int lowestAdhForPack = getLowestAdhForPack(packs);
					tableItems.get(inx - 1)[3] = lowestAdhForPack != 0 ? 
							lowestAdhForPack + "%" :"-";
				}
			}
		}
	}

	/**
	 * @param packs
	 * @return
	 *
	 * Returning the short drug names from a package ShortName(PackedDrugs +
	 * QuantityOnHand) Example: AZT 100(60 + 4) ADT 120(30 + 2)
	 *
	 */
	public String buildShortDrugNamesForPack(Packages packs) {
		String shortNames = new String();
		// Run through all non-returned packs
		HashMap<Integer, Integer> accAmountMap = getAccumulatedAmountsForPack(packs);
		List<PackagedDrugs> pckDrugs = packs.getPackagedDrugs();
		// For each pack, get the drug name, and pill count
		for (PackagedDrugs pd : pckDrugs) {
			if (pd == null || pd.getStock() == null){
				continue;
			}
			Stock stock = pd.getStock();
			Drug drug = stock.getDrug();
			String shortDrugName = DrugManager.getShortGenericDrugName(drug, true);
			int amtPacked = pd.getAmount();
			int amtAccumulated = 0;
			if (accAmountMap.containsKey(drug.getId())) {
				amtAccumulated = accAmountMap.get(drug.getId());
			}
			shortNames += shortDrugName + "(" + amtPacked + " + "
					+ amtAccumulated + ")   ";
		}
		return shortNames;
	}

	/**
	 * Method getAccumulatedAmountsForPack.
	 * @param p Packages
	 * @return HashMap<Integer,Integer>
	 */
	private HashMap<Integer, Integer> getAccumulatedAmountsForPack(Packages p) {
		HashMap<Integer, Integer> pillcountMap = new HashMap<Integer, Integer>();
		Set<AccumulatedDrugs> accumDrugsSet = p.getAccumulatedDrugs();
		for (AccumulatedDrugs ad : accumDrugsSet) {
			int currPillCnt = 0;
			int drugId = ad.getPillCount().getDrug().getId();
			if (pillcountMap.containsKey(drugId)) {
				currPillCnt = pillcountMap.get(drugId);
			}
			currPillCnt += ad.getPillCount().getAccum();
			pillcountMap.put(drugId, currPillCnt);
		}
		return pillcountMap;
	}

	/**
	 * Method getLowestAdhForPack.
	 * @param p Packages
	 * @return int
	 */
	private int getLowestAdhForPack(Packages p) {
		Set<PillCount> pcSet = p.getPillCounts();
		lowestAdh = initializeArray(new int[pcSet.size()], -1);
		int inx = 0;
		for (PillCount pc : pcSet) {
			lowestAdh[inx] = AdherenceManager.getAdherencePercent(hSession, pc
					.getAccum(), pc);
			inx++;
		}
		Arrays.sort(lowestAdh);
		// return the first non-negative adherance
		for (int adh : lowestAdh) {
			if (adh >= 0) {
				return adh;
			}
		}
		return 0;
	}

	/**
	 * @param array int[]
	 * @param value int
	 * @return int[]
	 */
	private static int[] initializeArray(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			array[i] = value;
		}
		return array;
	}

}
