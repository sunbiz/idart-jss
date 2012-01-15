package model.manager.excel.download;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;

/**
 */
public class ExcelConversions {

	private static Logger log = Logger.getLogger(ExcelConversions.class);

	public ExcelConversions() {
		super();
	}

	/**
	 * Trys to create a date out of the String using several formats
	 * 
	 * @param oldDate String
	 * @return Date
	 */
	public Date toDatabaseDate(String oldDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				iDartProperties.importDateFormat);
		Date date = null;

		if (oldDate == null)
			return date;

		try {
			date = sdf.parse(oldDate);
		} catch (ParseException e) {
			log.error("Error converting date '" + oldDate + "' to format '"
					+ iDartProperties.importDateFormat + "'");
			try {
				// Try Again
				// sdf = new SimpleDateFormat("dd/MM/yyyy");
				sdf = new SimpleDateFormat("yyyy/MM/dd");
				date = sdf.parse(oldDate);

			} catch (ParseException pe) {
				try {
					// And again
					sdf = new SimpleDateFormat("dd-MM-yyyy");
					date = sdf.parse(oldDate);

				} catch (ParseException pee) {
					// Can not parse the date therefore date = null
				}

			}
		}
		return date;
	}

	/**
	 * little helper method to convert string Sex to char
	 * 
	 * @param string
	 *            The string to convert
	 * 
	 * @return the char value of the Sex
	 */
	public char toSex(String string) {
		char result = 'U';

		if (string.startsWith("m") || string.startsWith("M")) {
			result = 'M';
		} else if (string.startsWith("f") || string.startsWith("F")) {
			result = 'F';
		}

		return result;
	}

	/**
	 * little helper method to convert a string value of "y" or "n" to its
	 * boolean value
	 * 
	 * @param string
	 * 
	 * @return the boolean value of the string
	 */
	public boolean toBoolean(String string) {
		if (string.equalsIgnoreCase("y") || string.equalsIgnoreCase("yes"))
			return true;
		return false;
	}

	/**
	 * Method toInt.
	 * @param string String
	 * @return int
	 */
	public int toInt(String string) {
		int i = -1;
		try {
			i = Integer.parseInt(string);
		} catch (NumberFormatException ne) {
		}
		return i;
	}

	/**
	 * Method toChar.
	 * @param string String
	 * @return char
	 */
	public char toChar(String string) {
		if ((string != null) && (string.length() == 1))
			return string.charAt(0);
		return '0';
	}

	/**
	 * Method toDouble.
	 * @param string String
	 * @return double
	 */
	public double toDouble(String string) {
		double d = -1;
		try {
			d = Double.parseDouble(string);
		} catch (NumberFormatException ne) {

		}
		return d;
	}

}
