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

package org.celllife.idart.gui.stockCenter;

import model.manager.AdministrationManager;
import model.nonPersistent.PharmacyDetails;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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
public class StockCenterInfo extends GenericFormGui {

	// contains stockCenter details Displayed as Facility Details
	private Group grpSelectStockCenter;

	private Button rdBtnAddStockCenter;

	private Button rdBtnUpdateStockCenter;

	private Button rdBtnUpdatePharmacyDetails;

	private Label lblStockCenterName;

	private Text txtStockCenterName;

	private Button btnSearch;

	private Label lblPreferredStockCenter;

	private Button rdBtnPreferredYes;

	private Button rdBtnPreferredNo;

	//Contains the Facility Details group as well as the
	// preview label
	private Group grpPharmacyDetails;

	private Group grpContactDetails;

	private Label lblInstructions;

	private Label lblPharmacyName;

	private Text txtPharmacyName;

	private Label lblStreetAdd;

	private Text txtStreetAdd;

	private Label lblCity;

	private Text txtCity;

	private Label lblTel;

	private Text txtTel;

	private Label lblPharmacistName1;

	private Text txtPharmacistName1;

	private Label lblPharmacyAssistant;

	private Text txtPharmacyAssistant;

	private Group grpLabel;

	private Canvas canvasLabel;

	private Label lblCanvasPharmName;

	private Label lblCanvasPharmacist;

	private Label lblCanvasAddress;

	private PharmacyDetails localFacilityDetails;

	private boolean fieldsChanged;

	// This field is specifically used to check if the user tries to
	// update a stockCenter from the preferred to non preferred
	private boolean preferredChanged;

	private StockCenter localStockCenter;

	private final String[] screenModes = {"Add StockCenter", "Update StockCenter",
	"Update Facility Details" };

	private String currentScreenMode;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public StockCenterInfo(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		init();
	}

	public void init() {
		localFacilityDetails = new PharmacyDetails();
		localFacilityDetails = LocalObjects.pharmacy;
		localStockCenter = new StockCenter();
		currentScreenMode = screenModes[0];
	}

	/**
	 * This method initialises newPharmacyDetailsInfo
	 */
	@Override
	protected void createShell() {
		String shellText = "Add a New Pharmacy";
		Rectangle bounds = new Rectangle(25, 0, 800, 680);
		buildShell(shellText, bounds);
	}

	@Override
	protected void createContents() {
		fieldsChanged = false;
		preferredChanged = false;
		createGrpSelectStockCenter();
		createGrpPharmacyDetails();
		createGrpContactDetails();
		createGrpLabel();

	}

	/**
	 * This method initialises compHeader
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Add a New Pharmacy";
		iDartImage icoImage = iDartImage.PHARMACYUSER;
		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createCompButtons() {
		// Parent Class generic call
		buildCompButtons();
	}

	/**
	 * This method initialises grpSelectPharmacy
	 */
	private void createGrpSelectStockCenter() {
		// create widgets
		grpSelectStockCenter = new Group(getShell(), SWT.NONE);
		grpSelectStockCenter.setBounds(new Rectangle(40, 100, 720, 120));
		grpSelectStockCenter.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpSelectStockCenter
		.setText("Pharmacy Details (related to where stock is held)");

		rdBtnAddStockCenter = new Button(grpSelectStockCenter, SWT.RADIO);
		rdBtnAddStockCenter.setBounds(new Rectangle(105, 25, 150, 30));
		rdBtnAddStockCenter.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnAddStockCenter.setText("Add New Pharmacy");

		rdBtnUpdateStockCenter = new Button(grpSelectStockCenter, SWT.RADIO);
		rdBtnUpdateStockCenter.setBounds(new Rectangle(280, 20, 150, 30));
		rdBtnUpdateStockCenter.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnUpdateStockCenter.setText("Update a Pharmacy");

		rdBtnUpdatePharmacyDetails = new Button(grpSelectStockCenter, SWT.RADIO);
		rdBtnUpdatePharmacyDetails.setBounds(new Rectangle(455, 20, 160, 30));
		rdBtnUpdatePharmacyDetails.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnUpdatePharmacyDetails.setText("Update Facility Details");

		lblStockCenterName = new Label(grpSelectStockCenter, SWT.NONE);
		lblStockCenterName.setBounds(new Rectangle(155, 60, 100, 20));
		lblStockCenterName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblStockCenterName.setAlignment(SWT.RIGHT);
		lblStockCenterName.setText("Pharmacy name:");

		txtStockCenterName = new Text(grpSelectStockCenter, SWT.BORDER);
		txtStockCenterName.setBounds(new Rectangle(
				265, 60, 200, 20));
		txtStockCenterName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtStockCenterName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				if ((btnSearch != null) && (btnSearch.getEnabled()) && rdBtnUpdateStockCenter.getSelection()) {
					if (evt.character == SWT.CR) {
						cmdEnterPressed();
					}
				}
				if(rdBtnAddStockCenter.getSelection()) {
					fieldsChanged = true;
				}
				rdBtnPreferredNo.setEnabled(true);
				rdBtnPreferredYes.setEnabled(true);

			}
		});
		txtStockCenterName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event1) {
				if(rdBtnUpdateStockCenter.getSelection()) {
					fieldsChanged = true;
				}
			}
		});

		btnSearch = new Button(grpSelectStockCenter, SWT.NONE);
		btnSearch.setBounds(new Rectangle(470, 57, 110, 28));
		btnSearch.setText("Pharmacy Search");
		btnSearch
		.setToolTipText("Press this button to search for pharmacies.");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchWidgetSelected();
			}
		});
		btnSearch.setVisible(false);

		lblPreferredStockCenter = new Label(grpSelectStockCenter, SWT.NONE);
		lblPreferredStockCenter.setBounds(new Rectangle(140, 97, 200, 20));
		lblPreferredStockCenter.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPreferredStockCenter.setAlignment(SWT.RIGHT);
		lblPreferredStockCenter.setText("Preferred Pharmacy?");

		Composite cmpRadio = new Composite(grpSelectStockCenter, SWT.NONE);
		cmpRadio.setLayout(null);
		cmpRadio.setBounds(new Rectangle(340, 75, 100, 40));

		rdBtnPreferredYes = new Button(cmpRadio, SWT.RADIO);
		rdBtnPreferredYes.setBounds(new Rectangle(10, 10, 40, 30));
		rdBtnPreferredYes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnPreferredYes.setText("Yes");
		rdBtnPreferredYes.setEnabled(false);
		rdBtnPreferredYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(rdBtnUpdateStockCenter.getSelection() && (!rdBtnPreferredYes.getSelection())) {
					fieldsChanged = true;
					if(rdBtnUpdateStockCenter.getSelection() &&
							!"".equalsIgnoreCase(txtStockCenterName.getText()) &&
							localStockCenter.isPreferred()) {
						preferredChanged = true;
					}
				} else {
					preferredChanged = false;
				}
			}
		});

		rdBtnPreferredNo = new Button(cmpRadio, SWT.RADIO);
		rdBtnPreferredNo.setBounds(new Rectangle(60, 10, 40, 30));
		rdBtnPreferredNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnPreferredNo.setText("No");
		rdBtnPreferredNo.setSelection(true);
		rdBtnPreferredNo.setEnabled(false);
		rdBtnPreferredNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (rdBtnUpdateStockCenter.getSelection()
						&& (!rdBtnPreferredNo.getSelection())) {
					fieldsChanged = true;
				}
				if (rdBtnUpdateStockCenter.getSelection() && preferredChanged
						&& rdBtnPreferredNo.getSelection()) {
					MessageBox mbox = new MessageBox(getShell(),
							SWT.ICON_INFORMATION | SWT.OK);
					mbox.setText("Cannot Change Preferred Pharmacy ");
					mbox
					.setMessage("'"
							+ txtStockCenterName.getText()
							+ "' is currently the preferred pharmacy and this cannot be changed here.\n\nIf you would like to make another pharmacy the preferred one, please select it using the search button, and then set that pharmacy to the preferred one. ");
					mbox.open();

					rdBtnPreferredYes.setSelection(true);
					rdBtnPreferredNo.setSelection(false);

					if (""
							.equalsIgnoreCase(txtStockCenterName.getText()
									.trim())
									|| localStockCenter.getStockCenterName().equalsIgnoreCase(txtStockCenterName.getText())) {
						fieldsChanged = false;
					}
				}
			}
		});


		// add listeners
		rdBtnAddStockCenter
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnAddStockCenter.getSelection()) {

					if(changeModes()) {

						String headerTxt = "Add a New Pharmacy";
						iDartImage icoImage = iDartImage.PHARMACYUSER;
						buildCompHeader(headerTxt, icoImage);
						getShell().setText("Add a New Pharmacy");
						currentScreenMode = screenModes[0];
						clearForm();
						enableFields();
						// reset fields changed
						fieldsChanged = false;

					} else {
						rdBtnAddStockCenter.setSelection(false);
						rdBtnUpdateStockCenter.setSelection(false);
						rdBtnUpdatePharmacyDetails.setSelection(false);

						// select previous value
						if(currentScreenMode.equalsIgnoreCase(screenModes[0])) {
							rdBtnAddStockCenter.setSelection(true);
						} else if(currentScreenMode.equalsIgnoreCase(screenModes[1])) {
							rdBtnUpdateStockCenter.setSelection(true);
						} else {
							rdBtnUpdatePharmacyDetails.setSelection(true);
						}
					}
				}
			}
		});

		rdBtnUpdateStockCenter
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnUpdateStockCenter.getSelection()) {

					if (changeModes()) {

						String headerTxt = "Update a Pharmacy";
						iDartImage icoImage = iDartImage.PHARMACYUSER;
						buildCompHeader(headerTxt, icoImage);
								getShell().setText("Update a Pharmacy");
						currentScreenMode = screenModes[1];
						clearForm();
						enableFields();
						// reset fields changed
						fieldsChanged = false;


					} else {
						rdBtnAddStockCenter.setSelection(false);
						rdBtnUpdateStockCenter.setSelection(false);
						rdBtnUpdatePharmacyDetails.setSelection(false);

						// select previous value
						if(currentScreenMode.equalsIgnoreCase(screenModes[0])) {
							rdBtnAddStockCenter.setSelection(true);
						} else if(currentScreenMode.equalsIgnoreCase(screenModes[1])) {
							rdBtnUpdateStockCenter.setSelection(true);
						} else {
							rdBtnUpdatePharmacyDetails.setSelection(true);
						}
					}

				}
			}
		});

		rdBtnUpdatePharmacyDetails
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if (rdBtnUpdatePharmacyDetails.getSelection()) {

					if (changeModes()) {

						String headerTxt = "Update Facility Details";
						iDartImage icoImage = iDartImage.PHARMACYUSER;
						buildCompHeader(headerTxt, icoImage);
						getShell().setText("Update Facility Details");
						currentScreenMode = screenModes[2];
						clearForm();
						enableFields();
						// reset fields changed
						fieldsChanged = false;
						loadPharmacyDetails();

					} else {
						rdBtnAddStockCenter.setSelection(false);
						rdBtnUpdateStockCenter.setSelection(false);
						rdBtnUpdatePharmacyDetails.setSelection(false);

						// select previous value
						if(currentScreenMode.equalsIgnoreCase(screenModes[0])) {
							rdBtnAddStockCenter.setSelection(true);
						} else if(currentScreenMode.equalsIgnoreCase(screenModes[1])) {
							rdBtnUpdateStockCenter.setSelection(true);
						} else {
							rdBtnUpdatePharmacyDetails.setSelection(true);
						}
					}

				}
			}
		});

		// set initial values
		rdBtnAddStockCenter.setSelection(true);
		rdBtnUpdateStockCenter.setSelection(false);
		rdBtnUpdatePharmacyDetails.setSelection(false);

	}

	/**
	 * This method checks if localFacilityDetails is initialised and
	 * also checks if changes were made in the screen
	 * @return
	 */
	private boolean changeModes() {

		if (fieldsChanged) {
			MessageBox mbWarning = new MessageBox(getShell(),
					SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			mbWarning.setText("iDART: Save Changes?");
			mbWarning
			.setMessage("You have not saved your changes. Would you like to save your changes?");
			switch( mbWarning.open()) {

			// proceed but save changes
			case SWT.YES:
				cmdSaveWidgetSelected();
				return true;
				// proceed without saving changes
			case SWT.NO:
				return true;
				// remain in current mode
			case SWT.CANCEL:
				return false;
			}
		}
		// proceed to next mode
		return true;
	}

	/**
	 * This method initialises grpPharmacies
	 */
	private void createGrpPharmacyDetails() {
		grpPharmacyDetails = new Group(getShell(), SWT.NONE);
		grpPharmacyDetails.setText("Facility Details (shown on labels and reports)");
		grpPharmacyDetails.setBounds(new Rectangle(40, 250, 720, 320));
		grpPharmacyDetails.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	/**
	 * This method initialises grpContactDetails
	 */
	private void createGrpContactDetails() {

		grpContactDetails = new Group(grpPharmacyDetails, SWT.NONE);
		grpContactDetails.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpContactDetails.setBounds(new Rectangle(20, 40, 350, 230));

		lblInstructions = new Label(grpContactDetails, SWT.CENTER);
		lblInstructions.setBounds(new org.eclipse.swt.graphics.Rectangle(50,
				20, 260, 20));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));

		lblPharmacyName = new Label(grpContactDetails, SWT.NONE);
		lblPharmacyName.setBounds(new org.eclipse.swt.graphics.Rectangle(18, 45, 120,
				20));
		lblPharmacyName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPharmacyName.setText("* Facility Name:");
		txtPharmacyName = new Text(grpContactDetails, SWT.BORDER);
		txtPharmacyName.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 45, 180,
				20));
		txtPharmacyName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPharmacyName.setFocus();
		txtPharmacyName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				txtNameKeyReleased();
				fieldsChanged = true;
			}

		});
		txtPharmacyName.setEnabled(false);

		lblStreetAdd = new Label(grpContactDetails, SWT.NONE);
		lblStreetAdd.setBounds(new org.eclipse.swt.graphics.Rectangle(18, 75,
				120, 20));
		lblStreetAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblStreetAdd.setText("* Street Address:");
		txtStreetAdd = new Text(grpContactDetails, SWT.BORDER);
		txtStreetAdd.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 75,
				180, 20));
		txtStreetAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtStreetAdd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				txtStreetKeyReleased();
				fieldsChanged = true;
			}
		});
		txtStreetAdd.setEnabled(false);

		lblCity = new Label(grpContactDetails, SWT.NONE);
		lblCity.setBounds(new org.eclipse.swt.graphics.Rectangle(18, 105, 120,
				20));
		lblCity.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCity.setText("* City:");
		txtCity = new Text(grpContactDetails, SWT.BORDER);
		txtCity.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 105, 180,
				20));
		txtCity.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtCity.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				txtCityKeyReleased();
				fieldsChanged = true;
			}
		});
		txtCity.setEnabled(false);

		lblTel = new Label(grpContactDetails, SWT.NONE);
		lblTel.setBounds(new org.eclipse.swt.graphics.Rectangle(18, 135, 124,
				20));
		lblTel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblTel.setText("* Telephone Number:");
		txtTel = new Text(grpContactDetails, SWT.BORDER);
		txtTel.setBounds(new org.eclipse.swt.graphics.Rectangle(150, 135, 180,
				20));
		txtTel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtTel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				txtTelNoKeyReleased();
				fieldsChanged = true;
			}
		});
		txtTel.setEnabled(false);

		lblPharmacistName1 = new Label(grpContactDetails, SWT.NONE);
		lblPharmacistName1.setBounds(new org.eclipse.swt.graphics.Rectangle(18,
				165, 120, 20));
		lblPharmacistName1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPharmacistName1.setText("* Head Pharmacist:");
		txtPharmacistName1 = new Text(grpContactDetails, SWT.BORDER);
		txtPharmacistName1.setBounds(new org.eclipse.swt.graphics.Rectangle(
				150, 165, 180, 20));
		txtPharmacistName1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPharmacistName1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				txtPharmacistKeyReleased();
				fieldsChanged = true;
			}
		});
		txtPharmacistName1.setEnabled(false);

		lblPharmacyAssistant = new Label(grpContactDetails, SWT.NONE);
		lblPharmacyAssistant.setBounds(new org.eclipse.swt.graphics.Rectangle(18,
				195, 120, 20));
		lblPharmacyAssistant.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblPharmacyAssistant.setText("  Pharmacy Assistant:");
		txtPharmacyAssistant = new Text(grpContactDetails, SWT.BORDER);
		txtPharmacyAssistant.setBounds(new org.eclipse.swt.graphics.Rectangle(
				150, 195, 180, 20));
		txtPharmacyAssistant.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPharmacyAssistant.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent evt) {
				txtPharmacistKeyReleased();
				fieldsChanged = true;
			}
		});
		txtPharmacyAssistant.setEnabled(false);

	}

	/**
	 * This method initialises grpLabel
	 */
	private void createGrpLabel() {

		grpLabel = new Group(grpPharmacyDetails, SWT.NONE);
		grpLabel.setText("Preview of Label");
		grpLabel.setBounds(new Rectangle(390, 40, 310, 230));
		grpLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		canvasLabel = new Canvas(grpLabel, SWT.NONE);
		canvasLabel.setBounds(new org.eclipse.swt.graphics.Rectangle(12, 18,
				285, 200));
		canvasLabel.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		createCanvasBorders();

		lblCanvasPharmName = new Label(canvasLabel, SWT.NONE);
		lblCanvasPharmName.setText("Facility Name");
		lblCanvasPharmName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCanvasPharmName.setBackground(ResourceUtils
				.getColor(iDartColor.WHITE));
		lblCanvasPharmName.setBounds(5, 6, 273, 20);
		lblCanvasPharmName
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		lblCanvasPharmName.setAlignment(SWT.CENTER);

		lblCanvasPharmacist = new Label(canvasLabel, SWT.NONE);
		lblCanvasPharmacist.setText("Pharmacist");
		lblCanvasPharmacist
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCanvasPharmacist.setBackground(ResourceUtils
				.getColor(iDartColor.WHITE));
		lblCanvasPharmacist.setBounds(5, 27, 273, 20);
		lblCanvasPharmacist.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10));
		lblCanvasPharmacist.setAlignment(SWT.CENTER);

		lblCanvasAddress = new Label(canvasLabel, SWT.NONE);
		lblCanvasAddress.setText("Physical Address");
		lblCanvasAddress.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblCanvasAddress
		.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		lblCanvasAddress.setBounds(5, 49, 273, 20);
		lblCanvasAddress.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));
		lblCanvasAddress.setAlignment(SWT.CENTER);

	}

	/**
	 * This method creates the ResourceUtils.getColor(iDartColor.BLACK) borders
	 * around the Label to be printed.
	 */
	private void createCanvasBorders() {

		Label lblBorderMiddle = new Label(canvasLabel, SWT.NONE);
		lblBorderMiddle.setBackground(ResourceUtils.getColor(iDartColor.BLACK));
		lblBorderMiddle.setBounds(0, 72, 285, 2);

		Label lblBorderBottom = new Label(canvasLabel, SWT.NONE);
		lblBorderBottom.setBounds(0, 198, 285, 2);
		lblBorderBottom.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		Label lblBorderLeft = new Label(canvasLabel, SWT.NONE);
		lblBorderLeft.setBounds(0, 0, 2, 200);
		lblBorderLeft.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		Label lblBorderRight = new Label(canvasLabel, SWT.NONE);
		lblBorderRight.setBounds(283, 0, 2, 200);
		lblBorderRight.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		Label lblBorderTop = new Label(canvasLabel, SWT.NONE);
		lblBorderTop.setBounds(0, 0, 285, 2);
		lblBorderTop.setBackground(ResourceUtils.getColor(iDartColor.BLACK));
	}

	/**
	 * This method initialises compButtons
	 */

	private void txtNameKeyReleased() {

		lblCanvasPharmName.setText(txtPharmacyName.getText());
	}

	private void txtPharmacistKeyReleased() {

		lblCanvasPharmacist.setText(txtPharmacistName1.getText());
	}

	private void txtStreetKeyReleased() {

		lblCanvasAddress.setText(txtStreetAdd.getText() + ", "
				+ txtCity.getText() + ", " + txtTel.getText());
	}

	private void txtCityKeyReleased() {

		lblCanvasAddress.setText(txtStreetAdd.getText() + ", "
				+ txtCity.getText() + ", " + txtTel.getText());
	}

	private void txtTelNoKeyReleased() {

		lblCanvasAddress.setText(txtStreetAdd.getText() + ", "
				+ txtCity.getText() + ", " + txtTel.getText());
	}

	/**
	 * Clears the current mode only
	 * to be used when the user presses the clear button
	 */
	public void clearMode() {

		// Clear mode only
		if(rdBtnAddStockCenter.getSelection()) {
			txtStockCenterName.setText("");
			rdBtnPreferredNo.setSelection(true);
			rdBtnPreferredYes.setSelection(false);
			rdBtnPreferredNo.setEnabled(false);
			rdBtnPreferredYes.setEnabled(false);
		}
		else if(rdBtnUpdateStockCenter.getSelection()) {
			txtStockCenterName.setText("");
			rdBtnPreferredNo.setSelection(true);
			rdBtnPreferredYes.setSelection(false);
			btnSearch.setEnabled(true);
			rdBtnPreferredNo.setEnabled(false);
			rdBtnPreferredYes.setEnabled(false);
		}
		else if(rdBtnUpdatePharmacyDetails.getSelection()) {

			// clear the text fields
			txtPharmacyName.setText("");
			txtStreetAdd.setText("");
			txtCity.setText("");
			txtTel.setText("");
			txtPharmacistName1.setText("");
			txtPharmacyAssistant.setText("");
			lblCanvasPharmName.setText("Facility Name");
			lblCanvasPharmacist.setText("Pharmacist");
			lblCanvasAddress.setText("Physical Address");
		}

		fieldsChanged = false;
		enableFields();

	}

	/**
	 * Clears the whole form
	 */
	@Override
	public void clearForm() {

		txtStockCenterName.setText("");
		rdBtnPreferredNo.setSelection(true);
		rdBtnPreferredYes.setSelection(false);

		// clear the text fields
		txtPharmacyName.setText("");
		txtStreetAdd.setText("");
		txtCity.setText("");
		txtTel.setText("");
		txtPharmacistName1.setText("");
		txtPharmacyAssistant.setText("");
		lblCanvasPharmName.setText("Facility Name");
		lblCanvasPharmacist.setText("Pharmacist");
		lblCanvasAddress.setText("Physical Address");

		fieldsChanged = false;
		enableFields();

	}

	private void enableFields() {

		// disable all fields

		//disable pharmacy fields
		txtStockCenterName.setEnabled(false);
		rdBtnPreferredNo.setEnabled(false);
		rdBtnPreferredYes.setEnabled(false);

		//disable Facility Details fields
		txtPharmacyName.setEnabled(false);
		txtStreetAdd.setEnabled(false);
		txtCity.setEnabled(false);
		txtTel.setEnabled(false);
		txtPharmacistName1.setEnabled(false);
		txtPharmacyAssistant.setEnabled(false);
		grpLabel.setEnabled(false);

		if(rdBtnAddStockCenter.getSelection()) {
			txtStockCenterName.setEnabled(true);
			btnSearch.setVisible(false);
		}
		else if(rdBtnUpdateStockCenter.getSelection()) {
			txtStockCenterName.setEnabled(true);
			btnSearch.setVisible(true);
			btnSearch.setEnabled(true);
		}
		else {
			txtPharmacyName.setEnabled(true);
			txtStreetAdd.setEnabled(true);
			txtCity.setEnabled(true);
			txtTel.setEnabled(true);
			txtPharmacistName1.setEnabled(true);
			txtPharmacyAssistant.setEnabled(true);
			grpLabel.setEnabled(true);
			btnSearch.setVisible(false);
		}

		//}
	}
	/**
	 * This method is called when the user pressed the "Save" button It calls
	 * the saveForm() method in AdministrationManager
	 */
	@Override
	protected void cmdSaveWidgetSelected() {

		if (fieldsOk()) {
			if(fieldsChanged) {
				Transaction tx = null;
				try {

					MessageBox mSave = new MessageBox(getShell(), SWT.ICON_QUESTION
							| SWT.YES | SWT.NO);

					if(currentScreenMode.equalsIgnoreCase(screenModes[0])) {
						mSave.setText("iDART: Add New Pharmacy?");
						mSave.setMessage("Are you sure you want to add Pharmacy '"
								+ txtStockCenterName.getText() + "'?");
					}
					else if(currentScreenMode.equalsIgnoreCase(screenModes[1])) {
						mSave.setText("iDART: Update Pharmacy?");
						mSave.setMessage("Are you sure you want to update the Pharmacy '"
								+ txtStockCenterName.getText() + "'?");
					}
					else if(currentScreenMode.equalsIgnoreCase(screenModes[2])) {
						mSave.setText("iDART: Update Facility Details?");
						mSave.setMessage("Are you sure you want to update the Facility Details?");
					}
					switch (mSave.open()) {

					case SWT.YES:
						tx = getHSession().beginTransaction();

						saveModeInformation();
						getHSession().flush();
						tx.commit();
						MessageBox confirmUpdate = new MessageBox(getShell(),
								SWT.OK | SWT.ICON_INFORMATION);
						confirmUpdate.setText("iDART: Database Updated");
						confirmUpdate.setMessage("The changes have been updated.");
						confirmUpdate.open();
						clearMode();
						break;
					case SWT.NO:
						break;

					}
				}
				// else, there was a problem saving the PharmacyDetails details to the
				// database
				catch (HibernateException he) {
					MessageBox confirmUpdate = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					confirmUpdate.setText("iDART: Database Error");
					confirmUpdate
					.setMessage("There was a problem saving the PharmacyDetails "
							+ "information to the database - sorry! \n\nPlease "
							+ "try again later.");
					confirmUpdate.open();
					if (tx != null) {
						tx.rollback();
					}
					getLog().error(
							"Hibernate Exception while adding/updating PharmacyDetails",
							he);
					cmdCancelWidgetSelected();
				}
			}
			else {
				MessageBox mSave = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_INFORMATION);
				mSave.setText("iDART: Changes Not Made");
				mSave.setMessage("No changes were made");
				mSave.open();

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

		MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);

		if(currentScreenMode.equalsIgnoreCase(screenModes[0])) {

			if ("".equalsIgnoreCase(txtStockCenterName.getText().trim()))  {

				missing.setText("iDART: Pharmacy name blank");
				missing
				.setMessage("A Pharmacy name has not been entered and cannot be blank. \n\n" +
				"Please enter a Pharmacy name");
				missing.open();
				txtStockCenterName.setFocus();
				return false;
			}

			if ((AdministrationManager.getStockCenter(getHSession(), txtStockCenterName
					.getText().trim()) != null)) {

				missing.setText("Pharmacy already exists");
				missing
				.setMessage("A Pharmacy with name '"
						+ txtStockCenterName.getText()
						+ "' already exists in the database. Please choose a different name.");
				missing.open();
				txtStockCenterName.setFocus();
				return false;
			}

		}

		else if(currentScreenMode.equalsIgnoreCase(screenModes[1])) {

			if ("".equalsIgnoreCase(txtStockCenterName.getText().trim()))  {

				missing.setText("Pharmacy name blank");
				missing
				.setMessage("A Pharmacy name has not been entered and cannot be blank. \n\n" +
				"Please enter a Pharmacy name");
				missing.open();
				txtStockCenterName.setFocus();
				return false;
			}
			if (localStockCenter.getId() == 0)  {

				missing.setText("No Pharmacy selected");
				missing
				.setMessage("No Pharmacy has been selected to update. \n\n" +
				"Please select a Pharmacy using the search button");
				missing.open();
				txtStockCenterName.setFocus();
				return false;
			}
			StockCenter sc = (AdministrationManager.getStockCenter(getHSession(), txtStockCenterName
					.getText().trim()));
			if ( sc != null && sc.getId()!= localStockCenter.getId()) {

				missing.setText("Pharmacy already exists");
				missing
				.setMessage("A Pharmacy with name '"
						+ txtStockCenterName.getText()
						+ "' already exists in the database. Please choose a different name.");
				missing.open();
				txtStockCenterName.setFocus();
				return false;
			}
		}

		else if(currentScreenMode.equalsIgnoreCase(screenModes[2])) {
			if (txtPharmacyName.getText().trim().equals("")) {

				missing.setText("Missing Information");
				missing.setMessage("The facility name cannot be blank. ");
				missing.open();
				txtPharmacyName.setFocus();
				return false;
			}

			if (txtStreetAdd.getText().trim().equals("")) {

				missing.setText("Missing field");
				missing.setMessage("The street address cannot be blank.");
				missing.open();
				txtStreetAdd.setFocus();
				return false;
			}

			if (txtCity.getText().trim().equals("")) {

				missing.setText("Missing field");
				missing.setMessage("The city cannot be blank.");
				missing.open();
				txtStreetAdd.setFocus();
				return false;
			}

			if (txtTel.getText().trim().equals("")) {

				missing.setText("Missing field");
				missing.setMessage("The telephone number cannot be blank.");
				missing.open();
				txtTel.setFocus();
				return false;
			}

			if (txtPharmacistName1.getText().trim().equals("")) {

				missing.setText("Missing field");
				missing.setMessage("The pharmacist's name cannot be blank.");
				missing.open();
				txtTel.setFocus();
				return false;
			}
		}

		return true;

	}

	/**
	 * This method is called when the user pressed the "Clear" button It calls
	 * the clearForm() method
	 */
	@Override
	protected void cmdClearWidgetSelected() {
		clearMode();

	}

	/**
	 * This method is called when the user pressed the "Cancel" button It
	 * disposes the newPatientAdmin
	 */
	@Override
	protected void cmdCancelWidgetSelected() {
		cmdCloseSelected();
	}

	/**
	 * Sets the information from the text fields, into the global
	 * localFacilityDetails variable.
	 * 
	 */
	public void setLocalPharmacyDetails() {

		localFacilityDetails.setPharmacyName(txtPharmacyName.getText());
		localFacilityDetails.setPharmacist(txtPharmacistName1.getText());
		localFacilityDetails.setAssistantPharmacist(txtPharmacyAssistant.getText());
		localFacilityDetails.setModified(true);

		localFacilityDetails.setStreet(txtStreetAdd.getText());
		localFacilityDetails.setCity(txtCity.getText());
		localFacilityDetails.setContactNo(txtTel.getText());

	}

	private void loadPharmacyDetails() {

		// Set up the textboxes
		txtPharmacyName.setText(localFacilityDetails.getPharmacyName() == null ? "" : localFacilityDetails
				.getPharmacyName());
		txtPharmacistName1.setText(localFacilityDetails.getPharmacist() == null ? ""
				: localFacilityDetails.getPharmacist());

		txtCity.setText(localFacilityDetails.getCity() == null ? "" : localFacilityDetails
				.getCity());
		txtStreetAdd.setText(localFacilityDetails.getStreet() == null ? ""
				: localFacilityDetails.getStreet());
		txtTel.setText(localFacilityDetails.getContactNo() == null ? ""
				: localFacilityDetails.getContactNo());

		txtPharmacyAssistant.setText(localFacilityDetails.getAssistantPharmacist() == null ? "" :
			localFacilityDetails.getAssistantPharmacist());


		// Setup the label
		lblCanvasPharmacist.setText(txtPharmacistName1.getText());
		lblCanvasPharmName.setText(txtPharmacyName.getText());
		lblCanvasAddress.setText(txtStreetAdd.getText().concat(", ").concat(
				txtCity.getText()).concat(", ").concat(txtTel.getText()));

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
	 * Method enableFields.
	 * 
	 * @param enable
	 *            boolean
	 */
	@Override
	protected void enableFields(boolean enable) {
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
			setHSession(HibernateUtil.getNewSession());
		} catch (HibernateException he) {
			getLog().error(he);
		}
	}


	private void cmdSearchWidgetSelected() {
		closeAndReopenSession();

		Search stockCenterSearch = new Search(getHSession(), getShell(),
				CommonObjects.STOCK_CENTER);

		if (stockCenterSearch.getValueSelected() != null) {

			localStockCenter = AdministrationManager.getStockCenter(getHSession(),
					stockCenterSearch.getValueSelected()[0]);

			if(localStockCenter == null) {
				MessageBox mbox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
				mbox.setText("iDART: Unable to load Pharmacy");
				mbox.setMessage("Unable to load Pharmacy");
				mbox.open();

				return;

			}
			loadStockCenterDetails();
			fieldsChanged = false;
			rdBtnPreferredNo.setEnabled(true);
			rdBtnPreferredYes.setEnabled(true);


		} else {
			btnSearch.setEnabled(true);
		}

		txtStockCenterName.setFocus();
	}

	private void loadStockCenterDetails() {
		txtStockCenterName.setText(localStockCenter.getStockCenterName());
		if(localStockCenter.isPreferred()) {
			rdBtnPreferredYes.setSelection(true);
			rdBtnPreferredNo.setSelection(false);
		}
		else {
			rdBtnPreferredNo.setSelection(true);
			rdBtnPreferredYes.setSelection(false);
		}

		btnSearch.setEnabled(false);

	}

	private boolean saveModeInformation() {
		if (currentScreenMode.equalsIgnoreCase(screenModes[0])) {
			// create and save new Stock Center
			StockCenter stockCenter = new StockCenter();
			stockCenter.setStockCenterName(txtStockCenterName.getText().trim());
			stockCenter.setPreferred(rdBtnPreferredYes.getSelection());
			AdministrationManager.saveStockCenter(getHSession(), stockCenter);

		}
		else if (currentScreenMode.equalsIgnoreCase(screenModes[1])) {
			localStockCenter.setStockCenterName(txtStockCenterName.getText().trim());
			localStockCenter.setPreferred(rdBtnPreferredYes.getSelection());
			AdministrationManager.saveStockCenter(getHSession(), localStockCenter);
		}
		else if (currentScreenMode.equalsIgnoreCase(screenModes[2])) {
			localFacilityDetails.setAssistantPharmacist(txtPharmacyAssistant.getText().trim());
			localFacilityDetails.setCity(txtCity.getText().trim());
			localFacilityDetails.setContactNo(txtTel.getText().trim());
			localFacilityDetails.setPharmacist(txtPharmacistName1.getText().trim());
			localFacilityDetails.setPharmacyName(txtPharmacyName.getText().trim());
			localFacilityDetails.setStreet(txtStreetAdd.getText().trim());
			AdministrationManager.savePharmacyDetails(getHSession(), localFacilityDetails);
			LocalObjects.pharmacy = localFacilityDetails;
		}

		return true;

	}

	private void cmdEnterPressed() {
		if(!"".equalsIgnoreCase(txtStockCenterName.getText().trim())) {
			localStockCenter = AdministrationManager.getStockCenter(getHSession(),
					txtStockCenterName.getText());

			if(localStockCenter == null) {
				MessageBox mbox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
				mbox.setText("iDART: Pharmacy '" + txtStockCenterName.getText() + "' does not exist!");
				mbox.setMessage("Pharmacy '" + txtStockCenterName.getText() + "' does not exist!\n\n"
						+ "Please enter a valid Pharmacy name or use the search button to select a pharmacy to update.");
				mbox.open();

				return;

			}
			loadStockCenterDetails();
			fieldsChanged = false;
			rdBtnPreferredNo.setEnabled(true);
			rdBtnPreferredYes.setEnabled(true);

		}

	}

}
