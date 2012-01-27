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

import model.manager.reports.EpisodeStatisticsReport;

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

/**
 */
public class EpisodeStats extends GenericReportGui {

	private static final String STARTING_IN_PERIOD = "Starting in period";

	private static final String ENDING_IN_PERIOD = "Ending in period";

	private Group grpClinicSelection;

	private CCombo cmbClinic;

	private Group grpDateRange;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private Text txtUpperCutoffAge;

	private CCombo cmbStartOrEnd;

	private Text txtLowerCutoffAge;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param active
	 *            boolean
	 */
	public EpisodeStats(Shell parent, boolean active) {
		super(parent, REPORTTYPE_MONITORINGANDEVALUATION, active);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		buildShell(REPORT_EPISODES_STATS, new Rectangle(100, 50, 600, 554));
		// create the composites
		createMyGroups();
	}

	protected void createMyGroups() {
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
		buildCompdHeader(REPORT_EPISODES_STATS, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {
		grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setText("");
		grpClinicSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpClinicSelection.setBounds(new Rectangle(108, 81, 386, 123));

		int ystart = 7;
		Label lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(9, ystart, 151, 20));
		lblClinic.setText("Select Clinic:");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setBounds(new Rectangle(216, ystart, 160, 20));
		cmbClinic.setEditable(false);
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateClinics(getHSession(), cmbClinic);

		Label lblStartOrEnd = new Label(grpClinicSelection, SWT.NONE);
		lblStartOrEnd.setBounds(new Rectangle(10, ystart + 32, 174, 21));
		lblStartOrEnd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblStartOrEnd.setText("Show episodes:");

		cmbStartOrEnd = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbStartOrEnd.setBounds(new Rectangle(216, ystart + 30, 160, 20));
		cmbStartOrEnd.setEditable(false);
		cmbStartOrEnd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbStartOrEnd.setItems(new String[] { STARTING_IN_PERIOD,
				ENDING_IN_PERIOD });
		cmbStartOrEnd.setText(STARTING_IN_PERIOD);

		Label lblCutoffAge = new Label(grpClinicSelection, SWT.NONE);
		lblCutoffAge.setBounds(new Rectangle(10, ystart + 61, 195, 20));
		lblCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCutoffAge.setText("Cutoff Age for Young Paediatrics:");

		txtLowerCutoffAge = new Text(grpClinicSelection, SWT.BORDER);
		txtLowerCutoffAge.setBounds(new Rectangle(218, ystart + 60, 43, 19));
		txtLowerCutoffAge.setText("5");
		txtLowerCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblYears = new Label(grpClinicSelection, SWT.NONE);
		lblYears.setBounds(new Rectangle(270, ystart + 60, 94, 20));
		lblYears.setText("years");
		lblYears.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblCutoffAge = new Label(grpClinicSelection, SWT.NONE);
		lblCutoffAge.setBounds(new Rectangle(10, ystart + 90, 195, 20));
		lblCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCutoffAge.setText("Cutoff Age for Paediatrics:");

		txtUpperCutoffAge = new Text(grpClinicSelection, SWT.BORDER);
		txtUpperCutoffAge.setBounds(new Rectangle(218, ystart + 90, 43, 19));
		txtUpperCutoffAge.setText("14");
		txtUpperCutoffAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblYears = new Label(grpClinicSelection, SWT.NONE);
		lblYears.setBounds(new Rectangle(270, ystart + 90, 94, 20));
		lblYears.setText("years");
		lblYears.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setBounds(new Rectangle(30, 220, 520, 201));
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblStartDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblStartDate.setBounds(new org.eclipse.swt.graphics.Rectangle(40, 30,
				180, 20));
		lblStartDate.setText("Select a START date:");
		lblStartDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblEndDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
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

		if (txtUpperCutoffAge.getText().equals("")) {
			MessageBox incorrectData = new MessageBox(getShell(),
					SWT.ICON_ERROR | SWT.OK);
			incorrectData.setText("Incorrect Numeric Value");
			incorrectData
			.setMessage("The cutoff age that was entered is incorrect. Please enter a number.");
			incorrectData.open();
			txtUpperCutoffAge.setText("");
			txtUpperCutoffAge.setFocus();

			viewReport = false;
		}

		if (!txtUpperCutoffAge.getText().equals("")) {
			try {
				Integer.parseInt(txtUpperCutoffAge.getText());
			} catch (NumberFormatException nfe) {
				MessageBox incorrectData = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);
				incorrectData.setText("Incorrect Numeric Value");
				incorrectData
				.setMessage("The cutoff age that was entered is incorrect. Please enter a number.");
				incorrectData.open();
				txtUpperCutoffAge.setText("");
				txtUpperCutoffAge.setFocus();

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
			boolean showStartReasons = cmbStartOrEnd.getText().equals(
					STARTING_IN_PERIOD);
			EpisodeStatisticsReport report = new EpisodeStatisticsReport(
					getShell(),
					cmbClinic.getText(), calendarStart.getCalendar()
					.getTime(),
					calendarEnd.getCalendar().getTime(), Integer
					.parseInt(txtUpperCutoffAge.getText()), Integer
					.parseInt(txtLowerCutoffAge.getText()),
					showStartReasons);
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
