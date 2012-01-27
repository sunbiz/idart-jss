package org.celllife.idart.misc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.AssertionFailedException;

/**
 * This class compares dates according the the Calendar fields. The comparator
 * will compare all calendar fields up until (and including) the field
 * specified. i.e. if Calendar.DAY_OF_MONTH is specified then the comparator
 * will only compare the year, month and day.
 * 
 * If no field is specified a normal date comparison is performed.
 */
public class DateFieldComparator implements Comparator<Date> {

	private Integer field;

	private final List<Integer> acceptedFields = Arrays.asList(Calendar.YEAR,
			Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
			Calendar.MINUTE, Calendar.SECOND);

	private int index = -1;


	public DateFieldComparator(int compareField) {
		setCompareField(compareField);
	}

	/**
	 * @param date1
	 * @param date2
	 * @return the value 0 if the date1 is equal to date2; a value less than 0
	 *         if this date1 is before the date2; and a value greater than 0 if
	 *         this date1 is after the date2 argument.
	 */
	@Override
	public int compare(Date date1, Date date2) {
		if (field == null)
			return date1.compareTo(date2);
		else {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(date1);
			Calendar c2 = Calendar.getInstance();
			c2.setTime(date2);

			int compare = 0;
			for (int i = 0; i <= index; i++) {
				compare = compareDateField(c1, c2, acceptedFields.get(i));
				if (compare != 0)
					return compare;
			}

			return compare;
		}
	}

	/**
	 * @param c1
	 * @param c2
	 * @param currentField
	 * @return Returns -1 if c1.currentfield is before c2.currentfield. Returns
	 *         0 if c1.currentfield the same as c2.currentfield. Returns +1 if
	 *         c1.currentfield is after c2.currentfield
	 * @throws AssertionFailedException
	 *             if currentField isn't one of: Calendar.YEAR, Calendar.MONTH,
	 *             Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE
	 *             or Calendar.SECOND
	 */
	private int compareDateField(Calendar c1, Calendar c2, int currentField) {
		assert acceptedFields.contains(currentField) : "Unknown date part "
			+ currentField;
		int diff = c1.get(currentField) - c2.get(currentField);
		int compare = (diff < 0 ? -1 : (diff == 0 ? 0 : 1));
		return compare;
	}

	/**
	 * Set the field you wish to compare up to. The comparator will compare all
	 * calendar fields up until the field specified. i.e. if
	 * Calendar.DAY_OF_MONTH is specified then the comparator will only compare
	 * the year, month and day.
	 * 
	 * If no field is specified a normal date comparison is performed.
	 * 
	 * @param compareField
	 */
	public void setCompareField(int compareField) {
		index = acceptedFields.indexOf(compareField);
		if (index < 0)
			throw new IllegalArgumentException("Unknown calendar field");
		this.field = compareField;
	}

	/**
	 * 
	 * 
	 * @param date1
	 * @param date2
	 * @param field
	 *            Calendar field you wish to compare up to.
	 * @return the value 0 if the date1 is equal to date2; a value less than 0
	 *         if this date1 is before the date2; and a value greater than 0 if
	 *         this date1 is after the date2 argument.
	 */
	public static int compare(Date date1, Date date2, int field) {
		DateFieldComparator c = new DateFieldComparator(field);
		return c.compare(date1, date2);
	}
}
