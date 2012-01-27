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

package org.celllife.idart.gui.doctor;

import java.text.MessageFormat;

import model.manager.AdministrationManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

/**
 */
public class AddDoctor extends GenericFormGui {

	private Button btnSearch;

	private Text txtDoctorSurname;

	private Text txtDoctorFirstname;

	private Text txtTelephone;

	private Text txtCellphoneNo;

	private Text txtEmail;

	private Label lblInstructions;

	private Label lblDoctorSurname;

	private Label lblDoctorFirstname;

	private Label lblTelephone;

	private Label lblCellphoneNo;

	private Label lblEmail;

	private Doctor localDoctor;

	private Group grpDoctorInfo;

	private Composite compStatus;

	private Label lblStatus;

	private Button rdBtnActive;

	private Button rdBtnInactive;

	private boolean isAddnotUpdate;

	private iDARTChangeListener changeListener;

	/**
	 * Use true if you want to add a new doctor, use false if you are updating
	 * an existing doctor
	 * @param parent Shell
	 */
	public AddDoctor(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		if (!isAddnotUpdate) {
			enableFields(false);
			txtDoctorSurname.setEnabled(false);
			txtDoctorFirstname.setEnabled(false);
		}
	}

	/**
	 * This method initializes newDoctor
	 */
	@Override
	protected void createShell() {
		isAddnotUpdate = ((Boolean) getInitialisationOption(OPTION_isAddNotUpdate))
		.booleanValue();
		String shellTxt = isAddnotUpdate ? Messages.getString("adddoctor.screen.title.new") //$NON-NLS-1$
				: Messages.getString("adddoctor.screen.title.update"); //$NON-NLS-1$
		Rectangle bounds = new Rectangle(100, 100, 600, 440);
		buildShell(shellTxt, bounds);
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = (isAddnotUpdate ? Messages.getString("adddoctor.screen.title.new") //$NON-NLS-1$
				: Messages.getString("adddoctor.screen.title.update")); //$NON-NLS-1$
		iDartImage icoImage = iDartImage.DOCTOR;
		// Parent class generic call
		buildCompHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
		// Parent Class generic call
		buildCompButtons();
	}

	/**
	 * This method initializes compDoctorInfo
	 * 
	 */
	@Override
	protected void createContents() {

		// grpDoctorInfo
		grpDoctorInfo = new Group(getShell(), SWT.NONE);
		grpDoctorInfo.setBounds(new Rectangle(60, 90, 480, 240));

		lblInstructions = new Label(grpDoctorInfo, SWT.CENTER);
		lblInstructions.setBounds(new org.eclipse.swt.graphics.Rectangle(110,
				15, 260, 20));
		lblInstructions.setText(Messages.getString("common.label.compulsory")); //$NON-NLS-1$
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));

		// lblDoctor & txtDoctor
		lblDoctorSurname = new Label(grpDoctorInfo, SWT.NONE);
		lblDoctorSurname.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				50, 130, 20));
		lblDoctorSurname.setText(Messages.getString("adddoctor.label.surname")); //$NON-NLS-1$
		lblDoctorSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDoctorSurname = new Text(grpDoctorInfo, SWT.BORDER);
		txtDoctorSurname.setBounds(new org.eclipse.swt.graphics.Rectangle(170,
				50, 170, 20));
		txtDoctorSurname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDoctorSurname.setFocus();

		// btnSearch
		btnSearch = new Button(grpDoctorInfo, SWT.NONE);
		btnSearch.setBounds(new org.eclipse.swt.graphics.Rectangle(350, 48,
				105, 30));
		btnSearch.setText(Messages.getString("adddoctor.button.search.title")); //$NON-NLS-1$
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch.setVisible(!isAddnotUpdate);
		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});

		// lblDoctorFirstname & txtDoctorFirstname
		lblDoctorFirstname = new Label(grpDoctorInfo, SWT.NONE);
		lblDoctorFirstname.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				80, 130, 20));
		lblDoctorFirstname.setText(Messages.getString("adddoctor.label.firstname.title")); //$NON-NLS-1$
		lblDoctorFirstname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDoctorFirstname = new Text(grpDoctorInfo, SWT.BORDER);
		txtDoctorFirstname.setBounds(new org.eclipse.swt.graphics.Rectangle(
				170, 80, 170, 20));
		txtDoctorFirstname.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblTelephone & txtTelephone
		lblTelephone = new Label(grpDoctorInfo, SWT.NONE);
		lblTelephone.setBounds(new org.eclipse.swt.graphics.Rectangle(30, 110,
				130, 20));
		lblTelephone.setText(Messages.getString("adddoctor.label.telephone.title")); //$NON-NLS-1$
		lblTelephone.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtTelephone = new Text(grpDoctorInfo, SWT.BORDER);
		txtTelephone.setBounds(new org.eclipse.swt.graphics.Rectangle(170, 110,
				170, 20));
		txtTelephone.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblCellphoneNo & txtMobile
		lblCellphoneNo = new Label(grpDoctorInfo, SWT.NONE);
		lblCellphoneNo.setBounds(new org.eclipse.swt.graphics.Rectangle(30,
				140, 130, 20));
		lblCellphoneNo.setText(Messages.getString("adddoctor.label.cellphone.title")); //$NON-NLS-1$
		lblCellphoneNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtCellphoneNo = new Text(grpDoctorInfo, SWT.BORDER);
		txtCellphoneNo.setBounds(new org.eclipse.swt.graphics.Rectangle(170,
				140, 170, 20));
		txtCellphoneNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblEmail & txtEmail
		lblEmail = new Label(grpDoctorInfo, SWT.NONE);
		lblEmail.setBounds(new org.eclipse.swt.graphics.Rectangle(30, 170, 130,
				20));
		lblEmail.setText(Messages.getString("adddoctor.label.email.title")); //$NON-NLS-1$
		lblEmail.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtEmail = new Text(grpDoctorInfo, SWT.BORDER);
		txtEmail.setBounds(new org.eclipse.swt.graphics.Rectangle(170, 170,
				170, 20));
		txtEmail.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblStatus = new Label(grpDoctorInfo, SWT.NONE);
		lblStatus.setBounds(new org.eclipse.swt.graphics.Rectangle(30, 200,
				130, 20));
		lblStatus.setText(Messages.getString("adddoctor.label.status.title")); //$NON-NLS-1$
		lblStatus.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// compAccStatus
		compStatus = new Composite(grpDoctorInfo, SWT.NONE);
		compStatus.setBounds(new Rectangle(170, 200, 220, 20));

		rdBtnActive = new Button(compStatus, SWT.RADIO);
		rdBtnActive.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0, 80,
				20));
		rdBtnActive.setText(Messages.getString("adddoctor.buttonactive.title")); //$NON-NLS-1$
		rdBtnActive.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnActive.setSelection(true);

		rdBtnInactive = new Button(compStatus, SWT.RADIO);
		rdBtnInactive.setBounds(new org.eclipse.swt.graphics.Rectangle(90, 0,
				80, 20));
		rdBtnInactive.setText(Messages.getString("adddoctor.buttoninactive.title")); //$NON-NLS-1$
		rdBtnInactive.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnInactive.setSelection(false);

		if (isAddnotUpdate) {
			rdBtnInactive.setEnabled(false);
		}

		getShell().setDefaultButton(btnSave);

	}

	@Override
	protected void cmdSaveWidgetSelected() {

		if (fieldsOk()) {

			Transaction tx = null;
			String action = ""; //$NON-NLS-1$

			try {
				tx = getHSession().beginTransaction();
				// this is a new doctor
				if (localDoctor == null && isAddnotUpdate) {

					localDoctor = new Doctor(txtDoctorSurname.getText(),
							txtDoctorFirstname.getText(), txtTelephone
							.getText(), txtCellphoneNo.getText(),
							txtEmail.getText(), 'T',
							rdBtnActive.getSelection() ? true : false);
					action = Messages.getString("adddoctor.action"); //$NON-NLS-1$
					AdministrationManager
					.saveDoctor(getHSession(), localDoctor);

				}

				// else, we're updating an existing doctor
				else if (localDoctor != null && !isAddnotUpdate) {
					localDoctor.setLastname(txtDoctorSurname.getText());
					localDoctor.setFirstname(txtDoctorFirstname.getText());
					localDoctor.setTelephoneno(txtTelephone.getText());
					localDoctor.setMobileno(txtCellphoneNo.getText());
					localDoctor.setEmailAddress(txtEmail.getText());
					localDoctor.setActive(rdBtnActive.getSelection() ? true
							: false);
					action = Messages.getString("adddoctor.updated"); //$NON-NLS-1$
				}

				getHSession().flush();
				tx.commit();
				String message = MessageFormat.format(Messages.getString("adddoctor.message"), localDoctor.getFullname(),action); //$NON-NLS-1$
				showMessage(MessageDialog.INFORMATION, Messages.getString("adddoctor.messageupdate"), message);//$NON-NLS-1$ 
				fireChangeEvent(localDoctor);
				cmdCancelWidgetSelected();
			} catch (HibernateException he) {
				getLog().error(Messages.getString("adddoctor.errordb"), he); //$NON-NLS-1$
				showMessage(MessageDialog.ERROR, Messages.getString("adddoctor.errordb"), //$NON-NLS-1$ 
						Messages.getString("adddoctor.errordb.saving"));//$NON-NLS-1$
				if (tx != null) {
					tx.rollback();
				}
			}

		}

	}

	/**
	 * clears the current form
	 */
	@Override
	public void clearForm() {

		txtDoctorSurname.setText(EMPTY); 
		txtDoctorFirstname.setText(EMPTY); 
		txtEmail.setText(EMPTY); 
		txtCellphoneNo.setText(EMPTY);
		txtTelephone.setText(EMPTY); 

		txtDoctorSurname.setFocus();
		txtDoctorSurname.setEditable(true);
		txtDoctorFirstname.setEditable(true);

		rdBtnActive.setSelection(true);
		rdBtnInactive.setSelection(false);

		localDoctor = null;

		enableFields(isAddnotUpdate);

	}

	@Override
	protected void cmdCancelWidgetSelected() {
		cmdCloseSelected();
		changeListener = null;
	}

	@Override
	protected void cmdClearWidgetSelected() {

		clearForm();
		btnSearch.setEnabled(true);

	}

	private void cmdSearchWidgetSelected() {

		Search doctorSearch = new Search(getHSession(), getShell(),
				CommonObjects.DOCTOR, true);

		if (doctorSearch.getValueSelected() != null) {

			localDoctor = AdministrationManager.getDoctor(getHSession(),
					doctorSearch.getValueSelected()[0]);

			if (loadDoctorsDetails()) {
				enableFields(true);
				btnSearch.setEnabled(false);
				txtDoctorSurname.setEnabled(false);
				txtDoctorFirstname.setEnabled(false);
			}

			else {
				showMessage(MessageDialog.ERROR, Messages.getString("adddoctor.db.error"), //$NON-NLS-1$
						Messages.getString("adddoctor.db.doctorinfo"));//$NON-NLS-1$
			}
		}
	}

	/**
	 * Check if the necessary field names are filled in. Returns true if there
	 * are fields missing
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {

		boolean fieldsOkay = true;

		if (txtDoctorSurname.getText().equals(EMPTY)) { //$NON-NLS-1$
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage(Messages.getString("adddoctor.surname.empty")); //$NON-NLS-1$
			b.setText(Messages.getString("adddoctor.missingfields")); //$NON-NLS-1$
			b.open();
			txtDoctorSurname.setFocus();
			fieldsOkay = false;
		}

		else if (txtDoctorFirstname.getText().equals(EMPTY)) { //$NON-NLS-1$
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage(Messages.getString("adddoctor.firstname.empty")); //$NON-NLS-1$
			b.setText(Messages.getString("adddoctor.missingfields")); //$NON-NLS-1$
			b.open();
			txtDoctorFirstname.setFocus();
			fieldsOkay = false;

		}
		return fieldsOkay;
	}

	/**
	 * Method loadDoctorsDetails.
	 * @return boolean
	 */
	private boolean loadDoctorsDetails() {

		boolean loadSuccessful = false;

		try {
			txtDoctorSurname.setText(localDoctor.getLastname() == null ? EMPTY : localDoctor.getLastname()); //$NON-NLS-1$
			txtDoctorFirstname.setText(localDoctor.getFirstname() == null ? EMPTY : localDoctor.getFirstname()); //$NON-NLS-1$
			txtEmail.setText(localDoctor.getEmailAddress() == null ? EMPTY : localDoctor.getEmailAddress()); //$NON-NLS-1$
			txtCellphoneNo.setText(localDoctor.getMobileno() == null ? EMPTY : localDoctor.getMobileno()); //$NON-NLS-1$
			txtTelephone.setText(localDoctor.getTelephoneno() == null ? EMPTY : localDoctor.getTelephoneno()); //$NON-NLS-1$
			txtDoctorSurname.setEditable(false);
			txtDoctorFirstname.setEditable(false);
			txtTelephone.setFocus();

			if (localDoctor.isActive()) {
				rdBtnActive.setSelection(true);
				rdBtnInactive.setSelection(false);

			} else {
				rdBtnActive.setSelection(false);
				rdBtnInactive.setSelection(true);
			}
			loadSuccessful = true;
		} catch (Exception e) {
			loadSuccessful = false;
		}

		return loadSuccessful;

	}

	/**
	 * Method enableFields.
	 * @param enable boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
		txtEmail.setEnabled(enable);
		txtCellphoneNo.setEnabled(enable);
		txtTelephone.setEnabled(enable);
		btnSave.setEnabled(enable);
	}

	/**
	 * Method submitForm.
	 * @return boolean
	 */
	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void setLogger() {
		Logger log = Logger.getLogger(this.getClass());
		setLog(log);
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

}
