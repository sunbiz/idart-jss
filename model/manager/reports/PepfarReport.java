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

package model.manager.reports;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import model.manager.PrescriptionManager;
import model.manager.excel.conversion.exceptions.ReportException;
import model.nonPersistent.DrugCombination;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class PepfarReport extends AbstractJasperReport {

	private final Date endDate;

	private final int cutoffAge;

	private final Clinic clinic;

	private final int cutoffAgeSub;

	private final Date startDate;

	private String totalAdults;

	private String totalPaeds;

	/**
	 * Constructor for ReportManager.
	 *
	 * @param hSession
	 *            Session
	 * @param parent
	 *            Shell
	 */
	public PepfarReport(Shell parent, Date theStartDate, Date theEndDate,
			int theCutoffAge, int cutoffAgeSub, Clinic clinic) {
		super(parent);
		cutoffAge = theCutoffAge;
		this.cutoffAgeSub = cutoffAgeSub;
		this.clinic = clinic;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	public static List<String> getPepfarDrugCombinations(Session session, Date startDate, 
			Date endDate, int cutoffAge, Clinic c) throws HibernateException {

		List<String> theReturnList = new ArrayList<String>();

		Calendar cutOffDate = Calendar.getInstance();
		cutOffDate.setTime(endDate);
		cutOffDate.add(Calendar.YEAR, -cutoffAge);

		List<Prescription> adultScripts = new ArrayList<Prescription>();
		List<Prescription> paedsScripts = new ArrayList<Prescription>();

		List<DrugCombination> drugCombinationList = new ArrayList<DrugCombination>();

		int totalAdultScripts = 0;
		int totalPaedsScripts = 0;

		adultScripts = PrescriptionManager.getValidPrescriptions(session, c
				.getId(), false, startDate, endDate, cutOffDate.getTime(), true);
		log.debug("Total adults scripts before processing: "
				+ adultScripts.size());
		getDrugCombosFromPrescriptions(adultScripts, drugCombinationList, true);

		paedsScripts = PrescriptionManager.getValidPrescriptions(session, c
				.getId(), true, startDate, endDate, cutOffDate.getTime());
		log.debug("Total paeds scripts before processing: "
				+ paedsScripts.size());
		getDrugCombosFromPrescriptions(paedsScripts, drugCombinationList, false);

		Map<Regimen, Set<Integer>> regimenIdMap = PrescriptionManager
		.getRegimenIdMap(session);
		for (DrugCombination combo : drugCombinationList) {
			Set<Integer> drugIdSet = combo.getDrugIdSet();
			for (Entry<Regimen, Set<Integer>> entry : regimenIdMap.entrySet()) {
				if (drugIdSet.containsAll(entry.getValue())) {
					Regimen theReg = entry.getKey();
					combo.addRegimen(theReg.getDrugGroup());
				}
			}
		}

		Iterator<DrugCombination> drugCombsIt = drugCombinationList.iterator();
		while (drugCombsIt.hasNext()) {
			DrugCombination dc = drugCombsIt.next();
			theReturnList.add(dc.toString() + "," + dc.getAdultsOnThis().size()
					+ "," + dc.getPaedsOnThis().size() + "\n");
			log.debug(dc.toString() + ": " + dc.getAdultsOnThis().size()
					+ " adults, " + dc.getPaedsOnThis().size() + " paeds");
			totalAdultScripts += dc.getAdultsOnThis().size();
			totalPaedsScripts += dc.getPaedsOnThis().size();
		}

		log
		.debug("Total adults scripts after processing: "
				+ totalAdultScripts);
		log.debug("Total paeds scripts after processing: " + totalPaedsScripts);

		theReturnList.add(totalAdultScripts + "," + totalPaedsScripts + "\n");
		return theReturnList;
	}

	private static void getDrugCombosFromPrescriptions(
			List<Prescription> scriptList,
			List<DrugCombination> drugCombinationList, boolean isAdults) {
		Iterator<Prescription> scriptsIt = scriptList.iterator();
		while (scriptsIt.hasNext()) {
			Prescription pre = scriptsIt.next();
			List<PrescribedDrugs> pdList = pre.getPrescribedDrugs();

			Iterator<PrescribedDrugs> pdIt = pdList.iterator();
			Set<Drug> drugIdSet = new HashSet<Drug>();

			while (pdIt.hasNext()) {
				Drug theDrug = pdIt.next().getDrug();
				if (theDrug.isARV()) {
					drugIdSet.add(theDrug);
				}
			}

			DrugCombination drugComb = new DrugCombination(drugIdSet);

			if (drugIdSet.size() > 0) {
				if (!drugCombinationList.contains(drugComb)) {
					if (isAdults) {
						drugComb.addAdultOnThis(pre.getPatient().getId());
					} else {
						drugComb.addPaedOnThis(pre.getPatient().getId());
					}

					drugCombinationList.add(drugComb);
				} else {
					DrugCombination drugCombination = drugCombinationList
					.get(drugCombinationList.indexOf(drugComb));
					if (isAdults) {
						drugCombination
						.addAdultOnThis(pre.getPatient().getId());
					} else {
						drugCombination.addPaedOnThis(pre.getPatient().getId());
					}
				}
			}

		}
	}

	@Override
	public Map<String, Object> getParameterMap() throws ReportException {
		
		
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("path", getReportPath());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		// Define the connection
		try {
			map.put("connection", JDBCUtil.currentSession());
		} catch (SQLException e) {
			throw new ReportException("Unable to get database connection", e);
		}

		map.put("adultsSum", totalAdults);
		map.put("paedsSum", totalPaeds);
		map.put("startDate", new Timestamp(startDate.getTime()));
		map.put("endDate", new Timestamp(endDate.getTime()));
		// calculate the cutoff birth dates
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);
		cal2.add(Calendar.YEAR, -cutoffAge);
		map.put("cutoffDate", new Timestamp(cal2.getTime().getTime()));
		cal2.setTime(endDate);
		cal2.add(Calendar.YEAR, -cutoffAgeSub);
		map.put("cutoffDateSub", new Timestamp(cal2.getTime().getTime()));
		map.put("cutoffAge", cutoffAge);
		map.put("cutoffAgeSub", cutoffAgeSub);
		map.put("clinic", clinic.getClinicName());
		cal2.set(0, 0, 0, 0, 0, 0);
		map.put("baseDate", new Timestamp(cal2.getTime().getTime()));

		return map;
	}

	@Override
	protected void generateData() throws ReportException {
		try {
			final List<String> strList = new ArrayList<String>();
			strList.addAll(PepfarReport.getPepfarDrugCombinations(hSession, startDate, 
					endDate, cutoffAge, clinic));
			String totalStr = strList.get(strList.size() - 1);
			totalAdults = totalStr.split(",")[0];
			totalPaeds = totalStr.split(",")[1];
			strList.remove(strList.size() - 1);
			Collections.sort(strList);
			File csvFile = new File("Reports" + java.io.File.separator
					+ "tmpDrugCombinations.csv");
			FileWriter out = new FileWriter(csvFile);
			out.write("drugComb,adultsOnThis,paedsOnThis\n");
			if (strList.isEmpty()){
				strList.add("-,0,0");
			}
			Iterator<String> strListIt = strList.iterator();
			while (strListIt.hasNext()) {
				out.write(strListIt.next());
			}
			out.close();
		} catch (Exception e) {
			throw new ReportException("Error writing data to CSV", e);
		}
	}

	@Override
	public String getReportFileName() {
		return "pepfarReport";
	}

	@Override
	public Object getDataSource() throws ReportException {
		try {
			JRCsvDataSource jcvs = new JRCsvDataSource(new File("Reports"
					+ java.io.File.separator + "tmpDrugCombinations.csv"));
			jcvs.setUseFirstRowAsHeader(true);
			return jcvs;
		} catch (Exception e) {
			throw new ReportException("Error getting data source", e);
		}
	}
}
