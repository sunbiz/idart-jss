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

package model.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

import com.jasperassistant.designer.viewer.ViewerApp;

/**
 */
@SuppressWarnings("unchecked")
public class ReportManager {

	private static Logger log = Logger.getLogger(ReportManager.class);

	private final Shell parent;

	private Thread thread;

	boolean reportFinished = false;

	boolean reportGenerationCancelled = false;

	boolean longQueryFinished = false;

	private FileInputStream fileInputStream;

	private Map map;

	private Connection connection;

	private Session hSession = null;

	private JasperPrint jp;

	private ArrayList<JasperPrint> reportList;

	/**
	 * Constructor for ReportManager.
	 * 
	 * @param hSession
	 *            Session
	 * @param parent
	 *            Shell
	 */
	public ReportManager(Session hSession, Shell parent) {

		this.parent = parent;
		this.hSession = hSession;
		reportFinished = false;

	}

	@Deprecated
	public void viewStockReportPerStockCenter(StockCenter stockCenter,
			Date startDate, Date endDate) {

		try {
			GregorianCalendar startCal = new GregorianCalendar();
			startCal.setTime(startDate);
			startCal.add(GregorianCalendar.DATE, -1);
			startDate = startCal.getTime();
			GregorianCalendar endCal = new GregorianCalendar();
			endCal.setTime(endDate);
			endCal.add(GregorianCalendar.DATE, 1);
			endDate = endCal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			log.info("Running Stock Report Per StockCenter on: "
					+ stockCenter.getStockCenterName() + " Between: "
					+ sdf.format(startDate) + " and " + sdf.format(endDate));

			File n = new File("Reports" + java.io.File.separator);
			map = new HashMap();
			map.put("p_stockCenterId", Integer.valueOf(stockCenter.getId()));
			map.put("p_stockCenterName", stockCenter.getStockCenterName());
			map.put("p_path", n.getCanonicalPath());
			map.put("p_startDate", sdf.format(startDate));
			map.put("p_endDate", sdf.format(endDate));
			map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
			map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
			map.put("pharmacist2", LocalObjects.pharmacy
					.getAssistantPharmacist());
			// Report to use
			fileInputStream = new FileInputStream("Reports"
					+ java.io.File.separator + "stockPerStockCenter.jasper");
			// Define the connection
			connection = JDBCUtil.currentSession();
			viewReport();
		} catch (FileNotFoundException e) {
			log.error("Unable to find report file.", e);
		} catch (IOException e) {
			log.error("Error reading report file.", e);
		} catch (SQLException e) {
			log
			.error(
					"Error getting connection to database while generating report.",
					e);
		}
	}

	public void viewPatientCollectionSheet(List<String> patientList) {

		File n = new File("Reports" + java.io.File.separator);
		map = new HashMap();

		reportList = new ArrayList();

		for (String patientId : patientList) {
			try {
				map.put("path", n.getCanonicalPath());
				map.put("patientid", patientId);
				map.put("HIBERNATE_SESSION", hSession);
				// Report to use
				fileInputStream = new FileInputStream("Reports"
						+ java.io.File.separator
						+ "patientCollectionSheet.jasper");
				// Define the connection
				connection = JDBCUtil.currentSession();
				addReportToQueue();

			} catch (IOException e) {
				log.error("Error reading report file.", e);
			} catch (SQLException e) {
				log
				.error(
						"Error getting connection to database while generating report.",
						e);
			}
		}
		viewBatchReport();
	}

	public void addReportToQueue() {
		reportFinished = false;
		reportGenerationCancelled = false;

		thread = new Thread() {
			@Override
			public void run() {
				try {
					jp = JasperFillManager.fillReport(fileInputStream, map,
							connection);
					reportFinished = true;
					while (!reportFinished) {
						sleep(1000);
						reportFinished = true;
					}

				} catch (InterruptedException ex) {
					reportGenerationCancelled = true;
				} catch (JRException e) {
					log.error("Error generating report.", e);
					reportGenerationCancelled = true;
				}

			}

		};
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
		createLoadingBar();
		reportList.add(jp);

	}

	/**
	 * Starts the report thread and shows a progress bar dialog. Only to be used
	 * by reports with SQL and HQL data sources at the moment
	 */
	public void viewBatchReport() {
		JasperPrint jp_0 = reportList.get(0);
		List<Integer> reportSizes = new ArrayList();
		for (int i = 0; i < reportList.size(); i++) {
			reportSizes.add(Integer.valueOf(reportList.get(i).getPages().size()));
		}

		for (int i = 1; i < reportList.size(); i++) {
			List<JRPrintPage> pageList = reportList.get(i).getPages();
			for (int j = 0; j < reportSizes.get(i).intValue(); j++) {
				jp_0.addPage(pageList.get(j));
			}
		}

		if (jp_0 != null) {
			if (jp_0.getPages().size() > 0) {
				ViewerApp viewer = new ViewerApp();
				viewer.getReportViewer().setDocument(jp_0);
				viewer.open();
			} else if (!reportGenerationCancelled) {
				MessageBox mNoPages = new MessageBox(parent, SWT.ICON_ERROR
						| SWT.OK);
				mNoPages.setText("Report Has No Pages");
				mNoPages
				.setMessage("The report you are trying to generate does not contain any data. \n\nPlease check the input values you have entered (such as dates) for this report, and try again.");
				mNoPages.open();
			}
		}

	}

	/**
	 * Starts the report thread and shows a progress bar dialog. Only to be used
	 * by reports with SQL and HQL data sources at the moment
	 */
	private void viewReport() {
		reportFinished = false;
		reportGenerationCancelled = false;
		thread = new Thread() {
			@Override
			public void run() {
				try {
					jp = JasperFillManager.fillReport(fileInputStream, map,
							connection);
					reportFinished = true;
					while (!reportFinished) {
						sleep(1000);
						reportFinished = true;
					}
				} catch (InterruptedException ex) {
					reportGenerationCancelled = true;
				} catch (JRException e) {
					reportGenerationCancelled = true;
					log.error("Error generating report.", e);
				}

			}

		};
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
		createLoadingBar();
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				if (jp != null) {
					if (jp.getPages().size() > 0) {
						ViewerApp viewer = new ViewerApp();
						viewer.getReportViewer().setDocument(jp);
						viewer.open();
					} else if (!reportGenerationCancelled) {
						MessageBox mNoPages = new MessageBox(parent,
								SWT.ICON_ERROR | SWT.OK);
						mNoPages.setText("Report Has No Pages");
						mNoPages
						.setMessage("The report you are trying to generate does not contain any data. \n\nPlease check the input values you have entered (such as dates) for this report, and try again.");
						mNoPages.open();
					}
				}
			}
		};
		Display.getCurrent().asyncExec(runner);
	}

	private void createLoadingBar() {
		new ProgressMonitorDialog(parent);
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Report Loading");
		shell.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
			@Override
			public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
				if (thread.isAlive()) {
					thread.interrupt();
					reportFinished = true;
				}
			}
		});
		Label label = new Label(shell, SWT.NONE);
		label.setSize(new Point(260, 20));
		label.setLocation(20, 30);
		label.setText("Please wait while the report loads.");
		label.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		label.setAlignment(SWT.CENTER);
		Label timelabel = new Label(shell, SWT.NONE);
		timelabel.setSize(new Point(260, 20));
		timelabel.setLocation(20, 50);
		// timelabel.setText("(approx " + timeEst + ")");
		timelabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		timelabel.setAlignment(SWT.CENTER);
		ProgressBar progressBar = new ProgressBar(shell, SWT.HORIZONTAL
				| SWT.INDETERMINATE);
		progressBar.setSize(new Point(200, 20));
		progressBar.setLocation(50, 80);
		Button button = new Button(shell, SWT.NONE);
		button.setSize(new Point(100, 30));
		button.setLocation(100, 120);
		button.setText("Cancel");
		button.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		button.setAlignment(SWT.CENTER);
		button
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				if ((thread != null) && thread.isAlive()) {
					thread.interrupt();
					reportFinished = true;
				}
			}
		});
		shell.setSize(new Point(300, 200));
		LayoutUtils.centerGUI(shell);
		shell.open();
		while (!reportFinished) {
			if (!parent.getDisplay().readAndDispatch()) {
				parent.getDisplay().sleep();
			}
		}
		if (!shell.isDisposed()) {
			shell.close();
		}
	}

}
