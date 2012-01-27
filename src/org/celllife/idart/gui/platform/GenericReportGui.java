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
package org.celllife.idart.gui.platform;

import java.text.MessageFormat;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

/**
 */
public abstract class GenericReportGui extends GenericGui implements
GenericReportGuiInterface {

	protected Composite compHeader;

	protected Composite compButtons;

	protected Label lblIcon;

	protected Label lblHeader;

	protected Button btnViewReport;

	protected Button btnClose;

	private int REPORT_TYPE = 0;

	private static boolean reportToOpen;

	private static boolean shouldClose;

	/**
	 * @param parent
	 * @param hSession
	 * @param reportType
	 *            int
	 */
	public GenericReportGui(Shell parent, Session hSession, int reportType) {
		super(parent, hSession);
		setReportType(reportType);
	}

	/**
	 * @param parent
	 * @param reportType
	 *            int
	 * @param activate
	 *            boolean
	 */
	public GenericReportGui(Shell parent, int reportType, boolean activate) {
		super(parent, null);
		setReportType(reportType);
		if (activate) {
			activate();
		}
	}

	protected void disableClosingShell(Shell shell) {
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				cmdCloseWidgetSelected();
			}
		});
	}

	@Override
	protected void activate() {
		setHSession(HibernateUtil.getNewSession());
		super.activate();
		disableClosingShell(getShell());
	}

	// Mandatory Abstract Methods
	protected abstract void createCompHeader();

	protected abstract void createCompButtons();

	protected abstract void cmdViewReportWidgetSelected();

	protected abstract void cmdCloseWidgetSelected();

	/**
	 * Method buildShell.
	 * 
	 * @param shellTxt
	 *            String
	 * @param bounds
	 *            Rectangle
	 */
	protected void buildShell(String shellTitle, Rectangle bounds) {
		String title = Messages.getString("common.screen.title"); //$NON-NLS-1$
		
		// show class names in title to if env variable set
		if (System.getenv("idart.debug.ui") != null){
			shellTitle += " - " + this.getClass().getName();
		}
		
		getShell().setText(
				MessageFormat.format(title, shellTitle, LocalObjects.getUser(
						getHSession()).getUsername()));
		getShell().setBounds(bounds);
		try {
			Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
			getShell().setImage(i);
		} catch (RuntimeException e) {
			getLog().error("Bad image URL", e); //$NON-NLS-1$
		}
		createCompHeader();
		buildCompButtons();
	}

	/**
	 * Method buildCompdHeader.
	 * 
	 * @param headerTxt
	 *            String
	 * @param icoImage
	 *            iDartImage
	 */
	protected void buildCompdHeader(String headerTxt, iDartImage icoImage) {
		// compHeader
		setCompHeader(new Composite(getShell(), SWT.NONE));
		getCompHeader().setLayout(new FormLayout());

		// lblHeader
		lblHeader = new Label(getCompHeader(), SWT.CENTER | SWT.SHADOW_IN);
		lblHeader.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		lblHeader.setText(headerTxt.replaceAll("&", "&&"));

		FormData fdata = new FormData();
		fdata.left = new FormAttachment(15, 0);
		fdata.right = new FormAttachment(85, 0);
		fdata.top = new FormAttachment(0, 15);
		lblHeader.setLayoutData(fdata);

		// lblIcon
		lblIcon = new Label(getCompHeader(), SWT.NONE);
		lblIcon.setImage(ResourceUtils.getImage(icoImage));

		fdata = new FormData();
		fdata.right = new FormAttachment(lblHeader, -15, SWT.LEFT);
		fdata.top = new FormAttachment(lblHeader, 0, SWT.CENTER);
		lblIcon.setLayoutData(fdata);

		// lblBackGround
		Label lblBg = new Label(getCompHeader(), SWT.NONE | SWT.SHADOW_IN);
		lblBg.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		fdata = new FormData();
		fdata.left = new FormAttachment(lblHeader, -5, SWT.LEFT);
		fdata.top = new FormAttachment(lblHeader, -5, SWT.TOP);
		fdata.bottom = new FormAttachment(lblHeader, 5, SWT.BOTTOM);
		fdata.right = new FormAttachment(lblHeader, 5, SWT.RIGHT);
		lblBg.setLayoutData(fdata);

		getCompHeader().pack();
		// set bound after packing otherwise it resizes the composite
		Rectangle b = getShell().getBounds();
		getCompHeader().setBounds(0, 0, b.width, 50);
	}

	protected void buildCompButtons() {

		Composite myCmp = new Composite(getShell(), SWT.NONE);

		RowLayout rowlyt = new RowLayout();
		rowlyt.justify = true;
		rowlyt.pack = true;
		rowlyt.spacing = 10;
		myCmp.setLayout(rowlyt);

		setCompButtons(new Composite(myCmp, SWT.NONE));
		getCompButtons().setLayout(rowlyt);

		btnViewReport = new Button(getCompButtons(), SWT.NONE);
		btnViewReport.setText(Messages.getString("reportgui.button.viewreport.text")); //$NON-NLS-1$
		btnViewReport.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnViewReport
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdViewReportWidgetSelected();
			}
		});

		// calling any extra buttons that might be constructed.
		createCompButtons();

		btnClose = new Button(getCompButtons(), SWT.NONE);
		btnClose.setText(Messages.getString("common.button.close.text")); //$NON-NLS-1$
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClose
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseWidgetSelected();
			}
		});

		Control[] buttons = getCompButtons().getChildren();
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].getLayoutData() == null) {
				buttons[i].setLayoutData(new RowData(150, 30));
			}
			buttons[i].pack();
		}

		myCmp.pack();
		Rectangle bnd = getShell().getBounds();
		myCmp.setBounds(0, bnd.height - 75, bnd.width, 40);

	}

	protected void cmdCloseSelected() {
		closeShell(true);
		setActive(false);
	}

	/**
	 * @return the compButtons
	 */
	protected Composite getCompButtons() {
		return compButtons;
	}

	/**
	 * @param compButtons
	 *            the compButtons to set
	 */
	protected void setCompButtons(Composite compButtons) {
		this.compButtons = compButtons;
	}

	/**
	 * @return the compHeader
	 */
	protected Composite getCompHeader() {
		return compHeader;
	}

	/**
	 * @param compHeader
	 *            the compHeader to set
	 */
	protected void setCompHeader(Composite compHeader) {
		this.compHeader = compHeader;
	}

	/**
	 * @return the rEPORT_TYPE
	 */
	public int getReportType() {
		return REPORT_TYPE;
	}

	/**
	 * @param report_type
	 *            the rEPORT_TYPE to set
	 */
	public void setReportType(int report_type) {
		REPORT_TYPE = report_type;
	}

	public void openShell() {
		if (!active) {
			activate();
		} else {
			getShell().open();
		}
	}

	/**
	 * @return the reportToOpen
	 */
	public static boolean isReportToOpen() {
		return reportToOpen;
	}

	/**
	 * @param reportToOpen
	 *            the reportToOpen to set
	 */
	public static void setReportToOpen(boolean reportToOpen) {
		GenericReportGui.reportToOpen = reportToOpen;
	}

	/**
	 * @return the shouldClose
	 */
	public static boolean isShouldClose() {
		return shouldClose;
	}

	/**
	 * @param shouldClose
	 *            the shouldClose to set
	 */
	public static void setShouldClose(boolean shouldClose) {
		GenericReportGui.shouldClose = shouldClose;
	}

}
