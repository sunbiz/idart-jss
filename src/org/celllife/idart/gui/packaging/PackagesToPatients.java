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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.manager.AdherenceManager;
import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.reports.PackageProcessingReport;
import model.manager.reports.PatientHistoryReport;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.function.IRule;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.events.AdherenceEvent;
import org.celllife.idart.events.PackageEvent;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.reportParameters.PatientHistory;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateChangedEvent;
import org.celllife.idart.gui.widget.DateChangedListener;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.celllife.idart.misc.DateFieldComparator;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.model.utils.PackageLifeStage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import com.adamtaft.eb.EventBusService;

/**
 * @author Sarah
 * 
 *         Created on Jul 27, 2005
 * 
 */
public class PackagesToPatients extends GenericFormGui implements
iDARTChangeListener {

	private Group grpClinicSelection;

	private Group grpPackageLists;

	private Group grpScanPackageId;

	private Group grpLastPackageInfo;

	private Group grpPatientDetails;

	private Group grpThisPackageInfo;

	private Group grpNextAppointment;

	private Label lblInstructions;

	private Label lblClinic;

	private Label lblFolderNo;

	private Label lblName;

	private Label lblDOB;

	private Label lblAge;

	private Label lblLastPackageDate;

	private Label lblDaysSinceLastPickup;
	
	private Text searchBar;
	
	private Text txtFolderNo;

	private Text txtName;

	private Text txtDOB;

	private Text txtAge;

	private Text txtLastPackageDate;

	private Label lblContentsDaysSinceLastPickup;

	private CCombo cmbClinic;

	private DateButton btnCaptureDate;

	private Button btnPatientHistoryReport;

	private Button btnPackagesReport;

	private DateButton btnCollectionDateForNextPackage;

	private Label lblAwaiting;

	private List lstAwaiting;

	private java.util.List<Packages> packageList;

	private Text txtPackageIdScan;

	private Label lblScanned;

	private List lstScanned;

	private Table tblThisPackage;

	private Table tblLastPackage;

	private SimpleDateFormat dateFormatter;

	private TableEditor editorTblLastPackage;

	private TableEditor editorTblThisPackage;

	private Packages previousPack;

	private int index = -1;

	private boolean packageLoaded;

	private Packages scannedPack;

	private boolean isAtRemoteClinic;
	
	private String appointmentErrorMsg = "";

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public PackagesToPatients(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	/**
	 * This method initializes getShell()
	 */
	@Override
	protected void createShell() {
		dateFormatter = new SimpleDateFormat("dd MMM yyyy");
		String shellTxt = "Scan Out Packages To Patients";
		Rectangle bounds = new Rectangle(25, 0, 900, 700);
		buildShell(shellTxt, bounds);
		createClinicSelection();
		createGrpPackageLists();
		createGrpLastPackageInfo();
		createGrpPatientDetails();
		createGrpThisPackageInfo();
		createGrpNextAppointment();
		createGrpScanPackageId();
		txtPackageIdScan.setFocus();
	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String txt = "Scan Out Packages To Patients";
		iDartImage icoImage = iDartImage.PATIENTARRIVES;
		buildCompHeader(txt, icoImage);
	}

	/**
	 * This method initializes compClinicSelection
	 */
	private void createClinicSelection() {
		isAtRemoteClinic = (Boolean) getInitialisationOption("isAtRemoteClinic");
		grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setBounds(new Rectangle(160, 62, 579, 80));



		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.ONLINE_DOWNREFERRAL_MODE)
				&& !isAtRemoteClinic) {

			lblInstructions = new Label(grpClinicSelection, SWT.CENTER
					| SWT.WRAP);

			lblInstructions
			.setText("Use this screen for recording packages which are collected by patients at the Main Clinic. If the patient is collecting their drugs at a down referral clinic, you will need to log out of iDART, and log in to the down referral clinic. ");

			lblInstructions.setBounds(new Rectangle(2, 5, 575, 30));
			lblInstructions.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8_ITALIC));
		}



		lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(90, 45, 90, 20));
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblClinic.setText("Collections at ");

		cmbClinic = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbClinic.setBounds(new Rectangle(185, 45, 180, 20));
		cmbClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinic.setEditable(false);
		cmbClinic.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbClinic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				cmbClinicWidgetSelected();
			}
		});

		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.OFFLINE_DOWNREFERRAL_MODE)) {
			CommonObjects.populateClinics(getHSession(), cmbClinic, true);
		} else {
			CommonObjects.populateClinics(getHSession(), cmbClinic,
					!isAtRemoteClinic);

			if (!isAtRemoteClinic) {
				cmbClinic.setEnabled(false);
			}
		}

		if (cmbClinic.getEnabled()) {
			cmbClinic.setFocus();
		}

		lblClinic = new Label(grpClinicSelection, SWT.NONE);
		lblClinic.setBounds(new Rectangle(375, 45, 20, 20));
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblClinic.setText(" on ");

		btnCaptureDate = new DateButton(
				grpClinicSelection,
				DateButton.NONE,
				new DateInputValidator(DateRuleFactory.beforeNowInclusive(true)));
		btnCaptureDate.setDate(new Date());
		btnCaptureDate.setBounds(new Rectangle(400, 40, 100, 28));
		btnCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCaptureDate.setText(dateFormatter.format(btnCaptureDate.getDate()));
		btnCaptureDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDateExpectedFields();
			}
		});
		btnCaptureDate.addDateChangedListener(new DateChangedListener() {
			
			@Override
			public void dateChanged(DateChangedEvent event) {
				fireChangeEvent();
			}
		});
	}

	/**
	 * This method initializes grpPackageLists
	 */
	private void createGrpPackageLists() {

		grpPackageLists = new Group(getShell(), SWT.NONE);
		grpPackageLists.setText("Package Details");
		grpPackageLists.setBounds(new Rectangle(20, 141, 860, 475));
		grpPackageLists.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblAwaiting = new Label(grpPackageLists, SWT.CENTER | SWT.BORDER
				| SWT.V_SCROLL);
		lblAwaiting.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.OFFLINE_DOWNREFERRAL_MODE)) {
			lblAwaiting.setText("Packages Awaiting Collection");
		} else {
			lblAwaiting.setText("Packages Awaiting Dispatch");
		}
		lblAwaiting.setBounds(new Rectangle(10, 23, 180, 20));
		lstAwaiting = new List(grpPackageLists, SWT.BORDER | SWT.V_SCROLL);
		lstAwaiting.setBounds(new Rectangle(10, 63, 180, 406));
		// lstAwaiting.isFocusControl();

		searchBar = new Text(grpPackageLists, SWT.BORDER);
		searchBar.setText("patient search");
		searchBar.setBounds(new Rectangle(10, 43, 180, 20));
		searchBar.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		searchBar.setFocus();

		searchBar.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.character == SWT.CR) {
					searchBarEnter();
				}
			}
		});
		
		addSearchBarFocusListener();
		
		updatePackagesAwaitingDispatchListForClinic(cmbClinic.getText());
		
		addSearchBarKeyListener();
		
		lblScanned = new Label(grpPackageLists, SWT.BORDER | SWT.CENTER);
		lblScanned.setBounds(new Rectangle(670, 23, 180, 20));
		lblScanned.setText("Scanned Packages");
		lblScanned.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lstScanned = new List(grpPackageLists, SWT.BORDER | SWT.V_SCROLL);
		lstScanned.setBounds(new Rectangle(670, 43, 180, 426));

	}

	/**
	 * This method initializes grpScanPackageId
	 */
	private void createGrpScanPackageId() {

		grpScanPackageId = new Group(grpPackageLists, SWT.NONE);
		grpScanPackageId.setBounds(new Rectangle(200, 16, 180, 42));
		grpScanPackageId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpScanPackageId.setText("1. Scan Package ID");

		txtPackageIdScan = new Text(grpScanPackageId, SWT.BORDER);
		txtPackageIdScan.setBounds(new Rectangle(5, 15, 170, 20));
		txtPackageIdScan.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));


		/*
		 * txtPackageIdScan.addKeyListener(new KeyAdapter() {
		 * 
		 * @Override public void keyPressed(KeyEvent evt) { if (evt.character ==
		 * SWT.CR) { cmdEnterPressed(); } } });
		 */

		txtPackageIdScan.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.character == SWT.CR) {
					cmdEnterPressed();
				}

			}
		});

		txtPackageIdScan.setEnabled(true);
		txtPackageIdScan.selectAll();

	}

	/**
	 * This method initializes grpScanPackageId
	 */
	private void createGrpPatientDetails() {

		grpPatientDetails = new Group(grpPackageLists, SWT.NONE);
		grpPatientDetails.setBounds(200, 146, 460, 97); // must end at 637
		grpPatientDetails.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientDetails.setText("Patient Details");

		lblFolderNo = new Label(grpPatientDetails, SWT.NONE);
		lblFolderNo.setBounds(new Rectangle(15, 13, 72, 18));
		lblFolderNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblFolderNo.setText("Folder No:");

		txtFolderNo = new Text(grpPatientDetails, SWT.BORDER);
		txtFolderNo.setBounds(new Rectangle(15, 31, 105, 20));
		txtFolderNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtFolderNo.setEnabled(false);
		txtFolderNo.setEditable(false);

		lblName = new Label(grpPatientDetails, SWT.NONE);
		lblName.setBounds(new Rectangle(132, 13, 72, 18));
		lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblName.setText("Name:");

		txtName = new Text(grpPatientDetails, SWT.BORDER);
		txtName.setBounds(new Rectangle(130, 31, 205, 20));
		txtName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtName.setEnabled(false);
		txtName.setEditable(false);

		lblDOB = new Label(grpPatientDetails, SWT.NONE);
		lblDOB.setBounds(new Rectangle(342, 13, 72, 18));
		lblDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDOB.setText("Date of Birth:");

		txtDOB = new Text(grpPatientDetails, SWT.BORDER);
		txtDOB.setBounds(new Rectangle(342, 31, 105, 20));
		txtDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDOB.setEnabled(false);
		txtDOB.setEditable(false);

		lblAge = new Label(grpPatientDetails, SWT.NONE);
		lblAge.setBounds(new Rectangle(15, 53, 72, 15));
		lblAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblAge.setText("Age:");

		txtAge = new Text(grpPatientDetails, SWT.BORDER);
		txtAge.setBounds(new Rectangle(15, 70, 105, 20));
		txtAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtAge.setEnabled(false);
		txtAge.setEditable(false);

		lblLastPackageDate = new Label(grpPatientDetails, SWT.NONE);
		lblLastPackageDate.setBounds(new Rectangle(130, 53, 78, 15));
		lblLastPackageDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblLastPackageDate.setText("Last Pickup:");

		txtLastPackageDate = new Text(grpPatientDetails, SWT.BORDER);
		txtLastPackageDate.setBounds(new Rectangle(130, 70, 155, 20));
		txtLastPackageDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtLastPackageDate.setEnabled(false);
		txtLastPackageDate.setEditable(false);

		lblDaysSinceLastPickup = new Label(grpPatientDetails, SWT.RIGHT);
		lblDaysSinceLastPickup.setBounds(new Rectangle(230, 52, 122, 15));
		lblDaysSinceLastPickup.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblDaysSinceLastPickup.setText("days ago:");

		lblContentsDaysSinceLastPickup = new Label(grpPatientDetails,
				SWT.BORDER);
		lblContentsDaysSinceLastPickup
		.setBounds(new Rectangle(292, 70, 155, 20));
		lblContentsDaysSinceLastPickup.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes grpScanPackageId
	 */
	private void createGrpLastPackageInfo() {

		grpLastPackageInfo = new Group(grpPackageLists, SWT.NONE);
		grpLastPackageInfo.setBounds(new Rectangle(410, 16, 250, 130));
		grpLastPackageInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpLastPackageInfo.setText("2. Returned from Last Package");

		tblLastPackage = new Table(grpLastPackageInfo, SWT.FULL_SELECTION
				| SWT.BORDER);
		tblLastPackage.setHeaderVisible(true);
		tblLastPackage.setLinesVisible(true);
		tblLastPackage.setBounds(5, 20, 240, 100);

		tblLastPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn clmDrugName = new TableColumn(tblLastPackage, SWT.NONE);
		clmDrugName.setText("Drug Name");
		clmDrugName
		.setWidth(((tblLastPackage.getBounds().width - 20) * 65 / 100) - 10);
		clmDrugName.setResizable(true);

		TableColumn clmInHand = new TableColumn(tblLastPackage, SWT.NONE);
		clmInHand.setText("Accum");
		clmInHand
		.setWidth(((tblLastPackage.getBounds().width - 20) * 20 / 100) + 5);
		clmInHand.setResizable(true);

		TableColumn clmPercent = new TableColumn(tblLastPackage, SWT.NONE);
		clmPercent.setText("%");
		clmPercent.setWidth((tblLastPackage.getBounds().width - 20) * 15 / 100);
		clmPercent.setResizable(true);
		attachLastPackageTableEditor();

	}

	/**
	 * This method initializes grpScanPackageId
	 */
	private void createGrpThisPackageInfo() {

		grpThisPackageInfo = new Group(grpPackageLists, SWT.NONE);
		grpThisPackageInfo.setBounds(new Rectangle(200, 251, 460, 140));
		grpThisPackageInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpThisPackageInfo.setText("3. Drugs In This Package");

		tblThisPackage = new Table(grpThisPackageInfo, SWT.FULL_SELECTION
				| SWT.BORDER);
		tblThisPackage.setBounds(new Rectangle(10, 20, 440, 110));
		tblThisPackage.setHeaderVisible(true);
		tblThisPackage.setLinesVisible(true);
		tblThisPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn clmName = new TableColumn(tblThisPackage, SWT.NONE);
		clmName.setText("Drug Name");
		clmName.setWidth(160);
		clmName.setResizable(true);

		TableColumn clmQuantity = new TableColumn(tblThisPackage, SWT.NONE);
		clmQuantity.setText("Qty Disp");
		clmQuantity.setWidth(80);
		clmQuantity.setResizable(true);

		TableColumn clmAccum = new TableColumn(tblThisPackage, SWT.NONE);
		clmAccum.setText("Qty Accum");
		clmAccum.setWidth(80);
		clmAccum.setResizable(true);

		TableColumn clmInHand = new TableColumn(tblThisPackage, SWT.NONE);
		clmInHand.setText("In Hand on Exit");
		clmInHand.setWidth(120);
		clmInHand.setResizable(true);
		attachThisPackageTableEditor();

	}

	private void attachThisPackageTableEditor() {
		// add a editor for the labels and inHand columns, otherwise open
		// batchinformation when col clicked
		editorTblThisPackage = new TableEditor(tblThisPackage);
		editorTblThisPackage.horizontalAlignment = SWT.LEFT;
		editorTblThisPackage.grabHorizontal = true;

		tblThisPackage.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editorTblThisPackage.getEditor();
				if (old != null) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblThisPackage.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblThisPackage.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					if (column == 2) {
						// Create the combo box for the in hand column

						final CCombo combo = new CCombo(tblThisPackage,
								SWT.NONE);
						combo.setForeground(item.getForeground());
						combo.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						combo.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));

						combo.setText(item.getText(column));
						combo.setForeground(item.getForeground());
						combo.setFocus();
						combo.setEditable(false);

						editorTblThisPackage.minimumWidth = combo.getBounds().width;

						// Set the control into the editor
						editorTblThisPackage.setEditor(combo, item, column);

						final int col = column;

						String[] possibleValues = getPossibleAccumValues((java.util.List<PackagedDrugs>) item
								.getData());

						for (String s : possibleValues) {
							combo.add(s);
						}

						combo.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {

								item.setText(col, combo.getText());
								try {
									item
									.setText(
											3,
											""
											+ (Integer
													.parseInt(item
															.getText(1)) + Integer
															.parseInt(item
																	.getText(2))));
								} catch (NumberFormatException ne) {
									item.setText(3, item.getText(1));
								}
							}
						});

						combo.addFocusListener(new FocusListener() {
							@Override
							public void focusGained(FocusEvent event1) {

							}

							@Override
							public void focusLost(FocusEvent event1) {

								item.setText(col, combo.getText());
								combo.dispose();

							}
						});
					}
				}
			}
		});

	}

	public void attachLastPackageTableEditor() { // add a editor for the
		// accum column

		// add a editor for the accum column
		editorTblLastPackage = new TableEditor(tblLastPackage);
		editorTblLastPackage.horizontalAlignment = SWT.LEFT;
		editorTblLastPackage.grabHorizontal = true;

		tblLastPackage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editorTblLastPackage.getEditor();
				if (old != null) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblLastPackage.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblLastPackage.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					if ((column == 1) && (item.getData() != null)) {
						// Create the Text object for our editor

						final Text text = new Text(tblLastPackage, SWT.NONE);
						text.setForeground(item.getForeground());
						text.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));

						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						editorTblLastPackage.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editorTblLastPackage.setEditor(text, item, column);

						final int col = column;
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								String oldValue = item.getText(col);
								item.setText(col, text.getText());

								// check user input
								if ((!text.getText().trim().equals(""))
										&& (!text.getText().trim().equals(
												oldValue))) {

									try {
										int accum = Integer.parseInt(text
												.getText());
										int adherencePercent = AdherenceManager
										.getAdherencePercent(
												getHSession(), accum,
												((PillCount) item
														.getData()));
										item.setText(col + 1, ""
												+ adherencePercent);

										if (adherencePercent >= 95) {
											item
											.setBackground(
													col + 1,
													ResourceUtils
													.getColor(iDartColor.GREEN));
										} else if (adherencePercent >= 90) {
											item
											.setBackground(
													col + 1,
													ResourceUtils
													.getColor(iDartColor.YELLOW));

										} else {
											item
											.setBackground(
													col + 1,
													ResourceUtils
													.getColor(iDartColor.RED));

										}

										((PillCount) item.getData())
										.setAccum(accum);

										updateThisPackageAccumColumn((PillCount) item
												.getData());
										setInHandOnExit(((PillCount) item
												.getData()));

									} catch (NumberFormatException e) {
										java.awt.Toolkit.getDefaultToolkit()
										.beep();
										text.setText("");
										item.setText(col, "");
										((PillCount) item.getData())
										.setAccum(-1);
										item
										.setBackground(
												col + 1,
												ResourceUtils
												.getColor(iDartColor.LIST_BACKGROUND));

									}

								} else if (!text.getText().trim().equals(
										oldValue))

								{
									((PillCount) item.getData()).setAccum(-1);

								}

							}
						});

						text.addFocusListener(new FocusListener() {
							@Override
							public void focusLost(FocusEvent event1) {

								tblLastPackage.setSelection(tblLastPackage
										.getItemCount());
								text.dispose();

							}

							@Override
							public void focusGained(FocusEvent event1) {

							}
						});
					}
				}
			}
		});
	}

	/**
	 * Update the accum column for the packageddrug in this package that has the
	 * same drug as the pillcount returned from the previous package
	 * 
	 * @param pc
	 */
	@SuppressWarnings("unchecked")
	private void updateThisPackageAccumColumn(PillCount pc) {
		Drug theDrug = pc.getDrug();

		for (int i = 0; i < tblThisPackage.getItemCount(); i++) {

			if ((tblThisPackage.getItem(i).getData() != null)
					&& (theDrug.getId() == ((java.util.List<PackagedDrugs>) tblThisPackage
							.getItem(i).getData()).get(0).getStock().getDrug()
							.getId())) {
				try {
					if (pc.getAccum() == -1) {
						tblThisPackage.getItem(i).setText(2, "0");

					} else {
						if (iDartProperties.accumByDefault) {
							tblThisPackage.getItem(i).setText(2,
									"" + pc.getAccum());
						} else {
							tblThisPackage.getItem(i).setText(2, "0");
						}

					}

				} catch (NumberFormatException e) {
					getLog().error(
					"NumberFormatException parsing qty to dispense");
					tblThisPackage.getItem(i).setText(2, "0");
				}

			}

		}
	}

	/**
	 * For this package drug, get the possible accum values (usually {0, qty
	 * accum from last package}, or {-} if this drug is side treatment or was
	 * not in the last package)
	 * 
	 * @param pdList
	 *            java.util.List<PackagedDrugs>
	 * @return String[]
	 */
	private String[] getPossibleAccumValues(java.util.List<PackagedDrugs> pdList) {

		int disp = 0;

		String[] returnString = new String[2];

		for (TableItem ti : tblLastPackage.getItems()) {

			if (ti.getData() != null) {

				Drug lastPackDrug = ((PillCount) ti.getData()).getDrug();

				if (lastPackDrug.equals(pdList.get(0).getStock().getDrug())) {
					try {
						int accum = Double.valueOf(ti.getText(1)).intValue();

						if (accum != -1) {
							returnString[0] = "" + (disp + accum);
							returnString[1] = "0";
						}

					} catch (NumberFormatException e) {
						getLog().error(
						"qty to dispense entered is Not a number");

					}

				}

			}
		}

		if (returnString[0] == null) {
			returnString = new String[1];
			returnString[0] = "-";

		}
		return returnString;
	}

	/**
	 * This method initializes grpScanPackageId
	 */
	private void createGrpNextAppointment() {

		grpNextAppointment = new Group(grpPackageLists, SWT.NONE);
		grpNextAppointment.setBounds(new Rectangle(200, 400, 273, 60));
		grpNextAppointment.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		String text = "4. Next Collection Date";
		grpNextAppointment.setText(text);

		// Construction of SWTCalenda
		IRule<Date> rule = DateRuleFactory.afterInclusive(btnCaptureDate.getDate(), true);
		String errorMessage = "Patient "+ txtFolderNo.getText() + " is collecting their current package on " +
				btnCaptureDate.getText() + ".\n\nTheir next appointment date must be after this date.";
		rule.setDescription(errorMessage);
		DateInputValidator dateValidator = new DateInputValidator(rule);
		btnCollectionDateForNextPackage = new DateButton(grpNextAppointment,
				DateButton.ZERO_TIMESTAMP, dateValidator);
		btnCollectionDateForNextPackage.setErrorMessageTitle("Invalid Appointment Date");
		btnCollectionDateForNextPackage.setDate(new Date());
		btnCollectionDateForNextPackage.setBounds(33, 20, 200, 30);
		btnCollectionDateForNextPackage.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		btnCollectionDateForNextPackage.setVisible(false);

		// lblPicPatientHistoryReport
		btnPatientHistoryReport = new Button(grpPackageLists, SWT.NONE);
		btnPatientHistoryReport.setBounds(new Rectangle(503, 415, 50, 43));
		btnPatientHistoryReport
		.setToolTipText("Press this button to view and / or print reports \nof patients' Prescription History.");
		btnPatientHistoryReport.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PATIENTHISTORY));

		btnPatientHistoryReport.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdPatientHistoryWidgetSelected();
			}
		});

		// lblPicPackagesReport
		btnPackagesReport = new Button(grpPackageLists, SWT.NONE);
		btnPackagesReport.setBounds(new Rectangle(568, 415, 50, 43));
		btnPackagesReport
		.setToolTipText("Press this button to view and / or print Scanned Out Packages Report.");
		btnPackagesReport.setImage(ResourceUtils.getImage(iDartImage.REPORTS));

		btnPackagesReport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdPackagesReportWidgetSelected();
			}
		});
	}

	/*
	 * Sets the in hand on exit column in a current package table
	 * 
	 * theCurrentPackageTableToUpdate must have PackagedDrugs as the data of its
	 * TableItems
	 * 
	 * @param accum @param pc
	 */
	/**
	 * Method setInHandOnExit.
	 * 
	 * @param pc
	 *            PillCount
	 */
	@SuppressWarnings("unchecked")
	public void setInHandOnExit(PillCount pc) {

		if (tblThisPackage != null) {
			Drug theDrug = pc.getDrug();

			for (int i = 0; i < tblThisPackage.getItemCount(); i++) {

				TableItem ti = tblThisPackage.getItem(i);

				if (theDrug.equals((((java.util.List<PackagedDrugs>) ti
						.getData()).get(0)).getStock().getDrug())) {
					try {
						int qty = Integer.parseInt(tblThisPackage.getItem(i)
								.getText(1));

						if (pc.getAccum() == -1) {
							tblThisPackage.getItem(i).setText(2, "");
							tblThisPackage.getItem(i).setText(3, "" + qty);

						} else {
							if (iDartProperties.accumByDefault) {
								tblThisPackage.getItem(i).setText(2,
										"" + pc.getAccum());
								tblThisPackage.getItem(i).setText(3,
										"" + (pc.getAccum() + qty));
							} else {
								tblThisPackage.getItem(i).setText(2, "");
								tblThisPackage.getItem(i).setText(3, "" + qty);
							}

						}

					} catch (NumberFormatException e) {
						getLog()
						.error(
						"NumberFormatException parsing qty to dispense");
						tblThisPackage.getItem(i).setText(2, "");
					}

				}

			}
		}
	}

	/**
	 * This method initializes compButtons
	 */
	@Override
	protected void createCompButtons() {
		buildCompButtons();
		btnSave.setEnabled(false);
	}

	/**
	 * This method is called when the user when anything is entered into the
	 * text field for the package id. If that key press is "Enter" and all error
	 * checks are passed, the package is updated in the database, and so are the
	 * 2 lists on the GUI
	 * 
	 * This screen is for scanning packages to patients, so when the package is
	 * scanned, all dates are updated.
	 * 
	 */
	private void cmdEnterPressed() {
		try {

			txtPackageIdScan.setText(txtPackageIdScan.getText().toUpperCase());
			index = lstAwaiting.indexOf(txtPackageIdScan.getText());
			if (index != -1) {
				packageLoaded = true;
				Runnable longJob = new Runnable() {
					@Override
					public void run() {
						scannedPack = packageList.get(lstAwaiting
								.indexOf(txtPackageIdScan.getText()
										.toUpperCase()));
						populatePatientDetails(scannedPack);
						populateLastPackageDetails(scannedPack
								.getPrescription().getPatient());
						populateThisPackageDetails(scannedPack);
						// enable the fields
						enableFields(true);
						SimpleDateFormat sdf = new SimpleDateFormat(
						"dd MMM yyyy");
						Date tmpBtnDate = null;
						try {
							tmpBtnDate = sdf.parse(btnCaptureDate.getText());
						} catch (ParseException e) {
							getLog().error("Problem parsing pack date", e);
						}
						if (tmpBtnDate != null) {
							btnCaptureDate.setDate(tmpBtnDate);
						} else {
							btnCaptureDate.setDate(new Date());
						}
						setDateExpectedFields();
					}
				};
				BusyIndicator.showWhile(getShell().getDisplay(), longJob);
				txtPackageIdScan.setEnabled(false);

			} else {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_ERROR);
				m.setText("Package Not Found");
				m
				.setMessage("Package '"
						+ txtPackageIdScan.getText()
						+ "' was not found in the list of packages awaiting dispatch.");
				m.open();

				txtPackageIdScan.setFocus();
				txtPackageIdScan.selectAll();

			}

		} catch (Exception e) {
			getLog().error(
					"Problem when setting dates after patient selection.", e);
		}
	}

	/**
	 * This method is called when the user chooses a clinic from the drop down
	 * list. Doing this, updates the list of packages awaiting to be dispatched
	 * from the pharmacy.
	 * 
	 */
	private void cmbClinicWidgetSelected() {
		updatePackagesAwaitingDispatchListForClinic(cmbClinic.getText());
		txtPackageIdScan.setEnabled(true);
		txtPackageIdScan.setFocus();
	}

	/**
	 * Method populateTblLastPackage.
	 * 
	 * @param previousPack
	 *            Packages
	 */
	private void populateTblLastPackage() {
		if (tblLastPackage != null) {
			tblLastPackage.clearAll();
			tblLastPackage.removeAll();
		}
		java.util.Set<PillCount> previousPackPillCounts = previousPack
		.getPillCounts();

		java.util.List<PackagedDrugs> packagedDrugsList = previousPack
		.getPackagedDrugs();
		Iterator<PackagedDrugs> packDrugIt = packagedDrugsList.iterator();
		tblLastPackage.removeAll();
		// keep track of all drugs already in the list
		java.util.List<Drug> usedDrugs = new ArrayList<Drug>();
		while (packDrugIt.hasNext()) {
			PackagedDrugs pd = packDrugIt.next();
			if (pd != null) {
				Drug d = pd.getStock().getDrug();
				if ((d.getSideTreatment() == 'F') && (!usedDrugs.contains(d))) {
					TableItem ti = new TableItem(tblLastPackage, SWT.NONE);
					if (tblLastPackage.getBounds().width < 300) {
						ti.setText(0, DrugManager.getShortGenericDrugName(d, true));
					} else {
						ti.setText(0, d.getName());
					}
					ti.setData("drugName", d.getName());
					// search for existing pillcounts
					Iterator<PillCount> previousPackPillCountsItr = previousPackPillCounts
					.iterator();
					while (previousPackPillCountsItr.hasNext()) {
						PillCount pc = previousPackPillCountsItr.next();
						if (pc.getDrug().equals(d)) {
							pc.setDateOfCount(new Date());
							ti.setData(pc);
							ti.setText(1, "" + pc.getAccum());
						}
					}
					if (ti.getData() == null) // didn't find a
						// previous
						// pillcount
					{
						ti.setData(new PillCount(-1, previousPack, new Date(),
								d));
					}
					usedDrugs.add(d);
				} else if ((d.getSideTreatment() == 'T')
						&& (!usedDrugs.contains(d))) {
					TableItem ti = new TableItem(tblLastPackage, SWT.NONE);
					ti.setText(0, d.getName());
					ti.setFont(ResourceUtils
							.getFont(iDartFont.VERASANS_8_ITALIC));
					ti.setData(null);
					usedDrugs.add(d);
				}
			}
		}
	}

	/**
	 * This method is called when the user pressed the "Close" button. It closes
	 * the active window
	 * 
	 */
	@Override
	protected void cmdCancelWidgetSelected() {
		closeShell(true);
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		if (submitForm()) {
			// set focus to the packageIDScan textField so that the user can
			// scan in another package immediately
			scannedPack = null;
			// txtPackageIdScan = null;
			// txtPackageIdScan = new Text(grpScanPackageId, SWT.BORDER);
			// txtPackageIdScan.setBounds(new Rectangle(5, 15, 170, 20));
			// txtPackageIdScan.setFont(ResourceUtils
			// .getFont(iDartFont.VERASANS_8));
			// // txtPackageIdScan.addListener(SWT.KeyDown, new KeyAdapter(){});
			//
			// txtPackageIdScan.addKeyListener(new KeyAdapter() {
			// @Override
			// public void keyPressed(KeyEvent evt) {
			// if (evt.character == SWT.CR) {
			// cmdEnterPressed();
			// }
			// }
			// });

			txtPackageIdScan.setFocus();
			txtPackageIdScan.setEnabled(true);
			txtPackageIdScan.selectAll();
		}

	}

	/**
	 * This method updates the two lists: - packages waiting to be dispatched,
	 * and - packages that have been scanned out of the pharmacy
	 * 
	 * @param index
	 *            int
	 */
	private void updateLists() {

		String move = lstAwaiting.getItem(index);
		lstScanned.add(move);
		lstAwaiting.remove(index);
		// Hack to synchronise the 2 lists.
		packageList.remove(scannedPack);

	}

	/**
	 * This method populates the package details
	 * 
	 * @param pack
	 *            Packages
	 */
	private void populatePatientDetails(Packages pack) {

		Patient pat = pack.getPrescription().getPatient();
		txtFolderNo.setText(pat.getPatientId());
		txtName.setText(pat.getLastname() + ", " + pat.getFirstNames());
		txtDOB.setText(dateFormatter.format(pat.getDateOfBirth()));
		txtAge.setText(pat.getAge() + " years");

		txtLastPackageDate.setText("");

	}

	/**
	 * Method populateLastPackageDetails.
	 * 
	 * @param pat
	 *            Patient
	 */
	private void populateLastPackageDetails(Patient pat) {
		previousPack = PackageManager
		.getLastPackagePickedUp(getHSession(), pat);

		if (previousPack != null) {

			// Check if package was created but not picked up
			if (previousPack.getPickupDate() == null) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Cannot Dispense to Patient");
				m
				.setMessage("You cannot dispense to patient "
						+ previousPack.getPrescription().getPatient()
						.getPatientId()
						+ " since a package has already been made on "
						+ dateFormatter.format(previousPack
								.getPackDate())
								+ " for this patient. "
								+ "\nThis package has not yet been collected by the "
								+ "patient. \n\n If this package is correct, please scan "
								+ "it out to the patient using the 'Scan Out Packages "
								+ "to Patients' screen. \n\nIf this package is NOT correct, "
								+ "please delete it using the 'Stock, Prescription & "
								+ "Package Deletions' screen.");

				m.open();
			}

			else {
				btnCaptureDate.setDate(new Date());
				adjustDaysSince();

				populateTblLastPackage();
			}

		} else {
			txtLastPackageDate.setText("initial pickup");
			lblContentsDaysSinceLastPickup.setText("");
			lblContentsDaysSinceLastPickup.setForeground(ResourceUtils
					.getColor(iDartColor.BLACK));

		}
	}

	/**
	 * Method populateThisPackageDetails.
	 * 
	 * @param pack
	 *            Packages
	 */
	@SuppressWarnings("unchecked")
	private void populateThisPackageDetails(Packages pack) {

		java.util.List<Drug> usedDrugs = new ArrayList<Drug>();

		for (PackagedDrugs pd : pack.getPackagedDrugs()) {

			if (!usedDrugs.contains(pd.getStock().getDrug())) {
				TableItem ti = new TableItem(tblThisPackage, SWT.NONE);
				ti.setText(0, pd.getStock().getDrug().getName());
				ti.setText(1, pd.getAmount() + "");
				ti.setText(3, pd.getAmount() + "");

				java.util.List<PackagedDrugs> pdList = new ArrayList<PackagedDrugs>();
				pdList.add(pd);
				ti.setData(pdList);

				if (pd.getStock().getDrug().getSideTreatment() == 'F') {
					boolean drugWasInLastPack = false;
					for (TableItem pillcountTi : tblLastPackage.getItems()) {
						if ((pillcountTi.getData() != null)
								&& ((((PillCount) pillcountTi.getData())
										.getDrug()).equals(pd.getStock()
												.getDrug()))) {
							drugWasInLastPack = true;
							break;
						}

					}

					if (!drugWasInLastPack) {
						ti.setText(2, "-");
						ti.setText(3, pd.getAmount() + "");
					}

				}
			}

			else {
				for (TableItem ti : tblThisPackage.getItems()) {
					if (((java.util.List<PackagedDrugs>) ti.getData()).get(0)
							.getStock().getDrug().equals(
									pd.getStock().getDrug())) {
						try {
							int currentDisp = Integer.parseInt(ti.getText(1));
							currentDisp += pd.getAmount();
							ti.setText(1, currentDisp + "");
							((java.util.List<PackagedDrugs>) ti.getData())
							.add(pd);
						} catch (NumberFormatException ne) {

						}
					}
				}
			}
		}
	}

	/**
	 * This method gets all the packages that are awaiting dispatch for a given
	 * clinic, and adds these packages to the GUI-list.
	 * 
	 * 
	 * @param clinic
	 */
	private void updatePackagesAwaitingDispatchListForClinic(String clinic) {

		this.lstAwaiting.removeAll();

		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.OFFLINE_DOWNREFERRAL_MODE)) {

			packageList = PackageManager.getPackagesAwaitingCollection(
					getHSession(), clinic);

		}

		else {
			if (isAtRemoteClinic) {
				packageList = PackageManager.getPackagesAwaitingCollection(
						getHSession(), clinic);
			} else {
				packageList = PackageManager.getPackagesAwaitingScanOut(
						getHSession(), clinic);
			}
		}


		for (int i = 0; i < packageList.size(); i++) {
			Packages p = packageList.get(i);
			lstAwaiting.add(p.getPackageId());
		}

	}
	
	private void updatePackagesAwaitingDispatchListForPatient(String patientId){
		
		this.lstAwaiting.removeAll();
		packageList = PackageManager.getAllWaitingPackagesForPatientWithIdLike(getHSession(), patientId);
		
		for (int i = 0; i < packageList.size(); i++) {
			Packages p = packageList.get(i);
			lstAwaiting.add(p.getPackageId());
		}
	}

	/**
	 * Method enableFields.
	 * 
	 * @param enable
	 *            boolean
	 */
	@Override
	protected void enableFields(boolean enable) {

		// cmbYear.setVisible(enable);
		// cmbMonth.setVisible(enable);
		// cmbDay.setVisible(enable);
		btnCollectionDateForNextPackage.setVisible(enable);
		// txtFolderNo.setEnabled(enable);
		// txtName.setEnabled(enable);
		// txtDOB.setEnabled(enable);
		// txtAge.setEnabled(enable);
		// txtLastPackageDate.setEnabled(enable);
		btnSave.setEnabled(enable);
	}

	@Override
	protected void cmdClearWidgetSelected() {
		enableFields(false);
		packageLoaded = false;
		btnCaptureDate.setDate(null);
		btnCollectionDateForNextPackage.setDate(null);
		previousPack = null;

		// clear tables and table editors
		tblLastPackage.clearAll();
		tblThisPackage.clearAll();
		tblLastPackage.removeAll();
		tblThisPackage.removeAll();
		txtPackageIdScan.setText("");
		txtFolderNo.setText("");
		txtName.setText("");
		txtDOB.setText("");
		txtAge.setText("");
		txtLastPackageDate.setText("");
		lblContentsDaysSinceLastPickup.setText("");
		txtPackageIdScan.setEnabled(true);

		Control old = editorTblLastPackage.getEditor();
		if (old != null) {
			old.dispose();
		}

		Control old2 = editorTblThisPackage.getEditor();
		if (old2 != null) {
			old2.dispose();
		}

		txtPackageIdScan.setFocus();
	}

	/**
	 * Method getPillCountsForLastPackage.
	 * 
	 * @return Set<PillCount>
	 */
	private Set<PillCount> getPillCountsForLastPackage() {
		if (previousPack == null)
			return null;
		
		Set<PillCount> pcsToSave = new HashSet<PillCount>();
		previousPack.getPillCounts().clear();
		for (int i = 0; i < tblLastPackage.getItemCount(); i++) {

			if (tblLastPackage.getItem(i).getData() != null) {
				PillCount pc = (PillCount) tblLastPackage.getItem(i).getData();
				if (pc.getAccum() != -1) {
					pc.setDateOfCount(btnCaptureDate.getDate());
					pc.setPreviousPackage(previousPack);
					pcsToSave.add(pc);
				}
			}
		}
		previousPack.getPillCounts().addAll(pcsToSave);

		return previousPack.getPillCounts();

	}

	private void setDateExpectedFields() {
		// set the colours for all the active fields

		if (!packageLoaded || txtPackageIdScan.getText().trim().length() == 0)
			return; // cos the following line cant be done...

		int numPeriods = PackageManager.getPackage(getHSession(),
				txtPackageIdScan.getText()).getWeekssupply();

		Calendar theCal = Calendar.getInstance();
		theCal.setTime(btnCaptureDate.getDate());
		theCal.add(Calendar.DATE, numPeriods * 7);

		adjustForNewDispDate(btnCaptureDate.getDate(), theCal.getTime());
		adjustDaysSince();
	}

	/**
	 * Change the pill count dates, package dates and next appointment date when
	 * the user changes the dispense date
	 * 
	 * @param theDispDate
	 * @param theNextAppDate
	 */
	private void adjustForNewDispDate(Date theDispDate, Date theNextAppDate) {

		btnCollectionDateForNextPackage.setDate(theNextAppDate);

		// go through the pill counts and set the date to the new date
		if ((theDispDate != null)) {
			for (int i = 0; i < tblLastPackage.getItemCount(); i++) {
				TableItem theItem = tblLastPackage.getItem(i);
				if (theItem.getData() != null) {
					((PillCount) theItem.getData()).setDateOfCount(theDispDate);
				}
			}
		}

		if (previousPack != null) {
			Date lastPickupDate = previousPack.getPickupDate();

			Calendar calDispDate = Calendar.getInstance();
			Calendar lastPickup = Calendar.getInstance();

			lastPickup.setTime(lastPickupDate);
			calDispDate.setTime(theDispDate);
		}

		else {

			lblContentsDaysSinceLastPickup.setText("Initial Pickup");
		}

		if (tblLastPackage != null) {
			// update pill counts
			for (int i = 0; i < tblLastPackage.getItemCount(); i++) {
				TableItem item = tblLastPackage.getItem(i);

				if (item.getData() != null) {
					((PillCount) item.getData()).setDateOfCount(btnCaptureDate
							.getDate());

					if (!item.getText(1).trim().equals("")) {

						try {
							int accum = Integer.parseInt(item.getText(1));
							int adherencePercent = AdherenceManager
							.getAdherencePercent(getHSession(), accum,
									((PillCount) item.getData()));
							item.setText(2, "" + adherencePercent);
							setColourForAdherencePercentCell(item, 2,
									adherencePercent);

						} catch (NumberFormatException e) {
							java.awt.Toolkit.getDefaultToolkit().beep();
							item.setText(1, "");
							item.setText(1, "");
							item.setBackground(2, ResourceUtils
									.getColor(iDartColor.LIST_BACKGROUND));
						}

					}
				}
			}
		}
	}

	/**
	 * Method setColourForAdherencePercentCell.
	 * 
	 * @param item
	 *            TableItem
	 * @param cell
	 *            int
	 * @param adherencePercent
	 *            int
	 */
	private void setColourForAdherencePercentCell(TableItem item, int cell,
			int adherencePercent) {
		if (adherencePercent >= 95) {
			item.setBackground(cell, ResourceUtils.getColor(iDartColor.GREEN));
		} else if (adherencePercent >= 90) {
			item.setBackground(cell, ResourceUtils.getColor(iDartColor.YELLOW));

		} else {
			item.setBackground(cell, ResourceUtils.getColor(iDartColor.RED));

		}
	}

	private void adjustDaysSince() {
		if (previousPack != null) {
			Date lastPickupDate = previousPack.getPickupDate();

			Calendar dateOfPickup = Calendar.getInstance();
			Calendar lastPickup = Calendar.getInstance();

			dateOfPickup.setTime(btnCaptureDate.getDate());
			lastPickup.setTime(lastPickupDate);

			long numOfDays = iDARTUtil.getDaysBetween(dateOfPickup.getTime(),
					lastPickupDate);

			txtLastPackageDate.setText(dateFormatter.format(lastPickupDate));
			lblContentsDaysSinceLastPickup.setText(numOfDays + " days");

			if (numOfDays > ((previousPack.getWeekssupply() * 7) + 3)) {
				lblContentsDaysSinceLastPickup.setForeground(ResourceUtils
						.getColor(iDartColor.RED));
			} else {

				lblContentsDaysSinceLastPickup.setForeground(ResourceUtils
						.getColor(iDartColor.BLACK));
			}
		} else {
			txtLastPackageDate.setText("Initial Pickup");
		}

	}

	/**
	 * View the patient history report, or show a patient selection report
	 * parameters screen if no patient is selected
	 */
	private void cmdPatientHistoryWidgetSelected() {

		getLog().info(
		"New Patient Packaging: User chose 'Patient History Report'");

		if (scannedPack != null) {
			PatientHistoryReport report = new PatientHistoryReport(getShell(),
					scannedPack.getPrescription().getPatient());
			viewReport(report);
		} else {
			PatientHistory patHistory = new PatientHistory(getShell(), true);
			patHistory.openShell();
		}

	}

	/**
	 * View the Scanned Packages report
	 */
	private void cmdPackagesReportWidgetSelected() {

		getLog().info(
		"Packages To Patients: User chose 'Scanned Packages Report'");

		Clinic c = AdministrationManager.getClinic(getHSession(), cmbClinic
				.getText().trim());

		if (btnCaptureDate.getDate() == null) {
			btnCaptureDate.setDate(new Date());
		}

		PackageProcessingReport report = new PackageProcessingReport(
				getShell(), c, btnCaptureDate.getDate(), btnCaptureDate
				.getDate(), PackageLifeStage.PICKED_UP);
		viewReport(report);

	}

	/**
	 * Method getAccumDrugsToSave.
	 * 
	 * @param p
	 *            Packages
	 * @return Set<AccumulatedDrugs>
	 */
	private Set<AccumulatedDrugs> getAccumDrugsToSave(Packages p) {

		java.util.Set<AccumulatedDrugs> adsToSave = new HashSet<AccumulatedDrugs>();
		for (int i = 0; i < tblThisPackage.getItemCount(); i++) {

			TableItem item = tblThisPackage.getItem(i);

			try {
				int inHand = Integer.parseInt(item.getText(3));
				int disp = Integer.parseInt(item.getText(1));

				if (inHand > disp) {

					AccumulatedDrugs ad = new AccumulatedDrugs();

					ad.setWithPackage(p);

					// find the pillcount
					String drugName = item.getText(0);
					for (int j = 0; j < tblLastPackage.getItemCount(); j++) {
						String lastPackDrugName = (String) tblLastPackage
						.getItem(j).getData("drugName");
						if (lastPackDrugName != null && lastPackDrugName.equals(drugName)) {
							ad.setPillCount((PillCount) tblLastPackage.getItem(
									j).getData());

						}

					}
					if (ad.getPillCount() == null) {
						getLog().error(
								"Did not save accumulated drug " + drugName
								+ " because it had no pillcount");
					} else {
						adsToSave.add(ad);
					}
				}
			} catch (NumberFormatException ne) {
				getLog().error(
						"NumberFormatError saving accumulated drug for "
						+ item.getText(0));

			}

		}
		return adsToSave;
	}

	@Override
	protected void clearForm() {
	}

	@Override
	protected void createContents() {
	}

	/**
	 * Method fieldsOk.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

		// If the scannedPack happens to be null.
		if (scannedPack == null)
			return false;

		// Checking dates
		Date packDate = scannedPack.getPackDate();
		Date receivedDate = scannedPack.getDateReceived();

		if (btnCaptureDate.getDate().after(new Date())) {
			MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
			mb.setText("Incorrect Date");
			mb
			.setMessage("The Capture Date for this package cannot be later than today's date");

			mb.open();
			return false;
		}

		if (previousPack != null
				&& (DateFieldComparator.compare(btnCaptureDate.getDate(),
						previousPack.getPickupDate(), Calendar.DAY_OF_MONTH) < 0)) {
			MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
			m.setText("Incorrect Date");
			m.setMessage("The patient picked up their last package ("
					+ previousPack.getPackageId() + ")on "
					+ sdf.format(previousPack.getPickupDate())
					+ ".\nThe Capture Date for this package cannot "
					+ "be before this date.");

			m.open();
			return false;
		}

		if (receivedDate == null
				&& DateFieldComparator.compare(btnCaptureDate.getDate(),
						packDate, Calendar.DAY_OF_MONTH) < 0) {
			MessageBox error = new MessageBox(getShell(), SWT.ICON_ERROR);
			error.setText("Incorrect Date");
			error.setMessage("Package '" + scannedPack.getPackageId()
					+ "' was created on " + sdf.format(packDate)
					+ ". \n\nThe date that this package leaves "
					+ "the pharmacy must be after this date.");
			error.open();
			return false;
		}

		if (receivedDate != null
				&& DateFieldComparator.compare(btnCaptureDate.getDate(),
						receivedDate, Calendar.DAY_OF_MONTH) < 0) {
			MessageBox error = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_ERROR);
			error.setText("Incorrect Date");
			error.setMessage("Package '" + scannedPack.getPackageId()
					+ "' was scanned in at the clinic on "
					+ sdf.format(receivedDate)
					+ ". \n\nThe date that this package is collected "
					+ "by the patient must be after this date.");
			error.open();
			return false;
		}

		// If there has been no failure then return true.
		return true;
	}

	/**
	 * Method submitForm.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean submitForm() {
		if (fieldsOk()) {
			Transaction tx = null;

			try {
				tx = getHSession().beginTransaction();

				Packages pack = PackageManager.getPackage(getHSession(),
						txtPackageIdScan.getText());

				// Save dates associated with this package
				if (AdministrationManager.getDefaultClinicName(getHSession())
						.equalsIgnoreCase(cmbClinic.getText())) {
					pack.setDateLeft(btnCaptureDate.getDate());
					pack.setDateReceived(btnCaptureDate.getDate());
				}

				// if collection date is today, store the time too, else store
				// 12am
				if (iDARTUtil.dateIsToday(btnCaptureDate.getDate())) {
					pack.setPickupDate(new Date());
				} else {
					pack.setPickupDate(btnCaptureDate.getDate());
				}

				Patient aPatient = pack.getPrescription().getPatient();
				PatientManager.setNextAppointmentDateAtVisit(getHSession(),
						aPatient, btnCaptureDate.getDate(),
						btnCollectionDateForNextPackage.getDate());

				Set<PillCount> pcs = getPillCountsForLastPackage();
				if (pcs != null) {
					AdherenceManager.save(getHSession(), pcs);
					EventBusService.publish(new AdherenceEvent(pcs));
				}
				Set<AccumulatedDrugs> accums = pack.getAccumulatedDrugs();
				if (accums != null) {
					accums.clear();
					accums.addAll(getAccumDrugsToSave(pack));
				} else {
					pack.setAccumulatedDrugs(getAccumDrugsToSave(pack));
				}
				PackageManager.savePackage(getHSession(), pack);
				
				if (pack.hasARVDrug() 
					&& aPatient.getAttributeByName(PatientAttribute.ARV_START_DATE) == null) {

					Packages firstArvPacks = PackageManager
						.getFirstPackageWithARVs(PackageManager
							.getAllPackagesForPatient(getHSession(),
									aPatient));
					if (firstArvPacks.getId() == pack.getId()) {

						// If this is the first ARV pack then get
						// the first episode.
						Episode firstEpisode = PatientManager
						.getFirstEpisode(aPatient);
						String epiStartReason = firstEpisode.getStartReason();
						if (epiStartReason.equalsIgnoreCase("NEW PATIENT")) {
							MessageBox mbox = new MessageBox(getShell(), SWT.YES
									| SWT.NO | SWT.ICON_QUESTION);
							mbox.setText("ART Start Date Not Set");
							mbox
							.setMessage("The ARV start date has not yet been set and this is the first time patient '"
									+ txtFolderNo.getText()
									+ "' is receiving ARV drugs. \n\nWould you like to set the ARV start date to "
									+ btnCaptureDate.getText()
									+ " now?");

							switch (mbox.open()) {
							case SWT.YES:
								PatientManager.addPatientAttributeToPatient(
										getHSession(), aPatient,
										setARVStartDate(btnCaptureDate
												.getText(), aPatient));
								getLog().info("ARV start date has been set");
								PatientManager.savePatient(getHSession(),
										aPatient);
								break;

							}
						}
					}
				}
				getHSession().flush();
				tx.commit();

				EventBusService.publish(new PackageEvent(PackageEvent.Type.PICKUP_ONLY, pack));
				
				updateLists();

				MessageBox m = new MessageBox(getShell(), SWT.ICON_INFORMATION
						| SWT.OK);
				m.setMessage("Package successfully saved.");
				m.setText("Package Saved Successfully");
				m.open();

			} catch (HibernateException he) {
				getLog().error("couldn't save scanned out packages");
				MessageBox cantSave = new MessageBox(getShell(), SWT.ICON_ERROR
						| SWT.OK);

				cantSave.setText("Cannot Save Scanned Out Packages");
				cantSave
				.setMessage("Unable to save scanned out packages and recorded pill counts. Please try again");
				cantSave.open();
				he.printStackTrace();
				if (tx != null) {
					tx.rollback();
				}
			}

			cmdClearWidgetSelected();
			return true;
		} else {
			getLog().info(
					"Information could not be saved "
					+ "due to invalid input fields.");
		}
		return false;
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.celllife.idart.gui.misc.iDARTChangeListner#changed(java.lang.Object)
	 */
	@Override
	public void changed(Object o) {
		if (o instanceof Date) {

		}
	}

	/**
	 * Method setARVStartDate.
	 * 
	 * @param startDate
	 *            String
	 * @param localPatient
	 *            Patient
	 * @return PatientAttribute
	 */
	private PatientAttribute setARVStartDate(String startDate,
			Patient localPatient) {
		PatientAttribute pa = localPatient
		.getAttributeByName(PatientAttribute.ARV_START_DATE);

		if (pa == null) {

			pa = new PatientAttribute();
			pa.setType(PatientManager.getAttributeTypeObject(getHSession(),
					PatientAttribute.ARV_START_DATE));
			pa.setPatient(localPatient);
		}

		pa.setValue(startDate);
		return pa;
	}
	
	private void setAppointmentErrorMsg(String patientId, String date) {
		appointmentErrorMsg =  "Patient "+ patientId + " is collecting their current package on " +
		date + ".\n\nTheir next appointment date must be after this date.";
	}
	
	private String getAppointmentErrorMsg(){
		return appointmentErrorMsg;
	}
	
	private void fireChangeEvent() {
		setAppointmentErrorMsg(txtFolderNo.getText(), btnCaptureDate.getText());
		IRule<Date> rule = DateRuleFactory.afterInclusive(btnCaptureDate.getDate(), true);
		rule.setDescription(getAppointmentErrorMsg());
		DateInputValidator dateValidator = new DateInputValidator(rule);
		btnCollectionDateForNextPackage.setValidator(dateValidator);
	}
	
	private void addSearchBarFocusListener(){
		searchBar.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				String text = searchBar.getText().trim();
				if(text.equalsIgnoreCase("patient search")){
					searchBar.setText("");
				}
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				String text = searchBar.getText().trim();
				if(text.equalsIgnoreCase("")){
					searchBar.setText("patient search");
					updatePackagesAwaitingDispatchListForClinic(cmbClinic.getText());
				}
			}
		});
	}
	
	private void addSearchBarKeyListener(){
		searchBar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String currentText = searchBar.getText().trim();

				if(!currentText.isEmpty() && !currentText.equalsIgnoreCase("")){
					// first, parse the text in txtPatientId
					String patientId = PatientBarcodeParser
							.getPatientId(currentText);
					if (!patientId.isEmpty() && !patientId.equalsIgnoreCase("")) {
						updatePackagesAwaitingDispatchListForPatient(patientId);
					}
				}
			}
		});
	}
	
	private void searchBarEnter(){
		if(lstAwaiting.getItemCount() == 1 ){
			txtPackageIdScan.setText(lstAwaiting.getItem(0));
			cmdEnterPressed();
		}
	}
}
