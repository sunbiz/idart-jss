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

import model.manager.DrugManager;
import model.manager.reports.StockReportPerDrug;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class StockReceiptsAndIssuesPerBatch extends GenericReportGui {

	private Group grpDrugSelection;

	private Label lblDrugBarcode;

	private Label lblDrugName;

	// private Text txtDrugBarcode;

	private Text txtDrugName;

	private Button btnDrugSearch;

	private Drug localDrug;

	private Group grpDateRange;

	private Label lblStartDate;

	private Label lblEndDate;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param hSession
	 *            Session
	 */
	public StockReceiptsAndIssuesPerBatch(Shell parent, Session hSession) {
		super(parent, hSession, REPORTTYPE_STOCK);
	}

	/**
	 * This method initializes newDate_DrugOrClinic
	 */
	@Override
	protected void createShell() {
		buildShell("Stock Receipts and Issues Per Batch", new Rectangle(
				70, 50, 600, 530));
		// create the composites
		createMyGroups();
	}

	private void createMyGroups() {
		createGrpDrugSelection();
		createGrpDateRange();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Stock Receipts And Issues Per Batch";
		iDartImage icoImage = iDartImage.REPORT_OUTGOINGPACKAGES;
		buildCompdHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes grpDrugSelection
	 * 
	 */
	private void createGrpDrugSelection() {

		grpDrugSelection = new Group(getShell(), SWT.NONE);
		grpDrugSelection.setText("Drug:");
		grpDrugSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDrugSelection.setBounds(new org.eclipse.swt.graphics.Rectangle(85,
				90, 410, 85));

		// Drug Barcode
		lblDrugBarcode = new Label(grpDrugSelection, SWT.NONE);
		lblDrugBarcode.setBounds(new org.eclipse.swt.graphics.Rectangle(5, 25,
				150, 20));
		lblDrugBarcode.setText("Please search for a drug: ");
		lblDrugBarcode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Drug Name
		lblDrugName = new Label(grpDrugSelection, SWT.NONE);
		lblDrugName.setBounds(new org.eclipse.swt.graphics.Rectangle(5, 50, 85,
				20));
		lblDrugName.setText("Drug Name:");
		lblDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDrugName = new Text(grpDrugSelection, SWT.BORDER);
		txtDrugName.setBounds(new org.eclipse.swt.graphics.Rectangle(160, 50,
				200, 20));
		txtDrugName.setEditable(false);
		txtDrugName.setEnabled(false);
		txtDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnDrugSearch = new Button(grpDrugSelection, SWT.NONE);
		btnDrugSearch.setBounds(new Rectangle(160, 19, 97, 28));
		btnDrugSearch.setText("Drug Search");
		btnDrugSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDrugSearch
		.setToolTipText("Press this button to search for an existing drug.");
		btnDrugSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				Search drugSearch = new Search(getHSession(),
						getShell(), CommonObjects.DRUG);
				if (drugSearch.getValueSelected() != null) {
					txtDrugName
					.setText(drugSearch.getValueSelected()[0]);
					cmdBarcodeScanned();
				}

			}
		});

	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(35, 190, 520, 232));

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
	 * @return Calendar
	 */
	public Calendar getCalendarStart() {
		return calendarStart.getCalendar();
	}

	/**
	 * Method getCalendarEnd.
	 * @return Calendar
	 */
	public Calendar getCalendarEnd() {
		return calendarEnd.getCalendar();
	}

	/**
	 * Method setStartDate.
	 * @param date Date
	 */
	public void setStartDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendarStart.setCalendar(calendar);
	}

	/**
	 * Method setEndDate.
	 * @param date Date
	 */
	public void setEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendarEnd.setCalendar(calendar);
	}

	/**
	 * Method addStartDateChangedListener.
	 * @param listener SWTCalendarListener
	 */
	public void addStartDateChangedListener(SWTCalendarListener listener) {

		calendarStart.addSWTCalendarListener(listener);
	}

	/**
	 * Method addEndDateChangedListener.
	 * @param listener SWTCalendarListener
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

	private void cmdBarcodeScanned() {

		localDrug = DrugManager.getDrug(getHSession(), txtDrugName.getText());

		if (localDrug == null) {
			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Drug Loaded");
			missing
			.setMessage("There was a problem with the drug that was scanned in. "
					+ "\nPlease select a drug by pressing on the 'Drug Search' button.");
			missing.open();
		} else {
			// txtDrugBarcode.setText(localDrug.getBarcode());
			txtDrugName.setText(localDrug.getName());
			// txtDrugBarcode.setEnabled(false);
			txtDrugName.setEnabled(false);
		}

	}

	@Override
	protected void cmdViewReportWidgetSelected() {

		if (txtDrugName.getText().equals("")) {
			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Drug Was Selected");
			missing
			.setMessage("No drug was selected. Please select a drug by pressing on the 'Drug Search' button.");
			missing.open();
		} else {
			StockReportPerDrug report = new StockReportPerDrug(getShell(),
					localDrug.getId(), calendarStart
					.getCalendar().getTime(), calendarEnd.getCalendar()
					.getTime());
			viewReport(report);
		}

	}

	/***************************************************************************
	 * This method is called when the user presses "Clear" button
	 * 
	 */
	private void cmdClearWidgetSelected() {

		// txtDrugBarcode.setText("");
		// txtDrugBarcode.setEnabled(true);

		txtDrugName.setText("");
		txtDrugName.setEnabled(true);

		txtDrugName.setFocus();

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
