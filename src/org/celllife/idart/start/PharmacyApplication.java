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

import java.util.Arrays;

import model.manager.AdministrationManager;
import model.manager.PatientManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.ConnectException;
import org.celllife.idart.database.DatabaseEmptyException;
import org.celllife.idart.database.DatabaseException;
import org.celllife.idart.database.DatabaseTools;
import org.celllife.idart.database.DatabaseWizard;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.events.EventManager;
import org.celllife.idart.gui.login.Login;
import org.celllife.idart.gui.login.LoginErr;
import org.celllife.idart.gui.welcome.ClinicWelcome;
import org.celllife.idart.gui.welcome.GenericWelcome;
import org.celllife.idart.gui.welcome.Load;
import org.celllife.idart.gui.welcome.PharmacyWelcome;
import org.celllife.idart.gui.welcome.ReportWorkerWelcome;
import org.celllife.idart.gui.welcome.StudyWorkerWelcome;
import org.celllife.idart.integration.eKapa.EkapaSubmitJob;
import org.celllife.idart.integration.eKapa.JobScheduler;
import org.celllife.idart.misc.MessageUtil;
import org.celllife.idart.misc.task.TaskManager;
import org.celllife.idart.sms.SmsRetrySchedulerJob;
import org.celllife.idart.sms.SmsSchedulerJob;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 */
public class PharmacyApplication {

	private static Logger log = Logger.getLogger(PharmacyApplication.class);
	private static Load loginLoad;

	/**
	 * Method main.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		// used for gui testing
		System.setProperty("org.eclipse.swtbot.search.defaultKey",
				iDartProperties.SWTBOT_KEY);

		DOMConfigurator.configure("log4j.xml");
		log.info("");
		log.info("*********************");
		log.info("iDART " + iDartProperties.idartVersionNumber + " starting");
		log.info("*********************");
		log.info("");

		createDisplay();
		openSplash();
		loadConstants();
		performStartupChecks();
		doInitialisationTasks();
		launch(args);
	}

	private static void createDisplay() {
		if (Display.getCurrent() == null) {
			new Display();
		}
	}

	private static void launch(String[] args) {

		closeSplash();

		if (args != null && args.length > 0) {
			String option = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);
			TaskManager.runTask(option, args);
		}
		runIDART();
	}

	private static void performStartupChecks() {
		try {
			if (!DatabaseTools._().checkDatabase()) {
				startSetupWizard(DatabaseWizard.PAGE_CREATE_DB);
			}
		} catch (ConnectException e) {
			startSetupWizard(DatabaseWizard.PAGE_CONNECTION_DETAILS);
		} catch (DatabaseEmptyException e) {
			startSetupWizard(DatabaseWizard.PAGE_CREATE_DB);
		} catch (DatabaseException e) {
			String msg = "Error while checking database consistency: ";
			log.error(msg, e);
			showStartupErrorDialog(msg + e.getMessage());
			System.exit(1);
		}
		
		loginLoad.updateProgress(30);
		
		try {
			DatabaseTools._().update();
		} catch (DatabaseException e) {
			String msg = "Error while updateing the database: "
				+ e.getMessage();
			if (DatabaseTools._().isOldVersion()) {
				msg = "Database needs to be manually updated to version 3.5.0.\n"
					+ "Please run the necessary update scripts and try again.";
			}
			log.error(msg, e);
			showStartupErrorDialog(msg);
			System.exit(1);
		}
		
		try {
			HibernateUtil.setValidation(true);
		} catch (Exception e) {
			String msg = "Error while checking database consistency: ";
			log.error(msg, e);
			showStartupErrorDialog(msg + e.getMessage());
			System.exit(1);
		}
		
		loginLoad.updateProgress(50);
	}

	private static void exit(int exitStatus) {
		closeSplash();
		System.exit(exitStatus);
	}

	private static void runIDART() {
		boolean userExited;
		GenericWelcome welcome = null;
		JobScheduler scheduler = new JobScheduler();
		EventManager events = new EventManager();
		events.register();
		do {
			Login loginScreen = new Login();

			if (loginScreen.isSuccessfulLogin()) {
				startEkapaJob(scheduler);
				startSmsJobs(scheduler);
				
				try {
					String role = LocalObjects.getUser(HibernateUtil.getNewSession()).getRole();
					if(role != null && role.equalsIgnoreCase("StudyWorker")){
						welcome = new StudyWorkerWelcome();
					} else if(role != null && role.equalsIgnoreCase("ReportsWorker")){
						welcome = new ReportWorkerWelcome();
					} else {
						if (LocalObjects.currentClinic == LocalObjects.mainClinic) {
							welcome = new PharmacyWelcome();
						} else {
							welcome = new ClinicWelcome();
						}
					}
				} catch (Exception e) {
					log.error("iDART CRASH: - Fatal Error caused by Exception:", e);

					MessageUtil.showError(e, "iDART Error",	MessageUtil.getCrashMessage());
					closeAllShells();
					exit(1);

					log.fatal(e.getMessage(), e);
				}
				log.debug("Logged out..");

				if (welcome == null) {
					closeAllShells();
					userExited = true;
				} else {
					userExited = false;
				}
			} else {
				userExited = true;
			}

		} while (!userExited && welcome != null && welcome.isTimedOut());

		scheduler.shutdown();
		events.deRegister();
		log.info("");
		log.info("*********************");
		log.info("iDART " + iDartProperties.idartVersionNumber + " exited");
		log.info("*********************");
		log.info("");
	}

	private static void startSmsJobs(JobScheduler scheduler) {
		if (iDartProperties.isCidaStudy) {
			String cidaGroupName = "cida";
			if (!scheduler.hasJob(cidaGroupName, SmsSchedulerJob.JOB_NAME)) {
				scheduler.scheduleOnceOff(SmsSchedulerJob.JOB_NAME, cidaGroupName, SmsSchedulerJob.class);
			}
			if (!scheduler.hasJob(cidaGroupName, SmsRetrySchedulerJob.JOB_NAME)) {
				scheduler.schedule(SmsRetrySchedulerJob.JOB_NAME, cidaGroupName, SmsRetrySchedulerJob.class, 60);
			}
		}
	}

	private static void startEkapaJob(JobScheduler scheduler) {
		if (iDartProperties.isEkapaVersion) {
			if (!scheduler.hasJob(EkapaSubmitJob.GROUP_NAME, EkapaSubmitJob.JOB_NAME)) {
				scheduler.schedule( EkapaSubmitJob.JOB_NAME, EkapaSubmitJob.GROUP_NAME, EkapaSubmitJob.class, 2);
			}
		}
	}

	private static void closeAllShells() {
		Display display = Display.getCurrent();

		if (display != null) {
			for (Shell s : display.getShells()){
				if (s != null) {
					s.dispose();
				}
			}
			display.dispose();
		}
	}

	private static void openSplash() {
		loginLoad = new Load();
	}

	private static void loadConstants() {
		try {
			iDartProperties.setiDartProperties();

			if (log.isTraceEnabled()) {
				try {
					log.trace("Current iDART properties: \n"
							+ iDartProperties.printProperties());
				} catch (Exception e1) {
					log.error("Error printing properties", e1);
				}
			}
		} catch (Exception e) {
			log.error("Unable to load idart.properties file.",e);
			showStartupErrorDialog("Unable to load properties from idart.properties file." +
			" Please ensure it exists.");
			System.exit(1);
		}
		
		try {
			PropertiesManager.sms();
			
			if (log.isTraceEnabled()) {
				try {
					log.trace("Current properties: \n"
							+ PropertiesManager.printProperties());
				} catch (Exception e1) {
					log.error("Error printing properties", e1);
				}
			}
		} catch (Exception e) {
			log.error("Unable to load sms.properties file.", e);
			showStartupErrorDialog("Unable to load properties from sms.properties file." +
					" Please ensure it exists.");
			System.exit(1);
		}
		loginLoad.updateProgress(10);
	}

	private static void startSetupWizard(int startPage) {
		closeSplash();
		DatabaseWizard wizard = new DatabaseWizard(startPage);
		Shell shell = new Shell();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		int returnCode = dialog.open();
		if (returnCode == Window.CANCEL) {
			showStartupErrorDialog("Startup failed. Unable to initialise the database.\n"
					+ "Check the error logs in the iDART folder for more information.");
			System.exit(1);
		}
	}

	private static void closeSplash() {
		if (loginLoad.isOpen()) {
			loginLoad.killMe();
		}
	}

	/**
	 * Method doInitialisationTasks.
	 * 
	 * @return boolean
	 */
	private static boolean doInitialisationTasks() {
		Session hSession = HibernateUtil.getNewSession();
		Transaction tx = null;
		try {
			log.info("Starting Initialisation Tasks");
			tx = hSession.beginTransaction();

			setPatientAttributes();
			CommonObjects.loadLanguages();
			
			loginLoad.updateProgress(5);

			// set default clinic
			LocalObjects.mainClinic = AdministrationManager
			.getMainClinic(hSession);
			LocalObjects.nationalIdentifierType = AdministrationManager
					.getNationalIdentifierType(hSession);
			
			loginLoad.updateProgress(5);

			LocalObjects.pharmacy = AdministrationManager
			.getPharmacyDetails(hSession);
			// do some database queries while loading
			PatientManager.checkPregnancies(hSession);
			
			loginLoad.updateProgress(5);

			// update units remaining for stock
			StockManager.updateStockLevels(hSession);
			
			loginLoad.updateProgress(5);

			hSession.flush();
			tx.commit();
			log.info("Finishing Initialisation Tasks");
			return true;
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			log.error("Hibernate error during startup tasks.", e);
			return false;
		} finally {
			try {
				hSession.close();
			} catch (Exception e) {
			}
		}
	}

	private static void setPatientAttributes() {
		Session sess = HibernateUtil.getNewSession();
		Transaction tx = sess.beginTransaction();
		
		PatientManager.checkPatientAttributes(sess);
		
		tx.commit();
		sess.flush();
		sess.close();
	}

	/**
	 * Method showStartupErrorDialog.
	 * 
	 * @param message
	 *            String
	 */
	private static void showStartupErrorDialog(String message) {
		closeSplash();
		new LoginErr(message);
	}

}
