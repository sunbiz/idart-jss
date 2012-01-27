package org.celllife.idart.database.wizard;

import org.celllife.idart.database.DatabaseTools;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CreateDatabasePage extends WizardPage implements PropertiesPage {
	private Composite container;
	private Button btnIncludeTest;

	public CreateDatabasePage() {
		super("Create Database");
		setTitle("Create Database");
		setDescription("The database seems to be empty.\nThis step will create the "
				+ "database tables and load the required data into it.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		Label lblIncludeTest = new Label(container, SWT.NULL);
		lblIncludeTest
		.setText("Would you like to include test data in the database?");

		btnIncludeTest = new Button(container, SWT.RADIO);
		btnIncludeTest.setText("Include test data");
		btnIncludeTest.setLayoutData(gd);
		btnIncludeTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(true);
			}
		});

		Button btnDontIncludeTest = new Button(container, SWT.RADIO);
		btnDontIncludeTest.setText("Don't include test data");
		btnDontIncludeTest.setLayoutData(gd);
		btnDontIncludeTest.setSelection(true);
		btnDontIncludeTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(true);
			}
		});

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(true);
	}

	@Override
	public boolean updateProperties() {
		return true;
	}

	@Override
	public boolean isRequired() {
		try {
			boolean checkData = !DatabaseTools._().checkDatabase();
			setPageComplete(!checkData);
			return checkData;
		} catch (Exception e) {
			return true;
		}
	}

	public boolean shouldIncludeTest() {
		return btnIncludeTest.getSelection();
	}
}
