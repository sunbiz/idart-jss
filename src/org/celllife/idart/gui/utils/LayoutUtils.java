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

package org.celllife.idart.gui.utils;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class LayoutUtils {

	/**
	 * private constructor to prevent instantiation
	 */
	private LayoutUtils() {
	}

	/**
	 * Method centerGUI.
	 * 
	 * @param shell
	 *            Shell
	 */
	public static void centerGUI(Shell shell) {

		Rectangle outer = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle inner = shell.getBounds();
		int xposition = (outer.width - inner.width) / 2;
		int yposition = ((outer.height - inner.height) / 2);
		shell.setLocation(xposition, yposition);

	}
}
