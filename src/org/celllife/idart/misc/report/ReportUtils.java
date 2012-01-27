package org.celllife.idart.misc.report;

/**
 *
 */

/**
 * @author renato
 *
 */

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.manager.DrugManager;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.Session;

public class ReportUtils extends net.sf.jasperreports.engine.JRDefaultScriptlet {

	public String getPatientAge(Date dob) {

		Calendar today = Calendar.getInstance();
		Calendar mydob = Calendar.getInstance();
		mydob.setTime(dob == null ? new Date() : dob);
		// Get age based on year
		int age = today.get(Calendar.YEAR) - mydob.get(Calendar.YEAR);
		// Add the tentative age to the date of birth to get this year's
		// birthday
		mydob.add(Calendar.YEAR, age);
		// If birthday hasn't happened yet, subtract one from
		// age
		if (today.before(mydob)) {
			age--;
		}
		return new String("" + age);
	}

	@SuppressWarnings("unchecked")
	public String getDrugContents(Integer packageDrugId) {

		Session session = HibernateUtil.getNewSession();
		String result = "";

		List<Object[]> list = session.createSQLQuery("select chemComp.acronym, chemDrugStrength.strength, packDrugs.amount, accumDrugs.accum " +
				"from  ( select amount, stock, parentPackage from PackagedDrugs pd where pd.id = :packageDrugId ) as packDrugs " +
				"left outer join ( select id, drug from Stock ) as stock on packDrugs.stock = stock.id " +
				"left outer join ( select chemicalCompound, drug, strength from ChemicalDrugStrength ) as chemDrugStrength " +
				" on stock.drug = chemDrugStrength.drug left outer join ( select acronym, id from ChemicalCompound) as chemComp " +
				"on chemDrugStrength.chemicalCompound = chemComp.id left outer join ( select withPackage, drug, accum " +
				"from PillCount, AccumulatedDrugs where AccumulatedDrugs.pillcount = pillCount.id ) as accumDrugs on " +
		"((accumDrugs.withPackage = packDrugs.parentPackage) and (accumDrugs.drug = stock.drug))")
		.setInteger("packageDrugId", packageDrugId.intValue()).list();


		int counter = 0;
		String amounts ="";

		if (list != null && list.size() > 0) {
			for (Object[] objects : list) {

				if (objects[1] != null) {
					if (list.size() == 1) {
						result += objects[0] + " " + objects[1] + " mg ("
						+ objects[2]
						          + (objects[3] != null ? "+" + objects[3] : "")
						          + ")/";
					} else {
						result += objects[0] + " " + objects[1] + " mg /";
					}
					counter++;
					if (counter == list.size() && list.size() > 1) {
						amounts += "(" + objects[2]
						                         + (objects[3] != null ? "+" + objects[3] : "")
						                         + ")";
					}
				}
				else
					return DrugManager.getDrugNameForPackagedDrug(session, packageDrugId);
			}
			result = result.substring(0, result.length() - 1);
			result += amounts;
		} else
			return DrugManager.getDrugNameForPackagedDrug(session, packageDrugId);





		return result;
	}

	/**
	 * Gets the number of days between two dates
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public int daysBetween(Date date1, Date date2) {
		return iDARTUtil.getDaysBetween(date1, date2);
	}

}
