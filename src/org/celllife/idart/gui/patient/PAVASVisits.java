package org.celllife.idart.gui.patient;

import java.util.Date;
import java.util.List;

import model.manager.PAVASManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientVisit;
import org.celllife.idart.database.hibernate.PatientVisitReason;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

public class PAVASVisits extends GenericFormGui {

	private DateButton btnVisitDate;
	private CCombo cmbVisitReason;
	private Text txtDiagnosis;
	private Text txtNotes;

	// THE GROUPS

	private Group grpVisits;

	// The Patient
	private final Patient localPatient;

	private Composite compVisitInfo;
	private PatientVisit localPatientVisit;

	private final ShowPAVAS myShowPAVAS;

	public PAVASVisits(Shell parent, Patient patient, ShowPAVAS myshowPAVAS) {
		super(parent, HibernateUtil.getNewSession());
		localPatient = PatientManager.getPatient(getHSession(), patient
				.getId());
		myShowPAVAS = myshowPAVAS;
	}

	public PAVASVisits(Shell parent, Patient patient, ShowPAVAS myshowPAVAS,
			int pv_id) {
		super(parent, HibernateUtil.getNewSession());
		localPatient = PatientManager.getPatient(getHSession(), patient
				.getId());
		myShowPAVAS = myshowPAVAS;
		localPatientVisit = PAVASManager.getPatientVisit(getHSession(), pv_id);
		SetUpVisit();
	}

	@Override
	protected void createShell() {
		String shellTxt = "Patient Visits";
		Rectangle bounds = new Rectangle(100, 100, 600, 375);

		buildShell(shellTxt, bounds);
	}

	/**
	 * This method initializes compHeader
	 * 
	 */

	@Override
	protected void createCompHeader() {

		String headerTxt = ("Patient Visits");

		iDartImage icoImage = (iDartImage.PAVASVISITS);

		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createContents() {
		{
			// here put the screen

			compVisitInfo = new Composite(getShell(), SWT.NONE);
			compVisitInfo.setBounds(new Rectangle(16, 55, 550, 245));

			grpVisits = new Group(compVisitInfo, SWT.NONE);
			grpVisits.setBounds(new Rectangle(30, 30, 510, 185));
			grpVisits.setText("Patient Visits");
			grpVisits.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			Label lblVisitDate = new Label(grpVisits, SWT.NONE);
			lblVisitDate.setBounds(new Rectangle(15, 30, 120, 20));
			lblVisitDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblVisitDate.setText("* Date of Visit:");
			btnVisitDate = new DateButton(grpVisits, DateButton.NONE,
					new DateInputValidator(DateRuleFactory
							.beforeNowInclusive(true)));
			btnVisitDate.setBounds(150, 26, 150, 28);
			btnVisitDate.setText("Visit Date");
			btnVisitDate.setToolTipText("Press this button to select a date.");

			Label lblVisitReason = new Label(grpVisits, SWT.NONE);
			lblVisitReason.setBounds(new Rectangle(15, 65, 100, 20));
			lblVisitReason.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblVisitReason.setText("* Visit Reason:");
			cmbVisitReason = new CCombo(grpVisits, SWT.BORDER);
			cmbVisitReason.setBounds(new Rectangle(150, 65, 150, 20));
			cmbVisitReason.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			cmbVisitReason.setEditable(false);
			cmbVisitReason.setBackground(ResourceUtils
					.getColor(iDartColor.WHITE));

			Label lblDiagnosis = new Label(grpVisits, SWT.NONE);
			lblDiagnosis.setBounds(new Rectangle(22, 100, 100, 20));
			lblDiagnosis.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblDiagnosis.setText("Diagnosis:");
			txtDiagnosis = new Text(grpVisits, SWT.BORDER);
			txtDiagnosis.setTextLimit(255);
			txtDiagnosis.setBounds(new Rectangle(150, 100, 330, 20));
			txtDiagnosis.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			Label lblNotes = new Label(grpVisits, SWT.NONE);
			lblNotes.setBounds(new Rectangle(22, 135, 50, 20));
			lblNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblNotes.setText("Notes:");
			txtNotes = new Text(grpVisits, SWT.BORDER);
			txtNotes.setTextLimit(255);
			txtNotes.setBounds(new Rectangle(150, 135, 330, 20));
			txtNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			List<PatientVisitReason> pvr = PAVASManager
			.getVisitReasons(getHSession());

			for (int i = 0; i < pvr.size(); i++) {

				cmbVisitReason.add(pvr.get(i).getvisitreason());

			}

		}
	}

	protected void cmdCloseSelectedWidget() {
		closeShell(true);
	}

	// add on the reports buttons and the exit button

	@Override
	protected void createCompButtons() {
		buildCompButtons();

	}

	// initialise stats

	// exit button

	// reports buttons

	@Override
	protected void cmdCancelWidgetSelected() {
		cmdCloseSelected();
	}

	@Override
	protected void cmdClearWidgetSelected() {
		clearForm();
	}

	@Override
	protected boolean fieldsOk() {
		// put the checks here
		// visit reason should be filled in and one of the options
		// date should be a date
		Date visitdate = btnVisitDate.getDate();
		if (visitdate == null) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Visit Validation");
			m
			.setMessage("The Visit date has not been set - please enter a date");
			m.open();
			return false;
		}

		// replaced by input validator on date button

		// if (visitdate.compareTo(new Date()) > 0) {
		// MessageBox m = new MessageBox(getShell(), SWT.OK
		// | SWT.ICON_INFORMATION);
		// m.setText("Patient Visit Validation");
		// m.setMessage("The test date must not be in the future");
		// m.open();
		// return false;
		// }

		if (cmbVisitReason.getText().equals("")) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Visit Validation");
			m
			.setMessage("The Visit reason has not been set - please enter a visit reason");
			m.open();
			return false;
		}
		return true;
	}

	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	@Override
	protected void enableFields(boolean enable) {

		// fieldsEnabled = enable;
		btnSave.setEnabled(true);
	}

	@Override
	protected void clearForm() {
		btnVisitDate.setText("Visit Date");
		cmbVisitReason.setText("");
		txtDiagnosis.setText("");
		txtNotes.setText("");
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		if (fieldsOk()) {

			Transaction tx = null;
			Date dateofVisit = btnVisitDate.getDate();

			try {
				tx = getHSession().beginTransaction();
				// this is a new patient visit
				if (localPatientVisit == null) {
					String s = "N";
					int intreason = 0;
					List<PatientVisitReason> pvr = PAVASManager
					.getVisitReasons(getHSession());
					for (int i = 0; i < pvr.size(); i++) {

						if (pvr.get(i).getvisitreason().equals(
								cmbVisitReason.getText())) {
							intreason = pvr.get(i).getId();
						}
					}

					localPatientVisit = new PatientVisit(localPatient.getId(),
							dateofVisit, s, intreason, txtDiagnosis.getText(),
							txtNotes.getText());
					PAVASManager.savePatientVisit(getHSession(),
							localPatientVisit);
					// MessageBox m = new MessageBox(getShell(), SWT.OK
					// | SWT.ICON_INFORMATION);
					// m.setText("Do we get the id");
					// String st = localPatientVisit.getId() + "";
					// m.setMessage("pv_id : " + st);
					// m.open();

				}

				// else, we're updating an existing doctor
				else if (localPatientVisit != null) {
					int intreason = 0;
					List<PatientVisitReason> pvr = PAVASManager
					.getVisitReasons(getHSession());
					for (int i = 0; i < pvr.size(); i++) {

						if (pvr.get(i).getvisitreason().equals(
								cmbVisitReason.getText())) {
							intreason = pvr.get(i).getId();
						}
					}
					localPatientVisit.setDateofVisit(dateofVisit);
					localPatientVisit.setVisitReason(intreason);
					localPatientVisit.setdiagnosis(txtDiagnosis.getText());
					localPatientVisit.setnotes(txtNotes.getText());
					PAVASManager.savePatientVisit(getHSession(),
							localPatientVisit);
				}

				getHSession().flush();
				tx.commit();
				myShowPAVAS.SetUpTables();
				cmdCancelWidgetSelected();
			} catch (HibernateException he) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Problems Saving to the Database");
				m
				.setMessage("There was a problem saving the patient visit information to the database. Please try again.");
				m.open();

				if (tx != null) {
					tx.rollback();
				}
				getLog().error(he);
			}

		}

	}

	private void SetUpVisit() {
		// if isAddnotUpdate=false; then get the Visit details based upon the
		// patient visit id
		// then fill in the details into localpatientvisit
		String reason = "";
		List<PatientVisitReason> pvr = PAVASManager
		.getVisitReasons(getHSession());
		for (int i = 0; i < pvr.size(); i++) {
			if (pvr.get(i).getId() == localPatientVisit.getVisitReason()) {
				reason = pvr.get(i).getvisitreason();
			}

		}
		btnVisitDate.setDate(localPatientVisit.getDateofVisit());
		cmbVisitReason.setText(reason);
		txtDiagnosis.setText(localPatientVisit.getdiagnosis());
		txtNotes.setText(localPatientVisit.getnotes());

	}

}
