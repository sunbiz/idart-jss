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

package org.celllife.idart.gui.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.manager.AdherenceManager;
import model.manager.DrugManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.facade.PillCountFacade;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.platform.GenericGuiInterface;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.hibernate.Session;

/**
 * Table Composite
 * 
 */
public class PillCountTable implements GenericGuiInterface {

	private Logger log = Logger.getLogger(PillCountTable.class);

	private Group grpPillCount;

	private Table theTable;

	private Label lblHeading;

	private TableColumn clmDrugName;

	private TableColumn clmInHand;

	private TableColumn clmPercent;

	private PillCountFacade pillFacade;

	private TableEditor editorTbl;

	private Packages previousPack;

	private Shell parent;

	Session hSession;

	private MouseListener mousAdapNoPillUseWaring;

	private iDARTChangeListener changeListener;

	private MouseListener tableEditorMouseAdaptor;

	/**
	 * Constructor for PillCountTable.
	 * 
	 * @param parent
	 *            Composite
	 * @param style
	 *            int
	 * @param session
	 *            Session
	 * @param bounds
	 *            Rectangle
	 */
	public PillCountTable(Composite parent, int style, Session session,
			Rectangle bounds) {
		log.info("Creating PillCountTable");

		this.parent = parent.getShell();
		grpPillCount = new Group(parent, style);
		grpPillCount.setBounds(bounds);
		hSession = session;
		pillFacade = new PillCountFacade(session);

		grpPillCount.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpPillCount.setText("Pill Count:");

		Rectangle boundsForTable = new Rectangle(10, 20, bounds.width - 20,
				bounds.height - 30);
		createTable(boundsForTable);

	}

	/**
	 * This constructor does not create a new group as you are already using a
	 * group as a parent
	 * 
	 * @param parent
	 * @param style
	 * @param session
	 * @param boundsForTable
	 *            Rectangle
	 */
	public PillCountTable(Group parent, int style, Session session,
			Rectangle boundsForTable) {

		grpPillCount = parent;

		hSession = session;
		pillFacade = new PillCountFacade(session);

		createTable(boundsForTable);

	}

	/**
	 * Method createTable.
	 * 
	 * @param bounds
	 *            Rectangle
	 */
	public void createTable(Rectangle bounds) {
		theTable = new Table(grpPillCount, SWT.FULL_SELECTION | SWT.BORDER);
		theTable.setHeaderVisible(true);
		theTable.setLinesVisible(true);
		theTable.setBounds(bounds);

		theTable.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		clmDrugName = new TableColumn(theTable, SWT.NONE);
		clmDrugName.setText("Drug Name");
		clmDrugName.setWidth(((bounds.width - 20) * 65 / 100) - 10);
		clmDrugName.setResizable(true);

		clmInHand = new TableColumn(theTable, SWT.NONE);
		clmInHand.setText("Accum");
		clmInHand.setWidth(((bounds.width - 20) * 20 / 100) + 5);
		clmInHand.setResizable(true);

		clmPercent = new TableColumn(theTable, SWT.NONE);
		clmPercent.setText("%");
		clmPercent.setWidth(((bounds.width - 20) * 15 / 100) + 15);
		clmPercent.setResizable(true);

		lblHeading = new Label(grpPillCount, SWT.NONE);
		lblHeading.setBounds(new Rectangle(10, 3, bounds.width - 13, 13));
		lblHeading.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_BOLD));
		lblHeading.setForeground(ResourceUtils.getColor(iDartColor.BLACK));
		lblHeading.setBackground(null);
		lblHeading.setVisible(false);
	}

	/**
	 * Method setHeading.
	 * 
	 * @param heading
	 *            String
	 */
	public void setHeading(String heading) {
		if (heading.length() > 0) {
			lblHeading.setText(heading);
			lblHeading.setVisible(true);
		} else {
			lblHeading.setText(heading);
			lblHeading.setVisible(false);
		}
	}

	/**
	 * Method setBounds.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param width
	 *            int
	 * @param height
	 *            int
	 */
	public void setBounds(int x, int y, int width, int height) {
		grpPillCount.setBounds(x, y, width, height);
	}

	public void clearTable() {
		theTable.clearAll();
		theTable.removeAll();
	}

	/**
	 * Fill the last package table with the details of the ARV drugs in the last
	 * package
	 * 
	 * @param previousPack
	 *            Packages
	 */
	public void populateLastPackageDetails(@SuppressWarnings("hiding")
	Packages previousPack,
			Date datePacked) {
		this.previousPack = previousPack;
		clearTable();
		disableIfUponFirstVisit();

		if (previousPack != null) {

			java.util.Set<PillCount> previousPackPillCounts = pillFacade
					.getPillCountsReturnedFromThisPackage(previousPack);

			java.util.List<PackagedDrugs> packagedDrugsList = previousPack
					.getPackagedDrugs();

			Iterator<PackagedDrugs> packDrugIt = packagedDrugsList.iterator();

			// keep track of all drugs already in the list
			java.util.List<Drug> usedDrugs = new ArrayList<Drug>();

			while (packDrugIt.hasNext()) {
				PackagedDrugs pd = packDrugIt.next();

				if (pd != null) {
					Drug d = pd.getStock().getDrug();

					if ((d.getSideTreatment() == 'F')
							&& (!usedDrugs.contains(d))) {
						TableItem ti = new TableItem(theTable, SWT.NONE);

						if (theTable.getBounds().width < 300)
							ti.setText(0, DrugManager
									.getShortGenericDrugName(d, true));
						else
							ti.setText(0, d.getName());

						// search for existing pillcounts

						Iterator<PillCount> previousPackPillCountsItr = previousPackPillCounts
								.iterator();

						while (previousPackPillCountsItr.hasNext()) {
							PillCount pc = previousPackPillCountsItr.next();
							if (pc.getDrug().equals(d)) {
								pc.setDateOfCount(new Date());
								ti.setData(pc);
								ti.setText(1, "" + pc.getAccum());
							}
						}

						if (ti.getData() == null)
						// didn't find a previous pillcount
						{
							ti.setData(new PillCount(-1, previousPack,
									datePacked, d));
						}
						usedDrugs.add(d);
					}

					else if ((d.getSideTreatment() == 'T')
							&& (!usedDrugs.contains(d))) {
						TableItem ti = new TableItem(theTable, SWT.NONE);
						ti.setText(0, d.getName());
						ti.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8_ITALIC));
						ti.setData(null);
						usedDrugs.add(d);
					}
				}
			}
		}
	}

	private void attachEditor() { // add a editor for the accum column

		// add a editor for the accum column
		editorTbl = new TableEditor(theTable);
		editorTbl.horizontalAlignment = SWT.LEFT;
		editorTbl.grabHorizontal = true;

		tableEditorMouseAdaptor = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editorTbl.getEditor();
				if (old != null)
					old.dispose();

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = theTable.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = theTable.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					if ((column == 1) && (item.getData() != null)) {
						// Create the Text object for our editor

						final Text text = new Text(theTable, SWT.NONE);
						text.setForeground(item.getForeground());
						text.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));

						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						editorTbl.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editorTbl.setEditor(text, item, column);

						final int col = column;
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								String oldValue = item.getText(col);
								item.setText(col, text.getText());

								// check user input
								if (!text.getText().trim().equals(oldValue)) {
									if (!updateAdherance(item, text.getText(),
											col)) {
										text.setText("");
									}
									fireChangeEvent(item.getData());
								}
							}
						});

						text.addFocusListener(new FocusListener() {
							@Override
							public void focusLost(FocusEvent event1) {

								theTable.setSelection(theTable.getItemCount());
								text.dispose();

							}

							@Override
							public void focusGained(FocusEvent event1) {

							}
						});
					}
				}
			}
		};

		theTable.addMouseListener(tableEditorMouseAdaptor);
	}

	/**
	 * Method updateAdherance.
	 * 
	 * @param item
	 *            TableItem
	 * @param text
	 *            String
	 * @param col
	 *            int
	 * @return boolean
	 */
	private boolean updateAdherance(final TableItem item, String text,
			final int col) {

		try {
			int accum = Integer.parseInt(text);
			return updateAdherence(item, accum, col);

		} catch (NumberFormatException e) {
			((PillCount) item.getData()).setAccum(-1);
			java.awt.Toolkit.getDefaultToolkit().beep();
			item.setText(col, "");
			((PillCount) item.getData()).setAccum(-1);
			item.setBackground(col + 1, ResourceUtils
					.getColor(iDartColor.LIST_BACKGROUND));
			return false;
		}

	}

	/**
	 * Method updateAdherence.
	 * 
	 * @param item
	 *            TableItem
	 * @param accum
	 *            Integer
	 * @param col
	 *            int
	 * @return boolean
	 */
	private boolean updateAdherence(final TableItem item, Integer accum,
			final int col) {
		if (accum > -1){
		int adherencePercent = AdherenceManager.getAdherencePercent(hSession,
				accum, ((PillCount) item.getData()));
		item.setText(col + 1, "" + adherencePercent);

		setColourForAdherencePercentCell(item, 2, adherencePercent);

			((PillCount) item.getData()).setAccum(accum);
		}
		return true;
	}

	/**
	 * Method getTable.
	 * 
	 * @return Table
	 */
	public Table getTable() {
		return theTable;
	}

	public void removeEditor() {
		if (editorTbl != null)
			editorTbl.dispose();
	}

	public void addPillCountWarning(final String message) {
		mousAdapNoPillUseWaring = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				MessageBox msg = new MessageBox(parent, SWT.DIALOG_TRIM);
				msg.setText("Pill Count Table Editor Warning");
				msg.setMessage(message);
				msg.open();
			}
		};
		theTable.addMouseListener(mousAdapNoPillUseWaring);
	}

	private void disableIfUponFirstVisit() {
		Date previousPackDate;
		long numOfDays = 0;

		removeTableAdaptor(tableEditorMouseAdaptor);
		removeTableAdaptor(mousAdapNoPillUseWaring);

		if (previousPack != null) {
			previousPackDate = previousPack.getPickupDate();
			numOfDays = iDARTUtil.getDaysBetween(previousPackDate, new Date());

			if (numOfDays <= 0) {
				addPillCountWarning("The pill count table has been disabled because the patient received their last package today."
						+ "\n\nIf you wish to record a pill count from the previous package please delete "
						+ "the most recent package for the patient, "
						+ "then record the pill count and re-dispense the package that was deleted.");
			} else {
				attachEditor();
			}
		} else {
			log.debug("Previous pack is null");
			addPillCountWarning("The pill count table has been disabled because the patient has "
					+ "not received any previous packages. \n\nYou will  be able to do "
					+ "a pill count for this patient when they receive their next package.");
		}
	}

	/**
	 * @param tableEditorMouseAdaptor2
	 */
	private void removeTableAdaptor(MouseListener tableEditorMouseAdaptor2) {
		if (tableEditorMouseAdaptor2 != null)
			theTable.removeMouseListener(tableEditorMouseAdaptor2);
	}

	/**
	 * Method getPillCounts.
	 * 
	 * @return Set<PillCount>
	 */
	public Set<PillCount> getPillCounts() {
		java.util.Set<PillCount> pcsToSave = new HashSet<PillCount>();
		if (previousPack == null)
			return pcsToSave;
		
		previousPack.getPillCounts().clear();
		for (int i = 0; i < theTable.getItemCount(); i++) {

			if (theTable.getItem(i).getData() != null) {
				PillCount pc = (PillCount) theTable.getItem(i).getData();
				if (pc.getAccum() != -1) {
					if (pc.getDateOfCount() == null) {
						pc.setDateOfCount(new Date());
					}
					pc.setPreviousPackage(previousPack);
					pcsToSave.add(pc);
				}
			}
		}
		previousPack.getPillCounts().addAll(pcsToSave);

		return previousPack.getPillCounts();
	}

	/**
	 * Method setColourForAdherencePercentCell.
	 * 
	 * @param item
	 *            TableItem
	 * @param cell
	 *            int
	 * @param adherencePercent
	 *            int
	 */
	private void setColourForAdherencePercentCell(TableItem item, int cell,
			int adherencePercent) {
		if (adherencePercent >= 95) {
			item.setBackground(cell, ResourceUtils.getColor(iDartColor.GREEN));
		} else if (adherencePercent >= 90) {
			item.setBackground(cell, ResourceUtils.getColor(iDartColor.YELLOW));
		} else {
			item.setBackground(cell, ResourceUtils.getColor(iDartColor.RED));

		}
	}

	/**
	 * Update the pillcounts given a new dateOfCount.
	 * 
	 * @param dispenseDate
	 */
	public void update(Date dateOfCount) {
		TableItem[] items = theTable.getItems();
		for (TableItem item : items) {
			PillCount pc = (PillCount) item.getData();
			if (pc != null) {
				pc.setDateOfCount(dateOfCount);
				updateAdherence(item, pc.getAccum(), 1);
			}
		}
	}

	/**
	 * Method addChangeListener.
	 * 
	 * @param listener
	 *            iDARTChangeListener
	 */
	public void addChangeListener(iDARTChangeListener listener) {
		this.changeListener = listener;
	}

	/**
	 * Method fireChangeEvent.
	 * 
	 * @param o
	 *            Object
	 */
	private void fireChangeEvent(Object o) {
		if (changeListener != null)
			changeListener.changed(o);
	}

	/**
	 * Method setPillCountGroupHeading.
	 * 
	 * @param txt
	 *            String
	 */
	public void setPillCountGroupHeading(String txt) {
		grpPillCount.setText(txt);
	}

	/**
	 * Method getHSession.
	 * 
	 * @return Session
	 */
	public Session getHSession() {
		return hSession;
	}

	/**
	 * Method setHSession.
	 * 
	 * @param session
	 *            Session
	 */
	public void setHSession(Session session) {
		hSession = session;
	}

}
