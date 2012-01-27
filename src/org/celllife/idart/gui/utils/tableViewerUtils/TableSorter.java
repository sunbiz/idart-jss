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
package org.celllife.idart.gui.utils.tableViewerUtils;

import java.util.Date;

import model.manager.PatientManager;
import model.nonPersistent.PackagesWithSelection;

import org.celllife.idart.database.hibernate.Appointment;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Utility Class to sort our TableViewer
 * 
 * @author Rashid
 * 
 */
public class TableSorter extends ViewerSorter {

	private static final int ASCENDING = 0;

	private static final int DESCENDING = 1;

	private int column;

	private int direction;

	/**
	 * Does the sort. If it's a different column from the previous sort, do an
	 * ascending sort. If it's the same column as the last sort, toggle the sort
	 * direction
	 * 
	 * @param columnToSort
	 */
	public void doSort(int columnToSort) {

		if (columnToSort == this.column) {
			// Same column as last sort. Toggle the direction
			direction = 1 - direction;
		} else {
			// New column. Do an ascending sort
			this.column = columnToSort;
			direction = ASCENDING;
		}
	}

	/**
	 * Compares the object sorting
	 * 
	 * Note this method can be customized but for the tiem being, I assume that
	 * all data is in String form
	 * 
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int rc = 0;
		PackagesWithSelection p1 = (PackagesWithSelection) e1;
		PackagesWithSelection p2 = (PackagesWithSelection) e2;

		switch (column) {
		case 0:
			rc = p1.isSelected() ? 1 : -1;
			break;
		case 1:
			rc = p1.getPackages().getPrescription().getPatient().getPatientId()
					.compareToIgnoreCase(
							p2.getPackages().getPrescription().getPatient()
									.getPatientId());
			break;
		case 2:
			rc = (p1.getPackages().getPrescription().getPatient().getLastname()
					+ ", " + p1.getPackages().getPrescription().getPatient()
					.getFirstNames()).compareToIgnoreCase(p2.getPackages()
					.getPrescription().getPatient().getLastname()
					+ ", "
					+ p2.getPackages().getPrescription().getPatient()
							.getFirstNames());
			break;
		case 3:
			rc = p1.getPackages().getPackDate().after(
					p2.getPackages().getPackDate()) ? 1 : -1;

			break;

		case 4:
			rc = p1.getPackages().getDateLeft().after(
					p2.getPackages().getDateLeft()) ? 1 : -1;

			break;

		case 5:
			Appointment app1 = PatientManager
					.getLatestActiveAppointmentForPatient(p1.getPackages()
							.getPrescription().getPatient());
			Appointment app2 = PatientManager
					.getLatestActiveAppointmentForPatient(p2.getPackages()
							.getPrescription().getPatient());

			if (app1 != null && app2 != null) {
				Date theDateExpected1 = app1.getAppointmentDate();
				Date theDateExpected2 = app2.getAppointmentDate();

				rc = theDateExpected1.after(theDateExpected2) ? 1 : -1;
			} else if (app1 != null)
				rc = 1;
			else if (app2 != null)
				rc = -1;

			break;
		}

		// If descending order, flip the direction
		if (direction == DESCENDING)
			rc = -rc;

		return rc;
	}
}
