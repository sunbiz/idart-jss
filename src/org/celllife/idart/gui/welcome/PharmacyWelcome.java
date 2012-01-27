package org.celllife.idart.gui.welcome;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.generalAdmin.GeneralAdmin;
import org.celllife.idart.gui.patientAdmin.PatientAdmin;
import org.celllife.idart.gui.reports.NewReports;
import org.celllife.idart.gui.stockControl.StockControl;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.Screens;
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
public class PharmacyWelcome extends GenericWelcome {

	public PharmacyWelcome() {
		super();
	}

	@Override
	protected String getWelcomeLabelText() {
		return Messages.getString("pharmacywelcome.screen.instructions"); //$NON-NLS-1$
	}

	@Override
	protected void createCompOptions(Composite compOptions) {
		// generalAdmin
		Label lblPicGeneralAdmin = new Label(compOptions, SWT.NONE);
		lblPicGeneralAdmin.setBounds(new Rectangle(40, 0, 50, 43));
		lblPicGeneralAdmin.setImage(ResourceUtils
				.getImage(iDartImage.GENERALADMIN));
		lblPicGeneralAdmin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new GeneralAdmin(shell);
			}
		});

		Button btnGeneralAdmin = new Button(compOptions, SWT.NONE);
		btnGeneralAdmin.setData(iDartProperties.SWTBOT_KEY, Screens.GENERAL_ADMIN.getAccessButtonId());
		btnGeneralAdmin.setBounds(new Rectangle(0, 50, 130, 40));
		btnGeneralAdmin.setText(Messages.getString("pharmacywelcome.button.generaladmin.text")); //$NON-NLS-1$
		btnGeneralAdmin
		.setToolTipText(Messages.getString("pharmacywelcome.button.generaladmin.tooltip")); //$NON-NLS-1$
		btnGeneralAdmin.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnGeneralAdmin
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				new GeneralAdmin(shell);
			}
		});

		// patientAdmin
		Label lblPicPatientAdmin = new Label(compOptions, SWT.NONE);
		lblPicPatientAdmin.setBounds(new Rectangle(200, 0, 50, 43));
		lblPicPatientAdmin.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTADMIN));
		lblPicPatientAdmin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new PatientAdmin(shell);
			}
		});

		Button btnPatientAdmin = new Button(compOptions, SWT.NONE);
		btnPatientAdmin.setData(iDartProperties.SWTBOT_KEY, Screens.PATIENT_ADMIN.getAccessButtonId());
		btnPatientAdmin.setBounds(new Rectangle(160, 50, 130, 40));
		btnPatientAdmin.setText(Messages.getString("pharmacywelcome.button.patientadmin.text")); //$NON-NLS-1$
		btnPatientAdmin
		.setToolTipText(Messages.getString("pharmacywelcome.button.patientadmin.tooltip")); //$NON-NLS-1$
		btnPatientAdmin.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPatientAdmin
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				new PatientAdmin(shell);
			}
		});

		// stockControl
		Label lblPicStockControl = new Label(compOptions, SWT.NONE);
		lblPicStockControl.setBounds(new Rectangle(360, 0, 50, 43));
		lblPicStockControl.setImage(ResourceUtils
				.getImage(iDartImage.STOCKCONTROL));
		lblPicStockControl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new StockControl();
			}
		});

		Button btnStockControl = new Button(compOptions, SWT.NONE);
		btnStockControl.setData(iDartProperties.SWTBOT_KEY, Screens.STOCK_CONTROL.getAccessButtonId());
		btnStockControl.setBounds(new Rectangle(320, 50, 130, 40));
		btnStockControl.setText(Messages.getString("pharmacywelcome.button.stockdispensing.text")); //$NON-NLS-1$
		btnStockControl
		.setToolTipText(Messages.getString("pharmacywelcome.button.stockdispensing.tooltip")); //$NON-NLS-1$
		btnStockControl.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnStockControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new StockControl();
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
		btnReports.setData(iDartProperties.SWTBOT_KEY, Screens.REPORTS.getAccessButtonId());
		btnReports.setText(Messages.getString("welcome.button.reports.text")); //$NON-NLS-1$
		btnReports.setBounds(new Rectangle(480, 50, 130, 40));
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
}
