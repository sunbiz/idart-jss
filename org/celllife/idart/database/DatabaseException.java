package org.celllife.idart.database;

import org.celllife.idart.misc.iDARTException;

public class DatabaseException extends iDARTException {

	private static final long serialVersionUID = -6950837837273666169L;

	public DatabaseException() {
		super();
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(Throwable cause) {
		super(cause);
	}

}
