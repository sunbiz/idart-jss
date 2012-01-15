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

package org.celllife.idart.gui.deletions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringTokenizer;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.StockManager;
import model.nonPersistent.StockLevelInfo;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.user.ConfirmWithPasswordDialogAdapter;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
public class DestroyStock extends GenericFormGui {

	private Button btnSearch;

	private CCombo cmbPharmacy;

	private Table tblStock;

	private Text txtDrugName;

	private Text txtPacksInStock;

	private Text txtPackSize;

	private Text txtReasonForDisposal;

	private Text txtDrugForm;

	private Drug localDrug;

	private StockCenter localStockCenter;

	private TableEditor editor;

	/**
	 * Constructor for DestroyStock.
	 * 
	 * @param parent
	 *            Shell
	 */
	public DestroyStock(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
	}

	/**
	 * This method initializes newBatchInfo
	 */
	@Override
	protected void createShell() {
		buildShell("Destroy Unusable Stock", new Rectangle(0, 0, 900,
				700));
	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Destroy Unusable Stock";
		iDartImage icoImage = iDartImage.DRUGALLERGY;
		buildCompHeader(headerTxt, icoImage);
		Label lblInstructions = new Label(getCompHeader(), SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(176, 55, 260, 20));
		lblInstructions.setText("All fields marked with * are compulsory");
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_ITALIC));
	}

	@Override
	protected void createContents() {
		createCompPharmacySelection();
		createGrpDrugInfo();
		createGrpStockInfo();
		btnSave.setEnabled(false);
		populatePharmacyCombo();
	}

	/**
	 * This method initializes compClinicSelection
	 */
	private void createCompPharmacySelection() {

		Composite compPharmacySelection = new Composite(getShell(), SWT.NONE);
		compPharmacySelection.setBounds(new Rectangle(274, 80, 344, 30));

		Label lblPharmacy = new Label(compPharmacySelection, SWT.NONE);
		lblPharmacy.setBounds(new Rectangle(0, 5, 149, 20));
		lblPharmacy.setText("Select a Pharmacy:");
		lblPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbPharmacy = new CCombo(compPharmacySelection, SWT.BORDER);
		cmbPharmacy.setBounds(new org.eclipse.swt.graphics.Rectangle(155, 5,
				180, 20));
		cmbPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbPharmacy.setEditable(false);
		cmbPharmacy.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbPharmacy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				StockCenter aPharmacy = AdministrationManager.getStockCenter(
						getHSession(), cmbPharmacy.getText());
				if (aPharmacy != null) {
					localStockCenter = aPharmacy;
				} else {
					getLog().warn(
							"Could not find a pharmacy with name "
							+ cmbPharmacy.getText());
				}
				cmdParameterSelectionChanged();

				// Check if there are any items in the table. If not, do not
				// allow user
				// to save
				if (tblStock.getItemCount() == 0) {
					btnSave.setEnabled(false);
					// enable search button
					btnSearch.setEnabled(true);
				} else {
					btnSave.setEnabled(true);
					btnSearch.setEnabled(false);
				}
			}
		});

	}

	private void cmdParameterSelectionChanged() {
		if (localStockCenter == null) {
			return;
		} else if (localDrug == null) {
			return;
		} else {
			tblStock.removeAll();
			loadStockDetails(localDrug, localStockCenter);
			txtDrugName.setEditable(false);
		}
	}

	/**
	 * This method initializes grpDrugInfo
	 */
	private void createGrpDrugInfo() {

		Group grpDrugInfo = new Group(getShell(), SWT.NONE);
		grpDrugInfo.setBounds(new Rectangle(165, 116, 565, 118));
		grpDrugInfo.setText("Drug Information");
		grpDrugInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblDrugName & txtDrugName
		Label lblDrugName = new Label(grpDrugInfo, SWT.NONE);
		lblDrugName.setBounds(new Rectangle(17, 24, 120, 20));
		lblDrugName.setText("Drug Name:");
		lblDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDrugName = new Text(grpDrugInfo, SWT.BORDER);
		txtDrugName.setBounds(new Rectangle(212, 25, 235, 20));
		txtDrugName.setEditable(false);
		txtDrugName.setEnabled(false);
		txtDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDrugName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdEnterPressedInDrugName();

				}
			}
		});

		btnSearch = new Button(grpDrugInfo, SWT.NONE);
		btnSearch.setBounds(new Rectangle(451, 18, 100, 30));
		btnSearch.setText("Drug Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdSearchSelected();
			}
		});
		btnSearch.setFocus();

		Label lblPacksInStock = new Label(grpDrugInfo, SWT.NONE);
		lblPacksInStock.setBounds(new Rectangle(18, 54, 160, 20));
		lblPacksInStock.setText("Total Packs in Stock:");
		lblPacksInStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPacksInStock = new Text(grpDrugInfo, SWT.BORDER);
		txtPacksInStock.setBounds(new Rectangle(212, 55, 335, 20));
		txtPacksInStock.setEditable(false);
		txtPacksInStock.setEnabled(false);
		txtPacksInStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblPackSize = new Label(grpDrugInfo, SWT.NONE);
		lblPackSize.setBounds(new Rectangle(18, 79, 160, 20));
		lblPackSize.setText("One Pack Contains:");
		lblPackSize.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackSize = new Text(grpDrugInfo, SWT.BORDER);
		txtPackSize.setBounds(new Rectangle(212, 80, 200, 20));
		txtPackSize.setEditable(false);
		txtPackSize.setEnabled(false);
		txtPackSize.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDrugForm = new Text(grpDrugInfo, SWT.NONE);
		txtDrugForm.setBounds(new Rectangle(422, 80, 127, 20));
		txtDrugForm.setEditable(false);
		txtDrugForm.setEnabled(false);
		txtDrugForm.setText("");
		txtDrugForm.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	/**
	 * This method initializes grpStockInfo
	 * 
	 */
	private void createGrpStockInfo() {

		Group grpBatchInfo = new Group(getShell(), SWT.NONE);
		grpBatchInfo.setText("Batch Information");
		grpBatchInfo.setBounds(new Rectangle(29, 245, 831, 368));
		grpBatchInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblBatchTableInfo = new Label(grpBatchInfo, SWT.CENTER
				| SWT.BORDER);
		lblBatchTableInfo.setBounds(new Rectangle(21, 18, 791, 60));
		lblBatchTableInfo.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblBatchTableInfo
		.setText("The amount received, dispensed, destroyed and in stock for each"
				+ " batch is shown in packs with the number of  pills in brackets  \n"
				+ "'10 (5)' means 10 packs and 5 loose pills, while '10' means 10 packs and no loose pills.\n" +
						"\nNote: this table does not show stock adjustments but if a stock take was done then the 'In Stock'" +
						"value will take this into consideration.");
		lblBatchTableInfo.setForeground(ResourceUtils.getColor(iDartColor.RED));

		createTblStock(grpBatchInfo);

		// lblReasonForDisposal & txtReasonForDisposal
		Label lblReasonForDisposal = new Label(grpBatchInfo, SWT.NONE);
		lblReasonForDisposal.setBounds(new Rectangle(140, 320, 170, 25));
		lblReasonForDisposal.setText("* Reason for Disposal: ");
		lblReasonForDisposal.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_12));

		txtReasonForDisposal = new Text(grpBatchInfo, SWT.BORDER);
		txtReasonForDisposal.setBounds(new Rectangle(320, 320, 335, 25));
		txtReasonForDisposal.setEditable(false);
		txtReasonForDisposal.setEnabled(false);
		txtReasonForDisposal.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_12));
		txtReasonForDisposal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					btnSave.setFocus();

				}
			}
		});

	}

	/**
	 * This method initializes tblStock
	 * 
	 */
	private void createTblStock(Group parentGrp) {

		tblStock = new Table(parentGrp, SWT.VIRTUAL | SWT.FULL_SELECTION);
		tblStock.setBounds(new Rectangle(21, 80, 791, 230));
		tblStock.setHeaderVisible(true);
		tblStock.setLinesVisible(true);
		tblStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn clmBatch = new TableColumn(tblStock, SWT.NONE);
		clmBatch.setText("Batch");
		clmBatch.setWidth(60);
		clmBatch.setResizable(true);

		TableColumn clmShelfNo = new TableColumn(tblStock, SWT.NONE);
		clmShelfNo.setText("Shelf");
		clmShelfNo.setWidth(50);
		clmShelfNo.setResizable(true);

		TableColumn clmManufacturer = new TableColumn(tblStock, SWT.NONE);
		clmManufacturer.setText("Manufacturer");
		clmManufacturer.setWidth(90);
		clmManufacturer.setResizable(true);

		TableColumn clmExpiry = new TableColumn(tblStock, SWT.NONE);
		clmExpiry.setText("Expiry Date");
		clmExpiry.setWidth(72);
		clmExpiry.setResizable(true);

		TableColumn clmUnitsReceived = new TableColumn(tblStock, SWT.NONE);
		clmUnitsReceived.setText("Received");
		clmUnitsReceived.setWidth(60);
		clmUnitsReceived.setResizable(true);

		TableColumn clmUnitsDispensed = new TableColumn(tblStock, SWT.NONE);
		clmUnitsDispensed.setText("Dispensed");
		clmUnitsDispensed.setWidth(70);
		clmUnitsDispensed.setResizable(true);

		TableColumn clmUnitsDestroyed = new TableColumn(tblStock, SWT.NONE);
		clmUnitsDestroyed.setText("Destroyed");
		clmUnitsDestroyed.setWidth(70);
		clmUnitsDestroyed.setResizable(true);

		TableColumn clmUnitsOnHand = new TableColumn(tblStock, SWT.NONE);
		clmUnitsOnHand.setText("In Stock");
		clmUnitsOnHand.setWidth(60);
		clmUnitsOnHand.setResizable(true);

		TableColumn clmUnitsToDispose = new TableColumn(tblStock, SWT.NONE);
		clmUnitsToDispose.setText("Packs to Destroy");
		clmUnitsToDispose.setWidth(104);
		clmUnitsToDispose.setResizable(true);

		TableColumn clmLooseUnitsToDispose = new TableColumn(tblStock, SWT.NONE);
		clmLooseUnitsToDispose.setText("Loose Pills to Destroy");
		clmLooseUnitsToDispose.setWidth(130);
		clmLooseUnitsToDispose.setResizable(true);

		TableColumn stockId = new TableColumn(tblStock, SWT.NONE);
		stockId.setWidth(0);

		editor = new TableEditor(tblStock);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		tblStock.addMouseListener(new MouseAdapter() {
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
				final TableItem item = tblStock.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblStock.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}

					}

					if (column == 8 || column == 9) {
						// Create the Text Object for your editor
						final Text text = new Text(tblStock, SWT.None);
						text.setForeground(item.getForeground());

						// Transfer any text from the cell to the text control
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));
						text.selectAll();
						text.setFocus();

						// Recalculate the minimum width for the editor
						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

						// Add a handler to transfer the text back to the cell
						// any time its modified
						final int col = column;
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								// Set the text of the editor back into the cell
								item.setText(col, text.getText());

							}
						});
					}
				}
			}
		});

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
	protected void cmdClearWidgetSelected() {
		clearFields();
		btnSearch.setEnabled(true);
	}

	private void clearFields() {
		txtDrugName.setText("");
		txtDrugName.setEnabled(true);
		txtPackSize.setText("");
		txtPackSize.setEnabled(false);
		txtDrugForm.setText("");
		txtPacksInStock.setText("");
		txtPacksInStock.setEnabled(false);
		txtReasonForDisposal.setText("");
		txtReasonForDisposal.setEnabled(false);
		txtReasonForDisposal.setEditable(false);
		btnSave.setEnabled(false);

		int items = tblStock.getItemCount();

		for (int i = 0; i < items; i++) {
			tblStock.remove(0);
		}
		tblStock.clearAll();

		// dispose of the table editor
		Control old2 = editor.getEditor();
		if (old2 != null) {
			old2.dispose();
		}
	}

	private void clearTable() {
		for (int i = 0; i < tblStock.getItemCount(); i++) {
			TableItem ti = tblStock.getItem(i);
			ti.setText(8, "0");
			ti.setText(9, "0");
		}
	}

	@Override
	protected void cmdCancelWidgetSelected() {
		cmdCloseSelected();
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		submitForm();
	}

	private void cmdSearchSelected() {

		Search drugSearch = new Search(getHSession(), getShell(),
				CommonObjects.DRUG, true);

		if (drugSearch.getValueSelected() != null) {
			localDrug = DrugManager.getDrug(getHSession(), drugSearch
					.getValueSelected()[0]);
			cmdParameterSelectionChanged();
			txtDrugName.setEditable(false);
			btnSearch.setEnabled(false);
			btnSave.setEnabled(true);
		}

		// if we've returned from the search GUI with the user having
		// pressed "cancel", enable the search button
		else

			if (!btnSearch.isDisposed()) {
				btnSearch.setEnabled(true);
				btnSave.setEnabled(false);
			}

		// Check if there are any items in the table. If not, do not allow user
		// to save
		if (tblStock.getItemCount() == 0) {
			btnSave.setEnabled(false);
			// enable search button
			btnSearch.setEnabled(true);
		} else {
			btnSave.setEnabled(true);
		}
	}

	/**
	 * Called when the user presses enter in the patient id field - searches for
	 * the patient
	 */
	private void cmdEnterPressedInDrugName() {
		localDrug = DrugManager.getDrug(getHSession(), txtDrugName.getText());

		if (localDrug == null) {
			MessageBox noDrug = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);

			noDrug.setText("Drug not in Database");
			noDrug.setMessage("There is no drug with this name ("
					+ txtDrugName.getText() + ") in the "
					+ cmbPharmacy.getText().trim() + " database.");
			noDrug.open();
			txtDrugName.setFocus();
			txtDrugName.setText("");

		}

		else {

			cmdParameterSelectionChanged();
			txtDrugName.setEditable(false);
		}

	}

	private void populatePharmacyCombo() {

		CommonObjects.populateStockCenters(getHSession(), cmbPharmacy);

		localStockCenter = AdministrationManager.getStockCenter(getHSession(),
				cmbPharmacy.getText());

	}

	private void loadStockDetails(Drug drug, StockCenter stockCenter) {

		final Drug finalDrug = drug;
		final StockCenter finalStockCenter = stockCenter;
		Runnable longJob = new Runnable() {
			@Override
			public void run() {
				int[] unitsInStock = new int[2];

				txtDrugName.setText(finalDrug.getName());
				txtDrugName.setEnabled(false);
				txtDrugName.setEnabled(false);

				txtReasonForDisposal.setEnabled(true);
				txtReasonForDisposal.setEditable(true);

				unitsInStock = StockManager.getTotalStockLevelsForDrug(
						getHSession(), finalDrug, finalStockCenter);

				txtPacksInStock.setText(unitsInStock[0]
				                                     + (unitsInStock[1] != 0 ? " (" + unitsInStock[1] + ")"
				                                    		 : ""));
				txtPackSize.setText("" + finalDrug.getPackSize());
				txtDrugForm.setText(finalDrug.getForm().getFormLanguage1());

				loadStockTable(finalDrug, finalStockCenter);
			}
		};
		BusyIndicator.showWhile(getShell().getDisplay(), longJob);

	}

	/**
	 * Method loadStockTable.
	 * 
	 * @param d
	 *            Drug
	 * @param c
	 *            Clinic
	 */
	private void loadStockTable(Drug d, StockCenter stockCenter) {
		List<Stock> stockList = StockManager.getCurrentStockForDrug(
				getHSession(), d, stockCenter);

		for (int i = 0; i < stockList.size(); i++) {
			Stock stock = stockList.get(i);
			StockLevel currentLevel = StockManager.getCurrentStockLevel(
					getHSession(), stock);

			if(currentLevel != null) {


				int[] stockRem = { currentLevel.getFullContainersRemaining(),
						currentLevel.getLoosePillsRemaining() };

				if (!((stockRem[0] == 0) && (stockRem[1] == 0))) {

					TableItem ti = new TableItem(tblStock, SWT.NONE);
					String[] tableEntry = new String[11];
					// tableEntry[0] = pd.getDrug().getBarcode();
					tableEntry[0] = stock.getBatchNumber();
					tableEntry[1] = stock.getShelfNumber();
					tableEntry[2] = stock.getManufacturer();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
					tableEntry[3] = sdf.format(stock.getExpiryDate());
					tableEntry[4] = new Integer(stock.getUnitsReceived())
					.toString();

					StockLevelInfo info = StockManager.getStockLevelInfo(getHSession(), stock);
					
					tableEntry[5] = info.getDispensedString();

					tableEntry[6] = info.getDestroyedString();

					tableEntry[7] = stockRem[0]
					                         + (stockRem[1] > 0 ? " (" + stockRem[1] + " )" : "");
					tableEntry[8] = "0";
					tableEntry[9] = "0";
					tableEntry[10] = String.valueOf(stock.getId());

					ti.setText(tableEntry);

					// set the background for the editable fields to
					// ResourceUtils.getColor(iDartColor.GRAY)
					ti.setBackground(8, ResourceUtils.getColor(iDartColor.GRAY));
					ti.setBackground(9, ResourceUtils.getColor(iDartColor.GRAY));
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
		boolean changesMade = false;
		for (int i = 0; i < tblStock.getItemCount(); i++) {

			TableItem ti = tblStock.getItem(i);
			try {

				if ((Integer.parseInt(ti.getText(8)) < 0)
						|| (Integer.parseInt(ti.getText(9)) < 0)) {
					MessageBox invalidStock = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					invalidStock.setText("Invalid quantity to destroy");
					invalidStock
					.setMessage("Cannot destroy negative number of stock."
							+ "Please enter a valid quantity.");
					invalidStock.open();
					clearTable();
					return false;
				}

				// Check if value to destroy is greater than available
				// amount
				else if ((Integer.parseInt(ti.getText(8)) > getPacks(ti
						.getText(7)))
						|| ((Integer.parseInt(ti.getText(8)) == getPacks(ti
								.getText(7))) && (Integer.parseInt(ti
										.getText(9)) > getPills(ti.getText(7))))) {
					MessageBox invalidStock = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					invalidStock.setText("Invalid quantity to destroy");
					invalidStock
					.setMessage("Cannot destroy more pills than the available amount");
					invalidStock.open();
					clearTable();
					return false;
				}

				else if ((Integer.parseInt(ti.getText(9)) > Integer
						.parseInt(txtPackSize.getText()))) {
					MessageBox invalidStock = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					invalidStock.setText("Invalid quantity to destroy");
					invalidStock
					.setMessage("The number of loose pills selected is more than the pack size. Please choose to "
							+ "destroy a pack instead.");
					invalidStock.open();
					clearTable();
					return false;
				} else if (txtReasonForDisposal.getText().trim().equals("")) {
					MessageBox noDisposalReason = new MessageBox(getShell(),
							SWT.OK | SWT.ICON_ERROR);
					noDisposalReason.setText("No Reason For Disposal");
					noDisposalReason
					.setMessage("Cannot destroy stock because a reason for disposal has not been entered."
							+ "Please enter a reason for disposal.");
					noDisposalReason.open();
					txtReasonForDisposal.setFocus();
					return false;
				} else if ((Integer.parseInt(ti.getText(8)) != 0)
						|| (Integer.parseInt(ti.getText(9)) != 0)) {

					changesMade = true;

				}

			} catch (NumberFormatException ne) {
				MessageBox invalidStock = new MessageBox(getShell(), SWT.OK
						| SWT.ICON_ERROR);
				invalidStock.setText("Invalid quantity to destroy");
				invalidStock
				.setMessage("Please enter a valid quantity to destroy for batch "
						+ ti.getText(0));
				invalidStock.open();
				clearTable();
				return false;
			}

		}

		// Check if changes were made
		if (!changesMade) {
			MessageBox msg = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
			msg.setText("No Quantities Entered.");
			msg
			.setMessage("You have not entered quantities that need to be destroyed (neither full units nor loose pills)"
					+ "\n\nPlease enter the quantity of drugs to destroy.");
			msg.open();
			return false;

		}
		return true;
	}

	/**
	 * Method submitForm.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean submitForm() {

		if (fieldsOk()) {
			ConfirmWithPasswordDialogAdapter passwordDialog = new ConfirmWithPasswordDialogAdapter(
					getShell(), getHSession());
			passwordDialog.setMessage("Please enter your Password");
			// if password verified
			String messg = passwordDialog.open();
			if (messg.equalsIgnoreCase("verified")) {
				Transaction tx = null;
				try {
					tx = getHSession().beginTransaction();

					for (TableItem ti : tblStock.getItems()) {
						if (!(ti.getText(8).trim().equals("0") && ti.getText(9)
								.trim().equals("0"))) {
							Integer packs = Integer.valueOf(ti.getText(8).trim());
							Integer pills = Integer.valueOf(ti.getText(9).trim());
							Integer stockId = Integer.valueOf(ti.getText(10).trim());
							
							StockManager.reduceStock(getHSession(), Integer
									.parseInt(txtPackSize.getText()),
									txtReasonForDisposal.getText(), packs,
									pills, stockId);
						}
					}
					
					getHSession().flush();
					tx.commit();

					MessageBox saved = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					saved.setText("Database Updated");
					saved
					.setMessage("The pills have been successfully destroyed.");
					saved.open();
					cmdCancelWidgetSelected();

				} catch (HibernateException he) {
					MessageBox saved = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_ERROR);
					saved.setText("Database Error");
					saved
					.setMessage("Could not destroy all pills. Please try again");
					saved.open();

					if (tx != null) {
						tx.rollback();
					}
					getLog().error(he);

				}

			}

		}
		return false;

	}

	/**
	 * Method getPills.
	 * 
	 * @param total
	 *            String
	 * @return int
	 */
	private int getPills(String total) {
		StringTokenizer st = new StringTokenizer(total, " (,)");
		// bypass packs
		st.nextToken();

		if (st.hasMoreTokens())
			return Integer.parseInt(st.nextToken());

		return 0;
	}

	/**
	 * Method getPacks.
	 * 
	 * @param total
	 *            String
	 * @return int
	 */
	private int getPacks(String total) {
		StringTokenizer st = new StringTokenizer(total, " (,)");

		return Integer.parseInt(st.nextToken());

	}

	@Override
	protected void clearForm() {
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

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
