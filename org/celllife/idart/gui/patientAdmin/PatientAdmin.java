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

package org.celllife.idart.gui.patientAdmin;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.patient.AddPatient;
import org.celllife.idart.gui.patient.MergePatients;
import org.celllife.idart.gui.patient.ShowPAVAS;
import org.celllife.idart.gui.platform.GenericAdminGui;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.prescription.AddPrescription;
import org.celllife.idart.gui.reportParameters.PatientHistory;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.Screens;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 */

public class PatientAdmin extends GenericAdminGui {

	private Button btnPatientAdd;

	private Button btnPatientUpdate;

	private Button btnPrescriptionUpdate;

	private Button btnPatientHistoryReport;

	private Button btnMergePatients;

	private Label lblPicPatientAdd;

	private Label lblPicPatientUpdate;

	private Label lblPicPrescriptionUpdate;

	private Label lblPicPatientHistoryReport;

	private Label lblPicMergePatients;

	private Label lblPicPatientVisitsandStats;

	private Button btnPatientVisitsandStats;

	/**
	 * Constructor for PatientAdmin.
	 * 
	 * @param parent
	 *            Shell
	 */
	public PatientAdmin(Shell parent) {
		super(parent);
	}

	/**
	 * This method initializes newPatientAdmin
	 */
	@Override
	protected void createShell() {
		String shellText = Messages.getString("PatientAdmin.shell.title"); //$NON-NLS-1$
		buildShell(shellText);
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerText = Messages.getString("PatientAdmin.shell.title"); //$NON-NLS-1$
		iDartImage icoImage = iDartImage.PATIENTADMIN;
		// Building the component from the GenericAdminGui
		buildCompHeader(headerText, icoImage);
	}

	/**
	 * This method initializes compOptions
	 * 
	 */
	@Override
	protected void createCompOptions() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.justify = true;
		compOptions.setLayout(rowLayout);

		GridLayout gl = new GridLayout(2, false);
		gl.verticalSpacing = 30;
		Composite compOptionsInner = new Composite(compOptions, SWT.NONE);
		compOptionsInner.setLayout(gl);

		GridData gdPic = new GridData();
		gdPic.heightHint = 43;
		gdPic.widthHint = 50;

		GridData gdBtn = new GridData();
		gdBtn.heightHint = 40;
		gdBtn.widthHint = 360;

		// lblPicPatientAdd
		lblPicPatientAdd = new Label(compOptionsInner, SWT.NONE);
		lblPicPatientAdd.setLayoutData(gdPic);
		lblPicPatientAdd
		.setImage(ResourceUtils.getImage(iDartImage.PATIENTNEW));
		lblPicPatientAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdAddPatientWidgetSelected();
			}
		});

		// btnPatientAdd
		btnPatientAdd = new Button(compOptionsInner, SWT.NONE);
		btnPatientAdd.setData(iDartProperties.SWTBOT_KEY, Screens.ADD_PATIENT.getAccessButtonId());
		btnPatientAdd.setText(Messages.getString("PatientAdmin.button.addNewPatient")); //$NON-NLS-1$
		btnPatientAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		btnPatientAdd
		.setToolTipText(Messages.getString("PatientAdmin.button.addNewPatient.tooltip")); //$NON-NLS-1$
		btnPatientAdd.setLayoutData(gdBtn);
		btnPatientAdd
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAddPatientWidgetSelected();
			}
		});

		// lblPicPatientUpdate
		lblPicPatientUpdate = new Label(compOptionsInner, SWT.NONE);
		lblPicPatientUpdate.setLayoutData(gdPic);
		lblPicPatientUpdate.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTUPDATE));
		lblPicPatientUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdUpdatePatientWidgetSelected();
			}
		});

		// btnPatientUpdate
		btnPatientUpdate = new Button(compOptionsInner, SWT.NONE);
		btnPatientUpdate.setData(iDartProperties.SWTBOT_KEY, Screens.UPDATE_PATIENT.getAccessButtonId());
		btnPatientUpdate.setLayoutData(gdBtn);
		btnPatientUpdate.setText(Messages.getString("PatientAdmin.button.updateExistingPatient")); //$NON-NLS-1$
		btnPatientUpdate
		.setToolTipText(Messages.getString("PatientAdmin.button.updateExistingPatient.tooltip")); //$NON-NLS-1$
		btnPatientUpdate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		btnPatientUpdate
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdUpdatePatientWidgetSelected();
			}
		});

		// lblPicPrescriptionUpdate
		lblPicPrescriptionUpdate = new Label(compOptionsInner, SWT.NONE);
		lblPicPrescriptionUpdate.setLayoutData(gdPic);
		lblPicPrescriptionUpdate.setImage(ResourceUtils
				.getImage(iDartImage.PRESCRIPTIONNEW));
		lblPicPrescriptionUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdUpdatePrescriptionWidgetSelected();
			}
		});

		// btnPrescriptionUpdate
		btnPrescriptionUpdate = new Button(compOptionsInner, SWT.NONE);
		btnPrescriptionUpdate.setData(iDartProperties.SWTBOT_KEY, Screens.UPDATE_PRESCRIPTION.getAccessButtonId());
		btnPrescriptionUpdate.setLayoutData(gdBtn);
		btnPrescriptionUpdate
		.setText(Messages.getString("PatientAdmin.button.updatePrescription")); //$NON-NLS-1$
		btnPrescriptionUpdate
		.setToolTipText(Messages.getString("PatientAdmin.button.updatePrescription.tooltip")); //$NON-NLS-1$
		btnPrescriptionUpdate.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10));
		btnPrescriptionUpdate
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdUpdatePrescriptionWidgetSelected();
			}
		});

		// lblPicPatientHistoryReport
		lblPicPatientHistoryReport = new Label(compOptionsInner, SWT.NONE);
		lblPicPatientHistoryReport.setLayoutData(gdPic);
		lblPicPatientHistoryReport.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PATIENTHISTORY));
		lblPicPatientHistoryReport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdViewPatientHistoryWidgetSelected();
			}
		});

		// btnPatientHistoryReport
		btnPatientHistoryReport = new Button(compOptionsInner, SWT.NONE);
		btnPatientHistoryReport.setData(iDartProperties.SWTBOT_KEY, Screens.PATIENT_HISTORY_REPORT.getAccessButtonId());
		btnPatientHistoryReport.setLayoutData(gdBtn);
		btnPatientHistoryReport.setText(Messages.getString("PatientAdmin.button.viewPatientHistory")); //$NON-NLS-1$
		btnPatientHistoryReport
		.setToolTipText(Messages.getString("PatientAdmin.button.viewPatientHistory.tooltip")); //$NON-NLS-1$
		btnPatientHistoryReport.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10));
		btnPatientHistoryReport
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdViewPatientHistoryWidgetSelected();
			}
		});

		// lblPicPatientInfoLabel
		lblPicMergePatients = new Label(compOptionsInner, SWT.NONE);
		lblPicMergePatients.setLayoutData(gdPic);
		lblPicMergePatients.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdMergePatientsWidgetSelected();
			}
		});

		// btnPatientInfoLabel
		lblPicMergePatients.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTDUPLICATES));
		btnMergePatients = new Button(compOptionsInner, SWT.NONE);
		btnMergePatients.setData(iDartProperties.SWTBOT_KEY, Screens.PATIENT_MERGE.getAccessButtonId());
		btnMergePatients.setLayoutData(gdBtn);
		btnMergePatients.setText(Messages.getString("PatientAdmin.button.mergePatient")); //$NON-NLS-1$
		btnMergePatients
		.setToolTipText(Messages.getString("PatientAdmin.button.mergePatient.tooltip")); //$NON-NLS-1$
		btnMergePatients.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		btnMergePatients
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdMergePatientsWidgetSelected();
			}
		});

		// lblPicPatientVisitsandStats
		lblPicPatientVisitsandStats = new Label(compOptionsInner, SWT.NONE);
		lblPicPatientVisitsandStats.setLayoutData(gdPic);
		lblPicPatientVisitsandStats.setImage(ResourceUtils
				.getImage(iDartImage.PAVAS));
		lblPicPatientVisitsandStats.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdViewPAVASWidgetSelected2();
			}
		});

		// btnPatientVisitsandStats
		btnPatientVisitsandStats = new Button(compOptionsInner, SWT.NONE);
		btnPatientVisitsandStats.setData(iDartProperties.SWTBOT_KEY, Screens.PATIENT_VISITS.getAccessButtonId());
		btnPatientVisitsandStats.setLayoutData(gdBtn);
		btnPatientVisitsandStats.setText(Messages.getString("PatientAdmin.button.patientVisits")); //$NON-NLS-1$
		btnPatientVisitsandStats
		.setToolTipText(Messages.getString("PatientAdmin.button.patientVisits.tooltip")); //$NON-NLS-1$
		btnPatientVisitsandStats.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10));
		btnPatientVisitsandStats
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdViewPAVASWidgetSelected2();
			}
		});

		compOptions.layout();
		compOptionsInner.layout();

	}

	private void cmdAddPatientWidgetSelected() {
		// AddPatient(true) to ADD new patient
		AddPatient.addInitialisationOption(
				GenericFormGui.OPTION_isAddNotUpdate, true);
		new AddPatient(getShell(), true);
	}

	private void cmdUpdatePatientWidgetSelected() {
		// AddPatient(false) to UPDATE patient's details
		AddPatient.addInitialisationOption(
				GenericFormGui.OPTION_isAddNotUpdate, false);
		new AddPatient(getShell(), false);
	}

	private void cmdUpdatePrescriptionWidgetSelected() {
		new AddPrescription(null, getShell(), false);
	}

	private void cmdViewPatientHistoryWidgetSelected() {
		new PatientHistory(getShell(), true);
	}

	private void cmdMergePatientsWidgetSelected() {
		new MergePatients(getShell());
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	@Override
	protected void cmdCloseSelectedWidget() {
		cmdCloseSelected();
	}

	private void cmdViewPAVASWidgetSelected2() {
		new ShowPAVAS(getShell());
	}

}
