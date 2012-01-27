package org.celllife.idart.gui.platform;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import model.manager.reports.iDARTReport;

import org.apache.log4j.Logger;
import org.celllife.idart.gui.reportParameters.ReportJob;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.messages.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.hibernate.Session;

/**
 */
public abstract class GenericGui extends Object implements GenericGuiInterface {

	private final DisposeListener disposeListner = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent event) {
			// When the shell is disposed, dispose the shell but don't close the
			// session in case it is being used by the parent.
			// Not sure if this will end up with lots of unclosed sessions?
			closeShell(false);
		}
	};

	private Logger log;

	protected Session hSession;

	private Shell parent;

	protected Shell myShell;

	protected boolean active;

	private static Vector<Class<? extends Composite>> compositesToProcess = new Vector<Class<? extends Composite>>();
	static {
		compositesToProcess.add(Table.class);
		compositesToProcess.add(Combo.class);
		compositesToProcess.add(CCombo.class);
		compositesToProcess.add(Tree.class);
	}

	/*
	 * String passed to the Key shall always be converted to lower case.
	 */
	private static Map<String, Object> options = new HashMap<String, Object>();

	/**
	 * Constructor for GenericGui.
	 * 
	 * @param parent
	 *            Shell
	 * @param hSession
	 *            Session
	 */
	public GenericGui(Shell parent, Session hSession) {
		super();
		if (parent == null) {
			RuntimeException re = new RuntimeException(
			"GenericGui Parent Class is Null. Cannot create GUI."); //$NON-NLS-1$
			getLog().error("Parent of Class is null. Cannot create class.", re); //$NON-NLS-1$
			throw re;
		}
		this.parent = parent;
		this.hSession = hSession;
		setLogger();
	}

	protected void activate() {
		if (parent != null) {
			setShell(new Shell(parent));
		} else {
			log
			.error("Creation of Shell: PARENT is NULL --> Not possible for shell to be null. Verify Class creation from GUI implementor: " //$NON-NLS-1$
					+ this.getClass().getName()
					+ "==>" //$NON-NLS-1$
					+ log.getName()
					+ " -- TIME: " //$NON-NLS-1$
					+ new Date().toString());
			setShell(new Shell());
		}
		getShell().addDisposeListener(disposeListner);
		createShell();
		LayoutUtils.centerGUI(getShell());
		setActive(true);
		openShell();
	}

	private void openShell() {
		getParent().setEnabled(false);
		getShell().open();
	}

	/**
	 * Method closeShell.
	 * 
	 * @param closeSession
	 *            boolean
	 */
	protected void closeShell(boolean closeSession) {
		getParent().setEnabled(true);
		if (closeSession && hSession != null && hSession.isOpen()) {
			hSession.close();
		}
		// to avoid calling this method twice, remove the dispose listener from
		// the shell
		if (!getShell().isDisposed()) {
			getShell().removeDisposeListener(disposeListner);
			getShell().dispose();
		}
	}

	// --- Mandatory Abstract methods

	/**
	 * This method is called during initialisation and is used to create and set
	 * the Logger for the class.
	 * 
	 * @see GenericGui#setLog(Logger)
	 */
	protected abstract void setLogger();

	/**
	 * This method is called during the class initialisation and should call
	 * methods to initialise the class.
	 */
	protected abstract void createShell();

	/**
	 * @return the hSession
	 */
	protected Session getHSession() {
		return this.hSession;
	}

	/**
	 * @param session
	 *            the hSession to set
	 */
	protected void setHSession(Session session) {
		this.hSession = session;
	}

	/**
	 * @return the myLog
	 */
	protected Logger getLog() {
		return this.log;
	}

	/**
	 * @param myLog
	 *            the myLog to set
	 */
	protected void setLog(Logger myLog) {
		this.log = myLog;
	}

	/**
	 * @return the parent
	 */
	protected Shell getParent() {
		return this.parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	protected void setParent(Shell parent) {
		this.parent = parent;
	}

	/**
	 * Method enableSpecificControls.
	 * 
	 * @param widgts
	 *            Control[]
	 * @param enable
	 *            boolean
	 */
	protected void enableSpecificControls(Control[] widgts, boolean enable) {
		for (int i = 0; i < widgts.length; i++) {
			processEnableWidget(widgts[i], enable);
		}
	}

	/**
	 * Method enableControlsByClass.
	 * 
	 * @param toEnable
	 *            Class<? extends Control>[]
	 * @param enable
	 *            boolean
	 */
	protected void enableControlsByClass(Class<? extends Control>[] toEnable,
			boolean enable) {
		Control[] ctr = getShell().getChildren();
		for (int i = 0; i < ctr.length; i++) {
			processEnableByClass(ctr[i], toEnable, enable);
		}
	}

	/**
	 * Method processEnableByClass.
	 * 
	 * @param ctr
	 *            Control
	 * @param toEnable
	 *            Class<? extends Control>[]
	 * @param enable
	 *            boolean
	 */
	private void processEnableByClass(Control ctr,
			Class<? extends Control>[] toEnable, boolean enable) {
		Control[] children;
		if (!ctr.getClass().equals(Table.class)
				&& (ctr instanceof Composite || ctr instanceof Group)) {
			if (ctr instanceof Composite) {
				children = ((Composite) ctr).getChildren();
				for (int i = 0; i < children.length; i++) {
					processEnableByClass(children[i], toEnable, enable);
				}
			} else if (ctr instanceof Group) {
				children = ((Group) ctr).getChildren();
				for (int i = 0; i < children.length; i++) {
					processEnableByClass(children[i], toEnable, enable);
				}
			}
		} else {
			for (int i = 0; i < toEnable.length; i++) {
				if (ctr.getClass().equals(toEnable[i])) {
					processEnableWidget(ctr, enable);
				}
			}
		}
	}

	/**
	 * Method enableAllControls.
	 * 
	 * @param enable
	 *            boolean
	 */
	protected void enableAllControls(boolean enable) {
		Control[] ctr = getShell().getChildren();
		for (int i = 0; i < ctr.length; i++) {
			processEnableAll(ctr[i], enable, null);
		}
	}

	/**
	 * Method enableAllControlsExcept.
	 * 
	 * @param widgts
	 *            Control[]
	 * @param enable
	 *            boolean
	 */
	protected void enableAllControlsExcept(Control[] widgts, boolean enable) {
		Control[] ctr = getShell().getChildren();
		Vector<Control> v = new Vector<Control>();
		for (int i = 0; i < widgts.length; i++) {
			v.add(widgts[i]);
		}
		for (int i = 0; i < v.size(); i++) {
			processEnableAll(ctr[i], enable, v);
		}
	}

	/**
	 * Method processEnableAll.
	 * 
	 * @param ctr
	 *            Control
	 * @param enable
	 *            boolean
	 * @param wdgts
	 *            Vector<Control>
	 */
	private void processEnableAll(Control ctr, boolean enable,
			Vector<Control> wdgts) {
		Control[] children;
		if (!compositesToProcess.contains(ctr.getClass())
				&& (ctr instanceof Composite || ctr instanceof Group)) {
			if (ctr instanceof Composite) {
				children = ((Composite) ctr).getChildren();
				for (int i = 0; i < children.length; i++) {
					processEnableAll(children[i], enable, wdgts);
				}
			} else if (ctr instanceof Group) {
				children = ((Group) ctr).getChildren();
				for (int i = 0; i < children.length; i++) {
					processEnableAll(children[i], enable, wdgts);
				}
			}
		} else {
			if (wdgts != null) {
				if (!wdgts.contains(ctr)) {
					processEnableWidget(ctr, enable);
				}
			} else {
				processEnableWidget(ctr, enable);
			}
		}
	}

	/**
	 * Method processEnableWidget.
	 * 
	 * @param ctr
	 *            Control
	 * @param enable
	 *            boolean
	 */
	private void processEnableWidget(Control ctr, boolean enable) {
		ctr.setEnabled(enable);
	}

	/**
	 * @return the myShell
	 */
	public Shell getShell() {
		return this.myShell;
	}

	/**
	 * @param myShell
	 *            the myShell to set
	 */
	protected void setShell(Shell myShell) {
		this.myShell = myShell;
	}

	/**
	 * Method addInitialisationOption.
	 * 
	 * @param name
	 *            String
	 * @param value
	 *            Object
	 */
	public static void addInitialisationOption(String name, Object value) {
		options.put(name.toLowerCase(), value);
	}

	/**
	 * Method getInitialisationOption.
	 * 
	 * @param name
	 *            String
	 * @return Object
	 */
	public static Object getInitialisationOption(String name) {
		return options.get(name.toLowerCase());
	}

	public static void removeOption(String option) {
		options.remove(option);
	}

	/**
	 * Method isActive.
	 * 
	 * @return boolean
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Method setActive.
	 * 
	 * @param active
	 *            boolean
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	protected void viewReport(iDARTReport report) {
		viewReport(new ReportJob(report));
		report.viewReport();
	}

	protected void viewReport(IRunnableWithProgress job) {
		try {
			new ProgressMonitorDialog(getShell()).run(true, true, job);
		} catch (InvocationTargetException e) {
			log.error(e);
			MessageDialog.openError(getShell(), Messages.getString("common.error"), e.getMessage()); //$NON-NLS-1$
		} catch (InterruptedException e) {
			log.info("The report has been cancelled");
			MessageDialog.openInformation(getShell(), Messages.getString("common.cancelled"), e //$NON-NLS-1$
					.getMessage());
		}
	}

	/**
	 * @param severity
	 *            one of MessageDialog.ERROR, MessageDialog.WARNING,
	 *            MessageDialog.INFORMATION
	 * @param title
	 * @param message
	 */
	protected boolean showMessage(int severity, String title, String message) {
		switch (severity) {
		case MessageDialog.ERROR:
			MessageDialog.openError(getShell(), title, message);
			break;
		case MessageDialog.WARNING:
			MessageDialog.openWarning(getShell(), title, message);
			break;
		case MessageDialog.INFORMATION:
			MessageDialog.openInformation(getShell(), title, message);
			break;
		case MessageDialog.QUESTION:
			return MessageDialog.openQuestion(getShell(), title, message);
		}
		return true;
	}
}
