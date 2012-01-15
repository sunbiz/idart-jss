package org.celllife.idart.gui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.widgets.Combo;

/**
 */
public class ComboUtils {

	/**
	 * private constructor to prevent instantiation
	 */
	private ComboUtils() {
	}

	/*
	 * Populates day/month/year combo boxes (form: 30 January 2006) setToToday
	 * determines whether the vaule should be set to the current date or not
	 * 
	 * includeFuture determines whether 1 year after the current year should be
	 * included eg. in 2006, can the date chosen be in 2007?
	 */
	/**
	 * Method populateDateCombos.
	 * 
	 * @param cmbDay
	 *            Combo
	 * @param cmbMonth
	 *            Combo
	 * @param cmbYear
	 *            Combo
	 * @param setToToday
	 *            boolean
	 * @param includeFuture
	 *            boolean
	 */
	public static void populateDateCombos(Combo cmbDay, Combo cmbMonth,
			Combo cmbYear, boolean setToToday, boolean includeFuture) {
		Calendar theCal = Calendar.getInstance();
		int currentYear = theCal.get(Calendar.YEAR);

		for (int i = 1; i < 32; i++) {
			cmbDay.add(Integer.toString(i));
		}

		String months[] = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };
		for (int i = 0; i < 12; i++) {
			cmbMonth.add(months[i]);
		}

		if (includeFuture) {
			for (int i = -1; i < 100; i++) {
				cmbYear.add(Integer.toString(currentYear - i));
			}
		} else {
			for (int i = 0; i < 100; i++) {
				cmbYear.add(Integer.toString(currentYear - i));
			}

		}

		if (setToToday) {
			ComboUtils.setToDate(cmbDay, cmbMonth, cmbYear, new Date());

		}

	}

	/**
	 * Sets three date combos (form: 30 January 2006) to the specififed date
	 * 
	 * @param cmbDay
	 * @param cmbMonth
	 * @param cmbYear
	 * @param theDate
	 */
	public static void setToDate(Combo cmbDay, Combo cmbMonth, Combo cmbYear,
			Date theDate) {

		Calendar theCal = Calendar.getInstance();
		theCal.setTime(theDate);
		int currentYear = theCal.get(Calendar.YEAR);
		int currentDay = theCal.get(Calendar.DATE);

		SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM");

		String currentMonth = sdfMonth.format(theCal.getTime());

		cmbDay.setText((new Integer(currentDay)).toString());

		cmbMonth.setText(currentMonth);

		cmbYear.setText((new Integer(currentYear)).toString());

	}

}
