package org.celllife.idart.gui.patient;

import java.util.List;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.welcome.GenericWelcome;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 */
public class AlternateIdSelector extends GenericOthersGui {

	private Table tblPatient;

	private Button btnCancel;

	private static Patient patientSelected;

	public AlternateIdSelector() {
		super(new Shell(), null);
	}

	/**
	 * Constructor for AlternateIdSelector.
	 * @param parent Shell
	 * @param theSearchString String
	 * @param possiblePatients List<Patient>
	 */
	public AlternateIdSelector(Shell parent, String theSearchString,
			List<Patient> possiblePatients) {
		super(parent, null);
		activate();
		createAlternateIdSelectorShell();
		populateTblPatient(theSearchString, possiblePatients);
		tblPatient.deselectAll();
		while (!getShell().isDisposed()) {
			if (!GenericWelcome.display.readAndDispatch())
				GenericWelcome.display.sleep();
		}
	}

	/**
	 * This method initializes sShell
	 */
	private void createAlternateIdSelectorShell() {

		tblPatient = new Table(getShell(), SWT.BORDER | SWT.FULL_SELECTION);
		tblPatient.setHeaderVisible(true);
		tblPatient.setLinesVisible(true);
		tblPatient.setBounds(new Rectangle(21, 30, 400, 204));
		tblPatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblPatient.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				tblPatientWidgetSelected();
			}
		});

		TableColumn tblColAltId = new TableColumn(tblPatient, SWT.NONE);
		tblColAltId.setWidth(120);
		tblColAltId.setText("Previous ID");

		TableColumn tblColCurrentId = new TableColumn(tblPatient, SWT.NONE);
		tblColCurrentId.setWidth(120);
		tblColCurrentId.setText("Current ID");

		TableColumn tblColPatientName = new TableColumn(tblPatient, SWT.NONE);
		tblColPatientName.setWidth(158);
		tblColPatientName.setText("Name");
	}

	/**
	 * Method populateTblPatient.
	 * @param theSearchString String
	 * @param possiblePatients List<Patient>
	 */
	private void populateTblPatient(String theSearchString,
			List<Patient> possiblePatients) {
		for (Patient p : possiblePatients) {
			TableItem ti = new TableItem(tblPatient, SWT.NONE);
			ti.setText(0, theSearchString);
			ti.setText(1, p.getPatientId());
			ti.setText(2, p.getLastname() + ", " + p.getFirstNames());
			ti.setData(p);
		}
	}

	private void tblPatientWidgetSelected() {
		if (tblPatient.getSelectionCount() == 1) {
			TableItem t[] = tblPatient.getSelection();
			patientSelected = (Patient) t[0].getData();
			getShell().dispose();
		}

	}

	private void cmdCancelWidgetSelected() {
		patientSelected = null;
		getShell().dispose();
	}

	/**
	 * Method getPatientSelected.
	 * @return Patient
	 */
	public Patient getPatientSelected() {
		return patientSelected;
	}

	@Override
	protected void createCompButtons() {
		btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				cmdCancelWidgetSelected();
			}
		});
	}

	@Override
	protected void createCompHeader() {

	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void createShell() {
		String shellTxt = "Select Patient By Previous Patient Number";
		Rectangle bounds = new Rectangle(0, 0, 450, 315);
		buildShell(shellTxt, bounds);
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
