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

import java.util.ArrayList;
import java.util.List;

import model.nonPersistent.PackagesWithSelection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MyContentProvider implements IStructuredContentProvider {

	private List<PackagesWithSelection> packList;

	public MyContentProvider(List<PackagesWithSelection> packList) {
		super();
		this.packList = packList;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (packList != null)
			return packList.toArray();

		// Hack. When the shell terminates, this method is called
		// and if a null value is returned then idart crashes
		return new ArrayList<PackagesWithSelection>().toArray();
	}

	@Override
	public void dispose() {
		// System.out.print(true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		packList = (List<PackagesWithSelection>) newInput;
		viewer.refresh();
	}

}
