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
import java.util.Date;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.exports.DrugDispensedObject;
import model.manager.exports.PackageExportObject;
import model.manager.exports.columns.DrugsDispensedEnum;
import model.manager.exports.excel.ExcelReportObject;
import model.manager.exports.excel.RowPerPackageExcelExporter;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.reportParameters.viewerUtils.DrugsDispensedLabelProvider;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.SafeSaveDialog;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.misc.SafeSaveDialog.FileType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class DrugsDispensed extends GenericReportGui {

	private Group grpDateRange;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private CheckboxTableViewer tblColumns;
	
	private Label lblColumnTableHeader;

	private CheckboxTableViewer tblDrugNames;
	
	private Label lblDrugTableHeader;
	
	private Link lnkSelectAllColumns;

	private Link lnkSelectAllDrugs;
	
	private Group grpPharmacySelection;

	private CCombo cmbPharmacy;

	private Button chkBatchInformation;

	private List<Drug> drugList = new ArrayList<Drug>();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public DrugsDispensed(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_STOCK, activate);
	}

	/**
	 * This method initializes newDate_DrugOrClinic
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Drugs Dispensed Report (Clinics, Patients & Drugs)";
		Rectangle bounds = new Rectangle(70, 50, 700, 680);
		buildShell(shellTxt, bounds);
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
		String headerTxt = "Drugs Dispensed Report (Clinics, Patients & Drugs)";
		iDartImage icoImage = iDartImage.REPORT_STOCKCONTROLPERDRUG;
		buildCompdHeader(headerTxt, icoImage);
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
		
		lnkSelectAllColumns = new Link(getShell(), SWT.NONE);
		lnkSelectAllColumns.setBounds(new Rectangle(115, 325, 220, 30));
		lnkSelectAllColumns
		.setText("Please select the columns you want included " +
				"in the report or <A>select all</A> columns");
		lnkSelectAllColumns
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));
		lnkSelectAllColumns.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				tblColumns.setAllChecked(true);
			}
		});

		lnkSelectAllDrugs = new Link(getShell(), SWT.NONE);
		lnkSelectAllDrugs.setBounds(new Rectangle(375, 325, 220, 30));
		lnkSelectAllDrugs
		.setText("Please select the drugs you want included in the " +
				" report or <A>select all</A> drugs");
		lnkSelectAllDrugs
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));
		lnkSelectAllDrugs.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				tblDrugNames.setAllChecked(true);
			}
		});
		
		createTblDrugs();
	}

	private void createTblDrugs() {
		
		lblColumnTableHeader = new Label(getShell(), SWT.BORDER);
		lblColumnTableHeader.setBounds(new Rectangle(120, 360, 200, 20));
		lblColumnTableHeader.setText("Column Name");
		lblColumnTableHeader.setAlignment(SWT.CENTER);
		lblColumnTableHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		tblColumns = CheckboxTableViewer.newCheckList(getShell(), SWT.BORDER);
		tblColumns.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(120, 380, 200, 200));
		tblColumns.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblColumns.setContentProvider(new ArrayContentProvider());
		tblColumns.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent arg0) {
				DrugsDispensedEnum element = (DrugsDispensedEnum) arg0.getElement();
				if (DrugsDispensedEnum.getCompulsory().contains(element)
						&& !arg0.getChecked()) {
					tblColumns.setChecked(element, true);
				}
			}
		});

		lblDrugTableHeader = new Label(getShell(), SWT.BORDER);
		lblDrugTableHeader.setBounds(new Rectangle(370, 360, 200, 20));
		lblDrugTableHeader.setText("Drug Name");
		lblDrugTableHeader.setAlignment(SWT.CENTER);
		lblDrugTableHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		tblDrugNames = CheckboxTableViewer.newCheckList(getShell(), SWT.BORDER);
		tblDrugNames.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(370, 380, 200, 200));
		tblDrugNames.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblDrugNames.setContentProvider(new ArrayContentProvider());
		
		drugList = DrugManager.getAllDrugs(getHSession());
		tblDrugNames.setInput(drugList);
		tblDrugNames.setLabelProvider(new DrugsDispensedLabelProvider());
		
		tblColumns.setInput(DrugsDispensedEnum.values());
		tblColumns.setCheckedElements(DrugsDispensedEnum.getDefaults());
		tblColumns.setGrayedElements(DrugsDispensedEnum.getCompulsory().toArray());
		
		populateTblDrugs();

		chkBatchInformation = new Button(getShell(), SWT.CHECK);
		chkBatchInformation.setBounds(new Rectangle(250, 585, 200, 20));
		chkBatchInformation
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		chkBatchInformation.setText("Include Batch Information in Report?");
		chkBatchInformation.setSelection(true);

	}

	private void populateTblDrugs() {

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
		calendarEnd.addSWTCalendarListener(new SWTCalendarListener() {
			@Override
			public void dateChanged(SWTCalendarEvent calendarEvent) {
				Date date = calendarEvent.getCalendar().getTime();
				DrugsDispensedEnum.setEndDate(date);
				tblColumns.refresh();
			}
		});
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

	@Override
	protected void cmdViewReportWidgetSelected() {

		if (cmbPharmacy.getText().equals("")) {
			showMessage(MessageDialog.ERROR,"No Pharmacy Was Selected",
					"No pharmacy was selected. Please select a pharmacy by looking through the list of available pharmacies.");
			return;
		}
		
		StockCenter pharm = AdministrationManager.getStockCenter(getHSession(),
				cmbPharmacy.getText());
		
		if (pharm == null) {
			showMessage(MessageDialog.ERROR,"Pharmacy not found",
					"There is no pharmacy called '"
					+ cmbPharmacy.getText()
					+ "' in the database. Please select a pharmacy by looking through the list of available pharmacies.");
			return;
		}
		
		if (iDARTUtil.before(calendarEnd.getCalendar().getTime(), calendarStart.getCalendar().getTime())){
			showMessage(MessageDialog.ERROR, "End date before start date",
					"You have selected an end date that is before the start date.\nPlease select an end date after the start date.");
			return;
		}
		
		SafeSaveDialog dialog = new SafeSaveDialog(getShell(), FileType.EXCEL);
		String path = "";
		dialog.setFileName("Drugs_Dispensed_Report");
		path = dialog.open();

		if (path != null) {
			ExcelReportObject reportObject = getColumnsFromTables(path);
			viewReport(new ExcelReportJob(reportObject, new RowPerPackageExcelExporter()));
			showMessage(MessageDialog.INFORMATION, "Report complete",
					"Report generation complete.\n\n" + reportObject.getPath());
		}
	}


	/**
	 * This method creates the report object from the selected 
	 * values from the table.
	 * @param path
	 */
	private ExcelReportObject getColumnsFromTables(String path) {
		ExcelReportObject exr = new ExcelReportObject();
		List<PackageExportObject> allColumns = new ArrayList<PackageExportObject>();
		Object[] obj = tblColumns.getCheckedElements();
		for(int i = 0; i < obj.length; i++) {
			DrugsDispensedEnum enu = (DrugsDispensedEnum) obj[i];
			DrugDispensedObject ddo = new DrugDispensedObject(enu);
			allColumns.add(ddo);
		}
		
		Object[] obj2 = tblDrugNames.getCheckedElements();
		for(int i = 0; i < obj2.length; i++) {
			Drug drug = (Drug) obj2[i];
			DrugDispensedObject ddo = new DrugDispensedObject(drug.getName(), String.class);
			ddo.setId(drug.getId());
			ddo.setDrugPackSize(drug.getPackSize());
			ddo.setSubTitle(DrugManager.getShortGenericDrugName(drug, false) + " ("
					+ drug.getPackSize() + " " +drug.getForm().getFormLanguage1()
					+ ")");
			allColumns.add(ddo);
		}
		
		exr.setColumns(allColumns);
		exr.setEndDate(iDARTUtil.getEndOfDay(calendarEnd.getCalendar().getTime()));
		exr.setPath(path);
		exr.setPharmacy(cmbPharmacy.getText());
		exr.setShowBatchInfo(chkBatchInformation.getSelection());
		exr.setStartDate(iDARTUtil.getBeginningOfDay(calendarStart.getCalendar().getTime()));
		
		return exr;
		
	}

	/***************************************************************************
	 * This method is called when the user presses "Clear" button
	 * 
	 */
	private void cmdClearWidgetSelected() {
		setStartDate(new Date());
		setEndDate(new Date());
		tblDrugNames.setAllChecked(false);
		tblColumns.setAllChecked(false);
		tblColumns.setGrayedElements(DrugsDispensedEnum.getCompulsory().toArray());
		tblColumns.setCheckedElements(DrugsDispensedEnum.getCompulsory().toArray());
		chkBatchInformation.setSelection(true);
		
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
