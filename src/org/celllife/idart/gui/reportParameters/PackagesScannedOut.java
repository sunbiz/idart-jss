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
import model.manager.PackageManager;
import model.manager.ReportManager;
import model.nonPersistent.PackagesWithSelection;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.utils.tableViewerUtils.MyColumnLabelProvider;
import org.celllife.idart.gui.utils.tableViewerUtils.MyContentProvider;
import org.celllife.idart.gui.utils.tableViewerUtils.TableSorter;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateChangedEvent;
import org.celllife.idart.gui.widget.DateChangedListener;
import org.celllife.idart.misc.DateFieldComparator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class PackagesScannedOut extends GenericReportGui {

	private Label lblClinic;

	private CCombo cmbClinic;

	private Clinic localClinic;

	private TableViewer tblViewer;

	private List<PackagesWithSelection> packageList;

	private List<Packages> packs;

	private List<String> selectedPatientIds;

	// Group patientSelection
	private Group grpPatientSelection;

	private Label lblSelect;
	private Label lblBlurb;

	private Button rdBtnSelectAll;

	private Button rdBtnSelectNone;

	private Button rdBtnSelectOnDate;

	private DateButton btnDate;

	// Date picker

	private static String[] columnNames = { "Selected", "Patient Number",
		"Patient Name", "Date Packaged", "Date Left", "Patient Expected On" };

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param active
	 *            boolean
	 */
	public PackagesScannedOut(Shell parent, boolean active, Date dateReported,
			String clinicName) {
		super(parent, REPORTTYPE_CLINICMANAGEMENT, active);
		btnDate.setDate(dateReported);

		// Set defauls and initialise gui
		initialiseGui(clinicName);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {

		String shellTxt = "Patient Collection Sheets";
		buildShell(shellTxt, new Rectangle(0, 0, 900, 700));
		// create the composites
		createMyGroups();

	}

	protected void createMyGroups() {
		createGrpClinicSelection();
		createCompInstructions();
		createCompPackagesTable();
		createGrpPatientSelection();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {

		String headerTxt = "Patient Collection Sheets";
		iDartImage icoImage = iDartImage.REPORT_ACTIVEPATIENTS;
		buildCompdHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {
		Group grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setText("");
		grpClinicSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpClinicSelection.setBounds(new Rectangle(150, 80, 580, 97));

		lblBlurb = new Label(grpClinicSelection, SWT.CENTER | SWT.WRAP);
		lblBlurb.setBounds(2, 5, 575, 70);
		lblBlurb
		.setText("These paper forms should only be used if the down-referral clinic does not have iDART installed. In the table below, you'll see a list of all packages that have been created for patients at the selected Down Referral Clinic. These packages have left the pharmacy but have not yet been received at the clinic. You can print out a 'Patient Collection Sheet' to accompany each of these packages. The paper forms should be delivered to the clinic with the ARV packages.");
		lblBlurb.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));

		lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(130, 77, 150, 17));
		lblClinic.setText("Select Down Referral Clinic:");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setBounds(new Rectangle(280, 75, 160, 20));
		cmbClinic.setEditable(false);
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateClinics(getHSession(), cmbClinic, false);
		localClinic = AdministrationManager.getClinic(getHSession(), cmbClinic
				.getText());
		cmbClinic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				localClinic = AdministrationManager.getClinic(getHSession(),
						cmbClinic.getText());

				// generate new input for table
				updateTable();
			}
		});

	}

	private void createCompInstructions() {
		Composite compInstructions = new Composite(getShell(), SWT.NONE);
		compInstructions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		compInstructions.setBounds(new Rectangle(80, 180, 740, 23));

		Label lblInstructions = new Label(compInstructions, SWT.CENTER);
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblInstructions.setBounds(new Rectangle(0, 10, 700, 15));
		lblInstructions
		.setText("Click a Column Header to Order by That Column. Click Again to Reverse the Ordering");

	}

	private void createCompPackagesTable() {
		Composite compPackagesTable = new Composite(getShell(), SWT.NONE);
		compPackagesTable.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		compPackagesTable.setBounds(new Rectangle(40, 200, 820, 355));

		// generate new input for table
		createNewDataForTable();

		tblViewer = new TableViewer(compPackagesTable, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		tblViewer.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		// tblViewer = new TableViewer(compPackagesTable, SWT.BORDER);
		tblViewer.setContentProvider(new MyContentProvider(packageList));
		MyColumnLabelProvider columnsLabels = new MyColumnLabelProvider(
				columnNames);
		columnsLabels.createColumns(tblViewer);
		// Set the header to visible
		tblViewer.getTable().setHeaderVisible(true);
		// Set the line of the table visible
		tblViewer.getTable().setLinesVisible(true);
		tblViewer.setInput(packageList);
		tblViewer.getTable().setBounds(10, 10, 800, 370);
		tblViewer.setSorter(new TableSorter());
	}

	public void createGrpPatientSelection() {
		grpPatientSelection = new Group(getShell(), SWT.RADIO);
		grpPatientSelection.setText("");
		grpPatientSelection
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientSelection.setBounds(new Rectangle(160, 570, 580, 40));

		lblSelect = new Label(grpPatientSelection, SWT.CENTER);
		lblSelect.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblSelect.setBounds(new Rectangle(30, 15, 50, 20));
		lblSelect.setText("Select ");

		rdBtnSelectAll = new Button(grpPatientSelection, SWT.RADIO);
		rdBtnSelectAll.setBounds(new Rectangle(90, 12, 45, 20));
		rdBtnSelectAll.setText("All");
		rdBtnSelectAll.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnSelectAll
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				selectPatients();
			}
		});

		rdBtnSelectNone = new Button(grpPatientSelection, SWT.RADIO);
		rdBtnSelectNone.setBounds(new Rectangle(145, 12, 65, 20));
		rdBtnSelectNone.setText("None");
		rdBtnSelectNone.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnSelectNone
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				selectPatients();
			}
		});

		rdBtnSelectOnDate = new Button(grpPatientSelection, SWT.RADIO);
		rdBtnSelectOnDate.setBounds(new Rectangle(220, 12, 175, 20));
		rdBtnSelectOnDate.setText("On Date Left Pharmacy");
		rdBtnSelectOnDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnSelectOnDate.setSelection(true);
		rdBtnSelectOnDate
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				selectPatients();
			}
		});

		btnDate = new DateButton(grpPatientSelection, DateButton.NONE, null);
		btnDate.setBounds(new Rectangle(405, 10, 100, 25));
		btnDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				// Unfortunately the group only manages the radio buttons from
				// the interface
				// so I need to toggle the buttons manually in the back end
				rdBtnSelectOnDate.setSelection(true);
				rdBtnSelectAll.setSelection(false);
				rdBtnSelectNone.setSelection(false);
			}
		});
		btnDate.addDateChangedListener(new DateChangedListener() {
			@Override
			public void dateChanged(DateChangedEvent event) {
				selectPatients();
			}
		});

	}

	/**
	 * This method goes through the table and finds all the patients that were
	 * selected by the user
	 * 
	 * return true if 1 or more patients were selected
	 */
	private boolean getSelectedPatients() {

		selectedPatientIds = new ArrayList<String>();

		for (PackagesWithSelection pack : packageList) {
			if (pack.isSelected()) {
				selectedPatientIds.add(pack.getPackages().getPrescription()
						.getPatient().getPatientId());
			}
		}

		if (selectedPatientIds.size() > 0)
			return true;

		return false;
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnViewReport.setLayoutData(new RowData(250, 30));
		btnViewReport.setText("Print Collection Sheets");

	}

	@Override
	protected void cmdViewReportWidgetSelected() {

		if (fieldsOk()) {

			try {
				new ReportManager(getHSession(), getShell())
				.viewPatientCollectionSheet(selectedPatientIds);

			} catch (Exception e) {
				getLog().error(e.getStackTrace());
			}

		}
	}

	/**
	 * Validation is performed in this method
	 * 
	 * @return
	 */
	private boolean fieldsOk() {
		/**
		 * checks if the user selected a clinic
		 */
		if (localClinic == null) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Clinic Was Selected");
			missing
			.setMessage("No clinic was selected. Please select a clinic by looking through the list of available clinics.");
			missing.open();
			return false;

		}
		/**
		 * Checks if the user selected any patients
		 */
		else if (!getSelectedPatients()) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Patients were selected");
			missing
			.setMessage("No patients were selected from the table. \n\nPlease select one or more patients from the table "
					+ "before trying to view the report.");
			missing.open();
			return false;

		}

		return true;

	}

	private void createNewDataForTable() {
		packs = PackageManager.getPackagesInTransit(getHSession(), cmbClinic
				.getText());
		packageList = new ArrayList<PackagesWithSelection>();

		for (Packages pack : packs) {
			PackagesWithSelection packWitSelection = new PackagesWithSelection(
					pack, false);
			packageList.add(packWitSelection);
		}

	}

	/**
	 * Set the initial state for the gui
	 */
	private void initialiseGui(String clinicName) {
		cmbClinic.setText(clinicName);
		updateTable();
	}

	/**
	 * This method updates and refreshes the table
	 */
	private void updateTable() {
		createNewDataForTable();
		selectPatients();
	}

	/**
	 * This method checks the patients that match the selected radio option
	 */
	private void selectPatients() {

		for (PackagesWithSelection pack : packageList) {

			if (rdBtnSelectAll.getSelection()) {
				pack.setSelected(true);
			} else if (rdBtnSelectOnDate.getSelection()) {
				if (btnDate.getDate() != null
						&& DateFieldComparator.compare(pack.getPackages()
								.getDateLeft(), btnDate.getDate(),
								Calendar.DAY_OF_MONTH) == 0) {
					pack.setSelected(true);
				} else {
					pack.setSelected(false);
				}
			} else {
				pack.setSelected(false);
			}
		}
		tblViewer.setInput(packageList);
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
