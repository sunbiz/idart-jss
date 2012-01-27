package org.celllife.idart.gui.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.celllife.function.DateRuleFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DateButtonTest {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private DateButton db;
	private Shell shell;
	private Display display;
	private Date afterNow;
	private Date beforeNow;
	private Date now;

	@BeforeClass
	public void setupDates() {
		Calendar cal = Calendar.getInstance();
		now = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		afterNow = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		beforeNow = cal.getTime();
	}

	@BeforeMethod
	public void beforeMethod() {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		db = new DateButton(shell, DateButton.ZERO_TIMESTAMP, null);
	}

	@AfterMethod
	public void afterMethod() {
		display.dispose();
	}

	@Test(groups = "manualTests")
	public void testDateButtonNoRestrictions() throws DateException {
		db.setDate(afterNow);
		db.setDate(beforeNow);
		db.setDate(now);
	}

	@Test(groups = "manualTests")
	public void testDateButtonSingleRestrictionPass() throws DateException {
		db.setValidator(new DateInputValidator(DateRuleFactory
				.afterNowInclusive(false)));
		db.setDate(afterNow);
	}

	@Test(groups = "manualTests", expectedExceptions = { DateException.class })
	public void testDateButtonSingleRestrictionFail() throws DateException {
		db.setValidator(new DateInputValidator(DateRuleFactory
				.afterNowInclusive(false)));
		db.setDate(beforeNow);
	}
}
