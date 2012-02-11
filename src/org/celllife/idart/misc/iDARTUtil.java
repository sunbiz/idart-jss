/**
 *
 */
package org.celllife.idart.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.welcome.GenericWelcome;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

/**
 *
 */
public class iDARTUtil {

	private static final String DATE_FORMAT = "dd MMM yyyy";
	private static SimpleDateFormat sdf;

	/**
	 * private constructor to prevent instantiation
	 */
	private iDARTUtil() {
	}

	/**
	 * Calculates the number of days between two dates.
	 * 
	 * @param d1
	 *            The first date.
	 * @param d2
	 *            The second date.
	 * 
	 * @return The number of days between the two dates. Zero is returned if the
	 *         dates are the same, one if the dates are adjacent, etc. The order
	 *         of the dates does not matter, the value returned is always >= 0.
	 */
	public static int getDaysBetween(Date dateOne, Date dateTwo) {
		assert (dateOne != null && dateTwo != null) : "Null date argument exception.";
		Calendar calOne = Calendar.getInstance();
		calOne.setTime(dateOne);
		Calendar calTwo = Calendar.getInstance();
		calTwo.setTime(dateTwo);
		return getDaysBetween(calOne, calTwo);
	}
	
	/**
	 * Give the input date this method returns a new date on the same day at
	 * 00:00:00
	 * 
	 * @param theDate
	 * @return
	 */
	public static Date getBeginningOfDay(Date theDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(theDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * Give the input date this method returns a new date on the same day at
	 * 23:59:59
	 * 
	 * @param theDate
	 * @return
	 */
	public static Date getEndOfDay(Date theDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(theDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 59);

		return cal.getTime();
	}

	/**
	 * Calculates the number of days between two calendar days in a manner which
	 * is independent of the Calendar type used.
	 * 
	 * @param d1
	 *            The first date.
	 * @param d2
	 *            The second date.
	 * 
	 * @return The number of days between the two dates. Zero is returned if the
	 *         dates are the same, one if the dates are adjacent, etc. The order
	 *         of the dates does not matter, the value returned is always >= 0.
	 *         If Calendar types of d1 and d2 are different, the result may not
	 *         be accurate.
	 */
	public static int getDaysBetween(java.util.Calendar d1,
			java.util.Calendar d2) {
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			java.util.Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
		- d1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	} // getDaysBetween()

	/**
	 * checks if the given date is valid birthDate
	 * 
	 * @param strDay
	 *            String
	 * @param strMonth
	 *            String
	 * @param strYear
	 *            String
	 * @return true if the date is valid else false
	 */
	public static boolean validBirthDate(String strDay, String strMonth,
			String strYear) {

		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("d-MMMM-yyyy");
			Date theDate = sdf1.parse(strDay + "-" + strMonth + "-" + strYear);

			return validBirthDate(theDate);

		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * checks if the given date is valid birthDate
	 * 
	 * @param theDate
	 *            Date
	 * @return true if the date is valid else false
	 */
	public static boolean validBirthDate(Date theDate) {

		if (theDate.after(new Date()))
			return false;

		return true;
	}

	/**
	 * Parses a string value from the database into an object.
	 * 
	 * @param dataType
	 * @param value
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parse(Class<T> dataType, String value) {
		// Method for converting a class into an object,
		// and populate object with the value.
		Object obj = null;

		try {
			Object instanceObj = dataType.newInstance();
			if (instanceObj instanceof Date) {
				obj = getDateFormat().parse(value);
			} else if (instanceObj instanceof Integer
					|| instanceObj instanceof Float
					|| instanceObj instanceof Long
					|| instanceObj instanceof Double
					|| instanceObj instanceof String) {
				obj = dataType.getConstructor(String.class).newInstance(value);
			} else if (instanceObj instanceof Class) {
				obj = Class.forName(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (T) obj;
	}

	/**
	 * Concerts an object to a String for storing in the database.
	 * 
	 * @param dataType
	 * @param v
	 * @return String
	 */
	public static String toString(Class<?> dataType, Object v) {
		Object obj = null;
		try {
			Object instanceObj = dataType.newInstance();
			if (instanceObj instanceof Date) {
				obj = format((Date) v);
			} else if (instanceObj instanceof Integer
					|| instanceObj instanceof Float
					|| instanceObj instanceof Long
					|| instanceObj instanceof Double
					|| instanceObj instanceof String) {
				obj = dataType.getConstructor(String.class).newInstance(v)
				.toString();
			} else if (instanceObj instanceof Class) {
				obj = Class.forName((String) v);
				obj = obj.getClass().getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (String) obj;
	}

	/**
	 * Method idContainsApostropheChar.
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean idContainsApostropheChar(String str) {
		String s = str;
		if (s.contains("'".subSequence(0, 1))) {
			// Show message dialog for wrong character.
			MessageBox msg = new MessageBox(GenericWelcome.shell, SWT.NONE);
			msg.setText("Character (\') not allowed in Patient Number:");
			msg
			.setMessage("iDART naming conventions do not allow the use of the \' character in the Patient Number field."
					+ " \\n\\nIf this character is used at your facility, you will need to replace it with another character within "
					+ "iDART. Examples of this are . , : + or -");
			msg.open();
			return true;
		}
		return false;
	}

	/**
	 * Method to check if a string contains alphaNumeric characters only but NO
	 * spaces
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAlphaNumeric(String str) {
		boolean blnAlphaNumeric = true;

		char chr[] = null;
		if (str.trim() != null) {
			chr = str.trim().toCharArray();

			for (int i = 0; i < chr.length; i++) {
				if (!(isAlpha(chr[i]) || isNumeric(chr[i]))) {
					blnAlphaNumeric = false;
					break;
				}
			}
		}
		return blnAlphaNumeric;
	}

	/**
	 * Method to check if a String contains alphaNumeric characters or spaces
	 * only
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAlphaNumericIncludeSpaces(String str) {
		boolean blnAlphaNumericIncludeSpaces = true;

		char chr[] = null;
		if (str.trim() != null) {
			chr = str.trim().toCharArray();

			for (int i = 0; i < chr.length; i++) {
				if (!(isAlpha(chr[i]) || isNumeric(chr[i]) || chr[i] == ' ')) {
					blnAlphaNumericIncludeSpaces = false;
					break;
				}
			}
		}
		return blnAlphaNumericIncludeSpaces;
	}

	/**
	 * Method to check if a string contains alpha characters only
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAlpha(String str) {

		boolean blnAlpha = true;

		char chr[] = null;
		if (str.trim() != null) {
			chr = str.trim().toCharArray();

			for (int i = 0; i < chr.length; i++) {
				if (!isAlpha(chr[i])) {
					blnAlpha = false;
					break;
				}
			}
		}
		return blnAlpha;
	}

	/**
	 * Method to check if a character is alpha.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isAlpha(Character chr) {
		return ((chr >= 'A' && chr <= 'Z') || (chr >= 'a' && chr <= 'z'));
	}

	/**
	 * Method to check if a string contains numeric values only
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {

		boolean blnNumeric = true;

		char chr[] = null;
		if (str.trim() != null) {
			chr = str.trim().toCharArray();

			for (int i = 0; i < chr.length; i++) {
				if (!isNumeric(chr[i])) {
					blnNumeric = false;
					break;
				}
			}
		}
		return blnNumeric;
	}

	/**
	 * Method to check if a string contains numeric values only
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(Character chr) {
		return chr >= '0' && chr <= '9';
	}

	/**
	 * This method removes the accumulated amount For Eg, if the String (60 + 1)
	 * is parsed to the method the String (60) will be returned
	 * 
	 * @param totalQty
	 * @return
	 */
	public static String removeAccumulated(String totalQty) {

		StringTokenizer st = new StringTokenizer(totalQty, "+");
		String result = "";

		if (st.countTokens() > 1) {
			result = st.nextToken();
			return result.trim() + ")";
		}

		return totalQty;
	}

	/**
	 * Helper method to convert the sex to a full String
	 * 
	 * @param sex
	 * @return
	 */
	public static String getSexString(char sex) {

		if (sex == 'F' || sex == 'f')
			return "Female";
		else if (sex == 'M' || sex == 'm')
			return "Male";
		else
			return "Unknown";

	}

	/**
	 * This method returns true if and only if date1 is before date2. Note: this
	 * method does not take time into consideration. For example 28 April 2008
	 * 10:52 is not before 28 April 2008 11:00
	 * 
	 * @param date1
	 * @param date2
	 * @return true if date1 is before date2 (ignoring timestamp)
	 */
	public static boolean before(Date date1, Date date2) {
		return DateFieldComparator.compare(date1, date2, Calendar.DAY_OF_MONTH) < 0;
	}

	/**
	 * 
	 * this method replaces methods from IntegerValidator.java (deleted).
	 * 
	 * @param String
	 *            str
	 * @return Object containing the int if str is an integer, returns null if
	 *         not an integer .
	 */

	public static Object isInteger(String str) {

		Double D = new Double(str);
		int intComp = D.intValue();
		double d = D.doubleValue();
		if ((d - intComp) == 0)
			return intComp;
		else
			return null;

	}

	/**
	 * 
	 * this method replaces methods from IntegerValidator.java (deleted).
	 * 
	 * @param String
	 *            str
	 * @return boolean containing true if str is a positive integer, false if
	 *         not
	 */

	public static boolean isPositiveInteger(String str) {

		Object i = isInteger(str);
		if (i == null)
			return false;

		if (Integer.parseInt(i.toString()) > 0)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * this method replaces methods from IntegerValidator.java (deleted).
	 * 
	 * @param String
	 *            str
	 * @return boolean containing true if str is a negative integer, false if
	 *         not
	 */

	public static boolean isNegativeInteger(String str) {
		Object i = isInteger(str);
		if (i == null)
			return false;

		if (Integer.parseInt(i.toString()) < 0)
			return true;
		else
			return false;
	}

	public static String format(Date date) {
		if (date == null){
			return ""; //$NON-NLS-1$
		}
		return getDateFormat().format(date);
	}

	private static SimpleDateFormat getDateFormat() {
		if (sdf == null) {
			sdf = new SimpleDateFormat(DATE_FORMAT);
		}
		return sdf;
	}

	public static Date zeroTimeStamp(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	public static boolean after(Date date1, Date date2) {
		return before(date2, date1);
	}
	
	public static int getAgeAt(Date dateOfBirth, Date date) {
		Calendar today = Calendar.getInstance();
		if (date != null) {
			today.setTime(date);
		}

		Calendar dob = Calendar.getInstance();
		dob.setTime(dateOfBirth);
		// Get age based on year
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		dob.set(Calendar.YEAR, today.get(Calendar.YEAR));
		// If birthday hasn't happened yet, subtract one from
		// age
		if (today.before(dob)) {
			age--;
		}
		return age;
	}

	public static boolean dateIsToday(Date date) {
		return DateFieldComparator.compare(date,new Date(), Calendar.DAY_OF_MONTH) == 0;
	}
	
	public static boolean hasZeroTimestamp(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		
		return (hour == 0 && min == 0 && sec == 0);
	}
	
	/**
	 * Converts a numeric column index to a letter column index 
	 * i.e. 0 = A, 27 = AB
	 * 
	 * @param columnIndex
	 *            the numeric index of the column
	 * @param zeroBased
	 *            a boolean indicating whether the columnIndex is
	 *            measured from zero or from one
	 * @return String representing the column in letter notation
	 */
	public static String columnIndexToLetterNotation(int columnIndex, boolean zeroBased) {
		if (!zeroBased && columnIndex > 0){
			columnIndex--;
		}
		
		int base = 26;
		StringBuffer b = new StringBuffer();
		do {
			int digit = columnIndex % base + 65;
			b.append(Character.valueOf((char) digit));
			columnIndex = (columnIndex / base) - 1;
		} while (columnIndex >= 0);
		return b.reverse().toString();
	}
	
	/**
	 * @return matching group of illegal characters or null if none found
	 */
	public static String checkPatientId(String patientId) {
		String illegalText = null;
		Matcher matcher = Pattern.compile(iDartProperties.illegalPatientIdRegex).matcher(patientId.trim());
		if (matcher.find()){
			illegalText = matcher.group();
		}
		return illegalText;
	}

	public static Date add(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
	

}
