package org.celllife.idart.update;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.gnu.amSpacks.ILogger;
import org.gnu.amSpacks.app.update.AbstractUpdateJob;
import org.gnu.amSpacks.app.update.UpdateFacade;

public class AutoUpdate {

	public class Log4JLogger implements ILogger {
		@Override
		public void log(String arg0) {
			log.info(arg0);
		}
	}

	private static Logger log = Logger.getLogger(AutoUpdate.class);
	private final UpdateFacade facade;

	public AutoUpdate() {
		java.net.URL updateUrl = null;
		try {
			updateUrl = new java.net.URL(iDartProperties.updateUrl);
		} catch (MalformedURLException e) {
			log.error(e);
		}

		facade = new UpdateFacade();
		facade.setTargetDirectory(new File("."));
		facade.setUrl(updateUrl);
		facade.setLog(new Log4JLogger());
	}

	public boolean updatesAvailable() {
		int numUpdates = 0;
		try {
			numUpdates = facade.getUpdates().size();
		} catch (Exception e) {
			log.error("Error fetching update list.", e);
		}
		return numUpdates > 0;
	}

	public void excecuteUpdate() {
		final AbstractUpdateJob updateJob = facade.getUpdateJob(facade
				.getUpdates());
		updateJob.setLogger(new Log4JLogger());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					updateJob.run(new NullProgressMonitor());
				} catch (InvocationTargetException e) {
					log.error("Error occurred during update.", e);
				} catch (InterruptedException e) {
					log.error("Update aborted.");
				}
			}
		}).start();
	}
}
