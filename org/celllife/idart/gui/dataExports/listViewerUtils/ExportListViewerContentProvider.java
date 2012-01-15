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
package org.celllife.idart.gui.dataExports.listViewerUtils;

import java.util.ArrayList;
import java.util.List;

import model.manager.exports.columns.SimpleColumnsEnum;

import org.apache.log4j.Logger;
import org.celllife.idart.gui.dataExports.DataExport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ExportListViewerContentProvider implements IStructuredContentProvider {

	private static Logger log = Logger.getLogger(DataExport.class);
	/**
	   * 
	   * @param arg0
	   *            
	   */
	  @Override
	public Object[] getElements(Object arg0) {
		  
		  Object[] result = new Object[((Object[]) arg0).length];
		  for(int i = 0; i < ((Object[])arg0).length; i++) {
			  result[i] = ((Object[])arg0)[i];
		  }
		 return result;
	  }

	  /**
	   * Disposes any created resources
	   */
	  @Override
	public void dispose() {
	    // Nothing to dispose
	  }

	  /**
	   * Called when the input changes
	   */
	  @Override
	public void inputChanged(Viewer arg0, Object oldObject, Object newObject) {
		 List<SimpleColumnsEnum> lst = new ArrayList<SimpleColumnsEnum>();
		 for (SimpleColumnsEnum l : lst){
			 log.debug("Input: " +l.toString()); 
		 }
		 
	  }
}
