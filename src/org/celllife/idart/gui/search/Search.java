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

package org.celllife.idart.gui.search;

import java.util.List;

import model.manager.SearchManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.welcome.GenericWelcome;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

/**
 */
public class Search extends GenericOthersGui {

	public static Table tblSearch;

	private TableColumn tableColumn1;

	private TableColumn tableColumn2;

	private String[] valueSelected;

	private Button btnClose;

	private Text searchBar;

	private List<? extends Object> searchList;

	private final int searchType;

	private String searchString;

	// Can be used to store any extra info in table
	private Object data;

	/**
	 * Constructor
	 * 
	 * @param hSession
	 *            Session
	 * @param localShell
	 *            Shell
	 * @param toDisplay
	 *            int
	 */
	public Search(Session hSession, Shell localShell, int toDisplay) {
		super(localShell, hSession);
		searchType = toDisplay;
		activate();
		retrieveSearchList(searchType, false);
		waitForDispose();
	}

	private void waitForDispose() {
		while (!getShell().isDisposed()) {
			try {
				if (!GenericWelcome.display.readAndDispatch()) {
					GenericWelcome.display.sleep();
				}
			} catch (Exception E) {/*
			 * do nothing cos the damn widget is already
			 * disposed.
			 */
			}
		}
	}

	/**
	 * Use this constructor where you need to specify whether the search
	 * includes doctors /patients marked as inactive
	 * 
	 * @param hSession
	 *            Session
	 * @param localShell
	 *            Shell
	 * @param toDisplay
	 *            int
	 * @param showInactive
	 *            boolean
	 */
	public Search(Session hSession, Shell localShell, int toDisplay,
			boolean showInactive) {
		super(localShell, hSession);

		searchType = toDisplay;
		activate();
		retrieveSearchList(searchType, showInactive);
		waitForDispose();
	}

	/**
	 * Use this constructor where you need to specify whether the search
	 * includes doctors /patients marked as inactive and parse in a string typed
	 * into the search
	 * 
	 * @param hSession
	 *            Session
	 * @param localShell
	 *            Shell
	 * @param toDisplay
	 *            int
	 * @param showInactive
	 *            boolean
	 * @param searchString
	 *            String
	 */
	public Search(Session hSession, Shell localShell, int toDisplay,
			boolean showInactive, String searchString) {
		super(localShell, hSession);

		this.searchString = searchString;
		searchType = toDisplay;
		activate();
		retrieveSearchList(searchType, showInactive);
		// user entered text before clicking on search
		// so we minimise the search
		if (!searchString.equals("")) {
			SearchManager.minimiseSearch(getTblSearch(), searchBar.getText()
					.trim(), searchList, searchType);
		}
		waitForDispose();
	}

	/**
	 * Use this constructor where you need to search for Stock of a specific
	 * given drug.
	 * 
	 * @param hSession
	 *            Session
	 * @param localShell
	 *            Shell
	 * @param showZeroBatches
	 *            boolean
	 * @param theDrug
	 *            Drug
	 */
	public Search(Session hSession, Shell localShell, boolean showZeroBatches,
			Drug theDrug) {
		super(localShell, hSession);

		searchType = CommonObjects.STOCK;
		activate();
		searchList = SearchManager.loadStock(getHSession(), this,
				showZeroBatches, theDrug);
		waitForDispose();
	}

	/**
	 * Method retrieveSearchList.
	 * 
	 * @param listType
	 *            int
	 * @param inactive
	 *            boolean
	 */
	private void retrieveSearchList(int listType, boolean inactive) {
		switch (listType) {
		case CommonObjects.NATION:
			searchList = SearchManager.loadNational(getHSession(), this);
			break;
		case CommonObjects.CLINIC:
			searchList = SearchManager.loadClinics(getHSession(), this);
			break;
		case CommonObjects.DOCTOR:
			searchList = SearchManager.loadDoctors(getHSession(), this);
			break;
		case CommonObjects.DRUG:
			searchList = SearchManager.loadDrugs(getHSession(), this, true,
					true);
			break;
		case CommonObjects.REGIMEN:
			searchList = SearchManager.loadRegimens(getHSession(), this);
			break;
		case CommonObjects.STOCK_TAKE:
			searchList = SearchManager.loadStockTakes(getHSession(), this);
			break;
		case CommonObjects.STOCK_CENTER:
			searchList = SearchManager.loadStockCenters(getHSession(), this);
			break;
		case CommonObjects.ATC:
			searchList = SearchManager.loadAtccodes(getHSession(), this);
			break;
		}
	}

	/**
	 * This method initializes tblSearch
	 */
	private void createTable() {
		tblSearch = new Table(getShell(), SWT.FULL_SELECTION);
		tblSearch.setBounds(new Rectangle(35, 20, 275, 250));
		tblSearch.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblSearch.setHeaderVisible(true);
		tblSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				tblSearchWidgetSelected();
			}
		});
		tableColumn1 = new TableColumn(tblSearch, SWT.BORDER);
		tableColumn1.setWidth(129);
		tableColumn2 = new TableColumn(tblSearch, SWT.BORDER);
		tableColumn2.setWidth(129);
		if (searchType == CommonObjects.DRUG) {
			tableColumn1.setWidth(195);
			tableColumn2.setWidth(65);
		}
	}

	/**
	 * This method initializes compButtons
	 */
	@Override
	protected void createCompButtons() {
		btnClose = new Button(getCompButtons(), SWT.NONE);
		btnClose.setText("Close");
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClose.setToolTipText("Press this button to close this screen."
				+ "\nThe information you've entered here will be lost.");
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				cmdCloseWidgetSelected();
			}
		});
	}

	private void tblSearchWidgetSelected() {
		try {
			TableItem t[] = tblSearch.getSelection();
			if (t == null || t.length <= 0)
				return;

			valueSelected = new String[2];
			valueSelected[0] = t[0].getText(0);
			valueSelected[1] = t[0].getText(1);
			data = t[0].getText(2);
			closeShell(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cmdCloseWidgetSelected() {
		valueSelected = null;
		closeShell(false);
	}

	/**
	 * @return Returns the tableColumn1.
	 */
	public TableColumn getTableColumn1() {
		return tableColumn1;
	}

	/**
	 * @return Returns the tableColumn2.
	 */
	public TableColumn getTableColumn2() {
		return tableColumn2;
	}

	/**
	 * @return Returns the tblSearch.
	 */
	public Table getTblSearch() {
		return tblSearch;
	}

	/**
	 * @return Returns the valueSelected.
	 */
	public String[] getValueSelected() {
		return valueSelected;
	}

	public Object getData() {
		return this.data;
	}

	@Override
	protected void createCompHeader() {
	}

	@Override
	protected void createCompOptions() {
		createTable();
		searchBar = new Text(getShell(), SWT.BORDER);
		searchBar.setBounds(new Rectangle(75, 290, 200, 20));
		searchBar.setFocus();
		searchBar.setText(null == searchString ? "" : searchString);
		searchBar.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// check for CTRL key pressed so that uses can copy
				// if (e.stateMask == SWT.CTRL)
				SearchManager.minimiseSearch(getTblSearch(), searchBar
						.getText().trim(), searchList, searchType);

				if ((e.character == SWT.CR)
						|| (e.character == (char) iDartProperties.intValueOfAlternativeBarcodeEndChar)) {

					TableItem[] tableitems = getTblSearch().getItems();
					for (int i = 0; i < tableitems.length; i++) {
						String tableItem_i = tableitems[i].getText();
						String searchtxt = searchBar.getText();

						// if (tableitems[i].getText().equalsIgnoreCase(
						// searchBar.getText())) {
						if (tableItem_i.equalsIgnoreCase(searchtxt)) {
							getTblSearch().setSelection(i);
							tblSearchWidgetSelected();
						}

					}

				}

			}
		});
	}

	@Override
	protected void createShell() {
		String shellTxt = "Search";
		Rectangle bounds = new Rectangle(100, 100, 350, 420);
		buildShell(shellTxt, bounds);
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
