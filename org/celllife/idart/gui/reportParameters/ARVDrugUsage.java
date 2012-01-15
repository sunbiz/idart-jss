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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.reports.ARVDrugUsageReport;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class ARVDrugUsage extends GenericReportGui {

	private Group grpDrugSelection;

	private Group grpDateRange;

	private Label lblStartDate;

	private Label lblEndDate;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private Table tblDrugs;

	private TableColumn tblColDrugName;

	private TableColumn tblColPacksize;

	private Label lblSelectDrugs;

	private Group grpPharmacySelection;

	private CCombo cmbPharmacy;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public ARVDrugUsage(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_STOCK, activate);
	}

	/**
	 * This method initializes newDate_DrugOrClinic
	 */
	@Override
	protected void createShell() {
		Rectangle bounds = new Rectangle(70, 50, 700, 680);
		buildShell(REPORT_ARV_DRUG_USAGE, bounds);
		createMyGroups();
	}

	private void createMyGroups() {
		createGrpDrugSelection();
		createGrpDateRange();
		createGrpPharmacySelection();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		iDartImage icoImage = iDartImage.REPORT_OUTGOINGPACKAGES;
		buildCompdHeader(REPORT_ARV_DRUG_USAGE, icoImage);
	}

	private void createGrpPharmacySelection() {

		grpPharmacySelection = new Group(getShell(), SWT.NONE);
		grpPharmacySelection.setText("Pharmacy");
		grpPharmacySelection.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpPharmacySelection.setBounds(new org.eclipse.swt.graphics.Rectangle(
				180, 70, 320, 50));

		Label lblClinic = new Label(grpPharmacySelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(6, 25, 100, 20));
		lblClinic.setText("Select pharmacy");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbPharmacy = new CCombo(grpPharmacySelection, SWT.BORDER);
		cmbPharmacy.setBounds(new Rectangle(110, 23, 200, 20));
		cmbPharmacy.setEditable(false);
		cmbPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbPharmacy.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		CommonObjects.populateStockCenters(getHSession(), cmbPharmacy);

	}

	/**
	 * This method initializes grpDrugSelection
	 * 
	 */
	private void createGrpDrugSelection() {

		grpDrugSelection = new Group(getShell(), SWT.NONE);
		grpDrugSelection.setText("Drugs:");
		grpDrugSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDrugSelection.setBounds(new Rectangle(79, 330, 517, 275));

		lblSelectDrugs = new Label(grpDrugSelection, SWT.CENTER);
		lblSelectDrugs.setBounds(new Rectangle(26, 14, 456, 20));
		lblSelectDrugs.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblSelectDrugs
		.setText("Please select a maximum of 11 drugs by clicking the checkboxes");

		createTblDrugs();
	}

	private void createTblDrugs() {
		tblDrugs = new Table(grpDrugSelection, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.CHECK);
		tblDrugs.setBounds(new Rectangle(30, 33, 470, 235));
		tblDrugs.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblDrugs.setHeaderVisible(true);

		tblColDrugName = new TableColumn(tblDrugs, SWT.NONE);
		tblColDrugName.setText("Drug Name");
		tblColDrugName.setWidth(325);

		tblColPacksize = new TableColumn(tblDrugs, SWT.NONE);
		tblColPacksize.setWidth(60);
		tblColPacksize.setText("Packsize");

		populateTblDrugs();

	}

	private void populateTblDrugs() {
		List<Drug> drugList = DrugManager.getAllDrugs(getHSession());

		Collections.sort(drugList);

		Iterator<Drug> iter = new ArrayList<Drug>(drugList).iterator();
		TableItem[] t = new TableItem[drugList.size()];

		String[] itemText;

		int i = 0;
		while (iter.hasNext()) {
			Drug drug = iter.next();
			t[i] = new TableItem(tblDrugs, SWT.NONE);
			itemText = new String[2];
			itemText[0] = drug.getName();
			itemText[1] = (new Integer(drug.getPackSize())).toString();
			t[i].setText(itemText);
			t[i].setData(drug);
			i++;
		}
	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(79, 120, 520, 201));
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

		// Extra button added for clearing values from parameters
		Button btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdClearWidgetSelected();
			}
		});
	}

	/**
	 * Method getCheckedDrugs.
	 * 
	 * @param table
	 *            Table
	 * @return List<Drug>
	 */
	private List<Drug> getCheckedDrugs(Table table) {
		List<Drug> drugList = new ArrayList<Drug>();

		TableItem[] items = table.getItems();

		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				drugList.add((Drug) (items[i].getData()));
			}
		}

		return drugList;
	}

	@Override
	protected void cmdViewReportWidgetSelected() {

		StockCenter pharm = AdministrationManager.getStockCenter(getHSession(),
				cmbPharmacy.getText());

		if (cmbPharmacy.getText().equals("")) {

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
					+ cmbPharmacy.getText()
					+ "' in the database. Please select a pharmacy by looking through the list of available pharmacies.");
			missing.open();

		} else {

			List<Drug> drugList = getCheckedDrugs(tblDrugs);
			if (drugList.size() == 0) {
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
				mb.setText("No Drugs Added");
				mb.setMessage("Please " + "use the check boxes to select "
						+ "up to 11 drugs to be included in this report.");
				mb.open();

			} else if (drugList.size() > 11) {
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
				mb.setText("Too Many Drugs Added");
				mb.setMessage("Please " + "use the check boxes to select "
						+ "up to 11 drugs to be included in this report.");
				mb.open();

			} else if (calendarStart.getCalendar().getTime().after(
					calendarEnd.getCalendar()
					.getTime())) {

				MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
				mb.setText("Invalid End Date");
				mb.setMessage("Please select an end date after the start date");
				mb.open();
			}
			else {
				ARVDrugUsageReport report = new ARVDrugUsageReport(getShell(),
						cmbPharmacy.getText(),
						drugList,
						calendarStart
						.getCalendar().getTime(), calendarEnd.getCalendar()
						.getTime());
				viewReport(report);
			}


		}


	}

	/***************************************************************************
	 * This method is called when the user presses "Clear" button
	 * 
	 */
	private void cmdClearWidgetSelected() {

		TableItem[] items = tblDrugs.getItems();

		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				items[i].setChecked(false);
			}
		}

		setStartDate(new Date());
		setEndDate(new Date());

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
