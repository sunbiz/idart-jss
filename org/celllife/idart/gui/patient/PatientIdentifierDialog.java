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

package org.celllife.idart.gui.patient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AlternatePatientIdentifier;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

public class PatientIdentifierDialog extends GenericOthersGui {

	class PatientIdentifierEditingSupport extends EditingSupport {

		private CellEditor editor;

		public PatientIdentifierEditingSupport(ColumnViewer viewer) {
			super(viewer);
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object arg0) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			PatientIdentifier model = (PatientIdentifier) element;
			return model.getValueEdit();
		}

		@Override
		protected void setValue(Object element, Object value) {
			PatientIdentifier model = (PatientIdentifier) element;
			model.setValueEdit(((String) value).trim().toUpperCase());
			getViewer().update(element, null);
		}
	}
	
	private Patient localPatient;

	private TableViewer tblViewer;

	private ArrayList<PatientIdentifier> identiers;

	private boolean changesMade;

	public PatientIdentifierDialog(Shell parent, Session session, Patient patient) {
		super(parent, session);
		localPatient = patient;
	}
	
	private void loadPatientIdentifiers() {
		List<IdentifierType> types = PatientManager.getAllIdentifierTypes(getHSession());
		identiers = new ArrayList<PatientIdentifier>();
		typeLoop: for (IdentifierType type : types) {
			if (type.isVoided())
				continue;
			
			for (PatientIdentifier identifier : localPatient.getPatientIdentifiers()) {
				if (identifier.getType().getId() == type.getId()){
					identifier.setValueEdit(identifier.getValue());
					identiers.add(identifier);
					continue typeLoop;
				}
			}
			
			// create new identifier if the patient doesn't already have on for this type
			PatientIdentifier identifier = new PatientIdentifier();
			identifier.setType(type);
			identifier.setValueEdit(EMPTY);
			identifier.setPatient(localPatient);
			identiers.add(identifier);
		}
		
		tblViewer.setInput(identiers);
	}

	/**
	 * This method initializes newPrintBlankLabel
	 */
	@Override
	protected void createShell() {
		buildShell(Messages.getString("PatientIdDialog.title"), 
				new Rectangle(0, 0, 600, 570)); //$NON-NLS-1$
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		buildCompHeader(Messages.getString("PatientIdDialog.title"), 
				iDartImage.PATIENTINFOLABEL); //$NON-NLS-1$
	}

	/**
	 * This method initializes compOptions
	 * 
	 */
	@Override
	protected void createCompOptions() {
		Label lblInfoText = new Label(getShell(), SWT.WRAP);
		lblInfoText.setBounds(new Rectangle(50, 80, 500, 40));
		lblInfoText.setText(Messages.getString("PatientIdDialog.info_text")); //$NON-NLS-1$
		
		tblViewer = new TableViewer(getShell(), SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		tblViewer.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(50, 130, 500, 330));
		tblViewer.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblViewer.setContentProvider(new ArrayContentProvider());
		tblViewer.getTable().setHeaderVisible(true);
		tblViewer.getTable().setLinesVisible(true);
		createColumns(tblViewer);

	}

	/**
	 * Creates the table layout
	 * 
	 * @param viewer
	 */
	public void createColumns(final TableViewer viewer) {
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText(Messages.getString("PatientIdDialog.column_header.type")); //$NON-NLS-1$
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				PatientIdentifier content = (PatientIdentifier) element;
				return content.getType().getName();
			}
		});

		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setWidth(200);
		column1.getColumn().setText(Messages.getString("PatientIdDialog.column_header.number")); //$NON-NLS-1$
		column1.getColumn().setMoveable(true);
		column1.setEditingSupport(new PatientIdentifierEditingSupport(viewer));
		column1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				PatientIdentifier content = (PatientIdentifier) element;
				return content.getValueEdit();
			}
		});
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
		Button btnClose = new Button(getCompButtons(), SWT.PUSH);
		btnClose.setText(Messages.getString("PatientIdDialog.button.done")); //$NON-NLS-1$
		btnClose.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSaveSelected();
			}
		});
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		Button btnCancel = new Button(getCompButtons(), SWT.PUSH);
		btnCancel.setText(Messages.getString("genericformgui.button.cancel.text")); //$NON-NLS-1$
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseSelected();
			}
		});
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	protected void cmdSaveSelected() {
		if (fieldsOk()){
			Set<PatientIdentifier> patientIds = localPatient.getPatientIdentifiers();
			for (PatientIdentifier newId : identiers) {
				if (newId.getValueEdit() == null || newId.getValueEdit().isEmpty()) { 
					if (newId.getId() != -1) {
						patientIds.remove(newId);
						changesMade = true;
					}
					continue;
				}

				if (newId.getId() == -1){
					changesMade = true;
					patientIds.add(newId);
					newId.setValue(newId.getValueEdit());
					newId.setValueEdit(null);
					continue;
				}
				
				// check if an existing id was changed
				if (!newId.getValue().equals(newId.getValueEdit())){
					changesMade = true;
					AlternatePatientIdentifier alt = new AlternatePatientIdentifier(
							newId.getValue(), localPatient, new Date(), true, newId.getType());
					localPatient.getAlternateIdentifiers().add(alt);
					newId.setValue(newId.getValueEdit());
					newId.setValueEdit(null);
				} 
			}
			
			localPatient.setPatientId(localPatient.getPreferredIdentifier().getValue());
			cmdCloseSelected();
		}
	}

	private boolean fieldsOk() {
		boolean allEmpty = true;
		for (PatientIdentifier newId : identiers) {
			if (newId.getValueEdit() == null || newId.getValueEdit().isEmpty())
				continue;
			
			allEmpty = false;
			
			String illegalText = iDARTUtil.checkPatientId(newId.getValueEdit());
			if(illegalText != null){
				showMessage(MessageDialog.ERROR, MessageFormat.format(Messages.getString("patient.error.badCharacterInPatientId.title"), //$NON-NLS-1$
						illegalText),
						MessageFormat.format(Messages.getString("patient.error.badCharacterInPatientId"), //$NON-NLS-1$
						iDartProperties.illegalPatientIdChars));
				return false;
			}
			
			if (PatientManager.checkPatientIdentifier(getHSession(), newId.getPatient(), newId.getType(), newId.getValueEdit())){
				showMessage(MessageDialog.ERROR, Messages.getString("PatientIdDialog.error.exists.title"),  //$NON-NLS-1$
						MessageFormat.format(Messages.getString("PatientIdDialog.error.exists.message"), //$NON-NLS-1$
						newId.getType().getName(), newId.getValueEdit()));
				return false;
			}
			
			List<Patient> altPatients = PatientManager.getPatientsByAltId(
					getHSession(), newId.getType(), newId.getValueEdit());
			if (!altPatients.isEmpty()){
				String patientsWithThisOldId = EMPTY;
				for (Patient p : altPatients) {
					patientsWithThisOldId += (p.getPatientId() + ", "); //$NON-NLS-1$
				}
				patientsWithThisOldId = patientsWithThisOldId.substring(0,
						patientsWithThisOldId.length() - 2);
				
				MessageBox mSave = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				mSave.setText(Messages.getString("patient.warning.saveDuplicateId.title")); //$NON-NLS-1$
				mSave.setMessage(MessageFormat.format(Messages.getString("patient.warning.saveDuplicateId"), //$NON-NLS-1$
						patientsWithThisOldId));
				if (mSave.open() != SWT.YES){
					return false;
				}
			}
		}
		
		if (allEmpty){
			showMessage(MessageDialog.ERROR, Messages.getString("PatientIdDialog.error.empty.title"),  //$NON-NLS-1$
					Messages.getString("PatientIdDialog.error.empyt.message")); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}

	/**
	 * This method is called when the user pressed the "Close" button It
	 * disposes the current shell.
	 */
	private void cmdCloseSelected() {
		closeShell(false);
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}
	
	public void openAndWait() {
		activate();
		loadPatientIdentifiers();
		while (!getShell().isDisposed()) {
			if (!getShell().getDisplay().readAndDispatch()) {
				getShell().getDisplay().sleep();
			}
		}
	}
	
	public boolean isChangesMade(){
		return changesMade;
	}
}
