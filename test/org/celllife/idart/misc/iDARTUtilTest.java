package org.celllife.idart.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author munaf
 * 
 */
public class iDARTUtilTest {

	/**
	 * Test method for
	 * {@link org.celllife.idart.misc.iDARTUtil#before(java.util.Date, java.util.Date)}
	 * .
	 */
	@Test(dataProvider = "dateProvider")
	public void testBefore(Date date1, Date date2, boolean result) {
		Assert.assertEquals(iDARTUtil.before(date1, date2), result);
	}

	@DataProvider(name = "dateProvider")
	public Object[][] dateProvider() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return new Object[][] {
				{ sdf.parse("2001-05-11-14-51-09"),
					sdf.parse("2001-05-11-14-51-09"), false },
					{ sdf.parse("2001-05-11-17-51-09"),
						sdf.parse("2001-05-11-14-51-09"), false },
						{ sdf.parse("2001-05-11-12-05-55"),
							sdf.parse("2001-05-11-14-51-09"), false },
							{ sdf.parse("2001-05-10-14-51-09"),
								sdf.parse("2001-05-11-14-51-09"), true },
								{ sdf.parse("2001-04-11-14-51-09"),
									sdf.parse("2001-05-11-14-51-09"), true },
									{ sdf.parse("2000-05-11-14-51-09"),
										sdf.parse("2001-05-11-14-51-09"), true } };
	}

	/**
	 * Test method for
	 * {@link org.celllife.idart.misc.iDARTUtil#isInteger(String)} .
	 */
	@Test(dataProvider = "intProvider")
	public void testIsInteger(String str, Object result) {
		Object o = iDARTUtil.isInteger(str);

		if (result == null) {
			Assert.assertNull(o);
		} else {
			Assert.assertEquals(iDARTUtil.isInteger(str), Integer
					.parseInt(result.toString()));
		}

	}

	/**
	 * 
	 * @return {@link iDARTUtil#isInteger(String)}
	 */
	@DataProvider(name = "intProvider")
	public Object[][] intProvider() {
		return new Object[][] { { "3.5", null }, { "0.5", null }, { "3.0", 3 } };
	}
	
	@Test(dataProvider = "spreadsheet")
	public void testConvertColumnIndexToLetterNotation(int index, String expectedColumnName){
		String columnName = iDARTUtil.columnIndexToLetterNotation(index, true);
		Assert.assertEquals(columnName, expectedColumnName);
	}
	
	@DataProvider(name = "spreadsheet")
	public Object[][] spreadsheetProvider() {
		return new Object[][] { { 0, "A" }, { 25, "Z" }, { 26, "AA" }, { 701, "ZZ" }, {702, "AAA"},  {18277, "ZZZ"} };
	}

}
