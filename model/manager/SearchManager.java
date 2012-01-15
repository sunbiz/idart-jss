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

package model.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import model.nonPersistent.PatientIdAndName;

import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.NationalClinics;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.StockTake;
import org.celllife.idart.gui.search.Search;
import org.celllife.idart.gui.search.SearchEntry;
import org.celllife.idart.gui.search.TableComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 */
public class SearchManager {
	private static TableComparator comparator;

	private static java.util.List<SearchEntry> listTableEntries;

	/**
	 * 
	 */
	public SearchManager() {
		super();
	}

	/**
	 * loads a list of the clinics onto the grid
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 *            Search
	 * @return List<Clinic>
	 * @throws HibernateException
	 */
	public static List<Clinic> loadClinics(Session sess, Search search)
	throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<Clinic> clinics = null;

		String itemText[];
		search.getTableColumn1().setText("Clinic");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("City");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Clinic...");

		clinics = AdministrationManager.getClinics(sess);

		TableItem[] t = new TableItem[clinics.size()];

		for (int i = 0; i < clinics.size(); i++) {
			Clinic c = clinics.get(i);

			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = c.getClinicName();
			itemText[1] = c.getNotes();
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}

		return clinics;
	}

	public static List<NationalClinics> loadNational(Session sess, Search search)
	throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<NationalClinics> clinics = null;

		String itemText[];
		search.getTableColumn1().setText("Facility Name");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("Province");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Clinic Details...");

		clinics = AdministrationManager.getClinicsDetails(sess);

		TableItem[] t = new TableItem[clinics.size()];

		for (int i = 0; i < clinics.size(); i++) {
			NationalClinics c = clinics.get(i);
			
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = c.getFacilityName();
			itemText[1] = c.getProvince();
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}

		return clinics;
	}

	
	/**
	 * * loads a list of the stcokCenters onto the grid
	 * 
	 * @param sess
	 * @param search
	 * @return
	 * @throws HibernateException
	 */
	public static List<StockCenter> loadStockCenters(Session sess, Search search)
	throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<StockCenter> stockCenters = null;

		String itemText[];
		search.getTableColumn1().setText("Pharmacy");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("Default Pharmacy");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Pharmacy...");

		stockCenters = AdministrationManager.getStockCenters(sess);

		TableItem[] t = new TableItem[stockCenters.size()];

		for (int i = 0; i < stockCenters.size(); i++) {
			StockCenter sc = stockCenters.get(i);

			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = sc.getStockCenterName();
			itemText[1] = sc.isPreferred() == true ? "Yes" : "No";
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}

		return stockCenters;
	}

	/**
	 * loads a list of doctors onto the grid
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 *            Search
	 * @return List<Doctor>
	 * @throws HibernateException
	 */
	public static List<Doctor> loadDoctors(Session sess, Search search)
	throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<Doctor> doctors = null;
		String itemText[];
		search.getTableColumn1().setText("Doctor's Name");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("Doctor's Status");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Doctor...");

		doctors = AdministrationManager.getAllDoctors(sess);

		TableItem[] t = new TableItem[doctors.size()];

		for (int i = 0; i < doctors.size(); i++) {
			Doctor theDoctor = doctors.get(i);
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = theDoctor.getFullname();
			itemText[1] = theDoctor.isActive() ? "Active" : "Inactive";
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}
		return doctors;
	}

	/**
	 * loads a list of patients onto the grid
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 *            Search
	 * @param includeInactive
	 *            boolean
	 * @param filterAwaitingPackage
	 *            boolean
	 * @return List<PatientIdAndName>
	 * @throws HibernateException
	 *//*
	*//**
	 * @param search
	 * @param includeInactive
	 *            - should we include inactive patients in the search?
	 * @return List<PatientIdAndName>
	 *//*
	public static List<PatientIdAndName> loadPatients(Session sess,
			Search search, boolean includeInactive,
			boolean filterAwaitingPackage) throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<PatientIdAndName> patients = null;
		String itemText[];
		search.getTableColumn1().setText("Patient No");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("Name");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Patient...");

		patients = getPatientIDsAndNames(sess, includeInactive,
				filterAwaitingPackage);

		TableItem[] t = new TableItem[patients.size()];

		for (int i = 0; i < patients.size(); i++) {
			PatientIdAndName idAndName = patients.get(i);
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = idAndName.getPatientId().toString();
			itemText[1] = idAndName.getNames();
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}

		comparator.setColumn(TableComparator.COL1_NAME);
		redrawTable();
		return patients;

	}*/

	/**
	 * Method loadStockTakes.
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 *            Search
	 * @return List<StockTake>
	 * @throws HibernateException
	 */
	public static List<StockTake> loadStockTakes(Session sess, Search search)
	throws HibernateException {
		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<StockTake> stockTake = null;
		String itemText[];
		search.getTableColumn1().setText("Stock Take Name");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});

		search.getTableColumn2().setText("End Date");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a StockTake...");
		stockTake = getStockTakes(sess);

		TableItem[] t = new TableItem[stockTake.size()];

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < stockTake.size(); i++) {
			StockTake stkTake = stockTake.get(i);
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = stkTake.getStockTakeNumber();
			itemText[1] = sdf.format(stkTake.getEndDate());
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}

		comparator.setColumn(TableComparator.COL1_NAME);

		redrawTable();
		return stockTake;
	}

	/**
	 * loads a list of regimens onto the grid
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 *            Search
	 * @return List<Object[]>
	 * @throws HibernateException
	 */
	/**
	 * @param search
	 * 
	 * @return List<Object[]>
	 */
	public static List<Object[]> loadRegimens(Session sess, Search search)
	throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<Object[]> regimens = null;
		String itemText[];
		search.getTableColumn1().setText("Drug Group Name");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});

		search.getTableColumn2().setText("Regimen");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Regimen...");
		regimens = AdministrationManager.getDrugGroupNamesAndRegs(sess);

		TableItem[] t = new TableItem[regimens.size()];

		for (int i = 0; i < regimens.size(); i++) {
			Object[] regName = regimens.get(i);
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = regName[0].toString();
			itemText[1] = regName[1].toString();
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
		}

		comparator.setColumn(TableComparator.COL1_NAME);

		redrawTable();
		return regimens;
	}

	/**
	 * loads a list of drugs onto the form
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 * @param includeSideTreatmentDrugs
	 *            boolean
	 * @param includeZeroDrugs
	 *            boolean
	 * @return List<Drug>
	 * @throws HibernateException
	 */
	public static List<Drug> loadDrugs(Session sess, Search search,
			boolean includeSideTreatmentDrugs, boolean includeZeroDrugs)
			throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<Drug> drugs = null;
		String itemText[];
		search.getTableColumn1().setText("Drug Name");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("Pack Size");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Drug...");

		if (includeZeroDrugs) {
			drugs = DrugManager.getAllDrugs(sess);
		} else {
			drugs = DrugManager.getDrugsListForStockTake(sess, false);
		}

		Collections.sort(drugs);

		Iterator<Drug> iter = new ArrayList<Drug>(drugs).iterator();
		TableItem[] t = new TableItem[drugs.size()];

		int i = 0;
		while (iter.hasNext()) {
			Drug drugList = iter.next();
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[2];
			itemText[0] = drugList.getName();
			itemText[1] = (Integer.valueOf(drugList.getPackSize())).toString();
			t[i].setText(itemText);
			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
			i++;
		}
		comparator.setColumn(TableComparator.COL1_NAME);
		redrawTable();
		return drugs;

	}

	/**
	 * loads a list of Stock into the table
	 * 
	 * @param sess
	 *            Session
	 * @param search
	 * @param onlyZeroBatches
	 *            boolean
	 * @param theDrug
	 *            Drug
	 * @return List<Stock>
	 * @throws HibernateException
	 */
	public static List<Stock> loadStock(Session sess, Search search,
			boolean onlyZeroBatches, Drug theDrug) throws HibernateException {

		listTableEntries = new ArrayList<SearchEntry>();
		comparator = new TableComparator();

		List<Stock> stock = null;
		String itemText[];
		search.getTableColumn1().setText("Batch Number");
		search.getTableColumn1().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColOneSelected();
			}
		});
		search.getTableColumn2().setText("Date Received");
		search.getTableColumn2().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				cmdColTwoSelected();
			}
		});

		search.getShell().setText("Select a Batch...");

		if (onlyZeroBatches) {
			stock = StockManager.getEmptyBatchesList(sess, theDrug);
		} else {
			stock = StockManager.getBatchesList(sess, theDrug);
		}

		Iterator<Stock> iter = new ArrayList<Stock>(stock).iterator();
		TableItem[] t = new TableItem[stock.size()];

		int i = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		while (iter.hasNext()) {
			Stock stockList = iter.next();
			t[i] = new TableItem(search.getTblSearch(), SWT.NONE);
			itemText = new String[3];
			itemText[0] = stockList.getBatchNumber();
			itemText[1] = (sdf.format((stockList.getDateReceived())));
			itemText[2] = String.valueOf(stockList.getId());
			t[i].setText(itemText);

			listTableEntries.add(new SearchEntry(itemText[0], itemText[1]));
			i++;
		}

		comparator.setColumn(TableComparator.COL1_NAME);
		redrawTable();
		return stock;

	}

	public static void redrawTable() {

		// Turn off drawing to avoid flicker
		Search.tblSearch.setRedraw(false);

		// We remove all the table entries, sort our
		// rows, then add the entries
		Search.tblSearch.removeAll();

		Collections.sort(listTableEntries, comparator);

		for (Iterator<SearchEntry> itr = listTableEntries.iterator(); itr
		.hasNext();) {
			SearchEntry theEntry = itr.next();
			TableItem item = new TableItem(Search.tblSearch, SWT.NONE);
			int c = 0;
			item.setText(c++, theEntry.getColumnOneName());
			item.setText(c++, theEntry.getColumnTwoName());
		}

		// Turn drawing back on
		Search.tblSearch.setRedraw(true);
	}

	private static void cmdColOneSelected() {

		comparator.setColumn(TableComparator.COL1_NAME);
		comparator.reverseDirection();
		redrawTable();
	}

	private static void cmdColTwoSelected() {

		comparator.setColumn(TableComparator.COL2_NAME);
		comparator.reverseDirection();
		redrawTable();
	}

	/**
	 * Method minimiseSearch.
	 * 
	 * @param t
	 *            Table
	 * @param searchString
	 *            String
	 * @param fullList
	 *            List<? extends Object>
	 * @param classid
	 *            int
	 */
	public static void minimiseSearch(Table t, String searchString,
			List<? extends Object> fullList, int classid) {
		t.removeAll();
		for (int i = 0; i < fullList.size(); i++) {
			int found1 = 0;
			int found2 = 0;

			switch (classid) {

			case CommonObjects.NATION:
				NationalClinics nClinic = (NationalClinics) fullList.get(i);
				found1 = nClinic.getFacilityName().toUpperCase().indexOf(
						searchString.toUpperCase());
				found2 = nClinic.getProvince().toUpperCase().indexOf(
						searchString.toUpperCase());
				if (found1 != -1 || found2 != -1) {
					TableItem tableItem = new TableItem(t, SWT.NONE);
					String[] newStrings = new String[2];
					newStrings[0] = nClinic.getFacilityName();
					newStrings[1]  = nClinic.getProvince();
					tableItem.setText(newStrings);
				}

				break;
			
			case CommonObjects.CLINIC:
				Clinic theClinic = (Clinic) fullList.get(i);
				found1 = theClinic.getClinicName().toUpperCase().indexOf(
						searchString.toUpperCase());
				if (found1 != -1) {
					TableItem tableItem = new TableItem(t, SWT.NONE);
					String[] newStrings = new String[1];
					newStrings[0] = theClinic.getClinicName();
					tableItem.setText(newStrings);
				}

				break;
			case CommonObjects.DOCTOR:
				Doctor theDoctor = (Doctor) fullList.get(i);

				found1 = theDoctor.getFullname().toUpperCase().indexOf(
						searchString.toUpperCase());

				String activity = theDoctor.isActive() ? "Active" : "Inactive";

				found2 = activity.toUpperCase().indexOf(
						searchString.toUpperCase());

				if (found1 != -1 || found2 != -1) {
					TableItem tableItem = new TableItem(t, SWT.NONE);
					String[] newStrings = new String[2];
					newStrings[0] = theDoctor.getFullname();
					newStrings[1] = theDoctor.isActive() ? "Active"
							: "Inactive";
					tableItem.setText(newStrings);
				}
				break;

			case CommonObjects.DRUG:
				Drug drug = (Drug) fullList.get(i);
				found1 = drug.getName().toUpperCase().indexOf(
						searchString.toUpperCase());
				found2 = (Integer.valueOf(drug.getPackSize())).toString()
				.toUpperCase().indexOf(searchString.toUpperCase());
				if (found1 != -1 || found2 != -1) {
					TableItem tableItem = new TableItem(t, SWT.NONE);
					String[] newStrings = new String[2];
					newStrings[0] = drug.getName();
					newStrings[1] = (new Integer(drug.getPackSize()))
					.toString();
					tableItem.setText(newStrings);
				}
				break;

			case CommonObjects.STOCK:
				Stock theStock = (Stock) fullList.get(i);
				found1 = theStock.getBatchNumber().toUpperCase().indexOf(
						searchString.toUpperCase());
				found2 = theStock.getBatchNumber().toUpperCase().indexOf(
						searchString.toUpperCase());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				if (found1 != -1 || found2 != -1) {
					TableItem tableItem = new TableItem(t, SWT.NONE);
					String[] newStrings = new String[2];
					newStrings[0] = theStock.getBatchNumber();
					newStrings[1] = sdf.format(theStock.getDateReceived());
					tableItem.setText(newStrings);
				}

				break;

			case CommonObjects.STOCK_TAKE:
				StockTake theStockTake = (StockTake) fullList.get(i);
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
				found1 = theStockTake.getStockTakeNumber().toUpperCase()
				.indexOf(searchString.toUpperCase());
				found2 = sdf1.format(theStockTake.getEndDate()).toUpperCase()
				.indexOf(searchString.toUpperCase());
				if (found1 != -1 || found2 != -1) {
					TableItem tableItem = new TableItem(t, SWT.NONE);
					String[] newStrings = new String[2];
					newStrings[0] = theStockTake.getStockTakeNumber();
					newStrings[1] = sdf1.format(theStockTake.getEndDate());
					tableItem.setText(newStrings);
				}

				break;
			}

		}

	}

	/**
	 * Method minimisePatientSearch.
	 * 
	 * @param t
	 *            Table
	 * @param searchString
	 *            String
	 *//*
	public static void minimisePatientSearch(Table t, String searchString) {

		TableItem[] tis = t.getItems();
		int counter = 0;

		for (int i = 0; i < tis.length; i++) {
			String a = tis[i].getText(0);
			String b = tis[i].getText(1);

			int c1 = a.indexOf(searchString);
			int c2 = b.indexOf(searchString);

			if ((c1 != -1) || (c2 != -1)) {
				// Found
				String[] result = new String[2];
				result[0] = a;
				result[1] = b;
				tis[counter].setText(result);
				counter++;
				log.info("Found: " + a + " " + b);
			} else {
				// Not Found
				String[] toit = new String[2];
				toit[0] = "";
				toit[1] = "";
				tis[counter].setText(toit);
			}
		}

		int outer = tis.length - 1;
		while (outer != counter) {
			t.remove(outer);
			outer--;

		}

	}*/

	/**
	 * @param sess
	 *            Session
	 * @param includeInactive
	 *            boolean
	 * @param filterPackageAwaiting
	 *            boolean
	 * @return a list of all patient ids and names including inactive patients
	 */
	public static List<PatientIdAndName> getPatientIDsAndNames(Session sess,
			boolean includeInactive, boolean filterPackageAwaiting) {
		List<PatientIdAndName> newList = new ArrayList<PatientIdAndName>();
		if (!filterPackageAwaiting) {
			List<PatientIdAndName> activePatients = getActivePatientIDsAndNames(sess);
			newList.addAll(activePatients);
			if (includeInactive) {
				List<PatientIdAndName> inactivePatients = getInactivePatientIDsAndNames(sess);
				newList.addAll(inactivePatients);
			}
		} else {
			// return only those patients with their package awaiting.
			List<PatientIdAndName> patientsWithPackageAwaiting = getPatientNameAndSurnameWithAwaitingPackages(sess);
			newList = patientsWithPackageAwaiting;
		}
		return newList;
	}
	
	public static List<PatientIdentifier> getPatientIdentifiers(Session session, String patientId,
			boolean includeInactivePatients)
			throws HibernateException {
		patientId = patientId == null ? "" : patientId.trim();
		
		String queryString = "select id from PatientIdentifier as id where " +
				"upper(id.value) like :patientId " +
				"or upper(id.patient.lastname) like :patientId " +
				"or upper(id.patient.firstNames) like :patientId";
		if (!includeInactivePatients) {
			queryString += " and id.patient.accountStatus = true";
		}
		queryString += " order by id.patient.lastname asc";
		
		Query query = session.createQuery(queryString)
				.setParameter("patientId", "%" + patientId.toUpperCase() + "%");
		
		@SuppressWarnings("unchecked")
		List<PatientIdentifier> list = query.list();
				
		return list;
	}
	
	public static List<PatientIdentifier> getPatientIdentifiersWithAwiatingPackages(Session session, String patientId)
			throws HibernateException {
		String queryString = "select distinct id "
			+ "from PatientIdentifier as id, Packages as pack "
			+ "where id.patient.id = pack.prescription.patient.id "
			+ "and id.value like :patientId "
			+ "and pack.pickupDate is null and pack.packDate != null "
			+ "and pack.packageReturned = false ";
		
		Query query = session.createQuery(queryString)
				.setParameter("patientId", "%" + patientId.toUpperCase() + "%");
		
		@SuppressWarnings("unchecked")
		List<PatientIdentifier> list = query.list();
				
		return list;
	}

	/**
	 * Method getPatientNameAndSurnameWithAwaitingPackages.
	 * 
	 * @param hSession
	 *            Session
	 * @return List<PatientIdAndName>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> getPatientNameAndSurnameWithAwaitingPackages(
			Session hSession) throws HibernateException {
		String hql = "select distinct pack.prescription.patient "
			+ "from Packages as pack "
			+ "where pack.pickupDate is null and pack.packDate != null and "
			+ "pack.packageReturned = false "
			+ "order by pack.prescription.patient.lastname ";

		List<Patient> result = hSession.createQuery(hql).setResultTransformer(
				Criteria.DISTINCT_ROOT_ENTITY).list();
		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();
		if (result != null) {
			Iterator<Patient> it = result.iterator();
			while (it.hasNext()) {
				Patient patient = it.next();
				returnList.add(new PatientIdAndName(patient.getId(),
						patient.getPatientId(), patient.getFirstNames()
						+ ", " + patient.getLastname()));
			}
			return returnList;
		} else
			return new ArrayList<PatientIdAndName>();
	}

	/**
	 * @param sess
	 *            Session
	 * @return a list of in active patient ids and names
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> getInactivePatientIDsAndNames(
			Session sess) throws HibernateException {

		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();
		List<Object[]> result = sess
		.createQuery(
				"select pat.id, pat.patientId, pat.firstNames, pat.lastname from "
				+ "Patient as pat where pat.accountStatus=false order by pat.clinic.clinicName, pat.patientId")
				.list();

		if (result != null) {
			for (Object[] obj : result) {

				returnList.add(new PatientIdAndName((Integer) obj[0], (String) obj[1],
						(String) obj[3] + ", " + (String) obj[2]));

			}

		}

		return returnList;

	}

	/**
	 * @param sess
	 *            Session
	 * @return a list of active patient ids and names
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> getActivePatientIDsAndNames(
			Session sess) throws HibernateException {
		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();
		List<Object[]> result = sess
		.createQuery(
				"select pat.id, pat.patientId, pat.firstNames, pat.lastname from "
				+ "Patient as pat where pat.accountStatus=true order by pat.clinic.clinicName, pat.patientId")
				.list();

		if (result != null) {
			for (Object[] obj : result) {
				returnList.add(new PatientIdAndName((Integer) obj[0], (String) obj[1],
						(String) obj[3] + ", " + (String) obj[2]));
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> getActivePatientWithValidPrescriptionIDsAndNames(
			Session sess) throws HibernateException {
		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();
		List<Object[]> result = sess
		.createQuery(
				"select distinct pat.id, pat.patientId, pat.firstNames, pat.lastname, pat.clinic.clinicName "
				+ "from Patient pat,  Prescription pre where pre.endDate is null "
				+ "and pat.id = pre.patient and pat.accountStatus = true order by "
				+ "pat.clinic.clinicName, pat.patientId")
				.list();

		if (result != null) {
			for (Object[] obj : result) {
				returnList.add(new PatientIdAndName((Integer)obj[0],(String) obj[1],
						(String) obj[3] + ", " + (String) obj[2]));
			}
		}

		return returnList;

	}

	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> findPatientsWithIdLike(Session sess,
			String patientid) {
		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();
		List<Object[]> results = sess.createQuery(
				"select pat.id, pat.patientId, pat.firstNames, pat.lastname from "
				+ "Patient as pat where UPPER(pat.patientId) like :id")
				.setString("id", "%" + patientid.toUpperCase() + "%").list();

		if (results != null) {
			for (Object[] obj : results) {
				returnList.add(new PatientIdAndName((Integer) obj[0], (String) obj[1],
						(String) obj[3] + ", " + (String) obj[2]));
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	public static List<PatientIdAndName> findActivePatientsWithValidPrescriptionsWithIdLike(
			Session sess, String patientid) {
		List<PatientIdAndName> returnList = new ArrayList<PatientIdAndName>();
		List<Object[]> results = sess.createQuery(
				"select distinct pat.id, pat.patientId, pat.firstNames, pat.lastname, pat.clinic.clinicName from "
				+ "Patient as pat, Prescription as pre where pre.endDate is null and UPPER(pat.patientId) like :id " +
				"and pat.id = pre.patient and pat.accountStatus = true order by " +
		"pat.clinic.clinicName, pat.patientId")
		.setString("id", "%" + patientid.toUpperCase() + "%").list();

		if (results != null) {
			for (Object[] obj : results) {
				returnList.add(new PatientIdAndName((Integer) obj[0], (String) obj[1],
						(String) obj[3] + ", " + (String) obj[2]));
			}
		}
		return returnList;

	}

	/**
	 * Method getStockTakes.
	 * 
	 * @param sess
	 *            Session
	 * @return List<StockTake>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<StockTake> getStockTakes(Session sess)
	throws HibernateException {
		List<StockTake> result = sess.createQuery(
		"select st from StockTake st where st.open = false").list();

		return result;
	}

}
