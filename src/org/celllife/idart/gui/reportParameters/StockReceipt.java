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

package org.celllife.idart.gui.reportParameters;

import java.util.Calendar;
import java.util.Date;

import model.manager.reports.StockReceiptReport;

import org.apache.log4j.Logger;
import org.celllife.idart.gui.platform.GenericReportGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class StockReceipt extends GenericReportGui {

	private Label lblIcon;

	private Group grpDateRange;

	private Label lblStartDate;

	private Label lblEndDate;

	private SWTCalendar calendarStart;

	private SWTCalendar calendarEnd;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Shell
	 * @param hSession
	 *            Session
	 */

	public StockReceipt(Shell parent, Session hSession) {
		super(parent, hSession, REPORTTYPE_STOCK);
	}

	/**
	 * This method initializes newStockReceipt
	 */
	@Override
	protected void createShell() {
		buildShell("Stock Receipt", new Rectangle(100, 50, 600, 448));
		// create the composites
		createMyGroups();
	}

	private void createMyGroups() {
		createGrpDateRange();
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {

		// compHeader
		compHeader = new Composite(getShell(), SWT.NONE);
		compHeader.setBounds(new org.eclipse.swt.graphics.Rectangle(85, 0, 430,
				70));

		// lblIcon
		lblIcon = new Label(compHeader, SWT.NONE);
		lblIcon
		.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 10, 50, 43));
		lblIcon.setText("");
		lblIcon.setImage(ResourceUtils
				.getImage(iDartImage.REPORT_PACKAGESARRIVE));

		// lblHeader
		lblHeader = new Label(compHeader, SWT.CENTER | SWT.SHADOW_IN);
		lblHeader.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		lblHeader.setBounds(new org.eclipse.swt.graphics.Rectangle(60, 20, 370,
				30));
		lblHeader.setText("Stock Receipt (Received at Pharmacy)");

	}

	/**
	 * This method initializes grpDateRange
	 * 
	 */
	private void createGrpDateRange() {

		grpDateRange = new Group(getShell(), SWT.NONE);
		grpDateRange.setText("Date Range:");
		grpDateRange.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		grpDateRange.setBounds(new Rectangle(35, 100, 520, 220));

		lblStartDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblStartDate.setBounds(new Rectangle(40, 30, 180, 20));
		lblStartDate.setText("Select a START date:");
		lblStartDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblEndDate = new Label(grpDateRange, SWT.CENTER | SWT.BORDER);
		lblEndDate.setBounds(new Rectangle(300, 30, 180, 20));
		lblEndDate.setText("Select an END date:");
		lblEndDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		calendarStart = new SWTCalendar(grpDateRange);
		calendarStart.setBounds(40, 65, 180, 120);

		calendarEnd = new SWTCalendar(grpDateRange);
		calendarEnd.setBounds(300, 65, 180, 120);
	}

	/**
	 * Method getCalendarStart.
	 * @return Calendar
	 */
	public Calendar getCalendarStart() {
		return calendarStart.getCalendar();
	}

	/**
	 * Method getCalendarEnd.
	 * @return Calendar
	 */
	public Calendar getCalendarEnd() {
		return calendarEnd.getCalendar();
	}

	/**
	 * Method setStartDate.
	 * @param date Date
	 */
	public void setStartDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendarStart.setCalendar(calendar);
	}

	/**
	 * Method setEndDate.
	 * @param date Date
	 */
	public void setEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendarEnd.setCalendar(calendar);
	}

	/**
	 * Method addStartDateChangedListener.
	 * @param listener SWTCalendarListener
	 */
	public void addStartDateChangedListener(SWTCalendarListener listener) {

		calendarStart.addSWTCalendarListener(listener);
	}

	/**
	 * Method addEndDateChangedListener.
	 * @param listener SWTCalendarListener
	 */
	public void addEndDateChangedListener(SWTCalendarListener listener) {

		calendarEnd.addSWTCalendarListener(listener);
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {
	}

	/**
	 * This method is called when the user presses the 'View Report' button. It
	 * calls the ReportManager.viewStockReceiptReport with the appropriate start
	 * and end dates, as given by the user.
	 * 
	 */
	@Override
	protected void cmdViewReportWidgetSelected() {
		if (calendarStart.getCalendar().getTime().after(
				calendarEnd.getCalendar()
				.getTime())) {

			MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR);
			mb.setText("Invalid End Date");
			mb.setMessage("Please select an end date after the start date");
			mb.open();
		}
		else {
			StockReceiptReport report = new StockReceiptReport(getShell(),
					calendarStart.getCalendar().getTime(), calendarEnd
					.getCalendar().getTime());
			viewReport(report);
		}
	}

	/**
	 * This method is called when the user presses "Close" button
	 * 
	 */
	@Override
	protected void cmdCloseWidgetSelected() {
		cmdCloseSelected();
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
