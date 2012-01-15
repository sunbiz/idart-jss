package org.celllife.idart.gui.stockControl;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.packaging.PackagesToOrFromClinic;
import org.celllife.idart.gui.packaging.PackagesToPatients;
import org.celllife.idart.gui.platform.GenericAdminGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
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

public class ManageDownReferral extends GenericAdminGui {

	public ManageDownReferral(Shell parent) {
		super(parent);
	}

	@Override
	protected void cmdCloseSelectedWidget() {
		cmdCloseSelected();
	}

	@Override
	protected void createCompHeader() {
		String text = "Manage Packages for Down-Referral Clinic";
		iDartImage icoImage = iDartImage.STOCKCONTROL;
		buildCompHeader(text, icoImage);
	}

	@Override
	protected void createCompOptions() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.justify = true;
		compOptions.setLayout(rowLayout);

		GridLayout gl = new GridLayout(2, false);
		gl.verticalSpacing = 30;
		gl.marginTop = 70;
		Composite compOptionsInner = new Composite(compOptions, SWT.NONE);
		compOptionsInner.setLayout(gl);

		GridData gdPic = new GridData();
		gdPic.heightHint = 43;
		gdPic.widthHint = 50;

		GridData gdBtn = new GridData();
		gdBtn.heightHint = 40;
		gdBtn.widthHint = 360;

		// Scan out from pharmacy
		Label label = new Label(compOptionsInner, SWT.NONE);
		label.setLayoutData(gdPic);
		label.setImage(ResourceUtils.getImage(iDartImage.OUTGOINGPACKAGES));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdScanOutFromPharmacySelected();
			}
		});

		// Scan out from pharmacy
		Button button = new Button(compOptionsInner, SWT.NONE);
		button.setText("Scan Out Packages from Pharmacy");
		button.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		button.setLayoutData(gdBtn);
		button
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdScanOutFromPharmacySelected();
			}
		});
		if (!iDartProperties.downReferralMode.equalsIgnoreCase(null)) {
			// Scan in at Clinic
			label = new Label(compOptionsInner, SWT.NONE);
			label.setLayoutData(gdPic);
			label.setImage(ResourceUtils
					.getImage(iDartImage.PACKAGESARRIVE));
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent mu) {
					cmdScanInAtClinicSelected();
				}
			});

			// Scan in at Clinic
			button = new Button(compOptionsInner, SWT.NONE);
			button.setLayoutData(gdBtn);
			button.setText("Scan in Packages at Clinic");
			button.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
			button
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmdScanInAtClinicSelected();
				}
			});


		}

		// Scan out to patient
		label = new Label(compOptionsInner, SWT.NONE);
		label.setLayoutData(gdPic);
		label.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTARRIVES));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdScanToPatientSelected();
			}
		});

		// Scan out to patient
		button = new Button(compOptionsInner, SWT.NONE);
		button.setLayoutData(gdBtn);
		button
		.setText("Scan Out Packages to Patients at Clinic");
		button.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10));
		button
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdScanToPatientSelected();
			}
		});

		compOptions.layout();
		compOptionsInner.layout();
	}

	protected void cmdScanToPatientSelected() {
		PackagesToPatients.addInitialisationOption("isAtRemoteClinic", true);
		new PackagesToPatients(getShell());
	}

	protected void cmdScanInAtClinicSelected() {
		PackagesToOrFromClinic.addInitialisationOption("isScanOut", false);
		new PackagesToOrFromClinic(getShell());
	}

	protected void cmdScanOutFromPharmacySelected() {
		PackagesToOrFromClinic.addInitialisationOption("isScanOut", true);
		new PackagesToOrFromClinic(getShell());
	}

	@Override
	protected void createShell() {
		buildShell("Manage Packages for Down-Referral Clinic");
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));

	}

}
