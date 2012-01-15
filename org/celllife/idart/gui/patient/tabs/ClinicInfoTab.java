/**
 *
 */
package org.celllife.idart.gui.patient.tabs;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.Pregnancy;
import org.celllife.idart.gui.misc.GenericTab;
import org.celllife.idart.gui.patient.ShowPAVAS;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.hibernate.Session;

/**
 */
public class ClinicInfoTab extends GenericTab implements IPatientTab {

	protected DateButton btnMenstrualOccurance;
	private Session hSession;
	private final Logger log = Logger.getLogger(this.getClass());
	private TabFolder parent;
	// default to true since sex combo on AddPatient defaults to female
	private boolean patientIsFemale = true;
	private Button rdBtnNotPregnant;
	private Button rdBtnPregnant;
	private int style;
	private Label lblPicPatientVisitsandStats;
	private Patient localPatient;

	/**
	 * Used to add a new pregnancy (if there is no current pregnancy) or
	 * otherwise update the confirm date of the current pregnancy.
	 * 
	 * Only called by setLocalPatient() when rdBtnPregnant is selected.
	 * 
	 * @throws ParseException
	 */
	private void addOrUpdatePregnancy(Patient patient) {
		// Check if local patient has a current pregnancy.
		Pregnancy currentPregnancy = PatientManager.getCurrentPregnancy(
				hSession, patient.getId());
		// we are adding a new pregnancy
		if (currentPregnancy == null) {
			Pregnancy preg = new Pregnancy();
			preg.setPatient(patient);
			preg.setConfirmDate(btnMenstrualOccurance.getDate());
			preg.setModified('T');
			patient.getPregnancies().add(preg);
		} else {
			// we are updating an existing pregnancy
			if (patient.getPregnancies().contains(currentPregnancy)) {
				for (Pregnancy pregForUpdate : patient.getPregnancies()) {
					if (pregForUpdate.getId() == currentPregnancy.getId()) {
						pregForUpdate.setPatient(patient);
						pregForUpdate.setConfirmDate(btnMenstrualOccurance
								.getDate());
						pregForUpdate.setModified('T');
						break; // pregnancy found. No further iteration.
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.patient.util.IPatientTab#changesMade(org.celllife
	 * .idart.database.hibernate.Patient)
	 */
	@Override
	public boolean changesMade(Patient patient) {
		boolean isPregnant;
		boolean changesMade = false;

		if (rdBtnPregnant.getSelection()) {
			isPregnant = true;
		} else {
			isPregnant = false;
		}

		// pregnancy tests
		Date theNewPregnancyDate = btnMenstrualOccurance.getDate();
		Pregnancy currentPregnancy = PatientManager.getCurrentPregnancy(
				hSession, patient.getId());
		if (((currentPregnancy == null) && (isPregnant))
				|| ((currentPregnancy != null) && (!isPregnant))) {
			changesMade = true;
		} else if (currentPregnancy != null) {
			Date currentConfirmDate = currentPregnancy.getConfirmDate();
			if (currentConfirmDate != null && theNewPregnancyDate != null) {
				if (!(currentConfirmDate.compareTo(theNewPregnancyDate) != 0)) {
					changesMade = true;
				}
			} else if (theNewPregnancyDate != null) {
				changesMade = true;
			}
		}

		return changesMade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#clear()
	 */
	@Override
	public void clear() {
		rdBtnPregnant.setSelection(false);
		rdBtnNotPregnant.setSelection(true);
		btnMenstrualOccurance.clearDate();
		btnMenstrualOccurance.setText("Not applicable");
	}

	@Override
	public void create() {
		this.tabItem = new TabItem(parent, style);
		tabItem.setText("  Clinical Info  ");
		createGrpClinicalInfo();
	}

	/**
	 * This method initializes grpPregnancy
	 */
	private void createGrpClinicalInfo() {

		Group grpPregnancy = new Group(tabItem.getParent(), SWT.NONE);
		grpPregnancy.setBounds(new Rectangle(3, 3, 750, 140));
		grpPregnancy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tabItem.setControl(grpPregnancy);

		// Pregnant?
		Label lblPregnant = new Label(grpPregnancy, SWT.NONE);
		lblPregnant.setBounds(new Rectangle(11, 25, 166, 20));
		lblPregnant.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPregnant.setText("  Patient Currently Pregnant?");

		// compAccStatus
		Composite compPregnancy = new Composite(grpPregnancy, SWT.NONE);
		compPregnancy.setBounds(new Rectangle(181, 21, 150, 20));

		rdBtnPregnant = new Button(compPregnancy, SWT.RADIO);
		rdBtnPregnant.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0,
				60, 20));
		rdBtnPregnant.setText("Yes");
		rdBtnPregnant.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnPregnant.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				btnMenstrualOccurance.setEnabled(true);
				if (btnMenstrualOccurance.getDate() == null) {
					btnMenstrualOccurance.setText("Unknown");
				}
			}
		});
		rdBtnPregnant.setSelection(false);

		rdBtnNotPregnant = new Button(compPregnancy, SWT.RADIO);
		rdBtnNotPregnant.setBounds(new Rectangle(61, 0, 70, 20));
		rdBtnNotPregnant.setText("No");
		rdBtnNotPregnant.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnNotPregnant.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnMenstrualOccurance.setEnabled(false);
				btnMenstrualOccurance.setText("Not applicable");
			}
		});
		rdBtnNotPregnant.setSelection(true);

		// Date Confirmed
		Label lblConfirmDate = new Label(grpPregnancy, SWT.NONE);
		lblConfirmDate.setBounds(new Rectangle(11, 58, 144, 20));
		lblConfirmDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblConfirmDate.setText("  Last Menstrual Period:");

		btnMenstrualOccurance = new DateButton(grpPregnancy,
				DateButton.ZERO_TIMESTAMP,
				null);
		btnMenstrualOccurance.setBounds(new Rectangle(160, 51, 200, 25));
		btnMenstrualOccurance.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		btnMenstrualOccurance.setText(" Not applicable ");
		btnMenstrualOccurance.setEnabled(false);

		lblPicPatientVisitsandStats = new Label(grpPregnancy, SWT.NONE);
		lblPicPatientVisitsandStats.setBounds(new Rectangle(15, 90, 50, 43));
		lblPicPatientVisitsandStats.setImage(ResourceUtils
				.getImage(iDartImage.PAVAS));
		lblPicPatientVisitsandStats
		.setToolTipText("Press this Button to enter the Patient Visits and Statistics Module.");
		lblPicPatientVisitsandStats.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdViewPAVASWidgetSelected();
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#enable(boolean,
	 * org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void enable(boolean enable, Color color) {
		// only enable if the patient is female
		enable &= isPatientFemale();
		rdBtnPregnant.setEnabled(enable);
		rdBtnNotPregnant.setEnabled(enable);
		if (isPatientFemale()) {
			if (rdBtnPregnant.getSelection()) {
				btnMenstrualOccurance.setEnabled(true);
			}
		} else {
			btnMenstrualOccurance.setEnabled(false);
			rdBtnPregnant.setEnabled(false);
			rdBtnPregnant.setSelection(false);
			rdBtnNotPregnant.setEnabled(false);
			rdBtnNotPregnant.setSelection(true);
		}
	}

	/**
	 * @return the patientIsFemale
	 */
	public boolean isPatientFemale() {
		return patientIsFemale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.patient.util.IPatientTab#populate(org.hibernate
	 * .Session, org.celllife.idart.database.hibernate.Patient, boolean)
	 */
	/**
	 * Method loadPatientDetails.
	 * 
	 * @param sess
	 *            Session
	 * @param patient
	 *            Patient
	 * @param isPatientActive
	 *            boolean
	 * @see org.celllife.idart.gui.patient.tabs.IPatientTab#loadPatientDetails(Session,
	 *      Patient, boolean)
	 */
	@Override
	public void loadPatientDetails(Patient patient, boolean isPatientActive) {
		if (PatientManager.getCurrentPregnancy(hSession, patient.getId()) != null) {
			Calendar theConfirmDate = Calendar.getInstance();
			theConfirmDate.setTime(PatientManager.getCurrentPregnancy(hSession,
					patient.getId()).getConfirmDate());
			rdBtnPregnant.setSelection(true);
			rdBtnNotPregnant.setSelection(false);
			btnMenstrualOccurance.setDate(theConfirmDate.getTime());
		} else {
			rdBtnNotPregnant.setSelection(true);
			rdBtnPregnant.setSelection(false);
			btnMenstrualOccurance.setText("Not applicable");
		}

		Character sex = new Character(patient.getSex());
		setPatientIsFemale(Character.toUpperCase(sex) == 'F');
		localPatient = patient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.patient.util.IPatientTab#setPatientDetails(org
	 * .celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public void setPatientDetails(Patient patient) {
		// nothing to set
	}

	/**
	 * Method setCurrentPregnancyEnded.
	 * 
	 * @param patient
	 *            Patient
	 */
	private void setCurrentPregnancyEnded(Patient patient) {
		Pregnancy currPregnancy = PatientManager.getCurrentPregnancy(hSession,
				patient.getId());
		if (currPregnancy != null) {
			for (Pregnancy p : patient.getPregnancies()) {
				if (p.getId() == currPregnancy.getId()) {
					p.setEndDate(new Date());
					break; // No more iteration when pregnancy found.
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.misc.IGenericTab#setParent(org.eclipse.swt.widgets
	 * .TabFolder)
	 */
	@Override
	public void setParent(TabFolder parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.misc.IGenericTab#setSession(org.hibernate.Session)
	 */
	@Override
	public void setSession(Session session) {
		this.hSession = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#setStyle(int)
	 */
	@Override
	public void setStyle(int SWTStyle) {
		this.style = SWTStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.patient.util.IPatientTab#submit(org.hibernate.
	 * Session, org.celllife.idart.database.hibernate.Patient)
	 */
	/**
	 * Method submit.
	 * 
	 * @param patient
	 *            Patient
	 * @see org.celllife.idart.gui.patient.tabs.IPatientTab#submit(Patient)
	 */
	@Override
	public void submit(Patient patient) {
		if (rdBtnPregnant.getSelection()) {
			addOrUpdatePregnancy(patient);
		} else {
			setCurrentPregnancyEnded(patient);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.IPatientTab#validateFields()
	 */
	/**
	 * Method validateFields.
	 * 
	 * @param patient
	 *            Patient
	 * @return Map<String,String>
	 * @see org.celllife.idart.gui.patient.tabs.IPatientTab#validateFields(Patient)
	 */
	@Override
	public Map<String, String> validateFields(Patient patient) {
		String title = "";
		String message = "";
		Boolean result = true;
		// if patient is pregnant, confirm date must be set
		if ((rdBtnPregnant.getSelection())
				&& (btnMenstrualOccurance.getDate() == null)) {
			title = "Last Menstrual Period Not Set";
			message = "If this patient is pregnant, please capture the last menstrual period (or an estimate).";
			result = false;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", result.toString());
		map.put("title", title);
		map.put("message", message);
		return map;
	}

	/**
	 * @param patientIsFemale
	 *            the patientIsFemale to set
	 */
	public void setPatientIsFemale(boolean patientIsFemale) {
		this.patientIsFemale = patientIsFemale;
	}

	private void cmdViewPAVASWidgetSelected() {
		if (localPatient == null) {
			new ShowPAVAS(parent.getShell());
		} else {
			new ShowPAVAS(parent.getShell(), localPatient);
		}
	}

}
