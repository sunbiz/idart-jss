package org.celllife.idart.database;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.database.wizard.ConnectionPage;
import org.celllife.idart.database.wizard.CreateDatabasePage;
import org.celllife.idart.database.wizard.PropertiesPage;
import org.celllife.idart.misc.PropertiesEncrypter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class DatabaseWizard extends Wizard {
	private static Logger log = Logger.getLogger(DatabaseWizard.class);
	public static final int PAGE_CONNECTION_DETAILS = 0;
	public static final int PAGE_CREATE_DB = 1;

	private final int startPage;
	private ConnectionPage connPage;
	private CreateDatabasePage dbPage;

	public DatabaseWizard(int startPage) {
		super();
		this.startPage = startPage;
		setWindowTitle("iDART Setup Wizard");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		if (startPage <= PAGE_CONNECTION_DETAILS) {
			connPage = new ConnectionPage();
			addPage(connPage);
		}
		if (startPage <= PAGE_CREATE_DB) {
			dbPage = new CreateDatabasePage();
			addPage(dbPage);
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		PropertiesPage ppage = (PropertiesPage) page;
		ppage.updateProperties();

		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage == null)
			return nextPage;

		ppage = (PropertiesPage) nextPage;
		if (ppage.isRequired())
			return nextPage;
		else
			return getNextPage(nextPage);
	}

	@Override
	public boolean performFinish() {
		if (startPage <= PAGE_CONNECTION_DETAILS) {
			try {
				log.info("Writing updated properties to file.");
				PropertiesEncrypter pe = new PropertiesEncrypter();
				pe.loadPropertiesFromString(iDartProperties
						.getPropertiesString());
				pe.encryptProperties();
				pe.savePropertiesToFile(iDartProperties.FILE);
			} catch (IOException e) {
				log.error("unable to write properties to file.", e);
				MessageDialog.openError(getShell(), "Properties  Error",
						"Unable to update the properties.\n"
						+ "Check the error logs for more information.");
				return false;
			}
		}
		if (startPage <= PAGE_CREATE_DB && dbPage.isRequired()) {
			try {
				new ProgressMonitorDialog(getShell()).run(false, false,
						new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
					throws InvocationTargetException,
					InterruptedException {
						monitor.beginTask("Initialising database.",
								IProgressMonitor.UNKNOWN);
						monitor.subTask("This will take a few minutes.");
						if (!DatabaseTools._().createDatabase(
										dbPage.shouldIncludeTest(), true))
							throw new InvocationTargetException(
									new DatabaseException());
					}
				});
			} catch (Exception e) {
				MessageDialog.openError(getShell(), "Database Error",
						"Unable to create the database.\n"
						+ "Check the error logs for more information.");
				return false;
			}
		}

		HibernateUtil.rebuildUtil();
		return true;
	}
}
