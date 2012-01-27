package org.celllife.idart.gui.welcome;

import java.text.MessageFormat;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.gui.packaging.PackagesToOrFromClinic;
import org.celllife.idart.gui.packaging.PackagesToPatients;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.reports.NewReports;
import org.celllife.idart.gui.user.ManagePharmUsers;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 */
public class ClinicWelcome extends GenericWelcome {

	public ClinicWelcome() {
		super();
	}

	@Override
	protected String getWelcomeLabelText() {
		return MessageFormat.format(Messages
				.getString("clinicwelcome.screen.instructions"),
				LocalObjects.currentClinic.getClinicName()); //$NON-NLS-1$
	}

	@Override
	public void createCompOptions(Composite compOptions) {
		// generalAdmin
		Label lblPicManageUsers = new Label(compOptions, SWT.NONE);
		lblPicManageUsers.setBounds(new Rectangle(40, 0, 50, 43));
		lblPicManageUsers.setImage(ResourceUtils
				.getImage(iDartImage.PHARMACYUSER));
		lblPicManageUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {

				ManagePharmUsers.addInitialisationOption(
						GenericFormGui.OPTION_isAddNotUpdate, true);

				new ManagePharmUsers(shell);
			}
		});

		Button btnManageUsers = new Button(compOptions, SWT.NONE);
		btnManageUsers.setBounds(new Rectangle(0, 50, 130, 50));
		btnManageUsers.setText(Messages.getString("clinicwelcome.button.manageusers.text")); //$NON-NLS-1$
		btnManageUsers.setToolTipText(Messages
				.getString("clinicwelcome.button.manageusers.tooltip")); //$NON-NLS-1$
		btnManageUsers.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnManageUsers
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				ManagePharmUsers.addInitialisationOption(
						GenericFormGui.OPTION_isAddNotUpdate, true);

				new ManagePharmUsers(shell);
			}
		});

		// patientAdmin
		Label lblPicPackagesArrive = new Label(compOptions, SWT.NONE);
		lblPicPackagesArrive.setBounds(new Rectangle(200, 0, 50, 43));
		lblPicPackagesArrive.setText(""); //$NON-NLS-1$
		lblPicPackagesArrive.setImage(ResourceUtils
				.getImage(iDartImage.PACKAGESARRIVE));
		lblPicPackagesArrive.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				PackagesToOrFromClinic.addInitialisationOption("isScanOut", false); //$NON-NLS-1$
				new PackagesToOrFromClinic(shell);
			}
		});


		Button btnPackagesArrive = new Button(compOptions, SWT.NONE);
		btnPackagesArrive.setBounds(new Rectangle(160, 50, 130, 50));
		btnPackagesArrive.setText(Messages.getString("clinicwelcome.button.packagesarrive.text")); //$NON-NLS-1$
		btnPackagesArrive
		.setToolTipText(Messages.getString("clinicwelcome.button.packagesarrive.tooltip")); //$NON-NLS-1$
		btnPackagesArrive.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPackagesArrive
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				PackagesToOrFromClinic.addInitialisationOption("isScanOut", false); //$NON-NLS-1$
				new PackagesToOrFromClinic(shell);
			}
		});

		// stockControl
		Label lblPicPatientArrives = new Label(compOptions, SWT.NONE);
		lblPicPatientArrives.setBounds(new Rectangle(360, 0, 50, 43));
		lblPicPatientArrives.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTARRIVES));
		lblPicPatientArrives.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdScanToPatientSelected();
			}
		});


		Button btnPatientArrives = new Button(compOptions, SWT.NONE);
		btnPatientArrives.setBounds(new Rectangle(320, 50, 130, 50));
		btnPatientArrives.setAlignment(SWT.CENTER);
		btnPatientArrives.setText(Messages.getString("clinicwelcome.button.patientcollects.text")); //$NON-NLS-1$

		btnPatientArrives.setToolTipText(Messages
				.getString("clinicwelcome.button.patientcollects.tooltip")); //$NON-NLS-1$
		btnPatientArrives.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPatientArrives.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdScanToPatientSelected();
			}
		});

		// reports
		Label lblPicReports = new Label(compOptions, SWT.NONE);
		lblPicReports.setBounds(new Rectangle(520, 0, 50, 43));
		lblPicReports.setImage(ResourceUtils.getImage(iDartImage.REPORTS));

		lblPicReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new NewReports(shell);
			}
		});

		Button btnReports = new Button(compOptions, SWT.NONE);
		btnReports.setText(Messages.getString("welcome.button.reports.text")); //$NON-NLS-1$
		btnReports.setBounds(new Rectangle(480, 50, 130, 50));
		btnReports.setToolTipText(Messages
				.getString("welcome.button.reports.tooltip")); //$NON-NLS-1$
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

	private void cmdScanToPatientSelected() {
		PackagesToPatients.addInitialisationOption("isAtRemoteClinic", true); //$NON-NLS-1$
		new PackagesToPatients(shell);
	}

}
