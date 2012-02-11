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

package org.celllife.idart.gui.reportParameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.PAVASManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientStatTypes;
import org.celllife.idart.database.hibernate.PatientStatistic;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.SafeSaveDialog;
import org.celllife.idart.misc.SafeSaveDialog.FileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class CohortsReport extends GenericReportGui {

	private Group grpDateInfo;

	private Label lblInstructions;

	private CCombo cmbMonth;

	private CCombo cmbYear;

	private Button btnExcelReport;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public CohortsReport(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_STOCK, activate);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		Rectangle bounds = new Rectangle(100, 50, 600, 510);
		String shellTxt = "Cohorts Report";
		buildShell(shellTxt, bounds);
		// create the composites
		createMyGroups();
	}

	private FileWriter write;

	private void createMyGroups() {
		createGrpClinicSelection();
		createGrpDateInfo();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Cohorts Report";
		iDartImage icoImage = iDartImage.PAVAS;
		buildCompdHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {

	}

	/**
	 * This method initializes grpDateInfo
	 * 
	 */
	private void createGrpDateInfo() {

		grpDateInfo = new Group(getShell(), SWT.NONE);
		grpDateInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(160, 180,
				280, 100));

		lblInstructions = new Label(grpDateInfo, SWT.NONE);
		lblInstructions.setBounds(new org.eclipse.swt.graphics.Rectangle(60,
				20, 160, 20));
		lblInstructions.setText("Select a Month and Year:");
		lblInstructions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbMonth = new CCombo(grpDateInfo, SWT.BORDER);
		cmbMonth.setBounds(new org.eclipse.swt.graphics.Rectangle(40, 50, 100,
				20));
		cmbMonth.setEditable(false);
		cmbMonth.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		String qs[] = { "Quarter 1", "Quarter 2", "Quarter 3", "Quarter 4" };
		for (int i = 0; i < 4; i++) {
			this.cmbMonth.add(qs[i]);
		}

		int intMonth = 1;
		cmbMonth.setText("Quarter " + intMonth);
		cmbMonth.setEditable(false);
		cmbMonth.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbMonth.setVisibleItemCount(4);

		// cmdYear
		cmbYear = new CCombo(grpDateInfo, SWT.BORDER);
		cmbYear.setBounds(new org.eclipse.swt.graphics.Rectangle(160, 50, 80,
				20));
		cmbYear.setEditable(false);
		cmbYear.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbYear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// get the current date12
		Calendar rightNow = Calendar.getInstance();
		int currentYear = rightNow.get(Calendar.YEAR);
		for (int i = currentYear - 2; i <= currentYear + 1; i++) {
			this.cmbYear.add(Integer.toString(i));
		}
		cmbYear.setText(String.valueOf(Calendar.getInstance()
				.get(Calendar.YEAR)));

	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
		btnExcelReport = new Button(getCompButtons(), SWT.NONE);
		btnExcelReport.setText("Excel Report");
		btnExcelReport.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnExcelReport
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdExcelReportWidgetSelected();
			}
		});
	}

	@Override
	protected void cmdViewReportWidgetSelected() {
		boolean viewReport = true;

		if (viewReport) {

			SafeSaveDialog dlg = new SafeSaveDialog(getShell(), FileType.CSV);

			String fileName = "";

			try {
				fileName = dlg.open();
			} catch (Exception e) {
				getLog().error(e);
			}

			Boolean runner = true;
			if (fileName == null) {
				fileName = "";
			}
			if (!fileName.equals("")) {

				File dstFile = new File(fileName);

				if (dstFile.exists()) {
					MessageBox exists = new MessageBox(getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					exists.setText("File Exists");
					exists.setMessage("The file " + fileName
							+ " already exists - please choose another name.");
					exists.open();
					runner = false;
				}

				if (dstFile.isDirectory()
						|| (dstFile.exists() && !dstFile.canWrite())) {
					MessageBox writable = new MessageBox(getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					writable.setText("File Not Writable");
					writable.setMessage("The file " + fileName
							+ " is not writable.");
					writable.open();
					runner = false;
				}

			}
			if (!fileName.equals("") && runner == true) {

				runReport(fileName);

				MessageBox success = new MessageBox(getShell(),
						SWT.ICON_INFORMATION | SWT.OK);
				success.setText("Report completed successfully");
				success.setMessage("The report has been run successfully.");
				success.open();
			}
		}
	}

	/**
	 * This method is called when the user presses "Close" button
	 * 
	 */
	@Override
	protected void cmdCloseWidgetSelected() {
		cmdCloseSelected();
	}

	/**
	 * This method is called when the user presses "Excel" button
	 * 
	 */
	private void cmdExcelReportWidgetSelected() {

	}

	protected void runReport(String filename) {
		// get the following
		write = null;
		try {
			write = new FileWriter(filename);

		} catch (IOException e) {
			getLog().error(e);
		}
		
		BufferedWriter bw = new BufferedWriter(write);
		PrintWriter pw = new PrintWriter(bw);

		// <Report Title> - 'Patient Visits Report'
		pw.println("Cohorts Report");

		// <Clinics> - get details from admin manager site
		String MyClinics = "";
		List<String> clinics = AdministrationManager
		.getClinicNames(HibernateUtil.getNewSession());
		for (int j = 0; j < clinics.size(); j++) {
			if (!MyClinics.equals("")) {
				MyClinics = MyClinics + " : ";
			}
			MyClinics = MyClinics + clinics.get(j);
		}
		pw.println(MyClinics);

		// <Start Date> - from the parameter

		// do 6 months
		// work out start date
		// work out end date
		boolean all6months = false;
		int cd4statid = 0;
		List<PatientStatTypes> pst = PAVASManager.getStatTypes(getHSession());
		for (int i = 0; i < pst.size(); i++) {
			if (pst.get(i).getstatname().equals("CD4 Count")) {
				cd4statid = pst.get(i).getId();
			}
		}

		String startmonth = "";
		String endmonth = "";
		String startyear = "";
		String endyear = "";
		if (cmbMonth.getText().equals("Quarter 1")) {
			startmonth = "FEB";
			endmonth = "MAY";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 1;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 1;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}
		if (cmbMonth.getText().equals("Quarter 2")) {
			startmonth = "MAY";
			endmonth = "AUG";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 1;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 1;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}
		if (cmbMonth.getText().equals("Quarter 3")) {
			startmonth = "AUG";
			endmonth = "NOV";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 1;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 1;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}
		if (cmbMonth.getText().equals("Quarter 4")) {
			startmonth = "NOV";
			endmonth = "FEB";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 1;
			Integer endintyear = Integer.parseInt(cmbYear.getText());
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}

		Date theStartDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
		String strTheStartDate = "" + startyear + "-" + startmonth + "-01";

		try {
			theStartDate = sdf.parse(strTheStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Date theEndDate = new Date();
		String strTheEndDate = "" + endyear + "-" + endmonth + "-01";

		try {
			theEndDate = sdf.parse(strTheEndDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// get query relating to episodes started between those dates for new
		// patient episode type - return all those patients over 6 years old -
		int nopatients = 0;
		List<Episode> newepisodes = PAVASManager.getCohort(HibernateUtil
				.getNewSession(), theStartDate, theEndDate);
		double[] basecd4list = new double[newepisodes.size() + 1];
		double[] sixmonthscd4list = new double[newepisodes.size() + 1];
		int nowithbasecd4 = 0;
		int nowithsixmonthscd4 = 0;
		int noafter6months = 0;
		int noall6months = 0;
		double basecd4median = 0;
		double sixmonthscd4median = 0;
		double dummyvar = 0;
		for (int j = 0; j < newepisodes.size(); j++) {

			Patient pat = newepisodes.get(j).getPatient();
			// pw.println(pat.getFirstNames() + " : " + pat.getPatientId());
			if (pat.getAge() > 6) {
				nopatients = nopatients + 1;
				// query 1
				// loop through patients to find : -
				// a) CD4 count before episode start date and date of CD4 count

				// put episode start date,patient into patientstatistic
				Date epistart = newepisodes.get(j).getStartDate();
				Date earliestdate = newepisodes.get(j).getStartDate();
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(earliestdate);
				gc.add(Calendar.DAY_OF_YEAR, -42);
				earliestdate = gc.getTime();

				List<PatientStatistic> firstcd4 = PAVASManager
				.getStatBetweenDates(HibernateUtil.getNewSession(), pat
						.getId(), cd4statid, earliestdate, epistart);

				if (firstcd4.size() > 0) {
					nowithbasecd4 = nowithbasecd4 + 1;
					basecd4list[nowithbasecd4] = Double.parseDouble(firstcd4
							.get(0).getstattext());
				}

				// - query 2
				// b) how many have an episode stop date of any type except
				// defaulted or lost to follow up -

				// put epistart date + 6 months ,patient into episode
				Date sixmonthsdate = newepisodes.get(j).getStartDate();
				gc.setTime(sixmonthsdate);
				gc.add(Calendar.MONTH, +6);
				sixmonthsdate = gc.getTime();
				all6months = true;

				List<Episode> earlierepisodes = PAVASManager
				.getEarlierEpisodes(HibernateUtil.getNewSession(), pat
						.getId(), sixmonthsdate);

				for (int ep = 0; ep < earlierepisodes.size(); ep++) {
					if (earlierepisodes.get(ep).isOpen()) {
						noafter6months = noafter6months + 1;
					} else {
						if (earlierepisodes.get(ep).getStopDate().after(
								sixmonthsdate)) {
							noafter6months = noafter6months + 1;
						} else {
							if (earlierepisodes.get(ep).getStopReason().equals(
							"Transferred Out")) {
								all6months = false;
							}
							if (earlierepisodes.get(ep).getStopReason().equals(
							"Defaulted")) {
								all6months = false;
							}
						}
					}
				}

				if (all6months == true) {
					noall6months = noall6months + 1;
				}
				// query 4
				// d) how many have a CD4 count within 6 months & 6 weeks of
				// initial
				// cd4 // check between 5 months and 7 months from epistartdate
				// for
				// CD4

				gc.setTime(sixmonthsdate);
				gc.add(Calendar.MONTH, -1);
				Date fivemonthsdate = gc.getTime();
				gc.setTime(sixmonthsdate);
				gc.add(Calendar.MONTH, 1);
				Date sevenmonthsdate = gc.getTime();

				List<PatientStatistic> sixmonthscd4 = PAVASManager
				.getStatBetweenDates(HibernateUtil.getNewSession(), pat
						.getId(), cd4statid, fivemonthsdate,
						sevenmonthsdate);

				if (sixmonthscd4.size() > 0) {
					// count and its value (put in another list)
					nowithsixmonthscd4 = nowithsixmonthscd4 + 1;
					sixmonthscd4list[nowithsixmonthscd4] = Double
					.parseDouble(sixmonthscd4.get(0).getstattext());
				}

				// end loop
			}

		}
		// get median basecd4s from list
		if (nowithbasecd4 > 1) {
			for (int m = 1; m < (nowithbasecd4 + 1); m++) {
				for (int n = m + 1; n < nowithbasecd4 + 1; n++) {
					if (basecd4list[m] > basecd4list[n]) {
						dummyvar = basecd4list[m];
						basecd4list[m] = basecd4list[n];
						basecd4list[n] = dummyvar;
					}
				}
			}
			int mid = (nowithbasecd4 / 2);
			if (nowithbasecd4 % 2 != 0) {
				basecd4median = basecd4list[mid + 1];
			} else {
				basecd4median = (basecd4list[mid] + basecd4list[mid + 1]) / 2;
			}

		} else {
			if (nowithbasecd4 == 1) {
				basecd4median = basecd4list[1];
			}
		}

		// get six months basecd4s from list
		if (nowithsixmonthscd4 > 1) {
			for (int m = 1; m < (nowithsixmonthscd4 + 1); m++) {
				for (int n = m + 1; n < nowithsixmonthscd4 + 1; n++) {
					if (sixmonthscd4list[m] > sixmonthscd4list[n]) {
						dummyvar = sixmonthscd4list[m];
						sixmonthscd4list[m] = sixmonthscd4list[n];
						sixmonthscd4list[n] = dummyvar;
					}
				}
			}
			int mid = (nowithsixmonthscd4 / 2);
			if (nowithsixmonthscd4 % 2 != 0) {
				sixmonthscd4median = sixmonthscd4list[mid + 1];
			} else {
				sixmonthscd4median = (sixmonthscd4list[mid] + sixmonthscd4list[mid + 1]) / 2;
			}

		} else {
			if (nowithsixmonthscd4 == 1) {
				sixmonthscd4median = sixmonthscd4list[1];
			}
		}

		pw.println(cmbMonth.getText() + " : " + cmbYear.getText());
		pw.println("");
		pw.println("6 Months Cohort");
		pw.println(",Baseline,6 Months");
		pw.println("Number of individuals in cohort (6 months)," + nopatients
				+ "," + noafter6months);
		pw.println("Number in cohort who have CD4+ counts (6 months),"
				+ nowithbasecd4 + "," + nowithsixmonthscd4);
		pw.println("Median CD4+ count for cohort (6 months)	," + basecd4median
				+ "," + sixmonthscd4median);
		pw
		.println("Number in cohort who received ARV's for 6 out of 6 months,,"
				+ noall6months);

		// do 12 months
		boolean all12months = false;

		startmonth = "";
		endmonth = "";
		startyear = "";
		endyear = "";
		if (cmbMonth.getText().equals("Quarter 1")) {
			startmonth = "AUG";
			endmonth = "NOV";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 2;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 2;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}
		if (cmbMonth.getText().equals("Quarter 2")) {
			startmonth = "NOV";
			endmonth = "FEB";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 2;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 1;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}
		if (cmbMonth.getText().equals("Quarter 3")) {
			startmonth = "FEB";
			endmonth = "MAY";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 1;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 1;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}
		if (cmbMonth.getText().equals("Quarter 4")) {
			startmonth = "MAY";
			endmonth = "AUG";
			Integer startintyear = Integer.parseInt(cmbYear.getText()) - 1;
			Integer endintyear = Integer.parseInt(cmbYear.getText()) - 1;
			startyear = startintyear.toString();
			endyear = endintyear.toString();
		}

		theStartDate = new Date();
		strTheStartDate = "" + startyear + "-" + startmonth + "-01";

		try {
			theStartDate = sdf.parse(strTheStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		theEndDate = new Date();
		strTheEndDate = "" + endyear + "-" + endmonth + "-01";

		try {
			theEndDate = sdf.parse(strTheEndDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// get query relating to episodes started between those dates for new
		// patient episode type - return all those patients over 6 years old -
		nopatients = 0;
		newepisodes = PAVASManager.getCohort(HibernateUtil.getNewSession(),
				theStartDate, theEndDate);
		double[] basecd4list12 = new double[newepisodes.size() + 1];
		double[] twelvemonthscd4list = new double[newepisodes.size() + 1];
		int nowithbasecd412 = 0;
		int nowithtwelvemonthscd4 = 0;
		int noafter12months = 0;
		int noall12months = 0;
		double basecd4median12 = 0;
		double twelvemonthscd4median = 0;
		dummyvar = 0;
		for (int j = 0; j < newepisodes.size(); j++) {

			Patient pat = newepisodes.get(j).getPatient();
			// pw.println(pat.getFirstNames() + " : " + pat.getPatientId());
			if (pat.getAge() > 6) {
				nopatients = nopatients + 1;
				// query 1
				// loop through patients to find : -
				// a) CD4 count before episode start date and date of CD4 count

				// put episode start date,patient into patientstatistic
				Date epistart = newepisodes.get(j).getStartDate();
				Date earliestdate = newepisodes.get(j).getStartDate();
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(earliestdate);
				gc.add(Calendar.DAY_OF_YEAR, -42);
				earliestdate = gc.getTime();

				List<PatientStatistic> firstcd4 = PAVASManager
				.getStatBetweenDates(HibernateUtil.getNewSession(), pat
						.getId(), cd4statid, earliestdate, epistart);

				if (firstcd4.size() > 0) {
					nowithbasecd412 = nowithbasecd412 + 1;
					basecd4list12[nowithbasecd412] = Double
					.parseDouble(firstcd4.get(0).getstattext());
				}

				// - query 2
				// b) how many have an episode stop date of any type except
				// defaulted or lost to follow up -

				// put epistart date + 6 months ,patient into episode
				Date twelvemonthsdate = newepisodes.get(j).getStartDate();
				gc.setTime(twelvemonthsdate);
				gc.add(Calendar.MONTH, +12);
				twelvemonthsdate = gc.getTime();
				all12months = true;

				List<Episode> earlierepisodes = PAVASManager
				.getEarlierEpisodes(HibernateUtil.getNewSession(), pat
						.getId(), twelvemonthsdate);

				for (int ep = 0; ep < earlierepisodes.size(); ep++) {
					if (earlierepisodes.get(ep).isOpen()) {
						noafter12months = noafter12months + 1;
					} else {
						if (earlierepisodes.get(ep).getStopDate().after(
								twelvemonthsdate)) {
							noafter12months = noafter12months + 1;
						} else {
							if (earlierepisodes.get(ep).getStopReason().equals(
							"Transferred Out")) {
								all12months = false;
							}
							if (earlierepisodes.get(ep).getStopReason().equals(
							"Defaulted")) {
								all12months = false;
							}
						}
					}
				}

				if (all12months == true) {
					noall12months = noall12months + 1;
				}
				// query 4
				// d) how many have a CD4 count within 6 months & 6 weeks of
				// initial
				// cd4 // check between 5 months and 7 months from epistartdate
				// for
				// CD4

				gc.setTime(twelvemonthsdate);
				gc.add(Calendar.MONTH, -1);
				Date elevenmonthsdate = gc.getTime();
				gc.setTime(twelvemonthsdate);
				gc.add(Calendar.MONTH, 1);
				Date thirteenmonthsdate = gc.getTime();

				List<PatientStatistic> twelvemonthscd4 = PAVASManager
				.getStatBetweenDates(HibernateUtil.getNewSession(), pat
						.getId(), cd4statid, elevenmonthsdate,
						thirteenmonthsdate);

				if (twelvemonthscd4.size() > 0) {
					// count and its value (put in another list)
					nowithtwelvemonthscd4 = nowithtwelvemonthscd4 + 1;
					twelvemonthscd4list[nowithtwelvemonthscd4] = Double
					.parseDouble(twelvemonthscd4.get(0).getstattext());
				}

				// end loop
			}

		}
		// get median basecd4s from list
		if (nowithbasecd412 > 1) {
			for (int m = 1; m < (nowithbasecd412 + 1); m++) {
				for (int n = m + 1; n < nowithbasecd412 + 1; n++) {
					if (basecd4list12[m] > basecd4list12[n]) {
						dummyvar = basecd4list12[m];
						basecd4list12[m] = basecd4list12[n];
						basecd4list12[n] = dummyvar;
					}
				}
			}
			int mid = (nowithbasecd412 / 2);
			if (nowithbasecd412 % 2 != 0) {
				basecd4median12 = basecd4list12[mid + 1];
			} else {
				basecd4median12 = (basecd4list12[mid] + basecd4list12[mid + 1]) / 2;
			}

		} else {
			if (nowithbasecd412 == 1) {
				basecd4median12 = basecd4list12[1];
			}
		}

		// get six months basecd4s from list
		if (nowithtwelvemonthscd4 > 1) {
			for (int m = 1; m < (nowithtwelvemonthscd4 + 1); m++) {
				for (int n = m + 1; n < nowithtwelvemonthscd4 + 1; n++) {
					if (twelvemonthscd4list[m] > twelvemonthscd4list[n]) {
						dummyvar = twelvemonthscd4list[m];
						twelvemonthscd4list[m] = twelvemonthscd4list[n];
						twelvemonthscd4list[n] = dummyvar;
					}
				}
			}
			int mid = (nowithtwelvemonthscd4 / 2);
			if (nowithtwelvemonthscd4 % 2 != 0) {
				twelvemonthscd4median = twelvemonthscd4list[mid + 1];
			} else {
				twelvemonthscd4median = (twelvemonthscd4list[mid] + twelvemonthscd4list[mid + 1]) / 2;
			}

		} else {
			if (nowithtwelvemonthscd4 == 1) {
				twelvemonthscd4median = twelvemonthscd4list[1];
			}
		}

		pw.println("");
		pw.println("12 Months Cohort");
		pw.println(",Baseline,12 Months");
		pw.println("Number of individuals in cohort (12 months)," + nopatients
				+ "," + noafter12months);
		pw.println("Number in cohort who have CD4+ counts (12 months),"
				+ nowithbasecd412 + "," + nowithtwelvemonthscd4);
		pw.println("Median CD4+ count for cohort (12 months)	,"
				+ basecd4median12 + "," + twelvemonthscd4median);
		pw
		.println("Number in cohort who received ARV's for 12 out of 12 months,,"
				+ noall12months);

		//
		pw.close();
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}