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

package org.celllife.idart.gui.prescription;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.DeletionsManager;
import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.reports.PatientHistoryReport;

import org.apache.log4j.Logger;
import org.celllife.function.AndRule;
import org.celllife.function.DateRuleFactory;
import org.celllife.function.IRule;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.doctor.AddDoctor;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.packaging.NewPatientPackaging;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.user.ConfirmWithPasswordDialogAdapter;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateChangedEvent;
import org.celllife.idart.gui.widget.DateChangedListener;
import org.celllife.idart.gui.widget.DateException;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.celllife.idart.integration.eKapa.gui.SearchPatientGui;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.FloatValidator;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class AddPrescription extends GenericFormGui implements
iDARTChangeListener {

	private Button btnAddDrug;

	private Button btnSearch;

	private Button btnEkapaSearch;

	private TableColumn clmAmt;

	private TableColumn clmDrugName;

	private TableColumn clmSpace;

	private TableColumn clmTake;

	private Text txtClinic;

	// private CCombo cmbClinicalStage;

	private CCombo cmbDoctor;

	private CCombo cmbDuration;

	private CCombo cmbUpdateReason;

	private Composite compButtonsMiddle;

	private Group grpDrugs;

	private Group grpParticulars;

	private Group grpPatientID;

	private int intDrugTableSize = 1;

	private Label lblNewPrescriptionId;

	private Label lblPicChild;

	private Label lblUpdateReason;

	private Patient thePatient;

	private TableColumn tblDescription;

	private Table tblDrugs;

	private TableColumn tblPerDay;

	private TableColumn tblTPD;

	private TableEditor editor;

	private Text txtAge;

	private Text txtDOB;

	private Text txtName;

	private Text txtPatientId;

	private Text txtSurname;

	private boolean isInitialPrescription;

	private Composite compInstructions;

	public Prescription localPrescription;

	private Set<Prescription> patientsPrescriptions;

	private Label lblPicAddDrug;

	private Composite compDispense;

	private Button btnDispenseDrugs;

	private Label lblPicDispenseDrugs;

	private Button btnAddDoctor;

	private Text txtAreaNotes;

	private Button btnPatientHistoryReport;

	private Text txtWeight;

	private CCombo cmbDrugGroup;

	private Button btnRemoveDrug;

	private Button btnMoveUp;

	private Button btnMoveDown;

	boolean fromShortcut;

	private DateButton btnCaptureDate;

	/**
	 * Constructor
	 * 
	 * @param patient
	 *            String
	 * @param theParent
	 *            Shell
	 * @param fromShortcut
	 *            boolean
	 */
	// this method happens when loading up this screen
	public AddPrescription(Patient patient, Shell theParent,
			boolean fromShortcut) {
		super(theParent, HibernateUtil.getNewSession());
		this.fromShortcut = fromShortcut;
		localPrescription = new Prescription();

		if (patient != null) {

			Patient pat = PatientManager.getPatient(getHSession(), patient.getId());
			localPrescription.setPatient(pat);

			if (!pat.getAccountStatusWithCheck()) {
				MessageBox noPatient = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);

				noPatient.setText("Patient Does Not Have Current Episode");
				noPatient
				.setMessage("The patient "
						+ (txtPatientId.getText()).toUpperCase()
						+ " does not have a current episode. \n\nYou need to start a new episode for this patient (using the 'Update Existing Patient' screen) before you capture their prescription.");
				noPatient.open();
				txtPatientId.setFocus();
				txtPatientId.setText("");
			} else {
				thePatient = pat;
				checkFirstPrescription();
				loadPatientDetails();
				enableFields(true);
				txtPatientId.setEnabled(false);
				btnSearch.setEnabled(false);
				btnEkapaSearch.setEnabled(false);

				if (isInitialPrescription) {
					setFormToInitialPrescription();
				} else {
					Prescription script = thePatient
					.getMostRecentPrescription();
					if (script != null) {
						localPrescription = script;
						loadPrescriptionDetails();
						cmbUpdateReason.setEnabled(true);
					} else {
						setFormToInitialPrescription();
					}
				}
			}
		}
	}

	/**
	 * This method initializes getMyShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = Messages.getString("addPrescription.title"); //$NON-NLS-1$
		Rectangle bounds = new Rectangle(50, 0, 900, 700);
		// Parent Generic Methods ------
		buildShell(shellTxt, bounds); // generic shell build
	}

	@Override
	protected void createContents() {
		createCompInstructions();
		createGrpPatientID();
		createParticularsGroup();
		createCompButtonsMiddle();
		createGrpDrugs();
		createCompDispense();
		clearForm();

	}

	/**
	 * Method addDisposeListener.
	 * 
	 * @param dl
	 *            DisposeListener
	 */
	public void addDisposeListener(DisposeListener dl) {
		getShell().addDisposeListener(dl);
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Patient's Prescription";
		iDartImage icoImage = iDartImage.PRESCRIPTIONNEW;
		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createCompButtons() {
		// Parent Class generic call
		buildCompButtons();
		btnSave.setText("Save this Prescription");
		// btnSavePrescription

	}

	/**
	 * This method initializes compInstructions
	 * 
	 */
	private void createCompInstructions() {

		compInstructions = new Composite(getShell(), SWT.NONE);
		compInstructions.setBounds(new Rectangle(327, 58, 250, 20));

		Label lblInstructions = new Label(compInstructions, SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(0, 2, 250, 18));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));
	}

	/**
	 * This method creates the group containing the patient's ID, search button
	 * & reason for prescription update
	 * 
	 */
	private void createGrpPatientID() {

		// grpPatientID
		grpPatientID = new Group(getShell(), SWT.NONE);
		grpPatientID.setBounds(new Rectangle(235, 84, 430, 70));

		// Patient ID
		Label lblPatientId = new Label(grpPatientID, SWT.NONE);
		lblPatientId.setBounds(new Rectangle(10, 15, 110, 20));
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$

		txtPatientId = new Text(grpPatientID, SWT.BORDER);
		txtPatientId.setBounds(new Rectangle(150, 13, 150, 20));
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ((e.character == SWT.CR)
						|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
					cmdSearchWidgetSelected();
				}
			}
		});

		btnSearch = new Button(grpPatientID, SWT.NONE);
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch.setBounds(new Rectangle(310, 4, 105, 30));
		btnSearch.setText("Patient Search");
		btnSearch
		.setToolTipText("Press this button to search for an existing patient");
		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});

		btnEkapaSearch = new Button(grpPatientID, SWT.NONE);
		btnEkapaSearch.setBounds(new Rectangle(310, 36, 105, 30));
		btnEkapaSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnEkapaSearch.setText("eKapa Search");
		if (!iDartProperties.isEkapaVersion) {
			btnEkapaSearch.setVisible(false);
		}
		if (thePatient != null) {
			btnEkapaSearch.setEnabled(false);
		}

		btnEkapaSearch
		.setToolTipText("Press this button to import a patient from eKapa.");
		btnEkapaSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdEkapaSearchWidgetSelected();
			}
		});

		if (thePatient != null) {
			btnSearch.setEnabled(false);
			btnEkapaSearch.setEnabled(false);
		}

		lblUpdateReason = new Label(grpPatientID, SWT.NONE);
		lblUpdateReason.setBounds(new Rectangle(10, 40, 140, 20));
		lblUpdateReason.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblUpdateReason.setText("* Reason for Update:");

		cmbUpdateReason = new CCombo(grpPatientID, SWT.BORDER | SWT.READ_ONLY);
		cmbUpdateReason.setBounds(new Rectangle(150, 40, 150, 20));
		cmbUpdateReason.setEditable(false);
		cmbUpdateReason.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbUpdateReason.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		CommonObjects.populatePrescriptionUpdateReasons(getHSession(),
				cmbUpdateReason);
		cmbUpdateReason.setVisibleItemCount(cmbUpdateReason.getItemCount());

	}

	@SuppressWarnings("unchecked")
	private void setCaptureDateRestrictions() throws DateException {
		IRule<Date> rule = DateRuleFactory.beforeNowInclusive(false);
		if (thePatient != null) {
			Episode latestEpi = PatientManager.getMostRecentEpisode(thePatient);
			Date dteEpisodeStart = latestEpi.getStartDate();
			IRule<Date> afterEpisodeStart = DateRuleFactory.afterInclusive(
					dteEpisodeStart, false);
			afterEpisodeStart.setDescription("This patient was marked as a "
					+ latestEpi.getStartReason()
					+ " on <date>. The prescription date must be on,"
					+ " or after this episode's date.");
			rule = new AndRule<Date>(rule, afterEpisodeStart);
		}

		btnCaptureDate.setValidator(new DateInputValidator(rule));
		btnCaptureDate.setDate(new Date());
	}

	/**
	 * This method initializes grpParticulars
	 * 
	 */
	private void createParticularsGroup() {

		// grpParticulars
		grpParticulars = new Group(getShell(), SWT.NONE);
		grpParticulars.setBounds(new Rectangle(40, 157, 810, 155));
		grpParticulars.setText("Patient and New Prescription Information");
		grpParticulars.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Name
		Label lblName = new Label(grpParticulars, SWT.NONE);
		lblName.setBounds(new Rectangle(10, 20, 110, 20));
		lblName.setText("First Names:");
		lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtName = new Text(grpParticulars, SWT.BORDER);
		txtName.setBounds(new Rectangle(120, 20, 130, 20));
		txtName.setEnabled(false);
		txtName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Surname
		Label lblSurname = new Label(grpParticulars, SWT.NONE);
		lblSurname.setBounds(new Rectangle(10, 45, 110, 20));
		lblSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblSurname.setText("Surname:");

		txtSurname = new Text(grpParticulars, SWT.BORDER);
		txtSurname.setBounds(new Rectangle(120, 45, 130, 20));
		txtSurname.setEnabled(false);
		txtSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblPicPatientHistoryReport
		btnPatientHistoryReport = new Button(grpParticulars, SWT.NONE);
		btnPatientHistoryReport.setBounds(new Rectangle(270, 48, 40, 40));
		btnPatientHistoryReport
		.setToolTipText("Press this button to view and / or print reports \nof patients' Prescription History.");
		btnPatientHistoryReport.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PATIENTHISTORY_30X26));

		btnPatientHistoryReport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdPatientHistoryWidgetSelected();
			}
		});
		btnPatientHistoryReport.setEnabled(false);

		// Age
		Label lblAge = new Label(grpParticulars, SWT.NONE);
		lblAge.setBounds(new Rectangle(10, 70, 110, 20));
		lblAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblAge.setText("Age && DOB:");
		txtAge = new Text(grpParticulars, SWT.BORDER);
		txtAge.setBounds(new Rectangle(120, 70, 40, 20));
		txtAge.setEnabled(false);
		txtAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDOB = new Text(grpParticulars, SWT.BORDER);
		txtDOB.setBounds(new Rectangle(170, 70, 80, 20));
		txtDOB.setEnabled(false);
		txtDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Picture of a child
		lblPicChild = new Label(grpParticulars, SWT.BORDER);
		lblPicChild.setBounds(new Rectangle(275, 90, 30, 26));
		lblPicChild.setImage(ResourceUtils.getImage(iDartImage.CHILD_30X26));
		lblPicChild.setVisible(false);

		// Down Referral Clinic
		Label lblClinic = new Label(grpParticulars, SWT.NONE);
		lblClinic.setBounds(new Rectangle(10, 95, 110, 20));
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblClinic.setText("Clinic:");

		txtClinic = new Text(grpParticulars, SWT.BORDER);
		txtClinic.setBounds(new Rectangle(120, 95, 130, 20));
		txtClinic.setText("");
		txtClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtClinic.setEditable(false);
		txtClinic.setEnabled(false);

		// Capture Date
		Label lblCaptureDate = new Label(grpParticulars, SWT.NONE);
		lblCaptureDate.setBounds(new Rectangle(10, 120, 110, 20));
		lblCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCaptureDate.setText("Capture Date:");

		btnCaptureDate = new DateButton(grpParticulars, DateButton.NONE, null);
		btnCaptureDate.setBounds(120, 120, 130, 25);
		btnCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCaptureDate.setEnabled(false);
		btnCaptureDate.addDateChangedListener(new DateChangedListener() {
			@Override
			public void dateChanged(DateChangedEvent event) {
				cmdUpdatePrescriptionId();
				cmdUpdateClinic();
			}
		});

		// Doctor
		Label lblDoctor = new Label(grpParticulars, SWT.NONE);
		lblDoctor.setBounds(new Rectangle(350, 20, 90, 20));
		lblDoctor.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDoctor.setText("* Doctor:");

		cmbDoctor = new CCombo(grpParticulars, SWT.BORDER | SWT.READ_ONLY
				| SWT.V_SCROLL);
		cmbDoctor.setBounds(new Rectangle(450, 20, 130, 20));
		cmbDoctor.setEditable(false);
		cmbDoctor.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbDoctor.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateDoctors(getHSession(), cmbDoctor, false);
		cmbDoctor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				cmbDoctor.removeAll();
				CommonObjects.populateDoctors(getHSession(), cmbDoctor, false);
				cmbDoctor.setVisibleItemCount(Math.min(
						cmbDoctor.getItemCount(), 25));
			}
		});
		cmbDoctor.setFocus();

		btnAddDoctor = new Button(grpParticulars, SWT.NONE);
		btnAddDoctor.setBounds(new Rectangle(560, 45, 40, 40));
		btnAddDoctor.setImage(ResourceUtils.getImage(iDartImage.DOCTOR_30X26));
		btnAddDoctor
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAddDoctorWidgetSelected();
			}
		});
		btnAddDoctor.setToolTipText("Press this button to add a new doctor");

		// Duration
		Label lblDuration = new Label(grpParticulars, SWT.NONE);
		lblDuration.setBounds(new Rectangle(350, 45, 90, 20));
		lblDuration.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDuration.setText("* Duration:");

		cmbDuration = new CCombo(grpParticulars, SWT.BORDER | SWT.READ_ONLY);
		cmbDuration.setBounds(new Rectangle(450, 45, 100, 20));
		cmbDuration.setEditable(false);
		cmbDuration.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbDuration.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populatePrescriptionDuration(getHSession(), cmbDuration);
		cmbDuration.setVisibleItemCount(cmbDuration.getItemCount());

		// Clinical Stage
		// Label lblClinicalStage = new Label(grpParticulars, SWT.NONE);
		// lblClinicalStage.setBounds(new Rectangle(350, 70, 84, 20));
		// lblClinicalStage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		// lblClinicalStage.setText("   Clinical Stage:");

		// cmbClinicalStage = new CCombo(grpParticulars, SWT.BORDER);
		// cmbClinicalStage.setBounds(new Rectangle(450, 70, 90, 20));
		// cmbClinicalStage
		// .setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		// cmbClinicalStage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		// CommonObjects.populateClinicalStage(getHSession(), cmbClinicalStage);
		// cmbClinicalStage.setEditable(false);

		// Weight
		Label lblWeight = new Label(grpParticulars, SWT.NONE);
		lblWeight.setBounds(new Rectangle(350, 72, 90, 20));
		lblWeight.setText("   Weight:");
		lblWeight.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtWeight = new Text(grpParticulars, SWT.BORDER);
		txtWeight.setBounds(new Rectangle(450, 70, 46, 20));
		txtWeight.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblKg = new Label(grpParticulars, SWT.NONE);
		lblKg.setBounds(new Rectangle(500, 72, 30, 20));
		lblKg.setText("kg");
		lblKg.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Drug Group
		Label lblDrugGroup = new Label(grpParticulars, SWT.NONE);
		lblDrugGroup.setBounds(new Rectangle(350, 120, 90, 20));
		lblDrugGroup.setText("  Drug Group:");
		lblDrugGroup.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbDrugGroup = new CCombo(grpParticulars, SWT.BORDER | SWT.READ_ONLY);
		cmbDrugGroup.setBounds(new Rectangle(450, 120, 130, 20));
		cmbDrugGroup.setVisibleItemCount(10);
		cmbDrugGroup.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbDrugGroup.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		CommonObjects.populateDrugGroups(getHSession(), cmbDrugGroup);
		cmbDrugGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {

				populateDrugsFromDrugGroup(cmbDrugGroup.getText());
			}
		});

		// Prescription Notes
		Label lblNotes = new Label(grpParticulars, SWT.CENTER | SWT.BORDER);
		lblNotes.setBounds(new Rectangle(620, 22, 170, 20));
		lblNotes.setText("Prescription Notes:");
		lblNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtAreaNotes = new Text(grpParticulars, SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		txtAreaNotes.setBounds(new Rectangle(620, 40, 170, 80));
		txtAreaNotes.setText("");
		txtAreaNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// New Prescription ID
		lblNewPrescriptionId = new Label(grpParticulars, SWT.CENTER
				| SWT.BORDER);
		lblNewPrescriptionId.setBounds(new Rectangle(620, 120, 170, 20));
		lblNewPrescriptionId.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes compButtonsMiddle
	 * 
	 */
	private void createCompButtonsMiddle() {

		// compButtonsMiddle
		compButtonsMiddle = new Composite(getShell(), SWT.NONE);
		compButtonsMiddle.setBounds(new Rectangle(200, 312, 500, 50));

		// Add Drug button and icon
		lblPicAddDrug = new Label(compButtonsMiddle, SWT.NONE);
		lblPicAddDrug.setBounds(new Rectangle(10, 10, 30, 26));
		lblPicAddDrug.setImage(ResourceUtils
				.getImage(iDartImage.PRESCRIPTIONADDDRUG_30X26));

		btnAddDrug = new Button(compButtonsMiddle, SWT.NONE);
		btnAddDrug.setBounds(new Rectangle(60, 12, 185, 27));
		btnAddDrug.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnAddDrug.setText("Add Drug to Prescription");
		btnAddDrug.setEnabled(false);
		btnAddDrug
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAddDrugWidgetSelected();
			}
		});
		btnAddDrug.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnRemoveDrug = new Button(compButtonsMiddle, SWT.NONE);
		btnRemoveDrug.setBounds(new Rectangle(268, 11, 181, 28));
		btnRemoveDrug.setText("Remove Selected Drug");
		lblPicAddDrug.setEnabled(false);
		btnRemoveDrug.setEnabled(false);
		btnRemoveDrug
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdRemoveDrugWidgetSelected();
			}
		});
		btnRemoveDrug.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// View Prescription History button and icon
	}

	/**
	 * This method initializes grpDrugs
	 * 
	 */
	private void createGrpDrugs() {

		grpDrugs = new Group(getShell(), SWT.NONE);

		grpDrugs.setText("Drugs In Prescription:");
		grpDrugs.setBounds(new Rectangle(100, 361, 700, 199));
		grpDrugs.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnMoveUp = new Button(grpDrugs, SWT.NONE);
		btnMoveUp.setBounds(5, 85, 40, 40);
		btnMoveUp.setImage(ResourceUtils.getImage(iDartImage.UPARROW_30X26));
		btnMoveUp
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {

				if (tblDrugs.getSelectionIndex() != -1) {
					cmdMoveDrug(-1);
				} else {
					MessageBox mb = new MessageBox(getShell(),
							SWT.ICON_QUESTION | SWT.OK);
					mb.setText("No Drug Selected ");
					mb.setMessage("Please select a drug to move.");
					mb.open();
				}
			}
		});

		btnMoveDown = new Button(grpDrugs, SWT.NONE);
		btnMoveDown.setBounds(655, 80, 40, 40);
		btnMoveDown
		.setImage(ResourceUtils.getImage(iDartImage.DOWNARROW_30X26));
		btnMoveDown
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (tblDrugs.getSelectionIndex() != -1) {
					cmdMoveDrug(1);
				} else {
					MessageBox mb = new MessageBox(getShell(),
							SWT.ICON_QUESTION | SWT.OK);
					mb.setText("No Drug Selected ");
					mb.setMessage("Please select a drug to move.");
					mb.open();
				}
			}
		});

		createDrugsTable();
	}

	/**
	 * This method initializes tblDrugs
	 * 
	 */
	private void createDrugsTable() {

		tblDrugs = new Table(grpDrugs, SWT.FULL_SELECTION);
		tblDrugs.setLinesVisible(true);
		tblDrugs.setBounds(new Rectangle(50, 25, 600, 160));
		tblDrugs.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblDrugs.setHeaderVisible(true);

		// 0 - clmSpace
		clmSpace = new TableColumn(tblDrugs, SWT.NONE);
		clmSpace.setWidth(28);
		clmSpace.setText("No");

		// clmDrugBarcode
		// clmDrugBarcode = new TableColumn(tblDrugs, SWT.NONE);
		// clmDrugBarcode.setText("Drug Barcode");
		// clmDrugBarcode.setWidth(120);
		// clmDrugBarcode.setResizable(false);

		// 1 - clmDrugName
		clmDrugName = new TableColumn(tblDrugs, SWT.NONE);
		clmDrugName.setText("Drug Name");
		clmDrugName.setWidth(235);
		clmDrugName.setResizable(false);

		// 2 - clmTake
		clmTake = new TableColumn(tblDrugs, SWT.NONE);
		clmTake.setWidth(90);
		// clmTake.setText("Directions");
		clmTake.setResizable(false);

		// 3 - clmAmt
		clmAmt = new TableColumn(tblDrugs, SWT.NONE);
		// clmAmt.setText("Amount");
		clmAmt.setWidth(40);
		clmAmt.setResizable(false);

		// 4 - tblDescription
		tblDescription = new TableColumn(tblDrugs, SWT.NONE);
		tblDescription.setWidth(70);
		tblDescription.setResizable(false);

		// 5 - tblPerDay
		tblPerDay = new TableColumn(tblDrugs, SWT.NONE);
		tblPerDay.setWidth(30);
		tblPerDay.setResizable(false);

		// 6 - tblTPD
		tblTPD = new TableColumn(tblDrugs, SWT.NONE);
		tblTPD.setWidth(95);
		tblTPD.setResizable(false);

		editor = new TableEditor(tblDrugs);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		tblDrugs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editor.getEditor();
				if (old != null) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblDrugs.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblDrugs.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}

					}

					if (column == 3 || column == 5) {
						// Create the Text Object for your editor
						final Text text = new Text(tblDrugs, SWT.None);
						text.setForeground(item.getForeground());

						// Transfer any text from the cell to the text control
						// and store a copy of the original text
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));
						text.selectAll();
						text.setFocus();

						// Recalculate the minimum width for the editor
						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

						// Add a handler to transfer the text back to the cell
						// any time its modified
						final int col = column;
						text.addModifyListener(new ModifyListener() {

							@Override
							public void modifyText(ModifyEvent event1) {
								// Set the text of the editor back into the cell
								String newVal = text.getText();

								// case for backspace and other non text
								// characters
								if (0 == newVal.length())
									return;
								if (iDARTUtil.isAlpha(newVal)
										|| newVal.length() > 5)
									return;

								item.setText(col, text.getText());

								if (col == 3) {
									if (!FloatValidator.isPositiveFloat(newVal))
										return;
									else {
										// Set the text of the editor back into
										// the cell
										item.setText(col, newVal);
										PrescribedDrugs pd = (PrescribedDrugs) item
										.getData();
										pd.setAmtPerTime(Double
												.parseDouble(newVal));
										item.setData(pd);
									}
								} else if (col == 5) {
									if (!iDARTUtil.isPositiveInteger(newVal))
										return;
									else {
										// Set the text of the editor back into
										// the cell
										item.setText(col, newVal);
										PrescribedDrugs pd = (PrescribedDrugs) item
										.getData();
										pd.setTimesPerDay(Integer
												.parseInt(newVal));
										item.setData(pd);
									}
								}

							}

						});
					}
				}
			}
		});

	}

	/**
	 * This method initializes compButtons
	 * 
	 * @return boolean
	 */

	/**
	 * Checks the GUI for valid fields entries
	 * 
	 * @return true if the requiResourceUtils.getColor(iDartColor.RED) fields
	 *         are filled in
	 */
	@Override
	protected boolean fieldsOk() {

		if ((cmbDoctor.getText().trim().equals(""))
				|| (lblNewPrescriptionId.getText().trim().equals(""))
				|| (cmbDuration.getText().trim().equals(""))) {
			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("Missing fields");
			missing
			.setMessage("Some fields are empty. Please re-enter the missing fields.");
			missing.open();
			txtPatientId.setFocus();
			return false;
		}

		else if (lblUpdateReason.isVisible()
				&& cmbUpdateReason.getText().trim().equals("")) {
			MessageBox missingUpdateReason = new MessageBox(getShell(),
					SWT.ICON_ERROR | SWT.OK);
			missingUpdateReason.setText("Missing Update Reason");
			missingUpdateReason
			.setMessage("The 'Reason for Update' field has not been completed. Please select a reason.");
			cmbUpdateReason.setFocus();
			missingUpdateReason.open();
			return false;
		} else if (!txtWeight.getText().equals("")) {
			try {
				Double.parseDouble(txtWeight.getText());
			} catch (NumberFormatException nfe) {
				MessageBox incorrectData = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);
				incorrectData.setText("Incorrect Weight Value");
				incorrectData
				.setMessage("The weight that was entered is incorrect. Please enter a number.");
				incorrectData.open();
				txtWeight.setFocus();
				return false;
			}
		}
		return true;

	}

	/**
	 * This method is called if it is found that this is the 1st prescription
	 * for this patient. Certain areas of the GUI are updated to reflect this.
	 * 
	 */
	private void setFormToInitialPrescription() {
		getShell().redraw();

		cmbUpdateReason.setText("Initial");
		cmbUpdateReason.setEnabled(false);
		lblHeader.setText("Add Initial Prescription");

		// btnDispenseDrugs.setEnabled(true);
		// lblPicDispenseDrugs.setEnabled(true);
		btnCaptureDate.setDate(new Date());
		btnSave.setText("Save Initial Prescription");
		btnSave.redraw();

		// Generate a new prescription id
		cmdUpdatePrescriptionId();
		cmbDuration.setText("1 month");

	}

	/**
	 * This method is called if it is found that this is the not the 1st
	 * prescription for this patient, but there's no current prescription.
	 * Certain areas of the GUI are updated to reflect this.
	 * 
	 */
	private void setFormToReactivationPrescription() {

		btnSave.setText("Save Prescription");
		btnSave.redraw();

		// Generate a new prescription id
		cmdUpdatePrescriptionId();
		cmbDuration.setText("1 month");

		btnSearch.setEnabled(false);
		btnEkapaSearch.setEnabled(false);

	}

	/**
	 * This method loads the GUI with details from the Prescription object
	 * passed to it.
	 * 
	 */
	private void loadPrescriptionDetails() {

		/*
		 * // set restrictions on the capture date try {
		 * setCaptureDateRestrictions(); } catch (DateException e) {
		 * e.printStackTrace(); }
		 */

		cmbUpdateReason.setText("Renewal");
		String tempAmtPerTime = "";
		cmbDoctor.setText(""
				+ AdministrationManager.getDoctor(getHSession(),
						localPrescription.getDoctor().getFullname())
						.getFullname());

		if (localPrescription.getDuration() <= 2) {
			cmbDuration.setText(localPrescription.getDuration() + " weeks");
		} else {
			cmbDuration
			.setText(localPrescription.getDuration() / 4 + " months");
		}

		// cmbClinicalStage.setText("" + localPrescription.getClinicalStage());

		// set previous note details
		if (localPrescription.getNotes() != null) {
			txtAreaNotes.setText(localPrescription.getNotes());
		}
		// set the previous weight

		if (localPrescription.getWeight() != null) {
			txtWeight.setText(localPrescription.getWeight().toString());
		}

		// Generate a new prescription id
		cmdUpdatePrescriptionId();

		List<PrescribedDrugs> drugs = localPrescription.getPrescribedDrugs();
		for (int i = 0; i < drugs.size(); i++) {

			PrescribedDrugs pd = drugs.get(i);
			Drug theDrug = pd.getDrug();
			Form theForm = theDrug.getForm();

			if (theForm.getFormLanguage1().equals(""))
				// is a cream - no amnt per time
			{
				tempAmtPerTime = "";
			}

			else {
				if (new BigDecimal(pd.getAmtPerTime()).scale() == 0) {
					tempAmtPerTime = ""
						+ new BigDecimal(pd.getAmtPerTime())
					.unscaledValue().intValue();
				} else {
					tempAmtPerTime = "" + pd.getAmtPerTime();
				}
			}

			TableItem ti = new TableItem(tblDrugs, SWT.NONE);
			String[] temp = new String[8];
			temp[0] = "" + intDrugTableSize;
			// temp[1] = "0";
			temp[1] = theDrug.getName();
			temp[2] = theForm.getActionLanguage1();
			temp[3] = tempAmtPerTime;
			temp[4] = theForm.getFormLanguage1();
			temp[5] = "" + pd.getTimesPerDay();
			temp[6] = "Times a day";

			ti.setText(temp);
			ti.setData(pd);
			intDrugTableSize += 1;

		}
	}

	/**
	 * This method is called if the user clicks on a row in the drugs table. The
	 * user is then asked if they want to delete the drug that they've selected.
	 * 
	 */
	private void cmdRemoveDrugWidgetSelected() {

		TableItem[] ti = tblDrugs.getSelection();

		if (ti != null) {
			try {
				String drug = ti[0].getText(1);
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				mb.setText("Remove Drug '" + drug + "'");
				mb.setMessage("Are you sure you would like to remove '" + drug
						+ "' from this prescription?");
				switch (mb.open()) {
				case SWT.YES:
					// Delete from Prescription
					int index = tblDrugs.getSelectionIndex();
					tblDrugs.remove(index);
					for (int i = index; i < tblDrugs.getItemCount(); i++) {
						TableItem oti = tblDrugs.getItem(i);
						int number = Integer.parseInt(oti.getText(0));
						number--;
						oti.setText(0, "" + number);
					}
					intDrugTableSize--;
					break;
				case SWT.NO:
					// Nothing
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION
						| SWT.OK);
				mb.setText("No Drug Selected ");
				mb.setMessage("Please select a drug to remove.");
				mb.open();

			}

		}

	}

	/**
	 * This method swaps the drug selected with the one above it (moveAmount =
	 * -1) or below it (moveAmount =1), if any
	 * 
	 * @param moveAmount
	 *            int
	 */
	private void cmdMoveDrug(int moveAmount) {

		int index = tblDrugs.getSelectionIndex();

		if (((index + moveAmount) >= 0)
				&& ((index + moveAmount) < tblDrugs.getItemCount())) {

			TableItem tmpTi = tblDrugs.getItem(index);
			TableItem tmpTiMovedTo = tblDrugs.getItem(index + moveAmount);

			String[] tempMoving = new String[8];
			tempMoving[0] = tmpTiMovedTo.getText(0);
			tempMoving[1] = tmpTi.getText(1);
			tempMoving[2] = tmpTi.getText(2);
			tempMoving[3] = tmpTi.getText(3);
			tempMoving[4] = tmpTi.getText(4);
			tempMoving[5] = tmpTi.getText(5);
			tempMoving[6] = tmpTi.getText(6);

			PrescribedDrugs pdMoving = (PrescribedDrugs) tmpTi.getData();

			String[] tempMovedTo = new String[8];
			tempMovedTo[0] = tmpTi.getText(0);
			tempMovedTo[1] = tmpTiMovedTo.getText(1);
			tempMovedTo[2] = tmpTiMovedTo.getText(2);
			tempMovedTo[3] = tmpTiMovedTo.getText(3);
			tempMovedTo[4] = tmpTiMovedTo.getText(4);
			tempMovedTo[5] = tmpTiMovedTo.getText(5);
			tempMovedTo[6] = tmpTiMovedTo.getText(6);

			PrescribedDrugs pdMovedTo = (PrescribedDrugs) tmpTiMovedTo
			.getData();

			tblDrugs.getItem(index).setText(tempMovedTo);
			tblDrugs.getItem(index).setData(pdMovedTo);

			tblDrugs.getItem(index + moveAmount).setText(tempMoving);
			tblDrugs.getItem(index + moveAmount).setData(pdMoving);

			tblDrugs.setSelection(index + moveAmount);

		}
	}

	/**
	 * This method loads the GUI with details of the global Patient object
	 * ('thePatient').
	 * 
	 */
	public void loadPatientDetails() {

		// set restrictions on the capture date
		try {
			setCaptureDateRestrictions();
		} catch (DateException e) {
			e.printStackTrace();
		}

		try {

			txtPatientId.setText(thePatient.getPatientId());
			txtName.setText(thePatient.getFirstNames());
			txtSurname.setText(thePatient.getLastname());
			txtClinic.setText(thePatient.getCurrentClinic().getClinicName());
			txtAge.setText(String.valueOf(thePatient.getAge()));

			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
			txtDOB.setText(String.valueOf(sdf.format(thePatient
					.getDateOfBirth())));

			btnDispenseDrugs.setEnabled(true);
			lblPicDispenseDrugs.setEnabled(true);

			// Show the child icon if age <= 12
			if (thePatient.getAge() <= 12) {
				lblPicChild.setVisible(true);
			} else {
				lblPicChild.setVisible(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called when the user presses the "Add Drug to this
	 * Prescription" button.
	 * 
	 */

	private void cmdAddDrugWidgetSelected() {
		if (fieldsOk()) {
			try {
				// Add a new table item
				TableItem ti = new TableItem(tblDrugs, SWT.NONE);
				ti.setText(0, (Integer.toString(tblDrugs.getItemCount())));
				new PrescriptionObject(getHSession(), ti, false, getShell());
				intDrugTableSize = tblDrugs.getItemCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loadPrescription() {
		patientsPrescriptions = thePatient.getPrescriptions();

		/*
		 * // set date restrictions try { setCaptureDateRestrictions(); } catch
		 * (DateException e) { e.printStackTrace(); }
		 */

		if (patientsPrescriptions.size() > 0) {
			// Patient has previous Prescription

			cmbUpdateReason.setEnabled(true);
			cmbUpdateReason.setText("");

			localPrescription = thePatient.getMostRecentPrescription();

			if (localPrescription == null) {
				// Patient has had a previous prescription, but doesn't
				// have an active one at the moment
				localPrescription = new Prescription();
				localPrescription.setPatient(thePatient);
				setFormToReactivationPrescription();
				// Generate a new prescription id
				cmdUpdatePrescriptionId();
			}

			else {

				loadPrescriptionDetails();

				lblHeader.setText("Update a Prescription");

				btnDispenseDrugs.setEnabled(true);
				lblPicDispenseDrugs.setEnabled(true);
			}

		} else {
			// New Prescription
			localPrescription = new Prescription();
			localPrescription.setPatient(thePatient);
			setFormToInitialPrescription();

			// Generate a new prescription id
			cmdUpdatePrescriptionId();
		}
		loadPatientDetails();

		enableFields(true);
		btnSearch.setEnabled(false);
		btnEkapaSearch.setEnabled(false);
		txtPatientId.setEnabled(false);
	}

	private void populateGUI() {

		loadPatientDetails();

		patientsPrescriptions = thePatient.getPrescriptions();

		if (patientsPrescriptions != null && patientsPrescriptions.size() > 0) {
			// Patient has previous Prescription

			cmbUpdateReason.setEnabled(true);
			cmbUpdateReason.setText("");
			localPrescription = thePatient.getMostRecentPrescription();

			if (localPrescription == null) {
				// Patient has had a previous prescription, but doesn't have
				// an active one at the moment
				localPrescription = new Prescription();
				localPrescription.setPatient(thePatient);
				setFormToReactivationPrescription();
				// Generate a new prescription id
				cmdUpdatePrescriptionId();
			} else {

				loadPrescriptionDetails();

				lblHeader.setText("Update a Prescription");

				btnDispenseDrugs.setEnabled(true);
				lblPicDispenseDrugs.setEnabled(true);
			}

		} else {
			// New Prescription
			localPrescription.setPatient(thePatient);
			setFormToInitialPrescription();
			// Generate a new prescription id
			cmdUpdatePrescriptionId();
		}

		loadPatientDetails();

		enableFields(true);

		txtPatientId.setEnabled(false);
		btnSearch.setEnabled(false);
		btnEkapaSearch.setEnabled(false);

	}

	/*
	 * This method called a "Search" GUI. Upon successful selection from the
	 * user, the PackageManager loads the selected patient's information
	 */
	private void cmdSearchWidgetSelected() {
		
		String parsedPatientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText());

		PatientSearch search = new PatientSearch(getShell(), getHSession());
		PatientIdentifier identifier = search.search(parsedPatientId);
		
		if (identifier != null) {
			thePatient = identifier.getPatient();
			txtPatientId.setText(thePatient.getPatientId());
					
			if (!thePatient.getAccountStatusWithCheck()) {
				MessageBox noPatient = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);

				noPatient.setText("Patient Does Not Have Current Episode");
				noPatient
				.setMessage("The patient "
						+ (txtPatientId.getText()).toUpperCase()
						+ " does not have a current episode. \n\nYou need to start a new episode for this patient (using the 'Update Existing Patient' screen) before you capture their prescription.");
				noPatient.open();
				txtPatientId.setFocus();
				txtPatientId.setText("");
			} else {
				loadPrescription();
			}
		}
	}

	private void cmdEkapaSearchWidgetSelected() {

		SearchPatientGui ps = new SearchPatientGui(getHSession(), getShell(),
				true);

		Patient p = ps.getPatient();
		// check our local database if this patient already exists
		if (p != null) {
			Patient patient = PatientManager.getPatient(getHSession(), p
					.getId());
			if (patient == null) {
				patient = p;

				MessageBox mSave = new MessageBox(getShell(), SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				mSave.setText("Import eKapa Patient?");
				mSave.setMessage("Are you sure you want to import patient '"
						+ patient.getPatientId() + "' ("
						+ patient.getLastname() + "," + patient.getFirstNames()
						+ ") into the iDART database?");
				switch (mSave.open()) {
				case SWT.YES:

					PatientManager.savePatient(getHSession(), patient);
					break;
				case SWT.NO:
					return;
				}

			}
			thePatient = PatientManager.getPatient(getHSession(), p.getId());
			populateGUI();

		}

	}

	private void cmdDispenseARVDrugsSelected() {
		if (submitForm()) {
			if (!fromShortcut) {
				new NewPatientPackaging(getParent(), localPrescription.getPatient());
			}
			closeShell(true);
		}
	}

	/**
	 * This method looks at all this patient's prescriptions and determines if
	 * this should be the 'Initial' Prescription. Set the global variable
	 * 'isInitialPrescription' accordingly.
	 * 
	 */
	private void checkFirstPrescription() {

		patientsPrescriptions = thePatient.getPrescriptions();

		if (patientsPrescriptions == null || patientsPrescriptions.isEmpty()) {
			isInitialPrescription = true;
		} else {
			isInitialPrescription = false;
		}
	}

	/**
	 * Method drugsAddedToPrescription.
	 * 
	 * @return boolean
	 */
	private boolean drugsAddedToPrescription() {

		boolean drugsAdded = true;

		if (tblDrugs.getItemCount() < 1) {
			MessageBox noDrugsAdded = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			noDrugsAdded.setText("No Drugs on Prescription");
			noDrugsAdded
			.setMessage("No drugs have been added to this prescription.");
			noDrugsAdded.open();
			drugsAdded = false;
		}

		return drugsAdded;

	}

	/**
	 * Method submitForm.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean submitForm() {

		boolean saveSuccessful = false;
		boolean deletedPrescription = false;

		// Check if all the fields are filled in
		if (fieldsOk() && drugsAddedToPrescription()) {
			MessageBox mSave = new MessageBox(getShell(), SWT.ICON_QUESTION
					| SWT.YES | SWT.NO);
			mSave
			.setText(isInitialPrescription ? "Add Initial Prescription for Patient "
					.concat(txtPatientId.getText())
					: "Add New Prescription for Patient "
						.concat(txtPatientId.getText()));
			mSave
			.setMessage(isInitialPrescription ? "Are you sure you want to add this prescription to Patient "
					.concat(txtPatientId.getText()).concat("?")
					: "Do you want to update the changes to Patient "
						.concat(txtPatientId.getText()).concat("?"));
			switch (mSave.open()) {
			case SWT.YES:
				Transaction tx = null;
				try {
					tx = getHSession().beginTransaction();
					if (localPrescription.getPatient().getId() <= 0) {
						PatientManager.savePatient(getHSession(),
								localPrescription.getPatient());
					}
					setLocalPrescription();

					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
					Prescription oldPrescription = localPrescription
					.getPatient().getCurrentPrescription();
					// Check if any packages have been created for the
					// prescription
					if ((oldPrescription != null)
							&& PackageManager.getPackagesForPrescription(
									getHSession(), oldPrescription).size() == 0) {
						List<PrescribedDrugs> prescribedDrugs = oldPrescription
						.getPrescribedDrugs();
						String drugs = "";
						if (prescribedDrugs.size() == 0) {
							drugs = "\nThere are no drugs on this prescription!";
						}
						for (int i = 0; i < prescribedDrugs.size(); i++) {
							drugs = drugs
							+ "\n\t\t"
							+ prescribedDrugs.get(i).getDrug()
							.getName();
						}
						MessageBox box = new MessageBox(getShell(),
								SWT.ICON_QUESTION | SWT.YES | SWT.NO);

						box.setText("Delete Previous, Unused Prescription");
						box
						.setMessage("This patient has not received any drugs from "
								+ "their previous prescription with id "
								+ oldPrescription.getPrescriptionId()
								+ " (details below). \n\n '"
								+ "Doctor: "
								+ oldPrescription.getDoctor()
								.getFullname()
								+ "\nDuration: "
								+ oldPrescription.getDuration()
								+ " weeks "
								+ "\nClinical Stage: "
								+ oldPrescription.getClinicalStage()
								+ "\nWeight: "
								+ oldPrescription.getWeight()
								+ "\nCaptured on: "
								+ sdf.format(oldPrescription.getDate())
								+ "\nDrugs on Prescription:\t"
								+ drugs
								+ "\n\nWould you like to delete this unused, previous prescription, "
								+ "and replace it with the one you have just created?");
						switch (box.open()) {
						case SWT.YES:
							// before we try anything, lets ask the user for
							// their password
							ConfirmWithPasswordDialogAdapter passwordDialog = new ConfirmWithPasswordDialogAdapter(
									getShell(), getHSession());
							passwordDialog
							.setMessage("Please enter your Password");
							// if password verified
							String messg = passwordDialog.open();
							if (messg.equalsIgnoreCase("verified")) {
								deleteScript(oldPrescription, tx);
								deletedPrescription = true;
							}
							break;
						}
					}
					
					// de-normalise table to speed up reports 
					if(localPrescription.containsARVDrug())
						localPrescription.setDrugTypes("ARV");
					
					PackageManager.saveNewPrescription(getHSession(),
							localPrescription, deletedPrescription);
					getHSession().flush();
					tx.commit();

					getLog().info(
							"Saved Prescription for Patient: "
							+ localPrescription.getPatient().getId());
					MessageBox done = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					done.setText("Database Updated");
					done.setMessage("Prescription '".concat(
							localPrescription.getPrescriptionId()).concat(
							"' has been added to patient '").concat(
									localPrescription.getPatient().getPatientId())
									.concat("'."));
					done.open();
					saveSuccessful = true;
				} catch (IllegalArgumentException ie) {
					MessageBox errorBox = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					errorBox
					.setText("Can't Save: Prescription Capture Date Invalid");
					errorBox
					.setMessage("Prescription '"
							.concat(
									localPrescription
									.getPrescriptionId())
									.concat(
									"' has a capture date before the capture date of the previous prescription. Prescription could not be saved "));
					errorBox.open();
					if (tx != null) {
						tx.rollback();
					}
					getLog().error(ie);
					saveSuccessful = false;
				}

				catch (HibernateException he) {

					MessageBox errorBox = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					errorBox.setText("Can't Save: Database Update Failed");
					errorBox
					.setMessage("There was a problem saving this prescription. Please try again.");
					if (tx != null) {
						tx.rollback();
					}
					getLog().error(he);
					saveSuccessful = false;
				}

				break;
			case SWT.NO:
				// Close the GUI
				// cmdCancelWidgetSelected();
				saveSuccessful = false;
			}
		}

		return saveSuccessful;
	}

	/**
	 * Saves the new prescription to the database
	 */
	private void cmdSavePrescriptionWidgetSelected() {

		if (submitForm()) {
			cmdCancelWidgetSelected();
		}

	}

	private void cmdAddDoctorWidgetSelected() {
		AddDoctor.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
				true);
		AddDoctor ad = new AddDoctor(getShell());
		ad.addChangeListener(this);
		// cmbDoctor.removeAll();
		// CommonObjects.populateDoctors(cmbDoctor, false);
	}

	/**
	 * This method sets the global variable 'localPrescription', using
	 * information entered by user onto this GUI.
	 * 
	 */

	private void setLocalPrescription() {

		localPrescription = new Prescription();
		localPrescription.setPrescriptionId(lblNewPrescriptionId.getText());
		localPrescription.setPatient(thePatient);
		localPrescription.setReasonForUpdate(cmbUpdateReason.getText());

		// weight
		if (!txtWeight.getText().equals("")) {
			localPrescription
			.setWeight(Double.parseDouble(txtWeight.getText()));
		} else {
			localPrescription.setWeight(0.0);
		}

		// clinical stage
		// if (cmbClinicalStage.getText() != null
		// && !(cmbClinicalStage.getText().equals(""))) {
		// localPrescription.setClinicalStage(Integer
		// .parseInt(cmbClinicalStage.getText()));
		// }

		// duration
		if (cmbDuration.getText().endsWith("weeks")) {
			String[] s = cmbDuration.getText().split(" ");

			localPrescription.setDuration(Integer.parseInt(s[0]));

		}

		else {
			String[] s = cmbDuration.getText().split(" ");

			localPrescription.setDuration(Integer.parseInt(s[0]) * 4);

		}

		Date scriptDate = btnCaptureDate.getDate();

		// if scriptDate is today, store the time too, else store 12am
		Calendar compareCal = Calendar.getInstance();
		Calendar scriptDateCal = Calendar.getInstance();
		scriptDateCal.setTime(scriptDate);

		if ((compareCal.get(Calendar.DAY_OF_MONTH) == scriptDateCal
				.get(Calendar.DAY_OF_MONTH))
				&& (compareCal.get(Calendar.MONTH) == scriptDateCal
						.get(Calendar.MONTH))
						&& (compareCal.get(Calendar.YEAR) == scriptDateCal
								.get(Calendar.YEAR))) {
			scriptDate = new Date();
		}

		localPrescription.setDate(scriptDate);
		localPrescription.setDoctor(AdministrationManager.getDoctor(
				getHSession(), cmbDoctor.getText()));
		localPrescription.setModified('T');
		localPrescription.setCurrent('T');
		localPrescription.setNotes(txtAreaNotes.getText());

		List<PrescribedDrugs> prescribedDrugsList = new ArrayList<PrescribedDrugs>();

		// Save the Prescription Drugs
		for (int i = 0; i < tblDrugs.getItemCount(); i++) {

			TableItem tmpItem = tblDrugs.getItem(i);

			PrescribedDrugs oldPD = (PrescribedDrugs) tmpItem.getData();
			if (oldPD != null) {
				PrescribedDrugs newPD = new PrescribedDrugs();
				newPD.setAmtPerTime(oldPD.getAmtPerTime());
				newPD.setDrug(oldPD.getDrug());
				newPD.setModified(oldPD.getModified());
				newPD.setPrescription(localPrescription);
				newPD.setTimesPerDay(oldPD.getTimesPerDay());
				prescribedDrugsList.add(newPD);
			}
		}

		localPrescription.setPrescribedDrugs(prescribedDrugsList);
	}

	/**
	 * This method is called when the user presses the 'Cancel' button
	 */
	@Override
	protected void cmdCancelWidgetSelected() {
		localPrescription = null;
		btnCaptureDate.setValidator(null);
		closeShell(false);

	}

	/**
	 * This method is called when the user presses the 'Clear' button
	 */
	@Override
	protected void cmdClearWidgetSelected() {
		clearForm();
		btnCaptureDate.setValidator(null);
		txtPatientId.setFocus();
	}

	/**
	 * adds the drugs in a drug group selected by the user to the drugs table.
	 * 
	 * @param DrugGroupName
	 *            String
	 */
	private void populateDrugsFromDrugGroup(String DrugGroupName) {

		String tempAmtPerTime = "";

		if (tblDrugs.getItemCount() > 0) {
			MessageBox mSave = new MessageBox(getShell(), SWT.ICON_QUESTION
					| SWT.YES | SWT.NO);
			mSave.setText("Remove Existing Drugs?");
			mSave
			.setMessage("This prescription has existing drugs. Remove them?");
			switch (mSave.open()) {
			case SWT.YES:
				tblDrugs.clearAll();
				tblDrugs.removeAll();
				intDrugTableSize = 0;
				break;
			case SWT.NO:
				// Close the GUI

			}
		}

		if (!"".equals(DrugGroupName)) {

			Regimen reg = DrugManager.getRegimen(getHSession(), DrugGroupName);
			Iterator<RegimenDrugs> it = reg.getRegimenDrugs().iterator();

			while (it.hasNext()) {
				RegimenDrugs rd = it.next();

				Drug d = rd.getDrug();

				TableItem ti = new TableItem(tblDrugs, SWT.NONE);
				ti.setText(0, (Integer.toString(tblDrugs.getItemCount())));

				Form f = d.getForm();

				if (f.getFormLanguage1().equals(""))
					// is a cream - no amnt per time
				{
					tempAmtPerTime = "";
				}

				else {
					if (new BigDecimal(rd.getAmtPerTime()).scale() == 0) {
						tempAmtPerTime = ""
							+ new BigDecimal(rd.getAmtPerTime())
						.unscaledValue().intValue();
					} else {
						tempAmtPerTime = "" + rd.getAmtPerTime();
					}
				}

				// Add the details to the table
				String[] temp = new String[8];
				temp[0] = ti.getText(0);
				temp[1] = d.getName();
				temp[2] = f.getActionLanguage1();
				temp[3] = tempAmtPerTime;
				temp[4] = f.getFormLanguage1();
				temp[5] = (new Integer(rd.getTimesPerDay())).toString();
				temp[6] = "times a day";

				ti.setText(temp);

				PrescribedDrugs pd = new PrescribedDrugs();
				pd.setAmtPerTime(rd.getAmtPerTime());
				pd.setDrug(d);
				pd.setModified('T');
				pd.setTimesPerDay(rd.getTimesPerDay());

				ti.setData(pd);

				intDrugTableSize = tblDrugs.getItemCount();
			}
		}
	}

	/**
	 * Clears the patientForm and sets the default values
	 */
	@Override
	public void clearForm() {

		try {
			txtPatientId.setText("");
			cmbUpdateReason.setText("");
			txtName.setText("");
			txtSurname.setText("");
			txtClinic.setText("");
			cmbDoctor.setText("");
			txtWeight.setText("");
			txtDOB.setText("");
			lblNewPrescriptionId.setText("");
			cmbDuration.setText("");
			// cmbClinicalStage.setText("");
			lblPicChild.setVisible(false);
			txtAge.setText("");
			cmbUpdateReason.setText("");
			cmbUpdateReason.setEnabled(false);
			txtAreaNotes.setText("");

			lblHeader.setText("Patient's Prescription");

			btnSave.setText("Update this Prescription");
			tblDrugs.clearAll();
			tblDrugs.setItemCount(0);
			intDrugTableSize = 1;

			enableFields(false);

			btnSearch.setEnabled(true);
			btnEkapaSearch.setEnabled(true);
			txtPatientId.setEnabled(true);

			tblDrugs.clearAll();
			tblDrugs.setItemCount(0);
			intDrugTableSize = 1;

			Control old = editor.getEditor();
			if (old != null) {
				old.dispose();
			}

			txtPatientId.setFocus();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param enable
	 */
	@Override
	protected void enableFields(boolean enable) {

		btnAddDrug.setEnabled(enable);
		lblPicAddDrug.setEnabled(enable);

		btnRemoveDrug.setEnabled(enable);
		btnPatientHistoryReport.setEnabled(enable);

		cmbDoctor.setEnabled(enable);
		btnAddDoctor.setEnabled(enable);
		btnMoveUp.setEnabled(enable);
		btnMoveDown.setEnabled(enable);

		btnDispenseDrugs.setEnabled(enable);

		// cmbClinicalStage.setEnabled(enable);
		cmbDrugGroup.setEnabled(enable);
		cmbDuration.setEnabled(enable);
		txtWeight.setEnabled(enable);
		txtAreaNotes.setEnabled(enable);

		btnCaptureDate.setEnabled(enable);

		btnSave.setEnabled(enable);
		btnDispenseDrugs.setEnabled(enable);
		lblPicDispenseDrugs.setEnabled(enable);

		Color theColour;

		if (enable) {
			theColour = ResourceUtils.getColor(iDartColor.WHITE);
		} else {
			theColour = ResourceUtils.getColor(iDartColor.WIDGET_BACKGROUND);
		}

		cmbUpdateReason.setBackground(theColour);
		cmbDoctor.setBackground(theColour);
		cmbDuration.setBackground(theColour);
		cmbDrugGroup.setBackground(theColour);
	}

	/**
	 * This method initializes compDispense
	 * 
	 */
	private void createCompDispense() {

		compDispense = new Composite(getShell(), SWT.NONE);
		compDispense.setBounds(new Rectangle(330, 567, 240, 50));

		lblPicDispenseDrugs = new Label(compDispense, SWT.NONE);
		lblPicDispenseDrugs.setBounds(new Rectangle(0, 0, 50, 43));
		lblPicDispenseDrugs.setImage(ResourceUtils
				.getImage(iDartImage.DISPENSEPACKAGES));

		lblPicDispenseDrugs.setEnabled(false);

		btnDispenseDrugs = new Button(compDispense, SWT.NONE);
		btnDispenseDrugs.setBounds(new Rectangle(60, 12, 180, 30));
		btnDispenseDrugs.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDispenseDrugs.setText("Dispense This Prescription");
		btnDispenseDrugs
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdDispenseARVDrugsSelected();
			}
		});
		btnDispenseDrugs.setEnabled(false);

	}

	private void cmdUpdatePrescriptionId() {

		Date scriptDate = btnCaptureDate.getDate();
		if (scriptDate == null) {
			btnCaptureDate.setDate(new Date());
			scriptDate = btnCaptureDate.getDate();
		}

		String prescriptionId = PackageManager.getNewPrescriptionId(
				getHSession(), thePatient, scriptDate);
		lblNewPrescriptionId.setText(prescriptionId);
	}

	/**
	 * This method gets the clinic that the patient belongs to at the time given
	 * by the captureDate.
	 */
	private void cmdUpdateClinic() {
		if (localPrescription != null) {
			txtClinic.setText((localPrescription.getPatient()
					.getClinicAtDate(btnCaptureDate.getDate())).getClinicName());
		}
	}

	/**
	 * View the patient history report, or show a patient selection report
	 * parameters screen if no patient is selected
	 */
	private void cmdPatientHistoryWidgetSelected() {

		getLog().info(
		"New Patient Packaging: User chose 'Patient History Report'");

		if (localPrescription != null) {
			PatientHistoryReport report = new PatientHistoryReport(getShell(),
					localPrescription.getPatient());
			viewReport(report);
		}
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		cmdSavePrescriptionWidgetSelected();
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	/**
	 * Method deleteScript.
	 * 
	 * @param prescriptionToRemove
	 *            Prescription
	 * @param tx
	 *            Transaction
	 */
	private void deleteScript(Prescription prescriptionToRemove, Transaction tx) {
		DeletionsManager.removeUndispensedPrescription(getHSession(),
				prescriptionToRemove);
		getHSession().flush();

		MessageBox mb = new MessageBox(getShell());
		mb.setText("Prescription Deletion Successful");
		mb
		.setMessage("This prescription was successfully removed from the database.");
		mb.open();

	}

	public Patient getPatient() {
		return thePatient;
	}

	@Override
	public void changed(Object o) {
		if (o instanceof Doctor) {
			Doctor doctor = (Doctor) o;
			cmbDoctor.setText(doctor.getFullname());
		}
	}
}
