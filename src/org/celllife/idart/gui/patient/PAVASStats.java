package org.celllife.idart.gui.patient;

import java.util.Date;
import java.util.List;

import model.manager.PAVASManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientStatTypes;
import org.celllife.idart.database.hibernate.PatientStatistic;
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

public class PAVASStats extends GenericFormGui {

	private DateButton btnTestTaken;
	private DateButton btnTestRecorded;
	private CCombo cmbStat1;
	private CCombo cmbStat2;
	private CCombo cmbStat3;
	private Text txtValue1;
	private Text txtValue2;
	private Text txtValue3;

	// THE GROUPS

	private Group grpStatistics;

	// The Patient
	private final Patient localPatient;
	private PatientStatistic localPatientStatistic;
	private List<PatientStatistic> localEntryStatistics;

	private Composite compStatInfo;
	private final ShowPAVAS myShowPAVAS;

	public PAVASStats(Shell parent, Patient patient, ShowPAVAS myshowPAVAS) {
		super(parent, HibernateUtil.getNewSession());
		localPatient = PatientManager.getPatient(getHSession(), patient
				.getId());
		myShowPAVAS = myshowPAVAS;
	}

	public PAVASStats(Shell parent, Patient patient, ShowPAVAS myshowPAVAS,
			int entry_id) {
		super(parent, HibernateUtil.getNewSession());
		localPatient = PatientManager.getPatient(getHSession(), patient
				.getId());
		myShowPAVAS = myshowPAVAS;
		localEntryStatistics = PAVASManager.localEntryStatistics(getHSession(),
				entry_id);
		SetUpStatistics();
	}

	@Override
	protected void createShell() {
		String shellTxt = "Patient Stats";
		Rectangle bounds = new Rectangle(100, 100, 600, 410);

		buildShell(shellTxt, bounds);
	}

	/**
	 * This method initializes compHeader
	 *
	 */

	@Override
	protected void createCompHeader() {

		String headerTxt = ("Patient Statistics");

		iDartImage icoImage = (iDartImage.PAVASSTATS);

		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createContents() {
		{
			// here put the screen

			compStatInfo = new Composite(getShell(), SWT.NONE);
			compStatInfo.setBounds(new Rectangle(16, 55, 550, 280));

			grpStatistics = new Group(compStatInfo, SWT.NONE);
			grpStatistics.setBounds(new Rectangle(30, 30, 510, 220));
			grpStatistics.setText("Patient Stats");
			grpStatistics.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			Label lblTestTaken = new Label(grpStatistics, SWT.NONE);
			lblTestTaken.setBounds(new Rectangle(15, 30, 120, 20));
			lblTestTaken.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblTestTaken.setText("* Date Test Taken:");
			btnTestTaken = new DateButton(grpStatistics, DateButton.NONE,
					new DateInputValidator(DateRuleFactory
							.beforeNowInclusive(true)));
			btnTestTaken.setBounds(150, 26, 150, 28);
			btnTestTaken.setText("Testing Date");
			btnTestTaken.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			btnTestTaken.setToolTipText("Press this button to select a date.");

			Label lblTestRecorded = new Label(grpStatistics, SWT.NONE);
			lblTestRecorded.setBounds(new Rectangle(15, 65, 120, 20));
			lblTestRecorded
			.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblTestRecorded.setText("* Date Recorded:");
			btnTestRecorded = new DateButton(grpStatistics, DateButton.NONE,
					new DateInputValidator(DateRuleFactory
							.beforeNowInclusive(true)));
			btnTestRecorded.setBounds(150, 61, 150, 28);
			btnTestRecorded.setText("Recorded Date");
			btnTestRecorded
			.setToolTipText("Press this button to select a date.");

			Label lblStat1 = new Label(grpStatistics, SWT.NONE);
			lblStat1.setBounds(new Rectangle(15, 100, 120, 20));
			lblStat1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblStat1.setText("* Statistic 1:");
			cmbStat1 = new CCombo(grpStatistics, SWT.BORDER);
			cmbStat1.setBounds(new Rectangle(150, 100, 150, 20));
			cmbStat1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			cmbStat1.setEditable(false);
			cmbStat1.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
			Label lblValue1 = new Label(grpStatistics, SWT.NONE);
			lblValue1.setBounds(new Rectangle(320, 100, 50, 20));
			lblValue1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblValue1.setText("Value:");
			txtValue1 = new Text(grpStatistics, SWT.BORDER);
			txtValue1.setBounds(new Rectangle(380, 100, 60, 20));
			txtValue1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			Label lblStat2 = new Label(grpStatistics, SWT.NONE);
			lblStat2.setBounds(new Rectangle(22, 135, 120, 20));
			lblStat2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblStat2.setText("Statistic 2:");
			cmbStat2 = new CCombo(grpStatistics, SWT.BORDER);
			cmbStat2.setBounds(new Rectangle(150, 135, 150, 20));
			cmbStat2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			cmbStat2.setEditable(false);
			cmbStat2.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
			Label lblValue2 = new Label(grpStatistics, SWT.NONE);
			lblValue2.setBounds(new Rectangle(320, 135, 60, 20));
			lblValue2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblValue2.setText("Value:");
			txtValue2 = new Text(grpStatistics, SWT.BORDER);
			txtValue2.setBounds(new Rectangle(380, 135, 60, 20));
			txtValue2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			Label lblStat3 = new Label(grpStatistics, SWT.NONE);
			lblStat3.setBounds(new Rectangle(22, 170, 120, 20));
			lblStat3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblStat3.setText("Statistic 3:");
			cmbStat3 = new CCombo(grpStatistics, SWT.BORDER);
			cmbStat3.setBounds(new Rectangle(150, 170, 150, 20));
			cmbStat3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			cmbStat3.setEditable(false);
			cmbStat3.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
			Label lblValue3 = new Label(grpStatistics, SWT.NONE);
			lblValue3.setBounds(new Rectangle(320, 170, 60, 20));
			lblValue3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblValue3.setText("Value:");
			txtValue3 = new Text(grpStatistics, SWT.BORDER);
			txtValue3.setBounds(new Rectangle(380, 170, 60, 20));
			txtValue3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			List<PatientStatTypes> pst = PAVASManager
			.getStatTypes(getHSession());
			for (int i = 0; i < pst.size(); i++) {
				cmbStat1.add(pst.get(i).getstatname());
				cmbStat2.add(pst.get(i).getstatname());
				cmbStat3.add(pst.get(i).getstatname());
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
		Date testdate = btnTestTaken.getDate();
		Date recordeddate = btnTestRecorded.getDate();

		if (testdate == null) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m
			.setMessage("The Testing date has not been set - please enter a testing Date");
			m.open();
			return false;
		}
		if (recordeddate == null) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m
			.setMessage("The Recorded date has not been set - please enter a recorded Date");
			m.open();
			return false;
		}

		// if (testdate.compareTo(new Date()) > 0) {
		// MessageBox m = new MessageBox(getShell(), SWT.OK
		// | SWT.ICON_INFORMATION);
		// m.setText("Patient Statistic Validation");
		// m.setMessage("The test date must not be in the future");
		// m.open();
		// return false;
		// }
		// if (recordeddate.compareTo(new Date()) > 0) {
		// MessageBox m = new MessageBox(getShell(), SWT.OK
		// | SWT.ICON_INFORMATION);
		// m.setText("Patient Statistic Validation");
		// m.setMessage("The recorded date must not be in the future");
		// m.open();
		// return false;
		// }
		if (testdate.compareTo(recordeddate) > 0) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m
			.setMessage("The recorded date must be before or after the testing date");
			m.open();
			return false;
		}

		if (cmbStat1.getText().equals("")) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m.setMessage("You must fill in a least one statistic");
			m.open();
			return false;
		}
		if (txtValue1.getText().equals("")) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m.setMessage("Your Statistic must have a value");
			m.open();
			return false;
		}
		if (txtValue2.getText().equals("") && !cmbStat2.getText().equals("")) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m.setMessage("Your Statistic must have a value");
			m.open();
			return false;
		}
		if (txtValue3.getText().equals("") && !cmbStat3.getText().equals("")) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m.setMessage("Your Statistic must have a value");
			m.open();
			return false;
		}

		try {
			Double.parseDouble(txtValue1.getText());
		} catch (NumberFormatException nfe) {
			if (getstatformat(cmbStat1.getText()).equals("N")) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Patient Statistic Validation");
				m.setMessage("Your Statistic should be a number");
				m.open();
				return false;
			}
		}
		try {
			Double.parseDouble(txtValue2.getText());
		} catch (NumberFormatException nfe) {
			if (getstatformat(cmbStat2.getText()).equals("N")) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Patient Statistic Validation");
				m.setMessage("Your Statistic should be a number");
				m.open();
				return false;
			}
		}
		try {
			Double.parseDouble(txtValue3.getText());
		} catch (NumberFormatException nfe) {
			if (getstatformat(cmbStat3.getText()).equals("N")) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Patient Statistic Validation");
				m.setMessage("Your Statistic should be a number");
				m.open();
				return false;
			}

		}
		if (!cmbStat1.getText().equals("")
				&& cmbStat1.getText().equals(cmbStat2.getText())) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m
			.setMessage("The statistics are duplicated - please only enter 1 of each type");
			m.open();
			return false;
		}
		if (!cmbStat1.getText().equals("")
				&& cmbStat1.getText().equals(cmbStat3.getText())) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m
			.setMessage("The statistics are duplicated - please only enter 1 of each type");
			m.open();
			return false;
		}
		if (!cmbStat2.getText().equals("")
				&& cmbStat2.getText().equals(cmbStat3.getText())) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Statistic Validation");
			m
			.setMessage("The statistics are duplicated - please only enter 1 of each type");
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
		btnTestTaken.setText("Testing Date");
		btnTestRecorded.setText("Recorded Date");
		cmbStat1.setText("");
		cmbStat2.setText("");
		cmbStat3.setText("");
		txtValue1.setText("");
		txtValue2.setText("");
		txtValue3.setText("");

	}

	@Override
	protected void cmdSaveWidgetSelected() {
		if (fieldsOk()) {

			Transaction tx = null;
			Date dateTestTaken = btnTestTaken.getDate();
			Date dateTestRecorded = btnTestRecorded.getDate();

			try {
				tx = getHSession().beginTransaction();
				// this is a new patient visit
				if (localPatientStatistic == null) {
					// String s = "N";
					int intstattype = 0;
					List<PatientStatTypes> pst = PAVASManager
					.getStatTypes(getHSession());
					for (int i = 0; i < pst.size(); i++) {

						if (pst.get(i).getstatname().equals(cmbStat1.getText())) {
							intstattype = pst.get(i).getId();
						}
					}

					if (getstatformat(cmbStat1.getText()).equals("N")) {
						localPatientStatistic = new PatientStatistic(0,
								localPatient.getId(), dateTestTaken,
								dateTestRecorded, intstattype, Double
								.parseDouble(txtValue1.getText()),
								txtValue1.getText(), null);
					} else {
						localPatientStatistic = new PatientStatistic(0,
								localPatient.getId(), dateTestTaken,
								dateTestRecorded, intstattype, 0, txtValue1
								.getText(), null);
					}
					PAVASManager.savePatientStatistic(getHSession(),
							localPatientStatistic);
					int ps_id = localPatientStatistic.getId();
					localPatientStatistic.setentry_id(ps_id);
					PAVASManager.savePatientStatistic(getHSession(),
							localPatientStatistic);

					intstattype = 0;
					for (int i = 0; i < pst.size(); i++) {
						if (pst.get(i).getstatname().equals(cmbStat2.getText())) {
							intstattype = pst.get(i).getId();
						}
					}
					if (intstattype != 0) {
						if (getstatformat(cmbStat2.getText()).equals("N")) {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, Double
									.parseDouble(txtValue2.getText()),
									txtValue2.getText(), null);
						} else {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, 0, txtValue2
									.getText(), null);
						}
						PAVASManager.savePatientStatistic(getHSession(),
								localPatientStatistic);
					}

					intstattype = 0;
					for (int i = 0; i < pst.size(); i++) {
						if (pst.get(i).getstatname().equals(cmbStat3.getText())) {
							intstattype = pst.get(i).getId();
						}
					}
					if (intstattype != 0) {
						if (getstatformat(cmbStat3.getText()).equals("N")) {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, Double
									.parseDouble(txtValue3.getText()),
									txtValue3.getText(), null);
						} else {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, 0, txtValue3
									.getText(), null);
						}
						PAVASManager.savePatientStatistic(getHSession(),
								localPatientStatistic);
					}
				}

				// else, we're updating an existing patient Statistic
				else if (localPatientStatistic != null) {
					// String s = "N";
					PAVASManager.deleteEntryStatistics(getHSession(),
							localPatientStatistic.getentry_id());
					int intstattype = 0;
					List<PatientStatTypes> pst = PAVASManager
					.getStatTypes(getHSession());
					for (int i = 0; i < pst.size(); i++) {

						if (pst.get(i).getstatname().equals(cmbStat1.getText())) {
							intstattype = pst.get(i).getId();
						}
					}

					if (getstatformat(cmbStat1.getText()).equals("N")) {
						localPatientStatistic = new PatientStatistic(0,
								localPatient.getId(), dateTestTaken,
								dateTestRecorded, intstattype, Double
								.parseDouble(txtValue1.getText()),
								txtValue1.getText(), null);
					} else {
						localPatientStatistic = new PatientStatistic(0,
								localPatient.getId(), dateTestTaken,
								dateTestRecorded, intstattype, 0, txtValue1
								.getText(), null);
					}
					PAVASManager.savePatientStatistic(getHSession(),
							localPatientStatistic);
					int ps_id = localPatientStatistic.getId();
					localPatientStatistic.setentry_id(ps_id);
					PAVASManager.savePatientStatistic(getHSession(),
							localPatientStatistic);

					intstattype = 0;
					for (int i = 0; i < pst.size(); i++) {
						if (pst.get(i).getstatname().equals(cmbStat2.getText())) {
							intstattype = pst.get(i).getId();
						}
					}
					if (intstattype != 0) {
						if (getstatformat(cmbStat2.getText()).equals("N")) {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, Double
									.parseDouble(txtValue2.getText()),
									txtValue2.getText(), null);
						} else {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, 0, txtValue2
									.getText(), null);
						}
						PAVASManager.savePatientStatistic(getHSession(),
								localPatientStatistic);
					}

					intstattype = 0;
					for (int i = 0; i < pst.size(); i++) {
						if (pst.get(i).getstatname().equals(cmbStat3.getText())) {
							intstattype = pst.get(i).getId();
						}
					}
					if (intstattype != 0) {
						if (getstatformat(cmbStat3.getText()).equals("N")) {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, Double
									.parseDouble(txtValue3.getText()),
									txtValue3.getText(), null);
						} else {
							localPatientStatistic = new PatientStatistic(ps_id,
									localPatient.getId(), dateTestTaken,
									dateTestRecorded, intstattype, 0, txtValue3
									.getText(), null);
						}
						PAVASManager.savePatientStatistic(getHSession(),
								localPatientStatistic);
					}

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

	private String getstatformat(String statname) {
		String statformat = "";
		List<PatientStatTypes> pst = PAVASManager.getStatTypes(getHSession());
		for (int i = 0; i < pst.size(); i++) {
			if (pst.get(i).getstatname().equals(statname)) {
				statformat = pst.get(i).getstatformat();
			}
		}
		return statformat;
	}

	private void SetUpStatistics() {
		// if isAddnotUpdate=false; then get the Visit details based upon the
		// patient visit id
		// then fill in the details into localpatientstatistic
		localPatientStatistic = localEntryStatistics.get(0);
		btnTestTaken.setDate(localPatientStatistic.getdatetested());
		btnTestRecorded.setDate(localPatientStatistic.getdaterecorded());

		String stattype = "";
		List<PatientStatTypes> pst = PAVASManager.getStatTypes(getHSession());
		for (int i = 0; i < pst.size(); i++) {
			if (pst.get(i).getId() == localPatientStatistic.getstattype()) {
				stattype = pst.get(i).getstatname();
			}
		}
		cmbStat1.setText(stattype);
		txtValue1.setText(localPatientStatistic.getstattext().trim());
		if (localEntryStatistics.size() > 1) {
			localPatientStatistic = localEntryStatistics.get(1);
			stattype = "";
			for (int i = 0; i < pst.size(); i++) {
				if (pst.get(i).getId() == localPatientStatistic.getstattype()) {
					stattype = pst.get(i).getstatname();
				}
			}
			cmbStat2.setText(stattype);
			txtValue2.setText(localPatientStatistic.getstattext().trim());
		}

		if (localEntryStatistics.size() > 2) {
			localPatientStatistic = localEntryStatistics.get(2);
			stattype = "";
			for (int i = 0; i < pst.size(); i++) {
				if (pst.get(i).getId() == localPatientStatistic.getstattype()) {
					stattype = pst.get(i).getstatname();
				}
			}
			cmbStat3.setText(stattype);
			txtValue3.setText(localPatientStatistic.getstattext().trim());
		}
	}

}
