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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.JDBCUtil;

/**
 */
public class ExcelReportManager {

	private static Logger log = Logger.getLogger(ExcelReportManager.class);

	/**
	 * Method getMonthlyReceiptsAndIssuesData.
	 * 
	 * @param clinic
	 *            Clinic
	 * @param pharm
	 *            Pharmacy
	 * @param calendar
	 *            Calendar
	 * @return List<Object[]>
	 */
	public static List<Object[]> getMonthlyReceiptsAndIssuesData(
			StockCenter stockCenter, Calendar calendar) {

		List<Object[]> results = new ArrayList<Object[]>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");

		try {
			// month start from report parameters from gui
			String monthStart = dateFormat.format(calendar.getTime());

			// add a month to obtain the month end
			calendar.add(Calendar.MONTH, 1);
			String monthEnd = dateFormat.format(calendar.getTime());

			// get connection from database
			Connection db = JDBCUtil.currentSession();
			Statement sql = db.createStatement();

			String query = "select distinct d.id, d.name, d.packsize, d.nsncode,d.stockcode "
					+ "from drug as d, stock as s "
					+ "where s.drug = d.id "
					+ "and s.pharmacy = "
					+ stockCenter.getId()
					+ "and d.sidetreatment = 'F' " + "order by d.name asc";

			ResultSet drugs = sql.executeQuery(query);

			if (drugs != null) {

				PreparedStatement ps;
				while (drugs.next()) {
					ps = db
							.prepareStatement("select CASE "
									+ "WHEN b.pills=0 THEN COALESCE((a.received - b.issued), a.received, -b.issued, 0) "
									+ "WHEN b.pills>0 THEN COALESCE((a.received - (b.issued+1)), a.received, -(b.issued+1), 0) "
									+ "ELSE 0 "
									+ "END as opening "
									+ ", COALESCE(c.received,0) as received, COALESCE(d.issued,0) as issued , COALESCE(d.pill,0) as pills, "
									+

							"CASE WHEN b.pills=0 THEN 0 "
									+ "WHEN b.pills>0 THEN ?-b.pills "
									+ "ELSE 0 "
									+ "END "
									+ "as openingpills, "
									+

							"COALESCE(e.issued,0) as destroyed , COALESCE(e.pill,0) as destroyedpills "
									+

							"from (select sum(s.unitsreceived) as received "
									+ "from drug as d, stock as s "
									+ "where s.drug = d.id and d.id = ? and s.stockCenter = "
									+ "? and s.datereceived < ? "
									+ ") as a, "
									+

							"(select round(floor(sum(pd.amount::real/d.packsize::real))::numeric,0) as issued, "
									+ "round((((sum(pd.amount::real/d.packsize::real)) - "
									+ "floor(sum(pd.amount::real/d.packsize::real)))*? "
									+ ")::numeric,0) as pills "
									+ "from drug as d, stock as s, packageddrugs as pd, package as p "
									+ "where d.id = ? "
									+ " and s.stockCenter = ? "
									+ " and s.drug = d.id and pd.stock = s.id "
									+ "and pd.parentpackage = p.id "
									+ "and p.packdate < ? "
									+ ") as b, "
									+

							"(select sum(s.unitsreceived) as received from drug as d, stock as s "
									+ "where d.id = ? "
									+ " and s.stockCenter = ? "
									+ " and s.drug = d.id "
									+ "and s.datereceived between ?"
									+ "::timestamp and ?"
									+ "::timestamp) as c, "
									+

							"(select round(floor(sum(pd.amount::real/d.packsize::real))::numeric,0) as issued,"
									+ " round((((sum(pd.amount::real/d.packsize::real)) - "
									+ "floor(sum(pd.amount::real/d.packsize::real)))*? "
									+ ")::numeric,0) as pill "
									+ "from drug as d, stock as s, packageddrugs as pd, "
									+ "package as p,prescription as pre "
									+ "where d.id = ? "
									+ " and s.stockCenter = ? "
									+ " and s.drug = d.id and "
									+ "pd.stock = s.id and "
									+ "pd.parentpackage = p.id "
									+ "and p.prescription = pre.id "
									+ "and p.packdate between ?"
									+ "::timestamp and ?"
									+ "::timestamp) as d, "
									+

							"(select round(floor(sum(pd.amount::real/d.packsize::real))::numeric,0) as issued,"
									+ "round((((sum(pd.amount::real/d.packsize::real)) - "
									+ "floor(sum(pd.amount::real/d.packsize::real)))*? "
									+ ")::numeric,0) as pill "
									+ "from drug as d, stock as s, "
									+ "packageddrugs as pd, package as p "
									+ "where d.id = ? "
									+ " and s.stockCenter = ? "
									+ " and s.drug = d.id and "
									+ "pd.stock = s.id and "
									+ "pd.parentpackage = p.id "
									+ "and p.prescription is null "
									+ "and p.packdate between ?"
									+ "::timestamp and ?" + "::timestamp) as e");

					ps.setInt(1, drugs.getInt(3));
					ps.setInt(2, drugs.getInt(1));
					ps.setInt(3, stockCenter.getId());
					ps.setInt(5, drugs.getInt(3));
					ps.setInt(6, drugs.getInt(1));
					ps.setInt(7, stockCenter.getId());
					ps.setInt(9, drugs.getInt(1));
					ps.setInt(10, stockCenter.getId());
					ps.setInt(13, drugs.getInt(3));
					ps.setInt(14, drugs.getInt(1));
					ps.setInt(15, stockCenter.getId());
					ps.setInt(18, drugs.getInt(3));
					ps.setInt(19, drugs.getInt(1));
					ps.setInt(20, stockCenter.getId());
					ps.setString(4, monthStart);
					ps.setString(8, monthStart);
					ps.setString(11, monthStart);
					ps.setString(12, monthEnd);
					ps.setString(16, monthStart);
					ps.setString(17, monthEnd);
					ps.setString(21, monthStart);
					ps.setString(22, monthEnd);

					ResultSet values = ps.executeQuery();

					if (values != null) {

						while (values.next()) {
							Object[] monthlyStats = new Object[9];
							monthlyStats[0] = drugs.getString(2);
							monthlyStats[1] = values.getInt(1);
							monthlyStats[2] = values.getInt(5);
							monthlyStats[3] = values.getInt(2);
							monthlyStats[4] = values.getInt(3);
							monthlyStats[5] = values.getInt(4);
							monthlyStats[6] = values.getInt(6);
							monthlyStats[7] = values.getInt(7);
							monthlyStats[8] = drugs.getInt(3);

							// add each entry to list
							results.add(monthlyStats);

						}
					}
				}
			}
			JDBCUtil.closeJDBCConnection();

		} catch (SQLException e) {
			log
					.error(
							"SQLException while retrieving the monthly receipts and issues data",
							e);

		}

		return results;
	}

	/**
	 * Method getDailyDispensingTotalsData.
	 * 
	 * @param clinic
	 *            Clinic
	 * @param stockCenter
	 *            stockCenter
	 * @param theSQLDate
	 *            java.sql.Timestamp
	 * @return List<Object[]>
	 */
	public static List<Object[]> getDailyDispensingTotalsData(Clinic clinic,
			java.sql.Timestamp theSQLDate) {

		List<Object[]> results = new ArrayList<Object[]>();
		int monthIndex = 0;

		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

		try {

			// Get database connection
			Connection db = JDBCUtil.currentSession();

			PreparedStatement ps;

			ps = db
					.prepareStatement("select date_part('month', ?)::integer as monthindex, "
							+ "count(a.patient) as patientsstarting, "
							+ "count (b.patient) as patientstotal "
							+ "from (select distinct patient from "
							+ "prescription, patient, clinic "
							+ "where patient.id=prescription.patient "
							+ "and clinic.id = patient.clinic "
							+ "and clinic.clinicName like ? "
							+ "and prescription.id in "
							+ "(select pre.id as preid from prescription as pre, "
							+ "prescribeddrugs as pd, "
							+ "drug as d "
							+ "where (((enddate is null and date < (? + interval '1 month')) "
							+ "or ((? + interval '1 month') between date "
							+ "and enddate))) "
							+ "and pd.prescription = pre.id "
							+ "and pd.drug = d.id "
							+ "and d.sidetreatment = 'F')) as b "
							+

							"left outer join "
							+ "(select patient, min(pickupdate) from "
							+ "package,prescription, patient, clinic, episode "
							+ "where prescription.id = package.prescription "
							+ "and clinic.id = patient.clinic "
							+ "and clinic.clinicName like ? "
							+ "and patient.id = prescription.patient "
							+ "and patient.id = episode.patient "
							+ "and episode.startReason like 'New Patient' "
							+ "and package.id in "
							+ "(select distinct packageddrugs.parentpackage "
							+ "from packageddrugs, stock, drug "
							+ "where packageddrugs.stock=stock.id "
							+ "and stock.drug = drug.id "
							+ "and drug.sidetreatment like 'F') "
							+ "group by patient having min(pickupdate) "
							+ "between ? and (? + interval '1 month')) as a "
							+ "on a.patient = b.patient");

			ps.setTimestamp(1, theSQLDate);
			ps.setObject(2, clinic.getClinicName());
			ps.setTimestamp(3, theSQLDate);
			ps.setTimestamp(4, theSQLDate);
			ps.setObject(5, clinic.getClinicName());
			ps.setTimestamp(6, theSQLDate);
			ps.setTimestamp(7, theSQLDate);

			ResultSet values = ps.executeQuery();

			if (values != null) {
				values.next();
				monthIndex = values.getInt(1);
			}

			ps = db
					.prepareStatement("SELECT a.daterec, "
							+ "coalesce(b.patients,0) as patients, "
							+ "coalesce(b.items::integer,0) as items, "
							+ "coalesce(c.patients,0) as patients2, "
							+ "coalesce(c.items::integer,0) as items2 "
							+

					"FROM "
							+ "(SELECT (? + x * interval'1 day') ::date as daterec "
							+ "FROM generate_series(0, 31) AS g(x) "
							+ "where "
							+ "date_part('month',?)=date_part('month',(? + x * interval'1 day') ::date)) as a "
							+

					"left outer join "
							+

					"(select package.datereceived::date as daterec, "
							+ "coalesce(count(distinct patientid),0) as patients, "
							+ "coalesce(floor(sum(amount::real/packsize::real)),0) as items, "
							+ "coalesce(sum(amount/packsize) - ((floor(sum(amount::real/packsize::real)))),0) as pills "
							+

					"from packageddrugs, package, prescription, "
							+ "patient, stock, drug, clinic "
							+ "where packageddrugs.parentpackage=package.id "
							+ "and package.prescription = prescription.id "
							+ "and prescription.patient = patient.id "
							+ "and packageddrugs.stock = stock.id "
							+ "and stock.drug = drug.id "
							+ "and (drug.sidetreatment like 'f' or drug.sidetreatment like 'F') "
							+ "and patient.clinic = clinic.id "
							+ "and clinic.clinicName like ? "
							+

					"group by "
							+ "package.datereceived::date) as b "
							+ "on (b.daterec::date = a.daterec::date and date_part('month',a.daterec)::integer=?) "
							+

					"left outer join "
							+

					"(select package.datereceived::date as daterec, "
							+ "coalesce(count(distinct patientid),0) as patients, "
							+ "coalesce(floor(sum(amount::real/packsize::real)),0) as items, "
							+ "coalesce(sum(amount/packsize) - ((floor(sum(amount::real/packsize::real)))),0) as pills "
							+ "from packageddrugs, package, prescription, "
							+ "patient, stock, drug, clinic "
							+ "where packageddrugs.parentpackage=package.id "
							+ "and package.prescription = prescription.id "
							+ "and prescription.patient = patient.id "
							+ "and packageddrugs.stock = stock.id "
							+ "and stock.drug = drug.id "
							+ "and (drug.sidetreatment like 't' or drug.sidetreatment like'T') "
							+ "and patient.clinic = clinic.id "
							+ "and clinic.clinicName like ? "
							+ "group by package.datereceived::date) as c "
							+ "on (c.daterec::date = b.daterec::date and date_part('month',b.daterec)::integer=?) "
							+ "order by a.daterec");

			ps.setTimestamp(1, theSQLDate);
			ps.setTimestamp(2, theSQLDate);
			ps.setTimestamp(3, theSQLDate);
			ps.setObject(4, clinic.getClinicName());
			ps.setInt(5, monthIndex);
			ps.setObject(6, clinic.getClinicName());
			ps.setInt(7, monthIndex);

			ResultSet data = ps.executeQuery();

			if (data != null) {

				while (data.next()) {
					Object[] monthlyStats = new Object[6];
					monthlyStats[0] = data.getDate(1);
					monthlyStats[1] = sdf.format(data.getDate(1));
					monthlyStats[2] = data.getInt(2);
					monthlyStats[3] = data.getInt(3);
					monthlyStats[4] = data.getInt(4);
					monthlyStats[5] = data.getInt(5);
					results.add(monthlyStats);

				}
			}

			// Close session
			JDBCUtil.closeJDBCConnection();
		} catch (Exception e) {
			log.error(e);
		}

		return results;
	}

}
