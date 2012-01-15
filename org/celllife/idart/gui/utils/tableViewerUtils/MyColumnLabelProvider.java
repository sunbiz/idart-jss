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
 */package org.celllife.idart.gui.utils.tableViewerUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.manager.PatientManager;
import model.nonPersistent.PackagesWithSelection;

import org.celllife.idart.database.hibernate.Appointment;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MyColumnLabelProvider {

	private String[] headers;

	private SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");

	/**
	 * Constructor
	 * 
	 * @param headers
	 */
	public MyColumnLabelProvider(String[] headers) {
		super();
		this.headers = headers;
		
	}

	public void createColumns(final TableViewer viewer) {

		// Column 0: Selected
		int i = 0;
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		//column.getColumn().setImage(image)
		
		column.getColumn().setWidth(60);
		column.getColumn().setText(headers[i]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new MyCheckBoxLabelProvider(viewer));
		column.setEditingSupport(new MyEditingSupport(viewer, i));
		column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((TableSorter) viewer.getSorter()).doSort(0);
				viewer.refresh();
			}
		});

		// Column 1: Patient Id
		i++;
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(130);
		column.getColumn().setText(headers[i]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((PackagesWithSelection) element).getPackages()
						.getPrescription().getPatient().getPatientId();
			}
		});
		column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((TableSorter) viewer.getSorter()).doSort(1);
				viewer.refresh();
			}
		});

		// Column 2: Patient Name
		i++;
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(230);
		column.getColumn().setText(headers[i]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((PackagesWithSelection) element).getPackages()
						.getPrescription().getPatient().getLastname()
						+ ", "
						+ ((PackagesWithSelection) element).getPackages()
								.getPrescription().getPatient().getFirstNames();
			}
		});
		column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((TableSorter) viewer.getSorter()).doSort(2);
				viewer.refresh();
			}
		});

		// Column 3: Date Packaged
		i++;
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(105);
		column.getColumn().setText(headers[i]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return sdf.format(((PackagesWithSelection) element)
						.getPackages().getPackDate());
			}
		});
		column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((TableSorter) viewer.getSorter()).doSort(3);
				viewer.refresh();
			}
		});

		// Column 4: Date Left
		i++;
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(105);
		column.getColumn().setText(headers[i]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return sdf.format(((PackagesWithSelection) element)
						.getPackages().getDateLeft());
			}
		});
		column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((TableSorter) viewer.getSorter()).doSort(4);
				viewer.refresh();
			}
		});

		// Column 5: Patient Expected on
		i++;
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(125);
		column.getColumn().setText(headers[i]);
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				Appointment app = PatientManager
						.getLatestActiveAppointmentForPatient(((PackagesWithSelection) element)
								.getPackages().getPrescription().getPatient());

				if (app != null) {
					Date theDateExpected = app.getAppointmentDate();
					return sdf.format(theDateExpected);
				} else
					return "Initial Pickup";
			}
		});
		column.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				((TableSorter) viewer.getSorter()).doSort(5);
				viewer.refresh();
			}
		});

	}
}