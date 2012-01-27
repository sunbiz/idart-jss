package org.celllife.idart.gui;

public class GUIException extends RuntimeException {

	private static final long serialVersionUID = 123424564654651L;

	public GUIException() {
		super();
	}

	public GUIException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GUIException(Throwable arg0) {
		super(arg0);
	}

	public GUIException(String message) {
		super(message);
	}
}
