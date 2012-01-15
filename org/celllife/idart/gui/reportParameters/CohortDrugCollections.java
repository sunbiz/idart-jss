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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.manager.exports.DataExportFunctions;
import model.manager.exports.DrugDispensedObject;
import model.manager.exports.EpisodeObject;
import model.manager.exports.PackageExportObject;
import model.manager.exports.columns.DrugsDispensedEnum;
import model.manager.exports.columns.EpisodeDetailsEnum;
import model.manager.exports.excel.ExcelReportObject;
import model.manager.exports.excel.RowPerPatientExcelExporter;
import model.nonPersistent.EntitySet;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.SafeSaveDialog;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.misc.SafeSaveDialog.FileType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Query;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class CohortDrugCollections extends GenericReportGui {

	private static final String FORMULA_COLUMN = "Days between expected and actual collection";

	private Group grpDateRange;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	private CheckboxTableViewer tblColumns;
	
	private Label lblColumnTableHeader;

	private CheckboxTableViewer tblPackageColumns;
	
	private Label lblDrugTableHeader;
	
	private Link lnkSelectAllColumns;

	private Link lnkSelectAllPackageColumns;
	
	private Group grpExplanation;
	
	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public CohortDrugCollections(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_STOCK, activate);
	}

	/**
	 * This method initializes newDate_DrugOrClinic
	 */
	@Override
	protected void createShell() {
		String shellTxt = Messages.getString("reports.cohortCollections");  //$NON-NLS-1$
		Rectangle bounds = new Rectangle(70, 50, 700, 680);
		buildShell(shellTxt, bounds);
		createMyGroups();
	}

	private void createMyGroups() {
		createGrpPackageColumnsSelection();
		createGrpDateRange();
		createGrpPharmacySelection();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = Messages.getString("reports.cohortCollections");  //$NON-NLS-1$
		iDartImage icoImage = iDartImage.REPORT_STOCKCONTROLPERDRUG;
		buildCompdHeader(headerTxt, icoImage);
	}

	private void createGrpPharmacySelection() {

		grpExplanation = new Group(getShell(), SWT.NONE);
		grpExplanation.setText("Report description");
		grpExplanation.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpExplanation.setBounds(new org.eclipse.swt.graphics.Rectangle(
				79, 60, 520, 60));

		Label lblClinic = new Label(grpExplanation, SWT.WRAP);
		lblClinic.setBounds(new Rectangle(6, 20, 510, 30));
		lblClinic.setText("This reports shows the details of all packages for" +
				" patients that have a '"+Episode.REASON_NEW_PATIENT+"' episode" +
						" between the start and end dates specified.");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}
	
	private void createGrpPackageColumnsSelection() {
		
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

		lnkSelectAllPackageColumns = new Link(getShell(), SWT.NONE);
		lnkSelectAllPackageColumns.setBounds(new Rectangle(375, 325, 220, 30));
		lnkSelectAllPackageColumns
		.setText("Please select the package columns you want included in the " +
				" report or <A>select all</A>");
		lnkSelectAllPackageColumns
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));
		lnkSelectAllPackageColumns.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				tblPackageColumns.setAllChecked(true);
			}
		});
		
		createTblPackages();
	}

	private void createTblPackages() {
		
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
		tblColumns.setInput(getColumns());
		tblColumns.setCheckedElements(DrugsDispensedEnum.getDefaults());

		lblDrugTableHeader = new Label(getShell(), SWT.BORDER);
		lblDrugTableHeader.setBounds(new Rectangle(370, 360, 200, 20));
		lblDrugTableHeader.setText("Drug Name");
		lblDrugTableHeader.setAlignment(SWT.CENTER);
		lblDrugTableHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		tblPackageColumns = CheckboxTableViewer.newCheckList(getShell(), SWT.BORDER);
		tblPackageColumns.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(370, 380, 200, 200));
		tblPackageColumns.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblPackageColumns.setContentProvider(new ArrayContentProvider());
		tblPackageColumns.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent arg0) {
				Object element = arg0.getElement();
				if (!arg0.getChecked() && Arrays.asList(getDefaults()).contains(element)) {
					tblPackageColumns.setChecked(element, true);
				}
			}
		});

		tblPackageColumns.setInput(getPackageColumns());
		tblPackageColumns.setCheckedElements(getDefaults());
		tblPackageColumns.setGrayedElements(getDefaults());
	}

	private Object[] getDefaults() {
		return new Object[] { DrugsDispensedEnum.dateDispensed,
				DrugsDispensedEnum.expectedRunoutDate };
		}

	private Object getPackageColumns() {
		return new Object[] { DrugsDispensedEnum.dateDispensed,
				DrugsDispensedEnum.drugGroupName,
				DrugsDispensedEnum.drugsCollected,
				DrugsDispensedEnum.packageId, DrugsDispensedEnum.regimen,
				DrugsDispensedEnum.expectedRunoutDate, FORMULA_COLUMN };
	}

	private Object getColumns() {
		return new Object[] { DrugsDispensedEnum.patientId,
				DrugsDispensedEnum.patientFirstName,
				DrugsDispensedEnum.patientLastName, DrugsDispensedEnum.sex,
				DrugsDispensedEnum.age, DrugsDispensedEnum.dateOfBirth,
				DrugsDispensedEnum.pregnant, DrugsDispensedEnum.arvStartDate,
				EpisodeDetailsEnum.startDate, EpisodeDetailsEnum.startReason,
				EpisodeDetailsEnum.startNotes, EpisodeDetailsEnum.stopDate,
				EpisodeDetailsEnum.stopReason, EpisodeDetailsEnum.stopNotes };
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

		if (iDARTUtil.before(calendarEnd.getCalendar().getTime(), calendarStart.getCalendar().getTime())){
			showMessage(MessageDialog.ERROR, "End date before start date",
					"You have selected an end date that is before the start date.\nPlease select an end date after the start date.");
			return;
		}
		
		SafeSaveDialog dialog = new SafeSaveDialog(getShell(), FileType.EXCEL);
		String path = "";
		dialog.setFileName("Cohort_Drug_Collections_report");
		path = dialog.open();

		if (path != null) {
			ExcelReportObject reportObject = getColumnsFromTables(path);
			EntitySet patients = getPatientSet(reportObject);
			if (patients.size() <= 0){
				showMessage(
						MessageDialog.INFORMATION,
						"No patients",
						"No patients have a '"
								+ Episode.REASON_NEW_PATIENT
								+ "' episode starting between the selected dates");
				return;
			}
			viewReport(new ExcelReportJob(reportObject, new RowPerPatientExcelExporter(patients)));
			showMessage(MessageDialog.INFORMATION, "Report complete",
					"Report generation complete.\n\n" + reportObject.getPath());
		}
	}


	private EntitySet getPatientSet(ExcelReportObject report) {
		String patientQuery = "select e.patient.id from Episode e where e.startDate between :startDate and :endDate" +
		" and e.startReason = :startReason";
		Query query = getHSession().createQuery(patientQuery);
		query.setString("startReason", Episode.REASON_NEW_PATIENT);
		query.setTimestamp("startDate", report.getStartDate());
		query.setTimestamp("endDate", report.getEndDate());
		@SuppressWarnings("unchecked")
		List<Integer> patients = query.list();
		return new EntitySet(patients);
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
			if (obj[i] instanceof DrugsDispensedEnum){
				DrugsDispensedEnum enu = (DrugsDispensedEnum) obj[i];
				DrugDispensedObject ddo = new DrugDispensedObject(enu);
				allColumns.add(ddo);
			} else if (obj[i] instanceof EpisodeDetailsEnum){
				EpisodeDetailsEnum enu = (EpisodeDetailsEnum) obj[i];
				EpisodeObject ddo = new EpisodeObject(enu, calendarStart.getCalendar().getTime(),calendarEnd.getCalendar().getTime());
				allColumns.add(ddo);
			}
		}
		
		List<PackageExportObject> endcolumns = new ArrayList<PackageExportObject>();
		Object[] obj2 = tblPackageColumns.getCheckedElements();
		for(int i = 0; i < obj2.length; i++) {
			if (obj2[i] instanceof DrugsDispensedEnum){
				DrugsDispensedEnum enu = (DrugsDispensedEnum) obj2[i];
				DrugDispensedObject ddo = new DrugDispensedObject(enu);
				endcolumns.add(ddo);
			} else if (obj2[i] instanceof String){
				DrugDispensedObject diff = new DrugDispensedObject(){
					@Override
					public Object getData(DataExportFunctions functions, int index) {
						String previousColumn = iDARTUtil.columnIndexToLetterNotation(currentColumnIndex-1, true);
						String nextColumn = iDARTUtil.columnIndexToLetterNotation(currentColumnIndex+1, true);
						String nextCell = nextColumn + (rowCounter+1);
						String previousCell = previousColumn + (rowCounter+1);
						String formula = "IF(NOT(ISBLANK("+nextCell+")),ROUND(" + nextCell + "-" + previousCell + ",1),\"\")";
						return new jxl.write.Formula(currentColumnIndex, rowCounter, formula);
					}
					
				};
				diff.setColumnWidth(17);
				diff.setColumnIndex(-1);
				diff.setTitle("Days late");
				endcolumns.add(diff);
			}
		}
		
		exr.setColumns(allColumns);
		exr.setEndColumns(endcolumns);
		exr.setEndDate(iDARTUtil.getEndOfDay(calendarEnd.getCalendar().getTime()));
		exr.setPath(path);
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
		tblPackageColumns.setAllChecked(false);
		tblColumns.setAllChecked(false);
		tblColumns.setGrayedElements(DrugsDispensedEnum.getCompulsory().toArray());
		tblColumns.setCheckedElements(DrugsDispensedEnum.getCompulsory().toArray());
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
