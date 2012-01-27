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
package org.celllife.idart.gui.packaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import model.manager.PackageManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.facade.PackageReturnFacade;
import org.celllife.idart.gui.patient.EpisodeViewer;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

/**
 */
public class PackageReturn extends GenericFormGui {

	// Fields which can be manipulated
	private Text txtPatientId;
	private Text txtAdditionalNotes;
	private Button btnSearchPatient;
	private Button rbtnReturnToStock;
	private Button rbtnDestroyStock;
	private DateButton btnCaptureDate;
	private Table tblPackages;
	private TableColumn[] tblPackageCols;
	private CCombo cmbReturnReason;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
	private CCombo cmbStopEpisode;
	private Text txtStopNotes;
	private DateButton btnStopDate;
	private Button btnPreviousEpisodes;
	private Patient localPatient;
	private EpisodeViewer epiView;
	private final PackageReturnFacade packReturnFacade;
	private List<Packages> returnPacks;

	boolean episodeStopResonChanged = false;
	boolean episodeStopDateChanged = false;

	private static final String NOLONGER_TREATED_AT_CLINIC = "No longer receiving treatment at clinic";
	private static final String DRUG_CHANGE = "Change of Drugs";
	private static final String DRUG_LOST = "Package lost in transit";
	private static final String APPOINTMENT_MISSED = "Missed Appointment";

	/**
	 * Constructor for PackageReturn.
	 * 
	 * @param parent
	 *            Shell
	 */
	public PackageReturn(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		packReturnFacade = new PackageReturnFacade(getHSession());
	}

	@Override
	protected void createShell() {

		sdf = new SimpleDateFormat("dd MMM yy  hh:mm");
		String shellTxt = "Return Uncollected Packages to Pharmacy";
		Rectangle bounds = new Rectangle(0, 0, 800, 690);
		buildShell(shellTxt, bounds);
		createGrpScreenInfo();
		createGrpPatientId();
		createGrpPatientPrescriptions();
		createGrpPackageToReturn();
		txtPatientId.setFocus();
		// clearWidgetSelected();
		enableFields(false);
	}

	@Override
	protected void createCompHeader() {
		String headerTxt = "Return Uncollected Packages to Pharmacy";
		iDartImage icoImage = iDartImage.PACKAGERETURN;
		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	private void createGrpScreenInfo() {
		Composite grpScrInfo = new Composite(getShell(), SWT.BORDER);
		grpScrInfo.setBounds(75, 60, 645, 60);
		Label lblInfo = new Label(grpScrInfo, SWT.NONE);
		lblInfo.setBounds(5, 10, 630, 45);
		lblInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));
		// lblInfo.setAlignment(SWT.CENTER);
		String infoTxt = "   Use this screen to return an uncollected package that has been packed for a patient. Note that you will NOT be"
			+ "\n    deleting the record, you will be marking it as  \"Not Collected.\" If you want to delete this package permanently  "
			+ "\n  (eg. made a mistake on packaging screen), please go to the \"Stock, Prescription and Package Deletions\" screen.";
		lblInfo.setText(infoTxt);
	}

	private void createGrpPatientId() {
		Group grpPatientId = new Group(getShell(), SWT.NONE);
		grpPatientId.setBounds(155, 139, 450, 50);
		grpPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientId
		.setText(" Patient Search (only those with packages awaiting collection)");
		grpPatientId.setForeground(ResourceUtils.getColor(iDartColor.BLUE));

		Label lblPatientId = new Label(grpPatientId, SWT.NONE);
		lblPatientId.setBounds(30, 25, 60, 20);
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$

		txtPatientId = new Text(grpPatientId, SWT.BORDER);
		txtPatientId.setBounds(100, 20, 150, 20);
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if ((btnSearchPatient != null)
						&& (btnSearchPatient.getEnabled())) {
					if ((e.character == SWT.CR)
							|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {
						cmdSearchSelectedWidget();
					}
				}
			}
		});

		btnSearchPatient = new Button(grpPatientId, SWT.None);
		btnSearchPatient.setBounds(260, 17, 150, 25);
		btnSearchPatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearchPatient.setText("Patient Search");
		btnSearchPatient.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				// Ticket #647
				cmdSearchSelectedWidget();
			}
		});
	}

	private void createGrpPatientPrescriptions() {

		Group grpPatientPackage = new Group(getShell(), SWT.NONE);
		grpPatientPackage.setBounds(20, 208, 750, 158);
		grpPatientPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientPackage.setText("   Patient's Packages  ");

		Label lblInfo = new Label(grpPatientPackage, SWT.NONE);
		lblInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));
		lblInfo.setBounds(10, 25, 720, 35);
		String infoTxt = "The following packages have been "
			+ "created for this patient but have not as yet, been collected. \n "
			+ "To return a package click on the row in the table below.";
		lblInfo.setText(infoTxt);
		lblInfo.setAlignment(SWT.CENTER);

		tblPackages = new Table(grpPatientPackage, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER);
		tblPackages.setBounds(10, 62, 728, 90);
		tblPackages.setHeaderVisible(true);
		tblPackages.setLinesVisible(true);
		tblPackages.setItemCount(3);
		tblPackages.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		String[] titles = { "Package ID", "Drugs in Package", "Date Packed",
				"Left Pharmacy", "Received at Clinic" };
		tblPackageCols = new TableColumn[titles.length];
		for (int i = 0; i < titles.length; i++) {
			tblPackageCols[i] = new TableColumn(tblPackages, SWT.NONE, i);
			tblPackageCols[i].setText(titles[i]);
			tblPackageCols[i].setResizable(true);
		}

		// Creating just 3 table items for starters.
		tblPackages.removeAll();
		tblPackages.clearAll();
		tblPackages.redraw();
		tblPackages.setItemCount(0);
		tblPackageCols[0].setWidth(125);
		tblPackageCols[1].setWidth(220);
		tblPackageCols[2].setWidth(120);
		tblPackageCols[3].setWidth(120);
		tblPackageCols[4].setWidth(120);
	}

	private void createGrpPackageToReturn() {
		Group grpPatientPackage = new Group(getShell(), SWT.NONE);
		grpPatientPackage.setBounds(20, 380, 750, 228);
		grpPatientPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientPackage.setText("   Package to Return  ");

		Label lblReturnReason = new Label(grpPatientPackage, SWT.NONE);
		lblReturnReason.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblReturnReason.setText("Reason for Return : ");
		lblReturnReason.setBounds(10, 28, 110, 25);

		cmbReturnReason = new CCombo(grpPatientPackage, SWT.BORDER);
		cmbReturnReason.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbReturnReason.setBounds(120, 25, 250, 20);
		cmbReturnReason.setEditable(false);
		cmbReturnReason.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent ev) {

				// As Default, the return date is set to today.
				String selection = cmbReturnReason.getItem(
						cmbReturnReason.getSelectionIndex()).trim();

				if (selection.equalsIgnoreCase(NOLONGER_TREATED_AT_CLINIC)) {
					// Enable the Episode group;
					cmbStopEpisode.setText("");
					btnPreviousEpisodes.setEnabled(true);
					txtStopNotes.setText("");
					btnStopDate.setText("Stop Date");
					rbtnDestroyStock.setEnabled(true);
					rbtnReturnToStock.setEnabled(true);
					rbtnDestroyStock.setSelection(false);
					rbtnReturnToStock.setSelection(false);
					if (localPatient.getAccountStatusWithCheck()) {
						txtStopNotes.setEnabled(true);
						btnStopDate.setEnabled(true);
						cmbStopEpisode.setEnabled(true);
						cmbStopEpisode.setBackground(ResourceUtils
								.getColor(iDartColor.WHITE));
						cmbReturnReason.setBackground(ResourceUtils
								.getColor(iDartColor.WHITE));
					}
					return;
				} else if (selection.equalsIgnoreCase(DRUG_CHANGE)) {
					txtStopNotes.setEnabled(false);
					btnStopDate.setEnabled(false);
					cmbStopEpisode.setEnabled(false);
					cmbStopEpisode.setBackground(ResourceUtils
							.getColor(iDartColor.WIDGET_BACKGROUND));
					btnPreviousEpisodes.setEnabled(false);
					txtStopNotes.setText("");
					cmbStopEpisode.setText("");
					btnStopDate.setText("Stop Date");
					rbtnDestroyStock.setEnabled(true);
					rbtnReturnToStock.setEnabled(true);
					rbtnDestroyStock.setSelection(false);
					rbtnReturnToStock.setSelection(true);
					return;
				} else if (selection.equalsIgnoreCase(DRUG_LOST)) {
					txtStopNotes.setEnabled(false);
					btnStopDate.setEnabled(false);
					cmbStopEpisode.setEnabled(false);
					cmbStopEpisode.setBackground(ResourceUtils
							.getColor(iDartColor.WIDGET_BACKGROUND));
					btnPreviousEpisodes.setEnabled(false);
					txtStopNotes.setText("");
					cmbStopEpisode.setText("");
					btnStopDate.setText("Stop Date");
					rbtnDestroyStock.setEnabled(true);
					rbtnReturnToStock.setEnabled(false);
					rbtnDestroyStock.setSelection(true);
					rbtnReturnToStock.setSelection(false);

					return;
				} else if (selection.equalsIgnoreCase(APPOINTMENT_MISSED)) {
					txtStopNotes.setEnabled(false);
					btnStopDate.setEnabled(false);
					cmbStopEpisode.setEnabled(false);
					cmbStopEpisode.setBackground(ResourceUtils
							.getColor(iDartColor.WIDGET_BACKGROUND));
					btnPreviousEpisodes.setEnabled(false);
					txtStopNotes.setText("");
					cmbStopEpisode.setText("");
					btnStopDate.setText("Stop Date");
					rbtnDestroyStock.setEnabled(true);
					rbtnDestroyStock.setSelection(false);
					rbtnReturnToStock.setSelection(true);
					rbtnReturnToStock.setEnabled(true);
					return;

				} else if (selection.equalsIgnoreCase("")) { // Empty selection
					// disables the
					// episode
					// group items.
					txtStopNotes.setEnabled(false);
					btnStopDate.setEnabled(false);
					cmbStopEpisode.setEnabled(false);
					cmbStopEpisode.setBackground(ResourceUtils
							.getColor(iDartColor.WIDGET_BACKGROUND));
					btnPreviousEpisodes.setEnabled(false);
					txtStopNotes.setText("");
					cmbStopEpisode.setText("");
					btnStopDate.setText("Stop Date");
					rbtnDestroyStock.setEnabled(false);
					rbtnReturnToStock.setEnabled(false);
					rbtnDestroyStock.setSelection(false);
					rbtnReturnToStock.setSelection(true);
					return;
				} else {
					txtStopNotes.setEnabled(true);
					btnStopDate.setEnabled(true);
					cmbStopEpisode.setEnabled(true);
					cmbStopEpisode.setBackground(ResourceUtils
							.getColor(iDartColor.WHITE));
					btnPreviousEpisodes.setEnabled(true);
					txtStopNotes.setText("");
					cmbStopEpisode.setText("");
					btnStopDate.setText("Stop Date");
					rbtnDestroyStock.setEnabled(true);
					rbtnReturnToStock.setEnabled(true);
					rbtnDestroyStock.setSelection(true);
					rbtnReturnToStock.setSelection(false);
					return;

				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		populateCombobox();

		Label lblCaptureDate = new Label(grpPatientPackage, SWT.NONE);
		lblCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCaptureDate.setText("Date of Return :");
		lblCaptureDate.setBounds(10, 65, 100, 25);

		btnCaptureDate = new DateButton(
				grpPatientPackage,
				DateButton.ZERO_TIMESTAMP,
				new DateInputValidator(DateRuleFactory.beforeNowInclusive(true)));
		btnCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCaptureDate.setText("Date Button");
		btnCaptureDate.setBounds(120, 55, 250, 25);

		Label lblAdditionalNotes = new Label(grpPatientPackage, SWT.NONE);
		lblAdditionalNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblAdditionalNotes.setBounds(new Rectangle(400, 10, 120, 15));
		lblAdditionalNotes.setText("Additional Notes");

		txtAdditionalNotes = new Text(grpPatientPackage, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
		txtAdditionalNotes
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		txtAdditionalNotes.setBounds(400, 25, 340, 65);
		txtAdditionalNotes.setText("This feature is not yet available.");

		Label lblDrugAction = new Label(grpPatientPackage, SWT.NONE);
		lblDrugAction.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDrugAction.setText("What do you want to do with these drugs? ");
		lblDrugAction.setBounds(10, 100, 250, 25);

		Composite cmpRbtnSelect = new Composite(grpPatientPackage, SWT.NULL);
		cmpRbtnSelect.setLayout(new RowLayout());
		cmpRbtnSelect.setBounds(10, 125, 390, 50);

		rbtnDestroyStock = new Button(cmpRbtnSelect, SWT.RADIO);
		rbtnDestroyStock.setBounds(5, 30, 360, 20);
		rbtnDestroyStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rbtnDestroyStock
		.setText("Destroy the drugs (permanently removed from system).");
		rbtnDestroyStock.setEnabled(false);

		rbtnReturnToStock = new Button(cmpRbtnSelect, SWT.RADIO);
		rbtnReturnToStock.setBounds(5, 5, 370, 20);
		rbtnReturnToStock
		.setText("Return drugs to stock (can be redispensed to other patients).");
		rbtnReturnToStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rbtnReturnToStock.setEnabled(false);

		Label lblCmpEpisode = new Label(grpPatientPackage, SWT.NONE);
		lblCmpEpisode.setText(" Episode");
		lblCmpEpisode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCmpEpisode.setBounds(new Rectangle(400, 100, 120, 15));

		Composite cmpEpisode = new Composite(grpPatientPackage, SWT.NONE);
		cmpEpisode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmpEpisode.setBounds(400, 112, 340, 110);

		Label lblStopEpisode = new Label(cmpEpisode, SWT.NORMAL);
		lblStopEpisode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblStopEpisode.setText("Stop : ");
		lblStopEpisode.setBounds(5, 13, 40, 15);

		cmbStopEpisode = new CCombo(cmpEpisode, SWT.BORDER);
		cmbStopEpisode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbStopEpisode.setText("");
		cmbStopEpisode.setBounds(80, 10, 130, 20);
		CommonObjects
		.populateDeactivationReasons(getHSession(), cmbStopEpisode);
		cmbStopEpisode.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent se) {
				String reason = cmbStopEpisode.getText();
				if (reason.trim().length() > 0) {
					Date stopDate = new Date();
					btnStopDate.setDate((Date) stopDate.clone());
					episodeStopDateChanged = true;
					episodeStopResonChanged = true;
				}
			}
		});

		Label lblOn = new Label(cmpEpisode, SWT.NONE);
		lblOn.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblOn.setText(" on");
		lblOn.setBounds(212, 13, 20, 15);

		btnStopDate = new DateButton(
				cmpEpisode,
				DateButton.ZERO_TIMESTAMP,
				new DateInputValidator(DateRuleFactory.beforeNowInclusive(true)));
		btnStopDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnStopDate.setText("Stop Date");
		btnStopDate.setBounds(234, 07, 100, 25);
		btnStopDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				episodeStopDateChanged = true;
			}
		});

		Label lblStopNotes = new Label(cmpEpisode, SWT.NONE);
		lblStopNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblStopNotes.setText("Stop Notes : ");
		lblStopNotes.setBounds(5, 44, 70, 15);

		txtStopNotes = new Text(cmpEpisode, SWT.BORDER);
		txtStopNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtStopNotes.setBounds(80, 38, 255, 20);

		btnPreviousEpisodes = new Button(cmpEpisode, SWT.NONE);
		btnPreviousEpisodes
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPreviousEpisodes.setBounds(5, 70, 329, 32);
		btnPreviousEpisodes.setText("View All Previous Episodes");
		btnPreviousEpisodes.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent se) {
				// first check that there is consistency between the state of
				// episodes and the patient's account status
				if (localPatient.hasPreviousEpisodes()) {
					epiView = new EpisodeViewer(getHSession(), getShell(),
							localPatient, false);
					epiView.openViewer();
				} else {
					MessageBox noEpisodesWarning = new MessageBox(getShell(),
							SWT.ICON_ERROR | SWT.OK);
					noEpisodesWarning.setText("Patient Previous Episodes");
					noEpisodesWarning
					.setMessage("The patient "
							+ (txtPatientId.getText()).toUpperCase()
							+ " has no previous episodes to edit.\n"
							+ "Should you wish to Close the current episode, you may use\n "
							+ "this screen to assign the episode's Stop date for this patient.");
					noEpisodesWarning.open();
				}
			}
		});
	}

	protected void clearWidgetSelected() {
		txtPatientId.setText("");
		cmbReturnReason.setText("");
		cmbStopEpisode.setText("");
		// TODO: temporarily disables
		// txtAdditionalNotes.setText("");
		txtStopNotes.setText("");
		btnCaptureDate.setText("Date of Return");
		btnStopDate.setText("Stop Date");
		tblPackages.clearAll();
		tblPackages.removeAll();
		tblPackages.redraw();
		tblPackages.setItemCount(0);
		rbtnDestroyStock.setSelection(false);
		rbtnReturnToStock.setSelection(false);
	}

	/**
	 * Method populatePatientHistoryTable.
	 * 
	 * @param table
	 *            Table
	 * @param pat
	 *            Patient
	 */
	private void populatePatientHistoryTable(final Table table,
			final Patient pat) {
		table.clearAll();
		table.removeAll();
		table.redraw();
		table.setItemCount(0);
		tblPackageCols[0].setWidth(125);
		tblPackageCols[1].setWidth(220);
		tblPackageCols[2].setWidth(120);
		tblPackageCols[3].setWidth(120);
		tblPackageCols[4].setWidth(120);
		final List<Packages> packages = new ArrayList<Packages>();
		packages
		.addAll(packReturnFacade.getAllPackagesForPatient(localPatient));
		returnPacks = new ArrayList<Packages>();
		for (Packages pack : packages) {
			if (!pack.isPackageReturned()) {
				String contents = packReturnFacade
				.getPackageDrugsStringContent(pack);
				Date leftDte = pack.getDateLeft();
				String dte0 = sdf.format(pack.getPackDate());
				String dte1 = (leftDte == null ? "-" : sdf.format(leftDte));
				Date receivedDte = pack.getDateReceived();
				String dte2 = (receivedDte == null ? "-" : sdf
						.format(receivedDte));
				Date pickUpDte = pack.getPickupDate();
				String dte3 = (pickUpDte == null ? "-" : sdf.format(pickUpDte));
				if (!dte0.equals("-") && dte1.equals("-") && dte2.equals("-")
						&& dte3.equals("-")) {
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(0, pack.getPackageId());
					ti.setText(1, contents);
					// Date Packed
					ti.setText(2, dte0);
					ti.setData(pack);
					returnPacks.add(pack);
					// ti.getBounds(0).height = 100;
				} else if (!dte0.equals("-") /*
				 * && (dte1 != "-" | dte2 != "-")
				 */
						&& dte3.equals("-")) {
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(0, pack.getPackageId());
					ti.setText(1, contents);
					// Date Packed
					ti.setText(2, dte0);
					// Date Left
					ti.setText(3, dte1);
					// Date Received
					ti.setText(4, dte2);
					ti.getBounds(0).height = 100;
					returnPacks.add(pack); // Adding
					ti.setData(pack);

				}
			}
		}
		if (1 == table.getItems().length) {
			table.select(0);
		}
		// table.pack();
	}

	private void populateCombobox() {
		Object[] temp = PackageManager.getReturnReasons(getHSession())
		.toArray();

		String[] reasons = new String[temp.length];

		for (int i = 0; i < temp.length; i++) {
			reasons[i] = ((String) temp[i]).trim();
		}

		cmbReturnReason.setItems(reasons);

	}

	private void cmdSearchSelectedWidget() {

		String patientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText());
		
		PatientSearch search = new PatientSearch(getShell(), getHSession());
		search.setShowPatientsWithPackagesAwaiting(true);
		PatientIdentifier identifier = search.search(patientId);

		if (identifier != null) {
			txtPatientId.setText(identifier.getPatient().getPatientId());
			localPatient = identifier.getPatient();

			clearWidgetSelected();
			enableFields(true);
			txtPatientId.setText(localPatient.getPatientId());
			populatePatientHistoryTable(tblPackages, localPatient);
			enableSpecificControls(new Control[] { txtPatientId,
					btnSearchPatient, txtStopNotes, btnStopDate,
					cmbStopEpisode, btnPreviousEpisodes }, false);
			btnCaptureDate.setText(sdf.format(new Date()));
		} else {
			clearForm();
			txtPatientId.setFocus();
			txtPatientId.setText("");
		}
	}

	@Override
	protected void createContents() {
	}

	/**
	 * Method enableFields.
	 * 
	 * @param enable
	 *            boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
		txtPatientId.setEnabled(!enable);
		btnSearchPatient.setEnabled(!enable);
		cmbReturnReason.setEnabled(enable);
		txtStopNotes.setEnabled(enable);
		btnCaptureDate.setEnabled(enable);
		btnStopDate.setEnabled(enable);
		cmbStopEpisode.setEnabled(enable);

		// TODO: This field is permanently disabled until requirements have been
		// clarified
		txtAdditionalNotes.setEnabled(false);
		txtAdditionalNotes.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_BACKGROUND));
		txtAdditionalNotes.setForeground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));

		btnPreviousEpisodes.setEnabled(enable);
		btnSave.setEnabled(enable);
		rbtnDestroyStock.setEnabled(enable);
		rbtnReturnToStock.setEnabled(enable);
		if (enable) {
			// txtAdditionalNotes.setBackground(ResourceUtils
			// .getColor(iDartColor.WHITE));
			cmbReturnReason.setBackground(ResourceUtils
					.getColor(iDartColor.WHITE));
		} else {
			// txtAdditionalNotes.setBackground(ResourceUtils
			// .getColor(iDartColor.WIDGET_BACKGROUND));
			txtPatientId.setFocus();
			cmbStopEpisode.setBackground(ResourceUtils
					.getColor(iDartColor.WIDGET_BACKGROUND));
			cmbReturnReason.setBackground(ResourceUtils
					.getColor(iDartColor.WIDGET_BACKGROUND));
		}

	}

	/**
	 * Method fieldsOk.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {
		return false;
	}

	/**
	 * Method submitForm.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void clearForm() {

	}

	@Override
	protected void cmdCancelWidgetSelected() {
		closeShell(true);
	}

	@Override
	protected void cmdClearWidgetSelected() {
		clearWidgetSelected();
		enableFields(false);
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		String saveMsg = "To save this form, the following still needs to be done:\n";
		int msgInx = 0;

		if (tblPackages.getSelection().length <= 0) {
			msgInx++;
			saveMsg += "\n"
				+ msgInx
				+ ") Please select a package from the table\n so you can return it.";
		}

		// Checking if options were properly selected.
		if (cmbReturnReason.getText().equals("")) {
			msgInx++;
			saveMsg += "\n" + msgInx
			+ ") Please select a reason for returning a package.";
		} else if (localPatient.getAccountStatusWithCheck()) {
			if (cmbReturnReason.getText().equals(NOLONGER_TREATED_AT_CLINIC)) {
				if (cmbStopEpisode.getText().trim().equals("")) {
					msgInx++;
					saveMsg += "\n"
						+ msgInx
						+ ") Please choose the reason for stopping this episode from the Stop episode drop down list.";
				}

				if (!episodeStopDateChanged) {
					msgInx++;
					saveMsg += "\n"
						+ msgInx
						+ ") Please select a Date for stopping this Episode.";
				}

				if (btnCaptureDate.getText().equals("Date of Return")) {
					msgInx++;
					saveMsg += "\n" + msgInx
					+ ") Please select a Date of Return.";
				}
			}
		}

		if (!rbtnDestroyStock.getSelection()
				&& !rbtnReturnToStock.getSelection()) {
			msgInx++;
			saveMsg += "\n"
				+ msgInx
				+ ") Please choose what you want to do with the drugs: Return it or destroy it.";
		}

		if (!rbtnDestroyStock.getSelection()
				&& !rbtnReturnToStock.getSelection()) {
			msgInx++;
			saveMsg += "\n"
				+ msgInx
				+ ") Please choose what you want to do with the drugs: Return it or destroy it?";
		}

		if (tblPackages.getSelection().length > 0 && msgInx <= 0) {
			String confirmBlurb = "";
			TableItem ti = tblPackages.getItem(tblPackages.getSelectionIndex());
			String packId = ti.getText(0);
			String drugsInPack = ti.getText(1);
			Packages packToReturn = (Packages) ti.getData();

			StringTokenizer st = new StringTokenizer(drugsInPack, ",");
			// Building drug string
			String drugList = "\n";
			while (st.hasMoreElements()) {
				drugList += "\t*  " + st.nextToken().trim() + "\n";
			}
			String action = (rbtnDestroyStock.getSelection() ? "destroyed"
					: "returned to stock");
			confirmBlurb = "Are you sure you want to return package '" + packId
			+ "' to the Pharmacy? " + " This package contains:\n "
			+ drugList + "\nNOTE that the drugs will be " + action
			+ ".";
			/*
			 * if (cmbReturnReason.getText().equals( hashTblReasonsForReturn
			 * .get(enumReturnReasons.APPOINTMENT_MISSED))) { confirmBlurb += "
			 * This package return should only be done if patients missed (3) or
			 * more appointments."; }
			 */

			if (cmbReturnReason.getText().equals(NOLONGER_TREATED_AT_CLINIC)) {
				if (cmbStopEpisode.getText().equals("Unknown")) {
					confirmBlurb += "\n"
						+ " Make sure if the Episode \"Stop Reason\"\n\t should really be \"Unknown\". ";
				}
			}
			MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION
					| SWT.YES | SWT.NO);
			mb.setText("Confirm Package Return: " + cmbReturnReason.getText());
			mb.setMessage(confirmBlurb);
			if (mb.open() == SWT.YES) {
				cmdSavePackageReturn(action, packToReturn);
			} else {
				// Nothing, form stays the same.
			}
		}

		if (msgInx > 0) {
			MessageBox msg = new MessageBox(getShell(), SWT.DIALOG_TRIM);
			msg.setText("Form data status");
			msg.setMessage(saveMsg);
			msg.open();
		}

	}

	/**
	 * Method cmdSavePackageReturn.
	 * 
	 * @param action
	 *            String
	 * @param packageToReturn
	 *            Packages
	 */
	private void cmdSavePackageReturn(String action, Packages packageToReturn) {
		if (packageToReturn == null) {
			getLog().error("Unable to get package to return from database");
			MessageBox msg = new MessageBox(getShell(), SWT.DIALOG_TRIM);
			msg.setText("Error while returning package");
			msg
			.setMessage("An error has occured while trying to return the package");
			msg.open();
		} else {
			Transaction tx = null;
			try {
				tx = getHSession().beginTransaction();
				if (btnCaptureDate.getDate() == null) {
					btnCaptureDate.setDate(new Date());
				}
				packReturnFacade.returnPackage(packageToReturn,
						rbtnDestroyStock.getSelection(), btnCaptureDate
						.getDate(),
						cmbReturnReason.getText());

				// Checking if this episode has been closed
				// And then getting the latest episode and
				// closing it. Saving patient to database
				// with the closed episode;
				String stopReason = cmbStopEpisode.getText();
				String stopNotes = txtStopNotes.getText();
				if (episodeStopDateChanged) {
					Episode mostRecentEpisode = PatientManager
					.getMostRecentEpisode(localPatient);
					if (mostRecentEpisode.getId() > 0
							&& mostRecentEpisode.isOpen()) {
						packReturnFacade.closeEpisode(mostRecentEpisode,
								stopReason, btnStopDate.getDate(), stopNotes);
					}
				}

				tx.commit();
				getHSession().flush();

				// Message box shown after the information is saved to the
				// database
				MessageBox msg = new MessageBox(getShell(), SWT.DIALOG_TRIM);
				msg.setText("Package Returned Successfully.");
				msg
				.setMessage("The package was successfully returned to the pharmacy.");
				msg.open();

				closeShell(true);
			} catch (HibernateException e) {
				getLog().error(
						"Failure: Package was not " + action + " for package: "
						+ packageToReturn.getPackageId(), e);

				if (tx != null) {
					tx.rollback();
				}

				MessageBox msg = new MessageBox(getShell(), SWT.DIALOG_TRIM
						| SWT.ICON_ERROR);
				String errorAction = (action.equals("returned") ? "returned to pharmacy."
						: "destroyed.");
				msg.setText("Your package has not been " + action + ".");
				msg
				.setMessage("There was a problem when trying to save information about this "
						+ "package return to the database.\n\nThe package was not "
						+ errorAction);
				msg.open();
			}
		}

	}

	@Override
	protected void createCompButtons() {
		buildCompButtons();
		btnSave.setText("Return Uncollected Package");
		Rectangle bounds = btnSave.getBounds();
		bounds.x -= 11;
		bounds.width = 180;
		btnSave.setBounds(bounds);
		Rectangle bounds1 = getCompButtons().getBounds();
		bounds1.width += 100;
		getCompButtons().setBounds(bounds1);
		getCompButtons().pack();
	}
}
