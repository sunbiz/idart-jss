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

package org.celllife.idart.start;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 */
public class SysTrayTest {
	/**
	 * Method main.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		// We will need this
		final Display display = Display.getDefault();
		// Retrieves the system tray singleton
		final Tray tray = display.getSystemTray();
		// Creates a new tray item (displayed as an icon)
		final TrayItem item = new TrayItem(tray, 0);
		final Image img = new Image(display, "img/ArrowLeft.jpg");
		item.setToolTipText("SWT Tray in action");
		item.setImage(img);
		// The tray item can only receive Selection/DefaultSelection (left
		// click) or
		// MenuDetect (right click) events
		item.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// We need a Shell as the parent of our menu
				Shell s = new Shell(event.display);
				// Style must be pop up
				Menu m = new Menu(s, SWT.POP_UP);
				// Creates a new menu item that terminates the program
				// when selected
				MenuItem exit = new MenuItem(m, SWT.NONE);
				exit.setText("Goodbye!");
				exit.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event1) {
						System.exit(0);
					}
				});
				// We need to make the menu visible
				m.setVisible(true);
			}
		});

		// Wait forever...
		while (true) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
