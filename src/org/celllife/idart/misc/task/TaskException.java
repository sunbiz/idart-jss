package org.celllife.idart.misc.task;

import org.celllife.idart.misc.iDARTException;

public class TaskException extends iDARTException {

	private static final long serialVersionUID = -4840002411044029738L;

	public TaskException() {
		super();
	}

	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskException(String message) {
		super(message);
	}

	public TaskException(Throwable cause) {
		super(cause);
	}

}
