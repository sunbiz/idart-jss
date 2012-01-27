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

package org.celllife.idart.gui.prescription;

import model.manager.DrugManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

/**
 */
public class PrescriptionObject extends GenericOthersGui {

	private Group grpDrugBarcode;

	private Label lblBarcode;

	private Label lblTake;

	private Text txtTake;

	private Label lblDescription;

	private Label lblTimes;

	private TableItem tableItem;

	private Drug newDrug;

	private Button drugBarCodeSearch;

	private Text txtDrugName;

	private Group grpDrugInformation;

	private Label lblDrugName;

	private Button btnAddDrug;

	private Button btnClear;

	private Button btnCancel;

	private boolean isRegimen = false;

	private Text txtTimes;

	/**
	 * Constructor
	 * 
	 * @param hSession
	 *            Session
	 * @param ti
	 *            TableItem
	 * @param isRegimen
	 *            boolean
	 * @param parent
	 *            Shell
	 * @param localPrescription
	 *            Prescription
	 */
	public PrescriptionObject(Session hSession, TableItem ti,
			boolean isRegimen, Shell parent) {
		super(parent, hSession);
		this.isRegimen = isRegimen;
		activate();
		this.tableItem = ti;
		tableItem = ti;
		// should open immediately???
		cmdSearchWidgetSelected();
	}

	/**
	 * This method initializes getShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = isRegimen ? "Add Drug to this Drug Group"
				: "Add Drug to a Prescription";
		Rectangle bounds = new Rectangle(300, 200, 500, 430);
		buildShell(shellTxt, bounds);
		getShell().addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				cmdCancelWidgetSelected();
			}
		});
		createGrpDrugBarcode();
		createGrpDrugInformation();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		String txt = (isRegimen ? "Add Drug to this Drug Group"
				: "Add Drug to a Prescription");
		iDartImage icoImage = iDartImage.PRESCRIPTIONADDDRUG;
		buildCompHeader(txt, icoImage);
		lblHeader.setSize(lblHeader.getBounds().width + 100, lblHeader
				.getBounds().height);
		lblBg.setSize(lblBg.getBounds().width + 100, lblBg.getBounds().height);
	}

	/*
	 * This method initializes grpDrugBarcode
	 */
	private void createGrpDrugBarcode() {

		grpDrugBarcode = new Group(getShell(), SWT.NONE);
		grpDrugBarcode.setBounds(new org.eclipse.swt.graphics.Rectangle(45,
				120, 410, 45));

		lblBarcode = new Label(grpDrugBarcode, SWT.NONE);
		lblBarcode.setBounds(new org.eclipse.swt.graphics.Rectangle(15, 15,
				200, 20));
		lblBarcode.setText("Please Search for a Drug to Add:");
		lblBarcode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		drugBarCodeSearch = new Button(grpDrugBarcode, SWT.NONE);
		drugBarCodeSearch.setBounds(new org.eclipse.swt.graphics.Rectangle(280,
				12, 120, 26));
		drugBarCodeSearch.setText("Drug Search");
		drugBarCodeSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		drugBarCodeSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});
	}

	/**
	 * This method initializes grpDrugInformation
	 * 
	 */
	private void createGrpDrugInformation() {

		grpDrugInformation = new Group(getShell(), SWT.NONE);
		grpDrugInformation
		.setText("Drug Information and Dispensing Instruction");
		grpDrugInformation.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDrugInformation.setBounds(new Rectangle(85, 180, 350, 127));

		lblDrugName = new Label(grpDrugInformation, SWT.NONE);
		lblDrugName.setBounds(new org.eclipse.swt.graphics.Rectangle(15, 30,
				90, 20));
		lblDrugName.setText("Drug Name: ");
		lblDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDrugName = new Text(grpDrugInformation, SWT.BORDER);
		txtDrugName.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 30,
				184, 20));
		txtDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDrugName.setEnabled(false);

		lblTake = new Label(grpDrugInformation, SWT.NONE);
		lblTake.setBounds(new Rectangle(14, 59, 90, 20));
		lblTake.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblTake.setText("Take:");
		txtTake = new Text(grpDrugInformation, SWT.BORDER);
		txtTake.setBounds(new Rectangle(149, 59, 60, 20));
		txtTake.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtTake.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtTake.selectAll();
			}

		});
		txtTake.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdAddDrugWidgetSelected();
				}
			}

		});

		lblDescription = new Label(grpDrugInformation, SWT.NONE);
		lblDescription.setBounds(new Rectangle(219, 59, 120, 20));
		lblDescription.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblTimes = new Label(grpDrugInformation, SWT.NONE);
		lblTimes.setBounds(new Rectangle(14, 89, 90, 20));
		lblTimes.setText("Times a day:");
		lblTimes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtTimes = new Text(grpDrugInformation, SWT.NONE);
		txtTimes.setBounds(new Rectangle(149, 89, 60, 20));
		txtTimes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtTimes.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtTimes.selectAll();
			}

		});

		txtTimes.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdAddDrugWidgetSelected();
				}
			}

		});
	}

	/**
	 * Clears the patientForm and sets the default values
	 */
	public void clearForm() {
		txtTake.setVisible(true);
		lblDescription.setVisible(true);
		lblTake.setVisible(true);

		lblTake.setText("Take:");
		lblDescription.setText("");

		txtTake.setText("");
		txtTimes.setText("");
		txtDrugName.setText("");

		newDrug = null;

		enableFields(false);
	}

	private void cmdSearchWidgetSelected() {
		Search drugSearch = new Search(getHSession(), getShell(),
				CommonObjects.DRUG);
		if (drugSearch.getValueSelected() != null) {
			txtDrugName.setText(drugSearch.getValueSelected()[0]);
			loadDrugInformation();
			drugBarCodeSearch.setEnabled(false);
			enableFields(true);
			txtTake.selectAll();
			txtTake.setFocus();

		} else {
			enableFields(false);
			// enableAllControls(false);
		}
	}

	private void loadDrugInformation() {
		newDrug = DrugManager.getDrug(getHSession(), txtDrugName.getText());
		if (newDrug != null) {
			txtDrugName.setText(newDrug.getName());
			lblDescription.setText(newDrug.getForm().getFormLanguage1());
			lblTake.setText(newDrug.getForm().getActionLanguage1());
			txtTake.setFocus();
			int[] standardDosage = new int[2];

			// is a cream with no amount per time
			if (newDrug.getForm().getFormLanguage1().equals("")) {
				txtTake.setVisible(false);
				lblDescription.setVisible(false);
				lblTake.setVisible(false);
			} else {
				txtTake.setVisible(true);
				lblDescription.setVisible(true);
				lblTake.setVisible(true);
				double takeAmount = newDrug.getDefaultAmnt();
				String takeAmountStr = String.valueOf(takeAmount);

				// if the default take amount is actually a whole number
				// avoid it being displayed as a double
				if ((takeAmountStr.charAt(takeAmountStr.length() - 1) == '0')
						&& (takeAmountStr.charAt(takeAmountStr.length() - 2) == '.')) {
					txtTake.setText(String.valueOf((int) takeAmount));
				} else {
					txtTake.setText(takeAmountStr);
				}
			}
			txtTimes.setText(newDrug.getDefaultTimes() == 0 ? "1" : String
					.valueOf(newDrug.getDefaultTimes()));

			if (standardDosage[0] != 0) {
				btnAddDrug.setFocus();
			} else {
				txtTake.setFocus();
			}
		} else {
			MessageBox m = new MessageBox(getShell(), SWT.ICON_ERROR);
			m.setMessage("Drug '" + txtDrugName.getText()
					+ "' was not found in the database.");
			m.setText("Drug Not Found");
			m.open();
			txtDrugName.setText("");
		}
		txtTake.selectAll();
	}

	/**
	 * Check if the form is completed before proceeding
	 * 
	 * @return true if all fields are correctly filled in
	 */
	private boolean fieldsOk() {
		boolean result = true;
		if (newDrug == null) {
			MessageBox noDrugLoaded = new MessageBox(getShell(), SWT.ICON_ERROR);
			noDrugLoaded.setMessage("Please load a drug.");
			noDrugLoaded.setText("Field missing");
			noDrugLoaded.open();
			result = false;
			txtDrugName.setFocus();
		}
		if (txtTake.isVisible()) {
			if (txtTake.getText().equals("")) {
				MessageBox take = new MessageBox(getShell(), SWT.ICON_ERROR);
				take.setMessage("Please fill in the 'Take' field.");
				take.setText("Field missing");
				take.open();
				result = false;
				txtTake.setFocus();
			} else {
				Double takeAmount;
				try {
					takeAmount = Double.valueOf(txtTake.getText());
					if (!(takeAmount > 0)) {
						MessageBox notANumber = new MessageBox(getShell(),
								SWT.ICON_ERROR);
						notANumber
						.setMessage("The quantity supplied in the 'Take' field must be greater than 0.");
						notANumber.setText("Incorrect Information");
						notANumber.open();
						result = false;
						txtTake.setFocus();
					}
				} catch (Exception e) {
					MessageBox notANumber = new MessageBox(getShell(),
							SWT.ICON_ERROR);
					notANumber
					.setMessage("The information supplied in the 'Take' field is not a number.");
					notANumber.setText("Incorrect Information");
					notANumber.open();
					result = false;
					txtTake.setFocus();
				}
			}
		}
		if (txtTimes.getText().trim().equals("")) {
			MessageBox times = new MessageBox(getShell(), SWT.ICON_ERROR);
			times.setMessage("Please fill in the 'Times' field.");
			times.setText("Field mising");
			times.open();
			result = false;
			txtTimes.setFocus();
		} else {
			try {
				int times = Integer.parseInt(txtTimes.getText().trim());
				if (!(times > 0)) {
					MessageBox notANumber = new MessageBox(getShell(),
							SWT.ICON_ERROR);
					notANumber
					.setMessage("The quantity supplied in the 'Times' field must be greater than 0.");
					notANumber.setText("Incorrect Information");
					notANumber.open();
					result = false;
					txtTake.setFocus();
				}
			} catch (NumberFormatException e) {
				MessageBox notANumber = new MessageBox(getShell(),
						SWT.ICON_ERROR);
				notANumber
				.setMessage("The information supplied in the 'Times' field is not a number.");
				notANumber.setText("Incorrect Information");
				notANumber.open();
				result = false;
				txtTimes.setFocus();
			}
		}
		return result;
	}

	private void cmdAddDrugWidgetSelected() {
		if (fieldsOk()) {

			if (isRegimen) {
				// Create new RegimenDrug
				RegimenDrugs rd = new RegimenDrugs();
				rd.setAmtPerTime(txtTake.isVisible() ? Double.valueOf(
						txtTake.getText()).doubleValue() : 0);
				rd.setDrug(newDrug);
				rd.setModified('T');
				rd.setTimesPerDay(Integer.parseInt(txtTimes.getText()));
				tableItem.setData(rd);
			} else {
				// Create new PrescribedDrug
				PrescribedDrugs pd = new PrescribedDrugs();
				pd.setAmtPerTime(txtTake.isVisible() ? Double.valueOf(
						txtTake.getText()).doubleValue() : 0);
				pd.setDrug(newDrug);
				pd.setModified('T');
				pd.setTimesPerDay(Integer.parseInt(txtTimes.getText().trim()));
				tableItem.setData(pd);
			}
			Form f = newDrug.getForm();
			String[] temp = new String[8];
			temp[0] = tableItem.getText(0);
			temp[1] = newDrug.getName();
			temp[2] = f.getActionLanguage1();
			temp[3] = (txtTake.isVisible() ? txtTake.getText() : "");
			temp[4] = f.getFormLanguage1();
			temp[5] = txtTimes.getText();
			temp[6] = "times a day";
			tableItem.setText(temp);
			txtTimes.selectAll();
			closeShell(false);

		}
	}

	private void cmdClearWidgetSelected() {
		clearForm();
		txtDrugName.setFocus();
		drugBarCodeSearch.setEnabled(true);
	}

	private void cmdCancelWidgetSelected() {
		tableItem.dispose();
		closeShell(false);
	}

	/**
	 * Method setTableItem.
	 * 
	 * @param tableItem
	 *            TableItem
	 */
	public void setTableItem(TableItem tableItem) {
		this.tableItem = tableItem;
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnAddDrug = new Button(getCompButtons(), SWT.NONE);
		btnAddDrug.setSize(150, 30);
		if (isRegimen) {
			btnAddDrug.setText("Add to Group");
			btnAddDrug
			.setToolTipText("Press this button to add this drug to the regimen.");
		} else {
			btnAddDrug.setText("Add to Prescription");
			btnAddDrug
			.setToolTipText("Press this button to add this drug to the prescription.");
		}
		btnAddDrug.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnAddDrug
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdAddDrugWidgetSelected();
			}
		});

		btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear
		.setToolTipText("Press this button to clear all the information \nyou've entered, so that you can start again.");
		btnClear.setText("Clear");
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdClearWidgetSelected();
			}
		});

		btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnCancel.setText("Cancel");
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});
	}

	private void enableFields(boolean enable) {
		txtTake.setEnabled(enable);
		txtTimes.setEnabled(enable);
		btnAddDrug.setEnabled(enable);
	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
