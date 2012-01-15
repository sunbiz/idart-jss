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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.manager.AdministrationManager;
import model.manager.reports.MonthlyStockOverviewReport;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.StockCenter;
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

/**
 */
public class MonthlyReceiptsAndIssues extends GenericReportGui {

	private Group grpPharmacySelection;

	private CCombo cmbStockCenter;

	private Group grpDateInfo;

	private Label lblInstructions;

	private CCombo cmbMonth;

	private CCombo cmbYear;

	/**
	 * Constructor
	 *
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public MonthlyReceiptsAndIssues(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_STOCK, activate);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		Rectangle bounds = new Rectangle(100, 50, 600, 510);
		buildShell(REPORT_MONTHLY_RECEIPT_ISSUE, bounds);
		// create the composites
		createMyGroups();
	}

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
		iDartImage icoImage = iDartImage.REPORT_STOCKCONTROLPERCLINIC;
		buildCompdHeader(REPORT_MONTHLY_RECEIPT_ISSUE, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 *
	 */
	private void createGrpClinicSelection() {

		grpPharmacySelection = new Group(getShell(), SWT.NONE);
		grpPharmacySelection.setText("Pharmacy");
		grpPharmacySelection.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpPharmacySelection.setBounds(new org.eclipse.swt.graphics.Rectangle(
				140, 90, 320, 65));

		Label lblPharmacy = new Label(grpPharmacySelection, SWT.NONE);
		lblPharmacy.setBounds(new Rectangle(10, 25, 140, 20));
		lblPharmacy.setText("Select pharmacy");
		lblPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbStockCenter = new CCombo(grpPharmacySelection, SWT.BORDER);
		cmbStockCenter.setBounds(new Rectangle(156, 24, 160, 20));
		cmbStockCenter.setEditable(false);
		cmbStockCenter.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbStockCenter.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		CommonObjects.populateStockCenters(getHSession(), cmbStockCenter);

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
		String months[] = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
		"December" };
		for (int i = 0; i < 12; i++) {
			this.cmbMonth.add(months[i]);
		}

		int intMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		cmbMonth.setText(getMonthName(intMonth));
		cmbMonth.setEditable(false);
		cmbMonth.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbMonth.setVisibleItemCount(12);

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
	}

	@Override
	protected void cmdViewReportWidgetSelected() {

		StockCenter pharm = AdministrationManager.getStockCenter(getHSession(),
				cmbStockCenter.getText());

		if (cmbStockCenter.getText().equals("")) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Pharmacy Was Selected");
			missing
			.setMessage("No pharmacy was selected. Please select a pharmacy by looking through the list of available pharmacies.");
			missing.open();

		} else if (pharm == null) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("Pharmacy not found");
			missing
			.setMessage("There is no pharmacy called '"
					+ cmbStockCenter.getText()
					+ "' in the database. Please select a pharmacy by looking through the list of available pharmacies.");
			missing.open();

		}

		else {
			try {
				Date theDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
				String strTheDate = "" + cmbYear.getText() + "-"
				+ cmbMonth.getText() + "-01";
				theDate = sdf.parse(strTheDate);

				MonthlyStockOverviewReport report = new MonthlyStockOverviewReport(
						getShell(), pharm, theDate);
				viewReport(report);
			} catch (Exception e) {
				getLog()
				.error(
						"Exception while running Monthly Receipts and Issues report",
						e);
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
	 * Method getMonthName.
	 *
	 * @param intMonth
	 *            int
	 * @return String
	 */
	private String getMonthName(int intMonth) {

		String strMonth = "unknown";

		SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM");

		try {
			Date theDate = sdf2.parse(intMonth + "");
			strMonth = sdf1.format(theDate);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return strMonth;

	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
