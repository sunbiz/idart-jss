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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.manager.AdministrationManager;
import model.manager.DrugManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.jfree.util.Log;

/**
 */
public class AddDrug extends GenericFormGui {

	private Composite compSideTreatment;

	private Group grpDrugInfo;

	private Label lblInstructions;

	private Label lblDrugSearch;

	private Label lblName;

	private Label lblFormLanguage1;

	private Label lblPacksize;

	private Label lblPackDescription;

	private Label lblDispensingInstructions1;

	private Label lblDispensingInstructions2;

	private Label lblSideTreatment;

	private Button btnSearch;

	private Button rdBtnSideTreatment;

	private Button rdBtnARV;

	private Text txtName;

	private Text txtPacksize;

	private Text txtDispensingInstructions1;

	private Text txtDispensingInstructions2;

	boolean isAddnotUpdate;

	private Drug localDrug; // @jve:decl-index=0:

	private Group grpStandadDosages;

	private Label lblTake;

	private Text txtAmountPerTime;

	private Label lblTablets;

	private Text txtTimesPerDay;

	private Label lblTimesPerDay;

	private Text txtNSN;

	private Label lblNSN;

	private Text txtStockCode;

	private Label lblStockCode;

	private Combo cmbForm;

	private Composite compInstructions;

	private Group grpChemicalCompounds;

	private Table tblChemicalCompounds;

	private Button btnAddChemical;

	private Label lblAddChemical;

	private TableEditor editor;

	/**
	 * Use true if you want to add a new drug, use false if you are updating an
	 * existing drug
	 * 
	 * @param parent
	 *            Shell
	 */
	public AddDrug(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	/**
	 * This method initializes newDrug
	 */
	@Override
	protected void createShell() {
		isAddnotUpdate = (Boolean) getInitialisationOption(OPTION_isAddNotUpdate);
		// The GenericFormsGui class needs
		// Header text, icon URL, shell bounds
		String shellTxt = isAddnotUpdate ? "Add a New Drug"
				: "Update an Existing Drug";
		Rectangle bounds = new Rectangle(25, 0, 800, 600);
		// Parent Generic Methods ------
		buildShell(shellTxt, bounds); // generic shell build
	}

	@Override
	protected void createContents() {
		createCompDrugInfo();
		createGrpStandardDosages();
		createCompInstructions();
		createGrpChemicalCompounds();

		if (isAddnotUpdate) {
			enableFields(true);
			txtName.setFocus();
		} else {
			enableFields(false);
			btnSearch.setFocus();
		}
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = (isAddnotUpdate ? "Add a New Drug"
				: "Update an Existing Drug");
		iDartImage icoImage = iDartImage.PRESCRIPTIONADDDRUG;
		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createCompButtons() {
		// Parent Class generic call
		buildCompButtons();
	}

	/**
	 * This method initializes compDrugInfo
	 * 
	 */
	private void createCompDrugInfo() {

		// compDrugInfo
		grpDrugInfo = new Group(getShell(), SWT.NONE);
		grpDrugInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDrugInfo.setText("Drug Details");
		grpDrugInfo.setBounds(new Rectangle(18, 110, 483, 293));

		lblDrugSearch = new Label(grpDrugInfo, SWT.NONE);
		lblDrugSearch.setBounds(new Rectangle(16, 29, 210, 20));

		if (isAddnotUpdate) {
			lblDrugSearch.setText("");
		} else {
			lblDrugSearch.setText("* Please search for Drug to Update:");
		}
		lblDrugSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// btnSearch
		btnSearch = new Button(grpDrugInfo, SWT.NONE);
		btnSearch.setBounds(new Rectangle(230, 24, 90, 30));
		btnSearch.setText("Drug Search");
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
		btnSearch
		.setToolTipText("Press this button to search for an existing drug.");

		// lblName & txtName
		lblName = new Label(grpDrugInfo, SWT.NONE);
		lblName.setBounds(new Rectangle(16, 59, 180, 20));
		lblName.setText("* Drug Name:");
		lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtName = new Text(grpDrugInfo, SWT.BORDER);
		txtName.setBounds(new Rectangle(230, 59, 240, 20));
		txtName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblFormLanguage1 & txtFormLanguage1
		lblFormLanguage1 = new Label(grpDrugInfo, SWT.NONE);
		lblFormLanguage1.setBounds(new Rectangle(16, 86, 180, 20));
		lblFormLanguage1.setText("* Form:");
		lblFormLanguage1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbForm = new Combo(grpDrugInfo, SWT.BORDER);
		cmbForm.setBounds(new Rectangle(230, 86, 150, 20));
		CommonObjects.populateForms(getHSession(), cmbForm);
		cmbForm.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbForm.setText("");
		cmbForm.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbForm.setVisibleItemCount(cmbForm.getItemCount());

		cmbForm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				String theFormStr = cmbForm.getText();
				if (!"".equalsIgnoreCase(theFormStr)) {
					populateGrpStandardDosages(theFormStr);
					Form theForm = AdministrationManager.getForm(getHSession(),
							theFormStr);
					lblPackDescription.setText(theForm.getFormLanguage1()
							.equals("drops") ? "ml" : theForm
									.getFormLanguage1());

					txtDispensingInstructions1.setText("");
					txtDispensingInstructions2.setText("");
				}

			}
		});
		cmbForm.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String theText = cmbForm.getText();
				if (theText.length() > 2) {
					String s = theText.substring(0, 1);
					String t = theText.substring(1, theText.length());
					theText = s.toUpperCase() + t;
					String[] items = cmbForm.getItems();
					for (int i = 0; i < items.length; i++) {
						if (items[i].length() > 2
								&& items[i].substring(0, 3).equalsIgnoreCase(
										theText)) {
							cmbForm.setText(items[i]);
							cmbForm.setFocus();
						}
					}
				}
			}
		});

		// lblPacksize & txtPacksize
		lblPacksize = new Label(grpDrugInfo, SWT.NONE);
		lblPacksize.setBounds(new Rectangle(16, 115, 180, 20));
		lblPacksize.setText("* Pack Size:");
		lblPacksize.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPacksize = new Text(grpDrugInfo, SWT.BORDER);
		txtPacksize.setBounds(new Rectangle(230, 115, 50, 20));
		txtPacksize.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblPackDescription = new Label(grpDrugInfo, SWT.NONE);
		lblPackDescription.setBounds(new Rectangle(290, 115, 150, 20));
		lblPackDescription.setText("");
		lblPackDescription.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblDispensingInstructions1 & txtDispenseInstr
		lblDispensingInstructions1 = new Label(grpDrugInfo, SWT.NONE);
		lblDispensingInstructions1.setBounds(new Rectangle(16, 145, 200, 20));
		lblDispensingInstructions1
		.setText("  Dispensing Instructions (line 1):");
		lblDispensingInstructions1.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblDispensingInstructions1
		.setToolTipText("This appears on the drug label");

		txtDispensingInstructions1 = new Text(grpDrugInfo, SWT.BORDER);
		txtDispensingInstructions1.setBounds(new Rectangle(230, 145, 240, 20));
		txtDispensingInstructions1.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		// lblDispensingInstructions2 & txtDispensingInstructions2
		lblDispensingInstructions2 = new Label(grpDrugInfo, SWT.NONE);
		lblDispensingInstructions2.setBounds(new Rectangle(16, 175, 200, 20));
		lblDispensingInstructions2
		.setText("  Dispensing Instructions (line 2):");
		lblDispensingInstructions2.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblDispensingInstructions2
		.setToolTipText("This appears on the drug label");

		txtDispensingInstructions2 = new Text(grpDrugInfo, SWT.BORDER);
		txtDispensingInstructions2.setBounds(new Rectangle(230, 175, 240, 20));
		txtDispensingInstructions2.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		// Account Status
		lblSideTreatment = new Label(grpDrugInfo, SWT.NONE);
		lblSideTreatment.setBounds(new Rectangle(16, 205, 117, 20));
		lblSideTreatment.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblSideTreatment.setText("* Drug is:");

		compSideTreatment = new Composite(grpDrugInfo, SWT.NONE);
		compSideTreatment.setBounds(new Rectangle(230, 205, 220, 20));

		rdBtnSideTreatment = new Button(compSideTreatment, SWT.RADIO);
		rdBtnSideTreatment.setBounds(new org.eclipse.swt.graphics.Rectangle(0,
				0, 110, 20));
		rdBtnSideTreatment.setText("Side Treatment");
		rdBtnSideTreatment.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnSideTreatment.setSelection(false);

		rdBtnARV = new Button(compSideTreatment, SWT.RADIO);
		rdBtnARV.setBounds(new org.eclipse.swt.graphics.Rectangle(140, 0, 80,
				20));
		rdBtnARV.setText("ARV Drug");
		rdBtnARV.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnARV.setSelection(true);

		// lblNSN & txtNSN
		lblNSN = new Label(grpDrugInfo, SWT.NONE);
		lblNSN.setBounds(new Rectangle(16, 237, 180, 20));
		lblNSN.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblNSN.setText("  Drug Code 1:");
		lblNSN
		.setToolTipText("This is recorded on the report Receipts and Issues: ARV Drugs");

		txtNSN = new Text(grpDrugInfo, SWT.BORDER);
		txtNSN.setBounds(new Rectangle(228, 237, 240, 20));
		txtNSN.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblStockCode & txtStockCode
		lblStockCode = new Label(grpDrugInfo, SWT.NONE);
		lblStockCode.setBounds(new Rectangle(16, 266, 180, 20));
		lblStockCode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblStockCode.setText("  Drug Code 2:");
		lblStockCode
		.setToolTipText("This is recorded on the report Receipts and Issues: ARV Drugs");

		txtStockCode = new Text(grpDrugInfo, SWT.BORDER);
		txtStockCode.setBounds(new Rectangle(228, 266, 240, 20));
		txtStockCode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes grpStandadDosages
	 * 
	 */
	private void createGrpStandardDosages() {

		// grpStandadDosages
		grpStandadDosages = new Group(getShell(), SWT.NONE);
		grpStandadDosages.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpStandadDosages.setText("Standard Dosage ");
		grpStandadDosages.setLayout(null);
		grpStandadDosages.setBounds(new Rectangle(16, 423, 485, 61));

		// lblTake
		lblTake = new Label(grpStandadDosages, SWT.CENTER);
		lblTake.setBounds(new Rectangle(27, 28, 107, 22));
		lblTake.setText("Take");
		lblTake.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// txtAmountPerTime
		txtAmountPerTime = new Text(grpStandadDosages, SWT.BORDER);
		txtAmountPerTime.setBounds(new Rectangle(137, 26, 40, 22));
		txtAmountPerTime.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblTablets
		lblTablets = new Label(grpStandadDosages, SWT.CENTER);
		lblTablets.setBounds(new Rectangle(180, 28, 76, 22));
		lblTablets.setText("tablets");
		lblTablets.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// txtTimesPerDay
		txtTimesPerDay = new Text(grpStandadDosages, SWT.BORDER);
		txtTimesPerDay.setBounds(new Rectangle(260, 26, 40, 22));
		txtTimesPerDay.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblTimesPerDay
		lblTimesPerDay = new Label(grpStandadDosages, SWT.CENTER);
		lblTimesPerDay.setBounds(new Rectangle(298, 28, 126, 22));
		lblTimesPerDay.setText("times per day");
		lblTimesPerDay.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	/**
	 * This method initializes compButtons
	 * 
	 */

	@Override
	protected void cmdSaveWidgetSelected() {

		if (fieldsOk()) {
			MessageBox mSave = new MessageBox(getShell(), SWT.ICON_QUESTION
					| SWT.YES | SWT.NO);
			mSave.setText(isAddnotUpdate ? "Add New Drug" : "Update Details");
			mSave
			.setMessage(isAddnotUpdate ? "Are you sure you want to add this drug to the database?"
					: "Do you want to save the changes made to this drug?");

			switch (mSave.open()) {

			case SWT.YES:

				Transaction tx = null;
				String action = "";
				try {
					tx = getHSession().beginTransaction();
					if (isAddnotUpdate) {
						localDrug = new Drug();
						setLocalDrug();
						DrugManager.saveDrug(getHSession(), localDrug);
						action = "added";
					} else {
						setLocalDrug();
						action = "updated";
					}

					tx.commit();
					getHSession().flush();

					// Updating the drug list after being saved.
					MessageBox m = new MessageBox(getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					m.setMessage("Drug '".concat(localDrug.getName()).concat(
							"' has been " + action + "."));
					m.setText("Database Updated");
					m.open();

				} catch (HibernateException he) {
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					m.setText("Problems Saving to the Database");
					m
					.setMessage("There was a problem saving the drug's information to the database. Please try again.");
					m.open();
					if (tx != null) {
						tx.rollback();
					}
					getLog().error(he);
				}
				cmdCancelWidgetSelected(); // go back to previous screen
				break;
			case SWT.NO:
				// do nothing
			}

		}
	}

	/**
	 * Clears the form
	 */
	@Override
	public void clearForm() {

		try {

			txtName.setText("");
			cmbForm.setText("");
			txtPacksize.setText("");
			lblPackDescription.setText("");
			txtDispensingInstructions1.setText("");
			txtDispensingInstructions2.setText("");
			btnSearch.setEnabled(true);
			txtNSN.setText("");
			txtStockCode.setText("");
			txtTimesPerDay.setText("");
			txtAmountPerTime.setText("");

			localDrug = null;
			enableFields(isAddnotUpdate);

			for (int i = 0; i < tblChemicalCompounds.getItemCount(); i++) {
				tblChemicalCompounds.getItem(i).setText(1, "");
				tblChemicalCompounds.getItem(i).setChecked(false);
			}

			Control old = editor.getEditor();
			if (old != null) {
				old.dispose();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void cmdCancelWidgetSelected() {
		cmdCloseSelected();
	}

	private void cmdAddChemicalWidgetSelected() {

		final AddChemicalCompound ac = new AddChemicalCompound(getShell());
		ac.getShell().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {

				if (!(AddChemicalCompound.compoundAdded.trim().equals(""))) {
					ChemicalCompound cc = DrugManager
					.getChemicalCompoundByName(getHSession(),
							AddChemicalCompound.compoundAdded.trim());

					if (cc != null) {
						TableItem ti = new TableItem(tblChemicalCompounds,
								SWT.NONE);

						// populate table
						ti.setText(0, "[" + cc.getAcronym() + "] "
								+ cc.getName());
						ti.setData(cc);
					}
				}
			}
		});
	}

	private void cmdSearchWidgetSelected() {

		Search drugSearch = new Search(getHSession(), getShell(),
				CommonObjects.DRUG);

		if (drugSearch.getValueSelected() != null) {

			localDrug = DrugManager.getDrug(getHSession(), drugSearch
					.getValueSelected()[0]);
			loadDrugDetails();
			btnSearch.setEnabled(false);
			// txtBarcode.setEditable(false);
			enableFields(true);
			txtName.setFocus();

		} else {
			btnSearch.setEnabled(true);
		}

	}

	@Override
	protected void cmdClearWidgetSelected() {
		clearForm();
	}

	private void loadDrugDetails() {

		txtName.setText(localDrug.getName());
		cmbForm.setText(localDrug.getForm().getForm());
		txtPacksize.setText(String.valueOf(localDrug.getPackSize()));

		Form theForm = localDrug.getForm();
		lblPackDescription
		.setText(theForm.getFormLanguage1().equals("drops") ? "ml"
				: theForm.getFormLanguage1());
		txtDispensingInstructions1.setText(localDrug
				.getDispensingInstructions1());
		txtDispensingInstructions2.setText(localDrug
				.getDispensingInstructions2());

		populateGrpStandardDosages(cmbForm.getText());

		Object amntpertime = iDARTUtil.isInteger(""
				+ localDrug.getDefaultAmnt());
		String tmp = (amntpertime == null) ? "" + localDrug.getDefaultAmnt()
				: "" + amntpertime.toString();
		txtAmountPerTime.setText(tmp);


		txtTimesPerDay.setText(String.valueOf(localDrug.getDefaultTimes()));

		btnSearch.setEnabled(false);

		if (localDrug.getSideTreatment() == 'T') {
			rdBtnSideTreatment.setSelection(true);
			rdBtnARV.setSelection(false);
		}

		else {
			rdBtnARV.setSelection(true);
			rdBtnSideTreatment.setSelection(false);
		}

		if (localDrug.getStockCode() != null) {
			txtStockCode.setText(localDrug.getStockCode());
		}

		if (localDrug.getNsnCode() != null) {
			txtNSN.setText(localDrug.getNsnCode());
		}

		Iterator<ChemicalDrugStrength> chemicalDrugStrengthIt = localDrug
		.getChemicalDrugStrengths().iterator();

		while (chemicalDrugStrengthIt.hasNext()) {
			ChemicalDrugStrength cds = chemicalDrugStrengthIt.next();
			for (int i = 0; i < tblChemicalCompounds.getItemCount(); i++) {
				if (((ChemicalCompound) tblChemicalCompounds.getItem(i)
						.getData()).getId() == cds.getChemicalCompound()
						.getId()) {
					tblChemicalCompounds.getItem(i).setChecked(true);

					double d = cds.getStrength();
					String strength = "";

					Object o = iDARTUtil.isInteger("" + d);
					if (o == null) {
						strength += d;
					} else {
						strength += Integer.parseInt(o.toString());
					}


					tblChemicalCompounds.getItem(i).setText(1,
							strength.toString());
				}

			}

		}

	}

	/**
	 * Check if the necessary field names are filled in. Returns true if there
	 * are fields missing
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {

		// checking all simple fields

		if (txtName.getText().equals("")) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("Drug Name cannot be blank");
			b.setText("Missing Information");
			b.open();
			txtName.setFocus();
			return false;
		}

		if (DrugManager.drugNameExists(getHSession(), txtName.getText())
				&& isAddnotUpdate) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b
			.setMessage("Drug Name already exists. Please enter a different drug name");
			b.setText("Duplicate Drug Name");
			b.open();
			txtName.setFocus();
			return false;
		}

		if (cmbForm.indexOf(cmbForm.getText()) == -1) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b
			.setMessage("The form of the drug must be from the list provided.");
			b.setText("Incorrect Information");
			b.open();
			cmbForm.setFocus();
			return false;
		}

		if (txtPacksize.getText().equals("")) {
			MessageBox b = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			b.setMessage("Pack Size cannot be blank");
			b.setText("Missing Information");
			b.open();

			return false;
		}

		try {
			Integer.parseInt(txtPacksize.getText());
		} catch (NumberFormatException nfe) {
			MessageBox incorrectData = new MessageBox(getShell(),
					SWT.ICON_ERROR | SWT.OK);
			incorrectData.setText("Incorrect Pack Size");
			incorrectData
			.setMessage("The pack size that was entered is incorrect. Please enter a number.");
			incorrectData.open();
			txtPacksize.setFocus();
			return false;
		}

		if (txtAmountPerTime.isVisible()
				& (!txtAmountPerTime.getText().trim().equals(""))) {

			try {
				Double.parseDouble(txtAmountPerTime.getText().trim());
			} catch (NumberFormatException nfe) {
				MessageBox incorrectData = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);
				incorrectData.setText("Incorrect Standard Dosage Value");
				incorrectData
				.setMessage("The standard dosage that was entered is incorrect. Please enter a number.");
				incorrectData.open();
				txtAmountPerTime.setFocus();
				return false;
			}

		}

		if (!txtTimesPerDay.getText().trim().equals("")) {
			try {
				Integer.parseInt(txtTimesPerDay.getText());
			} catch (NumberFormatException nfe) {
				MessageBox incorrectData = new MessageBox(getShell(),
						SWT.ICON_ERROR | SWT.OK);
				incorrectData.setText("Incorrect Standard Dosage Value");
				incorrectData
				.setMessage("The standard dosage that was entered is incorrect. Please enter a number.");
				incorrectData.open();
				txtTimesPerDay.setFocus();
				return false;
			}
		}

		// end of checking all simple fields

		Set<ChemicalDrugStrength> chemicalDrugStrengthList = new HashSet<ChemicalDrugStrength>();

		// go through and check the chemical compounds table
		for (int i = 0; i < tblChemicalCompounds.getItemCount(); i++) {
			TableItem ti = tblChemicalCompounds.getItem(i);

			// there should be a strength for each checked chemical compound
			if (ti.getChecked()) {
				// create a ChemicalCompound object from the TableItem
				// information
				String currentChemComString = ti.getText(0);

				// get the acronym and name
				int endOfAcronym = currentChemComString.indexOf("]") + 1;
				String acronym = currentChemComString
				.substring(0, endOfAcronym).trim();
				String name = currentChemComString.substring(endOfAcronym + 1,
						currentChemComString.length()).trim();

				// create the chemicalCompound using name and acronym
				ChemicalCompound currentChemicalCompound = new ChemicalCompound(
						name, acronym);

				int strength = 0;
				try {
					strength = Integer.parseInt(ti.getText(1));

					if (strength <= 0) {
						MessageBox incorrectData = new MessageBox(getShell(),
								SWT.ICON_ERROR | SWT.OK);
						incorrectData.setText("Strength Not Valid");
						incorrectData.setMessage("The strength entered for  "
								+ ti.getText(0)
								+ " is invalid. Please enter a positive value");
						incorrectData.open();
						txtPacksize.setFocus();
						return false;
					}
				} catch (NumberFormatException nfe) {

					if (ti.getText(1).trim().equals("")) {
						MessageBox incorrectData = new MessageBox(getShell(),
								SWT.ICON_ERROR | SWT.OK);
						incorrectData.setText("Strength Not entered");
						incorrectData
						.setMessage("You have indicated that this drug contains the Chemical Component "
								+ ti.getText(0)
								+ ", but have not entered the strength.");
						incorrectData.open();
						txtPacksize.setFocus();
						return false;
					} else {
						MessageBox incorrectData = new MessageBox(getShell(),
								SWT.ICON_ERROR | SWT.OK);
						incorrectData.setText("Strength Not Entered");
						incorrectData
						.setMessage("The strength entered for  "
								+ ti.getText(0)
								+ " is not a number. \n\nPlease enter a number in the space provided.");
						incorrectData.open();
						txtPacksize.setFocus();
						return false;
					}
				}

				// get the drug
				if (localDrug == null) {
					localDrug = new Drug();
				}

				// create ChemicalDrugStrength which combines
				// chemicalCompound
				// and strength information
				ChemicalDrugStrength currentChemicalStrength = new ChemicalDrugStrength(
						currentChemicalCompound, strength, localDrug);

				// add to the set of chemical compounds for this drug
				chemicalDrugStrengthList.add(currentChemicalStrength);

			}
		}// end of for loop

		if (localDrug != null) {
			setLocalDrug();
		}
		// all ARV drugs must have a chemical compound and must not have the
		// same chemical compounds and strengths as an existing drug
		if (rdBtnARV.getSelection()) {

			if (localDrug == null) {
				localDrug = new Drug();
			}

			setLocalDrug();

			if (chemicalDrugStrengthList.isEmpty()) {
				MessageBox m = new MessageBox(getShell(), SWT.ICON_INFORMATION
						| SWT.OK);
				m.setText("No Chemical Compound");
				m
				.setMessage("All ARV drugs must have a chemical compound. \nPlease add a chemical for "
						+ localDrug.getName());
				m.open();

				return false;
			} else {
				/*// check that there are no other drugs already with the same
				// chemical composition
				String chemicalDrugMatch = DrugManager
				.existsChemicalComposition(getHSession(),
						chemicalDrugStrengthList, localDrug.getName());
				
				boolean flag = DrugManager
				.formChemicalComposition(getHSession(),
						chemicalDrugStrengthList, localDrug.getName(), localDrug.getForm().getForm());
				
				if (chemicalDrugMatch != null) {
					if(flag){
						MessageBox m = new MessageBox(getShell(),
								SWT.ICON_INFORMATION | SWT.OK);
						m.setText("Duplicate ARV Drug");
						m
						.setMessage("The drug you are trying to add has the same chemical composition as drug '"
								+ chemicalDrugMatch +" Form ("+localDrug.getForm().getForm()+")"
								+ "' which is already in the database.\n\nIf this is the same drug, but a different manufacturer, you will capture this information when you receive the stock using 'Stock Arrives at the Pharmacy' screen.");
						m.open();
						return false;
					}
					
				}*/
			}
		}
		return true;
	}

	private void setLocalDrug() {

		try {

			localDrug.setName(txtName.getText());

			localDrug.setPackSize(Integer.parseInt(txtPacksize.getText()));
			localDrug.setDispensingInstructions1(txtDispensingInstructions1
					.getText());
			localDrug.setDispensingInstructions2(txtDispensingInstructions2
					.getText());
			localDrug.setModified('T');
			localDrug.setForm(AdministrationManager.getForm(getHSession(),
					cmbForm.getText()));

			double amnt = 0;
			int times = 0;

			if (!txtAmountPerTime.getText().equals("")) {
				amnt = Double.valueOf(txtAmountPerTime.getText());

			}

			if (!txtTimesPerDay.getText().equals("")) {
				times = Integer.parseInt(txtTimesPerDay.getText());
			}

			localDrug.setDefaultAmnt(amnt);
			localDrug.setDefaultTimes(times);

			if (rdBtnSideTreatment.getSelection()) {
				localDrug.setSideTreatment('T');
			}

			else {
				localDrug.setSideTreatment('F');
			}

			localDrug.setNsnCode(txtNSN.getText());
			localDrug.setStockCode(txtStockCode.getText());

			if (localDrug.getChemicalDrugStrengths() == null) {
				localDrug
				.setChemicalDrugStrengths(new TreeSet<ChemicalDrugStrength>());
			}
			localDrug.getChemicalDrugStrengths().clear();

			for (int i = 0; i < tblChemicalCompounds.getItemCount(); i++) {
				TableItem ti = tblChemicalCompounds.getItem(i);
				if (ti.getChecked()) {

					Log.debug("found checked chem: " + ti);
					localDrug.getChemicalDrugStrengths().add(
							new ChemicalDrugStrength((ChemicalCompound) ti
									.getData(), Integer.parseInt(ti.getText(1)),
									localDrug));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
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

		txtName.setEnabled(enable);
		cmbForm.setEnabled(enable);
		txtPacksize.setEnabled(enable);
		txtDispensingInstructions1.setEnabled(enable);
		txtDispensingInstructions2.setEnabled(enable);
		rdBtnSideTreatment.setEnabled(enable);
		rdBtnARV.setEnabled(enable);
		txtAmountPerTime.setEnabled(enable);
		txtTimesPerDay.setEnabled(enable);
		txtNSN.setEnabled(enable);
		txtStockCode.setEnabled(enable);
		btnSave.setEnabled(enable);
		grpChemicalCompounds.setEnabled(enable);
		tblChemicalCompounds.setEnabled(enable);
		btnAddChemical.setEnabled(enable);

		if (enable) {
			cmbForm.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		} else {
			cmbForm.setBackground(ResourceUtils
					.getColor(iDartColor.WIDGET_BACKGROUND));
		}

	}

	/**
	 * Method populateGrpStandardDosages.
	 * 
	 * @param theFormString
	 *            String
	 */
	public void populateGrpStandardDosages(String theFormString) {
		Form form = AdministrationManager.getForm(getHSession(), theFormString);
		lblTake.setText(form.getActionLanguage1());
		lblTablets.setText(form.getFormLanguage1());

		if (lblTablets.getText().equals("")) {
			txtAmountPerTime.setVisible(false);
		} else {
			txtAmountPerTime.setVisible(true);
		}

		if ((form.getDispInstructions1() != null)
				&& (!form.getDispInstructions1().equals(""))) {
			txtDispensingInstructions1.setText(form.getDispInstructions1());
		}
		if ((form.getDispInstructions2() != null)
				&& (!form.getDispInstructions2().equals(""))) {
			txtDispensingInstructions2.setText(form.getDispInstructions2());
		}
	}

	/**
	 * This method initializes compInstructions
	 * 
	 */
	private void createCompInstructions() {
		compInstructions = new Composite(getShell(), SWT.NONE);
		compInstructions.setLayout(null);
		compInstructions.setBounds(new Rectangle(270, 79, 300, 25));

		lblInstructions = new Label(compInstructions, SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(0, 0, 300, 25));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));
	}

	/**
	 * This method initializes grpChemicalCcompounds
	 * 
	 */
	private void createGrpChemicalCompounds() {
		grpChemicalCompounds = new Group(getShell(), SWT.NONE);
		grpChemicalCompounds.setText("Chemical Composition ");
		grpChemicalCompounds.setBounds(new Rectangle(524, 110, 235, 372));
		grpChemicalCompounds.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpChemicalCompounds.setLayout(null);
		tblChemicalCompounds = new Table(grpChemicalCompounds, SWT.CHECK
				| SWT.FULL_SELECTION | SWT.BORDER);
		tblChemicalCompounds.setHeaderVisible(true);
		tblChemicalCompounds.setLinesVisible(true);
		tblChemicalCompounds.setBounds(new Rectangle(12, 20, 213, 301));
		tblChemicalCompounds.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		btnAddChemical = new Button(grpChemicalCompounds, SWT.NONE);
		btnAddChemical.setBounds(new Rectangle(47, 332, 178, 30));
		btnAddChemical.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnAddChemical.setText("Add Chemical Compound");
		btnAddChemical
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAddChemicalWidgetSelected();
			}
		});

		lblAddChemical = new Label(grpChemicalCompounds, SWT.NONE);
		lblAddChemical.setBounds(new Rectangle(14, 334, 30, 26));
		lblAddChemical.setText("");
		lblAddChemical.setImage(ResourceUtils.getImage(iDartImage.DRUG_30X26));

		TableColumn tblColChemicalCompounds = new TableColumn(
				tblChemicalCompounds, SWT.NONE);
		tblColChemicalCompounds.setWidth(140);
		tblColChemicalCompounds.setText("Chemical");

		TableColumn tblClmStrength = new TableColumn(tblChemicalCompounds,
				SWT.NONE);
		tblClmStrength.setWidth(72);
		tblClmStrength.setText("Strength");
		populateTblChemicalCompounds();

		editor = new TableEditor(tblChemicalCompounds);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		tblChemicalCompounds.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editor.getEditor();
				if (old != null) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblChemicalCompounds.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblChemicalCompounds.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					if (column == 1) {
						// Create the Text object for our editor

						final Text text = new Text(tblChemicalCompounds,
								SWT.NONE);
						text.setForeground(item.getForeground());
						text.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

						final int col = column;
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {

								item.setText(col, text.getText());

								// if you've set a strength, check the column
								if (!text.getText().trim().equals("")) {
									item.setChecked(true);
								}

								else if (item.getChecked()) {
									item.setChecked(false);
								}
							}
						});
					}
				}
			}
		});

		// if the user unchecks (or checks) a colum, clear the current contents
		// of the strength field
		tblChemicalCompounds.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {

				if (event.detail == SWT.CHECK) {
					TableItem ti = (TableItem) event.item;
					ti.setText(1, "");

				}
			}
		});

	}

	private void populateTblChemicalCompounds() {
		List<ChemicalCompound> chemicalCompoundList = new ArrayList<ChemicalCompound>();

		chemicalCompoundList = DrugManager
		.getAllChemicalCompounds(getHSession());

		Iterator<ChemicalCompound> chemicalCompoundIt = chemicalCompoundList
		.iterator();

		while (chemicalCompoundIt.hasNext()) {
			ChemicalCompound cc = chemicalCompoundIt.next();
			TableItem ti = new TableItem(tblChemicalCompounds, SWT.NONE);

			// populate table
			ti.setText(0, "[" + cc.getAcronym() + "] " + cc.getName());
			ti.setData(cc);
		}
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

}
