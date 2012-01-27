package org.celllife.idart.gui.patient;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import model.manager.AdministrationManager;
import model.manager.AlertManager;
import model.manager.CampaignManager;
import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.StudyManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Campaign;
import org.celllife.idart.database.hibernate.CampaignParticipant;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.integration.mobilisr.MobilisrManager;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.AbstractCancellableJob;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.mobilisr.api.validation.MsisdnValidator;
import org.celllife.mobilisr.api.validation.MsisdnValidator.ValidationError;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

public class AddPatientToStudy extends GenericFormGui {
	
	private static Logger log = Logger.getLogger(AddPatientToStudy.class);
	
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); //$NON-NLS-1$

	private String selectedCampaignName = EMPTY;
	private String originalCellNo = EMPTY;
	private boolean isAdd;
	private Patient localPatient;
	private Button btnSearch;
	private Text txtPatientId;
	private Text txtFirstname;
	private Text txtLastname;
	private Text txtDob;
	private Text txtSex;
	private Text txtInitDate;
	private Text txtFirstCollection;
	private Text txtEnrolmentDate;
	private Text txtCellNo;
	private Combo cmbCampaigns;
	private Combo cmbCustomMsgTime;
	private Button chkConsent;
	private Combo cmbLanguage;
	private Label lblNote;
	private Combo cmbNetwork;
	private Text txtAltCellNo;

	private StudyParticipant studyParticipant;

	public AddPatientToStudy(Shell parent, boolean isAdd) {
		super(parent, HibernateUtil.getNewSession());
		this.isAdd = isAdd;
	}

	@Override
	protected void clearForm() {
		studyParticipant = null;
		localPatient = null;
		txtPatientId.setText(EMPTY);
		txtFirstname.setText(EMPTY);
		txtLastname.setText(EMPTY);
		txtDob.setText(EMPTY);
		txtSex.setText(EMPTY);
		txtInitDate.setText(EMPTY);
		txtFirstCollection.setText(EMPTY);
		txtCellNo.setText(EMPTY);
		if (cmbCustomMsgTime != null)
			cmbCustomMsgTime.setText(PropertiesManager.sms().defaultCustomMsgTime());
		if (txtAltCellNo != null)
			txtAltCellNo.setText(EMPTY);
		if (cmbNetwork != null)
			cmbNetwork.setText(EMPTY);
		if (chkConsent != null)
			chkConsent.setSelection(false);
		if (cmbLanguage != null)
			cmbLanguage.select(0);
		if (cmbCampaigns != null)
			cmbCampaigns.select(0);
		if (txtEnrolmentDate != null)
			txtEnrolmentDate.setText(EMPTY);
		btnSave.setEnabled(false);
	}

	@Override
	protected void cmdCancelWidgetSelected() {
		closeShell(true);
	}

	@Override
	protected void cmdClearWidgetSelected() {
		clearForm();
	}

	/**
	 * Add the patient to the study
	 */
	@Override
	protected void cmdSaveWidgetSelected() {
		if (fieldsOk()){
			boolean success = true;
			if (isAdd) {
				success = addStudyParticipant();
			} else {
				success = removeStudyParticipant();
			}
			
			if (success) {
				clearForm();
			}
		}	
	}

	@Override
	protected void createCompButtons() {
		setBtnSaveText(isAdd ? Messages.getString("addtostudy.btn.save.text") : Messages.getString("addtostudy.btn.save.text.remove")); //$NON-NLS-1$ //$NON-NLS-2$
		buildCompButtons();
	}

	@Override
	protected void createCompHeader() {
		isAdd = (Boolean) GenericFormGui
				.getInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate);
		String headerTxt = isAdd ? Messages.getString("addtostudy.title.add") //$NON-NLS-1$
				: Messages.getString("addtostudy.title.remove"); //$NON-NLS-1$
		iDartImage icoImage = iDartImage.PATIENTNEW;
		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createContents() {
		createPatientSearch();
		createPatientDetailLabels();
		createPatientDetailTextBoxes();
		setChangeListeners();
	}

	@Override
	protected void enableFields(boolean enable) {
	}

	@Override
	protected boolean fieldsOk() {
		if (MobilisrManager.validateMsisdn(txtCellNo.getText().trim()) != null){
			ValidationError error = MobilisrManager.validateMsisdn(txtCellNo.getText().trim());
			String message;
			if (MsisdnValidator.Code.COUNTRY_CODE.equals(error.code)){
				message = MessageFormat.format(Messages.getString("patient.error.incorrectCellphoneCode"), //$NON-NLS-1$
						PropertiesManager.sms().msisdnPrefix());
			} else {
				message = MessageFormat.format(Messages.getString("patient.error.incorrectCellphone"), //$NON-NLS-1$
						error.message);
			}
			
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.invalidfield.title"), message); //$NON-NLS-1$
			txtCellNo.setFocus();
			return false;
		} else if (txtFirstname.getText().isEmpty()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("patient.error.firstname.blank")); //$NON-NLS-1$
			return false;
		} else if (txtLastname.getText().isEmpty()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("patient.error.surname.blank")); //$NON-NLS-1$
			return false;
		} else if (txtDob.getText().isEmpty()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("patient.error.dob.blank")); //$NON-NLS-1$
			return false;
		} else if (txtSex.getText().isEmpty()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.missingfield.sex")); //$NON-NLS-1$
			return false;
		} else if (txtInitDate.getText().isEmpty()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.missingfield.arvstartdate")); //$NON-NLS-1$
			return false;
		} else if (txtFirstCollection.getText().isEmpty()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.missingfield.collecteddrugs")); //$NON-NLS-1$
			return false;
		} else if (isAdd && !hasConsent()) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.missingfield.consent"));  //$NON-NLS-1$
			chkConsent.setFocus();
			return false;
		} else if (isAdd && cmbLanguage.getSelectionIndex() == 0) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.missingfield.language"));  //$NON-NLS-1$
			cmbLanguage.setFocus();
			return false;
		} else if (isAdd && cmbNetwork.getText().isEmpty()) {
				showMessage(MessageDialog.ERROR, 
						Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
						Messages.getString("addtostudy.error.missingfield.network"));  //$NON-NLS-1$
				cmbNetwork.setFocus();
				return false;
		} else if (isAdd && cmbCampaigns.getSelectionIndex() == 0) {
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.missingfield.campaign")); //$NON-NLS-1$
			cmbCampaigns.setFocus();
			return false;
		}

		if (!isAdd) {
			if (txtEnrolmentDate.getText().isEmpty()) {
				showMessage(MessageDialog.ERROR, 
						Messages.getString("patient.error.missingfield.title"),  //$NON-NLS-1$
						Messages.getString("addtostudy.error.missingfield.enrollmentDate")); //$NON-NLS-1$
				return false;
			}
		}
		
		return true;
	}

	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void createShell() {
		isAdd = (Boolean) GenericFormGui
				.getInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate);
		String shellTxt = isAdd ?  Messages.getString("addtostudy.title.add") //$NON-NLS-1$
				: Messages.getString("addtostudy.title.remove"); //$NON-NLS-1$
		Rectangle bounds = new Rectangle(25, 0, 800, 660);
		buildShell(shellTxt, bounds);
	}

	@Override
	protected void setLogger() {
		super.setLog(log);
	}

	private void createPatientSearch() {
		int col1 = 200;
		int col2 = 330;
		int row1 = 60;

		// Patient ID
		Label lblPatientId = new Label(getShell(), SWT.NONE);
		lblPatientId.setBounds(new Rectangle(col1, row1, 85, 20));
		lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId = new Text(getShell(), SWT.BORDER);
		txtPatientId.setFocus();
		txtPatientId.setBounds(new Rectangle(col2, row1, 150, 20));
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if ((btnSearch != null) && (btnSearch.getEnabled())) {
					if ((e.character == SWT.CR)
							|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
						cmdEnterPressedInPatientID();
					}
				}
			}

		});

		btnSearch = new Button(getShell(), SWT.NONE);
		btnSearch.setBounds(new Rectangle(500, row1, 110, 28));
		btnSearch.setText(Messages.getString("patient.button.search")); //$NON-NLS-1$
		btnSearch.setToolTipText(Messages
				.getString("patient.button.search.tooltip")); //$NON-NLS-1$

		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});
	}

	private void cmdEnterPressedInPatientID() {

		if (txtPatientId.getText().isEmpty()) {
			MessageBox mb = new MessageBox(getShell());
			mb.setText(Messages
					.getString("patient.error.missingPatientId.title")); //$NON-NLS-1$
			mb.setMessage(Messages.getString("patient.error.missingPatientId")); //$NON-NLS-1$
			mb.open();
			txtPatientId.setFocus();
			return;
		}

		String patientId = PatientBarcodeParser.getPatientId(txtPatientId.getText());

		String illegalText = iDARTUtil.checkPatientId(txtPatientId.getText());
		if (illegalText != null) {
			showMessage(MessageDialog.ERROR, MessageFormat.format(Messages.getString("patient.error.badCharacterInPatientId.title"), //$NON-NLS-1$
					illegalText),
					MessageFormat.format(Messages.getString("patient.error.badCharacterInPatientId"), //$NON-NLS-1$
					iDartProperties.illegalPatientIdChars));
			txtPatientId.setText(EMPTY);
			txtPatientId.setFocus();
			return;
		} else if (patientId != null) {
			txtPatientId.setText(patientId);
			txtPatientId.setText(txtPatientId.getText().toUpperCase());
			displayPatientSearchDialog();
		}
	}

	private void displayPatientSearchDialog() {
		PatientSearch search = new PatientSearch(getShell(), getHSession());
		PatientIdentifier identifier = search.search(txtPatientId.getText());
		
		if (identifier != null) {
			localPatient = identifier.getPatient();
			if (isAdd) {
				if (!checkIfPatientOnAnyCampaign(localPatient)) {
					updateGUIforNewLocalPatient();
				} else {
					showMessage(MessageDialog.INFORMATION, 
							Messages.getString("addtostudy.error.patient-already-on-study.title"), //$NON-NLS-1$
							Messages.getString("addtostudy.error.patient-already-on-study.msg")); //$NON-NLS-1$
					txtPatientId.setFocus();
				}
			} else {
				if (checkIfPatientOnAnyCampaign(localPatient)) {
					updateGUIforNewLocalPatient();
				} else {
					showMessage(MessageDialog.INFORMATION, 
							Messages.getString("addtostudy.error.patient-not-on-study.title"),   //$NON-NLS-1$
							Messages.getString("addtostudy.error.patient-not-on-study")); //$NON-NLS-1$
					txtCellNo.setFocus();
				}
			}
		} else if (!btnSearch.isDisposed()) {
			btnSearch.setEnabled(true);
		}
	}

	private void cmdSearchWidgetSelected() {
		displayPatientSearchDialog();
	}

	private void updateGUIforNewLocalPatient() {
		btnSave.setEnabled(true);
		
		txtPatientId.setText(localPatient.getPatientId());
		txtFirstname.setText(localPatient.getFirstNames());
		txtLastname.setText(localPatient.getLastname());
		txtDob.setText(MessageFormat.format(Messages.getString("addtostudy.txt.dob"), iDARTUtil.format(localPatient.getDateOfBirth()), localPatient.getAge())); //$NON-NLS-1$
		if (localPatient.isFemale()) {
			txtSex.setText(Messages.getString("patient.sex.female"));//$NON-NLS-1$
		} else if (localPatient.isMale()) {
			txtSex.setText(Messages.getString("patient.sex.male"));//$NON-NLS-1$
		} else {
			txtSex.setText(Messages.getString("common.unknown"));//$NON-NLS-1$
		}
		txtInitDate.setText(iDARTUtil.format(getInitiationDate(localPatient)));
		txtFirstCollection.setText(getFirstDrugCollection(localPatient));
		txtCellNo.setText(localPatient.getCellphone());
		txtCellNo.setFocus();
		originalCellNo = localPatient.getCellphone();
		
		if (!isAdd) {
			txtCellNo.setEnabled(false);
			Date enrolmentDate = StudyManager.getStudyEnrolmentDate(
					getHSession(), localPatient.getId());
			if (enrolmentDate != null) {
				txtEnrolmentDate.setText(iDARTUtil.format(enrolmentDate));
			} else {
				txtEnrolmentDate.setText(EMPTY);
			}
		}
	}

	private String getFirstDrugCollection(Patient pat) {
		Packages packages = PackageManager
				.getFirstPackageWithARVs(PackageManager
						.getAllPackagesForPatient(getHSession(), pat));
		
		if (packages != null) {
			StringBuilder drugsString = new StringBuilder();
			drugsString.append(DrugManager.getDrugListString(packages.getARVDrugSet(), ", ", false)); //$NON-NLS-1$
			SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a"); //$NON-NLS-1$
			SimpleDateFormat sdfDayName = new SimpleDateFormat("EEE, dd MMM yyyy"); //$NON-NLS-1$
			drugsString.append(Messages.getString("addtostudy.collected-at")); //$NON-NLS-1$
			drugsString.append(sdfTime.format(packages.getDateLeft()));
			drugsString.append(Messages.getString("addtostudy.on")); //$NON-NLS-1$
			drugsString.append(sdfDayName.format(packages.getDateLeft()));
			return drugsString.toString();
		} else {
			return EMPTY;
		}
	}

	private Date getInitiationDate(Patient pat) {
		PatientAttribute startDate = pat.getAttributeByName(PatientAttribute.ARV_START_DATE);
		return (Date) (startDate == null ? null : startDate.getObjectValue());
	}

	private void createPatientDetailLabels() {
		int row = 95;
		int col1 = 200;

		// Labels - firstname
		Label lblFirstname = new Label(getShell(), SWT.NONE);
		lblFirstname.setBounds(new Rectangle(col1, row, 120, 20));
		lblFirstname.setText(Messages.getString("patient.label.firstname")); //$NON-NLS-1$
		lblFirstname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;

		// Labels - surname
		Label lblSurname = new Label(getShell(), SWT.NONE);
		lblSurname.setBounds(new Rectangle(col1, row, 120, 20));
		lblSurname.setText(Messages.getString("patient.label.surname")); //$NON-NLS-1$
		lblSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;

		// Labels - dob
		Label lblDob = new Label(getShell(), SWT.NONE);
		lblDob.setBounds(new Rectangle(col1, row, 120, 20));
		lblDob.setText(Messages.getString("patient.label.dob").substring(0, //$NON-NLS-1$
				Messages.getString("patient.label.dob").length() - 1) //$NON-NLS-1$
				+ " &&" + Messages.getString("patient.label.age")); //$NON-NLS-1$ //$NON-NLS-2$ 
		lblDob.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;

		// Labels - sex
		Label lblSex = new Label(getShell(), SWT.NONE);
		lblSex.setBounds(new Rectangle(col1, row, 120, 20));
		lblSex.setText(Messages.getString("patient.label.sex")); //$NON-NLS-1$
		lblSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;

		// Labels - arv init date
		Label lblInitDate = new Label(getShell(), SWT.NONE);
		lblInitDate.setBounds(new Rectangle(col1, row, 120, 20));
		lblInitDate.setText("ARV Initiation Date:"); //$NON-NLS-1$
		lblInitDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;

		// Labels - first drug collection
		Label lblFirstCollection = new Label(getShell(), SWT.NONE);
		lblFirstCollection.setBounds(new Rectangle(col1, row, 130, 20));
		lblFirstCollection.setText("First Drug Collection:"); //$NON-NLS-1$
		lblFirstCollection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;

		// first separator
		Label lblHSeperator = new Label(getShell(), SWT.SEPARATOR
				| SWT.SHADOW_OUT | SWT.HORIZONTAL);
		lblHSeperator.setBounds(col1, row, 400, 20);
		row += 20;

		Label lblInstructions = new Label(getShell(), SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(col1, row, 300, 25));
		lblInstructions.setText(Messages.getString("common.label.compulsory")); //$NON-NLS-1$
		lblInstructions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10_ITALIC));
		row += 30;

		// label - cellphone number
		Label lblCellNo = new Label(getShell(), SWT.NONE);
		lblCellNo.setBounds(new Rectangle(col1, row, 130, 20));
		if (isAdd) {
			lblCellNo.setText(Messages.getString("common.compulsory.marker") //$NON-NLS-1$
					+ Messages.getString("patient.label.cellphone")); //$NON-NLS-1$ 
		} else {
			lblCellNo.setText(Messages.getString("patient.label.cellphone")); //$NON-NLS-1$ 
		}
		lblCellNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;
		if (isAdd) {
			
			// label - mobile network consent
			Label lblNetwork = new Label(getShell(), SWT.NONE);
			lblNetwork.setBounds(new Rectangle(col1, row, 130, 20));
			lblNetwork
					.setText(Messages.getString("common.compulsory.marker") + Messages.getString("patient.label.network")); //$NON-NLS-1$ //$NON-NLS-2$
			lblNetwork.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			row += 30;
			
			// label - alternate cell number
			Label lblAltCell = new Label(getShell(), SWT.NONE);
			lblAltCell.setBounds(new Rectangle(col1, row, 130, 20));
			lblAltCell.setText(Messages.getString("patient.label.cellphone.alternate")); //$NON-NLS-1$ //$NON-NLS-2$
			lblAltCell.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			row += 30;
			
			// label - informed consent
			Label lblConsent = new Label(getShell(), SWT.NONE);
			lblConsent.setBounds(new Rectangle(col1, row, 130, 20));
			lblConsent
					.setText(Messages.getString("common.compulsory.marker") + Messages.getString("patient.label.consent")); //$NON-NLS-1$ //$NON-NLS-2$
			lblConsent.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			row += 30;
			
			// label - language
			Label lblLanguage = new Label(getShell(), SWT.NONE);
			lblLanguage.setBounds(new Rectangle(col1, row, 130, 20));
			lblLanguage
					.setText(Messages.getString("common.compulsory.marker") + Messages.getString("patient.label.language")); //$NON-NLS-1$ //$NON-NLS-2$
			lblLanguage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			row += 30;

			// label - available campaigns
			Label lblAvailabelCampaigns = new Label(getShell(), SWT.NONE);
			lblAvailabelCampaigns.setBounds(new Rectangle(col1, row, 130, 20));
			lblAvailabelCampaigns
					.setText(Messages.getString("common.compulsory.marker") + Messages.getString("campaigns.label.available.campaigns")); //$NON-NLS-1$ //$NON-NLS-2$
			lblAvailabelCampaigns.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			row += 30;

			// link - refresh campaigns
			Link link = new Link(getShell(), SWT.NONE);
			link.setBounds(new Rectangle(col1 + 130, row, 230, 30));
			link
					.setText("<A>" //$NON-NLS-1$
							+ Messages
									.getString("campaigns.label.refresh.campaigns.list") //$NON-NLS-1$
							+ "</A>"); //$NON-NLS-1$
			link.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event event) {
					performCampaignsUpdate();
				}
			});
			row += 30;
			
			// label - available campaigns
			Label lblCustomMsgTime = new Label(getShell(), SWT.NONE);
			lblCustomMsgTime.setBounds(new Rectangle(col1, row, 130, 20));
			lblCustomMsgTime
					.setText(Messages.getString("common.compulsory.marker") + Messages.getString("campaigns.label.customMsgTime")); //$NON-NLS-1$ //$NON-NLS-2$
			lblCustomMsgTime.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			row += 30;
		} else {
			// label - available campaigns
			Label lblEnrollmentDate = new Label(getShell(), SWT.NONE);
			lblEnrollmentDate.setBounds(new Rectangle(col1, row, 130, 20));
			lblEnrollmentDate.setText(Messages
					.getString("campaigns.label.enrolement.date")); //$NON-NLS-1$
			lblEnrollmentDate.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			row += 30;
		}

		lblNote = new Label(getShell(), SWT.CENTER);
		lblNote.setBounds(new Rectangle(col1, row, 400, 25));
		lblNote
				.setText(isAdd ? Messages.getString("addtostudy.msg.questionnaire") //$NON-NLS-1$
						: Messages.getString("addtostudy.msg.interview")); //$NON-NLS-1$
		lblNote.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblNote.setForeground(ResourceUtils.getColor(iDartColor.RED));
		row += 40;

		// second separator
		Label lblSecondHSeperator = new Label(getShell(), SWT.SEPARATOR
				| SWT.SHADOW_OUT | SWT.HORIZONTAL);
		lblSecondHSeperator.setBounds(col1, row, 400, 20);

	}

	/**
	 * Update the campaigns drop down
	 */
	protected void performCampaignsUpdate(){
		updateCampaignsFromMobilisr(new RefreshJob());
		populateCampaigns();
	}

	private void createPatientDetailTextBoxes() {
		int row = 95;
		int col2 = 330;

		txtFirstname = new Text(getShell(), SWT.BORDER);
		txtFirstname.setBounds(new Rectangle(col2, row, 150, 20));
		txtFirstname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtFirstname.setEnabled(false);
		row += 30;

		txtLastname = new Text(getShell(), SWT.BORDER);
		txtLastname.setBounds(new Rectangle(col2, row, 150, 20));
		txtLastname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtLastname.setEnabled(false);
		row += 30;

		txtDob = new Text(getShell(), SWT.BORDER);
		txtDob.setBounds(new Rectangle(col2, row, 150, 20));
		txtDob.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDob.setEnabled(false);
		row += 30;

		txtSex = new Text(getShell(), SWT.BORDER);
		txtSex.setBounds(new Rectangle(col2, row, 150, 20));
		txtSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtSex.setEnabled(false);
		row += 30;

		txtInitDate = new Text(getShell(), SWT.BORDER);
		txtInitDate.setBounds(new Rectangle(col2, row, 150, 20));
		txtInitDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtInitDate.setEnabled(false);
		row += 30;

		txtFirstCollection = new Text(getShell(), SWT.MULTI | SWT.V_SCROLL
				| SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
		txtFirstCollection.setBounds(new Rectangle(col2, row, 350, 25));
		txtFirstCollection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 80;

		txtCellNo = new Text(getShell(), SWT.BORDER);
		txtCellNo.setBounds(new Rectangle(col2, row, 150, 20));
		txtCellNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		row += 30;
		if (isAdd) {
			cmbNetwork = new Combo(getShell(), SWT.NONE);
			cmbNetwork.setBounds(new Rectangle(col2, row, 150, 20));
			populateNetworks();
			row += 30;
			
			txtAltCellNo = new Text(getShell(), SWT.BORDER);
			txtAltCellNo.setBounds(new Rectangle(col2, row, 150, 20));
			txtAltCellNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			row += 30;
			
			chkConsent = new Button(getShell(), SWT.CHECK);
			chkConsent.setBounds(new Rectangle(col2, row, 20, 20));
			chkConsent.setSelection(false);
			row += 30;
			
			cmbLanguage = new Combo(getShell(), SWT.CHECK);
			cmbLanguage.setBounds(new Rectangle(col2, row, 150, 20));
			populateLanguages();
			row += 30;

			cmbCampaigns = new Combo(getShell(), SWT.NONE);
			cmbCampaigns.setBounds(new Rectangle(col2, row, 150, 20));
			populateCampaigns();
			row += 60;
			
			cmbCustomMsgTime = new Combo(getShell(), SWT.NONE);
			cmbCustomMsgTime.setBounds(new Rectangle(col2, row, 150, 20));
			populateMsgTimes();
			row += 30;
		} else {
			txtCellNo.setEnabled(false);
			txtEnrolmentDate = new Text(getShell(), SWT.BORDER);
			txtEnrolmentDate.setBounds(col2, row, 150, 20);
			txtEnrolmentDate.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			txtEnrolmentDate.setEnabled(false);
		}

	}

	private void populateNetworks() {
		List<String> networks = PropertiesManager.sms().networks();
		for (String network : networks) {
			cmbNetwork.add(network);
		}
	}
	
	private void populateLanguages() {
		List<String> languages = PropertiesManager.sms().languages();
		cmbLanguage.clearSelection();
		cmbLanguage.removeAll();
		if (languages != null && !languages.isEmpty()) {
			cmbLanguage.add(Messages.getString("addtostudy.please-select"), 0); //$NON-NLS-1$
			for (String lang : languages) {
				cmbLanguage.add(lang);
			}
			cmbLanguage.select(0);
		}
	}

	private void populateCampaigns() {
		List<Campaign> campaigns = CampaignManager.getCampaigns(getHSession());
		cmbCampaigns.clearSelection();
		cmbCampaigns.removeAll();
		if (campaigns != null && !campaigns.isEmpty()) {
			cmbCampaigns.add(Messages.getString("addtostudy.please-select"), 0); //$NON-NLS-1$
			for (Campaign campaign : campaigns) {
				cmbCampaigns.add(campaign.getName());
			}
			cmbCampaigns.select(0);
		} else {
			cmbCampaigns.add(Messages.getString("addtostudy.please-refresh"), 0); //$NON-NLS-1$
			cmbCampaigns.select(0);
		}
	}
	
	private void populateMsgTimes(){
		cmbCustomMsgTime.clearSelection();
		cmbCustomMsgTime.removeAll();
		Calendar cal = Calendar.getInstance();
		cal.setTime(iDARTUtil.zeroTimeStamp(new Date()));
		cal.add(Calendar.MINUTE, 60);
		for (int i = 0; i < 44; i++) {
			cal.add(Calendar.MINUTE, 30);
			cmbCustomMsgTime.add(timeFormat.format(cal.getTime()));
		}
		
		cmbCustomMsgTime.setText(PropertiesManager.sms().defaultCustomMsgTime());
	}

	private boolean hasConsent() {
		return chkConsent.getSelection();
	}

	private void setChangeListeners() {
		if (isAdd) {
			cmbCampaigns.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					int index = cmbCampaigns.getSelectionIndex();
					if(index != 0){
						selectedCampaignName = cmbCampaigns.getItem(index);
					}
				}
			});
		}
	}

	/**
	 * This method does not delete the studyparticipant record. It only set's
	 * the end date to today.
	 * @return 
	 */
	private boolean removeStudyParticipant() {
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();
			StudyParticipant participant = StudyManager.getActiveStudyParticipant(getHSession(), localPatient.getId());

			Long campaignId = PropertiesManager.sms().controlcampaignid();
			if (participant.isInStudy()){
				campaignId = getRegisteredMobilisrCampaignId(localPatient);
			}
			boolean success = true;
			if (campaignId > 0){
				success = sendMobilisrRequest(null, null, localPatient.getCellphone(), campaignId, null);
			}
			
			if (success){
				if (campaignId > 0){
					CampaignManager.removeCampaignParticipant(getHSession(), localPatient.getId());
				}
				StudyManager.removeStudyParticipant(getHSession(), localPatient.getId());
				
				getHSession().flush();
				tx.commit();
				
				showMessage(MessageDialog.INFORMATION,
						Messages.getString("addtostudy.success.title"),  //$NON-NLS-1$
						MessageFormat.format(
								Messages.getString("addtostudy.remove.success.message"),//$NON-NLS-1$
								localPatient.getPatientId()));//$NON-NLS-1$ //$NON-NLS-2$
			}
			
			return success;
		} catch (HibernateException e) {
			getLog().error("Error Removing Participant to Study.", e); //$NON-NLS-1$
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.save.failed.title"),  //$NON-NLS-1$
					Messages.getString("patient.error.save.failed"));//$NON-NLS-1$
			return false;
		}
	}

	private boolean addStudyParticipant() {
		//Check if the participant id has changed
		updateCellNo();
		
		String customTimeText = cmbCustomMsgTime.getText();
		Date customTime = null;
		try {
			customTime = timeFormat.parse(customTimeText);
		} catch (ParseException e) {
			log.error("Error parsing custom message time: " + customTimeText, e); //$NON-NLS-1$
			try {
				customTime = timeFormat.parse(PropertiesManager.sms().defaultCustomMsgTime());
			} catch (ParseException e1) {
				log.error("Error parsing default message time: " + customTimeText, e); //$NON-NLS-1$
			}
		}
		
		//Add user to the study
		if (studyParticipant == null){
			studyParticipant = new StudyParticipant(localPatient,
					AdministrationManager.getCidaStudy(getHSession()),
					null, new Date(), null); //$NON-NLS-1$
		}
		studyParticipant.setLanguage(cmbLanguage.getText().trim());
		studyParticipant.setAlternateCellphone(txtAltCellNo.getText().trim());
		studyParticipant.setNetwork(cmbNetwork.getText());
		
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();

			if (studyParticipant.getStudyGroup() == null)
				StudyManager.randomiseStudyParticipant(studyParticipant);
			
			Long campaignId = PropertiesManager.sms().controlcampaignid();
			boolean success = true;
			if(studyParticipant.isInStudy()){
				campaignId = getSelectedCampaignId(selectedCampaignName);
			}
			
			if (campaignId > 0){
				success = sendMobilisrRequest(LocalObjects.pharmacy.getPharmacyName(), String.valueOf(localPatient.getId()), 
						localPatient.getCellphone(), campaignId, customTime);
			}
			
			if (success){
				StudyManager.commitRandomization(studyParticipant);
				
				if (campaignId > 0){
					addCampaignParticipant(campaignId);
				}
				getHSession().save(studyParticipant);
				getHSession().flush();
				tx.commit();
				
				showMessage(MessageDialog.INFORMATION,
						Messages.getString("addtostudy.success.title"),  //$NON-NLS-1$
						MessageFormat.format(
								Messages.getString("addtostudy.add.success.message"),//$NON-NLS-1$
								localPatient.getPatientId()));//$NON-NLS-1$ //$NON-NLS-2$
			}

			return success;
		} catch (HibernateException he) {
			getLog().error("Error adding Participant to Study.", he); //$NON-NLS-1$
			showMessage(MessageDialog.ERROR, 
					Messages.getString("patient.error.save.failed.title"),  //$NON-NLS-1$
					Messages.getString("patient.error.save.failed"));//$NON-NLS-1$
			
			return false;
		}
	}
	
	private void addCampaignParticipant(Long campaignId) {
		Campaign campaign = CampaignManager.getCampaignByMobilisrId(getHSession(), campaignId);
		if (campaign != null){
			CampaignParticipant participant = new CampaignParticipant(campaign, localPatient);
			CampaignManager.addCampaignParticipant(getHSession(), participant);
		}
	}
	

	private boolean sendMobilisrRequest(String firstname, String lastname, String cellNo, Long campaignId, Date msgTime) {

		if (campaignId == null){
			String message = "Error " + (isAdd ? "adding" : "removing") + " patient for campaign. Null campaign id"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			log.error(message);
			showMessage(MessageDialog.ERROR, 
					Messages.getString("addtostudy.error.add.title"),  //$NON-NLS-1$
					message);
			return false;
		}
		
		if (isAdd) {
			try {
				MobilisrManager.addPatientToCampaign(firstname, lastname, cellNo, campaignId, msgTime);
				return true;
			} catch (RestCommandException e) {
				String exMessage = getMessageFromException(e);
				log.error("Error adding patient to campaign: " + campaignId, e); //$NON-NLS-1$
				String message = MessageFormat.format(Messages.getString("addtostudy.alert.add-to-campaign.message"),  //$NON-NLS-1$
						firstname, lastname, cellNo, campaignId, exMessage);
				AlertManager.createAlert(Messages.getString("addtostudy.alert.add-to-campaign.type"), //$NON-NLS-1$
						message, getHSession());
				showMessage(MessageDialog.ERROR, 
						Messages.getString("addtostudy.error.remove.title"),  //$NON-NLS-1$
						MessageFormat.format(Messages.getString("addtostudy.error.add.message"),  //$NON-NLS-1$
								localPatient.getPatientId(), exMessage));
			}
		} else {
			try {
				MobilisrManager.removePatientFromCampaign(campaignId, cellNo);
				return true;
			} catch (RestCommandException e) {
				String exMessage = getMessageFromException(e);
				log.error("Error removing patient from campaign: " + campaignId, e); //$NON-NLS-1$
				String message = MessageFormat.format(Messages.getString("addtostudy.alert.remove-from-campaign.message"),  //$NON-NLS-1$
						cellNo, campaignId, exMessage);
				AlertManager.createAlert(Messages.getString("addtostudy.alert.remove-from-campaign.type"), //$NON-NLS-1$
						message, getHSession());
				showMessage(MessageDialog.ERROR, 
						Messages.getString("addtostudy.error.remove.title"),  //$NON-NLS-1$
						MessageFormat.format(Messages.getString("addtostudy.error.remove.message"),  //$NON-NLS-1$
								localPatient.getPatientId(),exMessage));
			}
		}
		return false;
	}

	private Long getRegisteredMobilisrCampaignId(Patient patient) {
		return CampaignManager.getPatientRegisteredMobilisrCampaignId(getHSession(), patient.getId());
	}

	private Long getSelectedCampaignId(String campaignName) {
		if(campaignName!= null && !campaignName.trim().isEmpty()){
			Campaign campaign = CampaignManager.getCampaignByName(getHSession(), campaignName);
			if(campaign != null){
				return campaign.getMobilisrId();
			}
		}
		return null;
	}

	private void updateCellNo() {
		if (!originalCellNo.equalsIgnoreCase(txtCellNo.getText())) {
			
			String msisdn = txtCellNo.getText().trim();
			if(originalCellNo.isEmpty()){
				performUpdateCellNo(msisdn);
			} else {
				// notify user that cell no has changed
				boolean result = MessageDialog.openQuestion(getShell(), 
						Messages.getString("addtostudy.info.new-mobile-number.title"),  //$NON-NLS-1$
						MessageFormat.format(Messages.getString("addtostudy.msg.mobile-number-changed"),  //$NON-NLS-1$
								originalCellNo, msisdn)); 
				if (result) {
					// update the patient's cell no. Write change to DB
					performUpdateCellNo(msisdn);
					if(StudyManager.patientEverOnStudy(getHSession(), localPatient.getId()) ){
						try {
							MobilisrManager.updateMobilisrCellNo(originalCellNo, LocalObjects.pharmacy.getPharmacyName(), 
									String.valueOf(localPatient.getId()), msisdn);
						} catch (RestCommandException e) {
							getLog().error("Error updating Participant mobile number.", e); //$NON-NLS-1$
							showMessage(MessageDialog.ERROR, 
									Messages.getString("addtostudy.error.updating-patient-details.title"),  //$NON-NLS-1$
									Messages.getString("addtostudy.error.updating-patient-mobile.msg")); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	private void performUpdateCellNo(String msisdn) {
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();
			PatientManager.saveCellphoneNumber(getHSession(), msisdn, localPatient.getId());
			localPatient.setCellphone(msisdn);
			getHSession().flush();
			tx.commit();
		} catch (HibernateException he) {
			log.error("Error upding patient mobilie number",he); //$NON-NLS-1$
			showMessage(MessageDialog.ERROR, 
					Messages.getString("addtostudy.error.updating-patient-details.title"),  //$NON-NLS-1$
					Messages.getString("addtostudy.error.updating-patient-mobile.msg")); //$NON-NLS-1$
		}
	}
	
	private boolean checkIfPatientOnAnyCampaign(Patient patient) {
		try {
			return StudyManager
					.isPatientonStudy(getHSession(), patient.getId());
		} catch (HibernateException he) {
			log.error("Error checking if patient is on study",he); //$NON-NLS-1$
			showMessage(MessageDialog.ERROR,  Messages.getString("common.error"),  he.getMessage()); //$NON-NLS-1$
		}
		return false;
	}

	protected void updateCampaignsFromMobilisr(IRunnableWithProgress job) {
		try {
			new ProgressMonitorDialog(getShell()).run(true, true, job);
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			String message = getMessageFromException(targetException);
			showMessage(MessageDialog.ERROR,  Messages.getString("api.error"),  message); //$NON-NLS-1$
		} catch (InterruptedException e) {
			showMessage(MessageDialog.ERROR,  Messages.getString("common.cancelled"),  e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * @param targetException
	 * @return
	 */
	private String getMessageFromException(Throwable targetException) {
		String message = targetException.getMessage();
		if (targetException instanceof RestCommandException){
			RestCommandException re = (RestCommandException) targetException;
			Status status = re.getStatus();
			String url = re.getRequestUrl();
			if (status != null){
				int statusCode = status.getStatusCode();
				switch (statusCode) {
				case 404:
					message = "Server URL not found. Please check the settings.\n\n" + url;
					break;
				case 401:
					message = "Authentication with server failed. Please check the settings.";
					break;
				}
			}
		}
		return message;
	}
	
	class RefreshJob extends AbstractCancellableJob {

		protected Exception error;
		
		public RefreshJob() {
			super("Campaign refresh"); //$NON-NLS-1$
		}
		
		@Override
		public void performJob(IProgressMonitor monitor) throws Exception {
			monitor.worked(20);
			monitor.setTaskName(Messages.getString("addtostudy.task.campaign-refresh")); //$NON-NLS-1$
			
			Transaction tx = null;
			tx = getHSession().beginTransaction();
			List<Campaign> campaigns = MobilisrManager.getCampaigns();
			CampaignManager.updateCampaigns(getHSession(), campaigns);
			monitor.worked(45);
			getHSession().flush();
			tx.commit();
		}
	}
}
