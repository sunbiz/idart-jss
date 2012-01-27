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
import model.manager.reports.EpisodesStartedOrEndedReport;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class EpisodesStartedOrEndedReportGUI extends GenericReportGui {

	private CCombo cmbClinic;

	private Combo cmbStartedOrEnded;

	private Combo cmbOrderBy1;

	private Combo cmbOrderBy2;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private enum OrderByField {

		// the fieldName is used in the order by of a query of form 'select ep
		// from Episode as ep order by fieldname'
		PATIENT_ID("Patient Number", "patient.patientId"), PATIENT_NAME(
				"Patient Name", "patient.lastname"), START_DATE(
						"Start Date", "startDate"), START_REASON("Start Reason",
						"startReason"), START_NOTES("Start Notes", "startNotes"), STOP_DATE(
								"Stop Date", "stopDate"), STOP_REASON("Stop Reason",
								"stopReason"), STOP_NOTES("Stop Notes", "stopNotes");

		private final String displayName;
		private final String fieldName;

		OrderByField(String displayName, String fieldName) {
			this.displayName = displayName;
			this.fieldName = fieldName;
		}

		public static void populateOrderByOptions(Combo combo, boolean isStart) {
			combo.removeAll();
			combo.add(PATIENT_ID.displayName);
			combo.add(PATIENT_NAME.displayName);
			if (isStart) {
				combo.add(START_DATE.displayName);
				combo.add(START_REASON.displayName);
				combo.add(START_NOTES.displayName);
			} else {
				combo.add(STOP_DATE.displayName);
				combo.add(STOP_REASON.displayName);
				combo.add(STOP_NOTES.displayName);
			}
			combo.setText(combo.getItem(0));
		}

		public static OrderByField getByDisplayName(String displayName) {
			for (OrderByField o : OrderByField.values()) {
				if (o.displayName.equals(displayName))
					return o;
			}
			return null;
		}

	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */

	public EpisodesStartedOrEndedReportGUI(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_PATIENT, activate);
	}

	@Override
	protected void createShell() {

		buildShell(REPORT_EPISODES_STARTED_OR_ENDED, new Rectangle(70,
				50, 700, 580));
		// create the composites
		createMyGroups();

	}

	private void createMyGroups() {
		createGrpReportTypeAndClinicSelection();
		createGrpOrderByFieldSelection();
		createGrpDateRange();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		iDartImage icoImage = iDartImage.PATIENTARRIVES;
		buildCompdHeader(REPORT_EPISODES_STARTED_OR_ENDED, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpReportTypeAndClinicSelection() {

		Group grpReportTypeAndClinicSelection = new Group(getShell(), SWT.NONE);
		grpReportTypeAndClinicSelection.setText("");
		grpReportTypeAndClinicSelection.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpReportTypeAndClinicSelection.setBounds(new Rectangle(151, 83, 386,
				110));

		Label lblClinic = new Label(grpReportTypeAndClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(20, 28, 151, 20));
		lblClinic.setText("Select Clinic:");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbClinic = new CCombo(grpReportTypeAndClinicSelection, SWT.BORDER
				| SWT.READ_ONLY);
		cmbClinic.setBounds(new Rectangle(175, 23, 176, 20));
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateClinics(getHSession(), cmbClinic);

		Label lblStartedOrEnded = new Label(grpReportTypeAndClinicSelection,
				SWT.NONE);
		lblStartedOrEnded.setBounds(new Rectangle(20, 58, 151, 20));
		lblStartedOrEnded.setText("Show all episodes ");
		lblStartedOrEnded.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbStartedOrEnded = new Combo(grpReportTypeAndClinicSelection,
				SWT.BORDER | SWT.READ_ONLY);
		cmbStartedOrEnded.setBounds(new Rectangle(175, 53, 176, 20));
		cmbStartedOrEnded.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbStartedOrEnded.setBackground(ResourceUtils
				.getColor(iDartColor.WHITE));
		cmbStartedOrEnded.add("Started during period");
		cmbStartedOrEnded.add("Ended during period");
		cmbStartedOrEnded.setText(cmbStartedOrEnded.getItem(0));
		cmbStartedOrEnded.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				OrderByField
				.populateOrderByOptions(cmbOrderBy1, (cmbStartedOrEnded
						.getText().startsWith("Started")) ? true
								: false);
				OrderByField
				.populateOrderByOptions(cmbOrderBy2, (cmbStartedOrEnded
						.getText().startsWith("Started")) ? true
								: false);
				cmbOrderBy2.setText(cmbOrderBy2.getItem(1));
			}
		});

	}

	private void createGrpOrderByFieldSelection() {
		Group grpOrderByFieldSelection = new Group(getShell(), SWT.NONE);
		grpOrderByFieldSelection.setText("");
		grpOrderByFieldSelection.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpOrderByFieldSelection.setBounds(new Rectangle(121, 215, 446, 40));

		Label lblOrderBy1 = new Label(grpOrderByFieldSelection, SWT.NONE);
		lblOrderBy1.setBounds(new Rectangle(20, 13, 110, 20));
		lblOrderBy1.setText("Order results by ");
		lblOrderBy1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbOrderBy1 = new Combo(grpOrderByFieldSelection, SWT.BORDER
				| SWT.READ_ONLY);
		cmbOrderBy1.setBounds(new Rectangle(135, 8, 120, 20));
		cmbOrderBy1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbOrderBy1.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		OrderByField.populateOrderByOptions(cmbOrderBy1, (cmbStartedOrEnded
				.getText().startsWith("Started")) ? true : false);

		Label lblOrderBy2 = new Label(grpOrderByFieldSelection, SWT.NONE);
		lblOrderBy2.setBounds(new Rectangle(270, 13, 30, 20));
		lblOrderBy2.setText("then");
		lblOrderBy2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbOrderBy2 = new Combo(grpOrderByFieldSelection, SWT.BORDER
				| SWT.READ_ONLY);
		cmbOrderBy2.setBounds(new Rectangle(305, 8, 120, 20));
		cmbOrderBy2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbOrderBy2.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		OrderByField.populateOrderByOptions(cmbOrderBy2, (cmbStartedOrEnded
				.getText().startsWith("Started")) ? true : false);
		cmbOrderBy2.setText(cmbOrderBy2.getItem(1));
	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		Group grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(70, 270, 545, 201));
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblStartDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblStartDate.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 30,
				250, 20));
		lblStartDate.setText("Select a START date:");
		lblStartDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblEndDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblEndDate.setBounds(new org.eclipse.swt.graphics.Rectangle(283, 30,
				250, 20));
		lblEndDate.setText("Select an END date:");
		lblEndDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		calendarStart = new SWTCalendar(grpDateRange);
		calendarStart.setBounds(10, 55, 250, 140);

		calendarEnd = new SWTCalendar(grpDateRange);
		calendarEnd.setBounds(283, 55, 250, 140);

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

		if (cmbClinic.getText().equals("")) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Clinic Was Selected");
			missing
			.setMessage("No clinic was selected. Please select a clinic by looking through the list of available clinics.");
			missing.open();

		} else if ((cmbStartedOrEnded.getText() == null)
				|| ((cmbOrderBy1.getText() == null))
				|| ((cmbOrderBy2.getText() == null))) {
			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("Report options not selected");
			missing
			.setMessage("Report type or order by fields not selected. Please select by looking through the list of available options.");
			missing.open();
		}

		else {
			Clinic c = AdministrationManager.getClinic(getHSession(), cmbClinic
					.getText().trim());

			EpisodesStartedOrEndedReport report = new EpisodesStartedOrEndedReport(
					getShell(),
					c,
					calendarStart.getCalendar().getTime(),
					calendarEnd.getCalendar().getTime(),
					(cmbStartedOrEnded.getText().startsWith("Started")) ? true
							: false,
							OrderByField.getByDisplayName(cmbOrderBy1.getText()).fieldName,
							OrderByField.getByDisplayName(cmbOrderBy2.getText()).fieldName);
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
