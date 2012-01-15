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

import java.util.List;
import java.util.Vector;

import model.manager.PatientManager;
import model.manager.SearchManager;
import model.manager.reports.PackageTrackingReport;
import model.nonPersistent.PatientIdAndName;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 */
public class PackageTracking extends GenericReportGui {

	private Table tblWaitingPatients;

	private TableColumn tblColPatId;

	private TableColumn tblColPatName;

	private java.util.Vector<PatientIdAndName> allPatientsAtClinic = new Vector<PatientIdAndName>();

	private Group grpPatientSelection;

	private Text searchBar;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public PackageTracking(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_PATIENT, activate);
	}

	/**
	 * This method initializes newStockReceipt
	 */
	@Override
	protected void createShell() {

		buildShell(REPORT_PACKAGE_TRACKING, new Rectangle(100, 50, 500, 600));
		// create the composites
		createMyGroups();
	}

	private void createMyGroups() {
		createTableAndSearchBar();
		populatePatients();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		iDartImage icoImage = iDartImage.REPORT_PACKAGETRACKING;
		buildCompdHeader(REPORT_PACKAGE_TRACKING, icoImage);
	}

	/**
	 * This method initializes the table and search bar components
	 * 
	 */
	private void createTableAndSearchBar() {

		if (grpPatientSelection != null) {
			grpPatientSelection.dispose();
		}
		grpPatientSelection = new Group(getShell(), SWT.NONE);
		grpPatientSelection
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientSelection.setText("Select a Patient");
		grpPatientSelection.setBounds(new Rectangle(80, 80, 340, 440));

		tblWaitingPatients = new Table(grpPatientSelection, SWT.FULL_SELECTION);
		tblWaitingPatients.setBounds(new Rectangle(20, 35, 306, 365));
		tblWaitingPatients.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblWaitingPatients.setHeaderVisible(true);

		tblColPatId = new TableColumn(tblWaitingPatients, SWT.NONE);
		tblColPatId.setWidth(100);
		tblColPatId.setText("Patient Number");

		tblColPatName = new TableColumn(tblWaitingPatients, SWT.NONE);
		tblColPatName.setWidth(150);
		tblColPatName.setText("Patient Name");

		searchBar = new Text(grpPatientSelection, SWT.BORDER);
		searchBar.setBounds(new Rectangle(17, 409, 311, 20));
		searchBar.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		searchBar.setFocus();
		searchBar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				minimiseSearch(tblWaitingPatients, searchBar.getText(),
						allPatientsAtClinic);

				if ((e.character == SWT.CR)
						|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
					enterPressedInPatientSearchBar();
				}
			}
		});

	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
	}

	/**
	 * This method is called when the user presses the 'View Report' button. It
	 * calls the ReportManager.viewStockReceiptReport with the appropriate start
	 * and end dates, as given by the user.
	 * 
	 */
	@Override
	protected void cmdViewReportWidgetSelected() {

		if ((tblWaitingPatients.getSelection().length != 0)) {
			PackageTrackingReport report = new PackageTrackingReport(
					getShell(), tblWaitingPatients
					.getSelection()[0].getText(0));
			viewReport(report);
		}
		else if(!"".equals(searchBar.getText())) {
			enterPressedInPatientSearchBar();
		}else {
			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Patient Selected");
			missing.setMessage("Please select a patient");
			missing.open();

		}
		searchBar.setText("");
		searchBar.setFocus();

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
	 * Method minimiseSearch.
	 * @param t Table
	 * @param searchString String
	 * @param fullList List<PatientIdAndName>
	 */
	public static void minimiseSearch(Table t, String searchString,
			List<PatientIdAndName> fullList) {
		t.removeAll();
		for (int i = 0; i < fullList.size(); i++) {
			int found1 = 0;
			int found2 = 0;

			PatientIdAndName p = fullList.get(i);
			found1 = p.getPatientId().toUpperCase().indexOf(
					searchString.toUpperCase());
			found2 = p.getNames().toUpperCase().indexOf(
					searchString.toUpperCase());

			if (found1 != -1 || found2 != -1) {
				TableItem tableItem = new TableItem(t, SWT.NONE);
				String[] newStrings = new String[2];
				newStrings[0] = p.getPatientId();
				newStrings[1] = p.getNames();
				tableItem.setText(newStrings);
			}

		}

	}

	private void populatePatients() {

		java.util.List<PatientIdAndName> l = SearchManager
		.getPatientIDsAndNames(getHSession(), true, false);

		tblWaitingPatients.removeAll();
		allPatientsAtClinic = new Vector<PatientIdAndName>();
		for (int i = 0; i < l.size(); i++) {
			PatientIdAndName patStr = l.get(i);

			TableItem ti = new TableItem(tblWaitingPatients, SWT.NONE);
			ti.setText(0, patStr.getPatientId());
			ti.setText(1, patStr.getNames());

			allPatientsAtClinic.add(patStr);

		}
	}

	/**
	 *
	 *
	 */
	private void enterPressedInPatientSearchBar() {

		String patientId = "";

		if(tblWaitingPatients.getItemCount() == 1) {
			patientId = tblWaitingPatients.getItem(0).getText();
		}
		searchBar.setText(searchBar.getText().toUpperCase());

		if(patientId.isEmpty()) {
			patientId = PatientBarcodeParser.getPatientId(searchBar.getText());
		}

		if(patientId == null){
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Patient number not entered");
			mb
			.setMessage("You have not entered a patient number. \n\nPlease enter a patient number.");
			mb.open();
			searchBar.setText("");
			searchBar.setFocus();
			minimiseSearch(tblWaitingPatients, "", allPatientsAtClinic);
			return;
		}

		if (PatientManager.getPatient(getHSession(), patientId) != null) {
			PackageTrackingReport report = new PackageTrackingReport(
					getShell(), patientId);
			viewReport(report);
		} else {
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Patient Does Not Exist");
			mb
			.setMessage("There is no patient with ID '"
					+ searchBar.getText()
					+ "' in the database. \n\nIf this may be the old ID of a patient whose ID has changed, you can search by patients' previous IDs by going to the Update Patient screen and typing this ID there.");
			mb.open();
		}
		searchBar.setText("");
		searchBar.setFocus();
		minimiseSearch(tblWaitingPatients, "", allPatientsAtClinic);
	}

	@Override
	protected void setLogger() {
	}

}
