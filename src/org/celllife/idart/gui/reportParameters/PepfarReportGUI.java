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

import java.util.Calendar;
import java.util.Date;

import model.manager.AdministrationManager;
import model.manager.reports.PepfarReport;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class PepfarReportGUI extends GenericReportGui {

	private Group grpClinicSelection;

	private Label lblClinic;

	private CCombo cmbClinic;

	private Label lblWaitWhileLoading;

	private Label lblYoungCutoffAge;

	private Text txtYoungCutoffAge;

	private Text txtCutoffAge;

	private Label lblCutoffAge;

	private Label lblCuttoffYears;

	private Label lblYoungCutoffYears;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private Group grpDateRange;

	private Label lblStartDate;

	private Label lblEndDate;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */

	public PepfarReportGUI(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_MONITORINGANDEVALUATION, activate);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		buildShell(REPORT_PEPFAR, new Rectangle(70, 50, 700, 600));
		// create the composites
		createMyGroups();
		lblWaitWhileLoading = new Label(getShell(), SWT.CENTER);
		lblWaitWhileLoading.setBounds(new Rectangle(104, 451, 448, 21));
		lblWaitWhileLoading
		.setText("This report takes a while to load - please be patient.");
		lblWaitWhileLoading
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	private void createMyGroups() {
		createGrpClinicSelection();
		createGrpDateRange();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		iDartImage icoImage = iDartImage.REPORT_ACTIVEPATIENTS;
		buildCompdHeader(REPORT_PEPFAR, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {

		grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setText("");
		grpClinicSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpClinicSelection.setBounds(new Rectangle(151, 83, 386, 123));

		lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(9, 25, 151, 20));
		lblClinic.setText("Select Clinic:");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setBounds(new Rectangle(169, 25, 176, 20));
		cmbClinic.setEditable(false);
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateClinics(getHSession(), cmbClinic);

		lblYoungCutoffAge = new Label(grpClinicSelection, SWT.NONE);
		lblYoungCutoffAge.setBounds(new Rectangle(10, 57, 230, 21));
		lblYoungCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblYoungCutoffAge.setText("Cutoff Age for Young Paediatric Patients:");

		txtYoungCutoffAge = new Text(grpClinicSelection, SWT.BORDER);
		txtYoungCutoffAge.setBounds(new Rectangle(242, 57, 45, 20));
		txtYoungCutoffAge.setText("5");
		txtYoungCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblYoungCutoffYears = new Label(grpClinicSelection, SWT.NONE);
		lblYoungCutoffYears.setBounds(new Rectangle(295, 58, 50, 20));
		lblYoungCutoffYears.setText("years");
		lblYoungCutoffYears
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblCutoffAge = new Label(grpClinicSelection, SWT.NONE);
		lblCutoffAge.setBounds(new Rectangle(10, 86, 227, 20));
		lblCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCutoffAge.setText("Cutoff Age for Paediatric Patients:");

		txtCutoffAge = new Text(grpClinicSelection, SWT.BORDER);
		txtCutoffAge.setBounds(new Rectangle(243, 87, 43, 19));
		txtCutoffAge.setText("14");
		txtCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblCuttoffYears = new Label(grpClinicSelection, SWT.NONE);
		lblCuttoffYears.setBounds(new Rectangle(295, 87, 50, 20));
		lblCuttoffYears.setText("years");
		lblCuttoffYears.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(68, 231, 520, 201));
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

		if (cmbClinic.getText().equals("")) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Clinic Was Selected");
			missing
			.setMessage("No clinic was selected. Please select a clinic by looking through the list of available clinics.");
			missing.open();
			viewReport = false;

		}

		if (txtYoungCutoffAge.getText().equals("")) {
			MessageBox incorrectData = new MessageBox(getShell(),
					SWT.ICON_ERROR | SWT.OK);
			incorrectData.setText("Incorrect Numeric Value");
			incorrectData
			.setMessage("The minimum days late that was entered is incorrect. Please enter a number.");
			incorrectData.open();
			txtYoungCutoffAge.setText("");
			txtYoungCutoffAge.setFocus();

			viewReport = false;
		}

		if (!txtYoungCutoffAge.getText().equals("")) {
			try {
				Integer.parseInt(txtYoungCutoffAge.getText());
			} catch (NumberFormatException nfe) {
				MessageBox incorrectData = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);
				incorrectData.setText("Incorrect Numeric Value");
				incorrectData
				.setMessage("The minimum days late that was entered is incorrect. Please enter a number.");
				incorrectData.open();
				txtYoungCutoffAge.setText("");
				txtYoungCutoffAge.setFocus();

				viewReport = false;

			}
		}

		if (txtCutoffAge.getText().equals("")) {
			MessageBox incorrectData = new MessageBox(getShell(),
					SWT.ICON_ERROR | SWT.OK);
			incorrectData.setText("Incorrect Numeric Value");
			incorrectData
			.setMessage("The cutoff age that was entered is incorrect. Please enter a number.");
			incorrectData.open();
			txtCutoffAge.setText("");
			txtCutoffAge.setFocus();

			viewReport = false;
		}

		if (!txtCutoffAge.getText().equals("")) {
			try {
				Integer.parseInt(txtCutoffAge.getText());
			} catch (NumberFormatException nfe) {
				MessageBox incorrectData = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);
				incorrectData.setText("Incorrect Numeric Value");
				incorrectData
				.setMessage("The cutoff age that was entered is incorrect. Please enter a number.");
				incorrectData.open();
				txtCutoffAge.setText("");
				txtCutoffAge.setFocus();

				viewReport = false;

			}
		}

		if (calendarStart.getCalendar().getTime().after(
				calendarEnd.getCalendar().getTime())) {

			MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
			mb.setText("Invalid End Date");
			mb.setMessage("Please select an end date after the start date");
			mb.open();

			viewReport = false;
		}

		if (viewReport) {
			PepfarReport report = new PepfarReport(
					getShell(), calendarStart.getCalendar().getTime(),
					calendarEnd.getCalendar().getTime(), Integer
					.parseInt(txtCutoffAge.getText()), Integer
					.parseInt(txtYoungCutoffAge.getText()),
					AdministrationManager.getClinic(getHSession(), cmbClinic
							.getText()));
			viewReport(report);
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

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
