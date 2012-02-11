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

package org.celllife.idart.gui.deletions;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.manager.DeletionsManager;
import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.commonobjects.iDartProperties.LabelType;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.user.ConfirmWithPasswordDialogAdapter;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.print.label.PrintThread;
import org.celllife.idart.print.label.ScriptSummaryLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

/**
 */
public class DeleteStockPrescriptionsPackages extends GenericOthersGui {

	private Group grpDeletionTypeSelection;

	private Button rdBtnStock;

	private Button rdBtnPackage;

	private Button rdBtnPrescription;

	private Button rdBtnItem;

	private Label lblPackage;

	private Label lblPrescription;

	private Label lblStock;

	private Label lblItem;

	private Group grpPatientInfo;

	private Label lblPatientId;

	private Text txtPatientId;

	private Button btnSearch;

	private Button btnClose;

	private Button btnClear;

	private Patient localPatient;

	private Drug localDrug;

	private Group grpPackageInfo;

	private Label lblPackageId;

	private Text txtPackageId;

	private Label lblDatePacked;

	private Text txtDatePacked;

	private Label lblDrugsInPackage;

	private Table tblDrugsInPackage;

	private Packages packageToRemove;

	private Prescription prescriptionToRemove;

	private List<Stock> stockToRemove;

	private Button btnRemovePackage;

	private KeyListener patientIDListener;

	/**
	 * @param parent
	 */
	public DeleteStockPrescriptionsPackages(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		activate();
	}

	/**
	 * @param parent
	 * @param patient
	 */
	public DeleteStockPrescriptionsPackages(Shell parent, Patient patient) {
		super(parent, HibernateUtil.getNewSession());
		activate();
		localPatient = PatientManager.getPatient(getHSession(), patient.getId());
		txtPatientId.setText(patient.getPatientId());
		getPatientsPackages();
	}

	/**
	 * This method initializes getShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Stock, Prescription and Package Deletions";
		Rectangle bounds = new Rectangle(25, 0, 900, 700);
		buildShell(shellTxt, bounds);
		
		patientIDListener = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ((e.character == SWT.CR)
						|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
					cmdSearchWidgetSelected();
				}
			}
		};

		
		createGrpDeletionTypeSelection();
		createGrpPatientInfo();
		createGrpPackageInfo();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String txt = "Stock, Prescription and Package Deletions";
		iDartImage icoImage = iDartImage.REDOPACKAGE;
		buildCompHeader(txt, icoImage);
	}

	private void createGrpDeletionTypeSelection() {

		// grpDeletionTypeSelection
		grpDeletionTypeSelection = new Group(getShell(), SWT.NONE);
		grpDeletionTypeSelection
		.setBounds(new org.eclipse.swt.graphics.Rectangle(30, 80, 840,
				100));

		lblPackage = new Label(grpDeletionTypeSelection, SWT.NONE);
		lblPackage.setBounds(new org.eclipse.swt.graphics.Rectangle(100, 15,
				50, 43));
		lblPackage.setText("");
		lblPackage.setImage(ResourceUtils.getImage(iDartImage.PACKAGEDELETE));

		rdBtnPackage = new Button(grpDeletionTypeSelection, SWT.RADIO);
		rdBtnPackage.setBounds(30, 60, 180, 20);
		rdBtnPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnPackage.setText("Undo Created Package");
		rdBtnPackage.setSelection(true);
		rdBtnPackage
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnPackage.getSelection()) {
					switchDeletionType();
				}
			}
		});

		lblItem = new Label(grpDeletionTypeSelection, SWT.NONE);
		lblItem.setBounds(new org.eclipse.swt.graphics.Rectangle(310, 15, 50,
				43));
		lblItem.setText("");
		lblItem.setImage(ResourceUtils.getImage(iDartImage.DRUGALLERGY));

		rdBtnItem = new Button(grpDeletionTypeSelection, SWT.RADIO);
		rdBtnItem.setBounds(230, 60, 190, 20);
		rdBtnItem.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnItem.setText("Redo Single Item in Package");
		rdBtnItem
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnItem.getSelection()) {
					switchDeletionType();
				}
			}
		});

		lblStock = new Label(grpDeletionTypeSelection, SWT.NONE);
		lblStock.setBounds(new org.eclipse.swt.graphics.Rectangle(510, 15, 50,
				43));
		lblStock.setText("");
		lblStock.setImage(ResourceUtils.getImage(iDartImage.STOCKDELETE));

		rdBtnStock = new Button(grpDeletionTypeSelection, SWT.RADIO);
		rdBtnStock.setBounds(430, 60, 180, 20);
		rdBtnStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnStock.setText("Delete Incorrect Stock Batch");
		rdBtnStock
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnStock.getSelection()) {
					switchDeletionType();
				}
			}
		});
		lblPrescription = new Label(grpDeletionTypeSelection, SWT.NONE);
		lblPrescription.setBounds(new org.eclipse.swt.graphics.Rectangle(710,
				15, 50, 43));
		lblPrescription.setText("");
		lblPrescription.setImage(ResourceUtils
				.getImage(iDartImage.PRESCRIPTIONDELETE));

		rdBtnPrescription = new Button(grpDeletionTypeSelection, SWT.RADIO);
		rdBtnPrescription.setBounds(630, 60, 185, 20);
		rdBtnPrescription.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnPrescription.setText("Delete Incorrect Prescription");
		rdBtnPrescription
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnPrescription.getSelection()) {
					switchDeletionType();
				}
			}
		});

	}

	/**
	 * This method initializes grpPackageInfo
	 * 
	 */
	private void createGrpPatientInfo() {

		if (grpPatientInfo != null) {
			grpPatientInfo.dispose();
		}

		grpPatientInfo = new Group(getShell(), SWT.NONE);
		grpPatientInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(200,
				195, 500, 55));

		lblPatientId = new Label(grpPatientInfo, SWT.NONE);
		lblPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(20, 20,
				60, 20));
		lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientId = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(90, 15,
				240, 20));
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		if (!rdBtnStock.getSelection()) {
			txtPatientId.addKeyListener(patientIDListener);
		}

		txtPatientId.setFocus();

		btnSearch = new Button(grpPatientInfo, SWT.NONE);
		btnSearch.setBounds(new org.eclipse.swt.graphics.Rectangle(350, 10,
				100, 30));
		btnSearch.setText("Patient Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch
		.setToolTipText("Press this button to search for an existing patient.");

		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});

	}

	/**
	 * This method initializes createGrpDrugInfo
	 * 
	 */
	private void createGrpDrugInfo() {

		if (grpPatientInfo != null) {
			grpPatientInfo.dispose();
		}
		grpPatientInfo = new Group(getShell(), SWT.NONE);
		grpPatientInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(200,
				195, 500, 55));
		lblPatientId = new Label(grpPatientInfo, SWT.NONE);
		lblPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(20, 20,
				60, 20));
		lblPatientId.setText("Drug:");
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientId = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(90, 15,
				240, 20));

		if (rdBtnStock.getSelection()) {
			txtPatientId.addKeyListener(patientIDListener);
		}
		txtPatientId.setEnabled(false);

		btnSearch = new Button(grpPatientInfo, SWT.NONE);
		btnSearch.setBounds(new org.eclipse.swt.graphics.Rectangle(350, 10,
				100, 30));
		btnSearch.setText("Drug Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch
		.setToolTipText("Press this button to search for the drug for which you wish to delete a stock batch.");

		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdDrugSearchWidgetSelected();
			}
		});

	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				packageToRemove = null;
				prescriptionToRemove = null;
				stockToRemove = null;

				clearForm();
			}
		});

		btnClose = new Button(getCompButtons(), SWT.NONE);
		btnClose.setText("Close");
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClose
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnClose
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseWidgetSelected();
			}
		});
	}

	/**
	 * This method is called when the user presses the 'Close' button
	 * 
	 */
	private void cmdCloseWidgetSelected() {
		closeShell(true);
	}

	private void cmdSearchWidgetSelected() {

		boolean showInactivePatients = rdBtnPrescription.getSelection();
		
		String patientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText().trim());
		
		PatientSearch search = new PatientSearch(getShell(), getHSession());
		search.setShowInactive(showInactivePatients);
		PatientIdentifier identifier = search.search(patientId);
		
		if (identifier != null) {
			// First clear the form and fields
			packageToRemove = null;
			prescriptionToRemove = null;
			stockToRemove = null;

			clearForm();

			// load patient
			localPatient = identifier.getPatient();
			txtPatientId.setText(localPatient.getPatientId());
			if (rdBtnPackage.getSelection() || rdBtnItem.getSelection()) {
				getPatientsPackages();
			} else if (rdBtnPrescription.getSelection()) {
				getPatientsPrescriptions();
			}

		}

	}

	private void cmdDrugSearchWidgetSelected() {

		Search drugSearch = new Search(getHSession(), getShell(),
				CommonObjects.DRUG);

		if (drugSearch.getValueSelected() != null) {

			localDrug = DrugManager.getDrug(getHSession(), drugSearch
					.getValueSelected()[0]);

			txtPatientId.setText(localDrug.getName());

			getStockBatches();

		}

	}

	private void getPatientsPackages() {

		packageToRemove = PackageManager.getLastPackageMade(getHSession(),
				localPatient);

		if (packageToRemove == null) {
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING
					| SWT.OK);
			mb.setText("No Packages to Remove");
			mb
			.setMessage("Patient '"
					+ localPatient.getPatientId()
					+ "' does not have any packages that can be removed from the database at this point.\n\n "
					+ "Only the most recent package for a patient can be removed.");
			mb.open();
			txtPatientId.setText("");
			txtPackageId.setFocus();
		}

		else if (rdBtnItem.getSelection()
				&& ((packageToRemove.getPackagedDrugs().size() + packageToRemove
						.getAccumulatedDrugs().size()) <= 1)) {
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING
					| SWT.OK);
			mb.setText("Cannot Remove Last Item In Package");
			mb
			.setMessage("There is only 1 item in this package, so you cannot remove this simgle item.\n\nTo remove the entire package, select Undo Created Package.");
			mb.open();
			txtPatientId.setText("");
			txtPackageId.setFocus();
		} else {
			populatePackageGUI();
			btnRemovePackage.setEnabled(true);
		}

	}

	private void getPatientsPrescriptions() {

		prescriptionToRemove = PackageManager
		.getMostRecentPrescriptionWithoutPackages(getHSession(),
				localPatient);

		if (prescriptionToRemove == null) {
			MessageBox mb = new MessageBox(getShell());
			mb.setText("No Prescription to Remove");
			mb
			.setMessage("Patient '"
					+ localPatient.getPatientId()
					+ "' does not have any prescription that can be removed from the database at this point. \n\nThe only prescriptions that "
					+ "can be removed are those for which no packages have been created.");
			mb.open();
			txtPatientId.setText("");
			txtPackageId.setFocus();
		}

		else {
			populatePrescriptionGUI();
			btnRemovePackage.setEnabled(true);
		}

	}

	private void getStockBatches() {

		stockToRemove = StockManager.getEmptyBatchesForDrug(getHSession(),
				txtPatientId.getText());

		if ((stockToRemove == null) || (stockToRemove.size() == 0)) {
			MessageBox mb = new MessageBox(getShell());
			mb.setText("No Stock to Remove");
			mb
			.setMessage("Drug '"
					+ txtPatientId.getText()
					+ "' does not have any batches that can be removed from the database at this point. \n\nThe only batches that "
					+ "can be removed are those from which nothing has been dispensed.");
			mb.open();
			txtPatientId.setText("");

		}

		else {
			populateStockGUI();
			btnRemovePackage.setEnabled(true);
		}

	}

	private void populatePackageGUI() {

		String itemText[];

		txtPackageId.setText(packageToRemove.getPackageId());
		String date = (new SimpleDateFormat("dd/MM/yyyy 'at' h:mm a"))
		.format(packageToRemove.getPackDate());
		txtDatePacked.setText(date);

		List<PackagedDrugs> drugsInPackage = packageToRemove.getPackagedDrugs();

		java.util.Set<AccumulatedDrugs> accumInPackage = packageToRemove
		.getAccumulatedDrugs();

		TableItem[] t = new TableItem[drugsInPackage.size()
		                              + accumInPackage.size()];

		for (int j = 0; j < drugsInPackage.size(); j++) {
			PackagedDrugs pd = drugsInPackage.get(j);
			if (pd != null) {
				t[j] = new TableItem(tblDrugsInPackage, SWT.NONE);
				itemText = new String[5];
				itemText[0] = "Dispensed";
				itemText[1] = pd.getStock().getDrug().getName();
				itemText[2] = pd.getAmount() + "";
				itemText[3] = pd.getStock().getBatchNumber();
				t[j].setText(itemText);
				t[j].setData(pd);
			}

		}

		Iterator<AccumulatedDrugs> it = accumInPackage.iterator();
		int k = 0;
		while (it.hasNext()) {
			AccumulatedDrugs acc = it.next();
			if (acc != null) {
				t[k + drugsInPackage.size()] = new TableItem(tblDrugsInPackage,
						SWT.NONE);
				itemText = new String[5];
				itemText[0] = "Accumulated";
				itemText[1] = acc.getPillCount().getDrug().getName();
				itemText[2] = acc.getPillCount().getAccum() + "";
				itemText[3] = "";
				t[k + drugsInPackage.size()].setText(itemText);
				t[k + drugsInPackage.size()].setData(acc);
				k++;
			}
		}

	}

	private void populatePrescriptionGUI() {

		String itemText[];

		txtPackageId.setText(prescriptionToRemove.getPrescriptionId());
		String date = (new SimpleDateFormat("dd/MM/yyyy 'at' h:mm a"))
		.format(prescriptionToRemove.getDate());
		txtDatePacked.setText(date);

		List<PrescribedDrugs> drugsInScript = prescriptionToRemove
		.getPrescribedDrugs();

		TableItem[] t = new TableItem[drugsInScript.size()];

		for (int j = 0; j < drugsInScript.size(); j++) {

			t[j] = new TableItem(tblDrugsInPackage, SWT.NONE);

			PrescribedDrugs pd = drugsInScript.get(j);

			String tempAmtPerTime = "";

			if (new BigDecimal(pd.getAmtPerTime()).scale() == 0) {
				tempAmtPerTime = ""
					+ new BigDecimal(pd.getAmtPerTime()).unscaledValue()
					.intValue();
			} else {
				tempAmtPerTime = "" + pd.getAmtPerTime();
			}

			itemText = new String[2];
			itemText[0] = pd.getDrug().getName();

			Form theForm = pd.getDrug().getForm();
			if (theForm.getFormLanguage1().equals("")) // is a cream, no amount
				// per time
			{
				itemText[1] = theForm.getActionLanguage1() + " "
				+ pd.getTimesPerDay() + " times a day.";
			} else {
				itemText[1] = theForm.getActionLanguage1() + " "
				+ tempAmtPerTime + " " + theForm.getFormLanguage1()
				+ " " + pd.getTimesPerDay() + " times a day.";
			}

			t[j].setText(itemText);

		}

	}

	private void populateStockGUI() {
		java.util.List<Stock> batchList = null;

		// Clear the table of all previous rows
		tblDrugsInPackage.removeAll();

		try {

			batchList = stockToRemove;
			Iterator<Stock> iter = batchList.iterator();

			while (iter.hasNext()) {

				Stock thisStock = iter.next();

				final TableItem ti = new TableItem(tblDrugsInPackage, SWT.NONE);
				ti.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

				SimpleDateFormat df = new SimpleDateFormat("MM/yyyy");
				SimpleDateFormat dfDays = new SimpleDateFormat("dd/MM/yyyy");

				ti.setText(2, thisStock.getBatchNumber());
				ti.setText(3, (thisStock.getManufacturer() == null ? ""
						: thisStock.getManufacturer()));
				ti.setText(4, df.format(thisStock.getExpiryDate()));
				ti.setText(0, dfDays.format(thisStock.getDateReceived()));
				ti.setText(1, (new Integer(thisStock.getUnitsReceived()))
						.toString());
				ti.setText(5, thisStock.getStockCenter().getStockCenterName());

				ti.setData(thisStock);

			}
		}

		catch (HibernateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			batchList = null;
		}

	}

	private void cmdRemoveSelected() {

		// package deletion

		if (fieldsOk()) {

			// before we try anything, lets ask the user for their password
			ConfirmWithPasswordDialogAdapter passwordDialog = new ConfirmWithPasswordDialogAdapter(
					getShell(), getHSession());
			passwordDialog.setMessage("Please enter your Password");
			// if password verified
			String messg = passwordDialog.open();
			if (messg.equalsIgnoreCase("verified")) {

				if (rdBtnPackage.getSelection()) {
					deletePackage();
					clearForm();
					closeAndReopenSession();
				}

				else if (rdBtnPrescription.getSelection()) {
					deleteScript();
					clearForm();
					closeAndReopenSession();
				}

				else if (rdBtnStock.getSelection()) {
					deleteStock();
					clearForm();
					closeAndReopenSession();

				} else if (rdBtnItem.getSelection()) {
					deleteItem();
					clearForm();
					txtPatientId.setText(packageToRemove.getPrescription()
							.getPatient().getPatientId());
					closeAndReopenSession();
					cmdSearchWidgetSelected();
				}

			}
			// Incorrect password entered,
			else if (messg.equalsIgnoreCase("unverified")) {
				cmdCloseWidgetSelected();
			} else if (messg.equalsIgnoreCase("cancel")) {
				clearForm();
			}

		}

	}

	/**
	 * This method closes and reopens a session. It should be used in screens
	 * that don't close after a write
	 * 
	 */
	public void closeAndReopenSession() {

		try {
			if (getHSession() != null) {
				getHSession().close();
			}
			setHSession(HibernateUtil.getNewSession());
		} catch (HibernateException he) {
			getLog().error(he);
		}
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGrpPackageInfo() {

		if (grpPackageInfo != null) {
			grpPackageInfo.dispose();
		}
		grpPackageInfo = new Group(getShell(), SWT.NONE);
		grpPackageInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPackageInfo.setText("Package To Remove:");
		grpPackageInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				260, 840, 330));

		lblPackageId = new Label(grpPackageInfo, SWT.NONE);
		lblPackageId.setBounds(new Rectangle(260, 30, 100, 20));
		lblPackageId.setText("Package ID:");
		lblPackageId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackageId = new Text(grpPackageInfo, SWT.BORDER);
		txtPackageId.setBounds(new Rectangle(380, 30, 180, 20));
		txtPackageId.setEnabled(false);
		txtPackageId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDatePacked = new Label(grpPackageInfo, SWT.NONE);
		lblDatePacked.setBounds(new Rectangle(260, 55, 100, 20));
		lblDatePacked.setText("Date Packed:");
		lblDatePacked.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDatePacked = new Text(grpPackageInfo, SWT.BORDER);
		txtDatePacked.setBounds(new Rectangle(380, 55, 180, 20));
		txtDatePacked.setEnabled(false);
		txtDatePacked.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDrugsInPackage = new Label(grpPackageInfo, SWT.CENTER);
		lblDrugsInPackage.setBounds(new Rectangle(230, 100, 380, 15));
		lblDrugsInPackage.setText("Drugs in This Package:");
		lblDrugsInPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		createTblDrugsInPackage();

		btnRemovePackage = new Button(grpPackageInfo, SWT.NONE);
		btnRemovePackage.setBounds(new org.eclipse.swt.graphics.Rectangle(330,
				290, 170, 30));
		btnRemovePackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnRemovePackage.setEnabled(false);
		if (rdBtnPackage.getSelection()) {
			btnRemovePackage.setText("Remove this Package");
		} else if (rdBtnItem.getSelection()) {
			btnRemovePackage.setText("Remove this Drug");
		}
		btnRemovePackage
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdRemoveSelected();
			}
		});
		txtPatientId.setText("");

	}

	/**
	 * This method initializes tblDrugsInPackage
	 * 
	 */
	private void createTblDrugsInPackage() {

		tblDrugsInPackage = new Table(grpPackageInfo, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.SINGLE);
		tblDrugsInPackage.setHeaderVisible(true);
		tblDrugsInPackage.setLinesVisible(true);
		tblDrugsInPackage.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				118, 780, 160));
		tblDrugsInPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColDrugSource = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColDrugSource.setText("Dispensed / Accum");
		tblColDrugSource.setWidth(125);

		TableColumn tblColDrugName = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColDrugName.setText("Drug Name");
		tblColDrugName.setWidth(380);

		TableColumn tblColQuantity = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColQuantity.setText("Qty");
		tblColQuantity.setWidth(80);

		TableColumn tblColBatchNo = new TableColumn(tblDrugsInPackage, SWT.NONE);
		tblColBatchNo.setText("Batch No");
		tblColBatchNo.setWidth(125);

	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGrpPrescriptionInfo() {

		if (grpPackageInfo != null) {
			grpPackageInfo.dispose();
		}
		grpPackageInfo = new Group(getShell(), SWT.NONE);
		grpPackageInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPackageInfo.setText("Prescription To Remove:");
		grpPackageInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				260, 840, 330));

		lblPackageId = new Label(grpPackageInfo, SWT.NONE);
		lblPackageId.setBounds(new Rectangle(260, 30, 100, 20));
		lblPackageId.setText("Prescription ID:");
		lblPackageId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackageId = new Text(grpPackageInfo, SWT.BORDER);
		txtPackageId.setBounds(new Rectangle(380, 30, 180, 20));
		txtPackageId.setEnabled(false);
		txtPackageId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDatePacked = new Label(grpPackageInfo, SWT.NONE);
		lblDatePacked.setBounds(new Rectangle(260, 55, 100, 20));
		lblDatePacked.setText("Date Captured");
		lblDatePacked.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDatePacked = new Text(grpPackageInfo, SWT.BORDER);
		txtDatePacked.setBounds(new Rectangle(380, 55, 180, 20));
		txtDatePacked.setEnabled(false);
		txtDatePacked.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDrugsInPackage = new Label(grpPackageInfo, SWT.CENTER);
		lblDrugsInPackage.setBounds(new Rectangle(230, 100, 380, 15));
		lblDrugsInPackage.setText("Drugs on the Prescription:");
		lblDrugsInPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		createTblDrugsInPrescription();

		btnRemovePackage = new Button(grpPackageInfo, SWT.NONE);
		btnRemovePackage.setBounds(new org.eclipse.swt.graphics.Rectangle(330,
				290, 170, 30));
		btnRemovePackage.setEnabled(false);
		btnRemovePackage.setText("Remove this Prescription");
		btnRemovePackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnRemovePackage
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdRemoveSelected();
			}
		});

		txtPatientId.setText("");

	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGrpStockBatchSelection() {

		if (grpPackageInfo != null) {
			grpPackageInfo.dispose();
		}
		grpPackageInfo = new Group(getShell(), SWT.NONE);
		grpPackageInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPackageInfo.setText("Available Batches for Drug:");
		grpPackageInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				260, 840, 330));

		createTblStockForDrug();

		btnRemovePackage = new Button(grpPackageInfo, SWT.NONE);
		btnRemovePackage.setBounds(new org.eclipse.swt.graphics.Rectangle(330,
				290, 170, 30));
		btnRemovePackage.setEnabled(false);
		btnRemovePackage.setText("Remove Selected Batch");
		btnRemovePackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnRemovePackage
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdRemoveSelected();
			}
		});

		txtPatientId.setText("");

	}

	/**
	 * This method initializes tblDrugsInPackage
	 * 
	 */
	private void createTblStockForDrug() {

		tblDrugsInPackage = new Table(grpPackageInfo, SWT.BORDER
				| SWT.FULL_SELECTION);
		tblDrugsInPackage.setHeaderVisible(true);
		tblDrugsInPackage.setLinesVisible(true);
		tblDrugsInPackage.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				48, 780, 220));
		tblDrugsInPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColDrugName = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColDrugName.setText("Date Received");
		tblColDrugName.setWidth(100);

		TableColumn tblColQuantity = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColQuantity.setText("Qty");
		tblColQuantity.setWidth(60);

		TableColumn tblColBatchNo = new TableColumn(tblDrugsInPackage, SWT.NONE);
		tblColBatchNo.setText("Batch No");
		tblColBatchNo.setWidth(120);

		TableColumn tblColManufacturer = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColManufacturer.setText("Manufacturer");
		tblColManufacturer.setWidth(120);

		TableColumn tblColExpiryDate = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColExpiryDate.setText("Expiry Date");
		tblColExpiryDate.setWidth(100);

		TableColumn tblColClinic = new TableColumn(tblDrugsInPackage, SWT.NONE);
		tblColClinic.setText("Pharmacy");
		tblColClinic.setWidth(100);
	}

	/**
	 * This method initializes tblDrugsInPackage
	 * 
	 */
	private void createTblDrugsInPrescription() {

		tblDrugsInPackage = new Table(grpPackageInfo, SWT.BORDER
				| SWT.FULL_SELECTION);
		tblDrugsInPackage.setHeaderVisible(true);
		tblDrugsInPackage.setLinesVisible(true);
		tblDrugsInPackage.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				118, 780, 160));
		tblDrugsInPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColDrugName = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColDrugName.setText("Drug Name");
		tblColDrugName.setWidth(380);

		TableColumn tblColQuantity = new TableColumn(tblDrugsInPackage,
				SWT.NONE);
		tblColQuantity.setText("Dosage");
		tblColQuantity.setWidth(265);

	}

	private void clearForm() {

		txtPatientId.setText("");
		btnRemovePackage.setEnabled(false);

		if (rdBtnPrescription.getSelection() || rdBtnPackage.getSelection()) {
			txtPackageId.setText("");
			txtDatePacked.setText("");
		}

		tblDrugsInPackage.clearAll();
		tblDrugsInPackage.removeAll();

		txtPatientId.setFocus();

	}

	/**
	 * Method fieldsOk.
	 * 
	 * @return boolean
	 */
	private boolean fieldsOk() {

		if (rdBtnPackage.getSelection()) {
			if (txtPackageId.getText().equals("")) {
				MessageBox mb = new MessageBox(getShell());

				mb.setText("No Package To Remove");
				mb
				.setMessage("There is no package that can be removed. Please "
						+ "enter a patient number (or use the Search button) to load a "
						+ "possible package that can be removed.");
				mb.open();
				return false;
			}

			else
				return true;
		}

		else if (rdBtnPrescription.getSelection()) {
			if (txtPackageId.getText().equals("")) {
				MessageBox mb = new MessageBox(getShell());
				mb.setText("No Prescription To Remove");
				mb
				.setMessage("There is no prescription that can be removed. Please "
						+ "enter a patient number (or use the Search button) to load a "
						+ "possible prescription that can be removed.");
				mb.open();
				return false;
			}

			else
				return true;
		}

		else if (rdBtnStock.getSelection()) {
			if (tblDrugsInPackage.getItemCount() == 0) {
				MessageBox mb = new MessageBox(getShell());
				mb.setText("No Stock Batch To Remove");
				mb.setMessage("Please " + "use the Drug Search button to load "
						+ "possible batches that can be removed.");
				mb.open();
				return false;
			} else if (tblDrugsInPackage.getSelection().length == 0) {
				MessageBox mb = new MessageBox(getShell());
				mb.setText("No Stock Batch Selected");
				mb
				.setMessage("Please select a stock batch to remove by clicking a row in the table");
				mb.open();
				return false;
			}

			else
				return true;
		} else if (rdBtnItem.getSelection()) {

			if (tblDrugsInPackage.getItemCount() == 0) {
				MessageBox mb = new MessageBox(getShell());

				mb.setText("No Drug To Remove");
				mb
				.setMessage("There are no drugs that can be removed. Please "
						+ "enter a patient number (or use the Search button) to load a "
						+ "possible package with drugs that can be removed.");
				mb.open();
				return false;
			} else if (tblDrugsInPackage.getSelection().length == 0) {
				MessageBox mb = new MessageBox(getShell());

				mb.setText("No Drug To Remove");
				mb
				.setMessage("No drug has been selected from the package. Please "
						+ "select a drug to be removed from the list of drugs in "
						+ "the table. ");
				mb.open();
				return false;
			}

			else
				return true;
		} else
			return false; // should never happen

	}

	private void switchDeletionType() {

		if (rdBtnPackage.getSelection()) {
			prescriptionToRemove = null;
			stockToRemove = null;
			createGrpPatientInfo();
			createGrpPackageInfo();
		} else if (rdBtnPrescription.getSelection()) {
			packageToRemove = null;
			stockToRemove = null;
			createGrpPatientInfo();
			createGrpPrescriptionInfo();

		} else if (rdBtnStock.getSelection()) {
			packageToRemove = null;
			stockToRemove = null;
			createGrpDrugInfo();
			createGrpStockBatchSelection();

		} else if (rdBtnItem.getSelection()) {

			createGrpPatientInfo();
			createGrpPackageInfo();

		}
	}

	/**
	 * Method printSummaryLabel.
	 * 
	 * @param thePatient
	 *            Patient
	 * @param thePackage
	 *            Packages
	 */
	private void printSummaryLabel(Patient thePatient, Packages thePackage) {

		StockCenter stockCenter;
		if (packageToRemove.getPackagedDrugs().size() > 0) {
			stockCenter = packageToRemove.getPackagedDrugs().get(0).getStock()
			.getStockCenter();
		} else {
			getLog().error("There are no drugs in this package");
			return;
		}

		Prescription prescription = thePackage.getPrescription();
		List<Object> printerQueue = new ArrayList<Object>();

		String duration = "";
		if (prescription.getDuration() == 2) {
			duration = "2 week";
		} else {
			duration = prescription.getDuration() / 4 + " month";
		}

		// Obtaining next appointment date
		Appointment app = PatientManager
		.getLatestActiveAppointmentForPatient(thePatient);

		// First get list of drugs for label
		List<String> drugList = new ArrayList<String>();
		for (int i = 0; i < tblDrugsInPackage.getItemCount(); i++) {
			TableItem ti = tblDrugsInPackage.getItem(i);
			drugList.add(ti.getText(1) + " " + ti.getText(2));
		}

		ScriptSummaryLabel sml = new ScriptSummaryLabel();
		sml.setPharmacyName(stockCenter.getStockCenterName());
		sml.setDispDate(iDARTUtil.format(packageToRemove.getPackDate()));
		sml.setFolderNumber(thePatient.getPatientId());
		sml.setPatientFirstName(thePatient.getFirstNames());
		sml.setPatientLastName(thePatient.getLastname());
		sml.setPrescriptionId(packageToRemove.getPackageId());
		sml.setDrugs(drugList);
		sml.setNextAppointmentDate(iDARTUtil.format(app.getAppointmentDate()));
		if (iDartProperties.labelType.equals(LabelType.EKAPA)) {
			sml.setIssuesString(String.valueOf((prescription.getPackages()
					.size() + 1))
					+ " of a " + duration + " month script");
			sml
			.setBoldIssuesString(((prescription.getPackages().size() + 1) >= Integer
					.parseInt((duration.split(" "))[0])) ? true : false);
		} else {
			sml.setIssuesString(String.valueOf((prescription.getPackages()
					.size() + 1))
					+ " of a " + duration + " script");
		}

		printerQueue.add(sml);

		new PrintThread(printerQueue);
	}

	private void deletePackage() {

		// do we need to delete a pill count too?
		Packages previousPack = PackageManager.getPreviousPackageCollected(
				getHSession(), packageToRemove);

		boolean pillCountDelete = false;

		if ((previousPack != null) && (previousPack.getPillCounts() != null)
				&& (previousPack.getPillCounts().size() > 0)) {
			MessageBox deletePillcount = new MessageBox(getShell(), SWT.YES
					| SWT.NO | SWT.ICON_QUESTION);
			deletePillcount
			.setMessage("A Pill Count was recorded when the package was created."
					+ "\n\nWould you like to delete this Pill Count?");
			deletePillcount.setText("Delete Pill Count");

			switch (deletePillcount.open()) {
			case SWT.YES:
				pillCountDelete = true;

				break;
			}
		}

		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();

			DeletionsManager.removePackage(getHSession(), packageToRemove);

			boolean shouldRemoveARVStartDate = DeletionsManager
			.isFirstPackageOnNewPatientEpisode(packageToRemove);
			boolean patientHasArvStartDate = (localPatient
					.getAttributeByName(PatientAttribute.ARV_START_DATE) == null ? false
							: true);
			if (shouldRemoveARVStartDate && patientHasArvStartDate) {
				MessageBox deleteARVStartDate = new MessageBox(getShell(),
						SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				deleteARVStartDate
				.setMessage("The patient is a new patient and now contains no ARV packages."
						+ "\n\nWould you like to remove the ARV start date for this patient.");
				deleteARVStartDate.setText("Remove ARV Start Date?");

				switch (deleteARVStartDate.open()) {
				case SWT.YES:
					Patient p = packageToRemove.getPrescription().getPatient();
					p.removePatientAttribute(PatientAttribute.ARV_START_DATE);
					break;
				}
			}

			if (pillCountDelete) {
				DeletionsManager.removePillCountInfo(getHSession(),
						previousPack);
			}

			getHSession().flush();

			tx.commit();
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Package Deletion Successful");
			mb
			.setMessage("This package was successfully removed from the database."
					+ (pillCountDelete ? "Pill counts recorded at package creation time were also deleted."
							: "")
							+ "\n\nTo re-package this set of drugs for this patient, go back to the Patient Packaging page.");
			mb.open();

		} catch (HibernateException he) {

			if (tx != null) {
				tx.rollback();
			}
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Package Deletion Unsuccessful");
			mb
			.setMessage("There was a problem removing this package from the database.");
			mb.open();
			getLog().error("Package Deletion Unsuccessful", he);

		}
		if (getHSession() != null) {
			getHSession().close();
		}
		setHSession(HibernateUtil.getNewSession());

		clearForm();
	}

	private void deleteItem() {
		Object selectedDrug = null;
		String type = "";
		if (tblDrugsInPackage.getSelection().length > 0) {
			TableItem ti = tblDrugsInPackage.getSelection()[0];
			selectedDrug = ti.getData();
			type = ti.getText(0);
		}

		if (selectedDrug != null) {

			Transaction tx = null;
			try {
				tx = getHSession().beginTransaction();
				if (type.equals("Dispensed")) {
					DeletionsManager.removePackagedDrug(getHSession(),
							(PackagedDrugs) selectedDrug, packageToRemove);

				} else if (type.equals("Accumulated")) {
					DeletionsManager.removeAccumulatedDrug(getHSession(),
							(AccumulatedDrugs) selectedDrug);
				}

				// remove item from table
				tblDrugsInPackage.remove(tblDrugsInPackage.getSelectionIndex());

				MessageBox summaryLabel = new MessageBox(getShell(), SWT.YES
						| SWT.NO);
				summaryLabel.setText("Print Summary Label?");
				summaryLabel
				.setMessage("Would you like to print a new Script Summary Label?.");
				switch (summaryLabel.open()) {
				case SWT.YES:
					printSummaryLabel(localPatient, packageToRemove);
					break;
				case SWT.NO:
					break;
				}

				getHSession().flush();
				tx.commit();
				MessageBox mb = new MessageBox(getShell());
				mb.setText("Package Deletion Successful");
				mb
				.setMessage("This drug was successfully removed from the package.");
				mb.open();

			} catch (HibernateException he) {

				MessageBox mb = new MessageBox(getShell());
				mb.setText("Drug Deletion Unsuccessful");
				mb
				.setMessage("There was a problem removing this drug from the package.");
				mb.open();
				if (tx != null) {
					tx.rollback();
				}
				getLog().error(he);

			}
		} else {
			MessageBox mb = new MessageBox(getShell());
			mb.setText("No Drug Selected");
			mb
			.setMessage("Please select an accumulated or dispensed drug from the table.");
			mb.open();
		}
	}

	private void deleteScript()

	{
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();
			DeletionsManager.removeUndispensedPrescription(getHSession(),
					prescriptionToRemove);
			getHSession().flush();
			tx.commit();
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Prescription Deletion Successful");
			mb
			.setMessage("This prescription was successfully removed from the database.");
			mb.open();

		} catch (HibernateException he) {

			if (tx != null) {
				tx.rollback();
			}
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Prescription Deletion Unsuccessful");
			mb
			.setMessage("There was a problem removing this prescription from the database.");
			mb.open();
			getLog().error(he);
		}
		if (getHSession() != null) {
			getHSession().close();
		}
		setHSession(HibernateUtil.getNewSession());

		clearForm();
	}

	private void deleteStock() {

		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();
			DeletionsManager.removeUndispensedStock(getHSession(),
					(Stock) tblDrugsInPackage.getSelection()[0].getData());
			getHSession().flush();
			tx.commit();
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Stock Batch Deletion Successful");
			mb
			.setMessage("This stock batch was successfully removed from the database.");
			mb.open();

		} catch (HibernateException he) {

			if (tx != null) {
				tx.rollback();
			}
			MessageBox mb = new MessageBox(getShell());
			mb.setText("Stock Batch Deletion Unsuccessful");
			mb
			.setMessage("There was a problem removing this stock batch from the database.");
			mb.open();
			getLog().error(he);

		}
	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	/**
	 * Method getShell.
	 * 
	 * @return Shell
	 */
	@Override
	public Shell getShell() {
		return super.getShell();
	}
}
