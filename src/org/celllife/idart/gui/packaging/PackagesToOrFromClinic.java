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

package org.celllife.idart.gui.packaging;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.AdministrationManager;
import model.manager.PackageManager;
import model.manager.reports.PackageProcessingReport;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.reportParameters.PackagesScannedOut;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.celllife.idart.misc.DateFieldComparator;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.model.utils.PackageLifeStage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

/**
 */
public class PackagesToOrFromClinic extends GenericReportGui {

	private Group grpClinicSelection;

	private CCombo cmbClinic;

	private Group grpPackageDetails;

	private Label lblAwaiting;

	private List lstAwaiting;

	private Label lblPackageIdScan;

	private Text txtPackageIdScan;

	private Label lblScanned;

	private List lstScanned;

	private boolean isScanOut;

	private DateButton btnScanDate;

	private Button btnPrintCollectionSheets;

	private Map<String, Packages> packageIdMap;

	private Packages scannedPackage;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public PackagesToOrFromClinic(Shell parent) {
		super(parent, HibernateUtil.getNewSession(), REPORTTYPE_STOCK);
		activate();
		btnScanDate.setDate(new Date());
	}

	/**
	 * This method initializes getMyShell()
	 */
	@Override
	protected void createShell() {
		isScanOut = ((Boolean) getInitialisationOption("isScanOut"))
		.booleanValue();
		String shellTxt = isScanOut ? "Scan Out Packages To Down Referral Clinic"
				: "Packages Arriving";
		buildShell(shellTxt, new Rectangle(5, 0, 850, 700));
		createCompClinicSelection();
		createGrpPackageDetails();
		updateListsOnClinicLoad(cmbClinic.getText());
	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = isScanOut ? "Scan Out Packages To Down Referral Clinic"
				: "Packages Arrive";
		iDartImage icoImage = iDartImage.PACKAGESARRIVE;
		if (isScanOut) {
			icoImage = iDartImage.OUTGOINGPACKAGES;
		}

		buildCompdHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes compClinicSelection
	 */
	private void createCompClinicSelection() {

		grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setBounds(new org.eclipse.swt.graphics.Rectangle(
				150, 85, 540, 45));

		Label lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 18, 160,
				20));
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblClinic.setText(isScanOut ? "Packages Scanned Out To:"
				: "Scan in Packages at Clinic: ");
		cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setBounds(new org.eclipse.swt.graphics.Rectangle(175, 15,
				180, 20));
		cmbClinic.setEditable(false);
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbClinic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				cmbClinicWidgetSelected();
			}
		});

		CommonObjects.populateClinics(getHSession(), cmbClinic, false);

		if (cmbClinic.getEnabled()) {
			cmbClinic.setFocus();
		}

		Label lblOn = new Label(grpClinicSelection, SWT.None);
		lblOn.setBounds(new Rectangle(370, 18, 20, 15));
		lblOn.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblOn.setText("on");

		btnScanDate = new DateButton(
				grpClinicSelection,
				DateButton.NONE,
				new DateInputValidator(DateRuleFactory.beforeNowInclusive(true)));
		btnScanDate.setBounds(new Rectangle(398, 10, 125, 27));
		btnScanDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	/**
	 * This method initializes grpPackageDetails
	 */
	private void createGrpPackageDetails() {

		grpPackageDetails = new Group(getShell(), SWT.NONE);
		grpPackageDetails.setText("Package Details");
		grpPackageDetails.setBounds(new org.eclipse.swt.graphics.Rectangle(100,
				150, 630, 430));
		grpPackageDetails.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblAwaiting = new Label(grpPackageDetails, SWT.CENTER | SWT.BORDER
				| SWT.V_SCROLL);
		lblAwaiting.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblAwaiting.setText(isScanOut ? "Packages Awaiting Dispatch"
				: "Packages In Transit");
		lblAwaiting.setBounds(new org.eclipse.swt.graphics.Rectangle(25, 30,
				180, 20));
		lstAwaiting = new List(grpPackageDetails, SWT.BORDER | SWT.V_SCROLL);
		lstAwaiting.setBounds(new org.eclipse.swt.graphics.Rectangle(25, 50,
				180, 350));
		lstAwaiting.isFocusControl();

		lblPackageIdScan = new Label(grpPackageDetails, SWT.CENTER);
		lblPackageIdScan.setBounds(new org.eclipse.swt.graphics.Rectangle(215,
				175, 190, 20));
		lblPackageIdScan.setText("Please scan in the Package ID");
		lblPackageIdScan.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackageIdScan = new Text(grpPackageDetails, SWT.BORDER);
		txtPackageIdScan.setBounds(new org.eclipse.swt.graphics.Rectangle(215,
				195, 190, 20));
		txtPackageIdScan.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPackageIdScan.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.character == SWT.CR) {
					cmdEnterPressed();
				}
			}
		});
		txtPackageIdScan.setEnabled(true);
		txtPackageIdScan.setFocus();

		lblScanned = new Label(grpPackageDetails, SWT.BORDER | SWT.CENTER);
		lblScanned.setBounds(new org.eclipse.swt.graphics.Rectangle(420, 30,
				180, 20));
		lblScanned.setText("Scanned Packages");
		lblScanned.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lstScanned = new List(grpPackageDetails, SWT.BORDER | SWT.V_SCROLL);
		lstScanned.setBounds(new Rectangle(420, 50, 180, 350));

	}

	/**
	 * This method initializes compButtons
	 */
	@Override
	protected void createCompButtons() {

		if (isScanOut) {
			btnPrintCollectionSheets = new Button(getCompButtons(), SWT.NONE);
			btnPrintCollectionSheets.setText("Print Collection Sheets");
			btnPrintCollectionSheets.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			btnPrintCollectionSheets
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmdPrintCollectionSheetsSelected();
				}
			});
		}
	}

	private boolean fieldsOk() {

		Date packDate = (Date) scannedPackage.getPackDate().clone();

		// certain packages will not have a date left and thus we cannot clone
		// it without testing for a null
		Date dateLeft = scannedPackage.getDateLeft() != null ? (Date) scannedPackage
				.getDateLeft().clone()
				: null;

				Date receivedDate = (Date) btnScanDate.getDate().clone();

				// Packages scanned to remote clinic
				if (isScanOut) {
					if (dateLeft != null) {
						// If date the package left is NOT before pack date
						if (DateFieldComparator.compare(packDate, dateLeft,
								Calendar.DAY_OF_MONTH) > 0) {
							MessageBox error = new MessageBox(getShell(),
									SWT.ICON_ERROR);
							error.setText("Incorrect Date");
							error.setMessage("Package '"
									+ scannedPackage.getPackageId()
									+ "' was created on " + iDARTUtil.format(packDate)
									+ ". \n\nThe date that this package leaves "
									+ "the pharmacy must be after this date.");
							error.open();
							return false;
						}
					}
				} else {
					// Packages scanned into remote clinic
					if (DateFieldComparator.compare(receivedDate, dateLeft,
							Calendar.DAY_OF_MONTH) < 0) {
						MessageBox error = new MessageBox(getShell(), SWT.OK
								| SWT.ICON_ERROR);
						error.setText("Incorrect Date");
						error.setMessage("Package '" + scannedPackage.getPackageId()
								+ "' was scanned out of the pharmacy on "
								+ iDARTUtil.format(dateLeft)
								+ ". \n\nThe date that this package is received "
								+ "must be after this date.");
						error.open();
						return false;
					}
				}

				return true;
	}

	/**
	 * This method is called when the user when anything is entered into the
	 * text field for the package id. If that key press is "Enter" and all error
	 * checks are passed, the package is updated in the database, and so are the
	 * 2 lists on the GUI
	 * 
	 * This screen is for scanning packes to remote clinics, so only the date
	 * left is updated when the package is scanned
	 * 
	 */
	private void cmdEnterPressed() {

		// returns -1 if no match is found
		txtPackageIdScan.setText(txtPackageIdScan.getText().toUpperCase());
		if (lstAwaiting.indexOf(txtPackageIdScan.getText()) != -1) {

			scannedPackage = packageIdMap.get(txtPackageIdScan.getText()
					.toUpperCase());

			// used the dtePackScan button to set the dateLeft or dateRecieved
			// attributes of the scanned package, this is needed here since the
			// fieldsOk method uses it to test correctness of dates
			if (isScanOut) {
				scannedPackage.setDateLeft(btnScanDate.getDate());
			} else {
				scannedPackage.setDateReceived(btnScanDate.getDate());
			}

			if (scannedPackage != null && fieldsOk()) {
				Transaction tx = null;
				try {
					tx = getHSession().beginTransaction();
					getHSession().merge(scannedPackage);
					getHSession().flush();
					tx.commit();
					movePackageOnScan();

				} catch (HibernateException he) {
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					m.setText("Problem With Database");
					m
					.setMessage("There was a problem accessing the database with this information.");
					m.open();
					if (tx != null) {
						tx.rollback();
					}
				}

				if (getHSession() != null) {
					getHSession().close();
				}
				setHSession(HibernateUtil.getNewSession());
			}
		}

		else {
			MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
			m.setText("Package Not Found");
			m.setMessage("Package '"
					+ txtPackageIdScan.getText()
					+ "' was not found in the list of "
					+ (isScanOut ? "packages awaiting dispatch."
							: "packages in transit"));
			m.open();

			// set the focus back to the packageIdscan field and highlight the
			// incorrect package id.

			txtPackageIdScan.selectAll();
		}

	}

	/**
	 * This method is called when the user chooses a clinic from the drop down
	 * list. Doing this, updates the list of packages awaiting to be dispatched
	 * from the pharmacy.
	 * 
	 */
	private void cmbClinicWidgetSelected() {
		updateListsOnClinicLoad(cmbClinic.getText());
		txtPackageIdScan.setEnabled(true);
		txtPackageIdScan.setFocus();
	}

	/**
	 * This method is called when the user presses the "View Report" button. It
	 * calls the ReportManger, and passes the clinic name and current date.
	 * 
	 */
	@Override
	protected void cmdViewReportWidgetSelected() {
		if (cmbClinic.getText().trim().equals(""))
			return;

		Clinic c = AdministrationManager.getClinic(getHSession(), cmbClinic
				.getText().trim());

		PackageProcessingReport report = new PackageProcessingReport(
				getShell(), c, btnScanDate.getDate(),
				btnScanDate.getDate(),
				isScanOut ? PackageLifeStage.SCANNED_OUT
						: PackageLifeStage.SCANNED_IN);
		viewReport(report);

	}

	/**
	 * This method is called when the user pressed the "Close" button. It closes
	 * the active window
	 * 
	 */
	@Override
	protected void cmdCloseWidgetSelected() {
		cmdCloseSelected();
	}

	/**
	 * This method updates the two lists: - packages waiting to be dispatched,
	 * and - packages that have been scanned out of the pharmacy
	 * 
	 */
	private void movePackageOnScan() {

		int index = lstAwaiting.indexOf(txtPackageIdScan.getText()
				.toUpperCase());

		String move = lstAwaiting.getItem(index);
		lstScanned.add(move);
		lstAwaiting.remove(index);
		txtPackageIdScan.setText("");
	}

	/**
	 * This method gets all the packages that are awaiting dispatch for a given
	 * clinic, and adds these packages to the GUI-list.
	 * 
	 * 
	 * @param clinic
	 */
	private void updateListsOnClinicLoad(String clinic) {

		if (clinic.trim().equals(""))
			return;

		getLog().info("looking for packages for clinic " + clinic);

		lstAwaiting.removeAll();
		lstScanned.removeAll();

		java.util.List<Packages> packageList;

		if (isScanOut) {
			packageList = PackageManager.getPackagesAwaitingScanOut(
					getHSession(), clinic);
		} else {
			packageList = PackageManager.getPackagesInTransit(getHSession(),
					clinic);
		}

		packageIdMap = new HashMap<String, Packages>();

		for (int i = 0; i < packageList.size(); i++) {
			Packages p = packageList.get(i);
			String id = p.getPackageId();
			packageIdMap.put(id, p);
			lstAwaiting.add(id);
		}
	}

	private void cmdPrintCollectionSheetsSelected() {
		new PackagesScannedOut(getShell(), true, btnScanDate.getDate(),
				cmbClinic
				.getText());
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}
}
