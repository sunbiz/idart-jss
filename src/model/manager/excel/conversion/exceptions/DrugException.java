package model.manager.excel.conversion.exceptions;


/**
 * These exceptions are used for parsing data from the Excel Sheet into the
 * Database
 * 
 */
public class DrugException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6835596278769092640L;

	/**
	 * Constructor for DrugException.
	 * @param message String
	 */
	public DrugException(String message) {
		super(message);
	}

}
