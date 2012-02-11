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

package org.celllife.idart.gui.reportParameters;

import model.manager.AdministrationManager;
import model.manager.StockManager;
import model.manager.reports.StockTakeReport;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 */
public class StockTakeReportGUI extends GenericReportGui {

	private Group grpClinicSelection;

	private CCombo cmbPharmacy;

	private Label lblStockTake;

	private Text txtStockTake;

	private Button btnSearch;

	private Group grpStockTakeSelection;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param activate
	 *            boolean
	 */
	public StockTakeReportGUI(Shell parent, boolean activate) {
		super(parent, REPORTTYPE_STOCK, activate);
	}

	/**
	 * This method initializes newMonthlyStockOverview
	 */
	@Override
	protected void createShell() {
		buildShell(REPORT_STOCK_TAKE, new Rectangle(170, 50, 700, 361));
		// create the composites
		createMyGroups();
	}

	private void createMyGroups() {
		createGrpClinicSelection();
		creategrpStockTakeSelection();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		iDartImage icoImage = iDartImage.REPORT_ACTIVEPATIENTS;
		buildCompdHeader(REPORT_STOCK_TAKE, icoImage);
	}

	/**
	 * This method initializes grpClinicSelection
	 * 
	 */
	private void createGrpClinicSelection() {

		grpClinicSelection = new Group(getShell(), SWT.NONE);
		grpClinicSelection.setText("");
		grpClinicSelection.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpClinicSelection.setBounds(new Rectangle(151, 83, 386, 66));

		Label lblPharmacy = new Label(grpClinicSelection, SWT.NONE);
		lblPharmacy.setBounds(new Rectangle(9, 25, 151, 20));
		lblPharmacy.setText("Select pharmacy:");
		lblPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbPharmacy = new CCombo(grpClinicSelection, SWT.BORDER);
		cmbPharmacy.setBounds(new Rectangle(169, 25, 176, 20));
		cmbPharmacy.setEditable(false);
		cmbPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbPharmacy.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		CommonObjects.populateStockCenters(getHSession(), cmbPharmacy);

	}

	/**
	 * This method initializes grpStockTakeSelection
	 * 
	 */
	private void creategrpStockTakeSelection() {

		grpStockTakeSelection = new Group(getShell(), SWT.NONE);
		grpStockTakeSelection.setText("Stock Take:");
		grpStockTakeSelection.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		grpStockTakeSelection.setBounds(new Rectangle(69, 180, 520, 70));
		grpStockTakeSelection.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		lblStockTake = new Label(grpStockTakeSelection, SWT.NONE);
		lblStockTake.setBounds(new Rectangle(9, 25, 101, 20));
		lblStockTake.setText("Select Stock Take:");
		lblStockTake.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtStockTake = new Text(grpStockTakeSelection, SWT.BORDER);
		txtStockTake.setBounds(new Rectangle(120, 25, 200, 20));
		txtStockTake.setEditable(false);
		txtStockTake.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnSearch = new Button(grpStockTakeSelection, SWT.NONE);
		btnSearch.setBounds(new Rectangle(330, 20, 150, 30));
		btnSearch.setText("Stock Take Search");
		btnSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSearch
		.setToolTipText("Press this button to search for an existing stock take.");

		btnSearch
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdStockTakeSearchWidgetSelected();
			}
		});

	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
	}

	@Override
	protected void cmdViewReportWidgetSelected() {

		StockCenter pharm = AdministrationManager.getStockCenter(getHSession(),
				cmbPharmacy.getText());

		if (cmbPharmacy.getText().equals("")) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Pharmacy Was Selected");
			missing
			.setMessage("No pharmacy was selected. Please select a pharmacy by looking through the list of available pharmacies.");
			missing.open();

		} else if (pharm == null) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("Pharmacy not found");
			missing
			.setMessage("There is no pharmacy called '"
					+ cmbPharmacy.getText()
					+ "' in the database. Please select a pharmacy by looking through the list of available pharmacies.");
			missing.open();

		}

		else if (txtStockTake.getText().equals("")) {

			MessageBox missing = new MessageBox(getShell(), SWT.ICON_ERROR
					| SWT.OK);
			missing.setText("No Stock Take Was Selected");
			missing
			.setMessage("No Stock Take was selected. Please select a Stock Take using the search button.");
			missing.open();

		}

		else {
			StockTakeReport report = new StockTakeReport(getShell(), pharm,
					txtStockTake.getText());
			viewReport(report);
		}

	}

	private void cmdStockTakeSearchWidgetSelected() {

		StockManager.deleteInvalidStockTakes(getHSession());
		Search stockTakeSearch = new Search(getHSession(), getShell(),
				CommonObjects.STOCK_TAKE);

		if (stockTakeSearch.getValueSelected() != null) {
			txtStockTake.setText(stockTakeSearch.getValueSelected()[0]);
		}

	}

	/**
	 * This method is called when the user presses "Close" button
	 * 
	 */
	@Override
	protected void cmdCloseWidgetSelected() {
		cmdCloseSelected();
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
