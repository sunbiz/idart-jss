package org.celllife.idart.gui.welcome;

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

public class ReportWorkerWelcome extends GenericWelcome {

	public ReportWorkerWelcome() {
		super();
	}
	
	
	@Override
	protected void createCompOptions(Composite compOptions) {

		overrideBtnLogLocation(compOptions, new Rectangle(325, 0, 50, 43) ,new Rectangle(300, 50, 100, 40));
		//Reports
		Label lblPicReports = new Label(compOptions, SWT.NONE);
		lblPicReports.setBounds(new Rectangle(175, 0, 50, 43));
		lblPicReports.setImage(ResourceUtils
				.getImage(iDartImage.GENERALADMIN));
		lblPicReports.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				new NewReports(shell);
			}
		});
		
		Button btnReports = new Button(compOptions, SWT.NONE);
		btnReports.setBounds(new Rectangle(150, 50, 100, 40));
		btnReports.setText(Messages.getString("studyworkerwelcome.button.reports.text")); //$NON-NLS-1$
		btnReports
		.setToolTipText(Messages.getString("studyworkerwelcome.button.reports.tooltip")); //$NON-NLS-1$
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

	@Override
	protected String getWelcomeLabelText() {
		return Messages.getString("reportworkerwelcome.screen.instructions"); //$NON-NLS-1$
	}

}
