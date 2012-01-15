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

package org.celllife.idart.print.label;

import java.util.List;

/**
 * Simple class to print objects and allow user to carry on doing other things
 * 
 */
public class PrintThread implements Runnable {
	List<Object> printObjects = null;

	Thread t;

	/**
	 * Constructor for PrintThread.
	 * 
	 * @param toPrint
	 *            List<Object>
	 */
	public PrintThread(List<Object> toPrint) {
		printObjects = toPrint;
		t = new Thread(this);
		t.start();
	}

	/**
	 * Method run.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			PrintLabel.printiDARTLabels(printObjects);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
