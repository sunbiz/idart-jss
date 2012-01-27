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

package org.celllife.idart.gui.user;

import java.util.HashSet;
import java.util.Set;

import model.manager.AdministrationManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.User;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
public class ManagePharmUsers extends GenericFormGui {

	private Button rdBtnAddUser;

	private Button rdBtnUpdateUser;
	
	private Button rdBtnStudy;
	
	private Button rdBtnReports;

	private Table tblClinicAccess;

	private Text txtUser;

	private Text txtPassword;

	private Text txtPassConfirm;

	private Group grpUserInfo;

	private boolean isAddNotUpdate;

	private boolean isForClinicApp;
	
	private User localUser;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public ManagePharmUsers(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	/**
	 * This method initializes newUser
	 */
	@Override
	protected void createShell() {
		isAddNotUpdate = ((Boolean) getInitialisationOption(OPTION_isAddNotUpdate))
				.booleanValue();
		isForClinicApp = !LocalObjects.loggedInToMainClinic();
		String shellTxt = isAddNotUpdate ? "Add a New iDART User"
				: "Update Current User";
		Rectangle bounds = new Rectangle(25, 0, 800, 600);
		buildShell(shellTxt, bounds);
	}

	@Override
	protected void createContents() {
		localUser = LocalObjects.getUser(getHSession());
		createCompInstructions();
		createGrpAddOrConfigureUser();
		createGrpUserInfo();
		if (isAddNotUpdate) {
			txtUser.setFocus();
		} else {
			txtPassword.setFocus();
		}
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = (isAddNotUpdate ? "Add a New iDART User"
				: "Update Current User");
		iDartImage icoImage = iDartImage.PHARMACYUSER;
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

	private void createCompInstructions() {
		Composite compInstructions = new Composite(getShell(), SWT.NONE);
		compInstructions.setLayout(null);
		compInstructions.setBounds(new Rectangle(270, 79, 300, 25));

		Label lblInstructions = new Label(compInstructions, SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(0, 0, 300, 25));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));
	}

	/**
	 * This method initializes compButtonTab
	 * 
	 */
	private void createGrpAddOrConfigureUser() {

		Group grpAddOrConfigureUser = new Group(getShell(), SWT.NONE);
		grpAddOrConfigureUser.setBounds(new Rectangle(225, 130, 350, 50));

		rdBtnAddUser = new Button(grpAddOrConfigureUser, SWT.RADIO);
		rdBtnAddUser.setBounds(new Rectangle(20, 12, 150, 30));
		rdBtnAddUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnAddUser.setText("Add New iDART User");
		rdBtnAddUser.setSelection(true);
		rdBtnAddUser
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (rdBtnAddUser.getSelection()) {
							cmdAddWidgetSelected();
						}
					}
				});
		rdBtnUpdateUser = new Button(grpAddOrConfigureUser, SWT.RADIO);
		rdBtnUpdateUser.setBounds(new Rectangle(185, 12, 160, 30));
		rdBtnUpdateUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnUpdateUser.setText("Update Current User");
		rdBtnUpdateUser.setSelection(false);
		rdBtnUpdateUser
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						if (rdBtnUpdateUser.getSelection()) {
							cmdUpdateWidgetSelected();
						}
					}
				});
	}

	/**
	 * This method initializes grpUserInfo
	 * 
	 */
	private void createGrpUserInfo() {

		if (grpUserInfo != null) {
			grpUserInfo.dispose();
		}
		// grpUserInfo
		grpUserInfo = new Group(getShell(), SWT.NONE);
		grpUserInfo.setBounds(new Rectangle(100, 200, 600, 280));

		if (isAddNotUpdate) {

			// lblUser & txtUser
			Label lblUser = new Label(grpUserInfo, SWT.NONE);
			lblUser.setBounds(new Rectangle(30, 20, 125, 20));
			lblUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblUser.setText("* Username:");
			txtUser = new Text(grpUserInfo, SWT.BORDER);
			txtUser.setBounds(new Rectangle(185, 20, 130, 20));
			txtUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			// lblPassword & txtPass
			Label lblPassword = new Label(grpUserInfo, SWT.NONE);
			lblPassword.setBounds(new Rectangle(30, 50, 125, 20));
			lblPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblPassword.setText("* Password:");
			txtPassword = new Text(grpUserInfo, SWT.PASSWORD | SWT.BORDER);
			txtPassword.setBounds(new Rectangle(185, 50, 130, 20));
			txtPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			// lblPasswordConfirm & txtPassConfirm
			Label lblPasswordConfirm = new Label(grpUserInfo, SWT.NONE);
			lblPasswordConfirm.setBounds(new Rectangle(30, 80, 125, 20));
			lblPasswordConfirm.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			lblPasswordConfirm.setText("* Confirm Password:");
			txtPassConfirm = new Text(grpUserInfo, SWT.PASSWORD | SWT.BORDER);
			txtPassConfirm.setBounds(new Rectangle(185, 80, 130, 20));
			txtPassConfirm.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		}

		else {

			// lblUser & txtUser
			Label lblUser = new Label(grpUserInfo, SWT.NONE);
			lblUser.setBounds(new Rectangle(30, 20, 125, 20));
			lblUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblUser.setText("* Username:");
			txtUser = new Text(grpUserInfo, SWT.BORDER);
			txtUser.setBounds(new Rectangle(185, 20, 130, 20));
			txtUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			txtUser.setText(localUser.getUsername());
			txtUser.setEditable(false);
			txtUser.setEnabled(false);

			// lblPassword & txtPass
			Label lblPassword = new Label(grpUserInfo, SWT.NONE);
			lblPassword.setBounds(new Rectangle(30, 50, 125, 20));
			lblPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
			lblPassword.setText("  New Password:");
			txtPassword = new Text(grpUserInfo, SWT.PASSWORD | SWT.BORDER);
			txtPassword.setBounds(new Rectangle(185, 50, 130, 20));
			txtPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			// lblPasswordConfirm & txtPassConfirm
			Label lblPasswordConfirm = new Label(grpUserInfo, SWT.NONE);
			lblPasswordConfirm.setBounds(new Rectangle(30, 80, 145, 20));
			lblPasswordConfirm.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			lblPasswordConfirm.setText("  Confirm New Password:");
			txtPassConfirm = new Text(grpUserInfo, SWT.PASSWORD | SWT.BORDER);
			txtPassConfirm.setBounds(new Rectangle(185, 80, 130, 20));
			txtPassConfirm.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		}

		Label lblClinicAccess = new Label(grpUserInfo, SWT.BORDER);
		lblClinicAccess.setBounds(new Rectangle(370, 20, 200, 20));

		lblClinicAccess.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblClinicAccess.setText("Configure Clinic Access:");

		lblClinicAccess.setAlignment(SWT.CENTER);

		tblClinicAccess = new Table(grpUserInfo, SWT.CHECK | SWT.BORDER
				| SWT.FULL_SELECTION);
		tblClinicAccess.setBounds(370, 40, 200, 220);
		tblClinicAccess.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn tblColClinicName = new TableColumn(tblClinicAccess,
				SWT.NONE);
		tblColClinicName.setText("Clinic Name");
		tblColClinicName.setWidth(195);
		populateClinicAccessList();
		if(iDartProperties.isCidaStudy){//check if the cida property exists
			createUserRolesGroup();
		}

		if (isForClinicApp) {
			tblClinicAccess.setEnabled(false);
			tblClinicAccess.setBackground(ResourceUtils
					.getColor(iDartColor.GRAY));
		}
	}

	private void createUserRolesGroup(){
		
		Label noteLabel = new Label(grpUserInfo, SWT.WRAP | SWT.CENTER | SWT.NONE);
		noteLabel.setBounds(new Rectangle(50, 110, 250, 30));
		noteLabel.setText("Note this is for study purposes only. Leave blank for pharmacy staff");
		noteLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_ITALIC));
		
		Group grpUserRoles = new Group(grpUserInfo, SWT.NONE);
		grpUserRoles.setBounds(new Rectangle(50, 150, 250, 100));

		Label confLabel = new Label(grpUserRoles, SWT.NONE);
		confLabel.setBounds(new Rectangle(40, 10, 150, 15));
		confLabel.setText("Configure User Type:");
		confLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		rdBtnStudy = new Button(grpUserRoles, SWT.RADIO);
		rdBtnStudy.setBounds(new Rectangle(20, 25, 150, 30));
		rdBtnStudy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnStudy.setText("study worker");
		rdBtnStudy.setSelection(false);
		
		rdBtnReports = new Button(grpUserRoles, SWT.RADIO);
		rdBtnReports.setBounds(new Rectangle(20, 50, 150, 30));
		rdBtnReports.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnReports.setText("access to reports");
		rdBtnReports.setSelection(false);
		
	}
	private void populateClinicAccessList() {
		for (Clinic clinic : AdministrationManager.getClinics(getHSession())) {
			TableItem ti = new TableItem(tblClinicAccess, SWT.None);
			ti.setText(0, clinic.getClinicName());
			ti.setData(clinic);

			if ((!isAddNotUpdate) && localUser.getClinics().contains(clinic)) {
				ti.setChecked(true);
			} else if (isAddNotUpdate) {
				if (clinic.getClinicName().equals(
						LocalObjects.currentClinic.getClinicName())) {
					ti.setChecked(true);
				}

			}

		}
	}

	/**
	 * Method fieldsOk.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {

		// check the clinic table
		boolean checkedClinic = false;
		for (TableItem ti : tblClinicAccess.getItems()) {
			if (ti.getChecked()) {
				checkedClinic = true;
			}
		}

		if (!checkedClinic) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("All users need access to at least one clinic. \n\n"
					+ "Please select at least one clinic and try to save again.");
			b.setText("No Clinic Access Granted");

			b.open();
			return false;

		}
		if (isAddNotUpdate && txtUser.getText().trim().equals("")) {

			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("The user name cannot be blank");
			b.setText("Missing Information");
			b.open();
			txtUser.setFocus();
			return false;

		}
		if (isAddNotUpdate && txtPassword.getText().trim().equals("")) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("The password cannot be blank");
			b.setText("Missing Information");
			b.open();
			txtPassword.setFocus();
			return false;

		}
		if ((!isAddNotUpdate)
				&& !((txtPassword.getText().trim().equals("")) && (txtPassConfirm
						.getText().trim().equals("")))) {
			// user has filled in a password field - they are trying to
			// change the password, so it must be checked
			if (LocalObjects.getUser(getHSession()).getPassword()
					.equals(txtPassword.getText().trim())) {
				MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR
						| SWT.OK);
				b.setMessage("The new password is the same as the old password");
				b.setText("Invalid new Password");
				b.open();
				txtPassword.setFocus();

				txtPassword.setText("");
				txtPassConfirm.setText("");

				return false;
			}
		}
		return confirmPasswordMatches();
	}

	/**
	 * Method confirmPasswordMatches.
	 * 
	 * @return boolean
	 */
	private boolean confirmPasswordMatches() {
		if (!txtPassword.getText().equals(txtPassConfirm.getText())) {
			MessageBox m = new MessageBox(getShell(), SWT.OK | SWT.ICON_WARNING);
			m.setText("Inconsistent passwords");
			m.setMessage("Passwords do not match. Please re-enter both passwords");
			m.open();
			txtPassword.setText("");
			txtPassConfirm.setText("");

			txtPassword.setFocus();
			return false;
		}
		return true;
	}

	/**
	 * clears the current form
	 */
	@Override
	protected void clearForm() {

		if (isAddNotUpdate) {
			txtUser.setText("");
		}
		if(iDartProperties.isCidaStudy){//Check if the cida property exists
			rdBtnStudy.setSelection(false);
			rdBtnReports.setSelection(false);
		}

		txtPassword.setText("");
		txtPassConfirm.setText("");

		txtUser.setFocus();
	}

	@Override
	protected void cmdSaveWidgetSelected() {

		if (fieldsOk()) {
			
			//First we check access selected
			int option = SWT.YES;
			if(checkMainClinicAccessOnlySelected()) {
				
				MessageBox m = new MessageBox(getShell(), SWT.YES | SWT.NO
					| SWT.ICON_QUESTION);
				m.setText("Add User");
				m.setMessage("Are you sure you want to add this user without access to any of the down referral clinics?");
				option = m.open();
			}
			if(option == SWT.YES)
			 {
				Transaction tx = null;
	
				try {
					tx = getHSession().beginTransaction();
	
					Set<Clinic> sitesSet = new HashSet<Clinic>();
					for (TableItem ti : tblClinicAccess.getItems()) {
	
						if (ti.getChecked()) {
							sitesSet.add((Clinic) ti.getData());
						}
					}
	
					// before we try anything, lets ask the user for their password
					String confirm = "WARNING: You should only perform this action if you are sure you want to "
						+ (isAddNotUpdate ? "add" : "update")
						+ " this user. The user who performed this action, as well as the current time, will be recorded in the Transaction Log.";
					ConfirmWithPasswordDialogAdapter passwordDialog = new ConfirmWithPasswordDialogAdapter(
							getShell(), "Please enter your Password", confirm,
							getHSession());
					// if password verified
					String messg = passwordDialog.open();
	
					if (messg.equalsIgnoreCase("verified")) {
						if (isAddNotUpdate
								&& AdministrationManager.saveUser(getHSession(),
										txtUser.getText(), txtPassword.getText(), getSelectedRole(),
										sitesSet)) {
							getHSession().flush();
							tx.commit();
	
							MessageBox m = new MessageBox(getShell(), SWT.OK
									| SWT.ICON_INFORMATION);
							m.setText("New User Added");
							m.setMessage("A new user, named '".concat(
									txtUser.getText()).concat(
									"' has been added to the system."));
							m.open();
							cmdCancelWidgetSelected();
	
						} else if (!isAddNotUpdate) {
	
							// if new password has been filled in, change password
							if (!txtPassword.getText().trim().equals("")) {
								AdministrationManager.updateUserPassword(
										getHSession(), localUser, txtPassword
										.getText());
							}
							if (!sitesSet.equals(localUser.getClinics())) {
								AdministrationManager.updateUserClinics(
										getHSession(), localUser, sitesSet);
							}
	
							getHSession().flush();
							tx.commit();
							MessageBox m = new MessageBox(getShell(), SWT.OK
									| SWT.ICON_INFORMATION);
							m.setText("Password Changed");
							m.setMessage("User '".concat(txtUser.getText()).concat(
							"' has been updated successfully."));
							m.open();
							cmdCancelWidgetSelected();
						} else {
							if (tx != null) {
								tx.rollback();
							}
							MessageBox m = new MessageBox(getShell(), SWT.OK
									| SWT.ICON_WARNING);
							m.setText("Duplicate Pharmacy User");
							m.setMessage("The user '".concat(txtUser.getText())
									.concat("' already exists in the database. ")
									.concat("\n\nPlease choose another name."));
							m.open();
						}
					}
	
					else {
						MessageBox m = new MessageBox(getShell(), SWT.OK
								| SWT.ICON_ERROR);
						m.setText("Authentication Failed");
						m
						.setMessage("Database update not allowed because you could not provide your password.");
						m.open();
						clearForm();
					}
	
				}
	
				catch (HibernateException he) {
					if (tx != null) {
						tx.rollback();
					}
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_WARNING);
					m.setText("Problem Saving To Database");
					m
					.setMessage(isAddNotUpdate ? "The user '".concat(
							txtUser.getText()).concat(
							"' could not be saved. ").concat(
							"\n\nPlease try again.")
							: "The password could not be changed. Please try again");
					m.open();
					getLog().error(he);
				}
		}
		}

	}

	@Override
	protected void cmdCancelWidgetSelected() {
		closeShell(true);
	}

	@Override
	protected void cmdClearWidgetSelected() {

		clearForm();
	}

	private void cmdAddWidgetSelected() {
		isAddNotUpdate = true;
		getShell().setText("Add a New iDART User");
		createCompHeader();
		createGrpUserInfo();
		txtUser.setFocus();
	}

	private void cmdUpdateWidgetSelected() {
		isAddNotUpdate = false;
		getShell().setText("Update Current User");
		createCompHeader();
		createGrpUserInfo();
		txtPassword.setFocus();
	}

	/**
	 * Method enableFields.
	 * 
	 * @param enable
	 *            boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
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
	protected void setLogger() {
		Logger log = Logger.getLogger(this.getClass());
		setLog(log);
	}

	/**
	 * This method checks if only the main clinic was selected even if there are
	 * other clinics
	 * 
	 * @return
	 */
	private boolean checkMainClinicAccessOnlySelected() {

		boolean checkedMainClinic = false;
		int noOfClinics = 0;
		for (TableItem ti : tblClinicAccess.getItems()) {
			if (ti.getChecked()) {
				noOfClinics++;
				Clinic c = (Clinic) ti.getData();
				if (c.isMainClinic()) {
					checkedMainClinic = true;
				}
			}
		}

		if (checkedMainClinic && noOfClinics == 1)
			return true;
		else
			return false;
	}
	
	private String getSelectedRole(){
		if(rdBtnStudy != null && rdBtnStudy.getSelection()) {
			return "StudyWorker";
		}else if(rdBtnReports !=null && rdBtnReports.getSelection()){
			return "ReportsWorker";
		}else {
			return "Pharmacist";
		}
		
	}
}
