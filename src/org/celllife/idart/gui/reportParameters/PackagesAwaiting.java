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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.reports.PackagesAwaitingReport;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 */
public class PackagesAwaiting extends GenericReportGui {

	private Label lblClinic;

	private Clinic localClinic;

	private Table tblPackages;

	private TableColumn currentOrderingColumn = null;

	private boolean currentOrderingIsAscending = true;

	SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param active
	 *            boolean
	 */
	public PackagesAwaiting(Shell parent, boolean active) {
		super(parent, REPORTTYPE_CLINICMANAGEMENT, active);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		buildShell(REPORT_PACKAGES_AWAITING_PICKUP, new Rectangle(100, 200, 800, 600));
		// create the composites
		createMyGroups();
		populatePackageTable();
	}

	protected void createMyGroups() {
		createGrpClinicSelection();
		createCompInstructions();
		createCompPackagesTable();

	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		iDartImage icoImage = iDartImage.REPORT_ACTIVEPATIENTS;
		buildCompdHeader(REPORT_PACKAGES_AWAITING_PICKUP, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {
		Group grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setText("");
		grpClinicSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpClinicSelection.setBounds(new Rectangle(207, 71, 386, 60));

		lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(60, 25, 100, 20));
		lblClinic.setText("Select Clinic:");
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		final CCombo cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setBounds(new Rectangle(216, 25, 160, 20));
		cmbClinic.setEditable(false);
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateClinics(getHSession(), cmbClinic);
		localClinic = AdministrationManager.getClinic(getHSession(), cmbClinic
				.getText());
		cmbClinic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				localClinic = AdministrationManager.getClinic(getHSession(),
						cmbClinic.getText());

				populatePackageTable();
			}
		});

	}

	private void createCompInstructions() {
		Composite compInstructions = new Composite(getShell(), SWT.NONE);
		compInstructions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		compInstructions.setBounds(new Rectangle(30, 150, 740, 20));

		Label lblInstructions = new Label(compInstructions, SWT.CENTER);
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblInstructions.setBounds(new Rectangle(0, 2, 700, 15));
		lblInstructions
				.setText("Click a Column Header to Order by That Column. Click Again to Reverse the Ordering");

	}

	private void createCompPackagesTable() {
		Composite compPackagesTable = new Composite(getShell(), SWT.NONE);
		compPackagesTable.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		compPackagesTable.setBounds(new Rectangle(30, 170, 740, 320));

		tblPackages = new Table(compPackagesTable, SWT.BORDER);
		tblPackages.setHeaderVisible(true);
		tblPackages.setLinesVisible(true);
		tblPackages.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblPackages.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0, 740,
				320));

		final TableColumn tblColPackagesID = new TableColumn(tblPackages,
				SWT.NONE);
		tblColPackagesID.setWidth(120);

		tblColPackagesID.setText("Package ID");

		final TableColumn tblColPatName = new TableColumn(tblPackages, SWT.NONE);
		tblColPatName.setWidth(140);
		tblColPatName.setText("Patient Name");

		final TableColumn tblColDatePacked = new TableColumn(tblPackages,
				SWT.NONE);
		tblColDatePacked.setWidth(105);
		tblColDatePacked.setText("Date Packaged");

		final TableColumn tblColDateReceived = new TableColumn(tblPackages,
				SWT.NONE);
		tblColDateReceived.setWidth(105);
		tblColDateReceived.setText("Date Received");

		final TableColumn tblColDateExpected = new TableColumn(tblPackages,
				SWT.NONE);
		tblColDateExpected.setWidth(130);
		tblColDateExpected.setText("Patient Expected On");

		final TableColumn tblColDaysSinceLastPickup = new TableColumn(
				tblPackages, SWT.NONE);
		tblColDaysSinceLastPickup.setWidth(100);
		tblColDaysSinceLastPickup.setText("Days Patient Is Late");
		addColumnHeaderListeners();

	}

	private void populatePackageTable() {

		if (localClinic == null)
			return;
		List<Packages> packagesAwaiting = new ArrayList<Packages>();
		packagesAwaiting = PackageManager.getPackagesAwaitingCollection(
				getHSession(), localClinic.getClinicName());
		tblPackages.removeAll();

		for (Packages p : packagesAwaiting) {
			Patient pat = p.getPrescription().getPatient();
			Appointment app = PatientManager
					.getLatestActiveAppointmentForPatient(pat);

			String[] tiString = new String[6];

			tiString[0] = p.getPackageId();
			tiString[1] = pat.getLastname() + ", " + pat.getFirstNames();
			tiString[2] = p.getPackDate() != null ? sdf.format(p.getPackDate())
					: "N/A";
			tiString[3] = p.getDateReceived() != null ? sdf.format(p
					.getDateReceived()) : "N/A";
			if (app != null) {
				Date theDateExpected = app.getAppointmentDate();
				Calendar calToday = Calendar.getInstance();
				Calendar lastPickup = Calendar.getInstance();
				lastPickup.setTime(theDateExpected);
				long numOfDays = (calToday.getTimeInMillis() - lastPickup
						.getTimeInMillis())
						/ 1000 / 60 / 60 / 24;

				tiString[4] = sdf.format(theDateExpected);
				tiString[5] = numOfDays < 0 ? "0" : (new Long(numOfDays))
						.toString();

			} else {
				tiString[4] = "Initial Pickup";
				tiString[5] = "0";
			}

			TableItem ti = new TableItem(tblPackages, SWT.NONE);
			ti.setText(tiString);

		}
	}

	private void addColumnHeaderListeners() {
		Listener sortListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!(event.widget instanceof TableColumn))
					return;
				TableColumn tc = (TableColumn) event.widget;

				if ((currentOrderingColumn != null)
						&& (currentOrderingColumn.equals(tc))) {
					currentOrderingIsAscending = !currentOrderingIsAscending;
				} else {
					currentOrderingColumn = tc;
					currentOrderingIsAscending = true;
				}

				reorderByColumn(tblPackages.indexOf(tc),
						currentOrderingIsAscending);
				getLog().debug(
						"The table is sorted by column '"
								+ tc.getText()
								+ (currentOrderingIsAscending ? "' ascending"
										: "' descending"));
			}
		};

		for (int i = 0; i < tblPackages.getColumnCount(); i++) {
			(tblPackages.getColumn(i)).addListener(SWT.Selection, sortListener);
		}
	}

	/**
	 * Method reorderByColumn.
	 * 
	 * @param tcIndex
	 *            int
	 * @param asc
	 *            boolean
	 */
	private void reorderByColumn(int tcIndex, boolean asc) {
		final int tcInd = tcIndex;
		final boolean isAsc = asc;

		/**
		 */
		class tiComparator implements Comparator<TableItem> {
			/**
			 * Method compare.
			 * 
			 * @param ti1
			 *            TableItem
			 * @param ti2
			 *            TableItem
			 * @return int
			 */
			@Override
			public int compare(TableItem ti1, TableItem ti2) {
				int returnInt;
				String tiCol1 = ti1.getText(tcInd);
				String tiCol2 = ti2.getText(tcInd);

				if (tcInd == 0 || tcInd == 1) // String cols
				{
					returnInt = tiCol1.compareTo(tiCol2);

				}

				if (tcInd == 2 || tcInd == 3 || tcInd == 4)// date cols
				{
					try {
						returnInt = sdf.parse(tiCol1).compareTo(
								sdf.parse(tiCol2));
					} catch (ParseException p) {
						returnInt = 0;
					}
				} else if (tcInd == 5) // days late col
				{
					try {
						String[] s1 = tiCol1.split("\\s");
						Integer theDays1 = Integer.parseInt(s1[0]);

						String[] s2 = tiCol2.split("\\s");
						Integer theDays2 = Integer.parseInt(s2[0]);

						returnInt = theDays1.compareTo(theDays2);

					} catch (NumberFormatException p) {
						returnInt = 0;
					}
				} else {
					returnInt = tiCol1.compareTo(tiCol2);
				}

				if (isAsc)
					return returnInt;
				else
					return returnInt * -1;

			}
		}

		TableItem[] tableItems = tblPackages.getItems();
		Arrays.sort(tableItems, new tiComparator());

		for (int i = 0; i < tableItems.length; i++) {
			TableItem item = new TableItem(tblPackages, SWT.NULL);
			for (int j = 0; j < tblPackages.getColumnCount(); j++) {
				item.setText(j, tableItems[i].getText(j));
				item.setImage(j, tableItems[i].getImage(j));
			}
			tableItems[i].dispose();
		}

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

		if (localClinic == null) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Clinic Was Selected");
			missing
					.setMessage("No clinic was selected. Please select a clinic by looking through the list of available clinics.");
			missing.open();
			viewReport = false;

		}

		if (viewReport) {
			List<String[]> stringList = getDataOutOfTable();
			PackagesAwaitingReport report = new PackagesAwaitingReport(
					getShell(), localClinic.getClinicName(), stringList);
			viewReport(report);
		}

	}

	private List<String[]> getDataOutOfTable() {
		List<String[]> theStringList = new ArrayList<String[]>();
		String[] rowStr = new String[tblPackages.getColumnCount()];
		for (int i = 0; i < tblPackages.getColumnCount(); i++) {
			rowStr[i] = "col" + i;
		}
		// print the header
		theStringList.add(0, rowStr);

		TableItem[] items = tblPackages.getItems();

		for (TableItem ti : items) {
			rowStr = new String[tblPackages.getColumnCount()];
			for (int i = 0; i < tblPackages.getColumnCount(); i++) {

				rowStr[i] = ti.getText(i);
			}
			theStringList.add(rowStr);
		}
		return theStringList;
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
