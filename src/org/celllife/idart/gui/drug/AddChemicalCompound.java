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

import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.DrugManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.AtcCode;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.user.ConfirmWithPasswordDialogAdapter;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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

	private List lstAtc;

	public static ChemicalCompound compoundAdded = null; // only set if a component has

	/**
	 * Use true if you want to add a new doctor, use false if you are updating
	 * an existing doctor
	 * @param parent Shell
	 * @param cc 
	 */
	public AddChemicalCompound(Shell parent, Session session, ChemicalCompound cc) {
		super(parent, session);
		localChemicalCompound = cc;
		populateForm();
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
		grpChemicalCompoundInfo.setBounds(new Rectangle(60, 70, 480, 220));
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 10;
		grpChemicalCompoundInfo.setLayout(gridLayout);

		lblInstructions = new Label(grpChemicalCompoundInfo, SWT.CENTER);
		lblInstructions.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 3,1));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));

		// lblChemicalCompound & txtChemicalCompound
		lblName = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,1));
		lblName.setText("* Name:");
		lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtName = new Text(grpChemicalCompoundInfo, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1,1));
		txtName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblNameEg = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblNameEg.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,1));
		lblNameEg.setText("e.g. Lamivudine");
		lblNameEg.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblAcronym & txtAcronym
		lblAcronym = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblAcronym.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,1));
		lblAcronym.setText("* Acronym:");
		lblAcronym.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtAcronym = new Text(grpChemicalCompoundInfo, SWT.BORDER);
		txtAcronym.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1,1));
		txtAcronym.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblAcronymEg = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblAcronymEg.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,1));
		lblAcronymEg.setText("e.g. 3TC");
		lblAcronymEg.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		// atc code
		Label lblAtc = new Label(grpChemicalCompoundInfo, SWT.NONE);
		lblAtc.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,2));
		lblAtc.setText("  ATC Codes:");
		lblAtc.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		lstAtc = new List(grpChemicalCompoundInfo, SWT.BORDER);
		lstAtc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1,2));
		lstAtc.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		
		Button btnAtcAdd = new Button(grpChemicalCompoundInfo, SWT.NONE);
		btnAtcAdd.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,1));
		btnAtcAdd.setText("Add");
		btnAtcAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnAtcAdd.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAtcAddWidgetSelected();
			}
		});
		btnAtcAdd.setToolTipText("Press this button to search for an ATC drug code.");
		
		Button btnAtcRemove = new Button(grpChemicalCompoundInfo, SWT.NONE);
		btnAtcRemove.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,1));
		btnAtcRemove.setText("Remove");
		btnAtcRemove.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnAtcRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAtcRemoveWidgetSelected();
			}
		});
		btnAtcRemove.setToolTipText("Press this button to search for an ATC drug code.");
		
		grpChemicalCompoundInfo.layout();
	}

	protected void cmdAtcRemoveWidgetSelected() {
		int[] selectionIndices = lstAtc.getSelectionIndices();
		lstAtc.remove(selectionIndices);
	}

	protected void cmdAtcAddWidgetSelected() {
		Search atcSearch = new Search(getHSession(), getShell(),
				CommonObjects.ATC);

		if (atcSearch.getValueSelected() != null) {

			AtcCode atc = AdministrationManager.getAtccodeFromName(getHSession(), atcSearch
					.getValueSelected()[0]);
			
			if (atc == null){
				return;
			}
			Object data = lstAtc.getData(atc.getName());
			if (data == null){
				lstAtc.add(atc.getName());
				lstAtc.setData(atc.getName(), atc);
			}
		}
		
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
				localChemicalCompound = new ChemicalCompound();
			}
			
			localChemicalCompound.setName(txtName.getText().trim());
			localChemicalCompound.setAcronym(txtAcronym.getText().trim());
			
			localChemicalCompound.getAtccodes().clear();
			String[] selection = lstAtc.getItems();
			for (String atc : selection) {
				AtcCode code = (AtcCode) lstAtc.getData(atc);
				if (code != null){
					localChemicalCompound.getAtccodes().add(code);
				}
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
					compoundAdded = localChemicalCompound;

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

		closeShell(false);
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

		ChemicalCompound byName = DrugManager.getChemicalCompoundByName(getHSession(), txtName
				.getText().trim());
		if (byName != null && (localChemicalCompound != null && byName.getId() != localChemicalCompound.getId())) {
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

		ChemicalCompound byAcronym = DrugManager.getChemicalCompoundByAcronym(getHSession(),
				txtAcronym.getText().trim());
		if (byAcronym != null && (localChemicalCompound != null && byAcronym.getId() != localChemicalCompound.getId())) {
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

	private void populateForm() {
		if (localChemicalCompound != null){
			txtName.setText(localChemicalCompound.getName());
			txtAcronym.setText(localChemicalCompound.getAcronym());
			Set<AtcCode> atccodes = localChemicalCompound.getAtccodes();
			for (AtcCode atc : atccodes) {
				lstAtc.add(atc.getName());
				lstAtc.setData(atc.getName(), atc);
			}
		}
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
