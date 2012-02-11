package org.celllife.idart.integration.eKapa.gui;

import java.sql.SQLException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.manager.AdministrationManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.welcome.GenericWelcome;
import org.celllife.idart.integration.eKapa.EKapa;
import org.celllife.idart.integration.eKapa.EKapa.NumberType;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

/**
 * Creates the Gui screen and generates an iDart patient object corresponding to
 * the person selected.
 * 
 */
public class SearchPatientGui {

	private static final String INFORMATION = "Information";

	private static final String MISSING_INFORMATION = "Missing Information";

	private static Shell searchShell = null; // @jve:decl-index=0:visual-constraint="10,94"

	static Logger log = Logger.getLogger(SearchPatientGui.class.getName());

	private Patient selectedPatient; // @jve:decl-index=0:

	// alpha search
	private Composite alphaComp = null;

	private Label lblSN = null;

	private Text txtSN = null;

	private Label lblFN = null;

	private Text txtFN = null;

	private Label lblDOB = null;

	private Text txtDOB = null;

	private Label lblSex = null;

	private Combo cmbSex = null;

	private Label lblAR = null;

	private Text txtAR = null;

	// number search
	private Composite numComp = null;

	private Label lblNum = null;

	private Text txtNum = null;

	private Label lblType = null;

	private Combo cmbType = null;

	// tabs
	private TabFolder tabFolder = null;

	private TabItem tabAlpha = null;

	private TabItem tabNumber = null;

	// table
	private Table table = null;

	// buttons
	private Composite compButtons = null;

	private Button btnSearch = null;

	private Button btnClear = null;

	private Button btnCancel = null;

	private Shell parent = null;

	private Session hSession = null;

	/**
	 * Constructor for SearchPatientGui.
	 * @param hSession Session
	 * @param parent Shell
	 * @param withGui boolean
	 */
	public SearchPatientGui(Session hSession, Shell parent, boolean withGui) {
		super();
		this.parent = parent;
		this.hSession = hSession;
		if (!withGui)
			return;
		// gui option
		createShell();
		searchShell.open();

		while (!searchShell.isDisposed()) {
			if (!GenericWelcome.display.readAndDispatch()) {
				GenericWelcome.display.sleep();
			}
		}

	}

	/**
	 * Checks that all the alpha search fields are valid. The rest of the
	 * validation is done in the eKapa class.
	 * 
	 * @return boolean
	 */
	private boolean alphaFieldsOK() {
		boolean result = false;
		if (txtSN.getText().length() < 2) {
			MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
					"Please enter a surname (at least 2 character)");
			txtSN.setFocus();
			txtSN.selectAll();
			return false;
		} else if (txtFN.getText().length() < 1) {
			MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
					"Please enter a first name (at least 1 character)");
			txtFN.setFocus();
			txtFN.selectAll();
			return false;
		} else if (txtDOB.getText().length() < 4) {
			MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
					"Please enter a date of birth (at least the year)\n Format is yyyymmdd");

			txtDOB.setFocus();
			txtDOB.selectAll();
			return false;
		} else if (txtDOB.getText().length() > 3) {
			try {
				Integer.parseInt(txtDOB.getText());
				result = true;
			} catch (NumberFormatException e) {
				txtDOB.setFocus();
				txtDOB.selectAll();
				MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
						"Please enter a date of birth (at least the year)\n The format is yyyymmdd");
				return false;
			}
		}

		if (txtAR.getText().length() > 0) {
			try {
				int ar = Integer.parseInt(txtAR.getText());
				if ((ar > 9) || (ar < 0)) {
					MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
							"The age range can only contain numbers from 0-9");
					txtAR.setFocus();
					txtAR.selectAll();
					result = false;
				} else {
					result = true;
				}
			} catch (NumberFormatException e) {
				MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
						"The age range can only contain numbers from 0-9");
				txtAR.setFocus();
				result = false;
			}
		}
		return result;
	}

	/**
	 * Cancels the patient search gui and returns to the previous screen
	 * 
	 */
	private void cmdCancel() {
		searchShell.getParent().setEnabled(true);
		searchShell.dispose();
	}

	/**
	 * Clears the search gui
	 * 
	 */
	private void cmdClear() {
		// get active tab and clear all fields
		txtNum.setText("");
		cmbType.select(0);
		if (iDartProperties.isEkapaVersion) {
			txtFN.setText("");
			txtSN.setText("");
			txtDOB.setText("");
			cmbSex.select(0);
			txtAR.setText("");
		}
		table.removeAll();
		table.setItemCount(0);
	}

	/**
	 * Search the database by number.
	 * <p>
	 * The number tab is open. This allows for searches by patient id, id number
	 * or other (usually passport number).
	 * 
	 */
	private void searchNum() {
		// first, parse the text in txtPatientId
		String patientId = PatientBarcodeParser.getPatientId(txtNum.getText());

		if (patientId != null) {
			txtNum.setText(patientId);
		}
		// first search idart database
		Patient p = PatientManager.getPatient(hSession, patientId);

		if (p != null) {
			selectedPatient = p;

			cmdCancel();
		} else {
			// search eKapa
			if (!iDartProperties.isEkapaVersion) {
				MessageDialog.openInformation(searchShell,INFORMATION,
						"That patient number does not exist");
				cmdClear();
				txtNum.setFocus();
				return;
			}

			// get the valid number type
			NumberType tmp = null;
			for (NumberType nt : EKapa.NumberType.values()) {
				if (nt.value().equals(cmbType.getText())) {
					tmp = nt;
					break;
				}
			}
			try {
				List<Patient> patients = EKapa.search(txtNum.getText(), tmp);
				if (patients.size() < 1) {
					MessageDialog.openError(searchShell,"Patient not found",
							"No matching patients were found");
				}
				populateTable(patients);
			} catch (SQLException e) {
				MessageDialog.openError(searchShell,"Connection Error",
						"Cannot connect to the ekapa database.  Please notify the System administrator");

			}
		}
	}

	/**
	 * Search the database by a number of criteria.
	 * <p>
	 * The alpha tab is open. This allows for searches by name, surname etc. A
	 * number of these criteria are compulsory.
	 * 
	 */
	private void searchAlpha() {
		if (!iDartProperties.isEkapaVersion) {
			log.error("This facility is not yet available for iDART");
			cmdClear();
			txtNum.setFocus();
			return;
		}
		// search eKapa
		int ar = 2;

		try {
			ar = Integer.valueOf(txtAR.getText()).intValue();

		} catch (NumberFormatException e) {
			ar = 2;
		}
		char sex = Character.toUpperCase(cmbSex.getText().charAt(0));

		try {
			List<Patient> patients = EKapa.search(txtFN.getText(), txtSN
					.getText(), txtDOB.getText(), ar, sex);
			if (patients.size() < 1) {
				MessageDialog.openInformation(searchShell,INFORMATION,
						"No patients were found");
			}
			populateTable(patients);
		} catch (SQLException e) {
			MessageDialog.openError(searchShell,"Connection Error",
					"Cannot connect to the ekapa database.  Please notify the System administrator");

		}
	}

	/**
	 * Searches for the patient, first in the iDART database then in the eKapa
	 * database
	 * 
	 */
	private void cmdSearch() {
		if (numComp.isVisible()) { // if its a number search
			if (numFieldsOK()) {
				searchNum();
			}
		} else if (alphaComp.isVisible()) { // must be alpha search
			if (alphaFieldsOK()) {
				searchAlpha();
			}
		}
	}

	/**
	 * Creates the alpha search composite
	 * 
	 */
	private void createAlphaComp() {
		GridData gdText = new GridData();
		gdText.horizontalAlignment = GridData.FILL;
		gdText.grabExcessHorizontalSpace = true;
		gdText.verticalAlignment = GridData.CENTER;

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 5;
		gridLayout.horizontalSpacing = 5;

		alphaComp = new Composite(tabFolder, SWT.NONE);
		alphaComp.setLayout(gridLayout);

		lblSN = new Label(alphaComp, SWT.NONE);
		lblSN.setText("* Surname:");
		lblSN.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblSN.setToolTipText("Surname must contain at least two characters");

		txtSN = new Text(alphaComp, SWT.BORDER);
		txtSN.setLayoutData(gdText);
		txtSN.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtSN.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdSearch();
				}
			}
		});

		lblFN = new Label(alphaComp, SWT.NONE);
		lblFN.setText("* First Name:");
		lblFN.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblFN.setToolTipText("First name must contain at least one character");
		txtFN = new Text(alphaComp, SWT.BORDER);
		txtFN.setLayoutData(gdText);
		txtFN.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtFN.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdSearch();
				}
			}
		});

		lblDOB = new Label(alphaComp, SWT.NONE);
		lblDOB.setText("* Date of Birth:");
		lblDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblDOB
		.setToolTipText("Date of birth must contain at least the year.  Format is yyyymmdd");
		txtDOB = new Text(alphaComp, SWT.BORDER);
		txtDOB.setLayoutData(gdText);
		txtDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDOB.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdSearch();
				}
			}
		});

		lblSex = new Label(alphaComp, SWT.NONE);
		lblSex.setText("* Sex:");
		lblSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblSex.setToolTipText("The gender of a patient must be selected");

		cmbSex = new Combo(alphaComp, SWT.BORDER | SWT.READ_ONLY);
		cmbSex.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbSex.setLayoutData(gdText);
		getGender(cmbSex);
		cmbSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbSex.select(0);

		lblAR = new Label(alphaComp, SWT.NONE);
		lblAR.setText("  Age Range:");
		lblAR.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblAR
		.setToolTipText("This number must be smaller than 9.  The default is 2");
		txtAR = new Text(alphaComp, SWT.BORDER);
		txtAR.setLayoutData(gdText);
		txtAR.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtAR.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdSearch();
				}
			}
		});
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	private void createCompButtons() {
		GridData gdGroup = new GridData();
		gdGroup.horizontalAlignment = GridData.FILL;
		gdGroup.grabExcessHorizontalSpace = true;
		gdGroup.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.widthHint = 112;
		GridLayout gridLayout = new GridLayout(3, true);

		gridLayout.horizontalSpacing = 5;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 15;
		compButtons = new Composite(searchShell, SWT.NONE);
		compButtons.setLayout(gridLayout);
		compButtons.setLayoutData(gdGroup);

		btnSearch = new Button(compButtons, SWT.NONE);
		btnSearch.setText("Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch.setLayoutData(gridData);
		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearch();
			}
		});

		btnClear = new Button(compButtons, SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear.setLayoutData(gridData);
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdClear();
			}
		});

		btnCancel = new Button(compButtons, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setLayoutData(gridData);
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {

			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCancel();
			}
		});
	}

	/**
	 * This method initializes numComp
	 * 
	 */
	private void createNumComp() {

		GridData gdText = new GridData();
		gdText.horizontalAlignment = GridData.FILL;
		gdText.grabExcessHorizontalSpace = true;
		gdText.verticalAlignment = GridData.CENTER;
		gdText.widthHint = 75;

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 5;
		gridLayout.horizontalSpacing = 5;

		numComp = new Composite(tabFolder, SWT.NONE);
		numComp.setLayout(gridLayout);

		lblNum = new Label(numComp, SWT.NONE);
		lblNum.setText("* Number:");
		lblNum.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtNum = new Text(numComp, SWT.BORDER);
		txtNum.setLayoutData(gdText);
		txtNum.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if ((e.character == SWT.CR)
						|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {

					if (cmbType.getText().equals(EKapa.NumberType.PWGC.value())) {
						String patientId = PatientBarcodeParser
						.getPatientId(txtNum.getText());

						if (patientId != null) {
							txtNum.setText(patientId);
						}
					}
					cmdSearch();
				}
			}
		});
		txtNum.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNum.setFocus();

		lblType = new Label(numComp, SWT.NONE);
		lblType.setText("* Type:");
		lblType.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbType = new Combo(numComp, SWT.BORDER | SWT.READ_ONLY);
		cmbType.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		getTypes(cmbType);
		cmbType.setText(cmbType.getItem(0));
		cmbType.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbType.setLayoutData(gdText);
		cmbType.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdSearch();
				}
			}
		});
	}

	/**
	 * This method initializes sShell
	 */
	private void createShell() {
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.marginWidth = 15;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.horizontalSpacing = 15;
		gridLayout.verticalSpacing = 15;
		gridLayout.marginHeight = 15;

		searchShell = new Shell(parent, SWT.DIALOG_TRIM);
		Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
		searchShell.setImage(i);
		searchShell.setText("eKapa Patient Search");
		searchShell.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		searchShell.setSize(new Point(721, 299));
		searchShell.setLayout(gridLayout);
		// add listener for cross in top left corner
		searchShell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				cmdCancel();
			}
		});

		createTabFolder();
		createTblResults();
		createCompButtons();
		LayoutUtils.centerGUI(searchShell);
	}

	/**
	 * This method initializes tabFolder
	 * 
	 */
	private void createTabFolder() {
		GridData gridData1 = new GridData();
		gridData1.heightHint = 160;
		gridData1.grabExcessVerticalSpace = true;

		tabFolder = new TabFolder(searchShell, SWT.NONE);
		tabFolder.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		createNumComp();
		tabFolder.setLayoutData(gridData1);
		tabNumber = new TabItem(tabFolder, SWT.NONE);
		tabNumber.setText("Number Search");
		tabNumber.setControl(numComp);

		if (!iDartProperties.isEkapaVersion)
			return;
		/*
		 * eKapa specific code
		 */
		// ekapa since iDART can't use this search facility
		createAlphaComp();
		tabAlpha = new TabItem(tabFolder, SWT.NONE);
		tabAlpha.setText("Details Search");
		tabAlpha.setControl(alphaComp);
	}

	/**
	 * Creates the results table.
	 * 
	 */
	private void createTblResults() {
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.verticalAlignment = GridData.FILL;
		gridData.heightHint = -1;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		table = new Table(searchShell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		table.setToolTipText("Double Click to select the patient");
		table.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				selectedPatient = (Patient) item.getData();
				cmdCancel();
			}
		});

		TableColumn colPawc = new TableColumn(table, SWT.NONE);
		colPawc.setWidth(90);
		colPawc.setText("PAWC Number");
		colPawc.setResizable(false);
		colPawc.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				sortColumn(0);
			}
		});

		TableColumn colFirstName = new TableColumn(table, SWT.NONE);
		colFirstName.setWidth(70);
		colFirstName.setText("First Name");
		colFirstName.setResizable(false);
		colFirstName.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				sortColumn(0);
			}
		});
		TableColumn colLastName = new TableColumn(table, SWT.NONE);
		colLastName.setWidth(80);
		colLastName.setText("Last Name");
		colLastName.setResizable(false);
		colLastName.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				sortColumn(0);
			}
		});
		TableColumn colDob = new TableColumn(table, SWT.NONE);
		colDob.setWidth(75);
		colDob.setText("Birthday");
		colDob.setResizable(false);
		colDob.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				sortColumn(0);
			}
		});
		TableColumn colSex = new TableColumn(table, SWT.NONE);
		colSex.setWidth(50);
		colSex.setText("Sex");
		colSex.setResizable(false);
		colSex.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				sortColumn(0);
			}
		});
		TableColumn colId = new TableColumn(table, SWT.NONE);
		colId.setWidth(90);
		colId.setText("ID Number");
		colId.setResizable(false);
		colId.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event e) {
				sortColumn(0);
			}
		});
	}

	/**
	 * Fills the combo box with valid genders.
	 * 
	 * @param combo
	 */
	private void getGender(Combo combo) {
		combo.add("Female");
		combo.add("Male");
	}

	/**
	 * Returns the selected patient.
	 * 
	 * @return Patient
	 */
	public Patient getPatient() {
		if (selectedPatient == null)
			return null;
		else {
			selectedPatient.setClinic(AdministrationManager.getClinic(hSession,
					AdministrationManager.getDefaultClinicName(hSession)));
			Episode e = PatientManager.getMostRecentEpisode(selectedPatient);
			e.setStartDate(new Date());
			e.setStartReason("eKapa Import");
			PatientManager.addEpisodeToPatient(selectedPatient, e);
			selectedPatient.setAccountStatus(true);
			return selectedPatient;
		}
	}

	/**
	 * Get the valid types of numbers from the simpledomain table
	 * 
	 * @param combo
	 */
	private void getTypes(Combo combo) {
		if (iDartProperties.isEkapaVersion) {

			for (EKapa.NumberType ng : EKapa.NumberType.values()) {
				combo.add(ng.value());
			}
		} else {
			combo.add("Folder No");
		}
	}

	/**
	 * Checks that the number search fields are valid. The main check is done in
	 * the ekapa class.
	 * 
	 * @return boolean
	 */
	private boolean numFieldsOK() {
		boolean result = true;
		String number = txtNum.getText();
		if (number.length() <= 1) {
			MessageDialog.openInformation(searchShell,MISSING_INFORMATION,
					"Please enter a number");
			txtNum.setFocus();
			result = false;
		} else if (number.length() > 20) {
			MessageDialog.openInformation(searchShell,INFORMATION,
					"The number must be less than 20 characters");
			txtNum.setFocus();
			result = false;
		} else if (cmbType.getText().equals("")) {
			MessageDialog.openInformation(searchShell,INFORMATION,
					"Please choose a type");
			cmbType.setFocus();
			result = false;
		}
		return result;
	}

	/**
	 * Populates the table with the results.
	 * 
	 * @param patients
	 *            List<Patient>
	 */
	private void populateTable(List<Patient> patients) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		table.removeAll();
		table.setItemCount(0);
		for (Patient p : patients) {
			TableItem ti = new TableItem(table, SWT.NONE);
			ti.setText(0, p.getPatientId());
			ti.setText(1, p.getFirstNames());
			ti.setText(2, p.getLastname());
			ti.setText(3, df.format(p.getDateOfBirth()));
			ti.setText(4, (Character.toLowerCase(p.getSex()) == 'm') ? "Male"
					: "Female");
			PatientIdentifier natId = p.getIdentifier(LocalObjects.nationalIdentifierType);
			ti.setText(5, natId == null ? "" : natId.getValue());
			ti.setData(p);
		}
	}

	/**
	 * This method searches for the patient, only based on the pawc number. This
	 * is for when the barcode is scanned in AddPrescription, and the GUI isnt
	 * needed.
	 * 
	 * @param pawcNo
	 */
	public void searchNoGui(String pawcNo) {
		if ((selectedPatient = PatientManager.getPatient(hSession, pawcNo)) == null) {
			// idart patient doesn't exist .. try ekapa
			if (!iDartProperties.isEkapaVersion)
				return;
			try {
				List<Patient> patients = EKapa.search(pawcNo,
						EKapa.NumberType.PWGC);
				// only populate patient if unique patient is found
				if (patients.size() == 1) {
					selectedPatient = patients.get(0);
				}
			} catch (SQLException e) {
				MessageDialog.openInformation(searchShell,"Connection Error",
						"Cannot connect to the ekapa database.  Please notify the System administrator");
			}
		}
	}

	/**
	 * Sort all the table entries alphabetically by in tries in the column
	 * 
	 * @param column
	 *            entries to sort
	 * 
	 */
	private void sortColumn(int column) {
		log.debug("SORTING TABLE COLUMN " + column);
		// sort column 1
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		String[] origValues1 = new String[table.getColumnCount()];
		for (int i = 1; i < items.length; i++) {
			// save all values
			for (int k = 0; k < table.getColumnCount(); k++) {
				origValues1[k] = items[i].getText(k);
			}
			Object data1 = items[i].getData(items[i].getText(0));

			for (int j = 0; j < i; j++) {
				// save all values
				String value = items[j].getText(column);
				if (collator.compare(origValues1[column], value) < 0) {
					// columns
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(origValues1);
					item.setData(origValues1[0], data1);
					items = table.getItems();
					break;
				}
			}
		}
	}

}
