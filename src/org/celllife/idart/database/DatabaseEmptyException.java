package org.celllife.idart.database;

public class DatabaseEmptyException extends DatabaseException {

	private static final long serialVersionUID = 19631321188299202L;

	public DatabaseEmptyException() {
		super();
	}

	public DatabaseEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseEmptyException(String message) {
		super(message);
	}

	public DatabaseEmptyException(Throwable cause) {
		super(cause);
	}

}
