package org.celllife.idart.gui.patient.tabs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.misc.GenericTab;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

public class TreatmentManagementTab extends GenericTab implements IPatientTab {

	private DateButton btnNextAppointment;
	private Session hSession;
	private boolean isPatientActive;
	private final Logger log = Logger.getLogger(getClass());
	private TabFolder parent;
	private int style;
	private Text txtTreatmentSupporterName;
	private Text txtTreatmentSupporterPhone;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#changesMade(org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public boolean changesMade(Patient patient) {
		boolean noChangesMade = true;
		noChangesMade &= patient.getNextOfKinName().trim().equals(
				txtTreatmentSupporterName.getText().trim());
		noChangesMade &= patient.getNextOfKinPhone().trim().equals(
				txtTreatmentSupporterPhone.getText().trim());

		Date theNewAppointmentDate = btnNextAppointment.getDate();
		Appointment currentAppointment = PatientManager
		.getLatestActiveAppointmentForPatient(patient);

		if (currentAppointment != null) {
			Date theLatestAppointmentDate = currentAppointment
			.getAppointmentDate();
			if (theLatestAppointmentDate == null) {
				log
				.error("appointment extists, but next appointment date is null!");
				noChangesMade = false;
			} else if (theNewAppointmentDate != null
					&& theNewAppointmentDate
					.compareTo(theLatestAppointmentDate) != 0) {
				noChangesMade = false;
			}
		} else if (theNewAppointmentDate != null) {
			noChangesMade = false;
		}

		return !noChangesMade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#clear()
	 */
	@Override
	public void clear() {
		btnNextAppointment.setText("Next App Date");
		txtTreatmentSupporterName.setText("");
		txtTreatmentSupporterPhone.setText("");
		btnNextAppointment.setDate(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#create()
	 */
	@Override
	public void create() {
		tabItem = new TabItem(parent, style);
		tabItem.setText("  Treatment Management  ");
		createGrpTreatmentManagement();
	}

	/**
	 * This method initializes grpTreatmentSupporter
	 */
	private void createGrpTreatmentManagement() {

		Group grpTreatmentSupporter = new Group(tabItem.getParent(), SWT.NONE);
		grpTreatmentSupporter.setBounds(new Rectangle(3, 3, 750, 140));
		grpTreatmentSupporter.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		tabItem.setControl(grpTreatmentSupporter);

		Label lblNextAppointment = new Label(grpTreatmentSupporter, SWT.NONE);
		lblNextAppointment.setBounds(new Rectangle(6, 94, 114, 18));
		lblNextAppointment.setText("  Next Appointment:");
		lblNextAppointment.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnNextAppointment = new DateButton(grpTreatmentSupporter,
				DateButton.ZERO_TIMESTAMP,
				null);
		btnNextAppointment.setBounds(new Rectangle(195, 90, 200, 25));
		btnNextAppointment.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnNextAppointment.setText("Next App Date");


		// Treatment Supporter Name
		Label lblTreatmentSupporterName = new Label(grpTreatmentSupporter,
				SWT.NONE);
		lblTreatmentSupporterName.setBounds(new Rectangle(6, 32, 179, 20));
		lblTreatmentSupporterName.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblTreatmentSupporterName.setText("  Treatment Supporter's Name:");

		txtTreatmentSupporterName = new Text(grpTreatmentSupporter, SWT.BORDER);
		txtTreatmentSupporterName.setBounds(new Rectangle(194, 32, 200, 20));
		txtTreatmentSupporterName.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		// Treatment Supporter Phone
		Label lblTreatmentSupporterPhone = new Label(grpTreatmentSupporter,
				SWT.NONE);
		lblTreatmentSupporterPhone.setBounds(new Rectangle(6, 62, 179, 20));
		lblTreatmentSupporterPhone.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblTreatmentSupporterPhone.setText("  Treatment Supporter's Phone:");

		txtTreatmentSupporterPhone = new Text(grpTreatmentSupporter, SWT.BORDER);
		txtTreatmentSupporterPhone.setBounds(new Rectangle(194, 62, 200, 20));
		txtTreatmentSupporterPhone.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#enable(boolean,
	 *      org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void enable(boolean enable, Color color) {
		txtTreatmentSupporterName.setEnabled(enable);
		txtTreatmentSupporterPhone.setEnabled(enable);
		btnNextAppointment.setEnabled(enable);
		if (enable) {
			btnNextAppointment.setEnabled(isPatientActive);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#populate(org.hibernate.Session,
	 *      org.celllife.idart.database.hibernate.Patient, boolean)
	 */
	@Override
	public void loadPatientDetails(Patient patient, @SuppressWarnings("hiding")
			boolean isPatientActive) {
		this.isPatientActive = isPatientActive;
		txtTreatmentSupporterName.setText(patient.getNextOfKinName());
		txtTreatmentSupporterPhone.setText(patient.getNextOfKinPhone());

		Date theDateExpected = null;
		Appointment latestApp = PatientManager
		.getLatestActiveAppointmentForPatient(patient);
		if (latestApp != null) {
			theDateExpected = latestApp.getAppointmentDate();
		}
		if (theDateExpected != null) {
			btnNextAppointment.setText(iDARTUtil.format(theDateExpected));
		} else {
			btnNextAppointment.setText("Next App Date");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#setParent(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void setParent(TabFolder parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#setPatientDetails(org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public void setPatientDetails(Patient patient) {
		patient.setNextOfKinName(txtTreatmentSupporterName.getText());
		patient.setNextOfKinPhone(txtTreatmentSupporterPhone.getText());
		if (btnNextAppointment.getDate() != null) {
			PatientManager.setNextAppointmentDate(hSession, patient,
					btnNextAppointment.getDate());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#setSession(org.hibernate.Session)
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
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#submit(org.hibernate.Session,
	 *      org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public void submit(Patient patient) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.IPatientTab#validateFields()
	 */
	@Override
	public Map<String, String> validateFields(Patient patient) {
		// SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		// if (sdf.format(theNewAppointmentDate);
		Map<String, String> map = new HashMap<String, String>();
		if (btnNextAppointment.getDate() != null) {
			Date theNewAppointmentDate = btnNextAppointment.getDate();
			Calendar today = Calendar.getInstance();
			today.setTime(new Date());
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			boolean validNextAppointmentDate = (theNewAppointmentDate
					.compareTo(today.getTime()) == 0 || theNewAppointmentDate
					.after(today.getTime()));
			map.put("result", String.valueOf(validNextAppointmentDate));
			if (!validNextAppointmentDate) {
				map.put("title", "Error with next appointment date");
				map.put("message",
				"You can't set an appointment date in the past. Please select a date after today.");
			}
			return map;
		} else {
			map.put("result", String.valueOf(true));
			return map;
		}
	}
}