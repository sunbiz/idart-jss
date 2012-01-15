package org.celllife.idart.gui.patient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import model.manager.PAVASManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.PatientStatTypes;
import org.celllife.idart.database.hibernate.PatientStatistic;
import org.celllife.idart.database.hibernate.PatientVisit;
import org.celllife.idart.database.hibernate.PatientVisitReason;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.reportParameters.CohortsReport;
import org.celllife.idart.gui.reportParameters.PatientStatsReport;
import org.celllife.idart.gui.reportParameters.PatientVisitsReport;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ShowPAVAS extends GenericFormGui {

	private Text txtName;
	private Text txtPatientId;
	private Text txtDOB;
	private Text txtAge;
	private Text txtSex;
	public static Table tblVisits;
	private TableColumn tableColVisit1;
	private TableColumn tableColVisit2;
	private TableColumn tableColVisit3;
	public static Table tblStats;
	private TableColumn tableColStat1;
	private TableColumn tableColStat2;
	private TableColumn tableColStat3;
	private TableColumn tableColStat4;

	// THE GROUPS

	private Group grpParticulars;
	private Group grpVisits;
	private Group grpStats;
	private Group grpBack;

	private Button btnSearch;
	private Button btnClear;
	private Button btnNewVisit;
	private Button btnNewStat;
	private Button btnEditVisit;
	private Button btnEditStat;
	private Button btnStatReport;
	private Button btnVisitsReport;
	private Button btnCohortsReport;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
	"dd MMM yyyy");
	// The Patient
	private Patient localPatient;
	private boolean isAddnotUpdate;

	// The lists

	private Composite compPatientInfo;

	public ShowPAVAS(Shell parent, Patient patient) {
		super(parent, HibernateUtil.getNewSession());
		localPatient = patient;
		loadPatientDetails();
	}

	public ShowPAVAS(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	@Override
	protected void createShell() {
		String shellTxt = Messages.getString("PatientStats.title"); //$NON-NLS-1$
		Rectangle bounds = new Rectangle(25, 0, 900, 700);

		buildShell(shellTxt, bounds);

	}

	/**
	 * This method initializes compHeader
	 * 
	 */

	@Override
	protected void createCompHeader() {

		String headerTxt = ("Patient Visits and Statistics Module");

		iDartImage icoImage = (iDartImage.PAVAS);

		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createContents() {
		{

			// here put the screen
			compPatientInfo = new Composite(getShell(), SWT.NONE);
			compPatientInfo.setBounds(new Rectangle(16, 55, 854, 694));

			grpParticulars = new Group(compPatientInfo, SWT.NONE);
			grpParticulars.setBounds(new Rectangle(34, 35, 785, 90));
			grpParticulars.setText("Patient Particulars");
			grpParticulars.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			grpStats = new Group(compPatientInfo, SWT.NONE);
			grpStats.setBounds(new Rectangle(444, 140, 375, 365));
			grpStats.setText("Patient Stats");
			grpStats.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			if (isAddnotUpdate == true) {

				isAddnotUpdate = !isAddnotUpdate;
			}

			Label lblSex = new Label(grpParticulars, SWT.NONE);
			lblSex.setBounds(new Rectangle(400, 60, 44, 20));
			lblSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblSex.setText("Sex:");
			txtSex = new Text(grpParticulars, SWT.BORDER);
			txtSex.setBounds(new Rectangle(450, 60, 80, 20));
			txtSex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtSex.setEnabled(false);

			Label lblAge = new Label(grpParticulars, SWT.NONE);
			lblAge.setBounds(new Rectangle(265, 60, 44, 20));
			lblAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblAge.setText("Age:");
			txtAge = new Text(grpParticulars, SWT.BORDER);
			txtAge.setBounds(new Rectangle(315, 60, 40, 20));
			txtAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtAge.setEnabled(false);

			Label lblDOB = new Label(grpParticulars, SWT.NONE);
			lblDOB.setBounds(new Rectangle(7, 60, 84, 20));
			lblDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblDOB.setText("Date of Birth:");
			txtDOB = new Text(grpParticulars, SWT.BORDER);
			txtDOB.setBounds(new Rectangle(100, 60, 150, 20));
			txtDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtDOB.setEnabled(false);

			Label lblName = new Label(grpParticulars, SWT.NONE);
			lblName.setBounds(new Rectangle(400, 25, 48, 20));
			lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblName.setText("Name:");
			txtName = new Text(grpParticulars, SWT.BORDER);
			txtName.setBounds(new Rectangle(450, 25, 200, 20));
			txtName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtName.setEnabled(false);

			// Patient ID
			Label lblPatientId = new Label(grpParticulars, SWT.NONE);
			lblPatientId.setBounds(new Rectangle(7, 25, 84, 20));
			lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$
			lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtPatientId = new Text(grpParticulars, SWT.BORDER);
			txtPatientId.setFocus();
			txtPatientId.setBounds(new Rectangle(100, 25, 150, 20));
			txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtPatientId.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if ((btnSearch != null) && (btnSearch.getEnabled())) {
						if ((e.character == SWT.CR)
								|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
							cmdSearchWidgetSelected();
						}
					}
				}
			});

			btnSearch = new Button(grpParticulars, SWT.NONE);
			btnSearch.setBounds(new Rectangle(270, 20, 110, 28));

			// if this is adding a new patient, set
			if (isAddnotUpdate) {
				btnSearch.setText("Check Patient Number");
				btnSearch
				.setToolTipText("Press this button to check if the patient number already exists in the database.");
			} else {
				btnSearch.setText("Patient Search");
				btnSearch
				.setToolTipText("Press this button to search for an existing patient.");
			}

			btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			btnSearch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmdSearchWidgetSelected();
				}
			});

			btnClear = new Button(grpParticulars, SWT.NONE);
			btnClear.setBounds(new Rectangle(670, 20, 90, 28));
			btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			btnClear.setText("Clear");
			btnClear.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmdClearWidgetSelected();
				}
			});

			grpVisits = new Group(compPatientInfo, SWT.NONE);
			grpVisits.setBounds(new Rectangle(34, 140, 375, 365));
			grpVisits.setText("Patient Visits");
			grpVisits.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			tblVisits = new Table(grpVisits, SWT.NONE);
			tblVisits.setBounds(new Rectangle(30, 30, 315, 280));
			tblVisits.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			tblVisits.setHeaderVisible(true);
			tblVisits.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					// tblSearchWidgetSelected();
					btnEditVisit.setEnabled(true);
				}
			});
			tableColVisit1 = new TableColumn(tblVisits, SWT.BORDER);
			tableColVisit1.setWidth(160);
			tableColVisit1.setText("Visit Date");
			tableColVisit2 = new TableColumn(tblVisits, SWT.BORDER);
			tableColVisit2.setWidth(155);
			tableColVisit2.setText("Visit Reason");
			tableColVisit3 = new TableColumn(tblVisits, SWT.BORDER);
			tableColVisit3.setWidth(0);
			tableColVisit3.setText("Visit ID");

			// put in patient visit reasons ordered by date desc

			tblStats = new Table(grpStats, SWT.NONE);
			tblStats.setBounds(new Rectangle(30, 30, 315, 280));
			tblStats.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			tblStats.setHeaderVisible(true);
			tblStats.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					// tblSearchWidgetSelected();
					btnEditStat.setEnabled(true);
				}
			});
			tableColStat1 = new TableColumn(tblStats, SWT.BORDER);
			tableColStat1.setWidth(130);
			tableColStat1.setText("Date Tested");
			tableColStat2 = new TableColumn(tblStats, SWT.BORDER);
			tableColStat2.setWidth(100);
			tableColStat2.setText("Type");
			tableColStat3 = new TableColumn(tblStats, SWT.BORDER);
			tableColStat3.setWidth(85);
			tableColStat3.setText("Value");
			tableColStat4 = new TableColumn(tblStats, SWT.BORDER);
			tableColStat4.setWidth(0);
			tableColStat4.setText("Stat ID");

			// put in patient statistics ordered by da

			btnNewVisit = new Button(grpVisits, SWT.NONE);
			btnNewVisit.setBounds(new Rectangle(30, 322, 150, 28));
			btnNewVisit.setText("New Visit");
			btnNewVisit.setEnabled(false);
			btnNewVisit
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_VisitAdd();

				}
			});

			btnEditVisit = new Button(grpVisits, SWT.NONE);
			btnEditVisit.setBounds(new Rectangle(195, 322, 150, 28));
			btnEditVisit.setText("Edit Visit");
			btnEditVisit.setEnabled(false);
			btnEditVisit
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_VisitEdit();

				}
			});

			btnNewStat = new Button(grpStats, SWT.NONE);
			btnNewStat.setBounds(new Rectangle(30, 322, 150, 28));
			btnNewStat.setText("New Statistic");
			btnNewStat.setEnabled(false);
			btnNewStat
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_StatAdd();
				}
			});

			btnEditStat = new Button(grpStats, SWT.NONE);
			btnEditStat.setBounds(new Rectangle(195, 322, 150, 28));
			btnEditStat.setText("Edit Statistic");
			btnEditStat.setEnabled(false);
			btnEditStat
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_StatEdit();
				}
			});

			Label lblStatReport = new Label(compPatientInfo, SWT.NONE);
			lblStatReport.setBounds(new Rectangle(610, 540, 50, 43));
			lblStatReport.setImage(ResourceUtils
					.getImage(iDartImage.PAVASSTATS));

			btnStatReport = new Button(compPatientInfo, SWT.NONE);
			btnStatReport.setBounds(new Rectangle(670, 540, 150, 40));
			btnStatReport.setText("Patient Statistics Report");
			btnStatReport
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_StatsReport();
				}
			});

			Label lblVisitReport = new Label(compPatientInfo, SWT.NONE);
			lblVisitReport.setBounds(new Rectangle(370, 540, 50, 43));
			lblVisitReport.setImage(ResourceUtils
					.getImage(iDartImage.PAVASVISITS));

			btnVisitsReport = new Button(compPatientInfo, SWT.NONE);
			btnVisitsReport.setBounds(new Rectangle(430, 540, 150, 40));
			btnVisitsReport.setText("Patient Visits Report");
			btnVisitsReport
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_VisitsReport();
				}
			});

			Label lblCohortsReport = new Label(compPatientInfo, SWT.NONE);
			lblCohortsReport.setBounds(new Rectangle(170, 540, 50, 43));
			lblCohortsReport.setImage(ResourceUtils.getImage(iDartImage.PAVAS));

			btnCohortsReport = new Button(compPatientInfo, SWT.NONE);
			btnCohortsReport.setBounds(new Rectangle(230, 540, 120, 40));
			btnCohortsReport.setText("Cohorts Report");
			btnCohortsReport
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmd_CohortsReport();
				}
			});

			buildCompBackButton();

		}
	}

	protected void buildCompBackButton() {
		grpBack = new Group(compPatientInfo, SWT.NONE);
		grpBack.setBounds(new Rectangle(34, 530, 100, 60));
		grpBack.setText("Patient Stats");
		grpBack.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Button btnBackButton = new Button(grpBack, SWT.NONE);
		btnBackButton.setBounds(new Rectangle(0, 0, 100, 60));
		btnBackButton
		.setToolTipText("Press this button to return to the Welcome Page.");
		btnBackButton.setImage(ResourceUtils.getImage(iDartImage.BACKARROW));
		btnBackButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseSelectedWidget();
			}
		});
	}

	protected void cmdCloseSelectedWidget() {
		closeShell(true);
	}

	// add on the reports buttons and the exit button

	@Override
	protected void createCompButtons() {

	}

	// initialise stats

	// exit button

	// reports buttons

	@Override
	protected void cmdCancelWidgetSelected() {
	}

	@Override
	protected void cmdClearWidgetSelected() {
		txtName.setText("");
		txtSex.setText("");
		txtAge.setText("");
		txtPatientId.setText("");
		txtDOB.setText("");
		tblVisits.removeAll();
		tblStats.removeAll();
		btnNewVisit.setEnabled(false);
		btnNewStat.setEnabled(false);
		btnEditVisit.setEnabled(false);
		btnEditStat.setEnabled(false);
		txtPatientId.setFocus();

	}

	@Override
	protected boolean fieldsOk() {
		return false;
	}

	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	@Override
	protected void enableFields(boolean enable) {

		// fieldsEnabled = enable;
	}

	@Override
	protected void clearForm() {
	}

	@Override
	protected void cmdSaveWidgetSelected() {
	}

	private void cmdSearchWidgetSelected() {
		
		String patientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText());
		
		PatientSearch search = new PatientSearch(getShell(), getHSession());
		search.setShowInactive(true);
		PatientIdentifier identifier = search.search(patientId);
		
		if (identifier != null) {
			localPatient = identifier.getPatient();
			updateGUIforNewLocalPatient();
		} else {
			clearForm();
		}
	}

	private void updateGUIforNewLocalPatient() {
		loadPatientDetails();

	}

	public void cmd_VisitAdd() {
		// PatientVisit(true) to ADD new PatientVisit
		PAVASVisits.addInitialisationOption(
				GenericFormGui.OPTION_isAddNotUpdate, true);
		new PAVASVisits(getShell(), localPatient, this);
	}

	public void cmd_VisitEdit() {
		// PatientVisit(false) to ADD new PatientVisit
		PAVASVisits.addInitialisationOption(
				GenericFormGui.OPTION_isAddNotUpdate, true);
		TableItem ti = new TableItem(tblVisits, SWT.NONE);
		ti = tblVisits.getItem(tblVisits.getSelectionIndex());
		int pv_id = Integer.parseInt(ti.getText(2));
		new PAVASVisits(getShell(), localPatient, this, pv_id);
	}

	public void cmd_StatAdd() {
		// AddPatientStatistic(true) to ADD new Patient Statistic
		PAVASStats.addInitialisationOption(
				GenericFormGui.OPTION_isAddNotUpdate, true);
		new PAVASStats(getShell(), localPatient, this);
	}

	public void cmd_StatEdit() {
		// PatientVisit(false) to ADD new PatientVisit
		PAVASStats.addInitialisationOption(
				GenericFormGui.OPTION_isAddNotUpdate, true);
		TableItem ti = new TableItem(tblStats, SWT.NONE);
		ti = tblStats.getItem(tblStats.getSelectionIndex());
		int entry_id = Integer.parseInt(ti.getText(3));
		new PAVASStats(getShell(), localPatient, this, entry_id);
	}

	public void cmd_StatsReport() {
		// Patient Statistics Report
		new PatientStatsReport(getShell(), true);
	}

	public void cmd_VisitsReport() {
		// PatientVisit(false) to ADD new PatientVisit
		new PatientVisitsReport(getShell(), true);
	}

	public void cmd_CohortsReport() {
		// PatientVisit(false) to ADD new PatientVisit
		new CohortsReport(getShell(), true);
	}

	public void loadPatientDetails() {
		// populate the GUI
		txtName.setText(localPatient.getFirstNames() + " "
				+ localPatient.getLastname());
		char sex = localPatient.getSex();
		if (Character.toUpperCase(sex) == 'F') {
			txtSex.setText("Female");
		} else if (Character.toUpperCase(sex) == 'M') {
			txtSex.setText("Male");
		} else {
			txtSex.setText("Unknown");
		}

		txtAge.setText(String.valueOf(localPatient.getAge()));

		txtPatientId.setText(localPatient.getPatientId());
		Calendar theDOB = Calendar.getInstance();
		theDOB.setTime(localPatient.getDateOfBirth());
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
		txtDOB.setText(String.valueOf(theDOB.get(Calendar.DAY_OF_MONTH)) + " "
				+ monthFormat.format(theDOB.getTime()) + " "
				+ String.valueOf(theDOB.get(Calendar.YEAR)));

		SetUpTables();

	}

	public void SetUpTables() {
		tblVisits.removeAll();
		List<PatientVisit> pv = PAVASManager.getVisitsforPatient(HibernateUtil
				.getNewSession(), localPatient.getId());

		for (int i = 0; i < pv.size(); i++) {
			String reason = "";

			List<PatientVisitReason> pvr = PAVASManager
			.getVisitReasons(HibernateUtil.getNewSession());
			for (int j = 0; j < pvr.size(); j++) {
				if (pv.get(i).getVisitReason() == pvr.get(j).getId()) {
					reason = pvr.get(j).getvisitreason();
				}
			}
			TableItem ti = new TableItem(tblVisits, SWT.NONE);
			ti.setText(0, dateFormatter.format(pv.get(i).getDateofVisit()));
			ti.setText(1, reason);
			ti.setText(2, "" + pv.get(i).getId());
		}

		tblStats.removeAll();
		List<PatientStatistic> ps = PAVASManager.getStatsforPatient(
				HibernateUtil.getNewSession(), localPatient.getId());

		for (int i = 0; i < ps.size(); i++) {
			String statname = "";

			List<PatientStatTypes> pst = PAVASManager
			.getStatTypes(HibernateUtil.getNewSession());
			for (int j = 0; j < pst.size(); j++) {
				if (ps.get(i).getstattype() == pst.get(j).getId()) {
					statname = pst.get(j).getstatname();
				}
			}
			TableItem ti = new TableItem(tblStats, SWT.NONE);
			ti.setText(0, dateFormatter.format(ps.get(i).getdatetested()));
			ti.setText(1, statname);
			ti.setText(2, ps.get(i).getstattext());
			ti.setText(3, "" + ps.get(i).getentry_id());
		}

		btnNewVisit.setEnabled(true);
		btnNewStat.setEnabled(true);
		btnEditVisit.setEnabled(false);
		btnEditStat.setEnabled(false);

	}

}
