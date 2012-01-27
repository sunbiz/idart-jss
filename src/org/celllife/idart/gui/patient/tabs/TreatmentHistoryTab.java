/**
 *
 */
package org.celllife.idart.gui.patient.tabs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.misc.GenericTab;
import org.celllife.idart.gui.patient.util.PatientTreatmentHistory;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.Session;

/**
 * Treatment history tab. Shows a table of all the packages a patient has
 * collected.
 */
public class TreatmentHistoryTab extends GenericTab implements IPatientTab {

	// private Button

	private Session hSession;

	private Patient localPatient;

	private TabFolder parent;

	private int style;

	private Table tblTreatmentHist;

	private TableColumn[] treatmentHistTableColumns;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#changesMade(org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public boolean changesMade(Patient patient) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#clear()
	 */
	@Override
	public void clear() {
		tblTreatmentHist.clearAll();
		tblTreatmentHist.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#create()
	 */
	@Override
	public void create() {
		setTabItem(new TabItem(parent, style));
		getTabItem().setText("  Treatment History  ");
		createGrpTreatmentHistory();
	}

	private void createGrpTreatmentHistory() {
		Group grpTreatmentHistory = new Group(getTabItem().getParent(),
				SWT.NONE);
		grpTreatmentHistory.setBounds(new Rectangle(3, 3, 750, 170));
		grpTreatmentHistory
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpTreatmentHistory.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_BACKGROUND));
		getTabItem().setControl(grpTreatmentHistory);

		tblTreatmentHist = new Table(grpTreatmentHistory, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);
		tblTreatmentHist.setHeaderVisible(true);
		tblTreatmentHist.setLinesVisible(true);
		tblTreatmentHist.setBounds(new Rectangle(3, 3, 770, 170));
		tblTreatmentHist.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		String[] titles = { "Date", "Drugs Received", "Days Elapsed",
		"Lowest Adh" };
		treatmentHistTableColumns = new TableColumn[titles.length];
		for (int i = 0; i < titles.length; i++) {
			treatmentHistTableColumns[i] = new TableColumn(tblTreatmentHist,
					SWT.NONE, i);
			treatmentHistTableColumns[i].setText(titles[i]);
			treatmentHistTableColumns[i].setResizable(true);

		}
		treatmentHistTableColumns[0].setWidth(100);
		treatmentHistTableColumns[1].setWidth(480);
		treatmentHistTableColumns[2].setWidth(95);
		treatmentHistTableColumns[3].setWidth(90);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#enable(boolean,
	 *      org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void enable(boolean enable, Color color) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#loadPatientDetails()
	 */
	@Override
	public void loadPatientDetails(Patient patient, boolean isPatientActive) {
		this.localPatient = patient;
		loadPatientTreatmentHistory(localPatient);
	}

	/**
	 * Loading the current treatment history for this patient
	 */
	private void loadPatientTreatmentHistory(final Patient patient) {
		parent.getShell().getDisplay().asyncExec(new Runnable(){
			@Override
			public void run() {
				List<String[]> treatmentHistory = new PatientTreatmentHistory(hSession,
						patient).getTreatmentHistoryRecordList();
				// Loading the information in the treatment table.
				for (String[] tiRecord : treatmentHistory) {
					TableItem ti = new TableItem(tblTreatmentHist, SWT.NONE);
					ti.setText(tiRecord);
					ti.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
					ti.setData(null);
				}
			}
		});
		
		// Resizing the Treatment History table
		treatmentHistTableColumns[0].setWidth(100);
		treatmentHistTableColumns[1].setWidth(480);
		treatmentHistTableColumns[2].setWidth(95);
		treatmentHistTableColumns[3].setWidth(90);
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
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#submit(org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public void submit(Patient patient) {
		// nothing to submit
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#validateFields()
	 */
	@Override
	public Map<String, String> validateFields(Patient patient) {
		// nothing to validate
		Map<String, String> map = new HashMap<String, String>();
		map.put("result", String.valueOf(true));
		map.put("title", "");
		map.put("message", "");
		localPatient = patient;
		return map;
	}
}