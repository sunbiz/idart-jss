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
package org.celllife.idart.gui.drug;

import model.manager.DrugManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.user.ConfirmWithPasswordDialogAdapter;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

/**
 */
public class AddChemicalCompound extends GenericFormGui {

	private Text txtName;

	private Text txtAcronym;

	private Label lblInstructions;

	private Label lblName;

	private Label lblAcronym;

	private Label lblNameEg;

	private Label lblAcronymEg;

	private ChemicalCompound localChemicalCompound;

	private Group grpChemicalCompoundInfo;

	public static String compoundAdded = ""; // only set if a component has

	/**
	 * Use true if you want to add a new doctor, use false if you are updating
	 * an existing doctor
	 * @param parent Shell
	 */
	public AddChemicalCompound(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	/**
	 * This method initializes getShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Add a New ChemicalCompound";
		Rectangle bounds = new Rectangle(100, 100, 600, 440);
		buildShell(shellTxt, bounds);
		enableFields(true);
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Add a New Chemical Compound";
		iDartImage icoImage = iDartImage.DRUG;
		buildCompHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes compChemicalCompoundInfo
	 * 
	 */
	private void createCompChemicalCompoundInfo() {

		// grpChemicalCompoundInfo
		grpChemicalCompoundInfo = new Group(getShell(), SWT.NONE);
		grpChemicalCompoundInfo.setBounds(new Rectangle(60, 130, 480, 160));

		lblInstructions = new Label(grpChemicalCompoundInfo, SWT.CENTER);
		lblInstructions.setBounds(new org.eclipse.swt.graphics.Rectangle(110,
				15, 260, 20));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));

		// lblChemicalCompound & txtChemicalCompound
		lblName = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblName
		.setBounds(new org.eclipse.swt.graphics.Rectangle(30, 70, 80,
				20));
		lblName.setText("* Name:");
		lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtName = new Text(grpChemicalCompoundInfo, SWT.BORDER);
		txtName.setBounds(new org.eclipse.swt.graphics.Rectangle(110, 70, 220,
				20));
		txtName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblNameEg = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblNameEg.setBounds(new org.eclipse.swt.graphics.Rectangle(340, 70,
				120, 20));
		lblNameEg.setText("e.g. Lamivudine");
		lblNameEg.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblAcronym & txtAcronym
		lblAcronym = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblAcronym.setBounds(new org.eclipse.swt.graphics.Rectangle(30, 100,
				80, 20));
		lblAcronym.setText("* Acronym:");
		lblAcronym.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtAcronym = new Text(grpChemicalCompoundInfo, SWT.BORDER);
		txtAcronym.setBounds(new org.eclipse.swt.graphics.Rectangle(110, 100,
				220, 20));
		txtAcronym.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblAcronymEg = new Label(grpChemicalCompoundInfo, SWT.NONE);

		lblAcronymEg.setBounds(new org.eclipse.swt.graphics.Rectangle(340, 100,
				120, 20));
		lblAcronymEg.setText("e.g. 3TC");
		lblAcronymEg.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
		buildCompButtons();
	}

	@Override
	protected void cmdSaveWidgetSelected() {

		if (fieldsOk()) {

			// this is a new ChemicalCompound
			if (localChemicalCompound == null) {

				localChemicalCompound = new ChemicalCompound(txtName.getText().trim(),
						txtAcronym.getText().trim());

			}

			// before we try anything, lets ask the user for their password
			String confirm = "WARNING: You should only perform this action if you are sure you want to add a chemical compound to the database PERMANENTLY. The user who performed this action, as well as the current time, will be recorded in the Transaction Log.";
			ConfirmWithPasswordDialogAdapter passwordDialog = new ConfirmWithPasswordDialogAdapter(
					getShell(), "Please enter your Password", confirm, getHSession());
			// if password verified
			String messg = passwordDialog.open();
			if (messg.equalsIgnoreCase("verified")) {

				Transaction tx = null;

				try {
					tx = getHSession().beginTransaction();
					DrugManager.saveChemicalCompound(getHSession(),
							localChemicalCompound);
					tx.commit();
					MessageBox feedBack = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					feedBack.setText("Database Updated");
					feedBack.setMessage("Chemical Compound '".concat(
							localChemicalCompound.getName()).concat(
							"' has been added"));
					feedBack.open();
					compoundAdded = localChemicalCompound.getName();

				} catch (HibernateException he) {
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					m.setText("Problems Saving to the Database");
					m
					.setMessage("There was a problem saving the Chemical Compound's information to the database. Please try again.");
					m.open();
					if (tx != null) {
						tx.rollback();
					}
					getLog().error(he);
				}

				cmdCancelWidgetSelected();

			}
			// Incorrect password entered,
			else if (messg.equalsIgnoreCase("unverified")) {
				getShell().dispose();
			} else if (messg.equalsIgnoreCase("cancel")) {
				clearForm();
			}
		}

	}

	/**
	 * clears the current form
	 */
	@Override
	public void clearForm() {

		txtName.setText("");
		txtAcronym.setText("");

		txtName.setFocus();
		txtName.setEditable(true);
		txtAcronym.setEditable(true);

		localChemicalCompound = null;
		enableFields(true);

	}

	@Override
	protected void cmdCancelWidgetSelected() {

		getHSession().close();
		getShell().dispose();
	}

	@Override
	protected void cmdClearWidgetSelected() {

		clearForm();

	}

	/**
	 * Check if the necessary field names are filled in. Returns true if there
	 * are fields missing
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {

		boolean fieldsOkay = true;

		if (txtName.getText().trim().equals("")) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("Name cannot be blank");
			b.setText("Missing Fields");
			b.open();
			txtName.setFocus();
			return false;
		}

		if (txtAcronym.getText().trim().equals("")) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("Acronym cannot be blank");
			b.setText("Missing Fields");
			b.open();
			txtAcronym.setFocus();
			return false;

		}

		if (DrugManager.getChemicalCompoundByName(getHSession(), txtName
				.getText().trim()) != null) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b
			.setMessage("There is another chemical compound with this name. Please make sure you are not trying to add a chemical compound that is already saved.");
			b.setText("Name Already in Use");
			b.open();
			txtName.setFocus();
			return false;
		}


		if (!iDARTUtil.isAlphaNumeric(txtAcronym.getText().trim())) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b
			.setMessage("Acronym cannot contain non-alphanumeric characters or spaces");
			b.setText("Invalid Characters in Acronym");
			b.open();
			txtAcronym.setFocus();
			return false;
		}

		if (!iDARTUtil.isAlphaNumeric(txtName.getText().trim())) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b
			.setMessage("Name cannot contain non-alphanumeric characters or spaces");
			b.setText("Invalid Characters in Name");
			b.open();
			txtName.setFocus();
			return false;
		}

		if (DrugManager.getChemicalCompoundByAcronym(getHSession(),
				txtAcronym.getText().trim()) != null) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b
			.setMessage("There is another chemical compound with this acronym. Please make sure you are not trying to add a chemical compound that is already saved.");
			b.setText("Acronym Already in Use");
			b.open();
			txtAcronym.setFocus();
			return false;
		}

		if ((txtName.getText().trim().indexOf("/") != -1)
				|| (txtAcronym.getText().trim().indexOf("/") != -1)) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setText("Potential Combo Drug Detected!");
			b
			.setMessage("There is a '/' character in the chemical compound name. This may mean that you are trying to add a combo drug like LPv/r (Kaletra). You should be adding the individual compounds (LPV and r) separately - remove the '/' to conitnue.");
			b.open();
			return false;
		}
		return fieldsOkay;
	}

	/**
	 * Method enableFields.
	 * @param enable boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
		btnSave.setEnabled(enable);
	}

	@Override
	protected void createContents() {
		createCompChemicalCompoundInfo();
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
		setLog(Logger.getLogger(this.getClass()));
	}

}
