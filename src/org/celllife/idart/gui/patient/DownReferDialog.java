package org.celllife.idart.gui.patient;

import java.util.Date;

import model.manager.AdministrationManager;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DownReferDialog extends GenericOthersGui {

	private static final String infoText = "When you down refer this "
		+ "patient, you will still be packaging drugs for them. "
		+ "This patient will still be counted in the total number "
		+ "of patients on treatement but will be reflected in the "
		+ "down referral clinic's data, and no longer in the "
		+ "pharmacy's data.";
	private final Patient patient;
	private CCombo cmbClinic;
	private DateButton btnDownReferredDate;
	private Button btnYes;
	private final Date startDate;

	public DownReferDialog(Shell parent, Session session, Patient patient) {
		super(parent, session);
		this.patient = patient;
		startDate = patient.getMostRecentEpisode().getStartDate();
	}

	@Override
	protected void createCompButtons() {
		Button btnSave = new Button(getCompButtons(), SWT.NONE);
		btnSave.setText("Save");
		btnSave.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSave.setToolTipText("Press this button to save the information \n"
				+ "you have entered on this screen.");
		btnSave
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSaveWidgetSelected();
			}
		});

		Button btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setToolTipText("Press this button to close this screen.\n"
				+ "The information you've entered here will be lost.");
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});
	}

	protected void cmdCancelWidgetSelected() {
		closeShell(false);
	}

	protected void cmdSaveWidgetSelected() {
		if (fieldsOk()) {
			doSave();
		}
	}

	private boolean fieldsOk() {
		String clinicName = cmbClinic.getText();
		if (clinicName == null || clinicName.isEmpty()) {
			showMessage(MessageDialog.ERROR, "Clinic not selected",
			"Please select a clinic.");
			return false;
		}
		return true;
	}

	private void doSave() {
		Transaction tx = null;
		try {
			tx = getHSession().beginTransaction();
			Episode episode = patient.getMostRecentEpisode();
			Date date = btnDownReferredDate.getDate();
			episode.setStopDate(date);
			episode.setStopReason("Down-Referred");
			String clinicName = cmbClinic.getText();
			episode.setStopNotes("To " + clinicName);

			Episode newEpisode = new Episode();
			newEpisode.setPatient(patient);
			newEpisode.setStartDate(date);
			newEpisode.setStartNotes("At " + clinicName);
			String startReason;
			if (btnYes.getSelection()) {
				startReason = "Start at Down Referral Clinic";
			} else {
				startReason = "Restart at Down Referral Clinic";
			}
			newEpisode.setStartReason(startReason);
			patient.getEpisodes().add(newEpisode);

			Clinic clinic = AdministrationManager.getClinic(getHSession(),
					clinicName);

			patient.setClinic(clinic);
			newEpisode.setClinic(clinic);

			getHSession().flush();
			tx.commit();

			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Patient Down-Referred");
			m.setMessage("Patient '".concat(patient.getPatientId()).concat(
			"' has been down-referred"));
			m.open();

			closeShell(false);
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}

			getLog().error("Error saving patient to the database.", he);
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Problems Saving to the Database");
			m.setMessage("There was a problem saving the patient's "
					+ "information to the database. Please try again.");
			m.open();
		}
	}

	@Override
	protected void createCompHeader() {
		// compHeader
		setCompHeader(new Composite(getShell(), SWT.NONE));
		getCompHeader().setLayout(new FormLayout());

		FormData fd = new FormData();
		fd.left = new FormAttachment(10, 0);
		fd.right = new FormAttachment(90, 0);
		fd.top = new FormAttachment(0, 5);

		// lblHeader
		lblHeader = new Label(getCompHeader(), SWT.BORDER | SWT.WRAP);
		lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		lblHeader.setText(infoText);
		lblHeader.setLayoutData(fd);

		getCompHeader().pack();
		// Set bounds after pack, otherwise it resizes the composite
		Rectangle b = getShell().getBounds();
		getCompHeader().setBounds(0, 5, b.width, 100);
	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void createShell() {
		String shellTxt = "Down-Refer This Patient";
		buildShell(shellTxt, new Rectangle(25, 0, 550, 300));

		createContents();
	}

	private void createContents() {
		Composite compContents = new Composite(getShell(), SWT.NONE);

		GridLayout gl = new GridLayout(2, true);
		gl.horizontalSpacing = 15;
		gl.verticalSpacing = 10;
		gl.marginLeft = 85;
		compContents.setLayout(gl);

		Label label = new Label(compContents, SWT.CENTER);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER));
		label.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		label.setText("Down-Referral Clinic:");

		cmbClinic = new CCombo(compContents, SWT.BORDER);
		cmbClinic.setEditable(false);
		cmbClinic.setLayoutData(new GridData(150, 15));
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		CommonObjects.populateClinics(getHSession(), cmbClinic, false);

		label = new Label(compContents, SWT.CENTER);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER));
		label.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		label.setText("Date Patient is Down-Referred:");

		btnDownReferredDate = new DateButton(
				compContents,
				DateButton.ZERO_TIMESTAMP,
				new DateInputValidator(DateRuleFactory.between(startDate,
						true,
						new Date(), true, true)));
		btnDownReferredDate.setLayoutData(new GridData(155, 20));
		btnDownReferredDate.setText("Date");
		btnDownReferredDate
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDownReferredDate
		.setToolTipText("Press this button to select a date.");
		try {
			btnDownReferredDate.setDate(new Date());
		} catch (Exception e) {
			showMessage(MessageDialog.ERROR, "Error", e.getMessage());
		}

		label = new Label(compContents, SWT.CENTER);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER));
		label.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		label.setText("First Time at Down-Referral Clinic?");

		Composite compRadio = new Composite(compContents, SWT.NONE);
		compRadio.setLayout(new RowLayout());
		compRadio.setLayoutData(new GridData(150, 25));

		btnYes = new Button(compRadio, SWT.RADIO);
		btnYes.setText("Yes");
		Button btnNo = new Button(compRadio, SWT.RADIO);
		btnNo.setText("No");
		btnYes.setSelection(true);

		Rectangle b = getShell().getBounds();
		compContents.setBounds(0, 100, b.width, 100);
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	public void openAndWait() {
		activate();
		while (!getShell().isDisposed()) {
			if (!getShell().getDisplay().readAndDispatch()) {
				getShell().getDisplay().sleep();
			}
		}
	}
}
