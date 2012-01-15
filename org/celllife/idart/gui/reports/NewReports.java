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

package org.celllife.idart.gui.reports;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.celllife.idart.gui.dataExports.DataExport;
import org.celllife.idart.gui.dataQuality.DataQuality;
import org.celllife.idart.gui.platform.GenericAdminGui;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.platform.GenericReportGuiInterface;
import org.celllife.idart.gui.reportParameters.ARVDrugUsage;
import org.celllife.idart.gui.reportParameters.ClinicIndicators;
import org.celllife.idart.gui.reportParameters.CohortDrugCollections;
import org.celllife.idart.gui.reportParameters.DailyDispensingTotals;
import org.celllife.idart.gui.reportParameters.DrugCombinations;
import org.celllife.idart.gui.reportParameters.DrugsDispensed;
import org.celllife.idart.gui.reportParameters.EpisodeStats;
import org.celllife.idart.gui.reportParameters.EpisodesStartedOrEndedReportGUI;
import org.celllife.idart.gui.reportParameters.MissedAppointments;
import org.celllife.idart.gui.reportParameters.MonthlyReceiptsAndIssues;
import org.celllife.idart.gui.reportParameters.MonthlyStockReceipt;
import org.celllife.idart.gui.reportParameters.PackageProcessingReportGUI;
import org.celllife.idart.gui.reportParameters.PackageTracking;
import org.celllife.idart.gui.reportParameters.PackagesAwaiting;
import org.celllife.idart.gui.reportParameters.PatientHistory;
import org.celllife.idart.gui.reportParameters.PatientsExpected;
import org.celllife.idart.gui.reportParameters.PepfarReportGUI;
import org.celllife.idart.gui.reportParameters.PrescribingDoctors;
import org.celllife.idart.gui.reportParameters.StockTakeReportGUI;
import org.celllife.idart.gui.reportParameters.TransactionLog;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.model.utils.PackageLifeStage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 */
public class NewReports extends GenericAdminGui {

	private Group grpPatientReports;

	private Group grpStockReports;

	private Group grpMandEReports;

	private Group grpClinicManagementReports;

	private Composite compButton;

	private Label lblPicPatientReports;

	private Label lblPicStockReports;

	private Label lblPicMandEReports;

	private Label lblPicClinicManagementReports;

	private Table tblPatientReports;

	private Table tblStockReports;

	private Table tblMandEReports;

	private Table tblClinicManagementReports;

	private Button btnDataExport;
	
	private Button btnDataQuality;

	Map<String, GenericReportGui> reportGUIs = new  LinkedHashMap<String, GenericReportGui>();

	/**
	 * Constructor for NewReports.
	 * 
	 * @param parent
	 *            Shell
	 */
	public NewReports(Shell parent) {
		super(parent);
		// All GenericReports will not close
		GenericReportGui.setShouldClose(false);
		populateReportLists();
	}

	/**
	 * This method initializes newReports
	 */
	@Override
	protected void createShell() {
		buildShell(Messages.getString("NewReports.shell.title")); //$NON-NLS-1$
	}

	private void createMyGroups() {
		createGrpPatientReports();
		createGrpClinicManagementReports();
		createGrpStockReports();
		createGrpMandEReports();
		createCompButtons();
	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String titleText = Messages.getString("NewReports.shell.title"); //$NON-NLS-1$
		iDartImage icoImage = iDartImage.REPORTS;
		buildCompHeader(titleText, icoImage);
	}

	private void createGrpPatientReports() {
		grpPatientReports = new Group(getShell(), SWT.NONE);
		grpPatientReports.setBounds(new Rectangle(100, 80, 325, 200));
		grpPatientReports.setText(Messages.getString("NewReports.section.patient")); //$NON-NLS-1$
		grpPatientReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

		lblPicPatientReports = new Label(grpPatientReports, SWT.NONE);
		lblPicPatientReports.setBounds(new org.eclipse.swt.graphics.Rectangle(
				10, 0, 50, 43));
		lblPicPatientReports.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PATIENTHISTORY));

		tblPatientReports = new Table(grpPatientReports, SWT.BORDER);
		tblPatientReports.setBounds(new Rectangle(20, 50, 285, 130));
		tblPatientReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColReportsAvailable = new TableColumn(tblPatientReports,
				SWT.NONE);
		tblColReportsAvailable.setText(Messages.getString("NewReports.table.title")); //$NON-NLS-1$
		tblColReportsAvailable.setWidth(270);

		tblPatientReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				clearSelections();

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblPatientReports.getItem(pt);
				if (item != null) {
					launchReport(item);
					tblPatientReports.select(tblPatientReports.indexOf(item));
				}
			}
		});
	}


	private void createGrpClinicManagementReports() {
		grpClinicManagementReports = new Group(getShell(), SWT.NONE);
		grpClinicManagementReports.setBounds(new Rectangle(100, 305, 325, 200));
		grpClinicManagementReports
		.setText(Messages.getString("NewReports.section.clinic")); //$NON-NLS-1$
		grpClinicManagementReports.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_12));

		lblPicClinicManagementReports = new Label(grpClinicManagementReports,
				SWT.NONE);
		lblPicClinicManagementReports
		.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 0, 50, 43));
		lblPicClinicManagementReports.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PACKAGESSCANNEDIN));

		tblClinicManagementReports = new Table(grpClinicManagementReports,
				SWT.BORDER);

		tblClinicManagementReports.setBounds(new Rectangle(20, 50, 285, 130));
		tblClinicManagementReports.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		TableColumn tblColReportsAvailable = new TableColumn(
				tblClinicManagementReports, SWT.NONE);
		tblColReportsAvailable.setWidth(270);
		tblColReportsAvailable.setText(Messages.getString("NewReports.table.title")); //$NON-NLS-1$

		tblClinicManagementReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				clearSelections();
				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblClinicManagementReports.getItem(pt);
				if (item != null) {
					launchReport(item);
					tblClinicManagementReports.select(tblClinicManagementReports.indexOf(item));
				}

			}
		});
	}

	/**
	 * Method launchReport.
	 * 
	 * @param item
	 *            TableItem
	 */
	private void launchReport(TableItem item) {
		try {
			GenericReportGui g = reportGUIs.get(item.getText());
			g.openShell();
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);

		}
	}

	private void createGrpStockReports() {
		grpStockReports = new Group(getShell(), SWT.NONE);
		grpStockReports.setBounds(new Rectangle(475, 80, 325, 200));
		grpStockReports.setText(Messages.getString("NewReports.section.stock")); //$NON-NLS-1$
		grpStockReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

		lblPicStockReports = new Label(grpStockReports, SWT.NONE);
		lblPicStockReports.setBounds(new org.eclipse.swt.graphics.Rectangle(10,
				0, 50, 43));
		lblPicStockReports.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_STOCKCONTROLPERDRUG));

		tblStockReports = new Table(grpStockReports, SWT.BORDER);

		tblStockReports.setBounds(new Rectangle(20, 50, 285, 130));
		tblStockReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColReportsAvailable = new TableColumn(tblStockReports,
				SWT.NONE);
		tblColReportsAvailable.setWidth(270);
		tblColReportsAvailable.setText(Messages.getString("NewReports.table.title")); //$NON-NLS-1$
		tblStockReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				clearSelections();
				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblStockReports.getItem(pt);
				if (item != null) {
					launchReport(item);
					tblStockReports.select(tblStockReports.indexOf(item));
				}
			}
		});
	}

	private void createGrpMandEReports() {
		grpMandEReports = new Group(getShell(), SWT.NONE);
		grpMandEReports.setBounds(new Rectangle(475, 305, 325, 200));
		grpMandEReports.setText(Messages.getString("NewReports.section.m_and_e")); //$NON-NLS-1$
		grpMandEReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

		lblPicMandEReports = new Label(grpMandEReports, SWT.NONE);
		lblPicMandEReports.setBounds(new org.eclipse.swt.graphics.Rectangle(10,
				0, 50, 43));
		lblPicMandEReports.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_STOCKCONTROLPERCLINIC));

		tblMandEReports = new Table(grpMandEReports, SWT.BORDER);
		tblMandEReports.setBounds(new Rectangle(20, 50, 285, 130));
		tblMandEReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColReportsAvailable = new TableColumn(tblMandEReports,
				SWT.NONE);
		tblColReportsAvailable.setWidth(270);
		tblColReportsAvailable.setText(Messages.getString("NewReports.table.title")); //$NON-NLS-1$

		tblMandEReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				clearSelections();
				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblMandEReports.getItem(pt);
				if (item != null) {
					launchReport(item);
					tblMandEReports.select(tblMandEReports.indexOf(item));
				}
			}
		});
	}

	/**
	 * This method initializes compBackButton
	 */
	protected void createCompBackButton() {
		buildCompBackButton();
	}

	private void populateReportLists() {

		// Patient Reports
		reportGUIs.put(GenericReportGuiInterface.REPORT_PATIENT_HISTORY,
				new PatientHistory(getShell(), false));
		reportGUIs.put(
				GenericReportGuiInterface.REPORT_EPISODES_STARTED_OR_ENDED,
				new EpisodesStartedOrEndedReportGUI(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_PACKAGE_TRACKING,
				new PackageTracking(getShell(), false));
		
		
		// Stock Reports
		reportGUIs.put(GenericReportGuiInterface.REPORT_MONTHLY_STOCK_RECEIPTS,
				new MonthlyStockReceipt(getShell(), false));
		reportGUIs.put(
				GenericReportGuiInterface.REPORT_DAILY_DISPENSING_TOTALS,
				new DailyDispensingTotals(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_STOCK_TAKE,
				new StockTakeReportGUI(
						getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_DRUGS_DISPENSED,
				new DrugsDispensed(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_COHORT_COLLECTIONS,
				new CohortDrugCollections(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_MONTHLY_RECEIPT_ISSUE,
				new MonthlyReceiptsAndIssues(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_ARV_DRUG_USAGE,
				new ARVDrugUsage(getShell(), false));
		
		// Clinic Management Reports
		
		PackageProcessingReportGUI packsCreated = new PackageProcessingReportGUI(
				getShell(), false);
		packsCreated.setPackageStage(PackageLifeStage.PACKED);
		reportGUIs.put(GenericReportGuiInterface.REPORT_PACKAGES_CREATED,
				packsCreated);
		
		PackageProcessingReportGUI packsLeft = new PackageProcessingReportGUI(
				getShell(), false);
		packsLeft.setPackageStage(PackageLifeStage.SCANNED_OUT);
		reportGUIs.put(GenericReportGuiInterface.REPORT_PACKAGES_LEAVING,
				packsLeft);
		
		PackageProcessingReportGUI packsRec = new PackageProcessingReportGUI(
				getShell(), false);
		packsRec.setPackageStage(PackageLifeStage.SCANNED_IN);
		reportGUIs.put(GenericReportGuiInterface.REPORT_PACKAGES_RECEIVED,
				packsRec);
		
		PackageProcessingReportGUI packsCollected = new PackageProcessingReportGUI(
				getShell(), false);
		packsCollected.setPackageStage(PackageLifeStage.PICKED_UP);
		reportGUIs.put(GenericReportGuiInterface.REPORT_PACKAGES_COLLECTED,
				packsCollected);
		
		reportGUIs.put(
				GenericReportGuiInterface.REPORT_PACKAGES_AWAITING_PICKUP,
				new PackagesAwaiting(getShell(), false));
		
		reportGUIs.put(
				GenericReportGuiInterface.REPORT_PATIENTS_EXPECTED_ON_A_DAY,
				new PatientsExpected(getShell(), false));
		
		reportGUIs.put(GenericReportGuiInterface.REPORT_MISSED_APPOINTMENTS,
				new MissedAppointments(getShell(), false));
		
		
		
		// M & E Reports
		reportGUIs.put(GenericReportGuiInterface.REPORT_DRUG_COMBINATIONS,
				new DrugCombinations(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_EPISODES_STATS,
				new EpisodeStats(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_TRANSACTION_LOG,
				new TransactionLog(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_PRESCRIBING_DOCTORS,
				new PrescribingDoctors(getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_PEPFAR,
				new PepfarReportGUI(
						getShell(), false));
		reportGUIs.put(GenericReportGuiInterface.REPORT_CLINIC_INDICATORS,
				new ClinicIndicators(getShell(), false));
		
		
		

		Iterator<Map.Entry<String, GenericReportGui>> reportGUIsItr = reportGUIs
		.entrySet().iterator();

		while (reportGUIsItr.hasNext()) {
			Map.Entry<String, GenericReportGui> nextPair = reportGUIsItr.next();

			switch (nextPair.getValue().getReportType()) {
			case GenericReportGuiInterface.REPORTTYPE_PATIENT:
				TableItem ti = new TableItem(tblPatientReports, SWT.NONE);
				ti.setText(nextPair.getKey());

				break;
			case GenericReportGuiInterface.REPORTTYPE_CLINICMANAGEMENT:
				TableItem ti2 = new TableItem(tblClinicManagementReports,
						SWT.NONE);
				ti2.setText(nextPair.getKey());

				break;
			case GenericReportGuiInterface.REPORTTYPE_STOCK:
				TableItem ti3 = new TableItem(tblStockReports, SWT.NONE);
				ti3.setText(nextPair.getKey());

				break;
			case GenericReportGuiInterface.REPORTTYPE_MONITORINGANDEVALUATION:
				TableItem ti4 = new TableItem(tblMandEReports, SWT.NONE);
				ti4.setText(nextPair.getKey());

				break;
			}
		}
	}

	@Override
	protected void createCompOptions() {
		// create the composites
		// Std Options composite ResourceUtils.getColor(iDartColor.RED)uced for
		// this
		// class.
		getCompOptions().setBounds(new Rectangle(0, 0, 0, 0));
		createMyGroups();
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	protected void createCompButtons() {
		compButton = new Composite(getShell(), SWT.NONE);
		compButton.setBounds(new Rectangle(40, 520, 680, 40));
		btnDataExport = new Button(compButton, SWT.NONE);
		btnDataExport.setText(Messages.getString("NewReports.button.export")); //$NON-NLS-1$
		btnDataExport.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDataExport.setBounds(new Rectangle(120, 5, 200, 30));
		btnDataExport
		.setToolTipText(Messages.getString("NewReports.button.export.tooltip")); //$NON-NLS-1$
		btnDataExport
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdDataExportsSelected();
			}
		});
		btnDataQuality = new Button(compButton, SWT.NONE);
		btnDataQuality.setText(Messages.getString("NewReports.button.dataQuality")); //$NON-NLS-1$
		btnDataQuality.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDataQuality.setBounds(new Rectangle(500, 5, 180, 30));
		btnDataQuality.setToolTipText(Messages.getString("NewReports.button.dataQuality.tooltip")); //$NON-NLS-1$
		btnDataQuality
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdDataQualitySelected();
			}
		});
	}

	/**
	 * Deselects all previously selected values in the tables
	 *
	 */
	private void clearSelections() {
		for(int i = 0; i < tblClinicManagementReports.getItemCount(); i++) {
			tblClinicManagementReports.deselect(i);
		}

		for(int i = 0; i < tblMandEReports.getItemCount(); i++) {
			tblMandEReports.deselect(i);
		}

		for(int i = 0; i < tblPatientReports.getItemCount(); i++) {
			tblPatientReports.deselect(i);
		}

		for(int i = 0; i < tblStockReports.getItemCount(); i++) {
			tblStockReports.deselect(i);
		}

	}

	public void cmdDataExportsSelected() {
		new DataExport(getShell());
	}

	public void cmdDataQualitySelected() {
		new DataQuality(getShell());
	}
	
	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	@Override
	protected void cmdCloseSelectedWidget() {
		cmdCloseSelected();
	}

}
