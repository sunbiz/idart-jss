package model.manager.excel.conversion.exceptions;

import org.celllife.idart.misc.iDARTException;

/**
 * These exceptions are used for parsing data from the Excel Sheet into the
 * Database
 * 
 */
public class ParseException extends iDARTException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8732597445776054560L;

	/**
	 * Constructor for ParseException.
	 * @param message String
	 */
	public ParseException(String message) {
		super(message);
	}

}
