/**
 *
 */
package org.celllife.idart.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for
 * {@link org.celllife.idart.misc.DateFieldComparator#compare(java.util.Date, java.util.Date)}
 * .
 */
public class DateFieldComparatorTest {

	/**
	 * Test method for
	 * {@link org.celllife.idart.misc.DateFieldComparator#compare(java.util.Date, java.util.Date)}
	 * .
	 */
	@Test(dataProvider = "dateProvider")
	public void testCompare(Date d1, Date d2, int field, int result,
			@SuppressWarnings("unused") String fieldName) {
		DateFieldComparator c = new DateFieldComparator(field);
		Assert.assertEquals(c.compare(d1, d2), result);
	}

	@DataProvider(name = "dateProvider")
	public Object[][] dateProvider() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return new Object[][] {
				{ sdf.parse("20010511145109"), sdf.parse("20010511145109"),
						Calendar.SECOND, 0, "SECOND 0" },
				{ sdf.parse("20010511145109"), sdf.parse("20010511145115"),
						Calendar.SECOND, -1, "SECOND -1" },
				{ sdf.parse("20010511145116"), sdf.parse("20010511145115"),
						Calendar.SECOND, +1, "SECOND +1" },
				{ sdf.parse("20010510145116"), sdf.parse("20010511145116"),
						Calendar.SECOND, -1, "SECOND -1 (day diff)" },
				{ sdf.parse("20010611145116"), sdf.parse("20010511145116"),
						Calendar.SECOND, +1, "SECOND +1 (month diff)" },

				{ sdf.parse("20010511145100"), sdf.parse("20010511145151"),
						Calendar.MINUTE, 0, "MINUTE 0" },
				{ sdf.parse("20010511145051"), sdf.parse("20010511145151"),
						Calendar.MINUTE, -1, "MINUTE -1" },
				{ sdf.parse("20010511145251"), sdf.parse("20010511145151"),
						Calendar.MINUTE, +1, "MINUTE +1" },
				{ sdf.parse("20010511135151"), sdf.parse("20010511145151"),
						Calendar.MINUTE, -1, "MINUTE -1 (hour diff)" },
				{ sdf.parse("20020511145151"), sdf.parse("20010511145151"),
						Calendar.MINUTE, +1, "MINUTE +1 (year diff)" },

				{ sdf.parse("20010511140000"), sdf.parse("20010511145151"),
						Calendar.HOUR_OF_DAY, 0, "HOUR 0" },
				{ sdf.parse("20010411135115"), sdf.parse("20010511145515"),
						Calendar.HOUR_OF_DAY, -1, "HOUR -1" },
				{ sdf.parse("20010511155515"), sdf.parse("20010511145515"),
						Calendar.HOUR_OF_DAY, +1, "HOUR +1" },
				{ sdf.parse("20010510145515"), sdf.parse("20010511145515"),
						Calendar.HOUR_OF_DAY, -1, "HOUR -1 (day diff)" },
				{ sdf.parse("20010512145515"), sdf.parse("20010511145515"),
						Calendar.HOUR_OF_DAY, +1, "HOUR +1 (day diff)" },

				{ sdf.parse("20010511000000"), sdf.parse("20010511145515"),
						Calendar.DAY_OF_MONTH, 0, "DAY 0" },
				{ sdf.parse("20010510145515"), sdf.parse("20010511145515"),
						Calendar.DAY_OF_MONTH, -1, "DAY -1" },
				{ sdf.parse("20010512145515"), sdf.parse("20010511145515"),
						Calendar.DAY_OF_MONTH, +1, "DAY +1" },
				{ sdf.parse("20000511145515"), sdf.parse("20010511145515"),
						Calendar.DAY_OF_MONTH, -1, "DAY -1 year diff" },
				{ sdf.parse("20010611145515"), sdf.parse("20010511145515"),
						Calendar.DAY_OF_MONTH, +1, "DAY +1 month diff" },

				{ sdf.parse("20010511111111"), sdf.parse("20010511145515"),
						Calendar.MONTH, 0, "MONTH 0" },
				{ sdf.parse("20010411145515"), sdf.parse("20010511145515"),
						Calendar.MONTH, -1, "MONTH -1" },
				{ sdf.parse("20010611145515"), sdf.parse("20010511145515"),
						Calendar.MONTH, +1, "MONTH +1" },
				{ sdf.parse("20000511145515"), sdf.parse("20010511145515"),
						Calendar.MONTH, -1, "MONTH -1 year dif)" },
				{ sdf.parse("20020511145515"), sdf.parse("20010511145515"),
						Calendar.MONTH, +1, "MONTH +1 year diff" },

				{ sdf.parse("20011111111111"), sdf.parse("20010511145515"),
						Calendar.YEAR, 0, "YEAR 0" },
				{ sdf.parse("20000511145515"), sdf.parse("20010511145515"),
						Calendar.YEAR, -1, "YEAR -1" },
				{ sdf.parse("20020511145515"), sdf.parse("20010511145515"),
						Calendar.YEAR, +1, "YEAR +1" } };
	}

}
