package org.celllife.idart.misc;

public class iDARTRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5011878114753583942L;

	public iDARTRuntimeException() {
	}

	public iDARTRuntimeException(String message) {
		super(message);
	}

	public iDARTRuntimeException(Throwable cause) {
		super(cause);
	}

	public iDARTRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
