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

package org.celllife.idart.gui.packaging;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import model.manager.DrugManager;
import model.manager.PackageManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.hibernate.Session;

/**
 */

/**
 * @author melissa
 * 
 */
public class BatchInformation extends GenericFormGui {

	private List<PackageDrugInfo> stockList;

	private TableColumn clmBatch;

	private TableColumn clmExpiry;

	private TableColumn clmShelfNo;

	private TableColumn clmLabelsToPrint;

	private TableColumn clmUnitsOnHand;

	private TableColumn clmQuantityToDispense;

	private TableColumn clmManufacturer;

	private Group grpDrugInfo; // @jve:decl-index=0:

	private Label lblDispensingInstructions1;

	private Label lblDispensingInstructions2;

	private Label lblDrugName;

	private Label lblPacksInStock;

	private Label lblPackSize; // @jve:decl-index=0:

	private final Packages currentPackage;

	private final Drug localDrug;

	List<Stock> batchList;

	private Table tblBatch;

	private Text txtDispensingInstructions1;

	private Text txtDispensingInstructions2;

	private Text txtDrugName;

	private Text txtPacksInStock;

	private Text txtPackSize;

	private Group grpBatchInfo;

	private Text txtDrugForm;

	private Label lblBatchTableInfo;

	private List<PackageDrugInfo> pdiList; // if there are already

	public boolean infoChanged = false;

	private StockCenter localStockCenter = null;

	private int totalDispensedQty = 0;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param ti
	 * @param pdiList
	 *            List<PackageDrugInfo>
	 * @param clinic
	 * @param patId
	 * @param patientName
	 *            String
	 * @param prescriptionId
	 * @param cNotes
	 *            String
	 * @param repeats
	 *            int
	 * @param weeksupply
	 *            int
	 * @param hSession
	 *            Session
	 */

	public BatchInformation(Session hSession, Shell parent,
			StockCenter stockCenter, Drug drug, Packages currentPackage,
			List<PackageDrugInfo> pdiList) {
		super(parent, hSession);
		this.pdiList = new ArrayList<PackageDrugInfo>(pdiList);
		this.currentPackage = currentPackage;
		this.localStockCenter = stockCenter;
		this.localDrug = drug;
		stockList = new ArrayList<PackageDrugInfo>();
		batchList = new ArrayList<Stock>();
		populateForm();
	}

	/**
	 * This method initializes getShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Batch Information";
		Rectangle bounds = new Rectangle(250, 100, 750, 656);
		buildShell(shellTxt, bounds);
		createGrpDrugInfo();
		createGrpBatchInfo();
	}

	/**
	 * This method initializes compHeader
	 */
	@Override
	protected void createCompHeader() {
		String headerTxt = "Batch Information";
		iDartImage icoImage = iDartImage.PRESCRIPTIONADDDRUG;
		buildCompHeader(headerTxt, icoImage);
	}

	/**
	 * This method initializes grpDrugInfo
	 */
	private void createGrpDrugInfo() {

		grpDrugInfo = new Group(getShell(), SWT.NONE);
		grpDrugInfo.setBounds(new Rectangle(76, 91, 565, 158));
		grpDrugInfo.setText("Drug Information");
		grpDrugInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// lblDispensingInstructions1 & txtDispensingInstructions1
		lblDispensingInstructions1 = new Label(grpDrugInfo, SWT.NONE);
		lblDispensingInstructions1.setBounds(new Rectangle(17, 24, 181, 20));
		lblDispensingInstructions1.setText("Dispensing Instructions (line 1):");
		lblDispensingInstructions1.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		txtDispensingInstructions1 = new Text(grpDrugInfo, SWT.BORDER);
		txtDispensingInstructions1.setBounds(new Rectangle(211, 25, 335, 20));
		txtDispensingInstructions1.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		// lblDispensingInstructions2 & txtDispensingInstructions2
		lblDispensingInstructions2 = new Label(grpDrugInfo, SWT.NONE);
		lblDispensingInstructions2.setBounds(new Rectangle(17, 49, 181, 20));
		lblDispensingInstructions2.setText("Dispensing Instructions (line 2):");
		lblDispensingInstructions2.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		txtDispensingInstructions2 = new Text(grpDrugInfo, SWT.BORDER);
		txtDispensingInstructions2.setBounds(new Rectangle(211, 50, 335, 20));
		txtDispensingInstructions2.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		// lblBarcode & txtBarcode
		// lblDrugName & txtDrugName
		lblDrugName = new Label(grpDrugInfo, SWT.NONE);
		lblDrugName.setBounds(new Rectangle(18, 74, 160, 20));
		lblDrugName.setText("Drug Name:");
		lblDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDrugName = new Text(grpDrugInfo, SWT.BORDER);
		txtDrugName.setBounds(new Rectangle(212, 75, 335, 20));
		txtDrugName.setEditable(false);
		txtDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblPacksInStock = new Label(grpDrugInfo, SWT.NONE);
		lblPacksInStock.setBounds(new Rectangle(18, 99, 160, 20));
		lblPacksInStock.setText("Total Packs in Stock:");
		lblPacksInStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPacksInStock = new Text(grpDrugInfo, SWT.BORDER);
		txtPacksInStock.setBounds(new Rectangle(212, 100, 335, 20));
		txtPacksInStock.setEditable(false);
		txtPacksInStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblPackSize = new Label(grpDrugInfo, SWT.NONE);
		lblPackSize.setBounds(new Rectangle(18, 124, 160, 20));
		lblPackSize.setText("One Pack Contains:");
		lblPackSize.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackSize = new Text(grpDrugInfo, SWT.BORDER);
		txtPackSize.setBounds(new Rectangle(212, 125, 200, 20));
		txtPackSize.setEditable(false);
		txtPackSize.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDrugForm = new Text(grpDrugInfo, SWT.BORDER);
		txtDrugForm.setBounds(new Rectangle(422, 125, 127, 20));
		txtDrugForm.setEditable(false);
		txtDrugForm.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	/**
	 * This method initializes grpBatchInfo
	 * 
	 */
	private void createGrpBatchInfo() {

		grpBatchInfo = new Group(getShell(), SWT.NONE);
		grpBatchInfo.setText("Batch Information");
		grpBatchInfo.setBounds(new Rectangle(50, 263, 642, 295));
		grpBatchInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		createTblBatch();

		lblBatchTableInfo = new Label(grpBatchInfo, SWT.CENTER | SWT.BORDER);
		lblBatchTableInfo.setBounds(new Rectangle(63, 16, 503, 58));
		lblBatchTableInfo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblBatchTableInfo
		.setText("Please note that the 'Qty to Dispense' column is "
				+ "measured in INDIVIDUAL units. \nFor example, if one pack "
				+ "contains 60 tablets, and \nyou want to dispense 1 pack, you must "
				+ "type 60 into the 'Qty to Dispense' column");
		lblBatchTableInfo.setForeground(ResourceUtils.getColor(iDartColor.RED));
	}

	/**
	 * This method initializes tblBatch
	 * 
	 */
	private void createTblBatch() {

		tblBatch = new Table(grpBatchInfo, SWT.VIRTUAL | SWT.FULL_SELECTION);
		tblBatch.setBounds(new Rectangle(20, 90, 599, 160));
		tblBatch.setHeaderVisible(true);
		tblBatch.setLinesVisible(true);
		tblBatch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		clmBatch = new TableColumn(tblBatch, SWT.NONE);
		clmBatch.setText("Batch No");

		clmBatch.setWidth(60);
		clmBatch.setResizable(true);

		clmExpiry = new TableColumn(tblBatch, SWT.NONE);
		clmExpiry.setText("Expiry Date");
		clmExpiry.setWidth(70);
		clmExpiry.setResizable(true);

		clmUnitsOnHand = new TableColumn(tblBatch, SWT.NONE);
		clmUnitsOnHand.setText("Packs in Stock");
		clmUnitsOnHand.setWidth(85);
		clmUnitsOnHand.setResizable(true);

		clmShelfNo = new TableColumn(tblBatch, SWT.NONE);
		clmShelfNo.setText("Shelf No");
		clmShelfNo.setWidth(55);
		clmShelfNo.setResizable(true);

		clmQuantityToDispense = new TableColumn(tblBatch, SWT.NONE);
		clmQuantityToDispense.setText("Qty to Dispense");
		clmQuantityToDispense.setWidth(95);
		clmQuantityToDispense.setResizable(true);

		clmLabelsToPrint = new TableColumn(tblBatch, SWT.NONE);
		clmLabelsToPrint.setText("Labels to Print");
		clmLabelsToPrint.setWidth(95);
		clmLabelsToPrint.setResizable(true);

		clmManufacturer = new TableColumn(tblBatch, SWT.NONE);
		clmManufacturer.setText("Manufacturer");
		clmManufacturer.setWidth(100);
		clmManufacturer.setResizable(true);
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
		for (int i = 0; i < tblBatch.getItemCount(); i++) {
			TableItem ti = tblBatch.getItem(i);
			ti.setText(4, "0");
			ti.setText(5, "0");
		}
	}

	@Override
	protected void cmdCancelWidgetSelected() {
		infoChanged = false;
		closeShell(false);
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		infoChanged = true;
		// addToStockList();
		if (addToStockList()) {
			pdiList.addAll(getStockList());
			closeShell(false);
		}
	}

	private void getBatches() {

		int[] currentStockLevels = new int[2];

		tblBatch.removeAll();

		try {

			batchList = StockManager.getCurrentStockForDrug(getHSession(),
					localDrug, localStockCenter);

			getLog().info("Populating " + batchList.size() + " batches");

			for (Stock thisStock : batchList) {

				StockLevel currentLevel = StockManager.getCurrentStockLevel(getHSession(), thisStock);
				if (currentLevel == null){
					continue;
				}
				currentStockLevels[0] = currentLevel.getFullContainersRemaining();
				currentStockLevels[1] = currentLevel.getLoosePillsRemaining();

				if (!((currentStockLevels[0] == 0) && (currentStockLevels[1] == 0))) {

					final TableItem ti = new TableItem(tblBatch, SWT.NONE);
					ti.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

					SimpleDateFormat df = new SimpleDateFormat("MM/yyyy");
					ti.setText(0, thisStock.getBatchNumber());
					ti.setText(6, (thisStock.getManufacturer() == null ? ""
							: thisStock.getManufacturer()));
					ti.setText(1, df.format(thisStock.getExpiryDate()));
					ti.setText(2, currentStockLevels[0]
					                                 + (currentStockLevels[1] != 0 ? " ("
					                                		 + currentStockLevels[1] + " loose)" : ""));
					ti.setText(3, thisStock.getShelfNumber());

					String myString[] = { "0", "0" };
					if (pdiList != null && pdiList.size() > 0) {
						myString = getPreviousDispensedQuantity(thisStock
								.getId());
					}

					ti.setText(4, myString[0]);
					ti
					.setBackground(4, ResourceUtils
							.getColor(iDartColor.GRAY));
					ti.setText(5, myString[1]);
					ti
					.setBackground(5, ResourceUtils
							.getColor(iDartColor.GRAY));
					ti.setText(7, String.valueOf(thisStock.getId()));
					ti.setData(thisStock);
				} else {
					getLog().warn("Tried to populate batch table with empty batch");
				}
			}
			attachTableEditor();
		}

		catch (HibernateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			batchList = null;
		}

	}

	private void attachTableEditor() {
		final TableEditor editor = new TableEditor(tblBatch);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		tblBatch.addMouseListener(new MouseAdapter() {
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
				final TableItem item = tblBatch.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblBatch.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}
					// Column 6 holds dropdowns, add if click in
					// column 5 or 6
					if (column == 5) {
						// Create the dropdown and add data to it
						final Text txtLabel = new Text(tblBatch, SWT.NONE);
						// Select the previously selected item from
						// the cell
						txtLabel.setText(item.getText(5));
						txtLabel.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						// Compute the width for the editor
						// Also, compute the column width, so that
						// the dropdown fits
						editor.minimumWidth = txtLabel.computeSize(95,
								SWT.DEFAULT).x;
						tblBatch.getColumn(5).setWidth(editor.minimumWidth);
						// Set the focus on the dropdown and set
						// into the editor
						txtLabel.setFocus();
						txtLabel.selectAll();
						editor.setEditor(txtLabel, item, 5);
						// Add a listener to set the selected item
						// back into the cell
						final int col = 5;
						txtLabel.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								// Set the text of the
								// editor's control back
								// into the cell
								item.setText(col, txtLabel.getText());

							}
						});
						item.setText(col, txtLabel.getText());
					}
					if (column == 4) {
						// Create the Text object for our editor
						final Text text = new Text(tblBatch, SWT.NONE);
						text.setForeground(item.getForeground());
						text.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						// Transfer any text from the cell to the
						// Text control,
						// set the color to match this row, select
						// the text,
						// and set focus to the control
						text.setText(item.getText(4));
						text.selectAll();
						text.setFocus();
						// Recalculate the minimum width for the
						// editor
						editor.minimumWidth = text.getBounds().width;
						// Set the control into the editor
						editor.setEditor(text, item, 4);
						// Add a handler to transfer the text back
						// to the cell
						// any time it's modified
						final int col = 4;
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								// Set the text of the
								// editor's control back
								// into the cell
								item.setText(col, text.getText());
								if (text.getText().trim().equals("0")
										|| text.getText().trim().equals("")) {
									item.setText(col + 1, "0");
								} else {
									// set the number of labels
									try {
										int units = Integer.parseInt(text
												.getText().trim());
										int numlabels = (int) Math
										.ceil(((double) units / localDrug
												.getPackSize()));
										item.setText(col + 1, Integer
												.toString(numlabels));
									} catch (NumberFormatException e) {
										getLog()
										.debug(
										"Error parsing string to integer.");
									}
								}
							}
						});
					}
				}
			}
		});
	}

	/**
	 * Method getPreviousDispensedQuantity.
	 * 
	 * @param theStockId
	 *            int
	 * @return String[]
	 */
	private String[] getPreviousDispensedQuantity(int theStockId) {
		String[] myString = new String[2];
		myString[0] = "0";
		myString[1] = "0";
		// loop thru the allPackagedDrugsList, looking for this batchno
		for (int i = 0; i < pdiList.size(); i++) {
			PackageDrugInfo pdi = pdiList.get(i);

			if (pdi.getStockId() == theStockId) {

				myString[0] = "" + pdi.getDispensedQty();
				myString[1] = "" + pdi.getNumberOfLabels();
			}

		}
		return myString;
	}

	/**
	 * Populate the form based on the row selected
	 */
	private void populateForm() {
		txtDrugName.setText(localDrug.getName());

		txtDispensingInstructions1.setText(localDrug
				.getDispensingInstructions1());
		txtDispensingInstructions2.setText(localDrug
				.getDispensingInstructions2());
		txtPackSize.setText("" + localDrug.getPackSize());
		txtDrugForm.setText(localDrug.getForm().getFormLanguage1());

		int[] currentLevelForDrug = StockManager.getTotalStockLevelsForDrug(
				getHSession(), localDrug, localStockCenter);
		txtPacksInStock.setText(currentLevelForDrug[0]
		                                            + (currentLevelForDrug[1] != 0 ? " (" + currentLevelForDrug[1]
		                                                                                                        + ")" : ""));
		getBatches();
		prepopulateQuantities();
	}

	/**
	 * Adds all the drugs that have quantities dispensed, to the list of drug
	 * batches
	 * 
	 * @return boolean
	 */
	private boolean addToStockList() {
		boolean addSuccessful = true;
		stockList.clear();
		int index = 0;
		boolean firstPdi = true;
		// Continue allowing user to enter the quantiy until a valid value is
		// entered
		try {
			for (int i = 0; i < tblBatch.getItemCount(); i++) {
				index++;
				TableItem ti = tblBatch.getItem(i);
				// if an amount has been dispensed from this particular
				// batch
				if (Integer.parseInt(ti.getText(4)) < 0)
					throw new NumberFormatException();
				else if (Integer.parseInt(ti.getText(4)) != 0) {
					// valid quantity was entered
					String strAvailable = ti.getText(2);
					int pillsAvailable = 0;
					if (strAvailable.contains("loose")) {

						StringTokenizer st = new StringTokenizer(strAvailable);
						pillsAvailable = Integer.parseInt(st.nextToken());

						String loosePart = st.nextToken();
						loosePart = loosePart.substring(1);

						pillsAvailable = pillsAvailable
						* Integer.parseInt(txtPackSize.getText())
						+ Integer.parseInt(loosePart);

					}

					else {
						pillsAvailable = Integer
						.parseInt(txtPackSize.getText())
						* Integer.parseInt(ti.getText(2));

					}
					if (Integer.parseInt(ti.getText(4)) > pillsAvailable) {
						MessageBox noStock = new MessageBox(getShell(), SWT.OK
								| SWT.ICON_ERROR);
						noStock.setText("No Stock from this Batch");
						noStock
						.setMessage("There is not enough stock available from batch number '"
								+ ti.getText(0)
								+ "'. There are "
								+ ti.getText(2)
								+ " units on hand. Please choose stock from a different batch.");
						noStock.open();
					} else {
						PrescribedDrugs pds = DrugManager.getPrescribedDrug(
								getHSession(), DrugManager.getDrug(
										getHSession(), txtDrugName.getText()),
										currentPackage.getPrescription());
						DecimalFormat df = new DecimalFormat();
						df.setDecimalSeparatorAlwaysShown(false);
						String amtPerTimeString = df
						.format(pds.getAmtPerTime());
						Stock stock = (Stock) ti.getData();
						Patient pat = currentPackage.getPrescription()
						.getPatient();
						totalDispensedQty += Integer.parseInt(ti.getText(4));
						PackageDrugInfo pdi = new PackageDrugInfo(
								amtPerTimeString,
								stock.getBatchNumber(),
								pat.getCurrentClinic().getClinicName(),
								Integer.parseInt(ti.getText(4)),
								localDrug.getForm().getFormLanguage1(),
								localDrug.getForm().getFormLanguage2(),
								localDrug.getForm().getFormLanguage3(),
								txtDrugName.getText(),
								stock.getExpiryDate(),
								pat.getCurrentClinic().getNotes(),
								pat.getPatientId(),
								pat.getFirstNames(),
								pat.getLastname(),
								txtDispensingInstructions1.getText(),
								txtDispensingInstructions2.getText(),
								stock.getId(),
								pds.getTimesPerDay(),
								Integer.parseInt(ti.getText(5)),
								localDrug.getSideTreatment() == 'T' ? true
										: false,
										LocalObjects.getUser(getHSession()),
										new Date(),
										currentPackage.getPrescription().getPackages()
										.size()+1,
										currentPackage.getWeekssupply(),
										null,
										PackageManager
										.getQuantityDispensedForLabel(
												currentPackage
												.getAccumulatedDrugs(),
												Integer.parseInt(ti.getText(4)),
												stock.getDrug().getName(),
												stock.getDrug().getPackSize(),
												false, true),
												PackageManager
												.getQuantityDispensedForLabel(
														currentPackage
														.getAccumulatedDrugs(),
														Integer.parseInt(ti.getText(4)),
														stock.getDrug().getName(),
														Integer.parseInt(ti.getText(4)),
														false, true),
														PackageManager.getQuantityDispensedForLabel(
																currentPackage.getAccumulatedDrugs(),
																Integer.parseInt(ti.getText(4)), stock
																.getDrug().getName(), stock
																.getDrug().getPackSize(), true, true),
																currentPackage.getPrescription().getDuration()
																, "", currentPackage
										.getPackageId());

						pdi.setFirstBatchInPrintJob(firstPdi);
						firstPdi = false;
						stockList.add(pdi);
						addSuccessful = true;
					}
				}

			}// end for loop thru tableitems

		}// end try block
		catch (NumberFormatException ne) {
			MessageBox invalidQuantity = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_ERROR);
			invalidQuantity.setText("Invalid Quantity");
			invalidQuantity
			.setMessage("Please specify a valid quantity to dispense");
			invalidQuantity.open();
			TableItem ti = tblBatch.getItem(index - 1);
			ti.setText(4, "0");
			ti.setText(5, "0");
			addSuccessful = false;
			// getBatches();
		} catch (Exception e) {
			e.printStackTrace();
			addSuccessful = false;
		}
		return addSuccessful;
	}

	/**
	 * @return Returns the stockList.
	 */
	public java.util.List<PackageDrugInfo> getStockList() {
		return stockList;
	}

	/**
	 * @param stockList
	 *            The stockList to set.
	 */
	public void setStockList(java.util.List<PackageDrugInfo> stockList) {
		this.stockList = stockList;
	}

	/**
	 * Populate table with entries for the existing batches of this drug
	 */
	public void prepopulateQuantities() {

		Iterator<PackageDrugInfo> it = pdiList.iterator();
		List<PackageDrugInfo> pdiListRemoves = new ArrayList<PackageDrugInfo>();
		while (it.hasNext()) {
			PackageDrugInfo pdi = it.next();
			txtDispensingInstructions1.setText(pdi.getSpecialInstructions1()
					.equals("") ? "" : pdi.getSpecialInstructions1());
			txtDispensingInstructions2.setText(pdi.getSpecialInstructions2()
					.equals("") ? "" : pdi.getSpecialInstructions2());
			String stockId = (new Integer(pdi.getStockId())).toString();
			if (pdi.getDrugName().equals(txtDrugName.getText())) {
				pdiListRemoves.add(pdi);
				for (int i = 0; i < tblBatch.getItemCount(); i++) {
					TableItem ti = tblBatch.getItem(i);
					if (ti.getText(7).equals(stockId)) {
						ti.setText(4, (new Integer(pdi.getDispensedQty()))
								.toString());
						ti.setText(5, (new Integer(pdi.getNumberOfLabels()))
								.toString());
					}
				}
			}
		}
		pdiList.removeAll(pdiListRemoves);
	}

	/**
	 * Method getPdiList.
	 * 
	 * @return List<PackageDrugInfo>
	 */
	public List<PackageDrugInfo> getPdiList() {
		return pdiList;
	}

	/**
	 * Method setPdiList.
	 * 
	 * @param pdiList
	 *            List<PackageDrugInfo>
	 */
	public void setPdiList(List<PackageDrugInfo> pdiList) {
		this.pdiList = pdiList;
	}

	@Override
	protected void clearForm() {
	}

	@Override
	protected void createContents() {
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
	 * Method fieldsOk.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean fieldsOk() {
		return false;
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
		setLog(Logger.getLogger(this.getClass()));
	}

}
