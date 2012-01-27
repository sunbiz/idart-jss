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

import model.nonPersistent.PackagesWithSelection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

public class MyEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	public MyEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		switch (column) {
		case 0:
			editor = new CheckboxCellEditor(null, SWT.CHECK);
			break;
		}
		this.column = column;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		PackagesWithSelection packs = (PackagesWithSelection) element;

		switch (this.column) {
		case 0:
			return packs.isSelected();
		default:
			break;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		PackagesWithSelection packs = (PackagesWithSelection)element;

		switch (this.column) {
		case 0:
			// Toggle the selected field
			packs.setSelected(!packs.isSelected());
			break;
		
		default:
			break;
		}
		getViewer().update(element, null);
	}

}