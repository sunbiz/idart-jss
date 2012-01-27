package org.celllife.idart.misc.task;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * Represents and generic task
 */
public interface IdartTask {

	/**
	 * Initialise the task.
	 * 
	 * @param arg
	 *            an array of arguments from the command line
	 * @return true if initialisation succeeded
	 */
	public abstract boolean init(String[] args);

	/**
	 * Executes the task
	 * 
	 * @param monitor
	 * 
	 * @throws TaskException
	 *             if the task fails
	 */
	public abstract void run(IProgressMonitor monitor) throws TaskException;

	/**
	 * Called if initialisation fails and printed to output.
	 * 
	 * @return help text to display to user
	 */
	public abstract String getHelpText();

	/**
	 * Short task description. Used when printing out list of available tasks
	 */
	public abstract String getDescription();
}
