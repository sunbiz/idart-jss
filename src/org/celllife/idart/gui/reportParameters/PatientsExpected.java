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

import model.manager.reports.PatientsExpectedReport;

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
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class PatientsExpected extends GenericReportGui {

	private Group grpClinicSelection;

	private Label lblClinic;

	private CCombo cmbClinic;
	
	private Label lblOrderByProperty;
	
	private CCombo cmbOrderByProperty;
	
	private Label lblOrderByDirection;
	
	private Label lblWarning;
	
	private CCombo cmbOrderByDirection;

	private Group grpDateRange;

	private SWTCalendar swtCal;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public PatientsExpected(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_CLINICMANAGEMENT, activate);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		Rectangle bounds = new Rectangle(100, 50, 600, 510);
		buildShell(REPORT_PATIENTS_EXPECTED_ON_A_DAY, bounds);
		// create the composites
		createMyGroups();
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
		iDartImage icoImage = iDartImage.REPORT_PACKAGESSCANNEDIN;
		buildCompdHeader(REPORT_PATIENTS_EXPECTED_ON_A_DAY, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {

		grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setText("Patients Expected Report Settings");
		grpClinicSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpClinicSelection.setBounds(new Rectangle(60, 69, 465, 107));

		lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(30, 25, 152, 20));
		lblClinic.setText("Select Clinic");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setBounds(new Rectangle(202, 25, 160, 20));
		cmbClinic.setEditable(false);
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateClinics(getHSession(), cmbClinic);
		
		lblOrderByProperty = new Label(grpClinicSelection, SWT.NONE);
		lblOrderByProperty.setBounds(new Rectangle(30, 55, 152, 20));
		lblOrderByProperty.setText("Select Column to Order By");
		lblOrderByProperty.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbOrderByProperty = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbOrderByProperty.setBounds(new Rectangle(202, 55, 160, 20));
		cmbOrderByProperty.setEditable(false);
		cmbOrderByProperty.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbOrderByProperty.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbOrderByProperty.add("Patient Number");
		cmbOrderByProperty.add("Patient Name");
		cmbOrderByProperty.add("Script Duration");
		cmbOrderByProperty.add("Number of Packages");
		cmbOrderByProperty.setText(cmbOrderByProperty.getItem(0));
		
		lblOrderByDirection = new Label(grpClinicSelection, SWT.NONE);
		lblOrderByDirection.setBounds(new Rectangle(30, 85, 152, 20));
		lblOrderByDirection.setText("Select Direction to Order By");
		lblOrderByDirection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbOrderByDirection = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbOrderByDirection.setBounds(new Rectangle(202, 85, 160, 20));
		cmbOrderByDirection.setEditable(false);
		cmbOrderByDirection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbOrderByDirection.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbOrderByDirection.add("Ascending");
		cmbOrderByDirection.add("Descending");
		cmbOrderByDirection.setText(cmbOrderByDirection.getItem(0));
		

	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {
		
		lblWarning = new Label(getShell(), SWT.CENTER);
		lblWarning.setBounds(new Rectangle(60, 183, 465, 40));
		lblWarning.setText("Note that if you generate this report for a date in the past, it will include patients with appointments for that day AND patients who arrived on that day, without appointments.");
		lblWarning.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblWarning.setForeground(ResourceUtils.getColor(iDartColor.RED));

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Select Report Date:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(142, 224, 309, 211));

		swtCal = new SWTCalendar(grpDateRange);
		swtCal.setBounds(40, 40, 220, 160);

	}

	/**
	 * Method getCalendarDate.
	 * 
	 * @return Calendar
	 */
	public Calendar getCalendarDate() {
		return swtCal.getCalendar();
	}

	/**
	 * Method setCalendarDate.
	 * 
	 * @param date
	 *            Date
	 */
	public void setCalendarDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		swtCal.setCalendar(calendar);
	}

	/**
	 * Method addDateChangedListener.
	 * 
	 * @param listener
	 *            SWTCalendarListener
	 */
	public void addDateChangedListener(SWTCalendarListener listener) {

		swtCal.addSWTCalendarListener(listener);
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
		
		String orderByProperty = "patientID";
		String orderByDirection = "asc";
		
		/**
		 * not the cleanest code I know but I felt that creating an enum for this would
		 * be over kill
		 */
		if("Patient Number".equals(cmbOrderByProperty.getText())) {
			orderByProperty = "patientID";
		}
		else if("Patient Name".equals(cmbOrderByProperty.getText())) {
			orderByProperty = "name";
		}
		else if("Script Duration".equals(cmbOrderByProperty.getText())) {
			orderByProperty = "scriptduration";
		}
		else if("Number of Packages".equals(cmbOrderByProperty.getText())) {
			orderByProperty = "packcount";
		}
		
		if("Ascending".equals(cmbOrderByDirection.getText())){
			orderByDirection = "asc";
		}
		else if("Descending".equals(cmbOrderByDirection.getText())){
			orderByDirection = "desc";
		}
		
		
		if (cmbClinic.getText().equals("")) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Clinic Was Selected");
			missing
			.setMessage("No clinic was selected. Please select a clinic by looking through the list of available clinics.");
			missing.open();
			viewReport = false;

		}

		/*if (iDARTUtil.before(swtCal.getCalendar().getTime(), new Date())) {
			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("Invalid Report Date Selected");
			missing
			.setMessage("Invalid Report Date Selected. You cannot select a date in the past. Please select a correct date.");
			missing.open();
			viewReport = false;

		}*/

		if (viewReport) {
			PatientsExpectedReport report = new PatientsExpectedReport(
					getShell(), cmbClinic.getText(), swtCal.getCalendar()
					.getTime(), orderByProperty, orderByDirection);
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
