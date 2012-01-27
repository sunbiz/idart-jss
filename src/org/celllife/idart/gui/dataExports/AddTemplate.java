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

package org.celllife.idart.gui.dataExports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.manager.FileManager;
import model.manager.PatientManager;
import model.manager.exports.AppointmentColumnsGroup;
import model.manager.exports.EpisodeColumnsGroup;
import model.manager.exports.ExportColumn;
import model.manager.exports.ExportColumnGroup;
import model.manager.exports.PatientExportObject;
import model.manager.exports.PrescriptionColumnsGroup;
import model.manager.exports.ReportObject;
import model.manager.exports.SimpleColumn;
import model.manager.exports.columns.SimpleColumnsEnum;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.dataExports.listViewerUtils.ExportListViewerContentProvider;
import org.celllife.idart.gui.dataExports.listViewerUtils.ExportListViewerLabelProvider;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

/**
 */
public class AddTemplate extends GenericOthersGui {

	private Composite compDetails;

	private Button btnCancel;

	private Button btnSave;

	private Button btnClear;

	private Label lblExportName;

	private Text txtExportName;

	private CheckboxTableViewer tblViewerPatientAttributes;

	private Composite complexColumnComp;

	private CCombo cmbColumn;

	private Group grpSelectColumn;

	private Label lblPatientAttributes;

	private SimpleColumnsEnum[] patientFields;

	private iDARTChangeListener changeListener;

	private iDataExport EM;

	private ExportColumn exportColumn;

	private boolean updating = false;

	private String oldfilename = null;

	/**
	 * @param parent
	 */
	public AddTemplate(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	public void open() {
		activate();
		selectCompulsory();
	}

	@Override
	protected void createShell() {
		String shellTxt = "Make New Data Export";
		Rectangle bounds = new Rectangle(25, 0, 900, 700);
		buildShell(shellTxt, bounds);

		Session session = HibernateUtil.getNewSession();
		List<AttributeType> attributeTypes = PatientManager.getAllAttributeTypes(session);
		List<IdentifierType> identifierTypes = PatientManager.getAllIdentifierTypes(session);
		
		patientFields = new SimpleColumnsEnum[SimpleColumnsEnum.all.length
		                                      + attributeTypes.size() + identifierTypes.size()];
		for (int i = 0; i < SimpleColumnsEnum.all.length; i++) {
			patientFields[i] = SimpleColumnsEnum.all[i];
		}

		int index = SimpleColumnsEnum.all.length;
		for (AttributeType attributeType : attributeTypes) {
			patientFields[index++] = SimpleColumnsEnum
				.createFromAttributeType(attributeType);
		}
		
		for (IdentifierType idType : identifierTypes) {
			patientFields[index++] = SimpleColumnsEnum
				.createFromIdentifierType(idType);
		}

		createCompDetails();
	}

	@Override
	protected void createCompButtons() {
		btnSave = new Button(getCompButtons(), SWT.NONE);
		btnSave.setText("Save Export");
		btnSave.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSave.setToolTipText("Press this button to save the export.");
		btnSave
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSaveSelected();
			}
		});

		btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear.setText("Clear");
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdClearWidgetSelected();
			}
		});

		btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});

	}

	private void clearFields() {
		txtExportName.setText("");
		// Uncheck all selections in Patient Table
		tblViewerPatientAttributes.setAllChecked(false);
		// Check the Patient ID field in Patient Table
		tblViewerPatientAttributes.setChecked(patientFields[0], true);
	}

	public void createCompDetails() {
		compDetails = new Composite(getShell(), SWT.NONE);
		compDetails.setBounds(new Rectangle(25, 100, 850, 500));

		lblPatientAttributes = new Label(compDetails, SWT.CENTER | SWT.BORDER);
		lblPatientAttributes.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblPatientAttributes.setText("Patient Data");
		lblPatientAttributes.setBounds(new org.eclipse.swt.graphics.Rectangle(
				100, 30, 250, 20));

		tblViewerPatientAttributes = CheckboxTableViewer.newCheckList(
				compDetails, SWT.BORDER);
		tblViewerPatientAttributes.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(100, 50, 250, 350));
		tblViewerPatientAttributes.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblViewerPatientAttributes
		.setContentProvider(new ExportListViewerContentProvider());
		tblViewerPatientAttributes
		.setLabelProvider(new ExportListViewerLabelProvider());
		tblViewerPatientAttributes.setInput(patientFields);
		tblViewerPatientAttributes
		.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent arg0) {
				SimpleColumnsEnum element = (SimpleColumnsEnum) arg0
				.getElement();
				if (getCompulsory().contains(element)
						&& !arg0.getChecked()) {
					tblViewerPatientAttributes
					.setChecked(element, true);
				} else if (getDependents().contains(element)
						&& arg0.getChecked()) {
					selectDependents(true);
				} else if (getDependents().contains(element)
						&& !arg0.getChecked()) {
					selectDependents(false);
				}
			}
		});

		grpSelectColumn = new Group(getShell(), SWT.NONE);
		grpSelectColumn.setBounds(new Rectangle(500, 30, 251, 21));
		grpSelectColumn.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbColumn = new CCombo(compDetails, SWT.BORDER);
		cmbColumn.setBounds(new org.eclipse.swt.graphics.Rectangle(500, 30,
				250, 20));
		cmbColumn.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbColumn.setEditable(true);
		cmbColumn.add(EpisodeColumnsGroup.COLUMN_TYPE);
		cmbColumn.add(PrescriptionColumnsGroup.COLUMN_TYPE);
		cmbColumn.add(AppointmentColumnsGroup.COLUMN_TYPE);
		cmbColumn.select(0);

		cmbColumn
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				loadComplexColumnDetails(cmbColumn.getText());
			}
		});

		complexColumnComp = new Composite(compDetails, SWT.BORDER);
		complexColumnComp.setBounds(new Rectangle(500, 50, 250, 350));

		lblExportName = new Label(compDetails, SWT.CENTER);
		lblExportName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblExportName.setText("Export Name");
		lblExportName.setBounds(new org.eclipse.swt.graphics.Rectangle(280,
				442, 75, 20));

		txtExportName = new Text(compDetails, SWT.BORDER);
		txtExportName.setBounds(new Rectangle(360, 440, 200, 20));
		txtExportName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		loadComplexColumnDetails(cmbColumn.getText());
	}

	private void loadComplexColumnDetails(String columnName) {

		complexColumnComp.dispose();
		complexColumnComp = new Composite(compDetails, SWT.BORDER);
		complexColumnComp.setBounds(new Rectangle(500, 50, 250, 350));

		if (columnName.equalsIgnoreCase(EpisodeColumnsGroup.COLUMN_TYPE)) {
			EM = new EpisodeGroupGUI();
			EM.createView(complexColumnComp);
		} else if (columnName
				.equalsIgnoreCase(PrescriptionColumnsGroup.COLUMN_TYPE)) {
			EM = new PrescriptionGroupGUI();
			EM.createView(complexColumnComp);
		} else if (columnName
				.equalsIgnoreCase(AppointmentColumnsGroup.COLUMN_TYPE)) {
			EM = new AppointmentGroupGUI();
			EM.createView(complexColumnComp);
		}

	}

	protected void cmdCancelWidgetSelected() {

		closeShell(true);

	}

	protected void cmdClearWidgetSelected() {
		clearFields();
		EM.clearForm();
	}

	protected void cmdSaveSelected() {
		if (fieldsOk()) {

			ReportObject obj = new PatientExportObject();

			String fileName = txtExportName.getText().trim();

			obj.setDescription("");
			obj.setAllPatients(true);
			obj.setName(fileName);

			Object[] patientSelections = tblViewerPatientAttributes
			.getCheckedElements();

			// Add patient Fields to report object
			for (int i = 0; i < patientSelections.length; i++) {
				SimpleColumnsEnum selection = (SimpleColumnsEnum) patientSelections[i];
				obj.addSimpleColumn(selection);
			}

			if (EM.fieldsOk()) {
				exportColumn = EM.getColumn();

				if (exportColumn != null) {
					obj.addColumn(exportColumn);
				}
			} else {
				MessageBox m = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				m.setText("Error reading number of episodes");
				m.setMessage("The number of " + cmbColumn.getText().trim()
						+ " selected is not a valid number.");

				m.open();
				return;
			}

			if (!updating) {
				FileManager.saveTemplate(obj);
			} else {
				FileManager.saveTemplate(obj, oldfilename);
			}

			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);

			m.setText("Data Export Saved");
			m.setMessage("The data export '" + fileName + "' "
					+ "has been saved successfully.");

			m.open();

			fireChangeEvent(obj);
			cmdCancelWidgetSelected();
		} else {
			txtExportName.setFocus();
		}
	}

	/**
	 * Method fieldsOk.
	 * 
	 * @return boolean
	 */
	private boolean fieldsOk() {

		String ifilename = txtExportName.getText().trim();

		if ("".equalsIgnoreCase(ifilename)) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("The Export Name is empty.");
			m.setMessage("You must supply a name for the export.");

			m.open();
			return false;
		} else if (!iDARTUtil.isAlphaNumericIncludeSpaces(ifilename)) {
			MessageBox m = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			m.setText("The Export Name Invalid");
			m
			.setMessage("The report name contains non-alphanumeric characters.");
			m.open();
			return false;
		}

		return true;

	}

	/**
	 * Method addChangeListener.
	 * 
	 * @param listener
	 *            iDARTChangeListener
	 */
	public void addChangeListener(iDARTChangeListener listener) {
		this.changeListener = listener;
	}

	/**
	 * Method fireChangeEvent.
	 * 
	 * @param o
	 *            Object
	 */
	private void fireChangeEvent(Object o) {
		if (changeListener != null) {
			changeListener.changed(o);
		}
	}

	@Override
	protected void createCompHeader() {
		String txt = "New Data Export";
		iDartImage icoImage = iDartImage.REDOPACKAGE;
		buildCompHeader(txt, icoImage);
	}

	@Override
	protected void createCompOptions() {
		// not implemented
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	public void load(ReportObject deo, String fileNameOnSystem) {
		updating = true;
		oldfilename = fileNameOnSystem;

		txtExportName.setText(deo.getName());
		ExportColumn selectedGroup = null;

		List<SimpleColumnsEnum> selectedColumns = new ArrayList<SimpleColumnsEnum>();

		List<ExportColumn> exportColumns = deo.getColumns();
		for (ExportColumn selectedExportColumn : exportColumns) {
			if (selectedExportColumn instanceof SimpleColumn) {
				SimpleColumn simpleCol = (SimpleColumn) selectedExportColumn;
				selectedColumns.add(simpleCol.getReturnValue());
			} else if (selectedExportColumn instanceof ExportColumnGroup) {
				selectedGroup = selectedExportColumn;
			}
		}

		tblViewerPatientAttributes
		.setCheckedElements(selectedColumns.toArray());
		selectCompulsory();

		if (selectedGroup != null) {
			loadComplexColumnDetails(selectedGroup.getColumnType());
			cmbColumn.setText(selectedGroup.getColumnType());
			EM.updateView(selectedGroup);
		}
	}

	private void selectCompulsory() {
		for (SimpleColumnsEnum compulsory : getCompulsory()) {
			tblViewerPatientAttributes.setChecked(compulsory, true);
		}
	}

	private List<SimpleColumnsEnum> getCompulsory() {
		return Arrays.asList(SimpleColumnsEnum.patientId);
	}

	private List<SimpleColumnsEnum> getDependents() {

		return Arrays.asList(SimpleColumnsEnum.lastCollectedDate,
				SimpleColumnsEnum.lastCollectedDrugs,
				SimpleColumnsEnum.expectedRunoutDate);
	}

	private void selectDependents(boolean state) {
		for (SimpleColumnsEnum compulsory : getDependents()) {
			tblViewerPatientAttributes.setChecked(compulsory, state);
		}
	}
}
