package model.manager.excel.conversion.exceptions;

/**
 * These exceptions are used for parsing data from the Excel Sheet into the
 * Database
 * 
 */
public class FormException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2320535575822870841L;

	/**
	 * Constructor for FormException.
	 * @param message String
	 */
	public FormException(String message) {
		super(message);
	}

}
