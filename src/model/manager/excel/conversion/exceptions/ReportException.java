package model.manager.excel.conversion.exceptions;

import org.celllife.idart.misc.task.TaskException;

/**
 */
public class ReportException extends TaskException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1555428187364461337L;

	/**
	 * Constructor for ReportException.
	 * @param exception String
	 */
	public ReportException(String exception) {
		super(exception);

	}

	public ReportException() {
		super();
	}

	public ReportException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReportException(Throwable cause) {
		super(cause);
	}



}
