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

package org.celllife.idart.gui.welcome;

import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class Load {
	private Shell splash;
	private ProgressBar bar;
	private int progress = 0;

	/**
	 * Default Constructor
	 */
	public Load() {
		createNewLoad();
	}

	/**
	 * This method initializes newLoad
	 */
	private void createNewLoad() {
		splash = new Shell(SWT.ON_TOP);
		bar = new ProgressBar(splash, SWT.NONE);
		bar.setMaximum(100);

		FormLayout layout = new FormLayout();
		splash.setLayout(layout);
		
		final Image image = ResourceUtils.getImage(iDartImage.SPLASH);
		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100, 0);
		labelData.bottom = new FormAttachment(100, 0);
		label.setLayoutData(labelData);
		
		FormData progressData = new FormData ();
		progressData.left = new FormAttachment (0, 5);
		progressData.right = new FormAttachment (100, -5);
		progressData.bottom = new FormAttachment (100, -5);
		bar.setLayoutData(progressData);
		
		splash.pack();
		
		Rectangle splashRect = splash.getBounds();
		Rectangle r = Display.getDefault().getBounds();
		int shellX = (r.width - splashRect.width) / 2;
		int shellY = (r.height - splashRect.height) / 2;
		splash.setLocation(shellX, shellY);

		splash.open();
	}

	public void killMe() {
		if (splash != null)
			splash.dispose();
		
		splash = null;
	}

	public boolean isOpen() {
		return splash != null && !splash.isDisposed();
	}
	
	public void updateProgress(int done) {
		if (!bar.isDisposed()) {
			this.progress += done;
			bar.setSelection(progress);
		}
	}

}
