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

/**
 * created on 08/05/2007
 * @author Rashid
 * gui class for stock take
 *
 * 2 functions
 * 	1) print out a form of all the stock in the clinic
 * 	2) allows users to capture the stock date information.
 */

package org.celllife.idart.gui.stockOnHand;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.StockManager;
import model.nonPersistent.BatchDetail;
import model.nonPersistent.DrugDetail;
import model.nonPersistent.StockLevelInfo;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.stockOnHand.treeViewerDetails.StockTreeContentProvider;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class StockOnHandGui extends GenericOthersGui {

	private Composite cmpStock;

	private Label lblPharmacy;

	private Label lblSummary;

	private CCombo cmbPharmacy;

	private Button btnClose;

	private Button rdbtnShowBatches;

	private Button rdbtnHideBatches;

	private TreeViewer tblStockTable;

	private StockCenter localStockCenter;

	private final String initialStockCenter;

	private static String[] columnNames = { "Drug", "Date Rec", "Received",
		"Dispensed(-)", "Destroyed(-)", "Adjusted(+-)", "Returned(+)", "On Hand" };

	/**
	 * @param parent
	 */
	public StockOnHandGui(Shell parent, String StockCenter) {
		super(parent, HibernateUtil.getNewSession());
		initialStockCenter = StockCenter;
		activate();
		// drugList = DrugManager.getDrugsList(getHSession());
	}

	/**
	 * This method initialises getShell()
	 */
	@Override
	protected void createShell() {
		String shellTxt = "Stock on Hand";
		Rectangle bounds = new Rectangle(50, 50, 900, 700);

		buildShell(shellTxt, bounds);

		createGrpStockDetails();
		populateStockGUI();

	}

	/**
	 * This method initialises getCompHeader()
	 * 
	 */
	@Override
	protected void createCompHeader() {

		// getCompHeader()
		setCompHeader(new Composite(getShell(), SWT.NONE));
		getCompHeader().setBounds(new Rectangle(172, 0, 570, 50));

		// lblIcon
		lblIcon = new Label(getCompHeader(), SWT.NONE);
		lblIcon
		.setBounds(new org.eclipse.swt.graphics.Rectangle(50, 10, 50,
				43));
		lblIcon.setText("");
		lblIcon.setImage(ResourceUtils.getImage(iDartImage.PRESCRIPTIONNEW));

		// lblHeader
		lblHeader = new Label(getCompHeader(), SWT.CENTER | SWT.SHADOW_IN);
		lblHeader.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));

		lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		lblHeader.setBounds(new Rectangle(110, 20, 410, 30));
		lblHeader.setText("Stock on Hand");

	}

	/**
	 * This method initialises createGrpDrugInfo
	 * 
	 */
	private void createGrpStockDetails() {

		if (cmpStock != null) {
			cmpStock.dispose();
		}
		cmpStock = new Composite(getShell(), SWT.NONE );
		cmpStock.setBounds(new Rectangle(20, 20, 860, 600));
		cmpStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblSummary = new Label(cmpStock, SWT.NONE);
		lblSummary.setBounds(new Rectangle(250, 51, 400, 20));
		lblSummary.setText("This table shows you all stock on hand at "
				+ (new SimpleDateFormat("hh:mma")).format(new Date()) + " on "
				+ (new SimpleDateFormat("dd MMM yyyy").format(new Date())));
		lblSummary.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblPharmacy = new Label(cmpStock, SWT.NONE);
		lblPharmacy.setBounds(new Rectangle(250, 81, 149, 20));
		lblPharmacy.setText("Select a Pharmacy:");
		lblPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbPharmacy = new CCombo(cmpStock, SWT.BORDER);
		cmbPharmacy.setBounds(new Rectangle(405, 81, 180, 20));
		cmbPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbPharmacy.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbPharmacy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				localStockCenter = AdministrationManager.getStockCenter(
						getHSession(), cmbPharmacy.getText().trim());
				populateStockGUI();
			}
		});
		cmbPharmacy.setEditable(false);

		rdbtnShowBatches = new Button(cmpStock, SWT.RADIO);
		rdbtnShowBatches.setBounds(new Rectangle(300, 111, 120, 20));
		rdbtnShowBatches.setText("Show Batches");
		rdbtnShowBatches.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdbtnShowBatches.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				tblStockTable.expandAll();
			}
		});
		rdbtnShowBatches.setSelection(true);
		
		rdbtnHideBatches = new Button(cmpStock, SWT.RADIO);
		rdbtnHideBatches.setBounds(new Rectangle(430, 111, 120, 20));
		rdbtnHideBatches.setText("Hide Batches");
		rdbtnHideBatches.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		rdbtnHideBatches.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				tblStockTable.collapseAll();
			}
		});
		

		populatePharmacyCombo();
		localStockCenter = AdministrationManager.getStockCenter(getHSession(),
				cmbPharmacy.getText().trim());

		tblStockTable = new TreeViewer(cmpStock, SWT.VIRTUAL | SWT.BORDER
				| SWT.SINGLE | SWT.V_SCROLL);
		tblStockTable.setContentProvider(new StockTreeContentProvider());
		tblStockTable.getTree().setBounds(15, 150, 830, 420);
		tblStockTable.getTree().setHeaderVisible(true);
		tblStockTable.getTree().setLinesVisible(true);
		tblStockTable.getTree().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		createColumns(tblStockTable);
		
	}

	/**
	 * This method initialises compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnClose = new Button(getCompButtons(), SWT.NONE);
		btnClose.setText("Close");
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		// btnClose.setBounds(new Rectangle(300, 5, 100, 30));
		btnClose
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnClose
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseWidgetSelected();
			}
		});
	}

	private void populatePharmacyCombo() {

		CommonObjects.populateStockCenters(getHSession(), cmbPharmacy);
		cmbPharmacy.setEnabled(true);
		cmbPharmacy.setText(initialStockCenter);
	}

	private void populateStockGUI() {

		if (localStockCenter == null) {
			getLog().info(
			"Tried to retrieve stock list, but localPharmay is null");
			return;
		}

		final List<DrugDetail> dataList = new ArrayList<DrugDetail>();
		final List<Drug> drugList = DrugManager.getAllDrugs(getHSession());
		final String parmacy = cmbPharmacy.getText();

		try {
			new ProgressMonitorDialog(getShell()).run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Calculating stock values", drugList.size());
					for (Drug drug : drugList) { 
						if (monitor.isCanceled()){
							throw new OperationCanceledException("Calculation cancelled");
						}
						monitor.subTask("Calculating values for " + drug.getName());
						DrugDetail dd = StockManager.getDrugDetail(getHSession(), drug,
								AdministrationManager.getStockCenter(getHSession(),
										parmacy));

						dataList.add(dd); 
						monitor.worked(1);
					}
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			showMessage(MessageDialog.ERROR,
					Messages.getString("common.error"), //$NON-NLS-1$
					Messages.getString("stockOnHand.error")); //$NON-NLS-1$
		} catch (InterruptedException e) {
			showMessage(MessageDialog.WARNING,
					Messages.getString("common.warning"), //$NON-NLS-1$
					Messages.getString("stockOnHand.warning")); //$NON-NLS-1$
		}

		
		tblStockTable.setInput(dataList);

		tblStockTable.refresh();
		tblStockTable.expandAll();

	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	private void cmdCloseWidgetSelected() {
		closeShell(true);
	}

	public void createColumns(final TreeViewer viewer) {

		// Column 0: Drug
		TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText(columnNames[0]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof DrugDetail)
					return ((DrugDetail) element).getDrugDetails();
				else
					return ((BatchDetail) element).getBatchName();
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// column.setLabelProvider(new StockTreeLabelProvider());

		// Column 1: Date Received
		TreeViewerColumn column1 = new TreeViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setWidth(75);
		column1.getColumn().setText(columnNames[1]);
		column1.getColumn().setMoveable(true);
		column1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof DrugDetail)
					return "";
				else
					return new SimpleDateFormat("yyyy/MM/dd")
				.format(((BatchDetail) element).getDateReceived());
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// Column 2: Total Received
		TreeViewerColumn column2 = new TreeViewerColumn(viewer, SWT.NONE);
		column2.getColumn().setWidth(65);
		column2.getColumn().setText(columnNames[2]);
		column2.getColumn().setMoveable(true);
		column2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof DrugDetail)
					return String.valueOf(((DrugDetail) element)
							.getUnitsReceived());
				else
					return String.valueOf(((BatchDetail) element)
							.getUnitsReceived());
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// Column 3: Dispensed
		TreeViewerColumn column3 = new TreeViewerColumn(viewer, SWT.NONE);
		column3.getColumn().setWidth(75);
		column3.getColumn().setText(columnNames[3]);
		column3.getColumn().setMoveable(true);
		column3.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				StockLevelInfo info = null;
				if (element instanceof DrugDetail){
					info = ((DrugDetail) element).getStockLevelInfo();
				}
				else{
					info = ((BatchDetail) element).getStockLevelInfo();
				}
				return info.getDispensedString();
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// Column 4: Destroyed
		TreeViewerColumn column4 = new TreeViewerColumn(viewer, SWT.NONE);
		column4.getColumn().setWidth(75);
		column4.getColumn().setText(columnNames[4]);
		column4.getColumn().setMoveable(true);
		column4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				StockLevelInfo info = null;
				if (element instanceof DrugDetail){
					info = ((DrugDetail) element).getStockLevelInfo();
				}
				else{
					info = ((BatchDetail) element).getStockLevelInfo();
				}
				return info.getDestroyedString();
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// Column 5: Adjusted
		TreeViewerColumn column5 = new TreeViewerColumn(viewer, SWT.NONE);
		column5.getColumn().setWidth(75);
		column5.getColumn().setText(columnNames[5]);
		column5.getColumn().setMoveable(true);
		column5.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				StockLevelInfo info = null;
				if (element instanceof DrugDetail){
					info = ((DrugDetail) element).getStockLevelInfo();
				}
				else{
					info = ((BatchDetail) element).getStockLevelInfo();
				}
				return info.getAdjustedString();
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// Column 6: Returned
		TreeViewerColumn column6 = new TreeViewerColumn(viewer, SWT.NONE);
		column6.getColumn().setWidth(75);
		column6.getColumn().setText(columnNames[6]);
		column6.getColumn().setMoveable(true);
		column6.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				StockLevelInfo info = null;
				if (element instanceof DrugDetail){
					info = ((DrugDetail) element).getStockLevelInfo();
				}
				else{
					info = ((BatchDetail) element).getStockLevelInfo();
				}
				return info.getReturnedString();
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});

		// Column 7: On hand
		TreeViewerColumn column7 = new TreeViewerColumn(viewer, SWT.NONE);
		column7.getColumn().setWidth(65);
		column7.getColumn().setText(columnNames[7]);
		column7.getColumn().setMoveable(true);
		column7.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				StockLevelInfo info = null;
				if (element instanceof DrugDetail){
					info = ((DrugDetail) element).getStockLevelInfo();
				}
				else{
					info = ((BatchDetail) element).getStockLevelInfo();
				}
				return info.getOnhandString();
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof DrugDetail) {
					return ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD);
				}
				else return super.getFont(element);
			}
		});
	}
}
