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

package org.celllife.idart.gui.label;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.print.label.PatientInfoLabel;
import org.celllife.idart.print.label.PrintLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 */
public class PrintPatientInfoLabel extends GenericOthersGui {

	private Label lblPatientId;

	private Label lblNumToPrint;

	private Combo cmbNumToPrint;

	private Button btnSearch;

	private Text txtPatientId;

	private Button btnPrint;

	private Button btnClear;

	private Button btnClose;

	private Group grpPatientInfo;

	private Label lblSurname;

	private Label lblFirstName;

	private Text txtSurname;

	private Text txtFirstName;

	private Text txtDateOfBirth;

	private Label lblDateOfBirth;

	private Label lblSex;

	private Button chkBxFemale;

	private Button chkBxMale;

	private Button chkBxUnknown;

	private Patient localPatient;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public PrintPatientInfoLabel(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		activate();
	}

	/**
	 * This method initializes getShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Print a Patient's Information Label";
		Rectangle bounds = new Rectangle(0, 0, 600, 444);
		buildShell(shellTxt, bounds);
		createGrpPatientInfo();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String txt = "Print a Patient's Information Label";
		iDartImage icoImage = iDartImage.PATIENTINFOLABEL;
		buildCompHeader(txt, icoImage);
	}

	/**
	 * This method initializes compOptions
	 * 
	 */
	@Override
	protected void createCompOptions() {

		Composite compOptions = new Composite(getShell(), SWT.NONE);
		compOptions.setBounds(new org.eclipse.swt.graphics.Rectangle(110, 90,
				380, 85));

		lblPatientId = new Label(compOptions, SWT.NONE);
		lblPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 20,
				130, 20));
		lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientId = new Text(compOptions, SWT.BORDER);
		txtPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 20,
				110, 20));
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			@Override
			public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
				if ((e.character == SWT.CR)
						|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
					cmdSearchWidgetSelected();
				}
			}
		});

		// lblNumToPrint
		lblNumToPrint = new Label(compOptions, SWT.NONE);
		lblNumToPrint.setBounds(new Rectangle(10, 50, 130, 20));
		lblNumToPrint.setText("Number of Labels:");
		lblNumToPrint.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// cmbNumToPrint
		cmbNumToPrint = new Combo(compOptions, SWT.NONE);
		cmbNumToPrint.setBounds(new Rectangle(152, 48, 90, 20));
		for (int i = 1; i < 13; i++) {
			cmbNumToPrint.add(Integer.toString(i));
		}
		cmbNumToPrint.setText("1");
		cmbNumToPrint.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnSearch = new Button(compOptions, SWT.NONE);
		btnSearch.setBounds(new org.eclipse.swt.graphics.Rectangle(270, 17,
				105, 30));
		btnSearch.setText("Patient Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});
		btnSearch
		.setToolTipText("Press this button to search for an existing patient.");
	}

	/**
	 * This method initializes grpPatientInfo
	 * 
	 */
	private void createGrpPatientInfo() {

		grpPatientInfo = new Group(getShell(), SWT.NONE);
		grpPatientInfo.setText("Patient Information");
		grpPatientInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientInfo.setBounds(new org.eclipse.swt.graphics.Rectangle(117,
				190, 350, 127));

		lblSurname = new Label(grpPatientInfo, SWT.NONE);
		lblSurname.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 30, 70,
				20));
		lblSurname.setText("Surname:");
		lblSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtSurname = new Text(grpPatientInfo, SWT.BORDER);
		txtSurname.setBounds(new org.eclipse.swt.graphics.Rectangle(105, 30,
				110, 20));
		txtSurname.setEditable(false);
		txtSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblFirstName = new Label(grpPatientInfo, SWT.NONE);
		lblFirstName.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 60,
				80, 20));
		lblFirstName.setText("First Name:");
		lblFirstName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtFirstName = new Text(grpPatientInfo, SWT.BORDER);
		txtFirstName.setBounds(new org.eclipse.swt.graphics.Rectangle(105, 60,
				110, 20));
		txtFirstName.setEditable(false);
		txtFirstName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDateOfBirth = new Label(grpPatientInfo, SWT.NONE);
		lblDateOfBirth.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 90,
				85, 20));
		lblDateOfBirth.setText("Date of Birth:");
		lblDateOfBirth.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDateOfBirth = new Text(grpPatientInfo, SWT.BORDER);
		txtDateOfBirth.setBounds(new org.eclipse.swt.graphics.Rectangle(105,
				90, 110, 20));
		txtDateOfBirth.setEditable(false);
		txtDateOfBirth.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblSex = new Label(grpPatientInfo, SWT.NONE);
		lblSex
		.setBounds(new org.eclipse.swt.graphics.Rectangle(241, 20, 29,
				16));
		lblSex.setText("Sex:");
		lblSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		chkBxFemale = new Button(grpPatientInfo, SWT.CHECK);
		chkBxFemale.setBounds(new org.eclipse.swt.graphics.Rectangle(244, 40,
				75, 22));
		chkBxFemale.setEnabled(false);
		chkBxFemale.setText("Female");
		chkBxFemale.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		chkBxMale = new Button(grpPatientInfo, SWT.CHECK);
		chkBxMale.setBounds(new org.eclipse.swt.graphics.Rectangle(244, 65, 77,
				22));
		chkBxMale.setEnabled(false);
		chkBxMale.setText("Male");
		chkBxMale.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		chkBxUnknown = new Button(grpPatientInfo, SWT.CHECK);
		chkBxUnknown.setBounds(new org.eclipse.swt.graphics.Rectangle(244, 90,
				77, 22));
		chkBxUnknown.setEnabled(false);
		chkBxUnknown.setText("Unknown");
		chkBxUnknown.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnPrint = new Button(getCompButtons(), SWT.NONE);
		btnPrint.setText("Print");
		btnPrint.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPrint
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdPrintWidgetSelected();
			}
		});
		btnPrint
		.setToolTipText("Press this button to print the specified patient information label.");

		btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear
		.setToolTipText("Press this button to clear all the information \nyou've entered, so that you can start again.");
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdClearWidgetSelected();
			}
		});

		btnClose = new Button(getCompButtons(), SWT.NONE);
		btnClose.setText("Close");
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClose.setToolTipText("Press this button to close this screen.");
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
	 * This method is called when the user pressed the "Print" button It checks
	 * that a patient ID has been entered, and also that a number of prints has
	 * been chosen. If it succeeds these checks, it creates a label for this
	 * patient, and prints it n number of times
	 */
	private void cmdPrintWidgetSelected() {

		getLog().info("User pressed 'Print' button");

		// Error checking
		if (localPatient == null) {
			MessageBox patientIdMissing = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			patientIdMissing.setText("Information Missing");
			patientIdMissing
			.setMessage("There's no information for the patient number: Either type in a patient number, or scan it in, or press the 'Patient Search' button.");
			patientIdMissing.open();
			txtPatientId.setFocus();
		}

		else if (cmbNumToPrint.getText().equals("")) {
			MessageBox numToPrintMissing = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			numToPrintMissing.setText("Information Missing");
			numToPrintMissing
			.setMessage("You haven't entered how many labels you would like printed. Please enter this information.");
			numToPrintMissing.open();
			cmbNumToPrint.setFocus();
		}

		// else, create a label for this patient,
		// and print it n number of times
		else {

			Object myInfoLabel;

			myInfoLabel = new PatientInfoLabel(localPatient);

			ArrayList<Object> labelList = new ArrayList<Object>(Integer
					.parseInt(cmbNumToPrint.getText()));

			for (int i = 0; i < Integer.parseInt(cmbNumToPrint.getText()); i++) {

				labelList.add(myInfoLabel);

			}
			try {
				PrintLabel.printiDARTLabels(labelList);
			} catch (Exception e) {
				getLog().error("Error printing patient info label", e);
			}

		}

	}

	/**
	 * This method is called when the user pressed the "Clear" button It clears
	 * the 2 input fields, enables the search button, and clears the information
	 * in the grpPatientInfo
	 */
	protected void cmdClearWidgetSelected() {
		localPatient = null;
		txtPatientId.setText("");
		btnSearch.setEnabled(true);
		cmbNumToPrint.setText("");
		txtPatientId.setFocus();
		txtSurname.setText("");
		txtFirstName.setText("");
		txtDateOfBirth.setText("");
		chkBxFemale.setSelection(false);
		chkBxMale.setSelection(false);

	}

	/**
	 * This method is called when the user pressed the "Close" button It
	 * disposes the current shell.
	 */
	protected void cmdCloseWidgetSelected() {
		getHSession().close();
		getShell().dispose();
	}

	/**
	 * This method is called when the user pressed the "Search" button It opens
	 * up a GUI where the user can select a patient If a patient is selected,
	 * his/her details are loaded into the grpPatientInfo.
	 */
	private void cmdSearchWidgetSelected() {
		
		String patientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText().trim());

		PatientSearch search = new PatientSearch(getShell(), getHSession());
		PatientIdentifier identifier = search.search(patientId);
		
		if (identifier != null) {
			localPatient = identifier.getPatient();
			txtPatientId.setText(localPatient.getPatientId());
			btnSearch.setEnabled(false);
			cmbNumToPrint.setFocus();
			loadPatientDetails(localPatient);
		} else {
			localPatient = null;
			txtPatientId.setFocus();
			txtPatientId.setText("");
		}

	}

	/**
	 * Method loadPatientDetails.
	 * 
	 * @param thePatient
	 *            Patient
	 */
	private void loadPatientDetails(Patient thePatient) {

		// populate the GUI
		txtSurname.setText(thePatient.getLastname());
		txtFirstName.setText(thePatient.getFirstNames());
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		txtDateOfBirth.setText(sdf.format(thePatient.getDateOfBirth()));

		// if patient is male, tick the male checkbox
		if ((thePatient.getSex() == 'm') || (thePatient.getSex() == 'M')) {
			chkBxMale.setSelection(true);
			chkBxFemale.setSelection(false);
			chkBxUnknown.setSelection(false);
		}

		// if patient is female, tick the female checkbox
		else if (thePatient.getSex() == 'f' || (thePatient.getSex() == 'F')) {
			chkBxFemale.setSelection(true);
			chkBxMale.setSelection(false);
			chkBxUnknown.setSelection(false);
		}

		// else, the patient sex has not been
		// captuResourceUtils.getColor(iDartColor.RED)
		else {
			chkBxUnknown.setSelection(true);
			chkBxFemale.setSelection(false);
			chkBxMale.setSelection(false);
		}
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
