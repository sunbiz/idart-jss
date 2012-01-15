package org.celllife.idart.misc;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableWithProgress;

public abstract class AbstractCancellableJob implements IRunnableWithProgress {

	private final Logger log = Logger.getLogger(this.getClass());
	protected Exception error;
	private final String taskName;

	public AbstractCancellableJob() {
		taskName = "Report task";
	}
	
	public AbstractCancellableJob(String taskName) {
		this.taskName = taskName;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	@Override
	public void run(final IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask("Starting " + taskName, 100);
			Thread reportThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						performJob(monitor);
					} catch (Exception e) {
						error = e;
						e.printStackTrace();
						log.error("Error running " + taskName, e);
					}
				}
			},taskName);
			reportThread.start();
			while (reportThread.isAlive()) {
				if (monitor.isCanceled()) {
					reportThread.interrupt();
					reportThread.stop();
					throw new OperationCanceledException("Job cancelled");
				}
				Thread.sleep(1000);
			}
			if (error != null)
				throw new InvocationTargetException(error,
						"Error running " + taskName);
		} finally {
			monitor.done();
		}
	}

	public abstract void performJob(final IProgressMonitor monitor) throws Exception;
}
