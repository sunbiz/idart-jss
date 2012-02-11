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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.SearchManager;
import model.manager.StockManager;
import model.manager.TemporaryRecordsManager;
import model.manager.reports.PatientHistoryReport;
import model.nonPersistent.PatientIdAndName;

import org.apache.log4j.Logger;
import org.celllife.function.DateRuleFactory;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.events.AdherenceEvent;
import org.celllife.idart.events.PackageEvent;
import org.celllife.idart.facade.PillCountFacade;
import org.celllife.idart.gui.composite.PillCountTable;
import org.celllife.idart.gui.deletions.DeleteStockPrescriptionsPackages;
import org.celllife.idart.gui.label.PrintEmergencyLabel;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.prescription.AddPrescription;
import org.celllife.idart.gui.reportParameters.PatientHistory;
import org.celllife.idart.gui.reprintLabels.ReprintLabels;
import org.celllife.idart.gui.stockOnHand.StockOnHandGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.widget.DateButton;
import org.celllife.idart.gui.widget.DateInputValidator;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.DateFieldComparator;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.print.label.PackageCoverLabel;
import org.celllife.idart.print.label.ScriptSummaryLabel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.adamtaft.eb.EventBusService;

/**
 */
public class NewPatientPackaging extends GenericFormGui implements
		iDARTChangeListener {

	private ListViewer lstWaitingPatients;
	private Button btnDispense;
	private Label lblClinic;
	private CCombo cmbSupply;
	private CCombo cmbSelectStockCenter;
	private TableEditor editorTblPrescriptionInfo;
	private boolean fieldsEnabled = false; // has a patient been selected to
	private Label lblDuration;
	private Label lblIndex;
	private Label lblPackageInfo1;
	private Label lblPackageInfo2;
	private Label lblPackageInfo3;
	private Label lblPicChild;
	private Label lblPicLeftArrow;
	private Label lblPicRightArrow;
	private Label lblNextAppointment;
	private Link lnkStockOnHand;
	private Button rdBtnDispenseNow;
	private Button rdBtnDispenseLater;
	private Button rdBtnNoAppointmentDate;
	private Button rdBtnYesAppointmentDate;
	private Button rdBtnPrintSummaryLabelNo;
	private Button rdBtnPrintSummaryLabelYes;
	private ProgressBar pbLoading;
	private Text searchBar;
	private Table tblPrescriptionInfo;
	private Text txtAreaNotes;
	private Text txtDoctor;
	private Text txtNextAppDate;
	private Text txtPatientAge;
	private Text txtPatientDOB;
	private Text txtPatientId;
	private Text txtPatientName;
	private Text txtPrescriptionDate;
	private Text txtPrescriptionId;
	private Label lblDateOfLastPickupContents;
	private DateButton btnCaptureDate;
	private DateButton btnNextAppDate;
	private PillCountTable pillCountTable;
	private PillCountFacade pillFacade; // facade classes
	private StockCenter localPharmacy; // local persistent objects
	private Packages newPack; // the new package
	private Packages previousPack; // the last package picked up
	private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy"); // local
	private boolean dateAlreadyDispensed;
	private Patient localPatient;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public NewPatientPackaging(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		createScreen();
	}

	/**
	 * Constructor for when the screen is accessed from a 'Dispense Now'
	 * shortcut, and must be populated for a particular patient
	 * 
	 * @param parent
	 *            Shell
	 * @param patient
	 */
	public NewPatientPackaging(Shell parent, Patient patient) {
		super(parent, HibernateUtil.getNewSession());
		createScreenForNewPatient(patient);
	}

	private void createScreenForNewPatient(Patient patient) {
		Patient p = PatientManager.getPatient(getHSession(), patient.getId());
		dateAlreadyDispensed = false;

		if (p == null) {
			getLog().error(
					"Patient number passed to packaging GUI does not belong to a patient");
		} else {
			populatePatientDetails(p.getId());
		}
	}

	/**
	 * Change the pill count dates, package dates and next appointment date when
	 * the user changes the dispense date
	 * 
	 * @param theDispDate
	 * @param theNextAppDate
	 */
	private void adjustForNewDispDate(Date theDispDate) {
		if (previousPack != null && theDispDate != null) {

			Date lastPickupDate = previousPack.getPickupDate();
			int numOfDays = iDARTUtil.getDaysBetween(lastPickupDate,
					theDispDate);
			lblDateOfLastPickupContents.setText(numOfDays + " days ("
					+ sdf.format(lastPickupDate) + ")");
		} else {
			lblDateOfLastPickupContents.setText("Initial Pickup");
		}

		if (theDispDate != null) {
			pillCountTable.update(theDispDate);
		}
	}

	private void attachPharmacyComboListener() {
		cmbSelectStockCenter.setEditable(false);
		cmbSelectStockCenter.setBackground(ResourceUtils
				.getColor(iDartColor.WHITE));
		cmbSelectStockCenter
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						localPharmacy = AdministrationManager.getStockCenter(
								getHSession(), cmbSelectStockCenter.getText());
						if (localPharmacy != null) {
							if (newPack.getPrescription() != null) {
								populateStockDetails();
								prepopulateQuantities();
							}
						} else {
							getLog().warn(
									"Tried to populate screen for update of pharmacy '"
											+ cmbSelectStockCenter.getText()
											+ "', but no pharmacy with that name was found in the database.");
						}
					}

				});
	}

	private void attachPrescriptionInfoTableEditor() {
		// add a editor for the labels and inHand columns, otherwise open
		// batch information when col clicked
		editorTblPrescriptionInfo = new TableEditor(tblPrescriptionInfo);
		editorTblPrescriptionInfo.horizontalAlignment = SWT.LEFT;
		editorTblPrescriptionInfo.grabHorizontal = true;

		tblPrescriptionInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editorTblPrescriptionInfo.getEditor();
				if (old != null) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblPrescriptionInfo.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblPrescriptionInfo.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					if (column == 4) {
						// Create the Text object for label column

						final Text text = new Text(tblPrescriptionInfo,
								SWT.NONE);
						text.setForeground(item.getForeground());
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));
						text.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));

						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						editorTblPrescriptionInfo.minimumWidth = text
								.getBounds().width;

						// Set the control into the editor
						editorTblPrescriptionInfo.setEditor(text, item, column);

						final int col = column;

						text.addModifyListener(new ModifyListener() {
							@Override
							@SuppressWarnings("unchecked")
							public void modifyText(ModifyEvent event1) {

								item.setText(col, text.getText());
								// check user input

								ArrayList<PackageDrugInfo> list = ((ArrayList<PackageDrugInfo>) item
										.getData());
								if (list.size() > 0) {
									if (!text.getText().trim().equals("")) {
										try {
											int labels = Integer.parseInt(text
													.getText());
											list.get(0).setNumberOfLabels(
													labels);
										} catch (NumberFormatException e) {
											java.awt.Toolkit
													.getDefaultToolkit().beep();
											text.setText("1");
											item.setText(col, "1");
											text.selectAll();
										} catch (IndexOutOfBoundsException iobex) {
											getLog().error(
													"No drugs in list in order to specify a label to print.",
													iobex);
										}

									} else {
										text.setText("1");
										item.setText(col, "1");
										text.selectAll();
									}

								} else {
									if (!text.getText().equals("0")) {
										java.awt.Toolkit.getDefaultToolkit()
												.beep();
										MessageBox msg = new MessageBox(
												getShell(), SWT.OK
														| SWT.ICON_ERROR);
										msg.setText("Label Amount Invalid.");
										msg.setMessage("You are unable to print a drug label for this drug "
												+ "since there no quantity has been specified. "
												+ "\n\nPlease set the quantity to dispense first.");
										msg.open();
										text.setText("0");
										text.selectAll();
										return;
									}
								}
							}
						});
					}

					else if (column == 5) {
						// Create the combo box for the in hand column

						final CCombo combo = new CCombo(tblPrescriptionInfo,
								SWT.NONE);
						combo.setForeground(item.getForeground());
						combo.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));
						combo.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));

						combo.setText(item.getText(column));
						combo.setForeground(item.getForeground());
						combo.setFocus();
						combo.setEditable(false);

						editorTblPrescriptionInfo.minimumWidth = combo
								.getBounds().width;

						// Set the control into the editor
						editorTblPrescriptionInfo
								.setEditor(combo, item, column);

						final int col = column;

						java.util.List<String> possibleValues = getPossibleInHandOnExitValues(item);
						Iterator<String> itr = possibleValues.iterator();

						while (itr.hasNext()) {
							combo.add(itr.next());
						}

						combo.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {

								item.setText(col, combo.getText());
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

					else {
						cmdTblPrescriptionWidgetSelected();
					}
				}
			}
		});

	}

	private void attachSearchBarListener() {
		searchBar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String currentText = searchBar.getText().trim();

				// first, parse the text in txtPatientId
				String patientId = PatientBarcodeParser
						.getPatientId(currentText);
				populatePatientDetails(patientId, e.character == SWT.CR);
			}
		});
	}

	private void attemptCaptureDateReset() {
		Date today = new Date();
		Date chosenDate = new Date();
		if (btnCaptureDate.getDate() != null) {
			chosenDate = btnCaptureDate.getDate();
		}

		if (chosenDate.after(today)) {
			MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
			mb.setText("Date Packed Cannot Be In The Future");
			mb.setMessage("The Date Packed cannot be in the future");
			mb.open();
			btnCaptureDate.setDate(today);
			chosenDate = today;
		}

		// Obtaining the currently selected capture date
		// if there is a previous pack date which is not
		// after the currently selected pack date.
		if (previousPack != null) {
			Date pickupDate = previousPack.getPickupDate();
			if (iDARTUtil.before(chosenDate, pickupDate)) {
				String msg = "Patient \"{0}\" collected their last package on {1,date,medium}. "
						+ "\n\nThe Date Packed for the new package you are trying to create must be after this date.";
				showMessage(MessageDialog.ERROR, "Date Packed Error",
						MessageFormat.format(msg, localPatient.getPatientId(),
								pickupDate));

				btnCaptureDate.setDate(today);
				chosenDate = today;
			}
		}

		if (iDARTUtil.before(chosenDate, today)) {
			chosenDate = iDARTUtil.zeroTimeStamp(chosenDate);
			btnCaptureDate.setDate(chosenDate);
		}

		if (fieldsEnabled) {
			String clinicName = localPatient.getClinicAtDate(chosenDate)
					.getClinicName();
			// Check if clinic has changed. If so, show message
			if (!lblClinic.getText().equalsIgnoreCase(clinicName)) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Patient's Clinic Has Changed");
				m.setMessage("Note that on "
						+ sdf.format(btnCaptureDate.getDate()) + ", patient '"
						+ localPatient.getPatientId()
						+ "' was collecting their drugs at " + clinicName
						+ ".\n\n Any packages you create for this patient "
						+ "on this day will be destined for this clinic, "
						+ "not the patient's current clinic.");
				m.open();
			}

			lblClinic.setText(clinicName);
			setDateExpectedFields();
			populatePrescriptionDetails();
			updateDateOfLastPickup();
		}
		if (newPack != null) {
			newPack.setPackDate(chosenDate);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.iDARTChangeListner#changed()
	 */
	/**
	 * Method changed.
	 * 
	 * @param o
	 *            Object
	 * @see org.celllife.idart.gui.misc.iDARTChangeListener#changed(Object)
	 */
	@Override
	public void changed(Object o) {
		if (o instanceof PillCount) {
			setInHandOnExit((PillCount) o);
		}

		if (o instanceof Date) {
			// Done if there has been a change in the ARV Start date
			// through a ARV Start Date Picker.
			Transaction tx = null;
			try {
				tx = getHSession().beginTransaction();
				String dte = iDARTUtil.toString(Date.class, o);
				newPack.getPrescription().getPatient()
						.getAttributeByName(PatientAttribute.ARV_START_DATE)
						.setValue(dte);
				PatientManager.savePatient(getHSession(), newPack
						.getPrescription().getPatient());
				getHSession().flush();
			} catch (Exception e) {
				if (tx == null) {
					getLog().error("Could not change ARV Start date", e);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method clears all textfields
	 * 
	 */
	@Override
	protected void clearForm() {
		lblClinic.setText("");
		txtPatientId.setText("");
		txtPatientAge.setText("");
		txtPatientDOB.setText("");
		searchBar.setText("");
		txtPatientName.setText("");
		btnDispense.setEnabled(false);
		lblDateOfLastPickupContents.setText("");
		txtPrescriptionDate.setText("");
		txtDoctor.setText("");
		txtPrescriptionId.setText("");
		txtAreaNotes.setText("");
		btnNextAppDate.setDate(new Date());
		tblPrescriptionInfo.clearAll();
		// tblLastPackageDrugs.clearAll();
		lblPicChild.setVisible(false);
		lblIndex.setText("");
		lblDuration.setText("");
		lblPicLeftArrow.setVisible(false);
		lblPicRightArrow.setVisible(false);
		highlightPackageInfo(ResourceUtils.getColor(iDartColor.BLACK));
		enableFields(false);
		pillCountTable.clearTable();

		closeAndReopenSession();
		searchBar.setFocus();
	}

	/**
	 * This method closes and reopens a session. It should be used in screens
	 * that don't close after a write
	 * 
	 */
	public void closeAndReopenSession() {
		try {
			if (getHSession() != null) {
				getHSession().close();
			}
			Session newSession = HibernateUtil.getNewSession();
			setHSession(newSession);
			pillCountTable.setHSession(newSession);
		} catch (HibernateException he) {
			getLog().error("Error closing and reopening session", he);
			he.printStackTrace();
		}
	}

	private void cmbWeeksSupplyChanged() {

		String theWeeks = cmbSupply.getText();
		int numPeriods = 4;

		try {
			numPeriods = Integer.parseInt(theWeeks.split(" ")[0]);
			if (theWeeks.endsWith("months") || theWeeks.endsWith("month")) {
				numPeriods = numPeriods * 4;
			}

		} catch (NumberFormatException ne) {
			getLog().warn(
					"NumberFormatException while trying to get weeks supply from combo",
					ne);
			numPeriods = 4;
		}
		if (newPack != null) {
			newPack.setWeekssupply(numPeriods);

			if (newPack.getPrescription() != null) {

				prepopulateQuantities();
			}

			if (rdBtnDispenseNow.getSelection()) {
				Calendar theCal = Calendar.getInstance();

				newPack.setPackDate(btnCaptureDate.getDate() == null ? new Date()
						: btnCaptureDate.getDate());

				theCal.setTime(newPack.getPackDate());
				theCal.add(Calendar.DATE, newPack.getWeekssupply() * 7);
				adjustForNewAppointmentDate(theCal.getTime());
			}
		}

	}

	@Override
	protected void cmdCancelWidgetSelected() {
	}

	@Override
	protected void cmdClearWidgetSelected() {
	}

	/**
	 * Method cmdDispenseDrugsSelected.
	 * 
	 * @param dispenseNow
	 *            boolean
	 */
	@SuppressWarnings("unchecked")
	private void cmdDispenseDrugsSelected(boolean dispenseNow) {
		java.util.List<PackageDrugInfo> allPackagedDrugsList = new ArrayList<PackageDrugInfo>();
		// remove pdis with none dispensed
		for (int i = 0; i < tblPrescriptionInfo.getItemCount(); i++) {
			java.util.List<PackageDrugInfo> pdiList = (java.util.List<PackageDrugInfo>) tblPrescriptionInfo
					.getItem(i).getData();
			Iterator<PackageDrugInfo> it = pdiList.iterator();
			while (it.hasNext()) {
				PackageDrugInfo pdi = it.next();
				if (pdi.getDispensedQty() != 0) {
					pdi.setDispenseDate(newPack.getPackDate());
					pdi.setWeeksSupply(getSelectedWeekSupply());
					pdi.setDispensedForLaterPickup(!dispenseNow);
					pdi.setPickupDate(dispenseNow ? new Date() : null);
					allPackagedDrugsList.add(pdi);
				}
			}
		}
		Set<AccumulatedDrugs> accumDrugSet = getAccumDrugsToSave();
		if (fieldsOkay(allPackagedDrugsList)
				&& ((allPackagedDrugsList.size() > 0) || (accumDrugSet.size() > 0))) {
			submitForm(dispenseNow, allPackagedDrugsList);
			getLog().info("submitForm() called");
			initialiseSearchList();
			clearForm();
		}
	}

	private void initialiseSearchList() {
		java.util.List<PatientIdAndName> patients = null;
		patients = SearchManager
				.getActivePatientWithValidPrescriptionIDsAndNames(getHSession());

		lstWaitingPatients.setInput(patients);
	}

	/**
	 * View the patient history report, or show a patient selection report
	 * parameters screen if no patient is selected
	 */
	private void cmdPatientHistoryWidgetSelected() {

		getLog().info(
				"New Patient Packaging: User chose 'Patient History Report'");

		if (localPatient != null) {
			PatientHistoryReport report = new PatientHistoryReport(getShell(),
					localPatient);
			viewReport(report);
		} else {
			PatientHistory patHistory = new PatientHistory(getShell(), true);
			patHistory.openShell();
		}
	}

	private void cmdPrintEmergencyLabelSelected() {

		new PrintEmergencyLabel(getShell());

	}

	private void cmdReprintLabelsSelected() {

		new ReprintLabels(getShell());
	}

	/**
	 * Called when the user wants to ResourceUtils.getColor(iDartColor.RED)o an
	 * arv package (ie. delete the most recent package for any patient)
	 */

	private void cmdRedoPackageSelected() {
		closeAndReopenSession();
		if ((newPack.getPrescription() != null)
				&& !(txtPatientId.getText().trim().equals(""))) {

			final Patient patient = newPack.getPrescription().getPatient();
			final DeleteStockPrescriptionsPackages myRedoPackage = new DeleteStockPrescriptionsPackages(
					getShell(),patient);
			myRedoPackage.getShell().addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					clearForm();

					populatePatientDetails(patient.getId());
				}
			});
		} else {

			final DeleteStockPrescriptionsPackages myRedoPackage = new DeleteStockPrescriptionsPackages(
					getShell());
			myRedoPackage.getShell().addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					clearForm();
				}
			});
		}
	}

	@Override
	protected void cmdSaveWidgetSelected() {
	}

	private int getSelectedWeekSupply() {
		String theWeeks = cmbSupply.getText();
		int numPeriods = Integer.parseInt("" + theWeeks.charAt(0));
		if (theWeeks.endsWith("months") || theWeeks.endsWith("month")) {
			numPeriods *= 4;
		}
		return numPeriods;
	}

	/**
	 * Passes the current list of pdis to a BatchInformation, where the pdis for
	 * the selected drug can be entered.
	 */
	@SuppressWarnings("unchecked")
	private void cmdTblPrescriptionWidgetSelected() {
		final java.util.List<PackageDrugInfo> finalPdisForThisDrug = (java.util.List<PackageDrugInfo>) tblPrescriptionInfo
				.getSelection()[0].getData();

		Drug drug = DrugManager.getDrug(getHSession(),
				tblPrescriptionInfo.getSelection()[0].getText(0));

		final BatchInformation myBatchInformation = new BatchInformation(
				getHSession(), getShell(), localPharmacy, drug, newPack,
				finalPdisForThisDrug);
		myBatchInformation.getShell().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (myBatchInformation.infoChanged) {
					// update the weekssupply
					tblPrescriptionInfo.getSelection()[0]
							.setData(myBatchInformation.getPdiList());
					getDispensedQuantity(myBatchInformation.getStockList(),
							tblPrescriptionInfo.getSelection()[0]);
				} else {
					tblPrescriptionInfo.getSelection()[0]
							.setData(finalPdisForThisDrug);
				}
			}
		});

	}

	private void cmdUpdatePrescriptionWidgetSelected() {

		final AddPrescription myPrescription = new AddPrescription(
				localPatient, getShell(), true);
		myPrescription.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				Patient patient = myPrescription.getPatient();
				closeAndReopenSession();
				patient = PatientManager.getPatient(getHSession(), patient.getId());
				if ((patient != null)
						&& (patient
								.getCurrentPrescription() != null)) {
					populatePatientDetails(patient.getId());
				} else {
					clearForm();
				}
			}
		});

	}

	/**
	 * This method initializes comp
	 * 
	 * @param parent
	 *            Composite
	 */
	private void createCompShowAppointmentOnLabels(Composite parent) {
		Composite compShowAppointmentOnLabels = new Composite(parent, SWT.NONE);
		compShowAppointmentOnLabels.setLayout(null);
		compShowAppointmentOnLabels.setBounds(new Rectangle(478, 173, 95, 22));

		rdBtnYesAppointmentDate = new Button(compShowAppointmentOnLabels,
				SWT.RADIO);
		rdBtnYesAppointmentDate.setBounds(new Rectangle(5, 1, 45, 20));
		rdBtnYesAppointmentDate.setText("Yes");
		rdBtnYesAppointmentDate.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		rdBtnNoAppointmentDate = new Button(compShowAppointmentOnLabels,
				SWT.RADIO);
		rdBtnNoAppointmentDate.setBounds(new Rectangle(55, 1, 38, 19));
		rdBtnNoAppointmentDate.setText("No");

		rdBtnNoAppointmentDate.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		if (iDartProperties.nextAppointmentDateOnLabels) {
			rdBtnYesAppointmentDate.setSelection(true);
			rdBtnNoAppointmentDate.setSelection(false);
		} else {
			rdBtnYesAppointmentDate.setSelection(false);
			rdBtnNoAppointmentDate.setSelection(true);
		}

	}

	/**
	 * This method initializes getCompButtons()
	 * 
	 */
	@Override
	protected void createCompButtons() {

		setCompButtons(new Composite(getShell(), SWT.NONE));
		getCompButtons().setBounds(new Rectangle(10, 626, 864, 34));

		// btnPrintEmergencyLabel
		Button btnRerrintLabels = new Button(getCompButtons(), SWT.NONE);
		btnRerrintLabels.setBounds(new Rectangle(0, 2, 120, 30));
		btnRerrintLabels.setText("Reprint Label");
		btnRerrintLabels
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						cmdReprintLabelsSelected();
					}
				});

		// btnRePrintLabel
		Button btnPrintCustomLabel = new Button(getCompButtons(), SWT.NONE);
		btnPrintCustomLabel.setBounds(new Rectangle(130, 2, 120, 30));
		btnPrintCustomLabel.setText("Custom Label");
		btnPrintCustomLabel
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						cmdPrintEmergencyLabelSelected();
					}
				});

		btnRerrintLabels.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblPicDispense = new Label(getCompButtons(), SWT.NONE);
		lblPicDispense.setBounds(new Rectangle(508, 0, 40, 34));

		lblPicDispense.setImage(ResourceUtils
				.getImage(iDartImage.DISPENSEPACKAGENOW_40X34));

		btnDispense = new Button(getCompButtons(), SWT.NONE);
		btnDispense.setBounds(new Rectangle(554, 2, 146, 30));
		btnDispense.setText("Create Package");
		btnDispense
				.setToolTipText("Press this button to dispense these drugs. \nA printer screen will appear for printing of drug labels, package labels, and possibly a script summary label.");
		btnDispense
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						cmdDispenseDrugsSelected(rdBtnDispenseNow
								.getSelection());
						getLog().info("cmdDispenseDrugsSelected() called");
					}
				});
		btnDispense.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDispense.setEnabled(false);

		Button btnClose = new Button(getCompButtons(), SWT.NONE);
		btnClose.setBounds(new Rectangle(735, 2, 126, 30));
		btnClose.setText("Close");
		btnClose.setToolTipText("Press this button to close this screen.");
		btnClose.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseSelected();
			}
		});
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblPicRedoPackage = new Label(getCompButtons(), SWT.NONE);
		lblPicRedoPackage.setBounds(new Rectangle(274, 0, 40, 34));

		lblPicRedoPackage.setImage(ResourceUtils
				.getImage(iDartImage.REDOPACKAGE_40X34));

		// btnRedoPackage
		Button btnRedoPackage = new Button(getCompButtons(), SWT.NONE);
		btnRedoPackage.setBounds(new Rectangle(318, 2, 155, 30));
		btnRedoPackage.setText("Undo Created Package");
		btnRedoPackage
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						cmdRedoPackageSelected();
					}
				});
		btnRedoPackage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes CompDispensingTypeSelection
	 */
	private void createCompDispensingTypeSelection() {

		Composite compDispensingTypeSelection = new Composite(getShell(),
				SWT.BORDER);
		compDispensingTypeSelection.setBounds(new Rectangle(242, 56, 643, 67));

		// lblPicDispenseNow
		Label lblPicDispenseNow = new Label(compDispensingTypeSelection,
				SWT.NONE);
		lblPicDispenseNow.setBounds(new Rectangle(13, 4, 40, 34));
		lblPicDispenseNow.setImage(ResourceUtils
				.getImage(iDartImage.DISPENSEPACKAGENOW_40X34));

		// btnDispenseNow
		rdBtnDispenseNow = new Button(compDispensingTypeSelection, SWT.RADIO);
		rdBtnDispenseNow.setBounds(new Rectangle(58, 9, 244, 30));
		rdBtnDispenseNow.setText("I am dispensing directly to patients");

		rdBtnDispenseNow
				.setToolTipText("Press this button to print out labels for all drugs "
						+ "in this prescription. \nThese labels should be placed on the "
						+ "drug containers.");
		rdBtnDispenseNow
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// cmdDispenseDrugsSelected(true);

						if (fieldsEnabled) {
							resetGUIforDispensingType();
						} else {
							lblNextAppointment.setText("Next Appointment:");
						}

					}
				});
		rdBtnDispenseNow.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblPicDispenseLater
		Label lblPicDispenseLater = new Label(compDispensingTypeSelection,
				SWT.NONE);
		lblPicDispenseLater.setBounds(new Rectangle(345, 3, 40, 34));
		lblPicDispenseLater.setImage(ResourceUtils
				.getImage(iDartImage.PACKAGESAWAITINGPICKUP_40X34));

		// btnDispenseLater
		rdBtnDispenseLater = new Button(compDispensingTypeSelection, SWT.RADIO);
		rdBtnDispenseLater.setBounds(new Rectangle(390, 8, 251, 30));
		rdBtnDispenseLater.setText("I am creating packages for later pickup");
		rdBtnDispenseLater
				.setToolTipText("Press this button to print out the package labels for this "
						+ "patient.\nThere will be a label for each of the drugs in this "
						+ "prescription,\nand a label for the cover of the package.");
		rdBtnDispenseLater
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (fieldsEnabled) {
							// change in hand values
							clearInHandOnExit();
							resetGUIforDispensingType();
						} else {
							lblNextAppointment.setText("Current Appointment:");
						}
					}
				});
		rdBtnDispenseLater.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		if (iDartProperties.dispenseDirectlyDefault) {
			rdBtnDispenseNow.setSelection(true);
			rdBtnDispenseLater.setSelection(false);

		} else {
			rdBtnDispenseNow.setSelection(false);
			rdBtnDispenseLater.setSelection(true);
		}

		final Label lblSelectPharmacy = new Label(compDispensingTypeSelection,
				SWT.NONE);
		lblSelectPharmacy.setBounds(new Rectangle(51, 41, 200, 20));
		lblSelectPharmacy.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblSelectPharmacy.setAlignment(SWT.RIGHT);
		lblSelectPharmacy.setText("Dispensing from pharmacy:");

		cmbSelectStockCenter = new CCombo(compDispensingTypeSelection,
				SWT.BORDER);
		cmbSelectStockCenter.setBounds(new Rectangle(255, 40, 300, 20));
		cmbSelectStockCenter.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));

		CommonObjects.populateStockCenters(getHSession(), cmbSelectStockCenter);
		localPharmacy = AdministrationManager.getStockCenter(getHSession(),
				cmbSelectStockCenter.getText());

		attachPharmacyComboListener();

	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Make up Drug Packages for Patients";
		iDartImage icoImage = iDartImage.DISPENSEPACKAGES;
		buildCompHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createCompLastPackage() {
		Composite compLastPackage = new Composite(getShell(), SWT.BORDER);
		compLastPackage.setLayout(null);
		compLastPackage.setBounds(new Rectangle(5, 412, 879, 207));

		createLastPackageTable(compLastPackage);
		createPrescriptionInfoTable(compLastPackage);

		Label lblSummaryLabel = new Label(compLastPackage, SWT.NONE);
		lblSummaryLabel.setBounds(new Rectangle(610, 177, 160, 18));
		lblSummaryLabel.setText("Print a script summary label?");
		lblSummaryLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblAppointmentDate = new Label(compLastPackage, SWT.NONE);
		lblAppointmentDate.setBounds(new Rectangle(274, 177, 208, 15));
		lblAppointmentDate.setText("Print next appointment date on labels?");
		lblAppointmentDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		createCompShowAppointmentOnLabels(compLastPackage);
		createCompSummaryLabel(compLastPackage);

	}

	/**
	 * This method initializes compPatientsWaiting
	 */
	private void createCompPatientsWaiting() {

		Group compPatientsWaiting = new Group(getShell(), SWT.NONE);
		compPatientsWaiting.setBounds(new Rectangle(6, 56, 213, 349));

		Label lblSearch = new Label(compPatientsWaiting, SWT.NONE);
		lblSearch.setBounds(new Rectangle(10, 7, 187, 20));
		lblSearch.setText("Patient Search:");
		lblSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		searchBar = new Text(compPatientsWaiting, SWT.BORDER);
		searchBar.setBounds(new Rectangle(10, 28, 187, 20));
		searchBar.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		searchBar.setFocus();

		attachSearchBarListener();

		Label lblWaitingPatients = new Label(compPatientsWaiting, SWT.BORDER);
		lblWaitingPatients.setBounds(new Rectangle(10, 58, 186, 20));
		lblWaitingPatients.setAlignment(SWT.CENTER);
		lblWaitingPatients.setText("1. Search Results");
		lblWaitingPatients.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_BOLD));
		
		lstWaitingPatients = new ListViewer(compPatientsWaiting);
		lstWaitingPatients.getList().setBounds(new Rectangle(9, 77, 187, 199));
		lstWaitingPatients.getList().setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lstWaitingPatients.setContentProvider(new ArrayContentProvider());
		lstWaitingPatients.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				if (!lstWaitingPatients.getSelection().isEmpty()) {
					pbLoading.setSelection(0);
					lstWaitingPatientsWidgetSelected();
				}
				
			}
		});
		
		Label lblClinicName = new Label(compPatientsWaiting, SWT.BORDER);
		lblClinicName.setBounds(new Rectangle(9, 280, 187, 15));
		lblClinicName.setAlignment(SWT.CENTER);
		lblClinicName.setText("Patient's Clinic");
		lblClinicName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_BOLD));

		lblClinic = new Label(compPatientsWaiting, SWT.BORDER);
		lblClinic.setBounds(new Rectangle(9, 299, 187, 20));
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblClinic.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_BACKGROUND));

		pbLoading = new ProgressBar(compPatientsWaiting, SWT.SMOOTH);
		pbLoading.setBounds(new Rectangle(10, 322, 185, 20));

		initialiseSearchList();

	}

	/**
	 * This method initializes compSummaryLabel
	 * 
	 * @param parent
	 *            Composite
	 */
	private void createCompSummaryLabel(Composite parent) {

		Composite compSummaryLabel = new Composite(parent, SWT.NONE);
		compSummaryLabel.setLayout(null);
		compSummaryLabel.setBounds(new Rectangle(771, 173, 98, 21));

		rdBtnPrintSummaryLabelYes = new Button(compSummaryLabel, SWT.RADIO);
		rdBtnPrintSummaryLabelYes.setBounds(new Rectangle(5, 1, 49, 20));
		rdBtnPrintSummaryLabelYes.setText("Yes");
		rdBtnPrintSummaryLabelYes.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		rdBtnPrintSummaryLabelNo = new Button(compSummaryLabel, SWT.RADIO);
		rdBtnPrintSummaryLabelNo.setBounds(new Rectangle(57, 1, 45, 19));
		rdBtnPrintSummaryLabelNo.setText("No");
		rdBtnPrintSummaryLabelNo.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		if (iDartProperties.summaryLabelDefault) {
			rdBtnPrintSummaryLabelYes.setSelection(true);
			rdBtnPrintSummaryLabelNo.setSelection(false);
		} else {

			rdBtnPrintSummaryLabelYes.setSelection(false);
			rdBtnPrintSummaryLabelNo.setSelection(true);
		}

	}

	@Override
	protected void createContents() {
		createCompDispensingTypeSelection();
		createCompPatientsWaiting();
		createGrpPatientInfo();
		createGrpPackageInfo();
		createGrpPrescriptionInfo();
		createCompLastPackage();
		clearForm();
	}

	/**
	 * This method initializes grpNotes
	 * 
	 * @param parent
	 *            Composite
	 */
	private void createGrpNotes(Composite parent) {

		Group grpNotes = new Group(parent, SWT.NONE);
		grpNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpNotes.setText("Prescription Notes");
		grpNotes.setBounds(new Rectangle(351, 15, 279, 107));

		txtAreaNotes = new Text(grpNotes, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		txtAreaNotes.setBounds(new Rectangle(15, 19, 249, 79));
		txtAreaNotes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtAreaNotes.setEditable(false);
		txtAreaNotes.setEnabled(false);

	}

	/**
	 * This method initializes grpPackageInfo
	 */
	private void createGrpPackageInfo() {

		Group grpPackageInfo = new Group(getShell(), SWT.NONE);
		grpPackageInfo.setBounds(new Rectangle(591, 129, 294, 137));
		grpPackageInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPicLeftArrow = new Label(grpPackageInfo, SWT.NONE);
		lblPicLeftArrow.setBounds(new Rectangle(21, 20, 40, 34));
		lblPicLeftArrow.setImage(ResourceUtils
				.getImage(iDartImage.ARROWRIGHT_40X34));
		lblPicLeftArrow.setVisible(false);

		lblPackageInfo1 = new Label(grpPackageInfo, SWT.CENTER);
		lblPackageInfo1.setText("This is package number");
		lblPackageInfo1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPackageInfo1.setBounds(new Rectangle(71, 10, 150, 20));

		lblPicRightArrow = new Label(grpPackageInfo, SWT.NONE);
		lblPicRightArrow.setBounds(new Rectangle(231, 20, 40, 34));
		lblPicRightArrow.setImage(ResourceUtils
				.getImage(iDartImage.ARROWLEFT_40X34));
		lblPicRightArrow.setVisible(false);
		// txtPrescriptionDate.setText("Initial Pickup");
		lblIndex = new Label(grpPackageInfo, SWT.RIGHT);
		lblIndex.setText(" ");
		lblIndex.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblIndex.setBounds(new Rectangle(93, 30, 33, 20));

		lblPackageInfo2 = new Label(grpPackageInfo, SWT.CENTER);
		lblPackageInfo2.setText(" of a ");
		lblPackageInfo2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPackageInfo2.setBounds(new Rectangle(130, 30, 40, 20));

		lblDuration = new Label(grpPackageInfo, SWT.NONE);
		lblDuration.setText(" ");
		lblDuration.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDuration.setBounds(new Rectangle(170, 30, 32, 21));

		lblPackageInfo3 = new Label(grpPackageInfo, SWT.CENTER);
		lblPackageInfo3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPackageInfo3.setText("month prescription");
		lblPackageInfo3.setBounds(new Rectangle(89, 50, 111, 20));

		Label lblSupply1 = new Label(grpPackageInfo, SWT.LEFT);
		lblSupply1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblSupply1.setText("Package Contains: ");
		lblSupply1.setBounds(new Rectangle(4, 75, 105, 20));

		cmbSupply = new CCombo(grpPackageInfo, SWT.BORDER);
		cmbSupply.setBounds(new Rectangle(111, 75, 105, 20));
		cmbSupply.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbSupply.setEditable(false);
		cmbSupply.setVisibleItemCount(10);
		CommonObjects.populatePrescriptionDuration(getHSession(), cmbSupply);
		cmbSupply.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbSupply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {

				cmbWeeksSupplyChanged();

			}
		});

		lblSupply1 = new Label(grpPackageInfo, SWT.LEFT);
		lblSupply1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblSupply1.setText("  supply.");
		lblSupply1.setBounds(new Rectangle(215, 76, 72, 20));

		// Capture Date
		Label lblCaptureDate = new Label(grpPackageInfo, SWT.NONE);
		lblCaptureDate.setBounds(new Rectangle(4, 101, 72, 20));
		lblCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCaptureDate.setText("Date Packed:");

		btnCaptureDate = new DateButton(
				grpPackageInfo,
				DateButton.NONE,
				new DateInputValidator(DateRuleFactory.beforeNowInclusive(true)));
		btnCaptureDate.setBounds(new Rectangle(110, 100, 155, 25));
		btnCaptureDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCaptureDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				attemptCaptureDateReset();
				pillCountTable.update(btnCaptureDate.getDate());
			}
		});

		btnCaptureDate.setDate(new Date());

		// Why should this be here when the patient is loaded...
		// Are not the dates meant to be captured properly?
		// Therefore they should be loaded properly.
		attemptCaptureDateReset();
		if (fieldsEnabled) {
			setDateExpectedFields();
		}

	}

	/**
	 * This method initializes grpPatientInfo
	 */
	private void createGrpPatientInfo() {

		Group grpPatientInfo = new Group(getShell(), SWT.NONE);
		grpPatientInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPatientInfo.setText("Patient Information");
		grpPatientInfo.setBounds(new Rectangle(242, 129, 339, 137));

		Label lblPatientId = new Label(grpPatientInfo, SWT.NONE);
		lblPatientId.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 30,
				110, 20));
		lblPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$
		lblPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientId = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientId.setBounds(new Rectangle(135, 30, 160, 20));
		txtPatientId.setEnabled(false);
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblPatientName = new Label(grpPatientInfo, SWT.NONE);
		lblPatientName.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 55,
				110, 20));
		lblPatientName.setText("Patient's Name:");
		lblPatientName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientName = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientName.setBounds(new Rectangle(135, 55, 160, 20));
		txtPatientName.setEnabled(false);
		txtPatientName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblPatientAge = new Label(grpPatientInfo, SWT.NONE);
		lblPatientAge.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 80,
				110, 20));
		lblPatientAge.setText("Patient's Age:");
		lblPatientAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientAge = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientAge.setBounds(new Rectangle(135, 80, 40, 20));
		txtPatientAge.setEnabled(false);
		txtPatientAge.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPatientDOB = new Text(grpPatientInfo, SWT.BORDER);
		txtPatientDOB.setBounds(new org.eclipse.swt.graphics.Rectangle(180, 80,
				114, 20));
		txtPatientDOB.setEnabled(false);
		txtPatientDOB.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblPicChild = new Label(grpPatientInfo, SWT.NONE);
		lblPicChild.setBounds(new Rectangle(300, 75, 30, 26));
		lblPicChild.setImage(ResourceUtils.getImage(iDartImage.CHILD_30X26));
		lblPicChild.setVisible(false);

		// next appointment date
		lblNextAppointment = new Label(grpPatientInfo, SWT.NONE);
		lblNextAppointment.setBounds(new Rectangle(10, 107, 120, 20));
		lblNextAppointment
				.setText(iDartProperties.dispenseDirectlyDefault == true ? "Next Appointment:"
						: "Current Appointment:");
		lblNextAppointment.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtNextAppDate = new Text(grpPatientInfo, SWT.BORDER);
		txtNextAppDate.setBounds(new Rectangle(135, 105, 160, 20));
		txtNextAppDate.setEnabled(false);
		txtNextAppDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNextAppDate.setVisible(false);

		btnNextAppDate = new DateButton(grpPatientInfo,
				DateButton.ZERO_TIMESTAMP, null);
		btnNextAppDate.snapToControl(false);
		btnNextAppDate.setBounds(new Rectangle(135, 105, 160, 25));
		btnNextAppDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnNextAppDate.setVisible(false);
	}

	/**
	 * This method initializes grpPrescriptionInfo
	 */
	private void createGrpPrescriptionInfo() {

		Group grpPrescriptionInfo = new Group(getShell(), SWT.NONE);
		grpPrescriptionInfo.setBounds(new Rectangle(242, 277, 642, 128));
		grpPrescriptionInfo
				.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPrescriptionInfo.setText("Prescription Information");

		Label lblPrescriptionId = new Label(grpPrescriptionInfo, SWT.NONE);
		lblPrescriptionId.setBounds(new Rectangle(10, 22, 115, 20));
		lblPrescriptionId.setText("Prescription ID:");
		lblPrescriptionId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPrescriptionId = new Text(grpPrescriptionInfo, SWT.BORDER);
		txtPrescriptionId.setBounds(new Rectangle(126, 22, 150, 20));
		txtPrescriptionId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPrescriptionId.setEnabled(false);

		Label lblDoctor = new Label(grpPrescriptionInfo, SWT.NONE);
		lblDoctor.setBounds(new Rectangle(10, 47, 115, 20));
		lblDoctor.setText("Doctor:");
		lblDoctor.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDoctor = new Text(grpPrescriptionInfo, SWT.BORDER);
		txtDoctor.setBounds(new Rectangle(126, 47, 150, 20));
		txtDoctor.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDoctor.setEnabled(false);

		Label lblDateOfLastPickup = new Label(grpPrescriptionInfo, SWT.NONE);
		lblDateOfLastPickup.setBounds(new Rectangle(10, 72, 114, 20));
		lblDateOfLastPickup
				.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDateOfLastPickup.setText("Date of Last Pickup:");

		lblDateOfLastPickupContents = new Label(grpPrescriptionInfo, SWT.BORDER);
		lblDateOfLastPickupContents.setBounds(new Rectangle(126, 72, 150, 20));
		lblDateOfLastPickupContents.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		// lblPicPatientHistoryReport
		Button btnPatientHistoryReport = new Button(grpPrescriptionInfo,
				SWT.NONE);
		btnPatientHistoryReport.setBounds(new Rectangle(294, 73, 40, 40));
		btnPatientHistoryReport
				.setToolTipText("Press this button to view and / or print reports \nof patients' Prescription History.");
		btnPatientHistoryReport.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PATIENTHISTORY_30X26));

		btnPatientHistoryReport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdPatientHistoryWidgetSelected();
			}
		});

		// lblPicUpdatePrescription
		Button btnUpdatePrescription = new Button(grpPrescriptionInfo, SWT.NONE);
		btnUpdatePrescription.setBounds(new Rectangle(293, 23, 40, 40));
		btnUpdatePrescription
				.setToolTipText("Press this button to Update this Patient's Prescription");
		btnUpdatePrescription.setImage(ResourceUtils
				.getImage(iDartImage.PRESCRIPTIONNEW_30X26));
		btnUpdatePrescription.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdUpdatePrescriptionWidgetSelected();
			}
		});

		Label lblPrescriptionDate = new Label(grpPrescriptionInfo, SWT.NONE);
		lblPrescriptionDate.setBounds(new Rectangle(10, 97, 113, 20));
		lblPrescriptionDate.setText("Prescription Date:");
		lblPrescriptionDate
				.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPrescriptionDate = new Text(grpPrescriptionInfo, SWT.BORDER);
		txtPrescriptionDate.setBounds(new Rectangle(126, 97, 150, 20));
		txtPrescriptionDate
				.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPrescriptionDate.setEnabled(false);

		createGrpNotes(grpPrescriptionInfo);
	}

	private void createLastPackageTable(Composite parent) {
		pillCountTable = new PillCountTable(parent, SWT.NONE, getHSession(),
				new Rectangle(8, 11, 245, 160));
		pillCountTable.getTable().setBounds(new Rectangle(0, 19, 244, 140));
		pillCountTable.setHeading("2. Pill Count - Last Package");
		pillCountTable.setPillCountGroupHeading("");
		pillCountTable.addChangeListener(this);
	}

	/**
	 * This method initializes tblPrescriptionInfo
	 * 
	 * @param parent
	 *            Composite
	 */
	private void createPrescriptionInfoTable(Composite parent) {

		lnkStockOnHand = new Link(parent, SWT.BORDER);
		lnkStockOnHand.setBounds(new Rectangle(274, 11, 596, 21));
		lnkStockOnHand
				.setText("3. Drugs To Dispense in This Package:        "
						+ "                                            "
						+ "                                       <A>Stock On Hand</A>");
		lnkStockOnHand
				.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_BOLD));
		lnkStockOnHand.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				new StockOnHandGui(getShell(), cmbSelectStockCenter.getText());
			}
		});

		tblPrescriptionInfo = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		tblPrescriptionInfo.setBounds(new Rectangle(274, 31, 596, 140));
		tblPrescriptionInfo.setHeaderVisible(true);
		tblPrescriptionInfo.setLinesVisible(true);
		tblPrescriptionInfo
				.setToolTipText("Click a row to view additional dispensing information");

		tblPrescriptionInfo
				.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn column = new TableColumn(tblPrescriptionInfo, SWT.NONE);
		column.setText("Drug Name");
		column.setWidth(195);
		column.setResizable(true);

		column = new TableColumn(tblPrescriptionInfo, SWT.NONE);
		column.setText("Dosage");
		column.setWidth(160);
		column.setResizable(true);

		column = new TableColumn(tblPrescriptionInfo, SWT.NONE);
		column.setText("Disp");
		column.setWidth(40);
		column.setResizable(true);

		column = new TableColumn(tblPrescriptionInfo, SWT.NONE);
		column.setText("Packs in Stock");
		column.setWidth(90);
		column.setResizable(true);

		column = new TableColumn(tblPrescriptionInfo, SWT.NONE);
		column.setText("Labels");
		column.setWidth(50);
		column.setResizable(true);

		column = new TableColumn(tblPrescriptionInfo, SWT.NONE);
		column.setText("In Hand");
		column.setWidth(58);
		column.setResizable(true);

		attachPrescriptionInfoTableEditor();

	}

	/**
	 * Create the GUI and populate it with all patients that have a valid script
	 * at the mainclinic
	 */
	private void createScreen() {
		setScreenToInitialState();
	}

	/**
	 * This method initializes newPatientPackaging
	 */
	@Override
	protected void createShell() {
		init();
		String shellTxt = "Make Up Drug Packages for Patients";
		buildShell(shellTxt, new Rectangle(25, 0, 900, 700));
	}

	/**
	 * This method is used to check that at least 1 drug item has been
	 * dispensed. The pharmacist doesn't have to dispense everything on this
	 * prescription, but s/he does have to dispense at least 1 item.
	 * 
	 * 
	 * @return boolean
	 */
	private boolean drugQuantitiesOkay() {
		boolean quantitiesEntered = false;
		for (int i = 0; i < tblPrescriptionInfo.getItemCount(); i++) {
			TableItem ti = tblPrescriptionInfo.getItem(i);
			if (!ti.getText(3).equals("0")) {
				quantitiesEntered = true;
			}
		}
		return quantitiesEntered;
	}

	/**
	 * Method enableFields.
	 * 
	 * @param enable
	 *            boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
		txtAreaNotes.setEnabled(enable);
		cmbSupply.setEnabled(enable);
		btnDispense.setEnabled(enable);
		btnNextAppDate.setEnabled(enable);
		btnCaptureDate.setEnabled(enable);
		tblPrescriptionInfo.setEnabled(enable);
		if (pillCountTable != null && rdBtnDispenseNow.getSelection()) {
			pillCountTable.getTable().setEnabled(enable);
		}

		fieldsEnabled = enable;
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
	 * Method fieldsOkay.
	 * 
	 * @param allPackagedDrugsList
	 *            java.util.List<PackageDrugInfo>
	 * @return boolean
	 */
	private boolean fieldsOkay(java.util.List<PackageDrugInfo> allPackagedDrugsList) {
		Patient patient = PatientManager.getPatient(getHSession(), localPatient.getId());
		
		if (patient == null || txtPatientId.getText().equals("")) {
			showMessage(MessageDialog.ERROR, "No Patient Selected",
					"No patient has been selected. You need to choose one.");
			return false;
		} 
		
		if (!drugQuantitiesOkay()) {
			showMessage(MessageDialog.ERROR, "No Drug Quantities Dispensed",
					"You have not entered quantities for any of the drugs.");
			return false;
		} 
		
		if (btnCaptureDate.getDate().before(
				newPack.getPrescription().getDate())
				&& !(sdf.format(btnCaptureDate.getDate()).equals(sdf
						.format(newPack.getPrescription().getDate())))) {
			showMessage(MessageDialog.ERROR, "Invalid Pack Date!",
					"The Date Packed cannot be before the Prescription Date "
							+ sdf.format(newPack.getPrescription().getDate())
							+ " ");
			return false;
		} 
		
		if (btnNextAppDate.getDate() != null
				&& btnNextAppDate.getDate().before(btnCaptureDate.getDate())
				&& !(sdf.format(btnCaptureDate.getDate()).equals(sdf
						.format(btnNextAppDate.getDate())))) {
			showMessage(MessageDialog.ERROR,
					"Next Appointment Date is before Date Packed",
					"The next appointment date cannot be before the date packed for this package.");
			return false;
		} 

		if (PackageManager
				.patientHasUncollectedPackages(
						getHSession(), patient)) {

			Date oldPackDate = PackageManager.getPackDateForUncollectedPackage(
					getHSession(),
					PatientManager.getPatient(getHSession(),
							txtPatientId.getText()));
			showMessage(
					MessageDialog.ERROR,
					"Package Already Created For Patient",
					"A package was already created for patient '"
							+ txtPatientId.getText()
							+ "' on "
							+ sdf.format(oldPackDate)
							+ " and it has not yet been received by the patient. "
							+ "\n\nYou can only create a new package for this patient "
							+ "when this previous package has either been collected or "
							+ "returned to the pharmacy (using the 'Return Uncollected Package' screen).");
			return false;
		} 

		if (dateAlreadyDispensed) {
			Packages lastPackageMade = PackageManager.getLastPackageMade(
					getHSession(), patient);
			// if package hasn't been collected yet or date is not today
			// http://dev.cell-life.org/jira/browse/IDART-14
			Date pickupDate = lastPackageMade.getPickupDate();
			if (pickupDate == null || iDARTUtil.hasZeroTimestamp(pickupDate)) {
				Date oldPackDate = lastPackageMade.getPackDate();

				showMessage(
						MessageDialog.ERROR,
						"Package Already Created On This Day",
						"A package was already created for patient '"
								+ txtPatientId.getText()
								+ "' on "
								+ sdf.format(oldPackDate)
								+ ".\n\niDART will not allow you to create another package for this patient on the same day, "
								+ "as the time at which they were packed will be the same. "
								+ "This causes problems with the pill count feature. ");
				return false;
			}
		}
		
		// open temporary session
		Session sess = HibernateUtil.getNewSession();
		for (PackageDrugInfo pdi : allPackagedDrugsList) {
			if (!checkStockStillAvailable(sess, pdi)) {
				showMessage(
						MessageDialog.ERROR,
						"Selected stock batch empty.",
						"The batch of stock selected for "
								+ pdi.getDrugName()
								+ " no longer contains any stock.\n\nPlease select another batch.");
				return false;
			}
		}

		// close temp session
		sess.close();

		for (PackageDrugInfo pdi : allPackagedDrugsList) {
			if (!checkForBreakingPacks(pdi)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Checks that there is still stock available.
	 * 
	 * @param sess
	 * @param pdi
	 * @return
	 */
	private boolean checkStockStillAvailable(Session sess, PackageDrugInfo pdi) {
		Stock stock = StockManager.getStock(sess, pdi.getStockId());
		if ('T' == stock.getHasUnitsRemaining()) {
			StockLevel sl = StockManager.getCurrentStockLevel(sess, stock);
			if (sl == null){
				return false;
			}
			int unitsRemaining = sl.getFullContainersRemaining()
					* stock.getDrug().getPackSize()
					+ sl.getLoosePillsRemaining();
			if (unitsRemaining >= pdi.getDispensedQty())
				return true;
		}
		return false;
	}

	private boolean checkForBreakingPacks(PackageDrugInfo pdi) {
		boolean fieldsOk = true;
		if ((pdi.getFormLanguage1().toUpperCase().startsWith("ML"))
				&& ((pdi.getDispensedQty() % DrugManager.getDrug(getHSession(),
						pdi.getDrugName()).getPackSize()) != 0)) {
			MessageBox m = new MessageBox(getShell(), SWT.YES | SWT.NO
					| SWT.ICON_QUESTION);
			m.setMessage("You are going to dispense " + pdi.getDispensedQty()
					+ " ml of " + pdi.getDrugName()
					+ ". Note that this means that a pack is being broken. "
					+ "\n\nAre you sure you want to break a pack?");
			m.setText("Dispensing Broken Pack");

			switch (m.open()) {
			case SWT.YES:
				fieldsOk = true;
				break;
			case SWT.NO:
				fieldsOk = false;
				break;
			}
		}
		return fieldsOk;
	}

	/**
	 * Method getAccumDrugsToSave.
	 * 
	 * @return Set<AccumulatedDrugs>
	 */
	private Set<AccumulatedDrugs> getAccumDrugsToSave() {

		java.util.Set<AccumulatedDrugs> adsToSave = new HashSet<AccumulatedDrugs>();
		for (int i = 0; i < tblPrescriptionInfo.getItemCount(); i++) {

			TableItem item = tblPrescriptionInfo.getItem(i);

			try {
				int inHand = Integer.parseInt(item.getText(5));
				int disp = Integer.parseInt(item.getText(2));

				if (inHand > disp) {

					AccumulatedDrugs ad = new AccumulatedDrugs();

					ad.setWithPackage(newPack);

					// find the pillcount
					String drugName = item.getText(0);
					Set<PillCount> pillCounts = pillCountTable.getPillCounts();
					for (PillCount count : pillCounts) {
						if (count.getDrug().getName().equals(drugName)) {
							ad.setPillCount(count);
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
			} catch (NumberFormatException p) {
				getLog().error(
						"NumberFormatError saving accumulated drug for "
								+ item.getText(0));
			}

		}
		return adsToSave;
	}

	/**
	 * Method getDispensedQuantity.
	 * 
	 * @param theStockList
	 *            java.util.List<PackageDrugInfo>
	 * @param ti
	 *            TableItem
	 */
	private void getDispensedQuantity(
			java.util.List<PackageDrugInfo> theStockList, TableItem ti) {

		int totalDispensedQty = 0;
		int totalNumberOfLabels = 0;

		// also going to reset inHand
		int previousDispensedQuantity = Integer.parseInt(ti.getText(2));
		int previousInHand = Integer.parseInt(ti.getText(5));
		for (int i = 0; i < theStockList.size(); i++) {

			PackageDrugInfo pdi = theStockList.get(i);
			if (ti.getText(0).equals(pdi.getDrugName())) {

				totalDispensedQty += pdi.getDispensedQty();
				totalNumberOfLabels += pdi.getNumberOfLabels();
			}

		}
		ti.setText(2, String.valueOf(totalDispensedQty));
		ti.setText(4, String.valueOf(totalNumberOfLabels));
		ti.setText(
				5,
				String.valueOf(totalDispensedQty
						+ (previousInHand - previousDispensedQuantity)));

		/*
		 * set the foreground colour to ResourceUtils.getColor(iDartColor.BLACK)
		 * since the user has indicated what batch the drug should come from
		 */
		if (totalDispensedQty != 0) {
			ti.setForeground(ResourceUtils.getColor(iDartColor.BLACK));
		} else {
			ti.setForeground(ResourceUtils.getColor(iDartColor.RED));

		}
	}

	/**
	 * Method getPossibleInHandOnExitValues.
	 * 
	 * @param ti
	 *            TableItem
	 * @return java.util.List<String>
	 */
	private void savePillCounts() {

		Set<PillCount> pcnts = pillCountTable.getPillCounts();
		// Setting the proper date for the pill counts
		if (pcnts != null) {
			for (PillCount pc : pcnts) {
				pc.setDateOfCount(btnCaptureDate.getDate());
			}
			pillFacade.save(pcnts);
			EventBusService.publish(new AdherenceEvent(pcnts));
		}

	}

	private java.util.List<String> getPossibleInHandOnExitValues(TableItem ti) {
		String drugName = ti.getText(0);
		java.util.List<String> possibleValuesList = new ArrayList<String>();
		if (rdBtnDispenseNow.getSelection()) {
			try {
				int disp = Integer.parseInt(ti.getText(2));

				Set<PillCount> pillCounts = pillCountTable.getPillCounts();
				for (PillCount count : pillCounts) {
					if (count.getDrug().getName().equals(drugName)) {
						int accum = count.getAccum();
						if (accum != -1) {
							possibleValuesList.add("" + (disp + accum));
						}
					}
				}
				possibleValuesList.add(disp + "");
			} catch (NumberFormatException n) {
				getLog().error("NumberFormatException parsing qty to dispense");
			}
		}
		return possibleValuesList;

	}

	/**
	 * This method is used to highlight the text describing this particular
	 * package (i.e. "This is package x of a n month's prescription"). If it is
	 * the last package of a prescription, the text would be displayed in
	 * ResourceUtils.getColor(iDartColor.RED) to highlight to the pharmacist
	 * that this patient is in need of another prescription
	 * 
	 * @param colorToHighlight
	 */
	private void highlightPackageInfo(Color colorToHighlight) {

		lblPackageInfo1.setForeground(colorToHighlight);
		lblIndex.setForeground(colorToHighlight);
		lblPackageInfo2.setForeground(colorToHighlight);
		lblDuration.setForeground(colorToHighlight);
		lblPackageInfo3.setForeground(colorToHighlight);

	}

	private void init() {
		sdf = new SimpleDateFormat("dd MMM yyyy");
		fieldsEnabled = false;
	}

	private void lstWaitingPatientsWidgetSelected() {
		clearForm();
		Runnable longJob = new Runnable() {
			@Override
			public void run() {
				StructuredSelection selection = (StructuredSelection) lstWaitingPatients.getSelection();
				if (selection != null && !selection.isEmpty()) {
					populatePatientDetails(((PatientIdAndName)selection.getFirstElement()).getId());
					cmbWeeksSupplyChanged();
				}
			}
		};
		BusyIndicator.showWhile(getShell().getDisplay(), longJob);
	}

	protected void populatePatientDetails(String patientID, boolean forceSelect) {
		java.util.List<PatientIdAndName> patients = null;
		if (patientID == null || patientID.trim().isEmpty()) {
			patients = SearchManager
					.getActivePatientWithValidPrescriptionIDsAndNames(getHSession());
		} else {
			patients = SearchManager.findPatientsWithIdLike(getHSession(),
					patientID);
		}
		
		lstWaitingPatients.setInput(patients);
		
		// Only try to load the patient if the user pressed enter.
		if (forceSelect) {
			if (patients.size() == 1) {
				populatePatientDetails(patients.get(0).getId());
			} else {
				String tmp = searchBar.getText();
				setScreenToInitialState();
				clearForm();
				searchBar.setText(tmp);
			}
		}
		
	}

	/**
	 * Checks to see the paitent belonging to the prescription has a package
	 * that has not yet been collected.
	 * 
	 * @param prescription
	 * @return true if patient has package awaiting collection
	 */
	private boolean patientHasPackageAwaitingPickup() {

		if (previousPack != null) {
			// Check if package was created but not picked up
			if (previousPack.getPickupDate() == null)
				return true;
		}
		return false;
	}

	/**
	 * This method populates the Patient Details and then runs the Populate
	 * Prescription Details details This method is run on startup and when the
	 * patient Id is clicked.
	 * 
	 * @param patientID
	 *            String
	 */
	private void populatePatientDetails(int patientID) {
		localPatient = PatientManager.getPatient(getHSession(), patientID);
		previousPack = null;
		searchBar.setText(localPatient.getPatientId());
		searchBar.setSelection(0, localPatient.getPatientId().length());
		lstWaitingPatients.setSelection(new StructuredSelection(
				new PatientIdAndName(localPatient.getId(), localPatient
						.getPatientId(), localPatient.getFirstNames() + ","
						+ localPatient.getLastname())));
		if (!localPatient.getAccountStatusWithCheck()
				|| localPatient.getCurrentPrescription() == null) {
			showMessage(MessageDialog.ERROR,
					"Patient can not be dispensed to.",
					"Patient is inactive or does not have a valid prescription.");
			initialiseSearchList();
			clearForm();
			return;
		}

		Clinic clinic = localPatient.getCurrentClinic();
		setDispenseTypeFromClinic();
		lblClinic.setText(clinic.getClinicName());
		txtPatientId.setText(localPatient.getPatientId());
		txtPatientName.setText(localPatient.getFirstNames() + " "
				+ localPatient.getLastname());
		txtPatientAge.setText("" + localPatient.getAge());
		txtPatientDOB.setText(new SimpleDateFormat("dd MMM yyyy")
				.format(localPatient.getDateOfBirth()));
		if (localPatient.getAge() < 15) {
			lblPicChild.setVisible(true);

		} else {
			lblPicChild.setVisible(false);

		}

		if (!iDartProperties.summaryLabelDefault) {
			rdBtnPrintSummaryLabelYes.setSelection(false);
			rdBtnPrintSummaryLabelNo.setSelection(true);
		} else {
			rdBtnPrintSummaryLabelYes.setSelection(true);
			rdBtnPrintSummaryLabelNo.setSelection(false);
		}

		Prescription pre = localPatient.getCurrentPrescription();
		if (pre == null) {
			MessageBox noScript = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			noScript.setText("Patient Does not Have a Vaild Prescription");
			noScript.setMessage("Patient '".concat(
					localPatient.getPatientId()).concat(
					"' does not have a vaild prescription."));
			noScript.open();
			initialiseSearchList();
			clearForm();
		} else {
			newPack = new Packages();
			newPack.setPrescription(pre);
			newPack.setClinic(localPatient.getCurrentClinic());
			// String theWeeks = cmbSupply.getText();
			//
			// int numPeriods =
			// Integer.parseInt(theWeeks.split(" ")[0]);
			//
			// if (theWeeks.endsWith("months") ||
			// theWeeks.endsWith("month")) {
			// numPeriods = numPeriods * 4;
			// }

			// weeks supply = script duration
			int numPeriods = pre.getDuration() >= 4 ? 4 : pre
					.getDuration();
			newPack.setWeekssupply(numPeriods);

			btnCaptureDate.setDate(new Date());
			newPack.setPackDate(btnCaptureDate.getDate());

			previousPack = PackageManager.getLastPackagePickedUp(
					getHSession(), localPatient);

			if (patientHasPackageAwaitingPickup()) {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Cannot Dispense to Patient");
				m.setMessage("You cannot dispense to patient "
						+ txtPatientId.getText()
						+ " since a package has already been made on "
						+ sdf.format(previousPack.getPackDate())
						+ " for this patient. "
						+ "\nThis package has not yet been collected by the "
						+ "patient. \n\n If this package is correct, please scan "
						+ "it out to the patient using the 'Scan Out Packages "
						+ "to Patients' screen. \n\nIf this package is NOT correct, "
						+ "please delete it using the 'Stock, Prescription & "
						+ "Package Deletions' screen.");

				m.open();
				clearForm();
				return;
			} else {
				populatePrescriptionDetails();
				populatePresciptionTable();
				prepopulateQuantities();
			}

			enableFields(true);

			resetGUIforDispensingType();

			pillFacade = new PillCountFacade(getHSession());

			if (previousPack != null && rdBtnDispenseNow.getSelection()) {
				pillCountTable.populateLastPackageDetails(previousPack,
						newPack.getPackDate());
			}

			if (rdBtnDispenseNow.getSelection()) {
				// Display the next appointment date if there is one,
				// or else base next appointment on todays date.
				Appointment nextApp = PatientManager
						.getLatestAppointmentForPatient(localPatient, true);
				if (nextApp == null) {
					Calendar theCal = Calendar.getInstance();
					attemptCaptureDateReset();
					theCal.setTime(newPack.getPackDate());
					theCal.add(Calendar.DATE, numPeriods * 7);
					adjustForNewDispDate(btnCaptureDate.getDate());
					adjustForNewAppointmentDate(theCal.getTime());
				}
			}
		}
	}

	private void setDispenseTypeFromClinic() {
		Clinic clinic = localPatient
				.getClinicAtDate(btnCaptureDate.getDate() != null ? btnCaptureDate
						.getDate() : new Date());
		if (clinic == null) {
			showMessage(
					MessageDialog.ERROR,
					"Patient Not Active on Date Selected ",
					"The patient did not have an open episode on the date you have specified so you are NOT able to dispense to them on this date.\n\n "
							+ "Please select a date when the patient was active.\n\n "
							+ "To see when the patient was active, look at their episodes in the 'Patient History Report' (press the button in the middle of this screen to load this report).");
			btnCaptureDate.setDate(new Date());
		} else {
			boolean isMainClinic = clinic.isMainClinic();
			
			rdBtnDispenseNow.setSelection(isMainClinic
					& iDartProperties.dispenseDirectlyDefault);
			rdBtnDispenseLater
					.setSelection(!(isMainClinic & iDartProperties.dispenseDirectlyDefault));
			
			resetGUIforDispensingType();
		}
	}

	/**
	 * Populates the prescription details on the form. This includes notes.
	 * 
	 * @param prescription
	 */
	private void populatePrescriptionDetails() {

		if (localPharmacy == null) {
			getLog().error(
					"Tried to populate prescription details, but localPharmacy is null");
			return;
		}
		if (newPack == null) {
			getLog().error(
					"Tried to populate prescription details, but pack is null");
			return;
		}
		if (newPack.getPrescription() == null) {
			getLog().error(
					"Tried to populate prescription details, but pack.prescription is null");
			return;
		}

		txtPrescriptionId
				.setText(newPack.getPrescription().getPrescriptionId());

		txtDoctor.setText(newPack.getPrescription().getDoctor().getFirstname()
				+ " " + newPack.getPrescription().getDoctor().getLastname());

		txtPrescriptionDate.setText(sdf.format(newPack.getPrescription()
				.getDate()));

		updateDateOfLastPickup();

		if (newPack.getPrescription().getNotes() != null) {
			txtAreaNotes.setText(newPack.getPrescription().getNotes());
		}
		int dur = newPack.getPrescription().getDuration();
		
		// Set the default index to 1 
		int index = 1;
		
		// Make sure that it is not a new prescription.
		if(previousPack != null && (previousPack.getPrescription().getId() == newPack.getPrescription().getId()))
			index = (previousPack.getNextIssueNo());

		// if the prescription is valid less than one month
		if (dur < 4) {
			lblDuration.setText(String.valueOf(dur));
			lblPackageInfo3.setText("week prescription");
			cmbSupply.setText(dur + " week");
			lblIndex.setText(String.valueOf(index));

			highlightPackageInfo(ResourceUtils.getColor(iDartColor.RED));
			lblPicLeftArrow.setVisible(true);
			lblPicRightArrow.setVisible(true);
		}
		// else, the prescription is valid for at least 1 month
		else {
			lblDuration.setText(String.valueOf(dur / 4));
			lblPackageInfo3.setText("month prescription");
			cmbSupply.setText("1 month");
			lblIndex.setText(String.valueOf(index));

			// check is prescription has been used up or if this is the last
			// repeat
			if ((dur / 4) <= index) {
				highlightPackageInfo(ResourceUtils.getColor(iDartColor.RED));
				lblPicLeftArrow.setVisible(true);
				lblPicRightArrow.setVisible(true);
			} else {
				lblPicLeftArrow.setVisible(false);
				lblPicRightArrow.setVisible(false);
			}
		}
	}

	/**
	 *
	 */
	private void updateDateOfLastPickup() {
		dateAlreadyDispensed = false;
		if (previousPack != null) {

			// Date of the [previous package] to be compared to the
			// current date, or the date set in the date picker
			Date lastPickupDate = previousPack.getPickupDate();

			// Date [selected using the package screen date picker].
			// This is the pack date, or the date captured as this
			// package is given to the patient.
			// A date before the previous package [is not allowed],
			// and this process should not continue.
			Date myPackDate = btnCaptureDate.getDate();

			// Test for pack date consistency.
			if (myPackDate != null && lastPickupDate != null
					&& iDARTUtil.before(myPackDate, lastPickupDate))
				// do not perform this procedure, just break,
				// since an earlier pack date is not allowed
				// pack date must be greater then last pickup date.
				return;

			int numOfDays = iDARTUtil.getDaysBetween(lastPickupDate,
					(myPackDate == null ? new Date() : myPackDate));

			lblDateOfLastPickupContents.setText(numOfDays
					+ " days ("
					+ new SimpleDateFormat("dd MMM yyyy")
							.format(lastPickupDate) + ")");

			if (numOfDays > ((previousPack.getWeekssupply() * 7) + 3)) {
				lblDateOfLastPickupContents.setForeground(ResourceUtils
						.getColor(iDartColor.RED));
				lblDateOfLastPickupContents.setBackground(ResourceUtils
						.getColor(iDartColor.WIDGET_BACKGROUND));
			} else if (numOfDays == 0) {
				lblDateOfLastPickupContents.setForeground(ResourceUtils
						.getColor(iDartColor.BLACK));
				lblDateOfLastPickupContents.setBackground(ResourceUtils
						.getColor(iDartColor.YELLOW));
				dateAlreadyDispensed = true;
			} else {
				lblDateOfLastPickupContents.setForeground(ResourceUtils
						.getColor(iDartColor.BLACK));
				lblDateOfLastPickupContents.setBackground(ResourceUtils
						.getColor(iDartColor.WIDGET_BACKGROUND));
			}

		}

		else {

			lblDateOfLastPickupContents.setText("Initial Pickup");
			lblDateOfLastPickupContents.setForeground(ResourceUtils
					.getColor(iDartColor.BLACK));
			lblDateOfLastPickupContents.setBackground(ResourceUtils
					.getColor(iDartColor.WIDGET_BACKGROUND));

		}

	}

	/**
	 * @param prescription
	 */
	private void populatePresciptionTable() {
		tblPrescriptionInfo.removeAll();
		String tempAmtPerTime = "";

		for (PrescribedDrugs pd : newPack.getPrescription()
				.getPrescribedDrugs()) {

			if (new BigDecimal(pd.getAmtPerTime()).scale() == 0) {
				tempAmtPerTime = ""
						+ new BigDecimal(pd.getAmtPerTime()).unscaledValue()
								.intValue();
			} else {
				tempAmtPerTime = "" + pd.getAmtPerTime();
			}

			Form theForm = pd.getDrug().getForm();

			TableItem ti = new TableItem(tblPrescriptionInfo, SWT.NONE);
			String[] tableEntry = new String[6];

			tableEntry[0] = pd.getDrug().getName();

			if (theForm.getFormLanguage1().equals("")) // is a cream, no amount
			// per time
			{
				tableEntry[1] = theForm.getActionLanguage1() + " "
						+ pd.getTimesPerDay() + " times a day.";
			} else {
				tableEntry[1] = theForm.getActionLanguage1() + " "
						+ tempAmtPerTime + " " + theForm.getFormLanguage1()
						+ " " + pd.getTimesPerDay() + " times a day.";
			}
			tableEntry[2] = "0";
			tableEntry[4] = "0";
			tableEntry[5] = "0";

			/*
			 * Highlight if drug isn't in stock
			 */
			if (1 <= 10) {
				ti.setForeground(ResourceUtils.getColor(iDartColor.RED));
			}

			ti.setText(tableEntry);
			ti.setData(new ArrayList<PackageDrugInfo>());

		}

		populateStockDetails();
	}

	/**
	 * Populates the units in stock column
	 */
	private void populateStockDetails() {
		if (localPharmacy == null) {
			getLog().error(
					"Tried to populate stock details, but localPharmacy is null");
			return;
		}
		if (newPack == null) {
			getLog().error("Tried to populate stock details, but pack is null");
			return;
		}
		if (newPack.getPrescription() == null) {
			getLog().error(
					"Tried to populate stock details, but pack.prescription is null");
			return;
		}

		int count = 0;

		for (PrescribedDrugs pd : newPack.getPrescription()
				.getPrescribedDrugs()) {

			TableItem ti = tblPrescriptionInfo.getItem(count);
			int[] unitsInStock = StockManager.getTotalStockLevelsForDrug(
					getHSession(), pd.getDrug(), localPharmacy);
			ti.setText(3, unitsInStock[0]
					+ (unitsInStock[1] > 0 ? " (" + unitsInStock[1] + " loose)"
							: ""));
			count++;
		}

	}

	/**
	 * 
	 * This method is used to prepopulate the drugs in a prescription with a
	 * default supply for the supply duration of the current pack eg. 60 tablets
	 * for a month. The default is one pack per month.
	 */
	private void prepopulateQuantities() {
		if (localPharmacy == null) {
			getLog().warn(
					"Tried to prepopulate quantities, but localPharmacy is null");
			return;
		}

		if (newPack == null) {
			getLog().warn("Tried to prepopulate quantities, but pack is null");
			return;
		}

		if (newPack.getPrescription() == null) {
			getLog().warn(
					"Tried to prepopulate quantities, but prescription is null");
			return;
		}

		Clinic theClinic = newPack.getPrescription().getPatient()
				.getCurrentClinic();

		int count = 0;

		pbLoading.setMinimum(0);
		pbLoading.setMaximum(newPack.getPrescription().getPrescribedDrugs()
				.size());

		for (PrescribedDrugs pd : newPack.getPrescription()
				.getPrescribedDrugs()) {

			Drug theDrug = pd.getDrug();

			int repeatNumber = Integer.parseInt(lblIndex.getText()
					.equalsIgnoreCase("") ? "0" : lblIndex.getText());

			int units = 0;
			int numlabels = 1;

			double unitsPerMonth;

			// for most items, which have an amountpertime, work out the
			// quanitity to prepopulates
			int packSize = theDrug.getPackSize();
			if (pd.getAmtPerTime() != 0) {
				if ((packSize % 28) == 0) {
					unitsPerMonth = pd.getAmtPerTime() * pd.getTimesPerDay()
							* 28;
				} else {
					unitsPerMonth = pd.getAmtPerTime() * pd.getTimesPerDay()
							* 30;
				}

				// round units up to multiple of packSize
				if (iDartProperties.roundUpForms.contains(theDrug.getForm()
						.getForm()) && unitsPerMonth % packSize != 0) {
					unitsPerMonth = (Math.floor(unitsPerMonth / packSize) + 1)
							* packSize;
				}

				switch (newPack.getWeekssupply()) {

				case 1: // 1 week supply
					
					// First, calculate the number of units required for the total supply
					units = (int) (pd.getAmtPerTime() * pd.getTimesPerDay() * 7);
					
					// round units up to multiple of packSize
					if (iDartProperties.roundUpForms.contains(theDrug.getForm()
							.getForm())) {
						
						if(units % packSize != 0) {
							int noOfPacks = units / packSize;
							units = (noOfPacks + 1 ) * packSize;
							
						}
					}
						
					break;

				case 2: // half a month (14 or 15 days) supply
					
					// First, calculate the number of units required for the total supply
					units = (int) Math.ceil(unitsPerMonth / 2);
					
					// round units up to multiple of packSize
					if (iDartProperties.roundUpForms.contains(theDrug.getForm()
							.getForm())) {
						if(units % packSize != 0) {
							int noOfPacks = units / packSize;
							units = (noOfPacks + 1 ) * packSize;
							
						}
					}
					
					break;

				default:
					// First, calculate the number of units required for the total supply
					if ((packSize % 28) == 0) {
						units = (int) ((newPack.getWeekssupply() / 4) * (pd.getAmtPerTime() * pd.getTimesPerDay()
								* 28));
					} else {
						units = (int) ((newPack.getWeekssupply() / 4) * (pd.getAmtPerTime() * pd.getTimesPerDay()
								* 30));
					}
					// Next round up syrup if required.
					if (iDartProperties.roundUpForms.contains(theDrug.getForm()
							.getForm())) {
						
						if(units % packSize != 0) {
							int noOfPacks = units / packSize;
							units = (noOfPacks + 1 ) * packSize;
							
						}
					}
					
				}
				
				numlabels = (int) Math.ceil(((double) units / packSize));
				
			} else { // for side treatment items that don't have an
				// amountpertime e.g. creams (Apply 3 times a day), set
				// to 1 pack and 1 label
				units = packSize;
				numlabels = 1;
			}

			Stock theSock = StockManager.getSoonestExpiringStock(getHSession(),
					theDrug, units, localPharmacy);
			TableItem ti = tblPrescriptionInfo.getItem(count);

			if (theSock != null) {
				DecimalFormat df = new DecimalFormat();
				df.setDecimalSeparatorAlwaysShown(false);
				String amtPerTimeString = df.format(pd.getAmtPerTime());

				PackageDrugInfo pdi = new PackageDrugInfo(
						amtPerTimeString,
						theSock.getBatchNumber(),
						theClinic.getClinicName(),
						units,
						theDrug.getForm().getFormLanguage1(),
						theDrug.getForm().getFormLanguage2(),
						theDrug.getForm().getFormLanguage3(),
						theDrug.getName(),
						theSock.getExpiryDate(),
						theClinic.getNotes(),
						txtPatientId.getText(),
						localPatient.getFirstNames(),
						localPatient.getLastname(),
						theDrug.getDispensingInstructions1(),
						theDrug.getDispensingInstructions2(),
						theSock.getId(),
						pd.getTimesPerDay(),
						numlabels,
						theDrug.getSideTreatment() == 'T' ? true : false,
						LocalObjects.getUser(getHSession()),
						new Date(),
						repeatNumber,
						newPack.getWeekssupply(),
						null,
						PackageManager.getQuantityDispensedForLabel(newPack
								.getAccumulatedDrugs(), units, theSock
								.getDrug().getName(), theSock.getDrug()
								.getPackSize(), false, true),
						PackageManager.getQuantityDispensedForLabel(newPack
								.getAccumulatedDrugs(), units, theSock
								.getDrug().getName(), units, false, true),
						PackageManager.getQuantityDispensedForLabel(newPack
								.getAccumulatedDrugs(), units, theSock
								.getDrug().getName(), theSock.getDrug()
								.getPackSize(), true, true),
						newPack.getPrescription().getDuration(),
						txtNextAppDate.getText(), newPack.getPackageId());

				ti.setText(2, (new Integer(units)).toString());

				ti.setText(4, (new Integer(numlabels)).toString());

				ti.setText(5, (new Integer(units)).toString());

				ti.setForeground(ResourceUtils.getColor(iDartColor.BLACK));

				java.util.List<PackageDrugInfo> pdiList = new ArrayList<PackageDrugInfo>();
				pdiList.add(pdi);
				ti.setData(pdiList);

			} else {
				ti.setData(new ArrayList<PackageDrugInfo>());

				ti.setText(2, "0");

				ti.setText(4, "0");

				ti.setText(5, "0");

				ti.setForeground(ResourceUtils.getColor(iDartColor.RED));
			}
			count++;
			pbLoading.setSelection(count);

		}

	}

	/**
	 * Method resetGUIforDispensingType.
	 * 
	 * @param isDispenseNow
	 *            boolean
	 */
	private void resetGUIforDispensingType() {
		if (!rdBtnDispenseNow.getSelection()) {
			pillCountTable.clearTable();
			Control old2 = editorTblPrescriptionInfo.getEditor();
			if (old2 != null) {
				old2.dispose();
			}
			pillCountTable.getTable().setEnabled(false);
			// Disabling the label printing.
			rdBtnYesAppointmentDate.setEnabled(false);
			rdBtnYesAppointmentDate.setSelection(false);
			rdBtnNoAppointmentDate.setEnabled(false);
			rdBtnNoAppointmentDate.setSelection(false);
		} else {
			if (previousPack != null) {
				pillCountTable.getTable().setEnabled(true);
				pillCountTable.populateLastPackageDetails(previousPack,
						newPack.getPackDate());
			}
			// Re-enabling the printing of the next appointment date.
			if(iDartProperties.nextAppointmentDateOnLabels) {
				rdBtnYesAppointmentDate.setEnabled(true);
				rdBtnYesAppointmentDate.setSelection(true);
				rdBtnNoAppointmentDate.setEnabled(true);
				rdBtnNoAppointmentDate.setSelection(false);
			}
			else {
				rdBtnYesAppointmentDate.setEnabled(true);
				rdBtnYesAppointmentDate.setSelection(false);
				rdBtnNoAppointmentDate.setEnabled(true);
				rdBtnNoAppointmentDate.setSelection(true);
			}
			
		}
		setDateExpectedFields();
	}

	/**
	 * Method savePackageAndPackagedDrugs.
	 * 
	 * @param dispenseNow
	 *            boolean
	 * @param allPackageDrugsList
	 *            java.util.List<PackageDrugInfo>
	 */
	private void savePackageAndPackagedDrugs(boolean dispenseNow,
			java.util.List<PackageDrugInfo> allPackageDrugsList) {

		// if pack date is today, store the time too, else store 12am
		Date today = new Date();
		Date packDate = new Date();
		packDate.setTime(newPack.getPackDate().getTime());

		if (DateFieldComparator.compare(today, packDate, Calendar.DAY_OF_MONTH) == 0) {
			newPack.setPickupDate(new Date());
		}

		newPack.setPackageId(newPack.getPrescription().getPrescriptionId()
				+ "-" + lblIndex.getText());
		newPack.setModified('T');
		
		int numPeriods = getSelectedWeekSupply();
		getLog().info("getSelectedWeekSupply() called");
		newPack.setWeekssupply(numPeriods);
		/*
		 * If the pharmacist is giving the drugs to the patient now, set the
		 * dateLeft, dateReceived and pickupDate to today. Else ... set these
		 * attributes to null (they will be set when the packages have left the
		 * pharmacy, arrived at the remote clinic, and when the patient has
		 * picked up their medications
		 */
		if (dispenseNow) {
			newPack.setDateLeft(newPack.getPackDate());
			newPack.setDateReceived(newPack.getPackDate());
			newPack.setPickupDate(newPack.getPackDate());
		} else {
			if (iDartProperties.downReferralMode
					.equalsIgnoreCase(iDartProperties.OFFLINE_DOWNREFERRAL_MODE)) {
				newPack.setDateLeft(newPack.getPackDate());
				newPack.setDateReceived(newPack.getPackDate());
				newPack.setPickupDate(null);
			} else {
				newPack.setDateLeft(null);
				newPack.setDateReceived(null);
				newPack.setPickupDate(null);
			}
		}

		// Make up a set of package drugs for this particular package
		java.util.List<PackagedDrugs> packagedDrugsList = new ArrayList<PackagedDrugs>();

		for (int i = 0; i < allPackageDrugsList.size(); i++) {

			PackageDrugInfo pdi = allPackageDrugsList.get(i);
			PackagedDrugs pd = new PackagedDrugs();
			pd.setAmount(pdi.getDispensedQty());
			pd.setParentPackage(newPack);
			pd.setStock(StockManager.getStock(getHSession(), pdi.getStockId()));
			pd.setModified('T');
			packagedDrugsList.add(pd);
			if (rdBtnDispenseNow.getSelection()) {
				pdi.setDateExpectedString(sdf.format(btnNextAppDate.getDate()));
			} else {
				
				Appointment nextApp = PatientManager
				.getLatestAppointmentForPatient(newPack
						.getPrescription().getPatient(), true);
				
				if(nextApp != null)
					pdi.setDateExpectedString(sdf.format(nextApp.getAppointmentDate()));
			}
			pdi.setPackagedDrug(pd);
			pdi.setNotes(localPatient.getCurrentClinic().getNotes());
			pdi.setPackageId(newPack.getPackageId());

		}

		newPack.setPackagedDrugs(packagedDrugsList);
		newPack.setAccumulatedDrugs(getAccumDrugsToSave());

		// de-normalise table to speed up reports 
		if(newPack.hasARVDrug()) 
			newPack.setDrugTypes("ARV");
		
		PackageManager.savePackage(getHSession(), newPack);

	}

	/**
	 * Method setARVStartDate.
	 * 
	 * @param startDate
	 *            String
	 * @return PatientAttribute
	 */
	private PatientAttribute setARVStartDate(String startDate) {
		PatientAttribute pa = newPack.getPrescription().getPatient()
				.getAttributeByName(PatientAttribute.ARV_START_DATE);

		if (pa == null) {

			pa = new PatientAttribute();
			pa.setType(PatientManager.getAttributeTypeObject(getHSession(),
					PatientAttribute.ARV_START_DATE));
			pa.setPatient(newPack.getPrescription().getPatient());

		}

		pa.setValue(startDate);
		return pa;
	}

	private void setDateExpectedFields() {

		if (rdBtnDispenseNow.getSelection()) {

			lblNextAppointment.setText("Next Appointment:");
			btnNextAppDate.setEnabled(true);
			btnNextAppDate.setVisible(true);
			txtNextAppDate.setVisible(false);

			if (!cmbSupply.getText().equals("")) {
				int numPeriods = getSelectedWeekSupply();

				Calendar theCal = Calendar.getInstance();

				theCal.setTime(btnCaptureDate.getDate());
				theCal.add(Calendar.DATE, numPeriods * 7);
				adjustForNewAppointmentDate(theCal.getTime());
				adjustForNewDispDate(btnCaptureDate.getDate());
			}
		}

		// else, patient is inactive
		else {
			// set the colours for all the active fields
			lblNextAppointment.setText("Current Appointment:");
			btnNextAppDate.setVisible(false);
			txtNextAppDate.setVisible(true);

			if ((newPack != null) && (newPack.getPrescription() != null)) {
				Appointment nextApp = PatientManager
						.getLatestAppointmentForPatient(newPack
								.getPrescription().getPatient(), true);

				if (nextApp != null) {
					adjustForNewDispDate(btnCaptureDate.getDate());
					adjustForNewAppointmentDate(nextApp.getAppointmentDate());
				} else {
					txtNextAppDate.setText("");
				}
			}
		}
	}

	private void adjustForNewAppointmentDate(Date theNextAppDate) {
		Calendar theNextAppCal = Calendar.getInstance();
		theNextAppCal.setTime(theNextAppDate);

		if (rdBtnDispenseNow.getSelection()) {
			btnNextAppDate.setDate(theNextAppDate);
		} else {
			txtNextAppDate.setText(sdf.format(theNextAppDate));
		}
	}

	/**
	 * 
	 * Sets the in hand on exit column in the current package table.
	 * 
	 * @param pc
	 */
	public void setInHandOnExit(PillCount pc) {
		Drug theDrug = pc.getDrug();

		for (int i = 0; i < tblPrescriptionInfo.getItemCount(); i++) {

			String drugName = tblPrescriptionInfo.getItem(i).getText(0);

			if (drugName.equals(theDrug.getName())) {
				try {
					int qty = Integer.parseInt(tblPrescriptionInfo.getItem(i)
							.getText(2));

					if (pc.getAccum() == -1) {
						tblPrescriptionInfo.getItem(i).setText(5, "" + qty);

					} else {
						if (iDartProperties.accumByDefault) {
							tblPrescriptionInfo.getItem(i).setText(5,
									"" + (pc.getAccum() + qty));
						} else {
							tblPrescriptionInfo.getItem(i).setText(5, "" + qty);
						}

					}

				} catch (NumberFormatException e) {
					getLog().error(
							"NumberFormatException parsing qty to dispense");
					tblPrescriptionInfo.getItem(i).setText(5, "");
				}

			}

		}
	}

	/**
	 * 
	 * Clears the in hand on exit column in the current package table.
	 * 
	 * @param pc
	 */
	public void clearInHandOnExit() {

		for (int i = 0; i < tblPrescriptionInfo.getItemCount(); i++) {

			try {
				int qty = Integer.parseInt(tblPrescriptionInfo.getItem(i)
						.getText(2));
				tblPrescriptionInfo.getItem(i).setText(5, "" + qty);

			} catch (NumberFormatException e) {
				getLog().error("NumberFormatException parsing qty to dispense");
				tblPrescriptionInfo.getItem(i).setText(5, "");
			}

		}
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	public void setNextAppointmentDate() {
		Date theNextAppDate = btnNextAppDate.getDate();
		Date captureDate = btnCaptureDate.getDate();

		Patient pat = newPack.getPrescription().getPatient();
		
		/*
		 * NOTE: we only get the current appointment date if the package is
		 * created for late pickup. If the package is dispensed directly, we
		 * create a new appointment
		 */
		if (rdBtnDispenseNow.getSelection()) {
			// if the next appointment is in the future, set it to the disp date
			PatientManager.setNextAppointmentDateAtVisit(getHSession(), pat,
					captureDate, theNextAppDate);
		}
	}

	private void setScreenToInitialState() {
		newPack = new Packages();
		newPack.setPackDate(new Date());
		btnCaptureDate.setDate(new Date());
		btnNextAppDate.setDate(new Date());
		lblDateOfLastPickupContents.setForeground(ResourceUtils
				.getColor(iDartColor.BLACK));
		lblDateOfLastPickupContents.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_BACKGROUND));
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

	/**
	 * Once the Submit button has been pressed, create the package, add in the
	 * packaged stock entries
	 * 
	 * @param dispenseNow
	 *            boolean
	 * @param allPackagedDrugsList
	 *            java.util.List<PackageDrugInfo>
	 */
	private void submitForm(boolean dispenseNow,
			java.util.List<PackageDrugInfo> allPackagedDrugsList) {

		Transaction tx = null;
		Map<Object, Integer> labelQuantities = new HashMap<Object, Integer>();
		try {
			tx = getHSession().beginTransaction();
			if (newPack.getPrescription() != null) {
				if (previousPack != null) {
					savePillCounts();
				}

				// Check if package contains ARV, and
				// do ARV Start date check.
				// Should be moved to a facade or manager class
				for (PackageDrugInfo info : allPackagedDrugsList) {
					if (!info.isSideTreatment()) {
						// Check the ARV Start Date if an
						// ARV package is present.
						checkARVStartDate();
						break;
					}
				}

				savePackageAndPackagedDrugs(dispenseNow, allPackagedDrugsList);
				getLog().info("savePackageAndPackagedDrugs() called");
				setNextAppointmentDate();

				if (allPackagedDrugsList.size() > 0) {

					// This map keeps a track of drugs dispensed in separate
					// batches
					Map<String, String> drugNames = new HashMap<String, String>();

					// Update pdi's to include accum drugs
					for (PackageDrugInfo info : allPackagedDrugsList) {

						info.setQtyInHand(PackageManager
								.getQuantityDispensedForLabel(
										newPack.getAccumulatedDrugs(),
										info.getDispensedQty(),
										info.getDrugName(), info
												.getPackagedDrug().getStock()
												.getDrug().getPackSize(),
										false, true));

						labelQuantities.put(info, 1);
						// set the String that will print out on each drug label
						// to indicate
						// (<amount dispensed> + <accumulated amount>)

						if (drugNames.containsKey(info.getDrugName())) {
							// not first batch, exclude pillcount value
							info.setSummaryQtyInHand(PackageManager
									.getQuantityDispensedForLabel(
											newPack.getAccumulatedDrugs(),
											info.getDispensedQty(),
											info.getDrugName(),
											info.getDispensedQty(), false,
											false));
						} else {

							info.setSummaryQtyInHand(PackageManager
									.getQuantityDispensedForLabel(
											newPack.getAccumulatedDrugs(),
											info.getDispensedQty(),
											info.getDrugName(),
											info.getDispensedQty(), false, true));
						}
						drugNames.put(info.getDrugName(), "test");
						// set the String that will print out on the
						// prescription summary label to indicate
						// for each drug the (<total amount dispensed> + <total
						// accumulated amount>)

						// before printing the labels, save pdi List
						TemporaryRecordsManager.savePackageDrugInfosToDB(
								getHSession(), allPackagedDrugsList);
						getHSession().flush();

					}
				}
				tx.commit();
				
				EventBusService
						.publish(new PackageEvent(
								dispenseNow ? PackageEvent.Type.PACKAGE_AND_PICKUP
										: PackageEvent.Type.PACKAGE_FOR_LATER,
								newPack));
				
				if (iDartProperties.printDrugLabels) {
					// Add the qty for the summary label
					labelQuantities.put(ScriptSummaryLabel.KEY,
							rdBtnPrintSummaryLabelYes.getSelection() ? 1 : 0);
					//Add the qty for the next appointment
					labelQuantities.put(CommonObjects.NEXT_APPOINTMENT_KEY, ( (rdBtnDispenseLater.getSelection()  ||  rdBtnYesAppointmentDate.getSelection()) ? 1 : 0));
					// Add the qty for the package label
					labelQuantities.put(PackageCoverLabel.KEY,
							rdBtnDispenseLater.getSelection() ? 1 : 0);
					// print labels
					PackageManager.printLabels(getHSession(),
							allPackagedDrugsList, labelQuantities);
				}
			}
		} catch (HibernateException he) {
			getLog().error("Problem with Saving this package", he);
			if (tx != null) {
				tx.rollback();
			}

			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("Problem Saving Package");
			m.setMessage("There was a problem saving this package of drugs.");

			m.open();
		}

		setScreenToInitialState();

		tblPrescriptionInfo.clearAll();
		pillCountTable.clearTable();

	}

	/**
	 * If the patient meets the following requirements the user is prompted
	 * whether or not to add an ARVstartDate to the patient.
	 * <ul>
	 * <li>Patient does not have any previous packages that contain ARV drugs.</li>
	 * <li>Patient does not already have an ARVStartDate attribute.</li>
	 * <li>Pharmacist is dispesing for direct pickup.</li>
	 * <li>The start reason for the patient's most recent episode is 'New
	 * Patient'</li>
	 * </ul>
	 * 
	 * @throws HibernateException
	 */
	private void checkARVStartDate() throws HibernateException {
		Patient pat = newPack.getPrescription().getPatient();
		if (PackageManager.getMostRecentARVPackage(getHSession(), pat) == null
				&& (pat.getAttributeByName(PatientAttribute.ARV_START_DATE)) == null
				&& rdBtnDispenseNow.getSelection()
				&& (Episode.REASON_NEW_PATIENT.equalsIgnoreCase(PatientManager
						.getMostRecentEpisode(pat).getStartReason()))) {

			getLog().info(
					"ARV start date not set, asking user if it should be set to this package's pack date");

			MessageBox mbox = new MessageBox(getShell(), SWT.YES | SWT.NO
					| SWT.ICON_QUESTION);
			mbox.setText("ARV Start Date Not Set");
			mbox.setMessage("The ARV start date has not yet been set and this is the first time patient '"
					+ txtPatientId.getText()
					+ "' is receiving ARV drugs. \n\nWould you like to set the ARV start date to "
					+ btnCaptureDate.getText() + " now?");

			switch (mbox.open()) {
			case SWT.YES:
				PatientManager.addPatientAttributeToPatient(getHSession(), pat,
						setARVStartDate(btnCaptureDate.getText()));
				getLog().info("ARV start date has been set");
				break;
			}
		}
	}
}
