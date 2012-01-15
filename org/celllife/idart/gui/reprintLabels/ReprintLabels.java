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

package org.celllife.idart.gui.reprintLabels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.manager.PackageManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.reprintLabels.tableViewerUtils.LabelEditingSupport;
import org.celllife.idart.gui.reprintLabels.tableViewerUtils.ReprintLabelsViewModel;
import org.celllife.idart.gui.search.PatientSearch;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.celllife.idart.print.label.PackageCoverLabel;
import org.celllife.idart.print.label.PatientInfoLabel;
import org.celllife.idart.print.label.PrintLabel;
import org.celllife.idart.print.label.ScriptSummaryLabel;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 */
public class ReprintLabels extends GenericOthersGui {
	
	private Label lblSelectPatient;

	private Text txtPatientId;

	private Button btnSearch;

	private Button btnPrintLabel;

	private Patient localPatient;

	private TableViewer tblViewer;

	private static String[] columnNames = { "Label Description",
	"No. of Labels" };

	private List<PackageDrugInfo> allPackagedDrugsList = new ArrayList<PackageDrugInfo>();

	private final List<ReprintLabelsViewModel> tableData = new ArrayList<ReprintLabelsViewModel>();

	private boolean dispensedDirectly = true;

	/***************************************************************************
	 * Default Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public ReprintLabels(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		activate();
	}

	/**
	 * This method initializes newPrintBlankLabel
	 */
	@Override
	protected void createShell() {
		buildShell("Reprint Patient's Labels", new Rectangle(0, 0, 600, 570));
		loadLastPackage();
	}

	private void loadLastPackage() {
		localPatient = PackageManager.getLastPatientDispensedToByUser(getHSession(), LocalObjects.getUser(getHSession()));
		if (localPatient == null) {
			cmdClearSelected();
			return;
		}
		loadLabelInfo();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		buildCompHeader("Reprint Patient's Labels", iDartImage.PATIENTINFOLABEL);
	}

	/**
	 * This method initializes compOptions
	 * 
	 */
	@Override
	protected void createCompOptions() {

		// Patient ID
		lblSelectPatient = new Label(getShell(), SWT.NONE);
		lblSelectPatient.setBounds(new Rectangle(100, 82, 90, 20));
		lblSelectPatient.setText("Select a Patient");
		lblSelectPatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPatientId = new Text(getShell(), SWT.BORDER);
		txtPatientId.setFocus();

		txtPatientId.setBounds(new Rectangle(195, 80, 150, 20));
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

		btnSearch = new Button(getShell(), SWT.NONE);
		btnSearch.setBounds(new Rectangle(360, 76, 110, 28));
		btnSearch.setText("Patient Search");
		btnSearch
		.setToolTipText("Press this button to search for an existing patient.");

		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});

		tblViewer = new TableViewer(getShell(), SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		tblViewer.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(50, 130, 500, 330));
		tblViewer.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblViewer.setContentProvider(new ArrayContentProvider());
		tblViewer.getTable().setHeaderVisible(true);
		tblViewer.getTable().setLinesVisible(true);
		tblViewer.getTable().setEnabled(false);
		createColumns(tblViewer);

	}

	/**
	 * Creates the table layout
	 * 
	 * @param viewer
	 */
	public void createColumns(final TableViewer viewer) {

		// Column 0: Label Description
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(400);
		column.getColumn().setText(columnNames[0]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				ReprintLabelsViewModel content = (ReprintLabelsViewModel) element;
				return content.getDisplayText();
			}

			@Override
			public Font getFont(Object element) {
				ReprintLabelsViewModel content = (ReprintLabelsViewModel) element;
				if (content.isDrug())
					return ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC);
				return super.getFont(element);
			}
		});

		// Column 1: Number of labeles
		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setWidth(100);
		column1.getColumn().setText(columnNames[1]);
		column1.getColumn().setMoveable(true);
		column1.setEditingSupport(new LabelEditingSupport(viewer, 1));
		column1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				ReprintLabelsViewModel content = (ReprintLabelsViewModel) element;
				if (content.getNum() == null)
					return "";
				return content.getNum().toString();
			}

			@Override
			public Font getFont(Object element) {
				ReprintLabelsViewModel content = (ReprintLabelsViewModel) element;
				if (content.isDrug())
					return ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC);
				return super.getFont(element);
			}

			@Override
			public Color getBackground(Object element) {
				ReprintLabelsViewModel content = (ReprintLabelsViewModel) element;
				if (content.getNum() != null)
					return ResourceUtils.getColor(iDartColor.WIDGET_BACKGROUND);
				return super.getBackground(element);
			}

		});
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnPrintLabel = new Button(getCompButtons(), SWT.PUSH);
		btnPrintLabel.setText("Print Label(s)");
		btnPrintLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPrintLabel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdPrintLabelSelected();
			}
		});
		btnPrintLabel
		.setToolTipText("Press this button to print the specified custom label.");
		btnPrintLabel.setEnabled(false);

		Button btnClear = new Button(getCompButtons(), SWT.PUSH);
		btnClear.setText("Clear");
		btnClear
		.setToolTipText("Press this button to clear all the information \nyou've entered), so that you can start again.");
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdClearSelected();
			}
		});
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Button btnClose = new Button(getCompButtons(), SWT.PUSH);
		btnClose.setText("Close");
		btnClose.setToolTipText("Press this button to close this screen.");
		btnClose
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseSelected();
			}
		});
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method is called when the user pressed the "Clear" button It clears
	 * the 2 input fields, enables the search button, and clears the information
	 * in the label preview
	 */
	private void cmdClearSelected() {
		txtPatientId.setText("");
		txtPatientId.setFocus();
		tblViewer.getTable().clearAll();
		tblViewer.getTable().removeAll();
		tblViewer.getTable().setEnabled(false);
		allPackagedDrugsList.clear();
		tableData.clear();
		btnPrintLabel.setEnabled(false);
	}

	/**
	 * This method is called when the user pressed the "Close" button It
	 * disposes the current shell.
	 */
	private void cmdCloseSelected() {
		closeShell(true);
	}

	/**
	 * This method is called when the user pressed the "Print" button It checks
	 * that a patient ID has been entered, and also that a number of prints has
	 * been chosen. If it succeeds these checks, it creates a label for this
	 * patient, and prints it n number of times
	 */
	private void cmdPrintLabelSelected() {
		// Add drug Label and summary Label quantities
		// tableQuantities = tableData.subList(1, tableData.size() - 2);
		Map<Object,Integer> labelMap = new HashMap<Object, Integer>();

		// add all drugs
		
		for (int i = 1; i < tableData.size(); i++) {
			ReprintLabelsViewModel model = tableData.get(i);
			if (model.getPdi() != null) {
				labelMap.put(model.getPdi(), model.getNum());
			}
		}
		
		int size = labelMap.size();
		
		// add summary label
		labelMap.put(ScriptSummaryLabel.KEY, tableData.get(size+1).getNum());
			
		boolean printedLabels = false;

		// If dispensed directly, the list is missing the package label
		if (dispensedDirectly) {
			labelMap.put(PackageCoverLabel.KEY, 0);
		}
		else {
			labelMap.put(PackageCoverLabel.KEY, tableData.get(size+2).getNum());
		}
		
		//Check if the next appointment date should go on the label
		if(iDartProperties.nextAppointmentDateOnLabels) {
			labelMap.put(CommonObjects.NEXT_APPOINTMENT_KEY, 1);
		}
		else {
			labelMap.put(CommonObjects.NEXT_APPOINTMENT_KEY, 0);
		}		
		
		if (checkForPackageLabels()) {
			PackageManager.printLabels(getHSession(), allPackagedDrugsList,
					labelMap);
			printedLabels = true;
		}
		
		if (checkForPatientLabels()) {

			ReprintLabelsViewModel obj = tableData.get(tableData.size() - 1);
			int noOfPatientLabels = obj.getNum();
			if (noOfPatientLabels > 0)
				printPatientInfoLabel(noOfPatientLabels);

			printedLabels = true;
		}
		if (!printedLabels) {
			MessageBox mb = new MessageBox(getShell());
			mb.setText("No Label Quantities Entered");
			mb
			.setMessage("You have not entered any label quantities to print. Please enter at least one quantity and press the Print Labels button.");
			mb.open();
			txtPatientId.setFocus();
			return;
		}

		cmdClearSelected();

	}

	/**
	 * Checks if the user selected to print any labels
	 * 
	 * @return
	 */
	private boolean checkForPatientLabels() {
		boolean foundQty = false;

		ReprintLabelsViewModel obj = tableData.get(tableData.size() - 1);
		Integer num = obj.getNum();
		if (num != null) {
			if (num > 0) {
				foundQty = true;
			}
		}

		return foundQty;
	}

	/**
	 * Checks if the user selected to print any labels
	 * 
	 * @return
	 */
	private boolean checkForPackageLabels() {
		boolean foundQty = false;

		for (int i = 0; i < tableData.size() - 1; i++) {
			Integer num = tableData.get(i).getNum();
			if (num != null) {
				if (num > 0) {
					foundQty = true;
				}
			}
		}
		return foundQty;
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}
	
	private void cmdSearchWidgetSelected() {

		String patientId = PatientBarcodeParser.getPatientId(txtPatientId
				.getText().trim());
		
		PatientSearch search = new PatientSearch(getShell(), getHSession());
		search.setShowInactive(true);
		PatientIdentifier identifier = search.search(patientId);
		
		if (identifier != null) {
			localPatient = identifier.getPatient();
			loadLabelInfo();
		}
	}

	private void loadLabelInfo() {
		// Before loading info first clear the screen
		cmdClearSelected();
		
		Packages previousPack = PackageManager.getMostRecentARVPackage(
				getHSession(), localPatient);
		if (previousPack == null) {
			MessageBox mbox = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			mbox.setText("Patient does not have a package");
			mbox.setMessage("The patient has not been dispensed to yet.");

			mbox.open();
			return;
		}
		allPackagedDrugsList = PackageManager.getPackageDrugInfoForPatient(
				getHSession(), localPatient.getPatientId(), previousPack
				.getPackageId());

		if (allPackagedDrugsList.size() == 0) {
			MessageBox mbox = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			mbox.setText("Label Information has not been saved");
			mbox
			.setMessage("The most recently printed labels have not yet been saved. You will not be able to reprint these labels at this time.");

			mbox.open();
			return;

		}
		tblViewer.getTable().setEnabled(true);
		btnPrintLabel.setEnabled(true);
		txtPatientId.setText(localPatient.getPatientId());
		createTableData();
	}

	private void createTableData() {
		// First Row
		PackageDrugInfo pdiTemp = allPackagedDrugsList.get(0);
		ReprintLabelsViewModel title = new ReprintLabelsViewModel("Most Recent Package (Created at "
			+ new SimpleDateFormat("h:mma").format(pdiTemp
					.getDispenseDate())
					+ " on "
					+ new SimpleDateFormat("dd MMM yyyy").format(pdiTemp
							.getDispenseDate()) + ")", null);
		tableData.add(title);
		
		for (PackageDrugInfo pdi : allPackagedDrugsList) {
			ReprintLabelsViewModel drug = new ReprintLabelsViewModel(pdi, 1);
			tableData.add(drug);
		}

		ReprintLabelsViewModel summary = new ReprintLabelsViewModel("      Script Summary Label", iDartProperties.summaryLabelDefault ? 1 : 0);
		tableData.add(summary);

		/**
		 * Check if the package was created for later pickup or dispensed
		 * directly
		 */
		if (pdiTemp.isDispensedForLaterPickup()) {
			tblViewer.setInput(tableData);

			ReprintLabelsViewModel packagelabel = new ReprintLabelsViewModel("      Package Label", 1);
			tableData.add(packagelabel);
			dispensedDirectly = false;
		}

		ReprintLabelsViewModel patientInfo = new ReprintLabelsViewModel("Patient Information Label",0);
		tableData.add(patientInfo);

		tblViewer.setInput(tableData);
	}

	private void printPatientInfoLabel(int noOfLabels) {
		// set up a patient info label
		Object myInfoLabel;
		myInfoLabel = new PatientInfoLabel(localPatient);
		ArrayList<Object> labelList = new ArrayList<Object>(1);

		for (int i = 0; i < noOfLabels; i++) {
			labelList.add(myInfoLabel);
		}

		try {
			PrintLabel.printiDARTLabels(labelList);
		} catch (Exception e) {
			getLog().error("Error printing patient info label", e);
		}
	}

}
