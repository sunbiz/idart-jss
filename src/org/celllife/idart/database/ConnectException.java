package org.celllife.idart.database;

import org.celllife.idart.misc.iDARTException;

public class ConnectException extends iDARTException {

	private static final long serialVersionUID = 2070608398874630925L;

	public ConnectException() {
	}

	public ConnectException(String message) {
		super(message);
	}

	public ConnectException(Throwable cause) {
		super(cause);
	}

	public ConnectException(String message, Throwable cause) {
		super(message, cause);
	}

}
