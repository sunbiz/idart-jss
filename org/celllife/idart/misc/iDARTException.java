package org.celllife.idart.misc;

public class iDARTException extends Exception {

	private static final long serialVersionUID = -458226694667804852L;

	public iDARTException() {
	}

	public iDARTException(String message) {
		super(message);
	}

	public iDARTException(Throwable cause) {
		super(cause);
	}

	public iDARTException(String message, Throwable cause) {
		super(message, cause);
	}

}
