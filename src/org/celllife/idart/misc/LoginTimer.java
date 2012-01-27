package org.celllife.idart.misc;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

/**
 */
public class LoginTimer implements Runnable {

	private static Logger log = Logger.getLogger(LoginTimer.class.getName());

	private Thread t;

	// this gets checked every 10 seconds
	private int LOGIN_TIMEOUT; // time in seconds

	private int countingTime = 0;

	private boolean terminate = false;

	private boolean disabled = false;

	private Display display = null;

	/**
	 * Constructor for LoginTimer.
	 * @param timeout int
	 */
	public LoginTimer(int timeout) {

		LOGIN_TIMEOUT = timeout;
		log.info("Starting new logout timer with timeout " + LOGIN_TIMEOUT
				+ " seconds.");
		display = Display.getCurrent();
		if (display == null) {
			display = new Display();
		}
		t = new Thread(this, "LoginTimer Thread");

	}

	/**
	 * Starts the timer (again).
	 * 
	 */
	public void start() {
		terminate = false;
		restart();
		if (t.isAlive())
			return;

		t = new Thread(this, "LoginTimer Thread");

		t.start();
	}

	/**
	 * Method stop.
	 * @return boolean
	 */
	public boolean stop() {
		if (!disabled) {
			terminate = true;
			log.debug("Terminating iDART - TIMER EXPIRED");
			return true;
		}
		return false;
	}

	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {

			// the thread sleeps and every 1 second it
			// wakes up to see if it should terminate
			int timeoutTime = LOGIN_TIMEOUT;

			while (countingTime < timeoutTime) {

				if (!disabled)
					increaseCount();
				if (terminate)
					break;

				// log.info("countingTime:"+countingTime+"
				// timeoutTime:"+timeoutTime);

				Thread.sleep(1000);

			}

			// log.warn("timeout exceeded at countingTime:"+countingTime+"
			// timeoutTime:"+timeoutTime);
			// wake the UI thread
			if (!display.isDisposed())
				display.syncExec(null);

		} catch (InterruptedException e) {
			log.error("Timer thread Interrupted", e);
		}
	}

	/**
	 * Method getThread.
	 * @return Thread
	 */
	public Thread getThread() {
		return t;
	}

	/**
	 * Method terminate.
	 * @return boolean
	 */
	public boolean terminate() {
		if (!disabled) {
			terminate = true;
			log.debug("Terminating iDART - TIMER EXPIRED");
			return true;
		}
		return false;
	}

	synchronized private void increaseCount() {
		countingTime++;
	}

	/**
	 * Restart the timer
	 * 
	 */
	synchronized public void restart() {
		countingTime = 0;
	}

	/**
	 * Method isDisabled.
	 * @return boolean
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Method setDisabled.
	 * @param disabled boolean
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
