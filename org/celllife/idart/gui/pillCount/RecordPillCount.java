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

package org.celllife.idart.gui.pillCount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.manager.AdherenceManager;
import model.manager.PackageManager;
import model.manager.TemporaryRecordsManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.composite.PillCountTable;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

/**
 */
public class RecordPillCount extends GenericFormGui {

	private Patient localPatient;

	private Group grpPatientInfo; // @jve:decl-index=0:

	private Label lblPatientId;

	private Label lblPatientName;

	private Label lblAge;

	private Label lblDateOfLastPickup;

	private Text txtPatientName;

	private Text txtAge;

	private Text txtDOB;

	private Text txtDateOfLastPickup;

	private Text txtPatientId;

	private PillCountTable compTable;

	private Label lblInstructions;

	private Button btnSearch;

	private Packages previousPack;

	/**
	 * Constructor for RecordPillCount.
	 * @param parent Shell
	 */
	public RecordPillCount(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	/**
	 * This method initializes newBatchInfo
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Record Pill Count";
		Rectangle bounds = new Rectangle(0, 0, 600, 620);
		buildShell(shellTxt, bounds);
	}

	@Override
	protected void createContents() {
		createGrpPatientInfo();
		createGrpStockInfo();
	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Record Pill Count";
		iDartImage icoImage = iDartImage.DRUGGROUP;
		buildCompHeader(headerTxt, icoImage);
		lblInstructions = new Label(getCompHeader(), SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(176, 55, 260, 20));
		lblInstructions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));
	}

	/**
	 * This method initializes grpPatientInfo
	 */
	private void createGrpPatientInfo() {

		grpPatientInfo = new Group(getShell(), SWT.NONE);
		grpPatientInfo.setBounds(new Rectangle(61, 119, 469, 140));
		grpPatientInfo.setText("Patient Information");
		grpPatientInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblPatientId & txtPatientId
		lblPatientId = new Label(grpPatientInfo, SWT.NONE);
		lblPatientId.setBounds(new Rectangle(15, 20, 145, 20));
		lblPatientId.setText(Messages.getString("common.compulsory.marker") + Messages.getString("patient.label.patientid")); //$NON-NLS-1$ //$NON-NLS-2$
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientId.setBounds(new Rectangle(165, 20, 160, 20));
		// txtPatientId.setEditable(false);
		// txtPatientId.setEnabled(false);
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdSearchSelected();
				}
			}
		});

		btnSearch = new Button(grpPatientInfo, SWT.NONE);
		btnSearch.setBounds(new Rectangle(345, 15, 100, 30));
		btnSearch.setText("Patient Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchSelected();
			}
		});

		lblPatientName = new Label(grpPatientInfo, SWT.NONE);
		lblPatientName.setBounds(new Rectangle(15, 50, 145, 20));
		lblPatientName.setText("Name:");
		lblPatientName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientName = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientName.setBounds(new Rectangle(165, 50, 160, 20));
		txtPatientName.setEditable(false);
		txtPatientName.setEnabled(false);
		txtPatientName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblAge = new Label(grpPatientInfo, SWT.NONE);
		lblAge.setBounds(new Rectangle(15, 80, 145, 20));
		lblAge.setText("Age:");
		lblAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtAge = new Text(grpPatientInfo, SWT.BORDER);
		txtAge.setBounds(new Rectangle(165, 80, 50, 20));
		txtAge.setEditable(false);
		txtAge.setEnabled(false);
		txtAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDOB = new Text(grpPatientInfo, SWT.BORDER);
		txtDOB.setBounds(new Rectangle(225, 80, 100, 20));
		txtDOB.setEditable(false);
		txtDOB.setEnabled(false);
		txtDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDateOfLastPickup = new Label(grpPatientInfo, SWT.NONE);
		lblDateOfLastPickup.setBounds(new Rectangle(15, 110, 145, 20));
		lblDateOfLastPickup.setText("Date of Last Pickup:");
		lblDateOfLastPickup
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDateOfLastPickup = new Text(grpPatientInfo, SWT.BORDER);
		txtDateOfLastPickup.setBounds(new Rectangle(165, 110, 160, 20));
		txtDateOfLastPickup.setEditable(false);
		txtDateOfLastPickup.setEnabled(false);
		txtDateOfLastPickup
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes grpStockInfo
	 * 
	 */
	private void createGrpStockInfo() {
		compTable = new PillCountTable(getShell(), SWT.BORDER, getHSession(),
				new Rectangle(29, 289, 531, 231));
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
		buildCompButtons();
	}

	@Override
	protected void cmdClearWidgetSelected() {
		clearFields();
		clearTable();
		btnSearch.setEnabled(true);
	}

	private void clearFields() {
		txtPatientId.setText("");
		txtPatientId.setEnabled(true);
		txtPatientId.setEditable(true);
		btnSave.setEnabled(false);
		txtPatientName.setText("");
		txtAge.setText("");
		txtDateOfLastPickup.setText("");
		txtDOB.setText("");
		int items = compTable.getTable().getItemCount();
		for (int i = 0; i < items; i++) {
			compTable.getTable().remove(0);
		}
	}

	private void clearTable() {
		for (int i = 0; i < compTable.getTable().getItemCount(); i++) {
			TableItem ti = compTable.getTable().getItem(i);
			ti.setText(8, "0");
			ti.setText(9, "0");
		}
	}

	@Override
	protected void cmdCancelWidgetSelected() {
		cmdCloseSelected();
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();
			AdherenceManager.save(getHSession(), compTable.getPillCounts());
			if (iDartProperties.isEkapaVersion) {
				java.util.List<AdherenceRecord> adhList = new ArrayList<AdherenceRecord>();

				adhList = AdherenceManager.getAdherenceRecords(getHSession(),
						previousPack);
				TemporaryRecordsManager.saveAdherenceRecordsToDB(getHSession(),
						adhList);

			}
			getHSession().flush();
			tx.commit();

			getLog().info("Pillcount saved");
			MessageBox save = new MessageBox(getShell(), SWT.ICON_INFORMATION
					| SWT.OK);

			save.setText("Pill Count Saved");
			save
			.setMessage("Saved pill count for patient '"
					+ txtPatientId.getText()
					+ "' have been saved successfully");
			save.open();

			cmdClearWidgetSelected();

		} catch (HibernateException he) {
			getLog().error("couldn't save pillcounts");
			MessageBox cantSave = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);

			cantSave.setText("Cannot Save pillcount");
			cantSave.setMessage("Unable to save pillcount. Please try again");
			cantSave.open();
			if (tx != null) {
				tx.rollback();
			}
		}

		if (getHSession() != null) {
			getHSession().close();
		}
		setHSession(HibernateUtil.getNewSession());

	}

	private void cmdSearchSelected() {
		
		String patientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText());

		PatientSearch search = new PatientSearch(getShell(), getHSession());
		PatientIdentifier identifier = search.search(patientId);
		
		if (identifier != null) {
			localPatient = identifier.getPatient();
			txtPatientId.setText(localPatient.getPatientId());
			txtPatientId.setEditable(false);
			txtPatientId.setEnabled(false);
			populateGui();
		}
	}

	protected void populateGui() {
		Prescription prescription = localPatient.getCurrentPrescription();
		if (prescription != null) {
			previousPack = PackageManager.getLastPackagePickedUp(getHSession(),
					prescription.getPatient());
			if (previousPack != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
				txtPatientName.setText(localPatient.getFirstNames() + " "
						+ localPatient.getLastname());
				txtAge.setText(String.valueOf(localPatient.getAge()));
				txtDOB.setText(sdf.format(localPatient.getDateOfBirth()));
				Date lastPickupDate = previousPack.getPickupDate();
				long numOfDays = iDARTUtil
				.getDaysBetween(previousPack
						.getPickupDate(), new Date());
				txtDateOfLastPickup.setText(numOfDays + " days ("
						+ sdf.format(lastPickupDate) + ")");
				btnSave.setEnabled(true);
				try {
					compTable.populateLastPackageDetails(previousPack, sdf.parse(txtDateOfLastPickup.getText()) );
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				MessageBox noPackage = new MessageBox(getShell().getParent()
						.getShell(), SWT.ICON_INFORMATION | SWT.OK);
				noPackage.setText("No Package for patient");
				noPackage
				.setMessage("This patient does not have any packages!");
				noPackage.open();
				cmdClearWidgetSelected();
			}
		} else {
			MessageBox noPackage = new MessageBox(getShell().getParent()
					.getShell(), SWT.ICON_INFORMATION | SWT.OK);
			noPackage.setText("No Prescription for patient");
			noPackage.setMessage("This patient does not have a prescription!");
			noPackage.open();
			cmdClearWidgetSelected();
		}
	}

	@Override
	protected void clearForm() {
	}

	/**
	 * Method enableFields.
	 * @param enable boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
	}

	/**
	 * Method fieldsOk.
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {
		return false;
	}

	/**
	 * Method submitForm.
	 * @return boolean
	 */
	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}
}
