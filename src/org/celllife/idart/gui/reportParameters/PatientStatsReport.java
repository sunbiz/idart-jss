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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.PAVASManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.PatientStatTypes;
import org.celllife.idart.database.hibernate.PatientStatistic;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.SafeSaveDialog;
import org.celllife.idart.misc.SafeSaveDialog.FileType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class PatientStatsReport extends GenericReportGui {

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private Group grpDateRange;

	private Label lblStartDate;

	private Label lblEndDate;

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
	"dd MMM yyyy");

	private FileWriter write;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */

	public PatientStatsReport(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_MONITORINGANDEVALUATION, activate);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		buildShell("Patient Statistics Report", new Rectangle(70, 50,
				700, 470));
		// create the composites
		createMyGroups();
	}

	private void createMyGroups() {
		createGrpDateRange();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Patient Statistics Report";
		iDartImage icoImage = iDartImage.PAVASSTATS;
		buildCompdHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(68, 100, 520, 201));
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblStartDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblStartDate.setBounds(new org.eclipse.swt.graphics.Rectangle(40, 30,
				180, 20));
		lblStartDate.setText("Select a START date:");
		lblStartDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblEndDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblEndDate.setBounds(new org.eclipse.swt.graphics.Rectangle(300, 30,
				180, 20));
		lblEndDate.setText("Select an END date:");
		lblEndDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		calendarStart = new SWTCalendar(grpDateRange);
		calendarStart.setBounds(20, 55, 220, 140);

		calendarEnd = new SWTCalendar(grpDateRange);
		calendarEnd.setBounds(280, 55, 220, 140);

		btnViewReport.setText("Create Report");

	}

	/**
	 * Method getCalendarStart.
	 * 
	 * @return Calendar
	 */
	public Calendar getCalendarStart() {
		return calendarStart.getCalendar();
	}

	/**
	 * Method getCalendarEnd.
	 * 
	 * @return Calendar
	 */
	public Calendar getCalendarEnd() {
		return calendarEnd.getCalendar();
	}

	/**
	 * Method setStartDate.
	 * 
	 * @param date
	 *            Date
	 */
	public void setStartDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendarStart.setCalendar(calendar);
	}

	/**
	 * Method setEndDate.
	 * 
	 * @param date
	 *            Date
	 */
	public void setEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendarEnd.setCalendar(calendar);
	}

	/**
	 * Method addStartDateChangedListener.
	 * 
	 * @param listener
	 *            SWTCalendarListener
	 */
	public void addStartDateChangedListener(SWTCalendarListener listener) {

		calendarStart.addSWTCalendarListener(listener);
	}

	/**
	 * Method addEndDateChangedListener.
	 * 
	 * @param listener
	 *            SWTCalendarListener
	 */
	public void addEndDateChangedListener(SWTCalendarListener listener) {

		calendarEnd.addSWTCalendarListener(listener);
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
	}

	@Override
	protected void cmdViewReportWidgetSelected() {

		boolean viewReport = true;

		if (!PAVASManager.checkValidEndDate(calendarStart.getCalendar()
				.getTime(), calendarEnd.getCalendar().getTime())) {

			MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
			mb.setText("Invalid End Date");
			mb.setMessage("Please select an end date after the start date");
			mb.open();

			viewReport = false;
		}

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

		// <Report Title> - 'Patient Statistics Report'
		pw.println("Patient Statistics Report");

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
		String mystartdate = dateFormatter.format(calendarStart.getCalendar()
				.getTime());
		pw.println(mystartdate);

		// <End Date> - from the parameter
		String myenddate = dateFormatter.format(calendarEnd.getCalendar()
				.getTime());
		pw.println(myenddate);

		String statname = "";
		List<PatientStatTypes> pst = PAVASManager.getStatTypes(HibernateUtil
				.getNewSession());
		for (int j = 0; j < pst.size(); j++) {
			statname = pst.get(j).getstatname();
			// Get statistics for StatName - get number of stats between dates -
			// get mean and median
			long NoOfStats = PAVASManager.getNumberofStats(HibernateUtil
					.getNewSession(), pst.get(j).getId(), calendarStart
					.getCalendar().getTime(), calendarEnd.getCalendar()
					.getTime());
			String mm = "";
			if (NoOfStats > 0 && pst.get(j).getstatformat().equals("N")) {
				double Statsmean = PAVASManager.getStatsMean(HibernateUtil
						.getNewSession(), pst.get(j).getId(), calendarStart
						.getCalendar().getTime(), calendarEnd.getCalendar()
						.getTime());
				double Statsmedian = PAVASManager.getStatsMedian(HibernateUtil
						.getNewSession(), pst.get(j).getId(), NoOfStats,
						calendarStart.getCalendar().getTime(), calendarEnd
						.getCalendar().getTime());
				mm = "- Mean = " + Statsmean + " - Median = " + Statsmedian;
			}
			pw.println(statname + "( " + NoOfStats + " Stats " + mm + ")");
			//
		}

		// ----------
		// <Patient 1><Statistic Type><Statistic Value> - from PAVAS Manager
		pw.println(" --------------- ");
		pw
		.println("Patient_id, Statistic type, Statistic Value, Date Recorded, Date Tested");
		List<PatientStatistic> ps = PAVASManager.getStatsforAllPatients(
				HibernateUtil.getNewSession(), calendarStart.getCalendar()
				.getTime(), calendarEnd.getCalendar().getTime());
		for (int j = 0; j < ps.size(); j++) {
			statname = "";
			for (int i = 0; i < pst.size(); i++) {
				if (pst.get(i).getId() == ps.get(j).getstattype()) {
					statname = pst.get(i).getstatname();
				}
			}
			String mydaterecorded = dateFormatter.format(ps.get(j)
					.getdaterecorded().getTime());
			String mydatetested = dateFormatter.format(ps.get(j)
					.getdatetested().getTime());
			pw.println(PAVASManager.getpatid(HibernateUtil.getNewSession(), ps
					.get(j).getpatient_id())
					+ ","
					+ statname
					+ ","
					+ ps.get(j).getstattext().trim()
					+ "," + mydaterecorded + "," + mydatetested);
		}

		pw.close();

	}

	/**
	 * This method is called when the user presses "Close" button
	 * 
	 */
	@Override
	protected void cmdCloseWidgetSelected() {
		cmdCloseSelected();
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
