package org.celllife.idart.gui.welcome;

import java.util.List;

import org.celllife.idart.database.hibernate.SimpleDomain;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.patient.AddPatient;
import org.celllife.idart.gui.patient.AddPatientToStudy;
import org.celllife.idart.gui.patient.StudyAlerts;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.reports.NewReports;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.hibernate.Query;
import org.hibernate.Session;

public class StudyWorkerWelcome extends GenericWelcome {

	public StudyWorkerWelcome() {
		super();
		
		
	}

	@Override
	protected void createCompOptions(Composite compOptions) {
		Composite grpHeading = new Composite(shell, SWT.NONE);
		grpHeading.setBounds(new Rectangle(160, 80, 590, 40));
		Label lblWelcomeBlurb = new Label(grpHeading, SWT.CENTER | SWT.SHADOW_IN);
		
		lblWelcomeBlurb.setText(getFacilityNameSubtitle());
		lblWelcomeBlurb.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));
		lblWelcomeBlurb.setBounds(new Rectangle(3, 5, 580, 25));
		
		createUpdatePatientComp(compOptions);
		createAddPatientComp(compOptions);
		createRemovePatientComp(compOptions);
		createReportsComp(compOptions);
		createStudyAlertsComp(compHelpAndLogoff);

		lblPicLogoff.setBounds(new Rectangle(380, 0, 50, 43));
		btnLogOff.setBounds(new Rectangle(340, 50, 130, 40));
	}

	private String getFacilityNameSubtitle() {
		Session sess = HibernateUtil.getNewSession();
		String qString = "select s from SimpleDomain as s where s.description='pharmacy_detail' and s.name='pharmacy_name'";
		Query q = sess.createQuery(qString);
		List<SimpleDomain> result = q.list();

		if (result == null) {
			return "";
		}
		return "At " + result.get(0).getValue();
	}

	private void createStudyAlertsComp(Composite compOptions) {
		
		Label lblPicStudyAlerts = new Label(compOptions, SWT.NONE);
		lblPicStudyAlerts.setBounds(new Rectangle(195, 0, 50, 43));
		lblPicStudyAlerts.setImage(ResourceUtils.getImage(iDartImage.GENERALADMIN));
		lblPicStudyAlerts.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
//				AddPatientToStudy.addInitialisationOption(
//						GenericFormGui.OPTION_isAddNotUpdate, true);
//				new AddPatientToStudy(shell, true);
				new StudyAlerts(shell);
			}
		});

		
		Button btnStudyAlerts = new Button(compOptions, SWT.NONE);
		btnStudyAlerts.setBounds(new Rectangle(150, 50, 130, 40));
		btnStudyAlerts.setText(Messages
				.getString("studyworkerwelcome.button.studyalerts.text")); //$NON-NLS-1$
		btnStudyAlerts.setToolTipText(Messages
				.getString("studyworkerwelcome.button.studyalerts.tooltip")); //$NON-NLS-1$
		btnStudyAlerts.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnStudyAlerts
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						AddPatientToStudy.addInitialisationOption(
//								GenericFormGui.OPTION_isAddNotUpdate, true);
//						new AddPatientToStudy(shell, true);
						new StudyAlerts(shell);
					}
				});
	}

	private void createReportsComp(Composite compOptions) {
		Label lblPicReports = new Label(compOptions, SWT.NONE);
		lblPicReports.setBounds(new Rectangle(525, 0, 50, 43));
		lblPicReports.setImage(ResourceUtils.getImage(iDartImage.GENERALADMIN));
		lblPicReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new NewReports(shell);
			}
		});

		Button btnReports = new Button(compOptions, SWT.NONE);
		btnReports.setBounds(new Rectangle(500, 50, 100, 40));
		btnReports.setText(Messages
				.getString("studyworkerwelcome.button.reports.text")); //$NON-NLS-1$
		btnReports.setToolTipText(Messages
				.getString("studyworkerwelcome.button.reports.tooltip")); //$NON-NLS-1$
		btnReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnReports
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						new NewReports(shell);
					}
				});
	}

	private void createRemovePatientComp(Composite compOptions) {
		Label lblPicRemovePatient = new Label(compOptions, SWT.NONE);
		lblPicRemovePatient.setBounds(new Rectangle(370, 0, 50, 43));
		lblPicRemovePatient.setImage(ResourceUtils
				.getImage(iDartImage.GENERALADMIN));
		lblPicRemovePatient.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				AddPatientToStudy.addInitialisationOption(
						GenericFormGui.OPTION_isAddNotUpdate, false);
				new AddPatientToStudy(shell, false);
			}
		});

		Button btnRemovePatient = new Button(compOptions, SWT.NONE);
		btnRemovePatient.setBounds(new Rectangle(310, 50, 170, 40));
		btnRemovePatient.setText(Messages
				.getString("studyworkerwelcome.button.removepatient.text")); //$NON-NLS-1$
		btnRemovePatient.setToolTipText(Messages
				.getString("studyworkerwelcome.button.removepatient.tooltip")); //$NON-NLS-1$
		btnRemovePatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnRemovePatient
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						AddPatientToStudy.addInitialisationOption(
								GenericFormGui.OPTION_isAddNotUpdate, false);
						new AddPatientToStudy(shell, false);
					}
				});
	}

	private void createAddPatientComp(Composite compOptions) {
		Label lblPicAddPatient = new Label(compOptions, SWT.NONE);
		lblPicAddPatient.setBounds(new Rectangle(190, 0, 50, 43));
		lblPicAddPatient.setImage(ResourceUtils
				.getImage(iDartImage.GENERALADMIN));
		lblPicAddPatient.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				AddPatientToStudy.addInitialisationOption(
						GenericFormGui.OPTION_isAddNotUpdate, true);
				new AddPatientToStudy(shell, true);
			}
		});

		Button btnAddPatient = new Button(compOptions, SWT.NONE);
		btnAddPatient.setBounds(new Rectangle(160, 50, 120, 40));
		btnAddPatient.setText(Messages
				.getString("studyworkerwelcome.button.addpatient.text")); //$NON-NLS-1$
		btnAddPatient.setToolTipText(Messages
				.getString("studyworkerwelcome.button.addpatient.tooltip")); //$NON-NLS-1$
		btnAddPatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnAddPatient
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						AddPatientToStudy.addInitialisationOption(
								GenericFormGui.OPTION_isAddNotUpdate, true);
						new AddPatientToStudy(shell, true);
					}
				});
	}

	private void createUpdatePatientComp(Composite compOptions) {
		Label lblPicUpdatePatient = new Label(compOptions, SWT.NONE);
		lblPicUpdatePatient.setBounds(new Rectangle(30, 0, 50, 43));
		lblPicUpdatePatient.setImage(ResourceUtils
				.getImage(iDartImage.GENERALADMIN));
		lblPicUpdatePatient.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				AddPatient.addInitialisationOption(
						GenericFormGui.OPTION_isAddNotUpdate, false);
				new AddPatient(shell, false);
			}
		});

		Button btnUpdatePatient = new Button(compOptions, SWT.NONE);
		btnUpdatePatient.setBounds(new Rectangle(0, 50, 120, 40));
		btnUpdatePatient.setText(Messages
				.getString("studyworkerwelcome.button.updatepatient.text")); //$NON-NLS-1$
		btnUpdatePatient.setToolTipText(Messages
				.getString("studyworkerwelcome.button.updatepatient.tooltip")); //$NON-NLS-1$
		btnUpdatePatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnUpdatePatient
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						AddPatient.addInitialisationOption(
								GenericFormGui.OPTION_isAddNotUpdate, false);
						new AddPatient(shell, false);
					}
				});
	}

	@Override
	protected String getWelcomeLabelText() {
		return Messages.getString("studyworkerwelcome.screen.instructions"); //$NON-NLS-1$
	}

}
